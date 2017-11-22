package com.vmware.AutoInfraVC;

import org.apache.log4j.Logger;

import com.vmware.Utils.Log4jInstance;
import com.vmware.vc.ClusterConfigSpec;
import com.vmware.vc.ClusterDasConfigInfo;
import com.vmware.vc.ClusterDrsConfigInfo;
import com.vmware.vc.HostConnectSpec;
import com.vmware.vc.ManagedObjectReference;



public class ClusterOperation {
	private VC vc;
	private Logger log = Log4jInstance.getLoggerInstance();
	public ClusterOperation(VC vc)
	{
		this.vc = vc;
		log.info("ClusterOperation instance initialized successfully.");
	}
	private ManagedObjectReference createDefaultCluster_(ManagedObjectReference dcMor,String clusterName) throws Exception {
		log.info("Try to Create cluster with default settings.");
		ManagedObjectReference hostFdr = vc.folder.getHostFolder(dcMor);
		ClusterConfigSpec defaultClusterSpec = vc.folder.createClusterSpec();
		return vc.folder.createCluster(hostFdr, clusterName, defaultClusterSpec);
		
	}
	public Boolean moveHostToCluster(String clusterName, String hostName) throws Exception
	{
		log.info("Try to move Host(" + hostName + ") To Cluster(" + clusterName + ")");
		ManagedObjectReference clusterMor = vc.cluster.getClusterByName(clusterName);
		ManagedObjectReference hostMor = vc.hostSystem.getHost(hostName);
		ManagedObjectReference[] hostArray = {hostMor};
		return vc.cluster.moveInto(clusterMor, hostArray);
	}
	public Boolean createDefaultCluster(String dataCenterName,String clusterName) throws Exception {
		log.info("Try to Create clusterwith default settings. Cluster Name is - " + clusterName + ", under datacenter - " + dataCenterName);
		ManagedObjectReference datacenterMor = vc.folder.getDataCenter(dataCenterName);
		ManagedObjectReference clusterMor = createDefaultCluster_(datacenterMor, clusterName);
		if (clusterMor==null)
			return false;
		return true;
	}
	
	private ManagedObjectReference createDefaultHACluster_(ManagedObjectReference dcMor,
			String clusterName) throws Exception {
		log.info("Try to Create HA cluster with default settings.");
		ManagedObjectReference hostFdr = vc.folder.getHostFolder(dcMor);
		ClusterConfigSpec defaultClusterSpec = vc.folder.createClusterSpec();
		ClusterDasConfigInfo dasConfigInfo = vc.folder.createDASConfigInfo();
		dasConfigInfo.setEnabled(true);
		defaultClusterSpec.setDasConfig(dasConfigInfo);
		return vc.folder.createCluster(hostFdr, clusterName, defaultClusterSpec);
	}
	public Boolean createDefaultHACluster(String dataCenterName,
			String clusterName) throws Exception {
		log.info("Try to Create HA cluster with default settings. Cluster Name is - " + clusterName + ", under datacenter - " + dataCenterName);
		ManagedObjectReference datacenterMor = vc.folder.getDataCenter(dataCenterName);
		ManagedObjectReference clusterMor = createDefaultHACluster_(datacenterMor, clusterName);
		
		if (clusterMor==null)
			return false;
		return true;
	}
	private ManagedObjectReference createDefaultDRSCluster_(ManagedObjectReference dcMor,
			String clusterName) throws Exception {
		log.info("Trying to Create DRS cluster with default settings.");
		ManagedObjectReference hostFdr = vc.folder.getHostFolder(dcMor);
		ClusterConfigSpec defaultClusterSpec = vc.folder.createClusterSpec();
		ClusterDrsConfigInfo drsConfigInfo = vc.folder.createDRSConfigInfo();
		drsConfigInfo.setEnabled(true);
		defaultClusterSpec.setDrsConfig(drsConfigInfo);
		return vc.folder.createCluster(hostFdr, clusterName, defaultClusterSpec);
	}
	public Boolean createDefaultDRSCluster(String dataCenterName, String clusterName) throws Exception {
		log.info("Trying to Create DRS cluster with default settings. Cluster Name is - " + clusterName + ", under datacenter - " + dataCenterName);
		ManagedObjectReference datacenterMor = vc.folder.getDataCenter(dataCenterName);
		ManagedObjectReference clusterMor = createDefaultDRSCluster_(datacenterMor, clusterName);
		if (clusterMor==null)
			return false;
		return true;		
	}
	private ManagedObjectReference addHostsToCluster_(ManagedObjectReference clusterMor,
			String esxhostName, String esxUser, String esxPWD) throws Exception {
		log.info("Trying to Add host to Cluster.");
		HostConnectSpec connectSpec = new HostConnectSpec();
		connectSpec.setHostName(esxhostName);
		connectSpec.setUserName(esxUser);
		connectSpec.setPassword(esxPWD);
		ManagedObjectReference hostMor = vc.cluster.addHost(clusterMor,
				connectSpec, true, null, null);
		
		return hostMor;
	}
	public Boolean addHostsToCluster(String dataCenterName, String ClusterName, String esxhostName, String esxUser, String esxPWD) throws Exception{
		log.info("Trying to Add host (" + esxhostName + ") to Cluster (" + ClusterName + ").");
		ManagedObjectReference datacenterMor = vc.folder.getDataCenter(dataCenterName);
		ManagedObjectReference ClusterMor = vc.folder.getClusterByName(ClusterName, datacenterMor);
		ManagedObjectReference hostMor = addHostsToCluster_(ClusterMor, esxhostName, esxUser, esxPWD);
		if (hostMor==null)
			return false;
		return true;	
	}
	public Boolean removeCluster(String dataCenterName, String ClusterName) throws Exception
	{
		log.info("Trying to Remove Cluster (" + ClusterName + ")");
		ManagedObjectReference datacenterMor = vc.folder.getDataCenter(dataCenterName);
		ManagedObjectReference ClusterMor = vc.folder.getClusterByName(ClusterName, datacenterMor);
		return vc.folder.destroy(ClusterMor);
	}
}
