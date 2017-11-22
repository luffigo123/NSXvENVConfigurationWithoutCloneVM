package com.vmware.testcases;

import org.testng.annotations.Test;
import com.vmware.Utils.MultiEnvSetup01;

public class LuanchMulti {

	@Test(groups = {"luanch"},alwaysRun=true)
	public void luanch(){
		MultiEnvSetup01 multiEnvSetup01 = new MultiEnvSetup01();
		multiEnvSetup01.envSetup();
	}
	
}
