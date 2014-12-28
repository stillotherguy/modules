package com.weaver.teams.security.login;

import java.io.IOException;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import com.weaver.teams.api.base.ClientInfo;
import com.weaver.teams.security.authentication.AuthenticationProcessor;
import com.weaver.teams.security.authentication.AuthenticationUtil;
import com.weaver.teams.security.authentication.TeamsAuthenticationToken;

/**
 * 登录过滤器，忽略大小写
 * 
 * @author Ricky
 */
public class TeamsFormLoginFilter extends AbstractAuthenticationProcessingFilter {

	private List<AuthenticationProcessor> authenticationProcessors;

	private static final String LOGIN_URL = "/teamsLogin";

	private boolean postOnly = true;

	protected TeamsFormLoginFilter() {
		super(LOGIN_URL);
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

		if (postOnly && !request.getMethod().equals("POST")) {
			throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
		}

		try { // 附加登录验证接口，用于验证动态密码，uKey和图片验证码的验证
			for (AuthenticationProcessor processor : authenticationProcessors) {
				processor.authentication(request, response);
			}
		} catch (AuthenticationException failed) {
			try {
				unsuccessfulAuthentication(request, response, failed);
				return null;
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		String username = AuthenticationUtil.obtainUsername(request);
		String password = AuthenticationUtil.obtainPassword(request);
		ClientInfo client = ClientInfo.obtainClient(request);

		TeamsAuthenticationToken authRequest = new TeamsAuthenticationToken(username, password, client);
		setDetails(request, authRequest);
		return getAuthenticationManager().authenticate(authRequest);
	}

	protected void setDetails(HttpServletRequest request, TeamsAuthenticationToken authRequest) {
		authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult)
			throws IOException, ServletException {

		// 更新 SecurityContextHolder
		SecurityContextHolder.getContext().setAuthentication(authResult);

		getRememberMeServices().loginSuccess(request, response, authResult);

		// Fire event
		if (this.eventPublisher != null) {
			eventPublisher.publishEvent(new InteractiveAuthenticationSuccessEvent(authResult, this.getClass()));
		}

		getSuccessHandler().onAuthenticationSuccess(request, response, authResult);

	}

	public List<AuthenticationProcessor> getAuthenticationProcessors() {
		return authenticationProcessors;
	}

	public void setAuthenticationProcessors(List<AuthenticationProcessor> authenticationProcessors) {
		this.authenticationProcessors = authenticationProcessors;
	}

	public boolean isPostOnly() {
		return postOnly;
	}

	public void setPostOnly(boolean postOnly) {
		this.postOnly = postOnly;
	}
}
