package com.weaver.teams.security.config;


public class SecurityConfig{
	
	//private static Properties properties=new Properties();
	
	public static boolean enableCaptcha(){
		return true;
		//return Boolean.valueOf(properties.getProperty("security.enableCaptcha"));
	}
	public static boolean enableUSB(){
		return false;
		//return Boolean.valueOf(properties.getProperty("security.enableUSB"));
	}
	public static boolean enableDynamicpass(){
		return false;
		//return Boolean.valueOf(properties.getProperty("security.enableDynamicpass"));
	}
	public static boolean enableRemember(){
		return false;
		//return Boolean.valueOf(properties.getProperty("security.enableRemember"));
	}

}
