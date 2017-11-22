package com.vmware.AutoInfraVC;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.concurrent.CountDownLatch;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;


import org.apache.log4j.Logger;

import com.vmware.Utils.Log4jInstance;

public class DownloadThread extends Thread{

	 // remote file RUL
    private String url = null;
    // local file name
    private String fileName = null;
    private long threadCount = 0;
    // offset of this thread in the whole file
    private long offset = 0;
    // The length of download block in this thread
    private long length = 0;
    private CountDownLatch end;
    private CloseableHttpClient httpClient;
    private HttpContext context;
    private Logger log = Log4jInstance.getLoggerInstance();
 
    /**
     * @param url
     *            remote file RUL
     * @param fileName
     *            local file name
     * @param offset
     *            offset of this thread in the whole file
     * @param length
     *            The length of download block in this thread
     * @author Sean Li
     * */
 
    public DownloadThread(String url, String file, long offset, long length,
            CountDownLatch end, Long threadCount, CloseableHttpClient httpClient) {
        this.url = url;
        this.fileName = file;
        this.offset = offset;
        this.length = length;
        this.end = end;
        this.threadCount = threadCount;
        this.httpClient = httpClient;
        this.context = new BasicHttpContext(); 
    }
 
    public void run() {
        try {
            HttpGet httpGet = new HttpGet(this.url);
            
            httpGet.addHeader("Range", "bytes=" + this.offset + "-" + (this.offset + this.length - 1));
            
            CloseableHttpResponse response = httpClient.execute(httpGet, context);
            ;
            BufferedInputStream bis = new BufferedInputStream(response.getEntity().getContent());
            byte[] buff = new byte[1024];
            int bytesRead;
            File newFile = new File(fileName);
            RandomAccessFile raf = new RandomAccessFile(newFile, "rw");
            while ((bytesRead = bis.read(buff, 0, buff.length)) != -1) {
                raf.seek(this.offset);
                raf.write(buff, 0, bytesRead);
                this.offset = this.offset + bytesRead;
            }
            raf.close();
            bis.close();
        } catch (ClientProtocolException e) {
        	log.error(e.getMessage());
        	e.printStackTrace();
        } catch (IOException e) {
        	log.error(e.getMessage());
        	e.printStackTrace();
        } finally {
            end.countDown();
        }
        
        DecimalFormat df = (DecimalFormat)NumberFormat.getInstance();  
        df.setMaximumFractionDigits(2);
        df.setRoundingMode(RoundingMode.HALF_UP);
        double accuracy_num = (double)end.getCount() / (double)threadCount * 100;
        log.info("BuildDownloading -- " + df.format(accuracy_num)+"%" + " is left!");
    }
}
