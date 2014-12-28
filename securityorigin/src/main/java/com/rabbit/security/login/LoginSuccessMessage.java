package com.weaver.teams.security.login;

import com.weaver.teams.api.base.ClientInfo;
import com.weaver.teams.api.tenant.Tenant;
import com.weaver.teams.api.user.User;
import com.weaver.teams.core.SpringContext;
import com.weaver.teams.jms.AbstractJmsMessage;
import com.weaver.teams.jms.JmsMessageProcessor;

public class LoginSuccessMessage extends AbstractJmsMessage {

	private static final long serialVersionUID = 2646522957369951167L;

	private Tenant tenant;
	private User user;
	private ClientInfo client;

	public LoginSuccessMessage(Tenant tenant, User user, ClientInfo client) {
		super();
		this.tenant = tenant;
		this.user = user;
		this.client = client;
	}

	@Override
	public JmsMessageProcessor<LoginSuccessMessage> getMessageProcessor() {
		return SpringContext.getBean(LoginSuccessMessageProcessor.class);
	}

	public Tenant getTenant() {
		return tenant;
	}

	public User getUser() {
		return user;
	}

	public ClientInfo getClient() {
		return client;
	}

}
