package cn.edu.nuc.weibo.parsewb;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import android.text.SpannableStringBuilder;

public class CallbackManager {
	ConcurrentHashMap<String,List<WeiboParseCallback>> callbackMap = null;
	public CallbackManager(){
		callbackMap = new ConcurrentHashMap<String, List<WeiboParseCallback>>();
	}
	public void put(String weibo,WeiboParseCallback weiboParseCallback){
		if (!callbackMap.contains(weibo)) {
			callbackMap.put(weibo, new ArrayList<WeiboParseCallback>());
		}
		callbackMap.get(weibo).add(weiboParseCallback);
		
	}
	public void callback(String weibo,SpannableStringBuilder spannableStringBuilder){
		List<WeiboParseCallback> callbacks = callbackMap.get(weibo);
		if (callbacks != null) {
			for (WeiboParseCallback weiboParseCallback2 : callbacks) {
				weiboParseCallback2.refresh(weibo, spannableStringBuilder);
				
			}
			callbacks.clear();
		}
		callbackMap.remove(weibo);
	}

}
