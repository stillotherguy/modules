package com.weaver.teams.security.authentication;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class TeamsRememberMeAuthenticationProvider implements AuthenticationProvider {
	
	 
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		if (!supports(authentication.getClass())) {
            return null;
        }

        if (!authentication.isAuthenticated()) {
        	return null;
        	//throw new BadCredentialsException("The presented RememberMeAuthenticationToken does not contain the expected key");
        }

        return authentication;
	}

	@Override
	 public boolean supports(Class<?> authentication) {
        return (TeamsAuthenticationToken.class.isAssignableFrom(authentication));
    }

}
