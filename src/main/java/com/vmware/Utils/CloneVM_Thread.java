package com.vmware.Utils;

import com.vmware.AutoInfraVC.VMOperation;
import com.vmware.Utils.PropertiesUtils;

public class CloneVM_Thread implements Runnable{
	private VMOperation vmOperation;
	private String sourceVM;
	private String destVMName;
	private String destHostName;
	private String destDatastore;
	
	private PropertiesUtils pu;
	
	private String number;
	
	//EnvConst.VM1_temp, testData.VM1, esx1_IP, destDatastore
	public CloneVM_Thread(VMOperation vmOperation, String sourceVM, String destVMName, String destHostName, String destDatastore, String no){
		this.vmOperation = vmOperation;
		this.sourceVM = sourceVM;
		this.destVMName = destVMName;
		this.destHostName = destHostName;
		this.destDatastore = destDatastore;
		
		pu = new PropertiesUtils("tempResult.properties");
		this.number = no;
	}

	@Override
	public void run() {	
		try {
			 boolean result = vmOperation.CloneVM(sourceVM, destVMName, destHostName, destDatastore);
			 String temp = String.valueOf(result);
//			 if(destVMName.contains("vm1")){
//				 pu.writeValueByKey("cloneVM1Flag", temp);
//			 }else{
//				 pu.writeValueByKey("cloneVM2Flag", temp);
//			 }
			 
		 if("first".equalsIgnoreCase(this.number)){
			 pu.writeValueByKey("cloneVM1Flag", temp);
		 }else if("second".equalsIgnoreCase(this.number)){
			 pu.writeValueByKey("cloneVM2Flag", temp);
		 }else {
			 
		 }
			 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

