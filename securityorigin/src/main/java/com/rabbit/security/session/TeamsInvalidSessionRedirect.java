package com.weaver.teams.security.session;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.web.DefaultRedirectStrategy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weaver.teams.api.base.ClientInfo;
import com.weaver.teams.core.mvc.ActionMessage;

/**
 * session过期页面跳转到登录页面
 * 
 * @author Ricky
 * 
 */
public class TeamsInvalidSessionRedirect extends DefaultRedirectStrategy {
	@Override
	public void sendRedirect(HttpServletRequest request, HttpServletResponse response, String url) throws IOException {
		ClientInfo client = ClientInfo.obtainClient(request);
		if (client.isMobile() || isAjaxRequest(request)) {
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

		ActionMessage actionMsg = new ActionMessage(ActionMessage.SESSION_EXPIRED);
		HashMap<String, Object> map = new HashMap<String, Object>();

		map.put("actionMsg", actionMsg);

		ObjectMapper mapper = new ObjectMapper();
		PrintWriter out;
		try {
			out = response.getWriter();
			mapper.writeValue(out, map);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	private boolean isAjaxRequest(HttpServletRequest request) {
		String requestedWith = request.getHeader("X-Requested-With");
		return requestedWith != null ? "XMLHttpRequest".equals(requestedWith) : false;
	}
}
