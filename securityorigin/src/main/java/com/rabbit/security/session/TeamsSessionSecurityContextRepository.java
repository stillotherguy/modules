package com.weaver.teams.security.session;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

public class TeamsSessionSecurityContextRepository extends HttpSessionSecurityContextRepository {

	private static final String JSESSIONID = "jsessionid";
	private static final String SESSIONKEY = "sessionkey";

	private TeamsSessionRegistry sessionRegistry;

	@Override
	public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
		SecurityContext context = super.loadContext(requestResponseHolder);
		HttpServletRequest request = requestResponseHolder.getRequest();

		String sessionId = resolveSessionKey(request);
		if (!StringUtils.isEmpty(sessionId)) {
			SessionInformation info = sessionRegistry.getSessionInformation(sessionId);
			if ((info != null) && !info.isExpired()) {
				context.setAuthentication((Authentication) info.getPrincipal());
			}
		}

		return context;
	}

	@Override
	public boolean containsContext(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		String sessionId = resolveSessionKey(request);
		if (sessionId != null) {
			TeamsSessionInformation info = sessionRegistry.getTeamsSessionInformation(sessionId);
			if (info != null) {
				session = info.getSession();
				// 动态调整Session过期时间
				long lastAccessed = session.getLastAccessedTime();
				long inactiveIncrement = session.getMaxInactiveInterval() * 1000;
				session.setMaxInactiveInterval((int) (((System.currentTimeMillis() - lastAccessed) + inactiveIncrement) / 1000));
			}
		}

		if (session == null) {
			return false;
		}

		return session.getAttribute(SPRING_SECURITY_CONTEXT_KEY) != null;
	}

	/**
	 * 手机api请求或者flash请求直接带sessionid参数
	 * 
	 * @param request
	 * @return
	 */
	private String resolveSessionKey(HttpServletRequest request) {
		String jsessionid = request.getParameter(JSESSIONID);
		String sessionkey = request.getParameter(SESSIONKEY);
		return jsessionid != null ? jsessionid : sessionkey;
	}

	public void setSessionRegistry(TeamsSessionRegistry sessionRegistry) {
		this.sessionRegistry = sessionRegistry;
	}
}
