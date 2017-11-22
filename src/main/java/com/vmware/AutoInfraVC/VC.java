package com.vmware.AutoInfraVC;

import java.util.Vector;

import org.apache.log4j.Logger;

import com.vmware.Utils.Log4jInstance;
import com.vmware.Utils.VCUtils.MorType;
import com.vmware.vcqa.vim.vm.guest.GuestOperationsManager;
import com.vmware.vc.ManagedObjectReference;
import com.vmware.vc.UserSession;
import com.vmware.vcqa.ConnectAnchor;
import com.vmware.vcqa.execution.setup.util.DatacenterHelper;
import com.vmware.vcqa.internal.vim.InternalFolder;
import com.vmware.vcqa.vim.ClusterComputeResource;
import com.vmware.vcqa.vim.Datacenter;
import com.vmware.vcqa.vim.Datastore;
import com.vmware.vcqa.vim.DistributedVirtualPortgroup;
import com.vmware.vcqa.vim.DistributedVirtualSwitch;
import com.vmware.vcqa.vim.Folder;
import com.vmware.vcqa.vim.HostSystem;
import com.vmware.vcqa.vim.ManagedEntity;
import com.vmware.vcqa.vim.Network;
import com.vmware.vcqa.vim.ResourcePool;
import com.vmware.vcqa.vim.SessionManager;
import com.vmware.vcqa.vim.VirtualApp;
import com.vmware.vcqa.vim.VirtualMachine;
import com.vmware.vcqa.vim.dvs.DistributedVirtualSwitchHelper;
import com.vmware.vcqa.vim.host.DatastoreSystem;
import com.vmware.vcqa.vim.host.NetworkSystem;
import com.vmware.vcqa.vim.host.StorageSystem;
import com.vmware.vcqa.vim.host.VsanSystem;
import com.vmware.vcqa.vim.profile.host.HostProfile;
import com.vmware.vcqa.vim.profile.host.ProfileManager;
import com.vmware.vcqa.vim.profile.host.ProfileManagerUtil;


public class VC {
	private Logger log = Log4jInstance.getLoggerInstance();
	
	public String VCIP = "";
	public String VCPort = "";
	public String VCUserName = "";
	public String VCPassword = "";
	
	public ConnectAnchor connectAnchor = null;
	public SessionManager sessionManager = null;
	public UserSession userSession = null;
	public Folder folder = null;
	public ManagedObjectReference rootFdrMor = null;
	public Datacenter datacenter = null;
	public DatacenterHelper dcHelper = null;
	public ClusterComputeResource cluster = null;
	public HostSystem hostSystem = null;
	public Datastore datastore = null;
	public DatastoreSystem datastoreSystem = null;
	//public DatastoreBrowser datastoreBrowser = null;
	public Network network = null;
	public VirtualMachine vm = null;
	public ResourcePool resourcePool = null;
	public VirtualApp vApp = null;
	public DistributedVirtualSwitch dvs = null;
	public DistributedVirtualPortgroup dvPortGroup = null;
	public DistributedVirtualSwitchHelper dvsHelper = null;
	public HostProfile hostProfile = null;
	public ProfileManager profileMgr = null;
	public ProfileManagerUtil profileMgrUtil = null;
	public InternalFolder internalFolder = null;
	public ManagedEntity managedEntity = null;
	public StorageSystem storageSystem = null;
	public NetworkSystem networkSystem = null;
	public VsanSystem vsanSystem = null;
	public GuestOperationsManager GosMgr = null;

	
	public VC(String VCIP, String VCPort, String VCUserName, String VCPassword) throws Exception
	{
		this.VCIP = VCIP;
		this.VCPort = VCPort;
		this.VCUserName = VCUserName;
		this.VCPassword = VCPassword;
		this.connectAnchor = new ConnectAnchor(this.VCIP, Integer.parseInt(this.VCPort));
		this.sessionManager = new SessionManager(this.connectAnchor);
		this.userSession = SessionManager.login(this.connectAnchor, this.VCUserName, this.VCPassword);
		this.folder = new Folder(this.connectAnchor);
		this.rootFdrMor = folder.getRootFolder();
		this.datacenter = new Datacenter(this.connectAnchor);
		this.dcHelper = new DatacenterHelper(connectAnchor);
		this.cluster = new ClusterComputeResource(this.connectAnchor);
		this.hostSystem = new HostSystem(this.connectAnchor);
		this.datastore = new Datastore(this.connectAnchor);
		this.datastoreSystem = new DatastoreSystem(this.connectAnchor);
		this.network = new Network(this.connectAnchor);
		this.vm = new VirtualMachine(this.connectAnchor);
		this.resourcePool = new ResourcePool(this.connectAnchor);
		this.vApp = new VirtualApp(this.connectAnchor);
		this.dvs = new DistributedVirtualSwitch(this.connectAnchor);
		this.dvPortGroup = new DistributedVirtualPortgroup(this.connectAnchor);
		this.dvsHelper = new DistributedVirtualSwitchHelper(this.connectAnchor);
		this.hostProfile = new HostProfile(this.connectAnchor);
		this.profileMgr = new ProfileManager(this.connectAnchor);
		this.profileMgrUtil = new ProfileManagerUtil(this.connectAnchor);
		this.internalFolder = new InternalFolder(this.connectAnchor);
		this.managedEntity = new ManagedEntity(this.connectAnchor);
		this.storageSystem = new StorageSystem(this.connectAnchor);
		this.networkSystem = new NetworkSystem(this.connectAnchor);
		this.vsanSystem = new VsanSystem(this.connectAnchor);
		this.GosMgr = new GuestOperationsManager(this.connectAnchor);
	}

	
	public void cleanUp() throws Exception {
		log.info("log out VC - " + this.VCIP);
		SessionManager.logout(this.connectAnchor);
	}
	

	
	public ManagedObjectReference getAvailableHostMor(String hostName) throws Exception {
		
		ManagedObjectReference mor = null;
		return mor;
	}
	
	public boolean isExists(String name, ManagedObjectReference entityMor)
			throws Exception {
		return (this.managedEntity.entityExistsInInventory(entityMor) &&
				this.managedEntity.getName(entityMor).equals(name));
	}
	public boolean destroy(ManagedObjectReference entityMor) throws Exception {
		if (this.managedEntity.entityExistsInInventory(entityMor)) {
			return this.managedEntity.destroy(entityMor);
		}
		return true;
	}
	
	/**
	 * Get the MO ID for the Virtual Machine by its name
	 * @param vmName
	 * @return VM MOId
	 * @throws Exception
	 */
	public String getVirtualMachineMoIdByName (String vmName) throws Exception {
			Vector<ManagedObjectReference> morListNet = this.folder.getAllChildEntity(this.rootFdrMor,MorType.VirtualMachine.toString());			
			for (int i=0; i<morListNet.size();i++) {
				if(this.vm.getName(morListNet.get(i)).equals(vmName)) {	//Locate the MOR with the specified name
					return morListNet.get(i).getValue();
				}
			}
			return null;
	}
}
