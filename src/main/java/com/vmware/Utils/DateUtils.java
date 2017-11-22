package com.vmware.Utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
	
	public String getCurrentTime(Date nowDate){
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		String temp = df.format(nowDate);
		return temp;
	}

	public String getDuringTime(String startTime, String endTime){
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");   
		Date startDate = null;
		Date endDate = null;
		try {
			startDate = df.parse(startTime);
			endDate = df.parse(endTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}   
		long l=endDate.getTime()-startDate.getTime();   
		long day=l/(24*60*60*1000);   
		long hour=(l/(60*60*1000)-day*24);   
		long min=((l/(60*1000))-day*24*60-hour*60);   
		long s=(l/1000-day*24*60*60-hour*60*60-min*60);   
		String duringTime = "" + day + "day, " + hour +" hour, "+ min + " minutes, " + s + "seconds"; 
		return duringTime;
	}
	
}
