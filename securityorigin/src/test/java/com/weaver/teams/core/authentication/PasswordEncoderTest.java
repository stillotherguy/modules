package com.weaver.teams.core.authentication;

import static org.junit.Assert.assertTrue;

import org.jasypt.util.password.StrongPasswordEncryptor;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import com.weaver.teams.security.authentication.PasswordEncoder;

public class PasswordEncoderTest {
	
	private static Logger logger = LoggerFactory.getLogger(PasswordEncoderTest.class);

	private static PasswordEncoder passwordEncoder;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		passwordEncoder = new PasswordEncoder();
		StrongPasswordEncryptor jasyptPasswordEncryptor = new StrongPasswordEncryptor();
		passwordEncoder.setPasswordEncryptor(jasyptPasswordEncryptor);
	}


	@Test
	public void testEncodePassword(){
		String rawPass = "111111";
		String encPass = passwordEncoder.encode(rawPass);
		logger.info(encPass);
	}
	@Test
	public void testIsPasswordValid(){
		String rawPass = "123456";
		String encPass = passwordEncoder.encode(rawPass);
		assertTrue(passwordEncoder.matches(rawPass, encPass));
		//assertTrue(passwordEncoder.matches(rawPass,"S5DeW0KRt3ffT+/JYcjFoK+MzZGXAVeZN4kmXmoSLPZhNYGTt2Fuo/bqDC/ZAYgJ"));
		//assertTrue(passwordEncoder.matches(rawPass,"oaHkuh5MRV/42sB7N6s8hFkFlkBVQ9Um5iifG8zLf 3tTtdQ Ml3xZULqkElvzhw"));
		assertTrue(passwordEncoder.matches(rawPass,"aw1e7LRlhpYCOYAUrunGYFh4X3pNdqEW19GH5KlTuIYIyUb3L2RS/TViFShwlYFV"));
	}
}
