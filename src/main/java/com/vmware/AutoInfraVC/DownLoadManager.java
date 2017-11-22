package com.vmware.AutoInfraVC;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CountDownLatch;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;

import com.vmware.Utils.Log4jInstance;
import com.vmware.Utils.ThreadSharedData;


public class DownLoadManager {
    // the unit size (byte) for each thread
	private ThreadSharedData FileName = null;
    private long unitSize = 1024 * 1024 * 8;
    @Autowired
    private String remoteFileUrl = null;
    private String localPath = null;
    private TaskExecutor taskExecutor;
    private CloseableHttpClient httpClient;
    private Long starttimes;
    private Long endtimes;
    private Logger log = Log4jInstance.getLoggerInstance();

    @Before
    public void setRemoteFileUrl(String remoteFileUrl)
    {
    	this.remoteFileUrl = remoteFileUrl;
    }
    public void setLocalPath(String localPath)
    {
    	this.localPath = localPath;
    }
    public void setUp()
    {
        starttimes = System.currentTimeMillis();
        log.info("Download Start....");
    }
     
    @After
    public void tearDown() throws Exception
    {
        endtimes = System.currentTimeMillis();
        log.info("Download Complete!");
        log.info("********************");
        log.info("cost time:"+(endtimes-starttimes)/1000+"s");
        log.info("********************");
    }
    public DownLoadManager(ThreadSharedData FileName) {
    	this.FileName = FileName;
    	log.info("Init downloading....");
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(100);
        httpClient = HttpClients.custom().setConnectionManager(cm).build();
    }
 
    // invoke multi-thread for downloading
    @Test
    public void  doDownload() throws IOException {
        String localPath=this.localPath;
        String fileName = new URL(this.remoteFileUrl).getFile();
        log.info("filename��"+fileName);
        fileName = fileName.substring(fileName.lastIndexOf("/") + 1,
                fileName.length()).replace("%20", " ");
        File fLocal = new File(localPath + fileName);
        this.FileName.value = fLocal.getName();
        if (fLocal.exists())
        	return;
        long fileSize = this.getRemoteFileSize(remoteFileUrl);
        this.createFile(localPath+fileName, fileSize);
        Long threadCount = (fileSize/unitSize)+(fileSize % unitSize!=0?1:0);
        long offset = 0;
        CountDownLatch end = new CountDownLatch(threadCount.intValue());
        
        if (fileSize <= unitSize) {// if the file is small or equal to unitSize
            DownloadThread downloadThread = new DownloadThread(remoteFileUrl, localPath+fileName, offset, fileSize,end,threadCount, httpClient);
            //taskExecutor.execute(downloadThread);
            downloadThread.start();
        } else {// If the file size is larger than unit size
            for (int i = 1; i < threadCount; i++) {
                DownloadThread downloadThread = new DownloadThread(
                remoteFileUrl, localPath+fileName, offset, unitSize,end, threadCount, httpClient);
                try {
					//taskExecutor.execute(downloadThread);
                	downloadThread.start();
				} catch (Exception e) {
					log.error(e.getMessage());
					e.printStackTrace();
				}
                offset = offset + unitSize;
            }
 
            if (fileSize % unitSize != 0) {
 
                DownloadThread downloadThread = new DownloadThread(remoteFileUrl, localPath+fileName, offset, fileSize - unitSize * (threadCount-1),end, threadCount,httpClient);
                // taskExecutor.execute(downloadThread);
                downloadThread.start();
            }
            
        }
        try {
            end.await();
        } catch (InterruptedException e) {
        	log.error(e.getMessage());
            e.printStackTrace();
        }
    }
 
    // get the size of remote file
 
    private long getRemoteFileSize(String remoteFileUrl) throws IOException {
        long fileSize = 0;
        HttpURLConnection httpConnection = (HttpURLConnection) new URL(
        remoteFileUrl).openConnection();
        httpConnection.setRequestMethod("HEAD");
        int responseCode = httpConnection.getResponseCode();
        if (responseCode >= 400) {
        	log.error("Error on Web server response!");
            return 0;
        }
        String sHeader;
        for (int i = 1;; i++) {
            sHeader = httpConnection.getHeaderFieldKey(i);
            if (sHeader != null && sHeader.equals("Content-Length")) {
            	log.info("File size:" + httpConnection.getContentLength());
                fileSize = Long.parseLong(httpConnection.getHeaderField(sHeader));
                break;
            }
        }
        return fileSize;
    }
 
    //create file
 
    private void createFile(String fileName, long fileSize) throws IOException {
        File newFile = new File(fileName);
        RandomAccessFile raf = new RandomAccessFile(newFile, "rw");
        raf.setLength(fileSize);
        raf.close();
    }
 
 
    public TaskExecutor getTaskExecutor() {
        return taskExecutor;
    }
 
    public void setTaskExecutor(TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }
     


}
