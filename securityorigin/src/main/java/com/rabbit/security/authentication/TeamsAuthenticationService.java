package com.weaver.teams.security.authentication;

import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.weaver.teams.api.base.ClientInfo;
import com.weaver.teams.api.tenant.Tenant;
import com.weaver.teams.api.tenant.TenantManager;
import com.weaver.teams.api.user.User;
import com.weaver.teams.core.TenantContext;
import com.weaver.teams.core.user.UserBuilder;
import com.weaver.teams.core.user.UserImpl;
import com.weaver.teams.core.user.UserManager;
import com.weaver.teams.security.exception.WrongPasswordException;
import com.weaver.teams.security.login.LoginSuccessHandler;
import com.weaver.teams.security.session.TeamsConcurrentSessionControlStrategy;
import com.weaver.teams.util.EncodeUtils;

/**
 * 注册过程中的第二步验证
 * 
 * @author Ricky
 */
@Service
public class TeamsAuthenticationService {

	private final Logger logger = LoggerFactory.getLogger(TeamsAuthenticationService.class);

	@Autowired
	private TenantManager tenantManager;
	@Autowired
	private UserManager userManager;
	@Autowired
	private UserBuilder userBuilder;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private TeamsConcurrentSessionControlStrategy sessionStrategy;
	@Autowired
	private SecurityContextRepository securityContextRepository;

	@Autowired
	private LoginSuccessHandler loginSuccessHandler;
	@Autowired
	private PreAuthenticationChecker preAuthenticationChecks;

	/**
	 * 后台登录操作，登录成功后没有页面跳转，
	 * 
	 * @param key 密钥，使用 EncodeUtils.encode(account)加密后的密钥
	 * @param request
	 * @param response
	 * @return 登录成功则返回Authentication，失败返回null
	 */
	public Authentication login(String key, HttpServletRequest request, HttpServletResponse response) {
		String account = EncodeUtils.decode(key);
		// 空账号不验证
		if (StringUtils.isEmpty(account)) {
			return null;
		}
		ClientInfo client = ClientInfo.obtainClient(request);
		TeamsAuthenticationToken authRequest = new TeamsAuthenticationToken(account, key, client);

		UserImpl loadedUser = userManager.loadUser(account);
		if (loadedUser == null) {
			logger.info("登录失败,Account not found:" + account);
			return null;
		}

		Tenant tenant = tenantManager.loadTenant(loadedUser.getTenantKey());

		preAuthenticationChecks.checkUser(loadedUser);

		Authentication authResult = createSuccessAuthentication(authRequest, tenant, loadedUser);
		if (authResult != null) {
			successfulAuthentication(request, response, authResult);
		}
		return authResult;
	}

	/**
	 * 后台登录操作，登录成功后没有页面跳转，
	 * 
	 * @param user 用户信息(密码必须,account或者email或者mobile必须有一个不为空)
	 * @param request
	 * @param response
	 * @return 登录成功则返回Authentication，失败返回null
	 */
	public Authentication login(UserImpl user, HttpServletRequest request, HttpServletResponse response) {
		String account = null;
		String[] accounts = new String[] { user.getMobile(), user.getEmail(), user.getAccount() };
		for (String str : accounts) {
			if (!StringUtils.isEmpty(str)) {
				account = str;
				break;
			}
		}
		// 空账号不验证
		if (StringUtils.isEmpty(account)) {
			return null;
		}
		String planPassword = user.getPassword();
		ClientInfo client = ClientInfo.obtainClient(request);
		TeamsAuthenticationToken authRequest = new TeamsAuthenticationToken(account, planPassword, client);

		UserImpl loadedUser = userManager.loadUser(account);
		if (loadedUser == null) {
			logger.info("登录失败,Account not found:" + account);
			return null;
		}

		Tenant tenant = tenantManager.loadTenant(loadedUser.getTenantKey());

		preAuthenticationChecks.checkUser(loadedUser);

		Authentication authResult = createSuccessAuthentication(authRequest, tenant, loadedUser);
		if (authResult != null) {
			successfulAuthentication(request, response, authResult);
		}
		return authResult;
	}

	/**
	 * 完成注册第一步后,加入/创建公司,重新加载租户信息
	 * 
	 * @param authentication 已经验证过的token
	 * @param request
	 * @param response
	 * @return 新的 {@link TeamsAuthenticationToken} 包含Tenant信息
	 * @throws AuthenticationException
	 */
	public Authentication reAuthenticate(Authentication authentication, HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		Assert.isInstanceOf(TeamsAuthenticationToken.class, authentication, "Only TeamsAuthenticationToken is supported");

		/**
		 * 没有被验证过的Token
		 */
		if (!authentication.isAuthenticated()) {
			return authentication;
		}

		TeamsAuthenticationToken authenticationToken = (TeamsAuthenticationToken) authentication;
		// 此时authenticationToken包含的Principal为account
		String account = (String) authenticationToken.getPrincipal();
		try {
			User user = userManager.loadUser(account);
			if (user.getTenantKey() != null) {
				Tenant tenant = tenantManager.reloadTenant(user.getTenantKey());
				Authentication newAuthentication = createSuccessAuthentication(authenticationToken, tenant, user);
				successfulAuthentication(request, response, newAuthentication);
				return newAuthentication;
			} else {
				// 用户注册未完成 没有添加公司 或者没有加入公司
				return authentication;
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new AuthenticationServiceException(e.getMessage(), e);
		}

	}

	/**
	 * @param authentication
	 * @param tenant
	 * @param user
	 * @return 若存在租户，则principal对象为Employee,租户不存在时返回对象为用户名
	 */
	protected Authentication createSuccessAuthentication(TeamsAuthenticationToken authentication, Tenant tenant, User user) {
		Object credentials = authentication.getCredentials();
		ClientInfo clientInfo = authentication.getClientInfo();
		Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		authorities.add(new SimpleGrantedAuthority(User.ROLE_USER));

		Object principal = authentication.getPrincipal();

		if (tenant != null) {
			TenantContext.setTemporaryContext(tenant);
			// 此处principal为employee对象
			principal = userBuilder.buildLoginUser(tenant, user, clientInfo);
			TenantContext.clearTemporaryContext();

			authorities = (Collection<GrantedAuthority>) ((User) principal).getAuthorities();
		}
		TeamsAuthenticationToken newAuthentication = new TeamsAuthenticationToken(principal, credentials, clientInfo, tenant, user, authorities);
		newAuthentication.setDetails(authentication.getDetails());
		return newAuthentication;
	}

	/**
	 * 用户密码验证
	 */
	protected void passwordAuthenticationChecks(UserImpl user, TeamsAuthenticationToken authentication) throws AuthenticationException {
		if (authentication.getCredentials() == null) {
			logger.info("Authentication failed: no credentials provided");
			throw new WrongPasswordException("密码错误!");
		}

		String presentedPassword = authentication.getCredentials().toString();

		boolean isPasswordValid = passwordEncoder.matches(presentedPassword, user.getPassword());

		if (!isPasswordValid) {
			logger.info("Authentication failed: password does not match stored value");
			throw new WrongPasswordException("密码错误!");
		}
	}

	/**
	 * 登录成功后相关处理
	 * 
	 * @param request
	 * @param response
	 * @param authResult
	 */
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, Authentication authResult) {

		sessionStrategy.onAuthentication(authResult, request, response);

		HttpRequestResponseHolder holder = new HttpRequestResponseHolder(request, response);
		securityContextRepository.loadContext(holder);
		// 更新 SecurityContextHolder
		SecurityContextHolder.getContext().setAuthentication(authResult);

		SecurityContext contextAfterChainExecution = SecurityContextHolder.getContext();
		// Crucial removal of SecurityContextHolder contents - do this before anything else.
		securityContextRepository.saveContext(contextAfterChainExecution, holder.getRequest(), holder.getResponse());

		try {
			loginSuccessHandler.postLoginSuccess(request, response, authResult);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

	}

}
