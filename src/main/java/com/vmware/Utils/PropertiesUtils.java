package com.vmware.Utils;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Properties;

public class PropertiesUtils {
	
	String filePath = "";
	
	public PropertiesUtils(String fileName) {
		super();
//		filePath = "src\\main\\resources\\tempResult.properties";
		  ClassLoader classLoader = PropertiesUtils.class.getClassLoader();  
	      URL resource = classLoader.getResource(fileName);  
	      filePath = resource.getPath();
	}

	public String getValueByKey(String key){
		Properties pps = new Properties();

			InputStream in = null;
			try {
				in = new BufferedInputStream (new FileInputStream(filePath));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			try {
				pps.load(in);
			} catch (IOException e) {
				e.printStackTrace();
			}
			String value = pps.getProperty(key);
System.out.print(" ** " + key + " = " + value + " ** ");
			return value;
	}
	
	public void writeValueByKey(String key, String value){
		Properties pps = new Properties();
		try {
			InputStream in = new FileInputStream(filePath);
			pps.load(in);

			OutputStream out = new FileOutputStream(filePath);
			pps.setProperty(key, value);
			pps.store(out, "Update " + key + " name");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}
	
}
