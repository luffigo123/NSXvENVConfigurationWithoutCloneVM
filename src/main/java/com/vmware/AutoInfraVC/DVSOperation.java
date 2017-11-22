package com.vmware.AutoInfraVC;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.vmware.Utils.Log4jInstance;
import com.vmware.vc.ConfigSpecOperation;
import com.vmware.vc.DistributedVirtualPortgroupPortgroupType;
import com.vmware.vc.ManagedObjectReference;
import com.vmware.vc.VMwareDVSConfigSpec;
import com.vmware.vc.VMwareDvsLacpGroupConfig;
import com.vmware.vc.VMwareDvsLacpGroupSpec;
import com.vmware.vcqa.vim.dvs.DVSUtil;
import com.vmware.vcqa.vim.host.NetworkSystem;
import com.vmware.vim.binding.vim.DistributedVirtualSwitch.NetworkResourceControlVersion;



public class DVSOperation {
	private VC vc;
	private Logger log = Log4jInstance.getLoggerInstance();
	public DVSOperation(VC vc)
	{
		this.vc = vc;
	}
	private ManagedObjectReference createDVSWithHost2(String dvsName, int maxPortNum, 
			List<ManagedObjectReference> hostMorList) throws Exception {
		return DVSUtil.createDVSWithMAXPorts(vc.connectAnchor, hostMorList, maxPortNum, dvsName);
	}
	public boolean  createDVSWithHost(String dvsName, int maxPortNum, 
			String hostName) throws Exception {
		log.info("Trying to create DVS with host - " + hostName + ", dvs Name is - " + dvsName);
		ManagedObjectReference hostMor = vc.hostSystem.getHost(hostName);
		ArrayList<ManagedObjectReference> hostMorList = new ArrayList<ManagedObjectReference>();
		hostMorList.add(hostMor);
		ManagedObjectReference dvsMor = createDVSWithHost2(dvsName, maxPortNum, hostMorList);
		if (dvsMor == null)
		{
			log.error("Failed to create DVS with host - " + hostName);
			return false;
		}
		log.error("Successfully created DVS with host - " + hostName);
		return true;
	}
	private boolean addHostToDVS(ManagedObjectReference dvsMor, ManagedObjectReference hostMor) throws Exception {
		Map<ManagedObjectReference, String> pNicMap = new HashMap<ManagedObjectReference, String>();
		NetworkSystem networkSystem = new NetworkSystem(vc.connectAnchor);
		String[] pNics = networkSystem.getPNicIds(hostMor);
		pNicMap.put(hostMor, pNics[0]); 	// choose the first free pNic
		return DVSUtil.addHostsWithPnicsToDVS(vc.connectAnchor, dvsMor, pNicMap);
	}
	public boolean addHostToDVS(String dataCenterName, String dvsName, String hostName) throws Exception{
		log.info("Trying to add host (" + hostName + ")" + " to DVS of - " + dvsName);
		ManagedObjectReference dcMor = vc.folder.getDataCenter(dataCenterName);
		ManagedObjectReference nwkFolderMor = vc.folder.getNetworkFolder(dcMor);
		ManagedObjectReference dvsMor = vc.folder.getDistributedVirtualSwitch(nwkFolderMor, dvsName);
		ManagedObjectReference hostMor = vc.hostSystem.getHost(hostName);
		return addHostToDVS(dvsMor, hostMor);
	}
	// return: The key of the added port group.
	// type Optional - e.g. DistributedVirtualPortgroupPortgroupType.EARLY_BINDING.value()
	public String addPortGroup(String dataCenterName, String dvsName, String type, int portNum, String portGroupName) 
			throws Exception {
		log.info("Trying to add PortGroup (" + portGroupName + ")" + " on dvs of " + dvsName);
		if(type == null)
			type = DistributedVirtualPortgroupPortgroupType.EARLY_BINDING.value();
		ManagedObjectReference dcMor = vc.folder.getDataCenter(dataCenterName);
		ManagedObjectReference nwkFolder = vc.folder.getNetworkFolder(dcMor);
		ManagedObjectReference dvsMor = vc.folder.getDistributedVirtualSwitch(nwkFolder, dvsName);
		return vc.dvs.addPortGroup(dvsMor, type, portNum, portGroupName);
	}
	public boolean enableNetworkIOControl(String dataCenterName, String dvsName, boolean isEnable) throws Exception {
		log.info("Trying to enable NetworkIOControl for dvs of " + dvsName);
		ManagedObjectReference dcMor = vc.folder.getDataCenter(dataCenterName);
		ManagedObjectReference dvsMor = vc.folder.getDistributedVirtualSwitch(dcMor, dvsName);
		return vc.dvs.enableNetworkResourceManagement(dvsMor, isEnable);
	}
	public boolean upgradeNetworkIOControlToV3(String dataCenterName, String dvsName) throws Exception {
		log.info("Trying to upgrade NetworkIOControl to version 3 for dvs of " + dvsName);
		ManagedObjectReference dcMor = vc.folder.getDataCenter(dataCenterName);
		ManagedObjectReference dvsMor = vc.folder.getDistributedVirtualSwitch(dcMor, dvsName);
		return vc.dvs.setNetworkResourceControlVersion(dvsMor, NetworkResourceControlVersion.version3.name());
	}
	public boolean upgradeEnhancedLACPSupport(String dataCenterName, String dvsName) throws Exception {
		log.info("Trying to upgrade EnhancedLACPSupport for dvs of " + dvsName);
		ManagedObjectReference dcMor = vc.folder.getDataCenter(dataCenterName);
		ManagedObjectReference dvsMor = vc.folder.getDistributedVirtualSwitch(dcMor, dvsName);
		VMwareDVSConfigSpec dvsConfigSpec = vc.dvsHelper.getConfigSpec(dvsMor);
		dvsConfigSpec.setLacpApiVersion("multipleLag");

		boolean isSuccess = vc.dvsHelper.reconfigure(dvsMor, dvsConfigSpec);
		if (!isSuccess) {
			log.info("Failed to upgrade EnhancedLACPSupport for dvs of " + dvsName + ", reason is - Reconfig dvs failed.");
			throw new Exception("Reconfig dvs failed.");
		}
		
		return vc.dvsHelper.getConfigSpec(dvsMor).getLacpApiVersion().equals("multipleLag");
	}
	public boolean newLinkAggregationGroup(String dataCenterName, String dvsName, String lagName) throws Exception {
		ManagedObjectReference dcMor = vc.folder.getDataCenter(dataCenterName);
		ManagedObjectReference dvsMor = vc.folder.getDistributedVirtualSwitch(dcMor, dvsName);
		VMwareDvsLacpGroupConfig dvsLacpGroupConfig = new VMwareDvsLacpGroupConfig();
		dvsLacpGroupConfig.setName(lagName);
		VMwareDvsLacpGroupSpec dvsLacpGroupSpec = new VMwareDvsLacpGroupSpec();
		dvsLacpGroupSpec.setLacpGroupConfig(dvsLacpGroupConfig);
		dvsLacpGroupSpec.setOperation(ConfigSpecOperation.ADD.value());
		VMwareDvsLacpGroupSpec[] dvsLacpGroupSpecs = new VMwareDvsLacpGroupSpec[1];
		dvsLacpGroupSpecs[0] = dvsLacpGroupSpec;
		
		return vc.dvsHelper.updateLacpConfig(dvsMor, dvsLacpGroupSpecs);
	}
}
