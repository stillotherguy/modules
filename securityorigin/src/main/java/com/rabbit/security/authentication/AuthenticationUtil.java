package com.weaver.teams.security.authentication;

import javax.servlet.http.HttpServletRequest;

public class AuthenticationUtil {

	/**
	 * 定义登录页面参数名称
	 */
	private static final String USERNAME_PARAM = "username";
	private static final String PASSWORD_PARAM = "password";
	private static final String TENANT_PARAM = "tenantid";

	/**
	 * 用户名转换为小写字母
	 */
	public static String obtainUsername(HttpServletRequest request) {
		String userName = request.getParameter(USERNAME_PARAM);
		return (userName == null ? "" : userName).trim().toLowerCase();
	}

	/**
	 * 租户ID
	 */
	public static String obtainTenantid(HttpServletRequest request) {
		String userName = request.getParameter(TENANT_PARAM);
		return (userName == null ? "" : userName).trim().toLowerCase();
	}

	/**
	 * 密码
	 */
	public static String obtainPassword(HttpServletRequest request) {
		String password = request.getParameter(PASSWORD_PARAM);
		return password == null ? "" : password;
	}

}
