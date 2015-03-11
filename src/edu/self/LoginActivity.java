package edu.self;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.HTTP;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bb.MainActivity;
import com.bb.util.Constants;
import com.bb.util.HttpUtil;
import com.example.tiaoxingdemo.R;




public class LoginActivity extends Activity {
	
	private EditText et_id;
	private EditText et_password;
	private ProgressDialog dialog;
	
	private String userId;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.loginpage);
        
        et_id = (EditText) findViewById(R.id.login_edit_account);
		et_password = (EditText) findViewById(R.id.login_edit_pwd);
		
		
		((Button) findViewById(R.id.login_btn))
		.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				 
				String uin = et_id.getText().toString().trim();
				String pwd = et_password.getText().toString().trim();
				if ((uin.length() == 0) || (pwd.length() == 0)) {
					Toast.makeText(LoginActivity.this, "用户名或者密码不能为空", Toast.LENGTH_LONG).show();
					return;
				} 
				check();
			}
		});
		
		((Button) findViewById(R.id.reg_btn)).setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
	        	Intent intent = new Intent();
				intent.setClass(LoginActivity.this, RegisterAccountActivity.class);
				startActivity(intent);
			}
		});
		 
    }
    
    public void check(){
    
        SharedPreferences preferences = getSharedPreferences( Constants.Preferences_user, Activity.MODE_APPEND);

        long loginTime = preferences.getLong("loginTime" , 0 ) ;
        userId = preferences.getString("userId", null );
        
//	      学生不能再一天里面更换学号和姓名 防止扫好立马换了再扫
        if( loginTime> 0 && loginTime < System.currentTimeMillis() ){
        	
        	dialog = new ProgressDialog(this);
    		dialog.setMessage("登录中,请稍候, 不能再一天里面更换学号和姓名");
    		dialog.show();
    		runOnUiThread(new Runnable() {
				public void run() {

		        	Intent intent = new Intent();
					intent.setClass(LoginActivity.this, MainActivity.class);
					intent.putExtra("userId", userId );
					
					Constants.userId =  et_id.getText().toString() ;  
					
					startActivity(intent);
					
					dialog.dismiss();
					LoginActivity.this.finish();
				}
    		}); 
        }else{
			login();
        }
        
    }
    
    
    public void login() {
    	dialog = new ProgressDialog(this);
		dialog.setMessage("登录中...请稍候");
		dialog.show();
    	new Thread(){
			public void run() {
				if (HttpUtil.isConnectInternet(LoginActivity.this)) {
					HttpPost post = new HttpPost(Constants.SERVER + Constants.SERVER_LOGIN);
					List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
					params.add(new BasicNameValuePair("userId", et_id.getText().toString()));
					params.add(new BasicNameValuePair("password", et_password.getText().toString()));
					try {
						post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
						post.getParams().setBooleanParameter(
								CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
						HttpResponse response = (HttpResponse) new DefaultHttpClient().execute(post);;
						if (response != null) {
							if (200 == response.getStatusLine().getStatusCode()) {
								InputStream is = response.getEntity().getContent();
								Reader reader = new BufferedReader(new InputStreamReader(is));
								StringBuilder builder = new StringBuilder((int) response.getEntity().getContentLength());
								char[] temp = new char[4000];
								int len = 0;
								while ((len = reader.read(temp)) != -1) {
									builder.append(temp, 0, len);
								}
								reader.close();
								is.close();
								String content = builder.toString();
								response.getEntity().consumeContent();
								if (content.equals("true")) {
									runOnUiThread(new Runnable() {
										public void run() {
											Intent intent = new Intent();
											intent.setClass(LoginActivity.this, MainActivity.class);
											intent.putExtra("userId", et_id.getText().toString());
											
											Constants.userId =  et_id.getText().toString() ; 
											 
									        SharedPreferences preferences = getSharedPreferences( Constants.Preferences_user, Activity.MODE_APPEND);
									        Editor sharedata = preferences.edit();
									        sharedata.putString("userId",  Constants.userId ) ;
									        sharedata.putLong("loginTime",  (System.currentTimeMillis() + 15*60*60*1000 ) ) ;
									        sharedata.commit();
									        
											System.out.println( " Constants.userId :::::" + Constants.userId ); 
											
											startActivity(intent);
											dialog.dismiss();
											LoginActivity.this.finish();
										}
									});
								} else if (content.equals("false")) {
									runOnUiThread(new Runnable() {
										public void run() {
											dialog.dismiss();
											Toast.makeText(LoginActivity.this, "帐号不存在或用户名密码错误！", Toast.LENGTH_LONG).show();
										}
									});
								} else {
									runOnUiThread(new Runnable() {
										public void run() {
											dialog.dismiss();
											Toast.makeText(LoginActivity.this, "登录失败，请稍后再试！", Toast.LENGTH_LONG).show();
										}
									});
								}
							}
						}
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					} catch (ClientProtocolException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			};
		}.start();
    }
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.clear();
		getMenuInflater().inflate(R.menu.login_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.register_account:
			startActivity(new Intent(this, RegisterAccountActivity.class));
			finish();
			break;
//		case R.id.net_setting:
//			startActivity(new Intent("android.settings.WIRELESS_SETTINGS"));
//			break;
//		case R.id.update:
//			
//			break;
		case R.id.exit:
			finish();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		SharedPreferences preferences = getSharedPreferences(
    			Constants.Preferences_user,
    			Activity.MODE_APPEND);
		Editor editor = preferences.edit();
		editor.putString(Constants.Preferences_userid, et_id.getText().toString());
		editor.commit();
	}
}