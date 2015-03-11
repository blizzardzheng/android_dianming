package com.bb.alarm; 


import java.util.ArrayList;
import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bb.MainActivity;
import com.bb.api.HttpApiAccessor;
import com.bb.model.Type;
import com.example.tiaoxingdemo.R;



public class AlarmReceiver extends BroadcastReceiver { 


	/** 
	 * 
	* 通过广播进行扫描，是否到达时间后再响起闹铃 
	* 
	* */ 
	@Override 
	public void onReceive( final Context context, Intent intent) { 
 
		new Thread() {
			public void run() {
				
			    ArrayList<Type> list2 = new ArrayList() ;
				try {
					list2 = HttpApiAccessor.getFollowed2(-1, -1, "&tuisong=yes&0");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(  list2.size() > 0 ) {
			    	NotificationManager nm2 = (NotificationManager)context.getSystemService(context.NOTIFICATION_SERVICE);	
			    	Type object = list2.get(0) ;  
					/* create Intent，调用AlarmAlert.class */
				    Intent intent2 = new Intent(context, MainActivity.class);

					Notification notification = new Notification( R.drawable.ic_launcher , "有推送消息" ,System.currentTimeMillis());
		   		
			       	//后面的参数分别是显示在顶部通知栏的小图标，小图标旁的文字（短暂显示，自动消失）系统当前时间 
			       	notification.defaults=Notification.DEFAULT_ALL;  
			       	notification.flags = notification.FLAG_AUTO_CANCEL ;
			        
			       	PendingIntent pt = PendingIntent.getActivity( context , 0, intent2 , PendingIntent.FLAG_UPDATE_CURRENT);
			       	
			       	if(object.getContent().length() > 20 ){
			       		notification.setLatestEventInfo( context  , object.getName() , object.getContent().substring(0, 20) , pt);	
			       	}else{
			       		notification.setLatestEventInfo( context  , object.getName() , object.getContent() , pt);
			       	}
			       	
			       	nm2.notify(19172440, notification); 
			    }
				
			};
		}.start();
		 
	} 
	  
 
	
	
	private static String pad(int c) {    
		
		if (c >= 10)        
			return String.valueOf(c);    
		else        
			return "0" + String.valueOf(c);
		
	}
	
	
} 
