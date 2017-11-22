package com.vmware.AutoInfraVC;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.vmware.Utils.Log4jInstance;
import com.vmware.vc.HttpNfcLeaseDeviceUrl;
import com.vmware.vc.HttpNfcLeaseInfo;
import com.vmware.vc.HttpNfcLeaseState;
import com.vmware.vc.LocalizedMethodFault;
import com.vmware.vc.ManagedEntityStatus;
import com.vmware.vc.ManagedObjectReference;
import com.vmware.vc.MethodFault;
import com.vmware.vc.OvfCreateDescriptorResult;
import com.vmware.vc.OvfCreateImportSpecParams;
import com.vmware.vc.OvfCreateImportSpecParamsDiskProvisioningType;
import com.vmware.vc.OvfCreateImportSpecResult;
import com.vmware.vc.OvfFile;
import com.vmware.vc.OvfNetworkMapping;
import com.vmware.vc.VirtualCdrom;
import com.vmware.vc.VirtualCdromIsoBackingInfo;
import com.vmware.vc.VirtualDevice;
import com.vmware.vc.VirtualDeviceConfigSpec;
import com.vmware.vc.VirtualDeviceConfigSpecOperation;
import com.vmware.vc.VirtualDeviceConnectInfo;
import com.vmware.vc.VirtualMachineCloneSpec;
import com.vmware.vc.VirtualMachineConfigSpec;
import com.vmware.vc.VirtualMachinePowerState;
import com.vmware.vc.VirtualMachineRelocateSpec;
import com.vmware.vcqa.TestConstants;
import com.vmware.vcqa.http.HttpFileAccess;
import com.vmware.vcqa.util.NetworkUtil;
import com.vmware.vcqa.vim.HttpConstants;
import com.vmware.vcqa.vim.HttpNfcLease;
import com.vmware.vcqa.vim.OvfManager;
import com.vmware.vcqa.vim.VMSpecManager;
import com.vmware.vcqa.vim.VirtualAppTestConstants;

public class VMOperation{
	private VC vc;
	private Logger log = Log4jInstance.getLoggerInstance();
	
	public VMOperation(VC vc)
	{
		this.vc = vc;
	}
	public boolean CreateVM(String vmName, String esxHostName) throws Exception
	{
		return CreateVM(vmName, esxHostName, null, null);
	}

	public boolean CreateVM(String vmName, String esxHostName,  String diskHardwareVersion) throws Exception
	{
		return CreateVM(vmName, esxHostName, diskHardwareVersion, null);
	}

	public String getVMIP(String vmName) throws Exception
	{
		log.info("Trying to get IP address of VM - " + vmName);
		return vc.vm.getIPAddress(vc.vm.getVM(vmName));
	}
	public String getHostESX(String vmName) throws Exception
	{
		log.info("Trying to get ESX hostname which the VM is running on - " + vmName);
		ManagedObjectReference vmMor = vc.vm.getVM(vmName);
		ManagedObjectReference hostMor = vc.vm.getHost(vmMor);
		return vc.hostSystem.getHostName(hostMor);
	}
	// diskHardwareVersion e.g. TestConstants.VM_DISK_VERSION9
	public boolean CreateVM(String vmName, String esxHostName, String diskHardwareVersion, String guestOS) throws Exception
	{
		log.info("Trying to create VM with name - " + vmName + " on ESX of " + esxHostName);
		ManagedObjectReference vmMor = null;
		String[] VM_CREATE_DEFAULT_DEVICE_LIST = {
				TestConstants.VM_VIRTUALDEVICE_DISK,
				TestConstants.VM_RECOMMENDED_ETHERNET }; // ETHERNET_E1000

		ManagedObjectReference hostMor = vc.hostSystem.getHost(esxHostName);
		ManagedObjectReference datacenter_Mor = vc.hostSystem.getDataCenter(hostMor);
		String guestID;
		if (guestOS == null) {
			guestID = TestConstants.VM_DEFAULT_GUEST_WINDOWS;
		} else {
			guestID = guestOS;
		}
		VirtualMachineConfigSpec vmConfigSpec = vc.vm.createVMConfigSpec(
				vc.hostSystem.getResourcePool(hostMor).elementAt(0), hostMor, vmName,
				guestID,
				Arrays.asList(VM_CREATE_DEFAULT_DEVICE_LIST), null);
		vmConfigSpec.setMemoryMB((long) 4);
		vmConfigSpec.setNumCPUs(2);
		if (diskHardwareVersion!=null) {
			vmConfigSpec.setVersion(diskHardwareVersion);
		}
		vmMor = vc.folder.createVM(vc.folder.getVMFolder(datacenter_Mor), vmConfigSpec, vc.hostSystem.getResourcePool(hostMor).elementAt(0), hostMor);
		if (vmMor.equals(null))
			return false;
		return true;
		
	}

	public boolean CreateVM(String vmName, String esxHostName, ManagedObjectReference datastoreMor) throws Exception
	{
		ManagedObjectReference hostMor = vc.hostSystem.getHost(esxHostName);
		return CreateVM(vmName, hostMor, datastoreMor);
	}

	public boolean CreateVM(String vmName, ManagedObjectReference hostMor, ManagedObjectReference datastoreMor) throws Exception
	{
		ManagedObjectReference vmMor = null;
		String[] VM_CREATE_DEFAULT_DEVICE_LIST = {
				TestConstants.VM_VIRTUALDEVICE_DISK,
				TestConstants.VM_RECOMMENDED_ETHERNET }; // ETHERNET_E1000
		ManagedObjectReference datacenter_Mor = vc.hostSystem.getDataCenter(hostMor);
		ManagedObjectReference poolMor = vc.hostSystem.getResourcePool(hostMor).elementAt(0);
		String guestID;
		guestID = TestConstants.VM_DEFAULT_GUEST_WINDOWS;

		VMSpecManager mgr = new VMSpecManager(vc.connectAnchor, poolMor, hostMor);	
		VirtualMachineConfigSpec vmConfigSpec = mgr.createVmConfigSpec(vmName, guestID,
				Arrays.asList(VM_CREATE_DEFAULT_DEVICE_LIST), null, datastoreMor);		
		vmConfigSpec.setMemoryMB((long) 4);
		vmConfigSpec.setNumCPUs(2);
		vmMor = vc.folder.createVM(vc.folder.getVMFolder(datacenter_Mor), vmConfigSpec, poolMor, hostMor);
		if (vmMor.equals(null))
			return false;
		return true;
	}
	
	public boolean CloneVM(ManagedObjectReference sourceVMMor, String destVMName, 
			ManagedObjectReference destHost, ManagedObjectReference destDatastore, boolean asTemplate) throws Exception
	{
		log.info("Trying to clone VM.");
		// Create CloneSpec obj
		VirtualMachineCloneSpec vmCloneSpec = new VirtualMachineCloneSpec();
		VirtualMachineRelocateSpec vmRelocateSpec = new VirtualMachineRelocateSpec();
		vmRelocateSpec.setHost(destHost);
		vmRelocateSpec.setDatastore(destDatastore);
		

		ManagedObjectReference RPMor = null;
		Vector<ManagedObjectReference> allCompRes = vc.cluster.getAllComputeResources();
		for (int i = 0; i < allCompRes.size(); i++)
		{
			Vector<ManagedObjectReference> hostMorList = vc.cluster.getHosts(allCompRes.elementAt(i));
			if (hostMorList == null)
				break;
			for (int y = 0; y < hostMorList.size(); y++)
			{
				String tempHostname = vc.hostSystem.getName(hostMorList.elementAt(y));
				if (tempHostname.toString().equals(vc.hostSystem.getHostName(destHost)))
				{
					RPMor = vc.cluster.getResourcePool(allCompRes.elementAt(i));
					break;
				}
			}
		}
		vmRelocateSpec.setPool(RPMor);
		vmCloneSpec.setLocation(vmRelocateSpec);
		vmCloneSpec.setTemplate(asTemplate);	
		ManagedObjectReference datacenterMor = vc.hostSystem.getDataCenter(destHost);
		ManagedObjectReference targetFolderMor = vc.folder.getVMFolder(datacenterMor);
		ManagedObjectReference vmMor = vc.vm.cloneVM(sourceVMMor, targetFolderMor , destVMName, vmCloneSpec);
		if (vmMor.equals(null))
			return false;
		return true;
	}

	public boolean CloneVM(ManagedObjectReference sourceVMMor, String destVMName, 
			ManagedObjectReference destHost, ManagedObjectReference destDatastore) throws Exception
	{
		return CloneVM(sourceVMMor, destVMName, destHost, destDatastore, false);
	}
	public boolean CloneVM(String sourceVM, String destVMName, 
			String destHostName, String destDatastore) throws Exception
	{
		ManagedObjectReference sourceVMMor = vc.vm.getVM(sourceVM);
		ManagedObjectReference destHostMor = vc.hostSystem.getHost(destHostName);
		ManagedObjectReference destDatastoreMor = vc.datastore.getDatastore(destHostMor, destDatastore);
		return CloneVM(sourceVMMor, destVMName, destHostMor, destDatastoreMor, false);
	}
	public boolean CloneVM(String sourceVM, String destVMName, 
			String destHostName) throws Exception
	{
		ManagedObjectReference sourceVMMor = vc.vm.getVM(sourceVM);
		ManagedObjectReference destHostMor = vc.hostSystem.getHost(destHostName);
		ManagedObjectReference destDatastoreMor = new HostUtils(this.vc).getLargestFreeDatastoreMor(destHostName);
		return CloneVM(sourceVMMor, destVMName, destHostMor, destDatastoreMor, false);
	}
	
	public boolean waitForGOSReady(String vmName, int iMinutes) throws Exception
	{
		log.info("Waitfor GOS ready, the VM name is - " + vmName + ", time out is - " + iMinutes + " minutes.");
		ManagedObjectReference vmMor = vc.vm.getVM(vmName);
		
			for (int i = 0; i < iMinutes * 60; i++) 
			{
				Thread.sleep(1000);
				ManagedEntityStatus vmHeartbeatStatus = vc.vm.getHeartbeatStatus(vmMor);
				if (vmHeartbeatStatus.name() == "GREEN")
				{
					log.info("**** The VM is ready -- " + vmName);
					return true;
				}
				continue;
			}
			log.error("Failed to Waitfor GOS ready, the VM name is - " + vmName + ", time out is - " + iMinutes + " minutes.");
			return false;
	}

	
	
	public boolean waitForGOSDown(String vmName, int iMinutes)
    {
		log.info("Waitfor VM down, the VM name is - " + vmName + ", time out is - " + iMinutes + " minutes.");
    	try {
    		ManagedObjectReference vmMor = vc.vm.getVM(vmName);
    		for (int i = 0; i < iMinutes * 60; i++) 
    		{
				Thread.sleep(1000);
				ManagedEntityStatus vmHeartbeatStatus = vc.vm.getHeartbeatStatus(vmMor);
				if (vmHeartbeatStatus.name().equalsIgnoreCase("red"))
				{
					log.info("**** The VM is Powered Off -- " + vc.vm.getVMName(vmMor));
					return true;
				}
				continue;
			}
    		return false;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
    }	
	public VirtualMachineConfigSpec createDefaultVMConfigSpec(String vmName,
			String[] DEVICE_LIST, String hostName) throws Exception {
		ManagedObjectReference hostMor = vc.hostSystem.getHost(hostName);
		VirtualMachineConfigSpec vmConfigSpec = vc.vm.createVMConfigSpec(
				vc.hostSystem.getResourcePool(hostMor).elementAt(0), hostMor,
				vmName, com.vmware.vcqa.TestConstants.VM_DEFAULT_GUEST_WINDOWS,
				Arrays.asList(DEVICE_LIST), null);
		vmConfigSpec.setMemoryMB((long) 4);
		vmConfigSpec.setNumCPUs(2);
		return vmConfigSpec;
	}

	public boolean PowerOnVM(String vmName) throws Exception {
		ManagedObjectReference vmMor = vc.vm.getVM(vmName);
		ManagedObjectReference vmHostMor = vc.vm.getHost(vmMor);
		return vc.vm.powerOnVM(vmMor, vmHostMor, false);
	}
	
	public boolean isPowerOn(String vmName) throws Exception {
		ManagedObjectReference vmMor = vc.vm.getVM(vmName);
		return vc.vm.getVMState(vmMor).equals(VirtualMachinePowerState.POWERED_ON);
	}

	public boolean isPowerOff(String vmName) throws Exception {
		ManagedObjectReference vmMor = vc.vm.getVM(vmName);
		return vc.vm.getVMState(vmMor).equals(VirtualMachinePowerState.POWERED_OFF);
	}

	public boolean isSuspended(String vmName) throws Exception {
		ManagedObjectReference vmMor = vc.vm.getVM(vmName);
		return vc.vm.getVMState(vmMor).equals(VirtualMachinePowerState.SUSPENDED);
	}

	public boolean isAllDiskOnSpecialDatastore(final ManagedObjectReference vmMor,
			final ManagedObjectReference expectedDSMor) throws Exception {
		HashMap<Integer, ManagedObjectReference> map = vc.vm.getVMDiskDatastoreMap(vmMor);
		for (Integer diskKey: map.keySet()) {
			ManagedObjectReference mapedDSMor = map.get(diskKey);
			if (!mapedDSMor.equals(expectedDSMor)) {
				return false;
			}
		}
		return true;
	}


	public boolean destroy(ManagedObjectReference vmMor) throws Exception {
		log.info("Trying to remove VM.");
		if (vc.managedEntity.entityExistsInInventory(vmMor)) {
			if (vc.vm.getVMState(vmMor).equals(VirtualMachinePowerState.POWERED_ON)) {
				//log.log("VM is powered on, shutdown first.");
				try {
					vc.vm.shutdownGuest(vmMor);
				} catch (Exception e) {
					vc.vm.powerOffVM(vmMor);
				}
			}
			// log.log("Destroy VM.");
			boolean destroied = vc.vm.destroy(vmMor);
			Thread.sleep(5*1000);//make sure datastore space has been re-calculated.
			return destroied;
		}
		return true;
	}
	public boolean destroy2(String vmName) throws Exception {
		log.info("Trying to remove VM - " + vmName);
		ManagedObjectReference vmMor = vc.vm.getVM(vmName);
		if (vc.managedEntity.entityExistsInInventory(vmMor)) {
			if (vc.vm.getVMState(vmMor).equals(VirtualMachinePowerState.POWERED_ON)) {
				//log.log("VM is powered on, shutdown first.");
				try {
					vc.vm.shutdownGuest(vmMor);
				} catch (Exception e) {
					vc.vm.powerOffVM(vmMor);
				}
			}
			// log.log("Destroy VM.");
			boolean destroied = vc.vm.destroy(vmMor);
			Thread.sleep(5*1000);//make sure datastore space has been re-calculated.
			return destroied;
		}
		return true;
	}

	/**
	 * Solution without thread, call downloadVM with a bug fix in it
	 * @param vmMor
	 * @param createDescriptor
	 * @param exportLocation
	 * @return
	 * @throws Exception
	 */
	public List<OvfFile> exportVM(final ManagedObjectReference vmMor,
			final boolean createDescriptor, final String exportLocation)
					throws Exception {
		ManagedObjectReference httpNfcLeaseMor = null;
		ManagedObjectReference ovfManagerMor = null;
		HttpNfcLeaseInfo leaseInfo = null;
		HttpNfcLease httpNfcLease = null;
		List<OvfFile> ovfFiles = null;

		final OvfManager iOvfMor = new OvfManager(vc.connectAnchor);
		ovfManagerMor = iOvfMor.getOvfManager();
		httpNfcLease = new HttpNfcLease(vc.connectAnchor);
		httpNfcLeaseMor = vc.vm.getExportVMLease(vmMor);
		if (httpNfcLeaseMor != null) {
			while(httpNfcLease.getHttpNfcLeaseState(httpNfcLeaseMor)
					.equals(HttpNfcLeaseState.INITIALIZING)) {
				Thread.sleep(1000);
			}
			if(httpNfcLease.getHttpNfcLeaseState(httpNfcLeaseMor)
					.equals(HttpNfcLeaseState.READY)) {
				leaseInfo = httpNfcLease.getHttpNfcInfo(httpNfcLeaseMor);
				ovfFiles = downloadVM(vmMor, leaseInfo, exportLocation);

				if (createDescriptor && ovfFiles != null) {
					/*
					 * create descriptor after exporting VM.
					 */
					final OvfCreateDescriptorResult descriptor
					= iOvfMor.createDescriptor(ovfManagerMor,
							vmMor, ovfFiles, exportLocation);
					if (com.vmware.vcqa.util.TestUtil.vectorToArray(descriptor.getError(), com.vmware.vc.LocalizedMethodFault.class) == null) {
						if (descriptor.getOvfDescriptor() != null) {
							//log.log("VM exported successfully");
							httpNfcLease.complete(httpNfcLeaseMor);
						} else {
							/*
							 * release the lock
							 */
							if (leaseInfo.getLease() != null) {
								httpNfcLease.abort(httpNfcLeaseMor, com.vmware.vcqa.util.TestUtil
										.vectorToArray(descriptor.getError(), com.vmware.vc.LocalizedMethodFault.class) == null ? null : com.vmware.vcqa.util.TestUtil
												.vectorToArray(descriptor.getError(), com.vmware.vc.LocalizedMethodFault.class)[0]);
							}
						}
					} else {
						//log.log("Failed to create descriptor");
						final LocalizedMethodFault[] errors = com.vmware.vcqa.util.TestUtil.vectorToArray(descriptor.getError(), com.vmware.vc.LocalizedMethodFault.class);
						//for (int i = 0; i < errors.length; i++) {
							//log.log("Error - "+ errors[i].getFault().getClass());
							//log.log("Error message - "+ errors[i].getLocalizedMessage());
						//}
						final MethodFault fault = errors[0].getFault();
						if(!httpNfcLease.getHttpNfcLeaseState(httpNfcLeaseMor)
								.equals(HttpNfcLeaseState.DONE)) {
							httpNfcLease.abort(httpNfcLeaseMor, errors[0]);
						}
						throw new com.vmware.vc.MethodFaultFaultMsg("", fault);
					}
				} else if (ovfFiles != null) {
					//log.log("VM exported successfully");
					httpNfcLease.complete(httpNfcLeaseMor);
				} else {
					//log.log("Failed to export VM");
					/*
					 * release the lock
					 */
					if (leaseInfo.getLease() != null) {
						httpNfcLease.abort(httpNfcLeaseMor, null);
					}
				}
			} else if(httpNfcLease.getHttpNfcLeaseState(httpNfcLeaseMor)
					.equals(HttpNfcLeaseState.ERROR)){
				final MethodFault fault = httpNfcLease.getError(httpNfcLeaseMor);
				throw new com.vmware.vc.MethodFaultFaultMsg("", fault);
			}
		} else {
			// log.log("Failed to obtain ApplianceLease");
			return null;
		}
		return ovfFiles;
	}

	private List<OvfFile> downloadVM(final ManagedObjectReference vmMor,
			final HttpNfcLeaseInfo httpNfcLeaseInfo,
			final String downloadLocation) throws Exception {
		String urlString = null;
		List<OvfFile> ovfFiles = null;
		String filename = null;
//		String dirName = null;
		String destFile = null;
		boolean success = false;
		String hostName = null;

		if (vmMor != null && httpNfcLeaseInfo != null) {
			hostName = vc.connectAnchor.getHostName();
			final HttpFileAccess http = new HttpFileAccess("", "");
			String tempDir = null;
			if(downloadLocation == null) {
				tempDir = System.getProperty(HttpConstants.PROP_OS_NAME)
						.toUpperCase().contains(com.vmware.vcqa.TestConstants.HOST_OS_WINDOWS) ?
								VirtualAppTestConstants.OVF_FILE_WIN_LOCATION
								: VirtualAppTestConstants.OVF_FILE_LINUX_LOCATION;
			} else {
				tempDir = downloadLocation;
			}

			/*
			 * get the device urls from the leaseInfo
			 */
			final HttpNfcLeaseDeviceUrl[] urls = com.vmware.vcqa.util.TestUtil.vectorToArray(httpNfcLeaseInfo.getDeviceUrl(), com.vmware.vc.HttpNfcLeaseDeviceUrl.class);
			ovfFiles = new ArrayList<OvfFile>();
			if(urls != null) {
				for (int i = 0; i < urls.length; i++) {
					urlString = urls[i].getUrl();
					if ((urlString.contains("https://*"))
							|| (urlString.contains("http://*"))) {
						urlString = urlString.replace("*", hostName);
					}

					filename = urlString.substring(urlString.lastIndexOf("/") + 1);

					/*
					 * Create the directories if they do not exist
					 */

					final File tmpDir = new File(tempDir);
					if (!tmpDir.exists()) {
						tmpDir.mkdirs();
					}
					//Comment below line to fix a bug, keep all files in single folder
					//filename = dirName + File.separator + filename;
					destFile = tempDir + File.separator + filename;

					success = http.downloadFile(urlString, destFile, null);
					if (success) {
						final OvfFile ovfFile = new OvfFile();
						ovfFile.setPath(filename);
						ovfFile.setDeviceId(urls[i].getKey());
						ovfFile.setSize(new File(destFile).length());
						ovfFiles.add(ovfFile);
					}
				}
			}
		}
		return ovfFiles;
	}

	public ManagedObjectReference importVM(String datacenter,
			String hostName, String deployVMName, String ovfFilePath) throws Exception {
		ManagedObjectReference dcMor = vc.folder.getDataCenter(datacenter);
		ManagedObjectReference hostMor = vc.hostSystem.getHost(hostName);
		ManagedObjectReference datastoreMor = new HostUtils(this.vc).getLargestFreeDatastoreMor(hostName);
		return this.importVM(dcMor, hostMor, datastoreMor, deployVMName, ovfFilePath);
	}
	public ManagedObjectReference importVM(ManagedObjectReference dcMor,
			ManagedObjectReference hostMor, ManagedObjectReference dsMor,
			String deployVMName, String ovfFilePath) throws Exception {
		OvfManager ovfManager = new OvfManager(vc.connectAnchor);
		ManagedObjectReference ovfManagerMor = ovfManager.getOvfManager();
		ManagedObjectReference vmFolderMor = vc.folder.getVMFolder(dcMor);
		ManagedObjectReference poolMor = null;
		
		Vector<ManagedObjectReference> allCompRes = vc.cluster.getAllComputeResources();
		for (int i = 0; i < allCompRes.size(); i++)
		{
			Vector<ManagedObjectReference> hostMorList = vc.cluster.getHosts(allCompRes.elementAt(i));
			if (hostMorList == null)
				break;
			for (int y = 0; y < hostMorList.size(); y++)
			{
				String tempHostname = vc.hostSystem.getName(hostMorList.elementAt(y));
				if (tempHostname.toString().equals(vc.hostSystem.getHostName(hostMor)))
				{
					poolMor = vc.cluster.getResourcePool(allCompRes.elementAt(i));
					break;
				}
			}
		}

		OvfCreateImportSpecParams importSpecParams = new OvfCreateImportSpecParams();
		importSpecParams.setEntityName(deployVMName);
		importSpecParams.setHostSystem(hostMor);
		importSpecParams.setDiskProvisioning(OvfCreateImportSpecParamsDiskProvisioningType.THIN.value());
		importSpecParams.setDeploymentOption("");
		
		
		
		OvfNetworkMapping networkMapping = new OvfNetworkMapping();
		networkMapping.setName("VSMgmt");
        networkMapping.setNetwork(vc.network.getNetworkByName("VM Network", hostMor)); // network);
        List<OvfNetworkMapping> networkMappingList = new ArrayList<OvfNetworkMapping>();
        networkMappingList.add(networkMapping);
		importSpecParams.setNetworkMapping(networkMappingList);
		

		OvfCreateImportSpecResult importSpecRes = ovfManager.createImportSpec(ovfManagerMor,
				ovfFilePath, poolMor, dsMor, importSpecParams, true);
		return vc.resourcePool.importVApp(importSpecRes, poolMor, hostMor, vmFolderMor, ovfFilePath);
	}

	public boolean AttachISO(String vmName, String esxHostName, String destDatastoreName, String ISOName) throws Exception
	{
		log.info("Trying to AttachISO to VM - " + vmName + ", the ISO is in datastore of " + destDatastoreName + ", iso name is - " + ISOName);
		ManagedObjectReference esxHostMor = vc.hostSystem.getHost(esxHostName);
		ManagedObjectReference vmDestMor = vc.vm.getVMByName(vmName, null); 
		Vector<VirtualDevice> vcdromList = vc.vm.getDevicesByType(vmDestMor, VirtualCdrom.class.getName());
		VirtualDevice cdrom = vcdromList.elementAt(0);
		VirtualDeviceConnectInfo vdci = new VirtualDeviceConnectInfo();
		vdci.setConnected(true);
		vdci.setStartConnected(true);
		cdrom.setConnectable(vdci);
		VirtualCdromIsoBackingInfo isoBackingInfo = new VirtualCdromIsoBackingInfo();
		isoBackingInfo.setDatastore(vc.datastore.getDatastore(esxHostMor, destDatastoreName));
		isoBackingInfo.setFileName("[" + destDatastoreName + "]" + " " + ISOName);

		cdrom.setBacking(isoBackingInfo);
		
		VirtualMachineConfigSpec vmcs = vc.vm.getVMConfigSpec(vmDestMor);
		List<VirtualDeviceConfigSpec> vdcsList = new ArrayList<VirtualDeviceConfigSpec>();
		VirtualDeviceConfigSpec vdcs = new VirtualDeviceConfigSpec();
		vdcs.setDevice(cdrom);
		vdcs.setOperation(VirtualDeviceConfigSpecOperation.EDIT);
		vdcsList.add(vdcs);
		vmcs.getDeviceChange().addAll(vdcsList);
		boolean success = vc.vm.reconfigVM(vmDestMor, vmcs);
		if (success)
		{
			log.info("Successfully Attached ISO to VM - " + vmName);
			return true;
		}
		else
		{
			log.info("Failed to Attached ISO to VM - " + vmName);
			return false;
		}
	}
	
	public boolean reconfigVMToUsePortGroup(String vmName, String portgroupName) throws Exception
	{
		log.info("Trying to connect the VM - " + vmName + " to portgroup - " + portgroupName);
		ManagedObjectReference vmMor = vc.vm.getVM(vmName);
		return NetworkUtil.reconfigVMToUsePortGroup(vc.connectAnchor, vmMor, portgroupName);
	}
	public VirtualMachineConfigSpec reconfigureVMConnectToPortgroup(String vmName, 
			Map<String, String> ethernetCardNetworkMap) throws Exception
	{
		ManagedObjectReference vmMor = vc.vm.getVM(vmName);
		return NetworkUtil.reconfigureVMConnectToPortgroup(vmMor, this.vc.connectAnchor, ethernetCardNetworkMap);
	}
}

