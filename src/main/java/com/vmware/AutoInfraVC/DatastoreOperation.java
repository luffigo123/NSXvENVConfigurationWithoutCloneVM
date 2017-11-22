package com.vmware.AutoInfraVC;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;




import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;

import com.vmware.Utils.Log4jInstance;
import com.vmware.vc.FileInfo;
import com.vmware.vc.HostDatastoreBrowserSearchResults;
import com.vmware.vc.HostMountMode;
import com.vmware.vc.HostNasVolumeSpec;
import com.vmware.vc.ManagedObjectReference;
import com.vmware.vcqa.util.SSHUtil;
import com.vmware.vcqa.vim.host.DataStoreBrowser;
import com.vmware.vcqa.vim.host.ServiceSystem;

public class DatastoreOperation {
	private VC vc;
	private String SSHName = "TSM-SSH";
	private Logger log = Log4jInstance.getLoggerInstance();
	public DatastoreOperation(VC vc)
	{
		this.vc = vc;
	}
	
	public void download(String remotePath, String localPath, String destHost, String destHostUsername, String destHostPassword) throws Exception
	{
		log.info("Trying to download file - " + remotePath + ", to local Path - " + localPath);
		ServiceSystem serviceSystem = new ServiceSystem(vc.connectAnchor);
		ManagedObjectReference hostMor_Dest = vc.hostSystem.getHost(destHost);
		ManagedObjectReference serviceSystemMor = serviceSystem.getServiceSystem(hostMor_Dest);
		String sshID = serviceSystem.getServiceID(serviceSystemMor, this.SSHName);
		boolean sshEnabled = serviceSystem.checkServiceStatus(serviceSystemMor, sshID);
		if(!sshEnabled)
		{
			log.info("Enable ssh for ESX - " + destHost);
			serviceSystem.startService(serviceSystemMor, sshID);
		}
		Connection conn = SSHUtil.getSSHConnection(destHost,destHostUsername, destHostPassword);
		SCPClient scpClient = conn.createSCPClient();
		scpClient.get(remotePath, localPath);
		
	}
	/*
	 * dsfilePath - like "[dataStoreName] /filename.txt"
	 */
	public boolean deleteFile(String dsfilePath, String esxHostName, String dataStoreName) throws Exception
	{
		log.info("Trying to delete file - " + dsfilePath + "on esx - " + esxHostName);
		ManagedObjectReference hostMor = vc.hostSystem.getHost(esxHostName);
		ManagedObjectReference datastoreMor = vc.datastore.getDatastore(hostMor, dataStoreName);
		DataStoreBrowser dataStoreBrowser = new DataStoreBrowser(vc.connectAnchor);
		ManagedObjectReference dataStoreBrowserMor = dataStoreBrowser.getDataStoreBrowserFromDataStore(datastoreMor);
		boolean b = dataStoreBrowser.deleteFile(dataStoreBrowserMor, dsfilePath);
		return b;
		
	}
	public boolean fileExist(String fileName, String esxHostName, String dataStoreName) throws Exception
	{
		log.info("checking if the file is existing - " + fileName + " on esx host of " + esxHostName + ", datastore - " + dataStoreName);
			ManagedObjectReference hostMor = vc.hostSystem.getHost(esxHostName);
			ManagedObjectReference datastoreMor = vc.datastore.getDatastore(hostMor, dataStoreName);
			DataStoreBrowser dataStoreBrowser = new DataStoreBrowser(vc.connectAnchor);
			ManagedObjectReference dataStoreBrowserMor = dataStoreBrowser.getDataStoreBrowserFromDataStore(datastoreMor);
			HostDatastoreBrowserSearchResults hdbsr = dataStoreBrowser.search(dataStoreBrowserMor, "[" + dataStoreName + "]", 
					dataStoreBrowser.createSearchSpec(""));
			FileInfo[] fileInfo = com.vmware.vcqa.util.TestUtil.vectorToArray(hdbsr.getFile(), com.vmware.vc.FileInfo.class);
			
			if(fileInfo != null) {
				for(int j=0;j<fileInfo.length;j++){
					FileInfo info = fileInfo[j];
					String path = info.getPath();
					if(fileName.equalsIgnoreCase(path)) {
						log.info("The file <" + fileName + ">is existing on esx host of " + esxHostName + ", datastore - " + dataStoreName);
						return true;
					}
				}
			}
			log.error("The file <" + fileName + ">is existing on esx host of " + esxHostName + ", datastore - " + dataStoreName);
			return false;
	}
	
	public boolean waitForFile(String fileName, String esxHostName, String dataStoreName, int iMinutes) throws Exception
    {
		log.info("waiting for file existing - " + fileName + "on esx host of " + esxHostName + ", datastore - " + dataStoreName + "with in " + iMinutes + "Minutes");

    		for (int i = 0; i < iMinutes * 60; i++) 
    		{
				Thread.sleep(10000);
				boolean fileExist = fileExist(fileName, esxHostName, dataStoreName);
				if (fileExist)
				{
					log.info("**** The File is existing -- " + fileName);
					return true;
				}
				continue;
			}
    		log.error("The file is NOT existing - " + fileName + "on esx host of " + esxHostName + ", datastore - " + dataStoreName + "with in " + iMinutes + "Minutes");
    		return false;
    }	
	public boolean addNFSDatastoreToHost(String nfsDatastoreName, String nfsIP, String nfsPath,
			String hostName, boolean isWritable) throws Exception {
		log.info("Trying to add NFS datastore to host - " + hostName + ", nfsDatastoreName is - " + 
			nfsDatastoreName + ", nfsServerIP is " + nfsIP + ", nfsPath is " + nfsPath);
		ManagedObjectReference hostMor = vc.hostSystem.getHost(hostName);

		return addNFS41DatastoreToHost(nfsDatastoreName, nfsIP, nfsPath, hostMor, isWritable, null);
	}
	private boolean addNFS41DatastoreToHost(String nfsDatastoreName, String nfsIP, String nfsPath,
			ManagedObjectReference hostMor, boolean isWritable, String type) throws Exception{
		String accessMode = isWritable ? HostMountMode.READ_WRITE.value():
			HostMountMode.READ_ONLY.value();
		HostNasVolumeSpec hostNasVolumeSpec = new HostNasVolumeSpec();
		hostNasVolumeSpec.setRemoteHost(nfsIP);
		List<String> hostList= new ArrayList<String>();
		hostList.add(nfsIP);
		hostNasVolumeSpec.setRemoteHostNames(hostList);	// NFS41
		hostNasVolumeSpec.setRemotePath(nfsPath);
		hostNasVolumeSpec.setLocalPath(nfsDatastoreName);
		hostNasVolumeSpec.setAccessMode(accessMode);
		if (type!=null) {
			hostNasVolumeSpec.setType(type);
		}

		ManagedObjectReference datastoreSystemMor = vc.datastoreSystem.getDatastoreSystem(hostMor);
		ManagedObjectReference nfsMor = vc.datastoreSystem.createNasDatastore(datastoreSystemMor, hostNasVolumeSpec);
		if (nfsMor == null)
		{
			log.error("Failed to Add NFS storage.");
			return false;
		}
		log.info("Successfully to add the NFS storage.");
		return true;
	}
	
	
}
