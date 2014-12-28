package com.weaver.teams.security.authentication;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.weaver.teams.security.exception.WrongPasswordException;


/**
/* 通过访问第三方系统来验证用户名和密码
 * @author Ricky
 */
public class HttpAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider{
	
	private UserDetailsService userDetailsService;
	private HttpAuthenticationService httpAuthenticationService;
	
	@Override
	protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication)
			throws AuthenticationException {

		String accountName = (String) authentication.getPrincipal();
		String passWord = (String) authentication.getCredentials();
		
		boolean authenticated = httpAuthenticationService.authenticate(accountName, passWord);
		if (!authenticated) {
			throw new WrongPasswordException("Http Authentication Failed!");
		}
	}
	
	@Override
	protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
		return userDetailsService.loadUserByUsername(username);
	}
	
	public UserDetailsService getUserDetailsService() {
		return userDetailsService;
	}

	public void setUserDetailsService(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	public HttpAuthenticationService getHttpAuthenticationService() {
		return httpAuthenticationService;
	}

	public void setHttpAuthenticationService(HttpAuthenticationService httpAuthenticationService) {
		this.httpAuthenticationService = httpAuthenticationService;
	}

}
