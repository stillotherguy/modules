package com.weaver.teams.security.login;

import com.weaver.teams.api.base.ClientInfo;
import com.weaver.teams.api.tenant.Tenant;
import com.weaver.teams.api.user.User;

/**
 * 登录成功事件处理接口
 * 
 * @author Ricky
 * 
 */
public interface LoginSuccessService<Employee extends User> {

	void postLoginSuccess(Tenant tenant, Employee employee, ClientInfo clientInfo);
}
