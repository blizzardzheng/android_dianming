package com.example.tiaoxingdemo;

import android.app.Application;
import android.content.Context;

 
public class MyApplication extends Application{

    private static Context context;
    private static boolean checkupdate;

    public void onCreate(){
    	
        super.onCreate();
        checkupdate =true;
        MyApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }

	public  static  void setCheckupdate(boolean checkupdate) {
		MyApplication.checkupdate = checkupdate;
	}

	public  static  boolean isCheckupdate() {
		return MyApplication.checkupdate;
	}
}
