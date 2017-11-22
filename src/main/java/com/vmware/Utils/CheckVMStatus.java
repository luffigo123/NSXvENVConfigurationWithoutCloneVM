package com.vmware.Utils;

import com.vmware.Utils.PropertiesUtils;

public class CheckVMStatus{

	private boolean cloneVM1Flag;
	private boolean cloneVM2Flag;
	
	private PropertiesUtils pu;
	
	public CheckVMStatus(){
		pu = new PropertiesUtils("tempResult.properties");
	}
	
	public boolean checkCloneStatus(){
		boolean result = false;
		
		cloneVM1Flag = Boolean.valueOf(pu.getValueByKey("cloneVM1Flag"));
		cloneVM2Flag = Boolean.valueOf(pu.getValueByKey("cloneVM2Flag"));
		
		if(cloneVM1Flag && cloneVM2Flag){
			result = true;
		}
		return result;
	}

}
