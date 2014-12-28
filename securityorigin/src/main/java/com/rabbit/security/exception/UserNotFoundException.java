package com.weaver.teams.security.exception;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserNotFoundException extends UsernameNotFoundException{


	private static final long serialVersionUID = 1L;

	public UserNotFoundException(String msg) {
		super(msg);
	}
	public UserNotFoundException(String msg, Throwable t) {
		super(msg, t);
	}

}
