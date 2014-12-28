package com.weaver.teams.security.authentication;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import com.weaver.teams.api.base.ClientInfo;
import com.weaver.teams.api.tenant.Tenant;
import com.weaver.teams.api.user.User;

public class TeamsAuthenticationToken extends AbstractAuthenticationToken {

	private static final long serialVersionUID = -2469458559362807924L;

	// 登录成功之前保存用户帐号名，登录成功后保存 Employee对象
	private Object principal;
	private Object credentials;
	private User user;
	// 登录成功后保存 TenantInfo对象
	private Tenant tenant;
	// 客户端信息
	private ClientInfo clientInfo;

	/**
	 * TeamsAuthenticationToken初始化，未认证的Token
	 * 
	 * @param principal 用户信息
	 * @param credentials 密码信息
	 * @param clientInfo 客户端消息
	 */
	public TeamsAuthenticationToken(Object principal, Object credentials, ClientInfo clientInfo) {
		super(null);
		this.principal = principal;
		this.credentials = credentials;
		this.clientInfo = clientInfo;
		setAuthenticated(false);
	}

	public TeamsAuthenticationToken(Object principal, Object credentials, ClientInfo clientInfo, Tenant tenant, User user,
			Collection<? extends GrantedAuthority> collection) {
		super(collection);
		this.principal = principal;
		this.credentials = credentials;
		this.clientInfo = clientInfo;
		this.tenant = tenant;
		this.user = user;
		super.setAuthenticated(true);
	}

	public TeamsAuthenticationToken(Tenant tenant) {
		super(null);
		this.tenant = tenant;
	}

	public void updatePrincipal(Object newPrincipal) {
		this.principal = newPrincipal;
	}

	@Override
	public Object getCredentials() {
		return this.credentials;
	}

	@Override
	public Object getPrincipal() {
		return this.principal;
	}

	public Tenant getTenant() {
		return tenant;
	}

	public User getUser() {
		return user;
	}

	public ClientInfo getClientInfo() {
		return clientInfo;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = (prime * result) + ((principal == null) ? 0 : principal.hashCode());
		result = (prime * result) + ((tenant == null) ? 0 : tenant.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		TeamsAuthenticationToken other = (TeamsAuthenticationToken) obj;
		if (principal == null) {
			if (other.principal != null) {
				return false;
			}
		} else if (!principal.equals(other.principal)) {
			return false;
		}
		if (tenant == null) {
			if (other.tenant != null) {
				return false;
			}
		} else if (!tenant.equals(other.tenant)) {
			return false;
		}
		return true;
	}

}
