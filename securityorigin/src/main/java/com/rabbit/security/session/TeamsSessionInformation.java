package com.weaver.teams.security.session;

import java.util.Date;

import javax.servlet.http.HttpSession;

import org.springframework.security.core.session.SessionInformation;

public class TeamsSessionInformation extends SessionInformation{
	
	private static final long serialVersionUID = 3997536940340285750L;
	
	private final HttpSession session;
	
	public TeamsSessionInformation(Object principal, HttpSession session, Date lastRequest) {
		super(principal, session.getId(), lastRequest);
		this.session = session;
	}

	public HttpSession getSession() {
		return session;
	}

}
