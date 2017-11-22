package com.vmware.Utils;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.vmware.Utils.DefaultEnvironment;
import com.vmware.Utils.Log4jInstance;

import ch.ethz.ssh2.Connection;

public class SSHConnection{

		public Connection conn = null;
		
//		private String hostName = DefaultEnvironment.nimbusServer;
//		private String domainUsername = DefaultEnvironment.domainUserName;
//		private String domainPassword = DefaultEnvironment.domainPassword;
		
		private Logger log = Log4jInstance.getLoggerInstance();
		
		boolean authenticationStatus = false;
		
//		public SSHConnection(String hostName)
//		{
//			this.conn = new Connection(hostName);
//			try {
//				conn.connect();
//				authenticationStatus = conn.authenticateWithPassword(domainUsername, domainPassword);
//				if(authenticationStatus){
//					log.info("Connect to " + hostName +" successfully");
//				}else{
//					log.error("failed to connect: " + hostName);
//				}
//					
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
		
		/**
		 * 
		 * @param hostIP
		 * @param userName
		 * @param password
		 */
		public SSHConnection(String hostIP, String userName, String password)
		{
			this.conn = new Connection(hostIP);
			try {
				conn.connect();
				authenticationStatus = conn.authenticateWithPassword(userName, password);
				if(authenticationStatus){
					log.info("Connect to " + hostIP +" successfully");
				}else{
					log.error("failed to connect: " + hostIP);
				}
					
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
}
