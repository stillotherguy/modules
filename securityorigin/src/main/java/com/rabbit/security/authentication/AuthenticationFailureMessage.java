package com.weaver.teams.security.authentication;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.AuthenticationException;

import com.weaver.teams.security.exception.CaptchaAuthenticationException;
import com.weaver.teams.security.exception.DynamicPasswdAuthenticationException;
import com.weaver.teams.security.exception.TenantExpiredException;
import com.weaver.teams.security.exception.UserDisabledException;
import com.weaver.teams.security.exception.UserNotFoundException;
import com.weaver.teams.security.exception.WrongPasswordException;

public class AuthenticationFailureMessage {

	private static final Map<String, String> failureMessageMap = new HashMap<String, String>();

	static {
		failureMessageMap.put(CaptchaAuthenticationException.class.getName(), "验证码输入不正确!");
		failureMessageMap.put(DynamicPasswdAuthenticationException.class.getName(), "动态密码不正确！");
		failureMessageMap.put(TenantExpiredException.class.getName(), "你输入的公司帐号已过期！");
		failureMessageMap.put(UserNotFoundException.class.getName(), "你输入的帐号不存在！");
		failureMessageMap.put(WrongPasswordException.class.getName(), "你输入的密码不正确！");
		failureMessageMap.put(UserDisabledException.class.getName(), "你的帐号已关闭！");
	}

	public static String getFailureMessage(AuthenticationException exception){
		return failureMessageMap.get(exception.getClass().getName());
	}
}
