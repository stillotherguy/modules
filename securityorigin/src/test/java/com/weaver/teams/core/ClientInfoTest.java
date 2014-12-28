package com.weaver.teams.core;

import static org.junit.Assert.*;

import org.junit.Test;

import com.weaver.teams.api.base.Client;
import com.weaver.teams.api.base.ClientInfo;

public class ClientInfoTest {

	@Test
	public void testIsMobile() {
		Client client = Client.ipad;
		ClientInfo info = new ClientInfo(client,"1.0");
		assertTrue(info.isMobile());
		
		Client client1 = Client.pc;
		ClientInfo info1 = new ClientInfo(client1,"1.0");
		assertFalse(info1.isMobile());
		
		Client client2 = Client.iphone;
		ClientInfo info2 = new ClientInfo(client2,"1.0");
		assertTrue(info2.isMobile());
	}

}
