package com.weaver.teams.security.interceptor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.weaver.teams.api.user.User;

public class UrlSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

	private Map<String, List<ConfigAttribute>> defaultRequestMap = new LinkedHashMap<String, List<ConfigAttribute>>();

	public UrlSecurityMetadataSource() {
		List<ConfigAttribute> adminSecurityConfigs = new ArrayList<ConfigAttribute>();
		adminSecurityConfigs.add(new SecurityConfig(User.ROLE_ADMIN));
		defaultRequestMap.put("/admin/**", adminSecurityConfigs);

		List<ConfigAttribute> userSecurityConfigs = new ArrayList<ConfigAttribute>();
		userSecurityConfigs.add(new SecurityConfig(User.ROLE_USER));
		defaultRequestMap.put("/**", userSecurityConfigs);
	}

	/**
	 * 返回对当前URL有访问权限的ROLE
	 */
	@Override
	public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
		HttpServletRequest request = ((FilterInvocation) object).getRequest();
		return lookupAttributes(request);
	}

	private List<ConfigAttribute> lookupAttributes(HttpServletRequest request) {
		for (Entry<String, List<ConfigAttribute>> entry : defaultRequestMap.entrySet()) {
			RequestMatcher matcher = new AntPathRequestMatcher(entry.getKey());
			if (matcher.matches(request)) {
				return entry.getValue();
			}
		}
		return null;
	}

	@Override
	public Collection<ConfigAttribute> getAllConfigAttributes() {
		Set<ConfigAttribute> allAttributes = new HashSet<ConfigAttribute>();

		for (Entry<String, List<ConfigAttribute>> entry : defaultRequestMap.entrySet()) {
			allAttributes.addAll(entry.getValue());
		}
		return allAttributes;
	}

	/**
	 * 支持FilterInvocation
	 */
	@Override
	public boolean supports(Class<?> clazz) {
		return FilterInvocation.class.isAssignableFrom(clazz);
	}

}
