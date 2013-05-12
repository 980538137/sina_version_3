package cn.edu.nuc.weibo.loadimg;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import android.graphics.Bitmap;

public class CallbackManager {
	ConcurrentHashMap<String, List<ImageCallback>> callbackMap = null;

	public CallbackManager() {
		callbackMap = new ConcurrentHashMap<String, List<ImageCallback>>();
	}

	public void put(String url, ImageCallback imageCallback) {
		if (!callbackMap.contains(url)) {
			callbackMap.put(url, new ArrayList<ImageCallback>());
		}
		callbackMap.get(url).add(imageCallback);
	}

	public void callback(String urlStr, Bitmap bitmap) {
		List<ImageCallback> callbacks = callbackMap.get(urlStr);
		if (callbacks == null) {
			return;
		}
		for (ImageCallback imageCallback : callbacks) {
			imageCallback.imageLoad(urlStr, bitmap);
		}
		callbacks.clear();
		callbackMap.remove(urlStr);
	}

}
