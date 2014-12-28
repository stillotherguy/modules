package com.weaver.teams.security.authentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.Assert;

import com.weaver.teams.api.tenant.Tenant;
import com.weaver.teams.api.tenant.TenantManager;
import com.weaver.teams.core.user.UserBuilder;
import com.weaver.teams.core.user.UserImpl;
import com.weaver.teams.core.user.UserManager;
import com.weaver.teams.security.exception.UserNotFoundException;

/**
 * 登录验证
 * 
 * @author Ricky
 */
public class TeamsAuthenticationProvider implements AuthenticationProvider {

	private final Logger logger = LoggerFactory.getLogger(TeamsAuthenticationProvider.class);

	@Autowired
	private TenantManager tenantManager;
	@Autowired
	private UserManager userManager;
	@Autowired
	private UserBuilder userBuilder;
	@Autowired
	private TeamsAuthenticationService authenticationService;

	/**
	 * 携带数据为 {@link TeamsAuthenticationToken}，包含Tenant信息
	 */
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		Assert.isInstanceOf(TeamsAuthenticationToken.class, authentication,
				"Only TeamsAuthenticationToken is supported");

		if (authentication.isAuthenticated()) {
			return authentication;
		}

		TeamsAuthenticationToken authenticationToken = (TeamsAuthenticationToken) authentication;

		if (authenticationToken.getPrincipal() == null) {
			throw new UserNotFoundException("Account not found:");
		}
		String account = authenticationToken.getPrincipal().toString();

		UserImpl loadedUser = userManager.loadUser(account);
		if (loadedUser == null) {
			logger.info("Account not found:" + account);
			throw new UserNotFoundException("Account not found:" + account);
		}
		Tenant tenant = tenantManager.loadTenant(loadedUser.getTenantKey());

		authenticationService.passwordAuthenticationChecks(loadedUser, authenticationToken);

		return authenticationService.createSuccessAuthentication(authenticationToken, tenant, loadedUser);
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return TeamsAuthenticationToken.class.isAssignableFrom(authentication);
	}

	public UserManager getUserManager() {
		return userManager;
	}

	public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}

	public TenantManager getTenantManager() {
		return tenantManager;
	}

	public void setTenantManager(TenantManager tenantManager) {
		this.tenantManager = tenantManager;
	}

}
