package com.vmware.AutoInfraVC;

import java.io.IOException;
import org.apache.log4j.Logger;
import com.vmware.Utils.Log4jInstance;
import com.vmware.Utils.ThreadSharedData;

public class Downloader extends Thread{
	
	private ThreadSharedData downloadComplete = null;
	private String localPath = null;
	private String remoteFileUrl = null;
	private Logger log = Log4jInstance.getLoggerInstance();
	public Downloader(String localPath, String remoteFileUrl, ThreadSharedData downloadComplete)
	{
		this.localPath = localPath;
		this.remoteFileUrl = remoteFileUrl;
		this.downloadComplete = downloadComplete;
	}
	
	public void run()
	{
		DownLoadManager downloadManager = new DownLoadManager(downloadComplete);
		downloadManager.setLocalPath(localPath);
		downloadManager.setUp();
		downloadManager.setRemoteFileUrl(remoteFileUrl);
		try {
			downloadManager.doDownload();
			downloadManager.tearDown();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.info("Set download completed mark as true.");
		downloadComplete.completed = true;
	}

}
