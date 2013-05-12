package cn.edu.nuc.weibo.parsewb;

import java.lang.Thread.State;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.edu.nuc.weibo.R;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;

public class WeiboParseManager {
	
	private static final String PATTERN_URL = "http://.* ";//url Pattern
	private static final String PATTERN_TOPIC = "#.+?#";//主题Pattern
	private static final String PATTERN_NAME = "@([\u4e00-\u9fa5A-Za-z0-9_]*)";//名字Pattern
	private static final String PATTERN_EMOTION = "\\[[\u4e00-\u9fa5A-Za-z0-9]*\\]";//表情Pattern
    //阻塞队列，存放微博内容
	private BlockingQueue<String> weiboQueue = null;
    
	private CallbackManager callbackManager = null;
	private ParseThread parseThread = null;
	private EmotionsParse emotionsParse = null;

	public WeiboParseManager() {
		weiboQueue = new ArrayBlockingQueue<String>(50);
		callbackManager = new CallbackManager();
		parseThread = new ParseThread();
		emotionsParse = new EmotionsParse();
	}
    //解析微博内容中的话题，表情，名字，URl
	public SpannableStringBuilder parseWeibo(String weibo,
			WeiboParseCallback callback) throws InterruptedException {
		SpannableStringBuilder builder = new SpannableStringBuilder(weibo);
		callbackManager.put(weibo, callback);

		if (!weiboQueue.contains(weibo)) {
			weiboQueue.put(weibo);
		}
		State state = parseThread.getState();
		if (state == State.NEW) {
			parseThread.start();
		} else if (state == State.TERMINATED) {
			parseThread = new ParseThread();
			parseThread.start();
		}
		return builder;

	}

	private SpannableStringBuilder parseWeibo(String weibo) {
		SpannableStringBuilder builder = new SpannableStringBuilder(weibo);
        replace(PATTERN_NAME, weibo, builder,false);
        replace(PATTERN_URL, weibo, builder,false);
        replace(PATTERN_TOPIC, weibo, builder,false);
        replace(PATTERN_EMOTION, weibo, builder, true);
		return builder;
	}
    //解析微博内容，对相应部分进行替换
	private void replace(String patternStr, String weibo,
			SpannableStringBuilder builder,boolean isEmotion) {
		//存放每条需要解析的内容，和开始，结束位置
		List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		//正则表达式
		Pattern pattern = Pattern.compile(patternStr);
		//进行匹配
		Matcher matcher = pattern.matcher(weibo);
		while (matcher.find()) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("phrase", matcher.group());
			map.put("start", matcher.start());
			map.put("end", matcher.end());
			list.add(map);
		}
		//循环解析
		for (HashMap<String, Object> hashMap : list) {
			if (isEmotion) {//解析表情
				String phrase = (String) hashMap.get("phrase");
				Drawable drawable = emotionsParse.getEmotionByName(phrase);
				if (drawable != null) {
					drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
					ImageSpan imageSpan = new ImageSpan(drawable);
					builder.setSpan(imageSpan, (Integer) hashMap.get("start"), (Integer) hashMap.get("end"), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
			}else {//解析@，话题，URl
				ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(
						R.color.highlight);
				builder.setSpan(foregroundColorSpan,
						(Integer) hashMap.get("start"),
						(Integer) hashMap.get("end"),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			

		}
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			String weibo = (String) msg.getData().getSerializable("weibo");
			SpannableStringBuilder builder = (SpannableStringBuilder) msg
					.getData().get("ssb");
			//回调刷新
			callbackManager.callback(weibo, builder);
		};
	};
    //解析线程
	class ParseThread extends Thread {
		boolean flag = true;

		@Override
		public void run() {
			while (flag) {
				String weibo = weiboQueue.poll();
				if (weibo == null) {
					break;
				}
				SpannableStringBuilder builder = parseWeibo(weibo);
				Message msg = handler.obtainMessage();
				Bundle bundle = msg.getData();
				bundle.putSerializable("weibo", weibo);
				bundle.putCharSequence("ssb", builder);
				handler.sendMessage(msg);
			}

		}
	}

}
