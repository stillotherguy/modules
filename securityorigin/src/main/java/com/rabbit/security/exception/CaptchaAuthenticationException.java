package com.weaver.teams.security.exception;

import org.springframework.security.core.AuthenticationException;

public class CaptchaAuthenticationException extends AuthenticationException {

	private static final long serialVersionUID = -1412828355942323819L;

	public CaptchaAuthenticationException(String msg) {
		super(msg);
	}

	public CaptchaAuthenticationException(String msg, Throwable t) {
		super(msg, t);
	}

}
