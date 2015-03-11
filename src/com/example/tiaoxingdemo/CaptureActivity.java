package com.example.tiaoxingdemo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.HTTP;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bb.util.Constants;
import com.bb.util.HttpUtil;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.xywy.drug.camera.CameraManager;

import edu.self.component.AppContext;

public class CaptureActivity extends Activity implements Callback
{

	private CaptureActivityHandler handler;
	private ViewfinderView viewfinderView;
	private boolean hasSurface;
	private Vector<BarcodeFormat> decodeFormats;
	private String characterSet;
	
	private TextView txtResult;
	
	private Button inputBtn;
	private Button cancelBtn;
	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private static final float BEEP_VOLUME = 0.10f;
	private boolean vibrate;


	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		 requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.capture);
		// 初始化CameraManager
		CameraManager.init(getApplication());

		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
		cancelBtn = (Button) findViewById(R.id.cancel_input);
		cancelBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v)
			{
				CaptureActivity.this.finish();
			}
		});
		
		inputBtn = (Button) findViewById(R.id.manual_input);
		inputBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v)
			{  
				checkIn();
			}
		});
		
		txtResult = (TextView) findViewById(R.id.txtResult);
		hasSurface = false;
	}

	
	private void checkIn(){
		
		new Thread(){
			public void run() {
				if (HttpUtil.isConnectInternet(CaptureActivity.this)) {
					HttpPost post = new HttpPost( Constants.SERVER + Constants.SERVER_CHECKIN );
					List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
					
					String  barCode = txtResult.getText().toString() ;
					barCode = barCode.replace("QR_CODE:", "") ;
					
					Log.i("== barCode ===", barCode );
					
					params.add(new BasicNameValuePair("barCode", barCode )); 
					params.add(new BasicNameValuePair("user_id", AppContext.userinfo.getUserId() )); 
					params.add(new BasicNameValuePair("user_name", AppContext.userinfo.getUserName() )); 
					
					try {
						post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
						post.getParams().setBooleanParameter( CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
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
								if (content.equals("1")) {
									runOnUiThread(new Runnable() {
										public void run() {  
											Toast.makeText(CaptureActivity.this, "点名成功！", Toast.LENGTH_SHORT).show();
											CaptureActivity.this.finish(); 
										}
									});
								} else if (content.equals("2")) {
									runOnUiThread(new Runnable() {
										public void run() { 
											Toast.makeText(CaptureActivity.this, "已经点名签到！", Toast.LENGTH_SHORT).show();
											CaptureActivity.this.finish();
										}
									});
								} else {
									runOnUiThread(new Runnable() {
										public void run() { 
											Toast.makeText(CaptureActivity.this, "失败，请稍后再试！", Toast.LENGTH_LONG).show();
											CaptureActivity.this.finish();
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
	protected void onResume()
	{
		super.onResume();
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface)
		{
			initCamera(surfaceHolder);
		}
		else
		{
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		decodeFormats = null;
		characterSet = null;

		playBeep = true;
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL)
		{
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		if (handler != null)
		{
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
	}

	private void initCamera(SurfaceHolder surfaceHolder)
	{
		try
		{
			CameraManager.get().openDriver(surfaceHolder);
		}
		catch (IOException ioe)
		{
			return;
		}
		catch (RuntimeException e)
		{
			return;
		}
		if (handler == null)
		{
			handler = new CaptureActivityHandler(this, decodeFormats,
					characterSet);
		}
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height)
	{

	}

	public void surfaceCreated(SurfaceHolder holder)
	{
		if (!hasSurface)
		{
			hasSurface = true;
			initCamera(holder);
		}

	}

	public void surfaceDestroyed(SurfaceHolder holder)
	{
		hasSurface = false;

	}

	public ViewfinderView getViewfinderView()
	{
		return viewfinderView;
	}

	public Handler getHandler()
	{
		return handler;
	}

	public void drawViewfinder()
	{
		viewfinderView.drawViewfinder();

	}


	public void handleDecode(Result obj, Bitmap barcode)
	{
		viewfinderView.drawResultBitmap(barcode);
		playBeepSoundAndVibrate();

		 txtResult.setText(obj.getBarcodeFormat().toString() + ":"+ obj.getText() );
	}

	private void initBeepSound()
	{
		if (playBeep && mediaPlayer == null)
		{
			// The volume on STREAM_SYSTEM is not adjustable, and users found it
			// too loud,
			// so we now play on the music stream.
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(
					R.raw.beep);
			try
			{
				mediaPlayer.setDataSource(file.getFileDescriptor(),
						file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			}
			catch (IOException e)
			{
				mediaPlayer = null;
			}
		}
	}

	private static final long VIBRATE_DURATION = 200L;

	private void playBeepSoundAndVibrate()
	{
		if (playBeep && mediaPlayer != null)
		{
			mediaPlayer.start();
		}
		if (vibrate)
		{
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	/**
	 * �?��网络是否链接
	 */
	public boolean hasConnectNetwork()
	{
		ConnectivityManager cManager = (ConnectivityManager) MyApplication
				.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cManager.getActiveNetworkInfo();
		if (info != null && info.isAvailable()) return true;
		return false;
	}

	/**
	 * When the beep has finished playing, rewind to queue up another one.
	 */
	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer)
		{
			mediaPlayer.seekTo(0);
		}
	};

}