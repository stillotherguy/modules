package com.weaver.teams.security.authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;

import com.weaver.teams.api.captcha.Captcha;
import com.weaver.teams.security.config.SecurityConfig;
import com.weaver.teams.security.exception.CaptchaAuthenticationException;

/**
 * 验证码 验证
 * 
 * @author Ricky
 */
public class CaptchaAuthentication implements AuthenticationProcessor {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	// 保存在session中的属性名
	public static final String CAPTCHA_ATTR_NAME = "captcha";
	// 页面参数名
	public static final String CAPTCHA_PARAM_NAME = "captcha";

	@Override
	public void authentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
		if (SecurityConfig.enableCaptcha()) {
			HttpSession session = request.getSession();
			if (session.getAttribute(CAPTCHA_ATTR_NAME) != null) {
				logger.info("Authenticat Captcha...");
				if (!attemptAuthentication(request)) {
					String msg = "wrong captcha.";
					logger.info(msg);
					throw new CaptchaAuthenticationException(msg);
				}
				session.removeAttribute(CAPTCHA_ATTR_NAME);
			}
		}
	}

	private boolean attemptAuthentication(HttpServletRequest request) throws AuthenticationException {
		HttpSession session = request.getSession();
		Captcha captcha = (Captcha) session.getAttribute(CAPTCHA_ATTR_NAME);
		String captchaString = request.getParameter(CAPTCHA_PARAM_NAME);
		return captchaString == null ? true : captcha.validateResponse(captchaString);
	}
}
