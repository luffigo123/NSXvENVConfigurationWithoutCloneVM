package com.vmware.Utils;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import com.vmware.Utils.DefaultEnvironment;
import com.vmware.model.VCinfo;


public class RegisterNSXtoVC {
	private String vc_IP;
	private String vcRootName;
	private String vcRootPassword;
	private String vcUserName;
	private String vcPassword;

	private String nsxVersion;
	private NSXRelateVCUtils nsxRelateVCUtils;
	
	
	public RegisterNSXtoVC() {
		super();

		this.vc_IP = DefaultEnvironment.vcIP;
		vcRootName = DefaultEnvironment.vcRootName;
		vcRootPassword = DefaultEnvironment.vcRootPassword;
		
		vcUserName = DefaultEnvironment.vcUserName;
		this.vcPassword = DefaultEnvironment.vcPasswd;
		
		nsxVersion = DefaultEnvironment.nsxVersion;
		
		nsxRelateVCUtils = new NSXRelateVCUtils();
	}

	public boolean registerNSXtoVC(){
		boolean result = false;
		String vcFingerprint = "";
		try {
			vcFingerprint = nsxRelateVCUtils.getVCThumbprint(vc_IP, vcRootName, vcRootPassword, this.nsxVersion);
		} catch (InterruptedException | ExecutionException | TimeoutException e1) {
			e1.printStackTrace();
		}
		try {
			VSMManagement vmsMrg = new VSMManagement();
	
			VCinfo vcInfo = new VCinfo(vc_IP, this.vcUserName, this.vcPassword, vcFingerprint, "true","","");
			vmsMrg.registerNSXtoVC(vcInfo);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
}
