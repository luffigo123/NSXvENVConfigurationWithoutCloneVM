package com.vmware.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "vcInfo")
public class VCinfo {
	private String ipAddress;
	private String userName;
	private String password;
	private String certificateThumbprint;
	private String assignRoleToUser;
	private String pluginDownloadServer;
	private String pluginDownloadPort;
	
	@XmlElement(name = "ipAddress")
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	
	@XmlElement(name = "userName")
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	@XmlElement(name = "password")
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	@XmlElement(name = "certificateThumbprint")
	public String getCertificateThumbprint() {
		return certificateThumbprint;
	}
	public void setCertificateThumbprint(String certificateThumbprint) {
		this.certificateThumbprint = certificateThumbprint;
	}
	
	@XmlElement(name = "assignRoleToUser")
	public String getAssignRoleToUser() {
		return assignRoleToUser;
	}
	public void setAssignRoleToUser(String assignRoleToUser) {
		this.assignRoleToUser = assignRoleToUser;
	}
	
	@XmlElement(name = "pluginDownloadServer")
	public String getPluginDownloadServer() {
		return pluginDownloadServer;
	}
	public void setPluginDownloadServer(String pluginDownloadServer) {
		this.pluginDownloadServer = pluginDownloadServer;
	}
	
	@XmlElement(name = "pluginDownloadPort")
	public String getPluginDownloadPort() {
		return pluginDownloadPort;
	}
	public void setPluginDownloadPort(String pluginDownloadPort) {
		this.pluginDownloadPort = pluginDownloadPort;
	}
	
	public VCinfo() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 
	 * @param ipAddress
	 * @param userName
	 * @param password
	 * @param certificateThumbprint
	 * @param assignRoleToUser
	 */
	public VCinfo(String ipAddress, String userName, String password, String certificateThumbprint,
			String assignRoleToUser) {
		super();
		this.ipAddress = ipAddress;
		this.userName = userName;
		this.password = password;
		this.certificateThumbprint = certificateThumbprint;
		this.assignRoleToUser = assignRoleToUser;
	}
	
	/**
	 * 
	 * @param ipAddress
	 * @param userName
	 * @param password
	 * @param certificateThumbprint
	 * @param assignRoleToUser
	 * @param pluginDownloadServer
	 * @param pluginDownloadPort
	 */
	public VCinfo(String ipAddress, String userName, String password, String certificateThumbprint,
			String assignRoleToUser, String pluginDownloadServer, String pluginDownloadPort) {
		super();
		this.ipAddress = ipAddress;
		this.userName = userName;
		this.password = password;
		this.certificateThumbprint = certificateThumbprint;
		this.assignRoleToUser = assignRoleToUser;
		this.pluginDownloadServer = pluginDownloadServer;
		this.pluginDownloadPort = pluginDownloadPort;
	}
		
}
