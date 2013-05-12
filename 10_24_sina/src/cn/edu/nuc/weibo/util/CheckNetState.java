package cn.edu.nuc.weibo.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;

public class CheckNetState {
	public static boolean checkNetworkState(Context context){
		ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		State state_mobile = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
		State state_wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
		if (state_mobile == State.CONNECTED || state_mobile == state_mobile.CONNECTING) {
			return true;
		}else if (state_wifi == State.CONNECTED || state_wifi == state_mobile.CONNECTING) {
			return true;
		}
		return false;
	}

}
