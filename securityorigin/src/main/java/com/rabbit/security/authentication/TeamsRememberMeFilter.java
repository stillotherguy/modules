package com.weaver.teams.security.authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationFilter;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

public class TeamsRememberMeFilter extends RememberMeAuthenticationFilter {

	private SessionAuthenticationStrategy sessionStrategy;

	public TeamsRememberMeFilter(AuthenticationManager authenticationManager, RememberMeServices rememberMeServices) {
		super(authenticationManager, rememberMeServices);
	}

	@Override
	protected void onSuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			Authentication authResult) {
		sessionStrategy.onAuthentication(authResult, request, response);
	}

	public SessionAuthenticationStrategy getSessionStrategy() {
		return sessionStrategy;
	}

	public void setSessionStrategy(SessionAuthenticationStrategy sessionStrategy) {
		this.sessionStrategy = sessionStrategy;
	}
}
