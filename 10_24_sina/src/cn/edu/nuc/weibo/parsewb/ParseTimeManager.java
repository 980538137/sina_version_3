package cn.edu.nuc.weibo.parsewb;

import java.lang.Thread.State;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class ParseTimeManager {
	//阻塞队列，存放微博发布时间信息
	private BlockingQueue<String> timeQueue = null;
	//发布时间解析线程
	private ParseTimeThread thread = null;
	//发布时间的解析回调
	private TimeCallbackManager callbackManager = null;
	
	public ParseTimeManager(){
		timeQueue = new ArrayBlockingQueue<String>(50);
		thread = new ParseTimeThread();
		callbackManager = new TimeCallbackManager();
	}
	//解析微博发布时间
	public void parseTime(String time,ParseTimeCallback parseTimeCallback) throws InterruptedException{
		callbackManager.put(time, parseTimeCallback);
		if (!timeQueue.contains(time)) {
			timeQueue.put(time);			
		}
		State state = thread.getState();
		if (state == State.NEW) {
			thread.start();
		}else if (state == State.TERMINATED) {
			thread = new ParseTimeThread();
			thread.start();
		}
	}
	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			Bundle bundle = msg.getData();
			String timeStr = bundle.getString("timeStr");
			String result = bundle.getString("result");
			//回调刷新
			callbackManager.callback(timeStr, result);
		};
	};
	//微博解析线程
	class ParseTimeThread extends Thread{
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
		SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yy MM dd HH:mm");
		String result;
		@Override
		public void run() {
			while(true){
				String timeStr = timeQueue.poll();
				if (timeStr == null) {
				   break;
				}
				
				try {
					calendar.setTime(simpleDateFormat.parse(timeStr));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				long currentTime = System.currentTimeMillis();
				long publishTime = calendar.getTimeInMillis();
				long time = currentTime - publishTime;
				long second_time = time/1000;
				if (second_time < 60) {
					result = second_time + "秒前";
				}else if (second_time >= 60 && second_time < 3600) {
					result = second_time/1000 + "分钟前";
				}else if (second_time >= 3600 && second_time < 86400) {
					result = time/3600000 + "小时前";
				}else if (second_time >= 86400 && second_time < 172800) {
					result = "昨天";
				}else if (second_time >= 172800 && second_time < 259200) {
					result = "前天";
				}else {
					result = simpleDateFormat2.format(calendar.getTime());
				}
				Message msg = handler.obtainMessage();
				Bundle bundle = msg.getData();
				bundle.putString("timeStr", timeStr);
				bundle.putString("result", result);
				handler.sendMessage(msg);
			}
		}
	}
	//回调接口
	interface ParseTimeCallback{
		public void refresh(String result);
	}
	//回调管理
	class TimeCallbackManager{
		private ConcurrentHashMap<String, List<ParseTimeCallback>> callbackMap = null;
		
		public TimeCallbackManager(){
			callbackMap = new ConcurrentHashMap<String, List<ParseTimeCallback>>();
		}
		
		public void put(String time,ParseTimeCallback parseTimeCallback){
			if (!callbackMap.contains(time)) {
				callbackMap.put(time, new ArrayList<ParseTimeManager.ParseTimeCallback>());
				
			}
			callbackMap.get(time).add(parseTimeCallback);
		}
		public void callback(String timeStr,String result){
			List<ParseTimeCallback> callbacks = callbackMap.get(timeStr);
			if (callbacks != null) {
				for (ParseTimeCallback parseTimeCallback : callbacks) {
					parseTimeCallback.refresh(result);
				}
			    callbacks.clear();
			}
			callbackMap.remove(timeStr);
			
		}
		
		
	}

}
