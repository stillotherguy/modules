package com.weaver.teams.security.login;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.web.DefaultRedirectStrategy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weaver.teams.api.base.ClientInfo;
import com.weaver.teams.api.tenant.Tenant;
import com.weaver.teams.api.user.User;
import com.weaver.teams.core.TenantContext;
import com.weaver.teams.core.UserContext;
import com.weaver.teams.core.user.UserImpl;

/**
 * 登录成功页面跳转策略
 * 
 * @author Ricky
 */
public class LoginSuccessRedirect extends DefaultRedirectStrategy {

	@Override
	public void sendRedirect(HttpServletRequest request, HttpServletResponse response, String url) throws IOException {
		// 记住之前访问的页面
		// String redirectUrl = calculateRedirectUrl(url);
		ClientInfo client = ClientInfo.obtainClient(request);
		if (client.isMobile()) {
			sendJsonRedirect(request, response);
		} else {
			super.sendRedirect(request, response, url);
		}
	}

	/**
	 * 返回JSON数据
	 * 
	 * @param request
	 * @param response
	 */
	protected void sendJsonRedirect(HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("application/json;charset=UTF-8");
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Expires", "0");
		response.setHeader("Pragma", "No-cache");
		HashMap<String, String> map = new HashMap<String, String>();

		User user = UserContext.getCurrentUser();
		UserImpl userImpl = (UserImpl) UserContext.getCurrentUserImpl();
		Tenant tenant = TenantContext.getCurrentTenant();
		HttpSession session = request.getSession();
		map.put("jsessionid", session.getId());
		map.put("succeed", Boolean.toString(true));
		if (user != null) {
			map.put("userid", Long.toString(user.getEmployeeId()));
			map.put("username", user.getUsername());
		} else if (userImpl != null) {
			map.put("userid", Long.toString(userImpl.getId()));
			map.put("username", userImpl.getUsername());
		}

		if (tenant != null) {
			map.put("tenantkey", tenant.getTenantKey());
		}

		ObjectMapper mapper = new ObjectMapper();
		PrintWriter out;
		try {
			out = response.getWriter();
			mapper.writeValue(out, map);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

}
