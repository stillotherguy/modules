package com.weaver.teams.security.login;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import com.weaver.teams.api.base.ClientInfo;
import com.weaver.teams.api.tenant.Tenant;
import com.weaver.teams.api.user.User;
import com.weaver.teams.jms.JmsMessageService;
import com.weaver.teams.security.authentication.TeamsAuthenticationToken;

/**
 * 登录成功后的附加操作接口<br>
 * 继承自{@link SavedRequestAwareAuthenticationSuccessHandler}
 * 
 * @author Ricky
 */
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

	public static final String REMEMBER_PARAM = "rememberMe";
	public static final String COOKIE_KEY = "TEAMS_COOKIE_KEY";
	public static final int MAX_AGE = 3600 * 24 * 14;// 默认两周

	@Autowired
	private JmsMessageService messageService;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication)
			throws ServletException, IOException {
		postLoginSuccess(request, response, authentication);
		super.onAuthenticationSuccess(request, response, authentication);
	}

	/**
	 * 登录成功后的事件处理接口
	 */
	public void postLoginSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		TeamsAuthenticationToken teamsAuthenticationToken = (TeamsAuthenticationToken) authentication;
		Tenant tenant = teamsAuthenticationToken.getTenant();
		if (tenant != null) {
			User user = (User) teamsAuthenticationToken.getPrincipal();
			ClientInfo clientInfo = teamsAuthenticationToken.getClientInfo();
			// 记住帐号(tenantKey和account)
			// TODO 账号管理
			// if (Boolean.parseBoolean(request.getParameter(REMEMBER_PARAM))) {
			// String[] cookieValues = new String[] { tenant.getTenantKey(), user.getAccount()};
			// Cookie cookie = new Cookie(COOKIE_KEY, StringUtils.encodeCookie(cookieValues));
			// cookie.setMaxAge(MAX_AGE);
			// cookie.setPath(getCookiePath(request));
			// response.addCookie(cookie);
			// }

			// 执行PostLoginSuccessHandler接口函数
			LoginSuccessMessage message = new LoginSuccessMessage(tenant, user, clientInfo);
			messageService.createMessage(message);
		}

	}

	protected String getCookiePath(HttpServletRequest request) {
		String contextPath = request.getContextPath();
		return contextPath.length() > 0 ? contextPath : "/";
	}

}
