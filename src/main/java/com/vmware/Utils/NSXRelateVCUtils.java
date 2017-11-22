package com.vmware.Utils;


import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;

import com.vmware.Utils.Log4jInstance;
import com.vmware.vc.KeyValue;
import com.vmware.vcqa.ConnectAnchor;
import com.vmware.vcqa.util.Assert;
import com.vmware.vcqa.vim.LicenseAssignmentManager;
import com.vmware.vcqa.vim.LicenseManager;
import com.vmware.vcqa.vim.SessionManager;


public class NSXRelateVCUtils {
	private Logger log = Log4jInstance.getLoggerInstance();
	private boolean success = false;
	
	public NSXRelateVCUtils(){
		super();
	}
	
	/**
	 * 
	 * @param regVCIP
	 * @param vcRootName
	 * @param vcRootPasswd
	 * @return
	 * @throws TimeoutException 
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */
//	public String getVCThumbprint(String regVCIP, String vcRootName, String vcRootPasswd) throws InterruptedException, ExecutionException, TimeoutException{
//	
//		String cmd = "openssl x509 -in /etc/vmware-vpx/ssl/rui.crt -fingerprint -sha1 -noout";
//		Callable<String> AsyncsshConn = new GetInfoViaSSHConnection(cmd, regVCIP, vcRootName,vcRootPasswd);
//		ExecutorService pool = Executors.newFixedThreadPool(1);
//		Future<String> f = pool.submit(AsyncsshConn);
//		String thumbPrint  = f.get(3, TimeUnit.MINUTES);
//
//		thumbPrint = thumbPrint.substring(thumbPrint.lastIndexOf("=") + 1,
//				thumbPrint.lastIndexOf("=") + 60);
//		log.info("Thumbprint Value: " + thumbPrint);
//		
//		return thumbPrint;
//	}
	
	public String getVCThumbprint(String regVCIP, String vcRootName, String vcRootPasswd, String nsxVersion) throws InterruptedException, ExecutionException, TimeoutException{
		
		int nsxR = Integer.valueOf(nsxVersion);
		String cmd = "";
		if(nsxR >= 640) {
			cmd = "openssl x509 -in /etc/vmware-vpx/ssl/rui.crt -fingerprint -sha256 -noout";
		}else {
			cmd = "openssl x509 -in /etc/vmware-vpx/ssl/rui.crt -fingerprint -sha1 -noout";
		}

		Callable<String> AsyncsshConn = new GetInfoViaSSHConnection(cmd, regVCIP, vcRootName,vcRootPasswd);
		ExecutorService pool = Executors.newFixedThreadPool(1);
		Future<String> f = pool.submit(AsyncsshConn);
		String thumbPrint  = f.get(3, TimeUnit.MINUTES);

		thumbPrint = thumbPrint.substring(thumbPrint.lastIndexOf("=") + 1,
				thumbPrint.lastIndexOf("=") + 60);
		log.info("Thumbprint Value: " + thumbPrint);
		
		return thumbPrint;
	}
    
	/**
	 * 
	 * @param regVCIP
	 * @param regVCUser
	 * @param regVCPassword
	 * @return
	 * @throws Exception
	 */
	public LicenseManager getLicenseManager(String regVCIP, String regVCUser, String regVCPassword)throws Exception{
        ConnectAnchor connectAnchor = new ConnectAnchor(regVCIP, 443);
        Assert.assertNotNull(
            SessionManager.login(connectAnchor, regVCUser, regVCPassword),
            "Unable to login into VC");
        LicenseManager lm = new LicenseManager(connectAnchor);
        return lm;
	}
	
	/**
	 * 
	 * @param regVCIP
	 * @param regVCUser
	 * @param regVCPassword
	 * @param licenseKey
	 */
	public void addLicenseToVCInventory(String regVCIP, String regVCUser, String regVCPassword, String licenseKey){
		try {
			LicenseManager lm = this.getLicenseManager(regVCIP, regVCUser, regVCPassword);
			lm.addLicense(licenseKey, this.buildLables());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param regVCIP
	 * @param regVCUser
	 * @param regVCPassword
	 * @param licenseKey
	 * @return
	 */
	public boolean updateLicenseKey(String regVCIP, String regVCUser, String regVCPassword, String licenseKey){
		try {
			LicenseManager lm = this.getLicenseManager(regVCIP, regVCUser, regVCPassword);
			LicenseAssignmentManager lam = lm.getLicenseAssignmentManager();
			success = lam.updateAssignedLicense("nsx-netsec", licenseKey, "nsx");
				if (success == false) {
					log.error("Adding NSX license failed, please check log for more details...");
				}
			} catch (Exception e) {
	              e.printStackTrace();
	              log.error(e.getLocalizedMessage());
	              throw new RuntimeException(e.getLocalizedMessage());
			}
	      log.info("NSX license applied sucessfully");
	      return success;
	}
	
    public KeyValue[] buildLables() {

        KeyValue[] labels = new KeyValue[2];

        KeyValue pair = new KeyValue();
        pair.setKey("NSXLicenseLabel");
        pair.setValue("" + System.nanoTime());

        KeyValue pair1 = new KeyValue();
        pair1.setKey("NSXLicenseLabel2");
        pair1.setValue("" + System.nanoTime() + 1);

        labels[0] = pair;
        labels[1] = pair1;

        return labels;
     }
    

}
