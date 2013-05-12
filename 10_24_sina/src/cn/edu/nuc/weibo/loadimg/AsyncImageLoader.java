package cn.edu.nuc.weibo.loadimg;

import java.lang.Thread.State;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;


public class AsyncImageLoader {
	ImageManager imageManager = null;
	CallbackManager callbackManager = null;
	BlockingQueue<String> urlQueue = null;
	DownloadThread downloadThread = null;
	private static final String EXTRA_IMG_URL = "extra_img_url";
	private static final String EXTRA_IMG = "extra_img";
	public AsyncImageLoader(Context context){
		imageManager = new ImageManager(context);
		callbackManager = new CallbackManager();
		urlQueue = new ArrayBlockingQueue<String>(50);
		downloadThread = new DownloadThread();
	}
	/**
	 * 获取图片
	 * @param urlStr
	 * @param imageCallback
	 * @return
	 */
	public Bitmap get(String urlStr,ImageCallback imageCallback){
		if (imageManager.contains(urlStr)) {
			return imageManager.getFromCache(urlStr);
		}else {
			callbackManager.put(urlStr, imageCallback);
			startDownloadThread(urlStr);
		}
		return null;
	}
	/**
	 * 开始下载线程
	 * @param urlStr
	 */
	private void startDownloadThread(String urlStr){
		if (!urlQueue.contains(urlStr)) {
			try {
				urlQueue.put(urlStr);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		State state = downloadThread.getState();
		if (state == State.NEW) {
			downloadThread.start();
		}else if (state == State.TERMINATED) {
			downloadThread  = new DownloadThread();
			downloadThread.start();
		}
	}
	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			Bundle bundle = msg.getData();
			String urlStr = (String) bundle.getSerializable(EXTRA_IMG_URL);
			Bitmap bitmap = bundle.getParcelable(EXTRA_IMG);
			callbackManager.callback(urlStr, bitmap);
		};
	};
	/**
	 * 下载图片线程
	 * @author songguoxing
	 *
	 */
	private class DownloadThread extends Thread{
		private boolean flag = true;
		@Override
		public void run() {
			try {
				while(flag){
					String urlStr = urlQueue.poll();
					if (urlStr == null) {
						break;
					}
					Bitmap bitmap = imageManager.safeGet(urlStr);
					Message msg = handler.obtainMessage();
					Bundle bundle = msg.getData();
					bundle.putSerializable(EXTRA_IMG_URL, urlStr);
					bundle.putParcelable(EXTRA_IMG, bitmap);
					handler.sendMessage(msg);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally{
				flag = false;
			}
			
		}
	}

}
