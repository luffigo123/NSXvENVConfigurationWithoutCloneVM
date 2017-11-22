package com.vmware.Utils;

import com.vmware.Utils.JAXBUtils.CollectionWrapper;
import com.vmware.model.VCinfo;

public class VSMManagement {

	
	public HttpReq httpReq;
	private PropertiesUtils pu;
	private String vsmIP;
	public JAXBUtils jAXBUtils;
	
	public VSMManagement(){
		httpReq = HttpReq.getInstance();
		pu = new PropertiesUtils("Config.properties");
		this.vsmIP = pu.getValueByKey("vsm_IP");
	}
	
	public void postRegisterNSXtoVC(String xmlContents){
		String url = "https://" + vsmIP + "/api/2.0/services/vcconfig";
		System.out.println(url);
		httpReq.putRequest(xmlContents, url);
	}
	
	public void registerNSXtoVC(VCinfo vcInfo){
		jAXBUtils = new JAXBUtils(VCinfo.class, CollectionWrapper.class);
		String xmlContents = this.jAXBUtils.objToXml(vcInfo, "UTF-8");
		this.postRegisterNSXtoVC(xmlContents);
	}
	
	public String queryVCConfigDetails() {
		String ep = "https://" + vsmIP + "/api/2.0/services/vcconfig";
		String xmlString = httpReq.getRequest(ep);
		return xmlString;
	}
}
