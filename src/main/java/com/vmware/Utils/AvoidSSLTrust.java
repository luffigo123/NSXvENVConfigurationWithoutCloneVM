package com.vmware.Utils;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
     * avoid HttpClient SSLPeerUnverifiedException: peer not authenticated
     * @author 
     *
     */
    public class AvoidSSLTrust {

        public static CloseableHttpClient generateClient() {
            try {
                SSLContext ctx = SSLContext.getInstance("TLSv1.2");
                X509TrustManager tm = new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                    public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}
                    public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}
                };
                ctx.init(null, new TrustManager[] { tm }, null);
                
                // Allow TLSv1 protocol only
                @SuppressWarnings("deprecation")
				SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                		ctx, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

                CloseableHttpClient httpclient = HttpClients.custom()
                        .setSSLSocketFactory(sslsf)
                        .build();

                return httpclient;
                
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        }
        
}