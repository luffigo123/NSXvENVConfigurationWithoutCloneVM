package com.vmware.AutoInfraVC;
import org.apache.log4j.Logger;

import com.vmware.Utils.Log4jInstance;
import com.vmware.vc.ManagedObjectReference;


public class DatacenterOperation {
	private VC vc;
	private Logger log = Log4jInstance.getLoggerInstance();
	
	public DatacenterOperation(VC vc)
	{
		this.vc = vc;
	}
	public Boolean createDatacenter(String DatacenterName) throws Exception
	{
		log.info("Trying to create Datacenter - " + DatacenterName);
		ManagedObjectReference dcMor = vc.folder.createDatacenter(vc.rootFdrMor, DatacenterName);
		if (dcMor==null)
		{
			log.error("Failed to create Datacenter - " + DatacenterName);
			return false;
		}
		log.error("Successfuly created Datacenter - " + DatacenterName);
		return true;
	}
	
	public Boolean RemoveDatacenter(String DatacenterName) throws Exception
	{
		log.info("Trying to Remove Datacenter - " + DatacenterName);
		ManagedObjectReference dcMor = vc.folder.getDataCenter(DatacenterName);
		return vc.folder.destroy(dcMor);
	}
}
