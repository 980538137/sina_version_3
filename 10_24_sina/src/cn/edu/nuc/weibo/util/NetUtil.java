package cn.edu.nuc.weibo.util;

import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.drawable.Drawable;

public class NetUtil {
	/**
	 * 获取网络上的图片
	 * @param url
	 * @return
	 */
	public static Drawable getNetImage(String url){
		if (url == null||url == "") {
			return null;
		}
		try {
			URL url2 = new URL(url);
			HttpURLConnection connection = (HttpURLConnection)url2.openConnection();
			return Drawable.createFromStream(connection.getInputStream(), "image");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
