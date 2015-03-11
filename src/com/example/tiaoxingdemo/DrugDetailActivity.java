package com.example.tiaoxingdemo;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnDrawListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tiaoxingdemo.DownloadManagerAsync.OnDownloadCompleteListener;

public class DrugDetailActivity extends Activity  {

	View head;
	LinearLayout mLayout;
	Builder builder;
	ListView expandableList;
	public List<String> groups = new ArrayList<String>();
	public List<String> child = new ArrayList<String>();
	private long id = 0;
	ProgressDialog progressDialog = null;

	private boolean drugIsCollected = false;
	private Button imageFavor;
	private Button imageFavor2;
	boolean bDataHaveReveived;
	Handler timeHandler;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.drug_info_detail);

		Bundle bundle = getIntent().getExtras();
		this.id = (long) bundle.getLong("drug");
		Button buttonBack = (Button) this.findViewById(R.id.back);
		Button buttonHome = (Button) this.findViewById(R.id.home);
		// Button buttonComment_post = (Button)
		// this.findViewById(R.id.comment_post);
//		Button buttonComment = (Button) this.findViewById(R.id.comment);

		buttonBack.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				DrugDetailActivity.this.finish();
			}
		});
		buttonHome.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				Intent home = new Intent();
				home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(home);

			}
		});

//		buttonComment.setOnClickListener(new OnClickListener() {
//			public void onClick(View v) {
//
//
//			}
//		});
//		imageFavor2 = (Button) this.findViewById(R.id.imageFavor2);
//		if (drugIsCollected) {
//			// drug_collected
//			// imageFavor2.setText("已收藏");
//		}
//		imageFavor2.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				// 判断是否已经添加
//			}
//		});

		progressDialog = ProgressDialog.show(this, "请稍等...", "数据加载中...", true,
				true);

		bDataHaveReveived = false;

		timeHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (bDataHaveReveived)
					return;
				// 关闭ProgressDialog
				if (progressDialog != null) {
					progressDialog.dismiss();
					progressDialog = null;

					Toast.makeText(DrugDetailActivity.this, "网络链接好像不稳定，请检查网络！",
							Toast.LENGTH_LONG).show();

				}

			}
		};
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (bDataHaveReveived)
					return;
				Message mesasge = new Message();
				mesasge.what = 0;
				timeHandler.sendMessage(mesasge);
			}
		}, 30000);


		builder = new AlertDialog.Builder(DrugDetailActivity.this);

	}

	/*
	 * private void creatDialog(String args) {
	 * 
	 * builder.setMessage(args); builder.setPositiveButton("确定", new
	 * DialogInterface.OnClickListener() {
	 * 
	 * @Override public void onClick(DialogInterface dialog, int which) { //
	 * TODO Auto-generated method stub
	 * 
	 * } }); builder.create().show();
	 * 
	 * }
	 */

	private void creatToast(String args) {
		Toast toast = Toast.makeText(getApplicationContext(), args,
				Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}

	/**
	 * A sample ListAdapter that presents content from arrays of speeches and
	 * text.
	 * 
	 */

	/**
	 * com.xywy.drug.view.EllipsizingTextView We will use a IllCell to display
	 * each speech. It's just a LinearLayout with two text fields.
	 * 
	 */

		/**
		 * Convenience method to set the title of a IllCell
		 */

		/**
		 * 半角转换为全角
		 * 
		 * @param input
		 * @return
		 */
		public String ToDBC(String input) {
			char[] c = input.toCharArray();
			for (int i = 0; i < c.length; i++) {
				if (c[i] == 12288) {
					c[i] = (char) 32;
					continue;
				}
				if (c[i] > 65280 && c[i] < 65375)
					c[i] = (char) (c[i] - 65248);
			}
			return new String(c);
		}

		/**
		 * 去除特殊字符或将所有中文标号替换为英文标号
		 * 
		 * @param str
		 * @return
		 */
		public String stringFilter(String str) {
			str = str.replaceAll("【", "[").replaceAll("】", "]")
					.replaceAll("！", "!").replaceAll("：", ":");// 替换中文标号
			String regEx = "[『』]"; // 清除掉特殊字符
			Pattern p = Pattern.compile(regEx);
			Matcher m = p.matcher(str);
			return m.replaceAll("").trim();
		}


	}




