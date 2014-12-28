package com.weaver.teams.security.login;

import java.util.Collection;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.weaver.teams.api.tenant.Tenant;
import com.weaver.teams.core.SpringContext;
import com.weaver.teams.core.TenantContext;
import com.weaver.teams.jms.JmsMessageProcessor;

@Service
public class LoginSuccessMessageProcessor implements JmsMessageProcessor<LoginSuccessMessage> {

	@Override
	public void processMessage(LoginSuccessMessage message) {
		Tenant tenant = message.getTenant();
		TenantContext.setTemporaryContext(tenant);
		Map<String, LoginSuccessService> map = SpringContext.getBeans(LoginSuccessService.class);
		Collection<LoginSuccessService> handlers = map.values();
		for (LoginSuccessService handler : handlers) {
			handler.postLoginSuccess(message.getTenant(), message.getUser(), message.getClient());
		}
		TenantContext.clearTemporaryContext();
	}

}
