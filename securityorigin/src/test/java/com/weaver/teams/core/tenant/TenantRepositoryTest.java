package com.weaver.teams.core.tenant;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import com.weaver.teams.api.tenant.Tenant;

@ContextConfiguration(locations = { "classpath:applicationContextTest-core.xml" })
public class TenantRepositoryTest {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private TenantRepository tenantRepository;

	@Test
	public void testGetTenant() throws IOException {
		String key = "TEST10";
		Tenant tenant = tenantRepository.getTenant(key);
		logger.info(tenant.getTenantName());
		assertTrue(tenant.getTenantKey().equals(key));

		assertNull(tenantRepository.getTenant(null));
		assertNull(tenantRepository.getTenant("null"));
		assertNull(tenantRepository.getTenant("testaaabbbcc"));
	}

}
