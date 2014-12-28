package com.weaver.teams.security.authentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.weaver.teams.api.tenant.Tenant;
import com.weaver.teams.api.user.User;
import com.weaver.teams.api.user.UserStatus;
import com.weaver.teams.security.exception.UserDisabledException;

@Service
public class PreAuthenticationChecker {

	private final Logger logger = LoggerFactory.getLogger(PreAuthenticationChecker.class);

	/**
	 * 租户状态检查
	 * 
	 * @param tenant
	 */
	public void checkTenant(Tenant tenant) {
		// 过期的租户可以继续使用
		// if(!tenant.isAvailable()){
		// logger.info("Tenant account is not available .");
		// throw new TenantExpiredException("Tenant is expired");
		// }
	}

	/**
	 * 用户状态检查
	 * 
	 * @param user
	 */
	public void checkUser(User user) {
		if (user.getStatus().equals(UserStatus.temp)) {
			logger.info("User account is disabled");
			throw new UserDisabledException("User is disabled");
		}
	}
}
