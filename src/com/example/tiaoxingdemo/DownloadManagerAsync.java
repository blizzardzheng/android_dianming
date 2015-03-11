package com.example.tiaoxingdemo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.protocol.HTTP;

import android.os.Message;
import android.util.Log;

public class DownloadManagerAsync
{
	public DownloadManagerAsync()
	{

	}

	// 实例化自定义的 Handler
//	EventHandler mHandler = new EventHandler(this);

	// 按指定 url 地址下载文件到指定路径
	public void download(final String url, final String savePath)
	{
		new Thread(new Runnable() {
			public void run()
			{
				try
				{
					sendMessage(FILE_DOWNLOAD_CONNECT);
					URL sourceUrl = new URL(url);
					URLConnection conn = sourceUrl.openConnection();
					InputStream inputStream = conn.getInputStream();

					int fileSize = conn.getContentLength();

					File savefile = new File(savePath);
					if (savefile.exists())
					{
						savefile.delete();
					}
					savefile.createNewFile();

					FileOutputStream outputStream = new FileOutputStream(
							savePath, true);

					byte[] buffer = new byte[1024];
					int readCount = 0;
					int readNum = 0;
					int prevPercent = 0;
					while (readCount < fileSize && readNum != -1)
					{
						readNum = inputStream.read(buffer);
						if (readNum > -1)
						{
						//	outputStream.write(buffer);
							outputStream.write(buffer,0,readNum);
							readCount = readCount + readNum;

							int percent = (int) (readCount * 100 / fileSize);
							if (percent > prevPercent)
							{
								// 发送下载进度信息
								sendMessage(FILE_DOWNLOAD_UPDATE, percent,
										readCount);

								prevPercent = percent;
							}
						}
					}

					outputStream.close();
					sendMessage(FILE_DOWNLOAD_COMPLETE, savePath);

				}
				catch (Exception e)
				{
					sendMessage(FILE_DOWNLOAD_ERROR, e);
					Log.e("MyError", e.toString());
				}
			}
		}).start();
	}

	// 读取指定 url 地址的响应内容
	public void download(final String url)
	{
		new Thread(new Runnable() {
			public void run()
			{
				try
				{
					sendMessage(FILE_DOWNLOAD_CONNECT);

					Log.d("DownloadManagerAsync", "download text  " + url);

					URL sourceUrl = new URL(url);
					URLConnection conn = sourceUrl.openConnection();
//					conn.setConnectTimeout(60 * 1000);
//					conn.setReadTimeout(20 * 1000);
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(conn.getInputStream(),
									HTTP.UTF_8));

					String line = null;
					StringBuffer content = new StringBuffer();
					while ((line = reader.readLine()) != null)
					{
						content.append(line);
					}

					reader.close();

					sendMessage(FILE_DOWNLOAD_COMPLETE, content.toString());

				}
				catch (Exception e)
				{
					sendMessage(FILE_DOWNLOAD_ERROR, e);
					Log.e("MyError", e.toString());
				}
			}
		}).start();
	}

	// 向 Handler 发送消息
	private void sendMessage(int what, Object obj)
	{
		// 构造需要向 Handler 发送的消息
//		Message msg = mHandler.obtainMessage(what, obj);
		// 发送消息
//		mHandler.sendMessage(msg);
	}

	private void sendMessage(int what)
	{
//		Message msg = mHandler.obtainMessage(what);
//		mHandler.sendMessage(msg);
	}

	private void sendMessage(int what, int arg1, int arg2)
	{
//		Message msg = mHandler.obtainMessage(what, arg1, arg2);
//		mHandler.sendMessage(msg);
	}

	private static final int FILE_DOWNLOAD_CONNECT = 0;
	private static final int FILE_DOWNLOAD_UPDATE = 1;
	private static final int FILE_DOWNLOAD_COMPLETE = 2;
	private static final int FILE_DOWNLOAD_ERROR = -1;

	// 自定义的 Handler
//	private class EventHandler extends Handler
//	{
//		private DownloadManagerAsync mManager;
//
//		public EventHandler(DownloadManagerAsync manager)
//		{
//			mManager = manager;
//		}

		// 处理接收到的消息
		public void handleMessage(Message msg)
		{

			switch (msg.what)
			{
			case FILE_DOWNLOAD_CONNECT:
//				if (mOnDownloadConnectListener != null) mOnDownloadConnectListener
//						.onDownloadConnect(mManager);
				break;
			case FILE_DOWNLOAD_UPDATE:
//				if (mOnDownloadUpdateListener != null) mOnDownloadUpdateListener
//						.onDownloadUpdate(mManager, msg.arg1);
				break;
			case FILE_DOWNLOAD_COMPLETE:
//				if (mOnDownloadCompleteListener != null) mOnDownloadCompleteListener
//						.onDownloadComplete(mManager, msg.obj);
				break;
			case FILE_DOWNLOAD_ERROR:

				// Exception e = (Exception)msg.obj.toString();
				// Toast.makeText( MyApplication.getAppContext() ,
				// msg.obj.toString(), Toast.LENGTH_LONG).show();
				// Toast.makeText( MyApplication.getAppContext() ,
				// R.string.nonetworkerror , Toast.LENGTH_LONG).show();
//
//				if (mOnDownloadErrorListener != null) mOnDownloadErrorListener
//						.onDownloadError(mManager, (Exception) msg.obj);
//				else
//					Log.d("DownloadManager", "mOnDownloadErrorListener==null");
				break;
			default:
				break;
			}
		}
//	}

	// 定义连接事件
	private OnDownloadConnectListener mOnDownloadConnectListener;

	public interface OnDownloadConnectListener
	{
		void onDownloadConnect(DownloadManagerAsync manager);
	}

	public void setOnDownloadConnectListener(OnDownloadConnectListener listener)
	{
		mOnDownloadConnectListener = listener;
	}

	// 定义下载进度更新事件
	private OnDownloadUpdateListener mOnDownloadUpdateListener;

	public interface OnDownloadUpdateListener
	{
		void onDownloadUpdate(DownloadManagerAsync manager, int percent);
	}

	public void setOnDownloadUpdateListener(OnDownloadUpdateListener listener)
	{
		mOnDownloadUpdateListener = listener;
	}

	// 定义下载完成事件
	private OnDownloadCompleteListener mOnDownloadCompleteListener;

	public interface OnDownloadCompleteListener
	{
		void onDownloadComplete(DownloadManagerAsync manager, Object result);
	}

	public void setOnDownloadCompleteListener(
			OnDownloadCompleteListener listener)
	{
		mOnDownloadCompleteListener = listener;
	}

	// 定义下载异常事件
	private OnDownloadErrorListener mOnDownloadErrorListener;

	public interface OnDownloadErrorListener
	{
		void onDownloadError(DownloadManagerAsync manager, Exception e);
	}

	public void setOnDownloadErrorListener(OnDownloadErrorListener listener)
	{
		mOnDownloadErrorListener = listener;
	}
}
