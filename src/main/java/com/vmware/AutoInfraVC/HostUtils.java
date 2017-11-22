package com.vmware.AutoInfraVC;

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.vmware.Utils.Log4jInstance;
import com.vmware.vc.HostConnectSpec;
import com.vmware.vc.HostInternetScsiHba;
import com.vmware.vc.HostInternetScsiHbaSendTarget;
import com.vmware.vc.HostInternetScsiHbaStaticTarget;
import com.vmware.vc.HostIpConfig;
import com.vmware.vc.HostNetworkPolicy;
import com.vmware.vc.HostNetworkSecurityPolicy;
import com.vmware.vc.HostPortGroupSpec;
import com.vmware.vc.HostStorageDeviceInfo;
import com.vmware.vc.HostSystemConnectionState;
import com.vmware.vc.HostSystemReconnectSpec;
import com.vmware.vc.HostVirtualNic;
import com.vmware.vc.HostVirtualNicManagerNicType;
import com.vmware.vc.HostVirtualNicSpec;
import com.vmware.vc.HostVirtualSwitchConfig;
import com.vmware.vc.HostVirtualSwitchSpec;
import com.vmware.vc.ManagedObjectReference;
import com.vmware.vcqa.ConnectAnchor;
import com.vmware.vcqa.vim.host.DatastoreInformation;
import com.vmware.vcqa.vim.host.VirtualNicManager;

public class HostUtils{
	private VC vc;
	private Logger log = Log4jInstance.getLoggerInstance();
	public HostUtils(VC vc)
	{
		this.vc = vc;
	}
	public boolean enableVswitchPromiscuousMode(String hostName, String switchName) throws Exception {
		log.info("Trying to enable PromiscuousMode for Vswitch of " + switchName + " on host - " + hostName);
        ManagedObjectReference hostMor = this.vc.hostSystem.getHost(hostName);
        ManagedObjectReference nwSystemMor = this.vc.networkSystem.getNetworkSystem(hostMor);
        HostVirtualSwitchSpec hostVirtualSwitchSpec = vc.networkSystem.getVirtualSwitchSpec(nwSystemMor, switchName);
        HostNetworkPolicy hostNetworkPlicy = hostVirtualSwitchSpec.getPolicy();
        HostNetworkSecurityPolicy hostNetworkSecurityPolicy = hostNetworkPlicy.getSecurity();
        hostNetworkSecurityPolicy.setAllowPromiscuous(true);
        
        return vc.networkSystem.updateVirtualSwitch(nwSystemMor, switchName, hostVirtualSwitchSpec);           
    }
	public boolean addPortGroup(String hostName, String switchName, String portGroupName) throws Exception
	{
		log.info("Trying to add PortGroup for Vswitch of " + switchName + " on host - " + hostName + ", portgroup name is " + portGroupName);
		ManagedObjectReference hostMor = this.vc.hostSystem.getHost(hostName);
        ManagedObjectReference nwSystemMor = this.vc.networkSystem.getNetworkSystem(hostMor);
        HostPortGroupSpec pgSpec = this.vc.networkSystem.createPortGroupSpec(portGroupName);
        pgSpec.setVswitchName(switchName);
        return this.vc.networkSystem.addPortGroup(nwSystemMor, pgSpec);
	}
	public boolean removePortGroup(String hostName, String portGroupName) throws Exception
	{
		log.info("Trying to remove PortGroup (" + portGroupName + ") on host of " + hostName);
		ManagedObjectReference hostMor = this.vc.hostSystem.getHost(hostName);
        ManagedObjectReference nwSystemMor = this.vc.networkSystem.getNetworkSystem(hostMor);
        return this.vc.networkSystem.removePortGroup(nwSystemMor, portGroupName);
	}

	public ManagedObjectReference getLargestFreeDatastoreMor(String esxHostName) throws Exception
	{
		log.info("Trying to get the Largest Free Datastore Mor on host of " + esxHostName);
		ManagedObjectReference hostMor = vc.hostSystem.getHost(esxHostName);
		List<ManagedObjectReference> datastore1MorList = vc.datastore.getDatastores(hostMor);

		long FreeSpace = 0;
		int recomendedDataStoreIndex = 0;
		for (int i=0; i<datastore1MorList.size();i++)
		{
			DatastoreInformation di = vc.datastore.getDatastoreInfo(datastore1MorList.get(i));
			if (di.getFreespace() >= FreeSpace)
			{
				FreeSpace = di.getFreespace();
				recomendedDataStoreIndex = i;
			}
		}
		return datastore1MorList.get(recomendedDataStoreIndex);
	}
	public String getLargestFreeDatastoreName(String esxHostName) throws Exception
	{
		log.info("Trying to get the Largest Free Datastore Name on host of " + esxHostName);
		ManagedObjectReference dsMor = this.getLargestFreeDatastoreMor(esxHostName);
		return this.getDatastoreName(dsMor);
	}
	public String getDatastoreName(ManagedObjectReference DatastoreMor) throws Exception
	{
		log.info("Trying to get the Datastore name by the Mor.");
		DatastoreInformation datastoreInformation = vc.datastore.getDatastoreInfo(DatastoreMor);
		return datastoreInformation.getName();
	}
	public boolean WaitForDatastoreName(ConnectAnchor connectAnchor, ManagedObjectReference DatastoreMor, String DatastoreName, int iMinutes) throws Exception
	{
		try {
    		for (int i = 0; i < iMinutes * 60; i++) 
    		{
				Thread.sleep(1000);
				String currentDataStoreName = this.getDatastoreName(DatastoreMor);
				if (currentDataStoreName == null)
					continue;
				if (currentDataStoreName.equalsIgnoreCase(DatastoreName))
				{
					log.info("**** Datastore Name is come out -- " + currentDataStoreName);
					return true;
				}
				continue;
			}
    		log.error("Failed to waitfor: Datastore Name can't be found, timeout is " + iMinutes + " minutes");
    		return false;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("Failed WaitForDatastoreName() - " + e.getMessage());
			e.printStackTrace();
			return false;
		}
		
	}

	/**
	 * add host with some resource like VM with guest OS to test datacenter
	 * @return add successfully
	 * @throws Exception
	 */
	public ManagedObjectReference addStandaloneHost2(String hostName, String hostUsername, String hostPWD, String datacenterName) throws Exception {
		ManagedObjectReference hostMor = vc.hostSystem.getHost(hostName);
		log.info("Trying to add host (" + hostName + ") to Datacenter of - " + datacenterName);
		// if the host is added
		if (hostMor != null)
		{
			log.info("The host has been added.");
			// Check if the host is disconnected
			HostSystemConnectionState hscs = vc.hostSystem.getHostState(hostMor);
			if(hscs.value().equalsIgnoreCase("disconnected"))
			{
				log.info("The host has been added, but it is in disconnected status, reconnect it.");
				// Re-connect the host
				HostConnectSpec hostCnxSpec = new HostConnectSpec();
				hostCnxSpec.setHostName(hostName);
				hostCnxSpec.setUserName(hostUsername);
				hostCnxSpec.setPassword(hostPWD);
				hostCnxSpec.setPort(null);
				hostCnxSpec.setForce(true);
				HostSystemReconnectSpec hsRSec = new HostSystemReconnectSpec();
				hsRSec.setSyncState(true);
				vc.hostSystem.reconnectHost(hostMor, hostCnxSpec, hsRSec);
			}
			// check whether the host is in the given datacenter or not
			if(!isExistsInDatacenter(hostName, datacenterName))
			{
				log.info("The host has been added, but it is not in the datacenter of " + datacenterName + ", move it to the datacenter.");
				vc.hostSystem.moveHost(vc.folder.getDataCenter(datacenterName), hostMor);
			}
			return vc.hostSystem.getHost(hostName);
		}
		// if the host is not added
		log.info("The host is not added, just add it.");
		ManagedObjectReference dcMor = vc.folder.getDataCenter(datacenterName);
		ManagedObjectReference folderMor = vc.datacenter.getHostFolder(dcMor);
		
		HostConnectSpec hostCnxSpec = new HostConnectSpec();
		hostCnxSpec.setHostName(hostName);
		hostCnxSpec.setUserName(hostUsername);
		hostCnxSpec.setPassword(hostPWD);
		hostCnxSpec.setPort(null);
		hostCnxSpec.setForce(true);
		
		hostMor = vc.hostSystem.addStandaloneHost(folderMor, hostCnxSpec, null, true);

		Thread.sleep(10000);
		return hostMor;
	}
    
	public Boolean addStandaloneHost(String hostName, String hostUsername, String hostPWD, String datacenterName) throws Exception {
		ManagedObjectReference hostMor = this.addStandaloneHost2(hostName, hostUsername, hostPWD, datacenterName);
		log.info("Trying to add host (" + hostName + ") to Datacenter of - " + datacenterName);
		if (hostMor != null)
		{
			log.info("Successfully added host (" + hostName + ") to Datacenter of - " + datacenterName);
			return true;
		}
		log.error("Failed to add host (" + hostName + ") to Datacenter of - " + datacenterName);
		return false;
	}
	public ManagedObjectReference reConnectHost2(String hostName, String hostUsername, String hostPWD, String datacenterName) throws Exception {
		ManagedObjectReference hostMor = vc.hostSystem.getHost(hostName);
		if (hostMor == null)
			return null;
		// Check if the host is disconnected
		HostSystemConnectionState hscs = vc.hostSystem.getHostState(hostMor);
		if(hscs.value().equalsIgnoreCase("disconnected"))
		{
			// Re-connect the host
			HostConnectSpec hostCnxSpec = new HostConnectSpec();
			hostCnxSpec.setHostName(hostName);
			hostCnxSpec.setUserName(hostUsername);
			hostCnxSpec.setPassword(hostPWD);
			hostCnxSpec.setPort(null);
			hostCnxSpec.setForce(true);
			HostSystemReconnectSpec hsRSec = new HostSystemReconnectSpec();
			hsRSec.setSyncState(true);
			vc.hostSystem.reconnectHost(hostMor, hostCnxSpec, hsRSec);
		}
		return vc.hostSystem.getHost(hostName);
	}
	public Boolean reConnectHost(String hostName, String hostUsername, String hostPWD, String datacenterName) throws Exception {
		ManagedObjectReference hostMor = this.reConnectHost2(hostName, hostUsername, hostPWD, datacenterName);
		if (hostMor != null)
			return true;
		return false;
	}
	public boolean isExistsInVC(String hostName) throws Exception {
		return vc.hostSystem.getHost(hostName)!=null;
	}
	
	public boolean isExistsInDatacenter(ManagedObjectReference hostMor,
			ManagedObjectReference dcMor) throws Exception {
		return vc.hostSystem.getDataCenter(hostMor).equals(dcMor);
	}

	public boolean isExistsInDatacenter(String hostName, String dcName) throws Exception {
		if (!this.isExistsInVC(hostName)) {
			return false;
		}
		ManagedObjectReference dcMor = vc.folder.getDataCenter(dcName);
		if (dcMor==null) {
			log.error("Failed: Datacenter - " + dcName + " is not exist.");
			throw new Exception("Datacenter does not exist");
		}
		ManagedObjectReference hostMor = vc.hostSystem.getHost(hostName);
		return isExistsInDatacenter(hostMor, dcMor);
	}
	
	
	public boolean LeaveMaintenanceMode(ManagedObjectReference hostMor) throws Exception {
		
		int timeOut = com.vmware.vcqa.TestConstants.EXIT_MAINTMODE_DEFAULT_TIMEOUT_SECS;
		if (vc.hostSystem.isHostInMaintenanceMode(hostMor)) {
			return vc.hostSystem.exitMaintenanceMode(hostMor, timeOut);
		}
		return true;
	}
	public boolean LeaveMaintenanceMode2(String hostName) throws Exception {
		log.info("Trying to make ESX host " + hostName + " leave from MaintenanceMode");
		ManagedObjectReference hostMor = vc.hostSystem.getHost(hostName);
		return this.LeaveMaintenanceMode(hostMor);
	}
	public boolean EnterMaintenanceMode(ManagedObjectReference hostMor) throws Exception {
		int timeOut = com.vmware.vcqa.TestConstants.ENTERMAINTENANCEMODE_TIMEOUT;
		if (vc.hostSystem.isHostInMaintenanceMode(hostMor)) {
			return vc.hostSystem.exitMaintenanceMode(hostMor, timeOut);
		}
		return true;
	}
	public boolean EnterMaintenanceMode(String hostName) throws Exception {
		log.info("Trying to put ESX host " + hostName + " into MaintenanceMode");
		ManagedObjectReference hostMor = vc.hostSystem.getHost(hostName);
		return EnterMaintenanceMode(hostMor);
	}
	public boolean addIScsiAdapter(ManagedObjectReference hostMor) throws Exception {
		ManagedObjectReference storageSystemMor = vc.storageSystem.getStorageSystem(hostMor);
		return vc.storageSystem.updateSoftwareInternetScsiEnabled(storageSystemMor, true);
	}
	public boolean addIScsiAdapter2(String hostName) throws Exception {
		log.info("Trying to add IScsi adapter to ESX host " + hostName);
		ManagedObjectReference hostMor = vc.hostSystem.getHost(hostName);
		return addIScsiAdapter(hostMor);
	}
	// rebootHost: true, reboot host after remove iScsi adapter to make it work; false, not reboot
	public boolean removeIScsiAdapter(ManagedObjectReference hostMor, String hostUserName, 
										String hostPWD, boolean rebootHost) throws Exception 
	{
		ManagedObjectReference storageSystemMor = vc.storageSystem.getStorageSystem(hostMor);
		boolean isSuccess = vc.storageSystem.updateSoftwareInternetScsiEnabled(storageSystemMor, false);
		if (!isSuccess) {
			return false;
		}
		
		if (rebootHost) {
			HashMap<ManagedObjectReference, ConnectAnchor> returnHostMap = vc.hostSystem.rebootHost(hostMor, 
					Integer.parseInt(vc.VCPort), true, hostUserName, hostPWD);
			if (!returnHostMap.containsKey(hostMor)) {
				return false;
			}
		}
		
		return true;
	}
	public boolean removeIScsiAdapter2(String hostName, String hostUserName, 
			String hostPWD, boolean rebootHost) throws Exception{
		log.info("Trying to remove IScsi adapter to ESX host " + hostName);
		ManagedObjectReference hostMor = vc.hostSystem.getHost(hostName);
		return removeIScsiAdapter(hostMor, hostUserName, hostPWD, rebootHost);
	}
	@SuppressWarnings("deprecation")
	public boolean addISCSISendTargetServer(ManagedObjectReference hostMor, String IScsi_Server_IP) throws Exception {
		ManagedObjectReference storageSystemMor = vc.storageSystem.getStorageSystem(hostMor);
		HostInternetScsiHbaSendTarget[] sendTargets = new HostInternetScsiHbaSendTarget[1];
		sendTargets[0] = new HostInternetScsiHbaSendTarget();
		sendTargets[0].setAddress(IScsi_Server_IP);
		sendTargets[0].setPort(new Integer(com.vmware.vcqa.TestConstants.HOST_INTERNET_SCSI_HBA_DEFAULT_PORT));
		HostInternetScsiHba iScsiHba = vc.storageSystem.getiScsiHba(storageSystemMor, 
				com.vmware.vcqa.TestConstants.ADAPTER_TYPE_SOFTWARE); // only one iScsi adapter
		String iScsiHbaID = iScsiHba.getDevice();
		return vc.storageSystem.addInternetScsiSendTargets(storageSystemMor, iScsiHbaID, sendTargets);
	}
	public boolean addISCSISendTargetServer2(String hostName, String IScsi_Server_IP) throws Exception {
		ManagedObjectReference hostMor = vc.hostSystem.getHost(hostName);
		return addISCSISendTargetServer(hostMor, IScsi_Server_IP);
	}
	@SuppressWarnings("deprecation")
	public boolean removeISCSISendTargetServer(String hostName, String IScsi_Server_IP) throws Exception {
		ManagedObjectReference hostMor = vc.hostSystem.getHost(hostName);
		ManagedObjectReference storageSystemMor = vc.storageSystem.getStorageSystem(hostMor);
		HostInternetScsiHbaSendTarget[] sendTargets = new HostInternetScsiHbaSendTarget[1];
		sendTargets[0] = new HostInternetScsiHbaSendTarget();
		sendTargets[0].setAddress(IScsi_Server_IP);
		sendTargets[0].setPort(new Integer(com.vmware.vcqa.TestConstants.HOST_INTERNET_SCSI_HBA_DEFAULT_PORT));
		HostInternetScsiHba iScsiHba = vc.storageSystem.getiScsiHba(storageSystemMor, 
				com.vmware.vcqa.TestConstants.ADAPTER_TYPE_SOFTWARE); // only one iScsi adapter
		String iScsiHbaID = iScsiHba.getDevice();
		return vc.storageSystem.removeInternetScsiSendTargets(storageSystemMor, iScsiHbaID, sendTargets);
	}
	
	@SuppressWarnings("deprecation")
	public boolean removeISCSIStaticTargetServer(String hostName, String IScsi_Server_IP) throws Exception {
		ManagedObjectReference hostMor = vc.hostSystem.getHost(hostName);
		ManagedObjectReference storageSystemMor = vc.storageSystem.getStorageSystem(hostMor);
		HostInternetScsiHbaStaticTarget[] staticTatgets = new HostInternetScsiHbaStaticTarget[1];
		staticTatgets[0] = new HostInternetScsiHbaStaticTarget();
		staticTatgets[0].setAddress(IScsi_Server_IP);
		staticTatgets[0].setPort(new Integer(com.vmware.vcqa.TestConstants.HOST_INTERNET_SCSI_HBA_DEFAULT_PORT));
		staticTatgets[0].setIScsiName("iqn.2007-05.com.vvol:storage.iscsi-scst-3");
		HostInternetScsiHba iScsiHba = vc.storageSystem.getiScsiHba(storageSystemMor, 
				com.vmware.vcqa.TestConstants.ADAPTER_TYPE_SOFTWARE); // only one iScsi adapter
		String iScsiHbaID = iScsiHba.getDevice();
		return vc.storageSystem.removeInternetScsiStaticTargets(storageSystemMor, iScsiHbaID, staticTatgets);
	}
	
	public boolean rescanAllStorageAdapter(String hostName) throws Exception {
		log.info("Trying to rescan all storage adapters for ESX host " + hostName);
		ManagedObjectReference hostMor = vc.hostSystem.getHost(hostName);
		ManagedObjectReference storageSystemMor = vc.storageSystem.getStorageSystem(hostMor);
		return vc.storageSystem.rescanAllHba(storageSystemMor);
	}
	
	public boolean isSoftwareInternetScsiEnabled(String hostName) throws Exception {
		log.info("Trying to check if  SoftwareInternetScsi is enabled for ESX host " + hostName);
		ManagedObjectReference hostMor = vc.hostSystem.getHost(hostName);
		ManagedObjectReference storageSystemMor = vc.storageSystem.getStorageSystem(hostMor);
		HostStorageDeviceInfo storageCfg = vc.storageSystem.getStorageInfo(storageSystemMor);
		return storageCfg.isSoftwareInternetScsiEnabled();
	}
	
	public String addVMKernelAdapter(String hostName, String portGroupName) throws Exception {
		log.info("Trying to add VMKernelAdapter (" + portGroupName + ") for ESX host " + hostName);
		ManagedObjectReference hostMor = vc.hostSystem.getHost(hostName);
		ManagedObjectReference networkSystemMor = vc.networkSystem.getNetworkSystem(hostMor);
		HostVirtualSwitchConfig[] hostVSwitch = vc.networkSystem.getVirtualSwitch(networkSystemMor);
		HostPortGroupSpec hostPortGroupSpec = vc.networkSystem.createPortGroupSpec(portGroupName);
		hostPortGroupSpec.setVswitchName(hostVSwitch[0].getName());	// host default has only one virtual switch
		boolean isSuccess = vc.networkSystem.addPortGroup(networkSystemMor, hostPortGroupSpec);
		if (!isSuccess) {
			return null;
		}

		HostIpConfig ipConfig = new HostIpConfig();
		ipConfig.setDhcp(true); 	// get ip automatically
		HostVirtualNicSpec hostVNicSpec = new HostVirtualNicSpec();
		hostVNicSpec.setIp(ipConfig);
		
		return vc.networkSystem.addVirtualNic(networkSystemMor, portGroupName, hostVNicSpec);	
	}	
	
	public boolean removeVMKernelAdapter(String hostName, String vNicID, 
			String portGroupName) throws Exception {
		log.info("Trying to remove VMKernelAdapter (" + portGroupName + ") for ESX host " + hostName);
		ManagedObjectReference hostMor = vc.hostSystem.getHost(hostName);
		ManagedObjectReference networkSystemMor = vc.networkSystem.getNetworkSystem(hostMor);
		boolean isSuccess = vc.networkSystem.removeVirtualNic(networkSystemMor, vNicID);
		if(!isSuccess) {
			return false;
		}
		
		isSuccess = vc.networkSystem.removePortGroup(networkSystemMor, portGroupName);
		if (!isSuccess) {
			return false;
		}
		
		return true;
	}
	
	
	
	public HostVirtualNic getVirtualNic(String hostName, String portGroupName) throws Exception {
		ManagedObjectReference hostMor = vc.hostSystem.getHost(hostName);
		ManagedObjectReference networkSystemMor = vc.networkSystem.getNetworkSystem(hostMor);
		//HostVirtualNic x = vc.networkSystem.getVirtualNic(networkSystemMor, portGroupName, true);
		
		return vc.networkSystem.getVirtualNic(networkSystemMor, portGroupName, true);
	}
			
	public boolean enableVMotionForVMKernelAdapter(String hostName, HostVirtualNic vNic) 
			throws Exception {
		ManagedObjectReference hostMor = vc.hostSystem.getHost(hostName);
		VirtualNicManager vnicManager = new VirtualNicManager(vc.connectAnchor);
		ManagedObjectReference vnicManagerMor = vnicManager.getVirtualNicManager(hostMor);
		return vnicManager.selectVnic(vnicManagerMor, HostVirtualNicManagerNicType.VMOTION.value(), vNic);
	}
	public boolean RemoveHost(String esxHostName) throws Exception
	{
		log.info("Trying to remove ESX host " + esxHostName);
		ManagedObjectReference hostMor = vc.hostSystem.getHost(esxHostName);
		return vc.folder.destroy(hostMor);
	}
}
