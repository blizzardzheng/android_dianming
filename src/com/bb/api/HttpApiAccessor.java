package com.bb.api;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.bb.model.Info;
import com.bb.model.Type;
import com.bb.util.Constants;


/**
 * 
 * @author 
 *
 */
public class HttpApiAccessor {

	
	
	public static String saveOrder( HashMap params  ) {
		String url = Constants.WEB_APP_URL + "orderEdit.do?method=save&type=json"  ;
		String result = null ;

		result = BaseAuthenicationHttpClient.doRequest(url, "", "" , params );

		return result ; 
	}
	
 
	public static boolean getTuisong(long lastId, int pageNo, String flag) throws Exception{
		String url = Constants.WEB_APP_URL + "order.do?method=goAlert&type=json&id=" + Constants.userId ;
		
		url = appendParams(url, lastId, pageNo);
		String jsonString = BaseAuthenicationHttpClient.doRequest(url, "", "");

		JSONArray jsonArray = new JSONArray(jsonString);	 
		
		if( jsonArray.length() > 0 ) 
			return true; 
		
		return false;
	}
		

//根据flag去获取指定的
	public static ArrayList<Info> getFollowed(long lastId, int pageNo, String flag , String search_type ) throws Exception{
		String url = Constants.WEB_APP_URL + "infoList.do?type=json";
		if(flag != null && !flag.equals( Constants.FLAG_ALL)){
			url += "&f=" + flag;
		}
		if( search_type != null ){
			url += "&search_type=" + search_type;
		} 
		
		return getUpdatesList(url,lastId,pageNo);
	}


	public static ArrayList<Type> getFollowed2(long lastId, int pageNo, String flag) throws Exception{
		String url = Constants.WEB_APP_URL + "type.do?method=list&type=json";
		if(flag != null  ){
			url += "&f=" + flag;
		} 
		
		System.out.println( url  );
		return getUpdatesList2(url,lastId,pageNo);
	}
	
	private static ArrayList<Type> getUpdatesList2(String url,long lastId, int pageNo) throws Exception{
		url = appendParams(url, lastId, pageNo);
		String jsonString = BaseAuthenicationHttpClient.doRequest(url, "", "");

		JSONArray jsonArray = new JSONArray(jsonString);		
		ArrayList<Type> ret = new ArrayList<Type>();
		for( int i = 0; i != jsonArray.length(); i++){
			JSONObject json = jsonArray.getJSONObject(i);
			Type food = jsonToType(json);
			ret.add(food);
		}
		return ret;
	}

//	将json数据解析赋值到object类
	private static Type jsonToType(JSONObject json) throws JSONException{
		Type object = new Type();
		
		object.id = json.getInt("id") ;  
		object.name = json.getString("name") ; 
		object.content = json.getString("content") ;
		 
		return object;
	}
	
	
//	
	private static ArrayList<Info> getUpdatesList(String url,long lastId, int pageNo) throws Exception{
		url = appendParams(url, lastId, pageNo);
		String jsonString = BaseAuthenicationHttpClient.doRequest(url, "", "");

		JSONArray jsonArray = new JSONArray(jsonString);		
		ArrayList<Info> ret = new ArrayList<Info>();
		for( int i = 0; i != jsonArray.length(); i++){
			JSONObject json = jsonArray.getJSONObject(i);
			Info info = jsonToInfo(json);
			ret.add(info);
		}
		return ret;
	}
	
//	将json数据解析赋值到food类
	private static Info jsonToInfo(JSONObject json) throws JSONException{
		Info info = new Info();
		info.info_description = json.getString("info_description") ;
		info.info_discount_price =  json.getString("info_discount_price") ;
		info.info_flag = json.getInt("info_flag") ;
		info.info_id = json.getInt("info_id") ;
		info.info_name = json.getString("info_name") ;
		info.info_pic = json.getString("info_pic") ;
		info.info_price =  json.getString("info_price").toString() ;
		
		return info;
	}

	
	private static String appendParams(String url, long lastId, int pageNo) {
		if(lastId != -1){
			url = "?last_id=" + lastId;
		}
		if(pageNo != -1){
			url = "&pageNo=" + pageNo;
		}
		return url;
	}

	
	
}



