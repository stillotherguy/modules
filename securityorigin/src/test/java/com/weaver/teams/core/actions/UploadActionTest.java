package com.weaver.teams.core.actions;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UploadActionTest {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	@Test
	public void test() {
		String filename="test.jpg";
		String sufix = filename.substring(filename.lastIndexOf("."));
		logger.info(sufix);
	}

}
