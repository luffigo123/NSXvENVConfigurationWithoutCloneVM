package com.vmware.Utils;

import java.util.List;

import com.vmware.AutoInfraVC.VC;
import com.vmware.vc.ManagedObjectReference;
import com.vmware.vcqa.vim.host.DatastoreInformation;

public class DatastoreUtils {

	private VC vc;
	
	public DatastoreUtils(VC vc) {
		this.vc = vc;
	}

	public String getDatastoreName(String esxiHostIP){
		String dataStoreName = "";
		try{
			ManagedObjectReference  hostMor = vc.hostSystem.getHost(esxiHostIP);
			List<ManagedObjectReference> datastore1MorList = vc.datastore.getDatastores(hostMor);
			 for (int i=0; i<datastore1MorList.size();i++){
				 DatastoreInformation di = vc.datastore.getDatastoreInfo(datastore1MorList.get(i));
				 String temp = di.getName();
				 if(temp.contains("datastore")){
					 dataStoreName = temp;
					 break;
				 }
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		 return dataStoreName;
	}
}
