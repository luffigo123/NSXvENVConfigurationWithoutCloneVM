//package com.vmware.Utils;
//
//import org.apache.log4j.Logger;
//
//import com.vmware.AutoInfraVC.DatacenterOperation;
//import com.vmware.AutoInfraVC.HostUtils;
//import com.vmware.AutoInfraVC.VC;
//import com.vmware.AutoInfraVC.VMOperation;
//import com.vmware.Utils.DatastoreUtils;
//import com.vmware.Utils.Log4jInstance;
//
//public class CloneUtils {
//	private Logger log = Log4jInstance.getLoggerInstance();
//	private String inputLanguage = null;
//	private TestData testData = null;
//	
//	private String vcUserName;
//	private String vcPassword;
//	private String esxiUsername;
//	private String esxiPasswd;
//	private String vcIP;
//	private String esxi01_IP;
//	private String helperVMname;
//	
//	
//	public CloneUtils() {
//		super();
//		inputLanguage = DefaultEnvironment.inputLanguage;
//		testData = new TestData(inputLanguage);
//		
//		vcIP = DefaultEnvironment.vcIP;
//		vcUserName = DefaultEnvironment.vcUserName;
//		vcPassword = DefaultEnvironment.vcPasswd;
//		esxi01_IP = DefaultEnvironment.esxiHost01_IPAddress;
//		esxiUsername = DefaultEnvironment.esxiUserName;
//		esxiPasswd = DefaultEnvironment.esxiPassword;
//		helperVMname = DefaultEnvironment.helperVMname;
//		
//	}
//
//
//	public boolean cloneVMToEXSi(){
//		
//		if(esxiPasswd == null){
//			esxiPasswd = "";
//		}
//		
//		try{
//
//			log.info("Create a temp Datacenter!");
//			VC vc = new VC(vcIP, "443", vcUserName, vcPassword);
//			HostUtils hostOps_forESX = new HostUtils(vc);
//			DatacenterOperation dcOps_froESX = new DatacenterOperation(vc);
//			VMOperation vmOps_froESX2 = new VMOperation(vc);
//			String DCName_temp =  "dcTemp";
//			if(!dcOps_froESX.createDatacenter(DCName_temp)){
//				log.info(inputLanguage + ": Failed to Add the temp DC in VC - " + vc.VCIP);
//				return false;
//			}
//			
//			log.info("Add the hostForCloneVM to VC!");
//			String hostForCloneVM = DefaultEnvironment.hostForCloneVM;
//			if(!hostOps_forESX.addStandaloneHost(hostForCloneVM, "root", "ca$hc0w", DCName_temp)){
//				log.info(inputLanguage + ": Failed to Add the host of " + hostForCloneVM + " to VC - " + vc.VCIP);
//				return false;
//			}
//			
//			log.info("Add ESXi_01 to the temp Datacenter!");
//			if(!hostOps_forESX.addStandaloneHost(esxi01_IP, esxiUsername, esxiPasswd, DCName_temp)){
//				log.info(inputLanguage + ": Failed to Add the host of " + esxi01_IP + " to VC - " + vc.VCIP);
//				return false;
//			}
//	
//			log.info("Clone 2 vm to ESXi_01");
//			DatastoreUtils datastoreUtils = new DatastoreUtils(vc);
//			String destDatastore = datastoreUtils.getDatastoreName(esxi01_IP);
////			new Thread(new CloneVM_Thread(vmOps_froESX2,helperVMname, testData.VM1, esxi01_IP, destDatastore)).start();
////			new Thread(new CloneVM_Thread(vmOps_froESX2,helperVMname, testData.VM2, esxi01_IP, destDatastore)).start();
//			new Thread(new CloneVM_Thread(vmOps_froESX2,helperVMname, testData.VM1, esxi01_IP, destDatastore, "first")).start();
//			new Thread(new CloneVM_Thread(vmOps_froESX2,helperVMname, testData.VM2, esxi01_IP, destDatastore, "second")).start();
//			
//			
//			log.info("If the clone VMs are ready, power on the VMs!");
//			CheckUtils checkUtils = new CheckUtils();
//			
//			if(checkUtils.checkCloneVMAreReady()){
//				if(!vmOps_froESX2.PowerOnVM(testData.VM1)){
//					log.info(inputLanguage + ": Failed to Power on the VM1 to host - " + esxi01_IP);
//					return false;
//				}
//				if(!vmOps_froESX2.PowerOnVM(testData.VM2)){
//					log.info(inputLanguage + ": Failed to Power on the VM2 to host - " + esxi01_IP);
//					return false;
//				}
//			}
//			
//			log.info("Remove the ESXi_01 from temp Datacenter, and then remove the temp Datacenter itself!");
//			hostOps_forESX.RemoveHost(esxi01_IP);
//			hostOps_forESX.RemoveHost(hostForCloneVM);
//			dcOps_froESX.RemoveDatacenter(DCName_temp);
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//		return true;
//	}
//}
