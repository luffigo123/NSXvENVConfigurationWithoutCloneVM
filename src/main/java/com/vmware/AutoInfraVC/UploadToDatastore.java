package com.vmware.AutoInfraVC;

import org.apache.log4j.Logger;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;

import com.vmware.Utils.Log4jInstance;
import com.vmware.vc.ManagedObjectReference;
import com.vmware.vcqa.util.SSHUtil;
import com.vmware.vcqa.vim.Datastore;
import com.vmware.vcqa.vim.host.ServiceSystem;

public class UploadToDatastore {
	private VC vc;
	private String SSHName = "TSM-SSH";
	private Logger log = Log4jInstance.getLoggerInstance();
	
	public UploadToDatastore(VC vc)
	{
		this.vc = vc;
	}
	
	@SuppressWarnings("null")
	public void Upload(String localFile, String destHost, String destHostUsername, String destHostPassword, String destDatastoreName) throws Exception {
		// Check if the SSH service is enabled on dest host
		log.info("Trying to upload localFile (" + localFile + ")" + " to host (" + destHost + ")" + " to datastore of - " + destDatastoreName);
		ManagedObjectReference datastoreMor = null;
		Datastore datastore = null;
		try {
			ServiceSystem serviceSystem = new ServiceSystem(vc.connectAnchor);
			ManagedObjectReference hostMor_Dest = vc.hostSystem.getHost(destHost);
			datastoreMor = vc.datastore.getDatastore(hostMor_Dest, destDatastoreName);
			ManagedObjectReference serviceSystemMor = serviceSystem.getServiceSystem(hostMor_Dest);
			String sshID = serviceSystem.getServiceID(serviceSystemMor, this.SSHName);
			boolean sshEnabled = serviceSystem.checkServiceStatus(serviceSystemMor, sshID);
			if(!sshEnabled)
				serviceSystem.startService(serviceSystemMor, sshID);
		} catch (Exception e) {
			log.error("Failed to upload file to esx host's datastore - " + e.getMessage());
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Check if the file is on dest Host, if not, upload it.
		DatastoreOperation dsOps = new DatastoreOperation(vc); 
		String fileName = localFile.substring(localFile.lastIndexOf("\\") + 1, localFile.length()).replace("%20", " ");
		boolean fileExistOnIntermediary = dsOps.fileExist(fileName, destHost, destDatastoreName);
		// upload to intermediary Host at first.
		if(!fileExistOnIntermediary)
		{
			//InternalAgentManager tam = new InternalAgentManager(vc.connectAnchor);
			
				Connection conn = SSHUtil.getSSHConnection(destHost,destHostUsername, destHostPassword);
				SCPClient scpClient = conn.createSCPClient();
				String originalDatastoreName = null;
				if(destDatastoreName.indexOf("(") >=0 )
				{
					originalDatastoreName = destDatastoreName;
					
					destDatastoreName = destDatastoreName.replace("(", "");
					destDatastoreName = destDatastoreName.replace(")", "");
					destDatastoreName = destDatastoreName.replace(" ", "");
					datastore.renameDatastore(datastoreMor, destDatastoreName);
				}
				scpClient.put(localFile, "//vmfs//volumes//" + destDatastoreName);
				log.info("File " + localFile + " has been uploaded to the intermediary host " + destDatastoreName);
				if(originalDatastoreName != null)
				{
					datastore.renameDatastore(datastoreMor, originalDatastoreName);
					boolean renameSuccessful = new HostUtils(vc).WaitForDatastoreName(vc.connectAnchor, datastoreMor, originalDatastoreName, 5);
					if (renameSuccessful != true)
						 throw new Exception();
				}
			
		}
		
	}
	@SuppressWarnings("unused")
	public void UploadToPath(String localFile, String destHost, String destHostUsername, String destHostPassword, String destDatastoreName, String vmFolder) throws Exception {
		// Check if the SSH service is enabled on dest host
		ManagedObjectReference datastoreMor = null;
			ServiceSystem serviceSystem = new ServiceSystem(vc.connectAnchor);
			ManagedObjectReference hostMor_Dest = vc.hostSystem.getHost(destHost);
			datastoreMor = vc.datastore.getDatastore(hostMor_Dest, destDatastoreName);
			ManagedObjectReference serviceSystemMor = serviceSystem.getServiceSystem(hostMor_Dest);
			String sshID = serviceSystem.getServiceID(serviceSystemMor, this.SSHName);
			boolean sshEnabled = serviceSystem.checkServiceStatus(serviceSystemMor, sshID);
			if(!sshEnabled)
				serviceSystem.startService(serviceSystemMor, sshID);

		// upload it.
		DatastoreOperation dsOps = new DatastoreOperation(vc); 
		String fileName = localFile.substring(localFile.lastIndexOf("\\") + 1, localFile.length()).replace("%20", " ");
				Connection conn = SSHUtil.getSSHConnection(destHost,destHostUsername, destHostPassword);
				SCPClient scpClient = conn.createSCPClient();

				scpClient.put(localFile, "//vmfs//volumes//" + destDatastoreName + "//" + vmFolder);
				log.info("File " + localFile + " has been uploaded to the intermediary host " + destDatastoreName);
			
		
	}
}
