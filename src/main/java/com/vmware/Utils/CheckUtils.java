package com.vmware.Utils;


public class CheckUtils {
	
	public CheckUtils() {
		super();
	}

	
	@SuppressWarnings("static-access")
	public boolean checkCloneVMAreReady(){
		boolean result = false;
		int time = 0;
		while(result != true){
			System.out.println(" ############## "+ Thread.currentThread().getName().toString() + " ################ ");
			CheckVMStatus check = new CheckVMStatus();
			result = check.checkCloneStatus();
			if(result == false){
				try {
					Thread.currentThread().sleep(60000);
					time ++;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if(time >= 60){
				break;
			}
		}
		return result;
	}
}
