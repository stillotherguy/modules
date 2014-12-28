package com.weaver.teams.core.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

/** 
* 
* @author KangJellen
* @version 2014-5-7
*/

public class StorageServiceTest {

	private StorageService storageService = new StorageServiceImpl();
	
	@Test
	public void testSaveFile(){
		String fileName = "text.jpg";
		File file = new File("d:/text.jpg");
		try {
			storageService.saveFile(file, fileName, file.length());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void testGetFile(){
		String fileUrl = "01f63e4f3e5a403d9cf5e8769bbc6218";
		try {
			storageService.getFile(fileUrl);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
