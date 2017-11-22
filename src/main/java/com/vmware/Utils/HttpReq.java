package com.vmware.Utils;

import java.util.HashMap;

public class HttpReq {
	private static HttpReq httpReq = null;

	private HttpReq(){
	}
	
	public static synchronized HttpReq getInstance()
	{
		if (httpReq == null){
			httpReq = new HttpReq();
		}
		return httpReq;
	}
	
	public String postRequest(String contents, String url) {
		 HttpReqUtils httpreq = new HttpReqUtils();
		 HashMap<String,String> headers = new HashMap<String,String>();
		 headers.put("Content-Type", "application/xml");
		 return httpreq.sendPostRequest(contents, url, headers);
	}
	
	public void postRequestWithHeader(String contents, String url,HashMap<String,String> headers){
		 HttpReqUtils httpreq = new HttpReqUtils();
		 HashMap<String,String> finalHeaders = headers;
		 headers.put("Content-Type", "application/xml");
		 httpreq.sendPostRequest(contents, url, finalHeaders);
	}
	
	public void delRequest(String url) {
		HttpReqUtils httpreq = new HttpReqUtils();
		httpreq.sendDeleteRequest(url);

	}
	
	public void delRequestWithHeader(String url, HashMap<String,String> headers){
		HttpReqUtils httpreq = new HttpReqUtils();
		httpreq.sendDeleteRequestWithHeader(url, headers);
	}

	public void putRequest(String contents, String url) {
		 HttpReqUtils httpreq = new HttpReqUtils();
		 HashMap<String,String> headers = new HashMap<String,String>();
		 headers.put("Content-Type", "application/xml"); 
		 httpreq.sendPutRequest(contents, url, headers);
	}
	
	public void putRequestWithHeader(String contents, String url, HashMap<String,String> headers){
		 HttpReqUtils httpreq = new HttpReqUtils();
		 HashMap<String,String> finalHeaders = headers;
		 finalHeaders.put("Content-Type", "application/xml"); 
		 httpreq.sendPutRequest(contents, url, finalHeaders);
	}

	public String getRequest(String url) {
		HttpReqUtils httpreq = new HttpReqUtils();
		 return httpreq.sendGetRequest(url);
	}
}
