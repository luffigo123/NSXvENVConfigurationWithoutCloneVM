package com.vmware.Utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;

import org.apache.log4j.Logger;

import com.vmware.Utils.DefaultEnvironment;
import com.vmware.Utils.LocaleUtils;
import com.vmware.Utils.Log4jInstance;

public class CreateConfigurationFile {
	private Logger log = Log4jInstance.getLoggerInstance();
	private String inputLanguage = null;
	private TestData testData = null;
	private LocaleUtils localeUtils;
	
	public CreateConfigurationFile() {
		super();
		inputLanguage = DefaultEnvironment.inputLanguage;
		testData = new TestData(inputLanguage);
		localeUtils = new LocaleUtils(inputLanguage);
	}
	
	
	public String initAutoEnvConfig(){
		  ClassLoader classLoader = CreateConfigurationFile.class.getClassLoader();  
	      URL resource = classLoader.getResource("AutoEnvConfig.cfg");  
	      String filePath = resource.getPath();
	      return filePath;
	}
	/**
	 * create the configuration file to record the testing information
	 * @param testData
	 * @throws IOException
	 */
	public void createConfigFile(){
		try{
				
			String autoConfigFile = this.initAutoEnvConfig();
			log.info("Start to create Config file for automation code");
			File f = new File(autoConfigFile);
			if(f.exists()){
				f.delete();
			}
	
			FileWriter writer = new FileWriter(autoConfigFile, false);
				
			log.info("Write Base info part 1 ");
			writer.write("Language=" + this.localeUtils.getLanguage() +System.getProperty("line.separator"));
			
			log.info("Write [Input settings] info");
			writer.write("[InputStrings]"+System.getProperty("line.separator"));
			writer.write("InputLanguage=" + DefaultEnvironment.inputLanguage +System.getProperty("line.separator"));
	
			log.info("Write [Environments] info");
			writer.write("[Environments]"+System.getProperty("line.separator"));
			writer.write("VSMIPAddress=" + DefaultEnvironment.vsmIP+ System.getProperty("line.separator"));
			writer.write("VSMUserName=" + DefaultEnvironment.vsmUserName + System.getProperty("line.separator"));
			writer.write("VSMPassword=" + DefaultEnvironment.vsmPasswd + System.getProperty("line.separator"));
			writer.write("VCIP=" + DefaultEnvironment.vcIP + System.getProperty("line.separator"));
			writer.write("VCUserName=" + DefaultEnvironment.vcUserName + System.getProperty("line.separator"));
			writer.write("VCPassword=" + DefaultEnvironment.vcPasswd + System.getProperty("line.separator"));
			writer.write("VCPort=" + "443" + System.getProperty("line.separator"));
			writer.write("dvSwitchName=" + testData.dvSwitchName+System.getProperty("line.separator"));
			writer.write("dvPortGroupName=" + testData.dvPortGroupName +System.getProperty("line.separator"));
			writer.write("DatacenterName=" + testData.DatacenterName + System.getProperty("line.separator"));
			writer.write("Cluster1=" + testData.Cluster1 + System.getProperty("line.separator"));
			writer.write("Cluster2=" + testData.Cluster2 + System.getProperty("line.separator"));
			writer.write("DataStore1=" + testData.DataStore1 + System.getProperty("line.separator")); 
			writer.write("Internal01=" + testData.PG_Internal01 + System.getProperty("line.separator"));
			writer.write("Uplink01=" + testData.PG_Uplink01 + System.getProperty("line.separator"));
//			writer.write("UplinkIP=" + "" + System.getProperty("line.separator"));
			writer.write("Host1InDvSwitch=" + DefaultEnvironment.esxiHost01_IPAddress + System.getProperty("line.separator"));
			writer.write("VM1=" + testData.VM1 + System.getProperty("line.separator"));
			writer.write("VM2=" + testData.VM2 + System.getProperty("line.separator"));
		
			log.info("Write Base info part 2");
//			writer.write("[Domain]"+System.getProperty("line.separator"));
//			writer.write("domainName = nsx8.com"+System.getProperty("line.separator"));
//			writer.write("netBiosName = nsx"+System.getProperty("line.separator"));
//			writer.write("domainUserName = administrator"+System.getProperty("line.separator"));
//			writer.write("domainPassword = ca$hc0wB"+System.getProperty("line.separator"));
//			writer.write("domainHostIP = 10.132.109.204"+System.getProperty("line.separator"));
			
			writer.write("[Add IP Pool]"+ System.getProperty("line.separator"));
			writer.write("IPPoolGateway=10.117.171.253"+ System.getProperty("line.separator"));
			writer.write("IPPoolPrefixLength=22"+System.getProperty("line.separator"));
			writer.write("IPPoolPrimaryDNS=10.117.0.1"+System.getProperty("line.separator"));
			writer.write("IPPoolSecondaryDNS=10.117.0.2"+System.getProperty("line.separator"));
			writer.write("IPPoolDNSSuffix=eng.vmware.com"+System.getProperty("line.separator"));
			writer.write("IPPoolStartIPAddress=10.117.168.253"+System.getProperty("line.separator"));
			writer.write("IPPoolEndIPAddress=10.117.168.254"+System.getProperty("line.separator"));
			
			writer.write("[Edge]"+System.getProperty("line.separator"));
			writer.write("edgeGatewayUplinkIPAddress = 192.168.1.2"+System.getProperty("line.separator"));
			writer.write("edgeGatewayUplinkSubnetMask = 255.255.255.0"+System.getProperty("line.separator"));
			writer.write("edgeGatewayUplinkSubnetPrefixLength = 24"+System.getProperty("line.separator"));
			writer.write("edgeGatewayInternalIPAddress = 192.168.2.2"+System.getProperty("line.separator"));
			writer.write("edgeGatewayInternalSubnetMask = 255.255.255.0"+System.getProperty("line.separator"));
			writer.write("edgeGatewayInternalSubnetPrefixLength = 24"+System.getProperty("line.separator"));
			
			writer.write("logicalRouterManagementIPAddress=1.1.1.2"+ System.getProperty("line.separator"));
			writer.write("logicalRouterManagementSubnetMask=255.255.255.0"+System.getProperty("line.separator"));
			writer.write("logicalRouterManagementSubnetPrefixLength=24"+System.getProperty("line.separator"));
			writer.write("logicalRouterUplinkIPAddress=2.2.2.2"+System.getProperty("line.separator"));
			writer.write("logicalRouterUplinkSubnetMask=255.255.255.0"+System.getProperty("line.separator"));
			writer.write("logicalRouterUplinkSubnetPrefixLength=24"+System.getProperty("line.separator"));
			writer.write("logicalRouterInternalIPAddress=3.3.3.3"+System.getProperty("line.separator"));
			writer.write("logicalRouterInternalSubnetMask=255.255.255.0"+ System.getProperty("line.separator"));
			writer.write("logicalRouterInternalSubnetPrefixLength=24"+System.getProperty("line.separator"));
			
			writer.write("universalLogicalRouterManagementIPAddress=4.4.4.4"+System.getProperty("line.separator"));
			writer.write("universalLogicalRouterManagementSubnetMask=255.255.255.0"+System.getProperty("line.separator"));
			writer.write("universalLogicalRouterManagementSubnetPrefixLength=24"+System.getProperty("line.separator"));
			writer.write("universalLogicalRouterUplinkIPAddress=5.5.5.5"+System.getProperty("line.separator"));
			writer.write("universalLogicalRouterUplinkSubnetMask=255.255.255.0"+System.getProperty("line.separator"));
			writer.write("universalLogicalRouterUplinkSubnetPrefixLength=24"+ System.getProperty("line.separator"));
			writer.write("universalLogicalRouterInternalIPAddress=6.6.6.6"+System.getProperty("line.separator"));
			writer.write("universalLogicalRouterInternalSubnetMask=255.255.255.0"+System.getProperty("line.separator"));
			writer.write("universalLogicalRouterInternalSubnetPrefixLength=24"+System.getProperty("line.separator"));

			writer.close();
			log.info("Config file completed.");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
