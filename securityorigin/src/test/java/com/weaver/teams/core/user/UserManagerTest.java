package com.weaver.teams.core.user;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import com.weaver.teams.api.user.User;
import com.weaver.teams.security.exception.UserNotFoundException;

@ContextConfiguration(locations = { "classpath:applicationContextTest-core.xml" })
public class UserManagerTest extends AbstractJUnit4SpringContextTests{
	
	@Autowired
	private UserManager userManager;

	@Test
	public void testLoadUser() {
		String account ="admin";
		try{
			User user1 = userManager.loadUser(account);
			logger.info(user1.getUsername());
			assertNotNull(user1);
		}catch (Exception e) {
			fail(e.getMessage());
		}
		try{
			userManager.loadUser(account);
			fail("未抛出异常");
		}catch (UserNotFoundException e) {
		}
	}

}
