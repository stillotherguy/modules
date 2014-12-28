package com.weaver.teams.security.authentication;

/**
 * 通过http的方式访问第三方系统验证用户名密码
 * @author Ricky
 */
public interface HttpAuthenticationService {

	boolean authenticate(String accountName, String passWord);

}
