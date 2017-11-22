package com.vmware.Utils;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.concurrent.Callable;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;



public class GetInfoViaSSHConnection implements Callable<String>{
	private Connection conn = null;
	private String cmd = null;
	
	public GetInfoViaSSHConnection(String cmd, String hostIP,String userName, String passwd) {
		SSHConnection sshc = new SSHConnection(hostIP, userName, passwd);
		this.cmd = cmd + "\n";
		conn = sshc.conn;
	}


	@SuppressWarnings("resource")
	private String exe(){
		String result = "";
		InputStream stdOut = null; 
		try {

			Session session = conn.openSession(); 
			OutputStream out = session.getStdin();
			session.requestDumbPTY();
			session.startShell();
			
			out.write(cmd.getBytes());
			stdOut = new StreamGobbler(session.getStdout()); 

			BufferedReader brs = new BufferedReader(new InputStreamReader(stdOut));
			String linetemp = null;
			
			while((linetemp =brs.readLine()) != null){
				if(linetemp.contains("Fingerprint")){
					result = linetemp;
					break;
				}
			}	
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return result;
	}


	@Override
	public String call(){	
		return this.exe();
	}

}
