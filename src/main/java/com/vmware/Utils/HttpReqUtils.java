package com.vmware.Utils;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

public class HttpReqUtils {
	private CloseableHttpClient httpclient = null;
	private String vsmUsername = null;
	private String vsmPassword = null;
	private String auth = null;
	

	public HttpReqUtils(){
		httpclient = AvoidSSLTrust.generateClient();
		vsmUsername = DefaultEnvironment.vsmUserName;
		vsmPassword = DefaultEnvironment.vsmPasswd;
		auth = new String(Base64.encodeBase64((vsmUsername+":"+vsmPassword).getBytes()));
	}

	/**
	 * Http get request
	 * @param url
	 * @return
	 */
	public String sendGetRequest(String url){

		HttpGet httpget = new HttpGet(url);
		httpget.setHeader("Authorization", "Basic " + auth);
        System.out.println("Executing request " + httpget.getRequestLine());
        CloseableHttpResponse response = null;
		try {
			response = httpclient.execute(httpget);

		} catch (IOException e) {
			e.printStackTrace();
		}
System.out.println("----------------------------------------");
System.out.println(response.getStatusLine());
        HttpEntity entity = response.getEntity();
        String queryInfo = null;
		try {
			queryInfo = EntityUtils.toString(entity,"UTF-8");
		} catch (ParseException | IOException e) {
			e.printStackTrace();
		}finally{
			try {
				httpclient.close();
			} catch (IOException e) {

				e.printStackTrace();
			}
		}
System.out.println(queryInfo);
        return queryInfo;
	}
	
	
	/**
	 * Send Http Post request
	 * @param content
	 * @param url
	 * @param headers
	 */
	@SuppressWarnings("rawtypes")
	public String sendPostRequest(String content, String url, HashMap<String,String> headers){
		HttpPost httpPost = new HttpPost(url);
		
		StringEntity stringEntity = new StringEntity(content,"UTF-8");
		httpPost.setHeader("Authorization", "Basic " + auth);
		Iterator it = headers.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry me = (Map.Entry)it.next();
			httpPost.setHeader(me.getKey().toString(), me.getValue().toString());
		}
		httpPost.setEntity(stringEntity);
	
System.out.println("request line------------" + httpPost.getRequestLine());
		
		String responseString = "";
		try {
			HttpResponse httpResponse = httpclient.execute(httpPost);
			if(httpResponse!=null){
				responseString = this.getResponseContent(httpResponse);
			}
//			HttpEntity responseEntity = httpResponse.getEntity();
//			if(responseEntity != null){
//				responseString = EntityUtils.toString(responseEntity);
//				System.out.println(responseString);	
//			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return responseString;
	}
	
	
	/**
	 * Send Http delete request
	 * @param url
	 */
	public void sendDeleteRequest(String url){
		HttpDelete httpdel = new HttpDelete(url);

		httpdel.setHeader("Authorization", "Basic " + auth);
        System.out.println("Executing request " + httpdel.getRequestLine());
        CloseableHttpResponse httpResponse = null;
		try {
			httpResponse = httpclient.execute(httpdel);
			if(httpResponse!=null){
				this.getResponseContent(httpResponse);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				httpclient.close();
			} catch (IOException e) {

				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Send Http delete request
	 * @param url
	 */
	@SuppressWarnings({ "rawtypes" })
	public void sendDeleteRequestWithHeader(String url,HashMap<String,String> headers){
		HttpDelete httpdel = new HttpDelete(url);

		httpdel.setHeader("Authorization", "Basic " + auth);
		Iterator it = headers.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry me = (Map.Entry)it.next();
			httpdel.setHeader(me.getKey().toString(), me.getValue().toString());
		}
		
        System.out.println("Executing request " + httpdel.getRequestLine());
        CloseableHttpResponse httpResponse = null;
		try {
			httpResponse = httpclient.execute(httpdel);
			
			if(httpResponse!=null){
				this.getResponseContent(httpResponse);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				httpclient.close();
			} catch (IOException e) {

				e.printStackTrace();
			}
		}
	}
	
	
	
	/**
	 * Send Http Put request
	 * @param content
	 * @param url
	 * @param headers
	 */
	@SuppressWarnings({ "rawtypes" })
	public void sendPutRequest(String content, String url, HashMap<String,String> headers){

		StringEntity stringEntity = new StringEntity(content,"UTF-8");
		HttpPut httpPut = new HttpPut(url);
		httpPut.setHeader("Authorization", "Basic " + auth);
		Iterator it = headers.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry me = (Map.Entry)it.next();
			httpPut.setHeader(me.getKey().toString(), me.getValue().toString());
		}
		httpPut.setEntity(stringEntity);
		
System.out.println("request line------------" + httpPut.getRequestLine());
		
		try {
			HttpResponse httpResponse = httpclient.execute(httpPut);
			if(httpResponse!= null){
				this.getResponseContent(httpResponse);
			}
			
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				httpclient.close();
			} catch (IOException e) {

				e.printStackTrace();
			}
		}
	}
	
	/**
	 * print response information
	 * @param httpResponse
	 * @throws ParseException
	 * @throws IOException
	 */
	 public String getResponseContent(HttpResponse httpResponse) throws ParseException, IOException{
		    // 获取响应消息实体
		    HttpEntity entity = httpResponse.getEntity();
		    // 响应状态
		    System.out.println("status:" + httpResponse.getStatusLine());
		    System.out.println("headers:");
		    HeaderIterator iterator = httpResponse.headerIterator();
		    while (iterator.hasNext()) {
		      System.out.println("\t" + iterator.next());
		    }
		    	    
		    // 判断响应实体是否为空
		    String responseString = "";
		    if (entity != null) {
		      responseString = EntityUtils.toString(entity);
		      System.out.println("response length:" + responseString.length());
		      responseString = responseString.replace("\r\n", "");
		      System.out.println("response content:" + responseString);
		     
		    }
		    return responseString;
	}
}
