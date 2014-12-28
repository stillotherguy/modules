package com.weaver.teams.security.authentication;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.security.crypto.codec.Utf8;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.CookieTheftException;
import org.springframework.security.web.authentication.rememberme.InvalidCookieException;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationException;
import org.springframework.util.StringUtils;

import com.weaver.teams.api.base.ClientInfo;
import com.weaver.teams.api.tenant.Tenant;
import com.weaver.teams.api.tenant.TenantManager;
import com.weaver.teams.api.user.User;
import com.weaver.teams.core.user.UserBuilder;
import com.weaver.teams.core.user.UserImpl;
import com.weaver.teams.core.user.UserManager;
import com.weaver.teams.security.exception.UserNotFoundException;

public class TeamsRememberMeServices implements RememberMeServices {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	public static final String REMEMBER_ME_COOKIE_KEY = "TEAMS_REMEMBER_ME_COOKIE";
	public static final String REMEMBER_ME_KEY = "TeamsRememberMeKey";
	public static final int TWO_WEEKS_S = 1209600;
	private static final String DELIMITER = ":";

	private boolean alwaysRemember = false;
	private Boolean useSecureCookie = null;
	private int tokenValiditySeconds = TWO_WEEKS_S;

	private TenantManager tenantManager;
	private UserManager userManager;

	@Autowired
	private UserBuilder userBuilder;
	@Autowired
	private TeamsAuthenticationService authenticationService;

	private String parameter;

	/**
	 * 获取cookie，自动登录
	 */
	@Override
	public final Authentication autoLogin(HttpServletRequest request, HttpServletResponse response) {
		String rememberMeCookie = extractRememberMeCookie(request);

		if (rememberMeCookie == null) {
			return null;
		}

		logger.debug("Remember-me cookie detected");

		if (rememberMeCookie.length() == 0) {
			logger.debug("Cookie was empty");
			cancelCookie(request, response);
			return null;
		}

		try {
			String[] cookieTokens = decodeCookie(rememberMeCookie);
			if (cookieTokens.length != 3) {
				throw new InvalidCookieException("Cookie token did not contain 3" + " tokens, but contained '" + Arrays.asList(cookieTokens) + "'");
			}

			long tokenExpiryTime;

			try {
				tokenExpiryTime = new Long(cookieTokens[1]).longValue();
			} catch (NumberFormatException nfe) {
				throw new InvalidCookieException("Cookie token[1] did not contain a valid number (contained '" + cookieTokens[1] + "')");
			}

			if (tokenExpiryTime < System.currentTimeMillis()) {
				throw new InvalidCookieException("Cookie token[1] has expired (expired on '" + new Date(tokenExpiryTime) + "'; current time is '"
						+ new Date() + "')");
			}
			String account = cookieTokens[0];

			String expectedTokenSignature = makeTokenSignature(tokenExpiryTime, account);

			if (!equals(expectedTokenSignature, cookieTokens[2])) {
				throw new InvalidCookieException("Cookie token[2] contained signature '" + cookieTokens[2]);
			}
			logger.debug("Remember-me cookie accepted");

			// 创建 TeamsAuthenticationToken

			UserImpl loadedUser = userManager.loadUser(account);
			if (loadedUser == null) {
				throw new UserNotFoundException("User '" + account + "' not found");
			}
			ClientInfo client = ClientInfo.obtainClient(request);
			TeamsAuthenticationToken result = new TeamsAuthenticationToken(account, loadedUser.getPassword(), client);
			Tenant tenant = tenantManager.loadTenant(loadedUser.getTenantKey());
			return authenticationService.createSuccessAuthentication(result, tenant, loadedUser);

		} catch (CookieTheftException cte) {
			cancelCookie(request, response);
			throw cte;
		} catch (UserNotFoundException noUser) {
			logger.debug("Remember-me login was valid but corresponding user not found.", noUser);
		} catch (InvalidCookieException invalidCookie) {
			logger.debug("Invalid remember-me cookie: " + invalidCookie.getMessage());
		} catch (AccountStatusException statusInvalid) {
			logger.debug("Invalid UserDetails: " + statusInvalid.getMessage());
		} catch (RememberMeAuthenticationException e) {
			logger.debug(e.getMessage());
		}
		cancelCookie(request, response);
		return null;
	}

	@Override
	public void loginFail(HttpServletRequest request, HttpServletResponse response) {
		logger.debug("Interactive login attempt was unsuccessful.");
		cancelCookie(request, response);
	}

	/**
	 * Examines the incoming request and checks for the presence of the configured "remember me" parameter.
	 * If it's present, or if <tt>alwaysRemember</tt> is set to true, calls <tt>onLoginSucces</tt>.
	 */
	@Override
	public final void loginSuccess(HttpServletRequest request, HttpServletResponse response, Authentication successfulAuthentication) {
		if (!rememberMeRequested(request, parameter)) {
			logger.debug("Remember-me login not requested.");
			return;
		}
		onLoginSuccess(request, response, successfulAuthentication);
	}

	protected boolean rememberMeRequested(HttpServletRequest request, String parameterName) {
		if (alwaysRemember) {
			return true;
		}
		String paramValue = request.getParameter(parameterName);
		if (paramValue != null) {
			if (paramValue.equalsIgnoreCase("true") || paramValue.equalsIgnoreCase("on") || paramValue.equalsIgnoreCase("yes")
					|| paramValue.equals("1")) {
				return true;
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Did not send remember-me cookie (principal did not set parameter '" + parameterName + "')");
		}
		return false;
	}

	public void createLoginToken(HttpServletRequest request, HttpServletResponse response, Authentication token, int seconds) {
		int defaultTokenValiditySeconds = getTokenValiditySeconds();
		setTokenValiditySeconds(seconds);
		onLoginSuccess(request, response, token);
		setTokenValiditySeconds(defaultTokenValiditySeconds);
	}

	public void onLoginSuccess(HttpServletRequest request, HttpServletResponse response, Authentication successfulAuthentication) {
		String account = null;
		TeamsAuthenticationToken token = (TeamsAuthenticationToken) successfulAuthentication;
		if (token.getPrincipal() instanceof User) {
			account = ((UserImpl) token.getUser()).getLoginAccount();
		} else {
			account = successfulAuthentication.getPrincipal().toString();
		}

		int tokenLifetime = getTokenValiditySeconds();
		long expiryTime = System.currentTimeMillis();

		expiryTime += 1000L * (tokenLifetime < 0 ? TWO_WEEKS_S : tokenLifetime);

		String signatureValue = makeTokenSignature(expiryTime, account);

		setCookie(new String[] { account, Long.toString(expiryTime), signatureValue }, tokenLifetime, request, response);

		if (logger.isDebugEnabled()) {
			logger.debug("Added remember-me cookie for user '" + account + "', expiry: '" + new Date(expiryTime) + "'");
		}
	}

	protected String extractRememberMeCookie(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if ((cookies == null) || (cookies.length == 0)) {
			return null;
		}
		for (Cookie cookie : cookies) {
			if (REMEMBER_ME_COOKIE_KEY.equals(cookie.getName())) {
				return cookie.getValue();
			}
		}
		return null;
	}

	public void setCookie(String[] tokens, int maxAge, HttpServletRequest request, HttpServletResponse response) {
		String cookieValue = encodeCookie(tokens);
		Cookie cookie = new Cookie(REMEMBER_ME_COOKIE_KEY, cookieValue);
		cookie.setMaxAge(maxAge);
		cookie.setPath(getCookiePath(request));

		if (useSecureCookie == null) {
			cookie.setSecure(request.isSecure());
		} else {
			cookie.setSecure(useSecureCookie);
		}

		response.addCookie(cookie);
	}

	protected String[] decodeCookie(String cookieValue) throws InvalidCookieException {
		for (int j = 0; j < (cookieValue.length() % 4); j++) {
			cookieValue = cookieValue + "=";
		}

		if (!Base64.isBase64(cookieValue.getBytes())) {
			throw new InvalidCookieException("Cookie token was not Base64 encoded; value was '" + cookieValue + "'");
		}

		String cookieAsPlainText = new String(Base64.decode(cookieValue.getBytes()));

		String[] tokens = StringUtils.delimitedListToStringArray(cookieAsPlainText, DELIMITER);

		if ((tokens[0].equalsIgnoreCase("http") || tokens[0].equalsIgnoreCase("https")) && tokens[1].startsWith("//")) {
			// Assume we've accidentally split a URL (OpenID identifier)
			String[] newTokens = new String[tokens.length - 1];
			newTokens[0] = tokens[0] + ":" + tokens[1];
			System.arraycopy(tokens, 2, newTokens, 1, newTokens.length - 1);
			tokens = newTokens;
		}

		return tokens;
	}

	/**
	 * Inverse operation of decodeCookie.
	 * 
	 * @param cookieTokens the tokens to be encoded.
	 * @return base64 encoding of the tokens concatenated with the ":" delimiter.
	 */
	protected String encodeCookie(String[] cookieTokens) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < cookieTokens.length; i++) {
			sb.append(cookieTokens[i]);

			if (i < (cookieTokens.length - 1)) {
				sb.append(DELIMITER);
			}
		}

		String value = sb.toString();

		sb = new StringBuilder(new String(Base64.encode(value.getBytes())));

		while (sb.charAt(sb.length() - 1) == '=') {
			sb.deleteCharAt(sb.length() - 1);
		}

		return sb.toString();
	}

	/**
	 * Sets a "cancel cookie" (with maxAge = 0) on the response to disable persistent logins.
	 */
	protected void cancelCookie(HttpServletRequest request, HttpServletResponse response) {
		logger.debug("Cancelling cookie");
		Cookie cookie = new Cookie(REMEMBER_ME_COOKIE_KEY, null);
		cookie.setMaxAge(0);
		cookie.setPath(getCookiePath(request));

		response.addCookie(cookie);
	}

	private String getCookiePath(HttpServletRequest request) {
		String contextPath = request.getContextPath();
		return contextPath.length() > 0 ? contextPath : "/";
	}

	/**
	 * Calculates the digital signature to be put in the cookie. Default value is
	 * MD5 ("username:tokenExpiryTime:password:key")
	 */
	protected String makeTokenSignature(long tokenExpiryTime, String username) {
		String data = username + ":" + tokenExpiryTime + ":" + REMEMBER_ME_KEY;
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("No MD5 algorithm available!");
		}

		return new String(Hex.encode(digest.digest(data.getBytes())));
	}

	/**
	 * Constant time comparison to prevent against timing attacks.
	 */
	private static boolean equals(String expected, String actual) {
		byte[] expectedBytes = bytesUtf8(expected);
		byte[] actualBytes = bytesUtf8(actual);
		if (expectedBytes.length != actualBytes.length) {
			return false;
		}

		int result = 0;
		for (int i = 0; i < expectedBytes.length; i++) {
			result |= expectedBytes[i] ^ actualBytes[i];
		}
		return result == 0;
	}

	private static byte[] bytesUtf8(String s) {
		if (s == null) {
			return null;
		}
		return Utf8.encode(s);
	}

	public TenantManager getTenantManager() {
		return tenantManager;
	}

	public void setTenantManager(TenantManager tenantManager) {
		this.tenantManager = tenantManager;
	}

	public UserManager getUserManager() {
		return userManager;
	}

	public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}

	public boolean isAlwaysRemember() {
		return alwaysRemember;
	}

	public void setAlwaysRemember(boolean alwaysRemember) {
		this.alwaysRemember = alwaysRemember;
	}

	public int getTokenValiditySeconds() {
		return tokenValiditySeconds;
	}

	public void setTokenValiditySeconds(int tokenValiditySeconds) {
		this.tokenValiditySeconds = tokenValiditySeconds;
	}

	public Boolean getUseSecureCookie() {
		return useSecureCookie;
	}

	public void setUseSecureCookie(Boolean useSecureCookie) {
		this.useSecureCookie = useSecureCookie;
	}

	public String getParameter() {
		return parameter;
	}

	public void setParameter(String parameter) {
		this.parameter = parameter;
	}
}
