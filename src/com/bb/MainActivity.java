package com.bb;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bb.alarm.AlarmReceiver;
import com.bb.ui.InfoListActivity;
import com.bb.util.Constants;
import com.example.tiaoxingdemo.CaptureActivity;
import com.example.tiaoxingdemo.R;

import edu.self.component.AppContext;
import edu.self.component.AppException;
import edu.self.component.Connect;
import edu.self.model.UserInfo;

/**
 * 系统启动类，显示操作
 * @author Administrator
 */
public class MainActivity extends Activity {

	
	private TextView   aboutRow, exitRow;

	private ProgressDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		dialog = new ProgressDialog(this);
		dialog.setMessage("正在加载用户信息...请稍候");
		dialog.show();

		new Thread() {
			public void run() {
				loadAllUserInfo();
				runOnUiThread(new Runnable() {
					public void run() {
						for (UserInfo userinfo : list) {
							if (userinfo.getUserId().equals(Constants.userId)) {
								AppContext.userinfo = userinfo;
								load();
							}
						}
						dialog.dismiss();
					}
				});
			};
		}.start();
	}

	private List<UserInfo> list;

	/**
	 * 加载所有用户的信息
	 */
	private void loadAllUserInfo() {
		list = new ArrayList<UserInfo>();
		Connect connect = new Connect( Constants.SERVER_USERS, Constants.HTTP_POST);
		try {
			byte[] data = connect.queryServer(null);
			JSONObject object = new JSONObject(new String(data, "gb2312"));
			JSONArray userArray = object.getJSONArray("users");
			for (int i = 0; i < userArray.length(); i++) {
				JSONObject userObject = userArray.getJSONObject(i);
				UserInfo userinfo = new UserInfo();
				userinfo.setUid(userObject.getInt("uid"));
				userinfo.setPassword(userObject.getString("password"));
				userinfo.setUserId(userObject.getString("userId"));
				userinfo.setUserName(userObject.getString("userName"));
				userinfo.setAddress(userObject.getString("address"));
				userinfo.setPhone(userObject.getString("phone"));
				userinfo.setGrade(userObject.getInt("grade"));
				list.add(userinfo);
			}
		} catch (AppException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	
	private void load() {

		TextView user_row = (TextView) findViewById(R.id.user_row);
		user_row.setText("当前用户：" + AppContext.userinfo.getUserName() );

		
//		// 查看
//		oneRow = (TextView) findViewById(R.id.one_row);
//		oneRow.setOnClickListener(new View.OnClickListener() {
//			public void onClick(View v) {
//
//				Intent all = new Intent(MainActivity.this, InfoListActivity.class);
//				all.putExtra("type", Constants.FLAG_ALL);
//				startActivity(all); 
//			}
//		});

		// 点名签到
		aboutRow = (TextView) findViewById(R.id.about_row);
		aboutRow.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				Log.i("CaptureActivity"," == CaptureActivity=====" );
				Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
				startActivityForResult(intent, 0);
				
			}
		});

		// 退出
		exitRow = (TextView) findViewById(R.id.exit_row);
		exitRow.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});

		 
		AlarmManager aManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		
		Intent intent = new Intent(this, AlarmReceiver.class); 
		intent.setAction("AlarmReceiver");
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0); 
		aManager.setRepeating(AlarmManager.RTC, 0, 2*60*1000, pendingIntent); 
		
	}

	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch (resultCode) { // resultCode为回传的标记，我在B中回传的是RESULT_OK
			case RESULT_OK:
	//			Bundle b = data.getExtras();
	//			String code = b.getString("code");
	//			System.out.println("onActivityResult ===" + code);
	
	//			if (code != null) {
	
					// Intent intent = new Intent(MainActivity.this,
					// InfoListActivity.class);
					// intent.putExtra("search_type", code.replace("QR_CODE:", "")
					// );
					// startActivity(intent) ;
	//			}
	
				break;
			default:
				break;
		}
	}

}
