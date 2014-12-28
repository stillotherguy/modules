package com.weaver.teams.security.authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;

/**
 * 附加登录验证接口，用于验证动态密码，uKey和图片验证码的验证
 * @author Ricky
 */
public interface AuthenticationProcessor {
	void authentication(HttpServletRequest request,HttpServletResponse response) throws AuthenticationException;
}
