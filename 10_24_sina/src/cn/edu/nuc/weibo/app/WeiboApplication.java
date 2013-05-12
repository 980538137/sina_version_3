package cn.edu.nuc.weibo.app;

import cn.edu.nuc.weibo.loadimg.AsyncImageLoader;
import cn.edu.nuc.weibo.parsewb.ParseTimeManager;
import cn.edu.nuc.weibo.parsewb.WeiboParseManager;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

public class WeiboApplication extends Application{
	public static AsyncImageLoader asyncImageLoader = null;
	public static Context mContext = null;
	public static WeiboParseManager weiboParseManager = null;
	public static ParseTimeManager parseTimeManager = null;
	public static Handler handler = null;
	public static final int TIME_OUT = 1;
	class MyHandler extends Handler{
		@Override
		public void handleMessage(Message msg) {
			int message = (Integer) msg.obj;
			if (message == TIME_OUT) {
				Toast.makeText(getApplicationContext(), "网络异常，请检查网络配置", Toast.LENGTH_SHORT).show();
			}
		}
	}
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		mContext = this.getApplicationContext();
		asyncImageLoader = new AsyncImageLoader(mContext);
		weiboParseManager = new WeiboParseManager();
		parseTimeManager = new ParseTimeManager();
		handler = new MyHandler();
	}

}
