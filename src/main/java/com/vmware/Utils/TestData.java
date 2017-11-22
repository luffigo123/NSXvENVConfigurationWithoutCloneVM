package com.vmware.Utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.HashMap;

public class TestData {
	@SuppressWarnings("unused")
	private String inputLanguage = null;
	private String TestDataFile = null;
	public Collection<String> AllTestData = null;
	public String SuperString = null;
	public String NativeString = null;
	private HashMap<String, String> TestDataMap = null;
	public String dvSwitchName = null;
	public String dvPortGroupName = null;
	public String DatacenterName = null;
	public String Cluster1 = null;
	public String Cluster2 = null;
	public String DataStore1 = null;
	public String PG_Internal01 = null;
	public String PG_Uplink01 = null;
	public String VM1 = null;
	public String VM2 = null;
	public String GroupObject_ipSetName = null;
	public String GroupObject_MacSetName = null;
	public String GroupObject_serivceName = null;
	public String GroupObject_securityGroupsName = null;
	public String GroupObject_serviceGroupName = null;
	public String securityTagName = null;
	public String AppFirewall_sectionName = null;
	public String AppFirewall_firewallName = null;
	public String AppFirewall_saveConfigurationName = null;
	
	public TestData(String inputLanguage){
		this.inputLanguage = inputLanguage;
		this.TestDataFile = System.getProperty("user.dir") + "\\src\\main\\resources\\TestData\\TestData_" + inputLanguage;
		this.TestDataMap = readDataFile();
		this.SuperString = this.TestDataMap.get("SuperString");
		this.NativeString = this.TestDataMap.get("NativeString");
		
		this.dvSwitchName = this.NativeString.substring(0, 1) + this.NativeString;
		this.dvPortGroupName = this.NativeString;
		this.DatacenterName = this.NativeString.substring(0, 1) + this.NativeString +this.NativeString.substring(0, 1);
		this.Cluster1 = this.NativeString;
		this.Cluster2 = this.NativeString + this.NativeString.substring(0, 1);
		this.DataStore1 = this.NativeString;
		this.PG_Internal01 = this.NativeString + this.NativeString.substring(0, 1);
		this.PG_Uplink01 = this.NativeString + this.NativeString.substring(0, 1)+ this.NativeString.substring(0, 1);
		this.VM1 = this.NativeString;
		this.VM2 = this.NativeString + this.NativeString.substring(0, 1);
		
	}
	

	private HashMap<String, String> readDataFile()
	{
		try {
			HashMap<String, String> TestDataMapTemp = new HashMap<String, String>();
			InputStreamReader isr = new InputStreamReader(new FileInputStream(TestDataFile), "UTF-8");
			BufferedReader bufr = new BufferedReader(isr);
			String line = null;
			while((line = bufr.readLine())!=null)
			{
				if(line.length()==0)
					continue;
				String[] sTemp = line.split("=");
				String value = null;
				if (sTemp.length > 1)
				{
					if(!sTemp[1].trim().isEmpty())
						value = sTemp[1].trim();
				}
				TestDataMapTemp.put(sTemp[0].trim(), value);
				
			}
			bufr.close();
			isr.close();
			return TestDataMapTemp;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

}

