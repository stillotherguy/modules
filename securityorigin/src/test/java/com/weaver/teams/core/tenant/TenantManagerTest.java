package com.weaver.teams.core.tenant;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import com.weaver.teams.api.tenant.Tenant;
import com.weaver.teams.api.tenant.TenantManager;

@ContextConfiguration(locations = { "classpath:applicationContextTest-core.xml" })
public class TenantManagerTest extends AbstractJUnit4SpringContextTests{

	@Autowired
	private TenantManager tenantManager;

	@Test
	public void testLoadTenant() {
		Tenant tenant = tenantManager.loadTenant("TEST10");
		logger.info(tenant.getTenantName());
		assertNotNull(tenant.getTenantName());
	}

}
