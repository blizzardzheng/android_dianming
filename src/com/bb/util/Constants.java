package com.bb.util;

import edu.self.model.UserInfo;


public class Constants {


    public static final String FLAG_ALL = "2";
    

	public static UserInfo userinfo;
	
	public static String userId;
	
	public static final String Preferences_user = "user";
	public static final String Preferences_userid = "userid";
	public static final String Preferences_password = "password";
	public static final String Preferences_autologin = "autologin";
	public static final String Preferences_savepassword = "savepassword";
	
//	public static final String IP = "10.0.2.2";
	

	public static final String IP = "192.168.0.102";
	
	public static final String PORT = "8080";
	
	public static final String SERVER = "http://" + IP + ":" + PORT + "/dm"; 
    
	public static final String  WEB_APP_URL = SERVER + "/" ;
	
	public static final String SERVER_LOGIN = "/Login?method=login";

	public static final String SERVER_CHECKIN = "/checkin.do";
	

	public static final String SERVER_SEND_COMMENT = "/sendComment";
	
	public static final String SERVER_REGISTER = "/RegisterAccount?method=RegisterAccount";
	public static final String SERVER_USERS = SERVER + "/Users"; //JsonArray.tostring
	public static final String SERVER_UPDATEUSER = SERVER + "/UpdateUser"; //boolean
	public static final String SERVER_UPDATERESOURCE = SERVER + "/UpdateResource"; //boolean;
	public static final String SERVER_RESOURCEBYUID = SERVER + "/ResourcesByUid";  //JsonArray.tostring
	public static final String SERVER_NEWRESOURCE = SERVER + "/NewResource";  //boolean
	public static final String SERVER_USERBYUSERID = SERVER + "/UserByuserId"; //Jsonobject
	public static final String SERVER_RESOURCES = SERVER + "/Resources";     //JsonArray.toString
	public static final String SERVER_COMMENTS = SERVER + "/Comments?rid=";     //JsonArray.toString
	public static final String SERVER_DELETERESOURCE = SERVER + "/DeleteResource";  //boolean

	public static final String HTTP_POST = "POST";
	public static final String HTTP_GET = "GET";
    
}
