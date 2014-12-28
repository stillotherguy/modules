package com.weaver.teams.security.exception;

import org.springframework.security.core.AuthenticationException;

public class DynamicPasswdAuthenticationException extends AuthenticationException {

	private static final long serialVersionUID = 5214181380567859548L;

	public DynamicPasswdAuthenticationException(String msg) {
		super(msg);
	}

}
