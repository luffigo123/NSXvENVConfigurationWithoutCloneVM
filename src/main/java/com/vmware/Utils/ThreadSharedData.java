package com.vmware.Utils;

public class ThreadSharedData {
	public String value = null;
	public String deltaValue = null;
	public String markString = null;
	public String nimbusNoResourceString = null;
	public String extendLeaseSuccessfully = null;
	public String vmMaxLease = null;
	public boolean completed = false;
	public boolean isExist = false;
	
	
	private static ThreadSharedData threadSharedData = null;
	
	private ThreadSharedData() {
		super();
		markString = "You can kill the VM with";
		nimbusNoResourceString = "NimbusExceptionNoPod";
		extendLeaseSuccessfully = "lease extended to";
		vmMaxLease = "Lease Duration cannot be more than 7 days";
	}
	
	public static synchronized ThreadSharedData getInstance(){
		if(threadSharedData == null){
			threadSharedData = new ThreadSharedData();
		}
		return threadSharedData;
	}
	
	
}
