package com.bb.ui;


import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bb.MainActivity;
import com.bb.api.HttpApiAccessor;
import com.bb.db.DbControl;
import com.bb.model.Info;
import com.bb.util.AsyncImageLoader;
import com.bb.util.AsyncImageLoader.ImageCallback;
import com.bb.util.Constants;
import com.example.tiaoxingdemo.R;



public class InfoInfoActivity extends Activity {


	private Info info ;
//	private EditText et_seat ;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.info_info);
		
//获取上一个activity传过来的参数值
		info = (Info) getIntent().getSerializableExtra("info");
		
		TextView tv_name = (TextView) this.findViewById(R.id.name);
		tv_name.setText(   info.info_name +  ": " + info.info_description + "，车位数剩余：" +   info.info_price   ) ;
		 
		
//		从服务器上获取图片，并且显示
		ImageView iv = (ImageView) this.findViewById(R.id.food_img) ; 
        String picPath = Constants.WEB_APP_URL + "upload/" + info.info_pic   ; 
		AsyncImageLoader asyncImageLoader = new AsyncImageLoader();
		
		Drawable cachedImage = asyncImageLoader.loadDrawable(
    			picPath , iv , new ImageCallback() {

					public void imageLoaded(Drawable imageDrawable,
							ImageView imageView, String imageUrl) {
						imageView.setImageDrawable(imageDrawable);
					}
				});

		if (cachedImage == null) {
			iv.setImageResource(R.drawable.pork);
		} else {
			iv.setImageDrawable(cachedImage);
		}
		
//		给按钮做响应事件
		Button btn = (Button) findViewById(R.id.button1) ;
		
		btn.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
        	  Intent all = new Intent(InfoInfoActivity.this, DituActivity.class);
              all.putExtra("ditu", info.info_discount_price );
              startActivity(all);
			}
		}) ;
		
		
	}
	
	
}
