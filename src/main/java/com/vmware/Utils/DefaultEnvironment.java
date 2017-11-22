package com.vmware.Utils;

public class DefaultEnvironment {
	private static Config cfg = Config.getInstance();

	public static final String inputLanguage = cfg.ConfigMap.get("inputLanguage");
	
	//vc info
	public static final String vcIP = cfg.ConfigMap.get("vcIP");
//	public static final String vcPort = cfg.ConfigMap.get("vcPort");
	public static final String vcUserName = cfg.ConfigMap.get("vcUserName"); 
	public static final String vcPasswd = cfg.ConfigMap.get("vcPasswd");
	public static final String vcRootName = cfg.ConfigMap.get("vcRootName"); 
	public static final String vcRootPassword = cfg.ConfigMap.get("vcRootPassword");

	//VSM info
	public static final String vsmIP = cfg.ConfigMap.get("vsmIP");
	public static final String vsmUserName = cfg.ConfigMap.get("vsmUserName"); 
	public static final String vsmPasswd = cfg.ConfigMap.get("vsmPasswd");
	
	//helper esxi
//	public static final String hostForCloneVM = cfg.ConfigMap.get("hostForCloneVM");
//	public static final String helperESXiUsername = cfg.ConfigMap.get("helperESXiUsername");
//	public static final String helperESXiPasswd = cfg.ConfigMap.get("helperESXiPasswd");
//	public static final String helperVMname = cfg.ConfigMap.get("helperVMname"); 
	
	//ESXi hosts info
	public static final String esxiHost01_IPAddress = cfg.ConfigMap.get("esxi01IP");
	public static final String esxiHost02_IPAddress = cfg.ConfigMap.get("esxi02IP");
	public static final String esxiUserName = cfg.ConfigMap.get("esxiUserName");
	public static final String esxiPassword = cfg.ConfigMap.get("esxiPassword");
	
	//For Edge
//	public static final String UplinkIP = cfg.ConfigMap.get("UplinkIP");
	
	public static final String nsxLicenseKey = cfg.ConfigMap.get("nsxLicenseKey");
	
	public static final String vmName = cfg.ConfigMap.get("vmName");
	public static final String nsxVersion = cfg.ConfigMap.get("nsxVersion");
	
}
