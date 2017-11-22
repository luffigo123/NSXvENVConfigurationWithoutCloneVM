package com.vmware.AutoInfraVC;

import java.awt.HeadlessException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.log4j.Logger;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

import com.vmware.Utils.Log4jInstance;
import com.vmware.vcqa.util.SSHUtil;

public class VCSSHConnnection {
	private String hostName = null;
	private String userName = null;
	private String pwd = null;
	private Session ssh = null;
	private Connection conn = null;
	private Logger log = Log4jInstance.getLoggerInstance();
	
	public VCSSHConnnection(String hostName, String userName, String pwd) throws IOException
	{
		try {
			this.hostName = hostName;
			this.userName = userName;
			this.pwd = pwd;
			this.conn = SSHUtil.getSSHConnection(this.hostName,this.userName, this.pwd);

		} catch (HeadlessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public String ExecuteCmd(String cmd) throws Exception
	{
		String result = null;
		SSHUtil.executeRemoteSSHCommand(this.conn, cmd);
		return "";
		
	}
	public String ExecuteCmd(String cmd, int timeOutInMinutes) throws Exception
	{
		String result = null;
		long maxTimeout = timeOutInMinutes * 60;
		SSHUtil.executeRemoteSSHCommand(this.conn, cmd, maxTimeout);
		return "";
		
	}
	public String ExecuteCmd2(String cmd) throws Exception
	{
			String result = null;
			SSHUtil.executeRemoteSSHCommand(this.conn, cmd);
			InputStream is = new StreamGobbler(this.ssh.getStdout());
			BufferedReader brs = new BufferedReader(new InputStreamReader(is));
			while(true){  
				result = brs.readLine();  
	            if(result==null){  
	                break;  
	            }  
	            return result;  
	        }
			return "";
	}
	public void close() throws Exception
	{
		SSHUtil.closeSSHConnection(this.conn);
	}
}
