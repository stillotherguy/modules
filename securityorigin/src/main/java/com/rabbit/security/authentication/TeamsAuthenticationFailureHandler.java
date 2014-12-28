package com.weaver.teams.security.authentication;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weaver.teams.api.base.ClientInfo;

public class TeamsAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
			throws IOException, ServletException {

		ClientInfo client = ClientInfo.obtainClient(request);
		if (client.isMobile()) {
			sendJsonRedirect(request, response, exception);
		} else {
			super.onAuthenticationFailure(request, response, exception);
		}
	}

	/**
	 * 返回JSON数据
	 * 
	 * @param request
	 * @param response
	 * @param exception
	 */
	protected void sendJsonRedirect(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) {
		response.setContentType("application/json;charset=UTF-8");
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Expires", "0");
		response.setHeader("Pragma", "No-cache");
		HashMap<String, String> map = new HashMap<String, String>();

		HttpSession session = request.getSession();
		map.put("jsessionid", session.getId());
		map.put("succeed", Boolean.toString(false));
		map.put("message", AuthenticationFailureMessage.getFailureMessage(exception));
		session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
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
