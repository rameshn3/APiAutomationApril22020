package io.restassured.base;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class TestBase {
public static Properties prop;
public TestBase() throws IOException {
	try {
		//create object for properties class
		prop=new Properties();
		//read the config.propertis file
		FileInputStream fis=new FileInputStream("src/main/java/io/restassured/config/config.properties");
		prop.load(fis);
	} catch (FileNotFoundException e) {
		
		e.printStackTrace();
	}
}
	
	
}
