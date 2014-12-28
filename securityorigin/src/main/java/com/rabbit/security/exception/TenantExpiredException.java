package com.weaver.teams.security.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * 租户未续费时抛出的异常
 * @author Ricky
 */
public class TenantExpiredException extends AuthenticationException{

	private static final long serialVersionUID = 1L;

	public TenantExpiredException(String msg) {
		super(msg);
	}

}
