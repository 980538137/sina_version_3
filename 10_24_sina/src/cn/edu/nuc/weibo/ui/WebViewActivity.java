package cn.edu.nuc.weibo.ui;

import cn.edu.nuc.weibo.R;

import com.weibo.net.AccessToken;
import com.weibo.net.DialogError;
import com.weibo.net.Oauth2AccessTokenHeader;
import com.weibo.net.Utility;
import com.weibo.net.Weibo;
import com.weibo.net.WeiboDialogListener;
import com.weibo.net.WeiboException;
import com.weibo.net.WeiboParameters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewActivity extends Activity implements WeiboDialogListener {
	private WebView webView = null;
	private ProgressDialog pd = null;
	private WeiboDialogListener dialogListener = null;
	//app_key
	private static final String CONSUMER_KEY = "2905041652";
	//app_secret
	private static final String CONSUMER_SECRET = "7d2f02b441c028a40cd12f06fa0bdf87";
	private static final String MREDIRECTURL = "http://www.sina.com";
	private Weibo weibo = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview);
		dialogListener = this;
		weibo = Weibo.getInstance();
		weibo.setupConsumerConfig(CONSUMER_KEY, CONSUMER_SECRET);
		weibo.setRedirectUrl(MREDIRECTURL);
		Utility.setAuthorization(new Oauth2AccessTokenHeader());
		webView = (WebView) this.findViewById(R.id.wv_id);
		webView.setHorizontalScrollBarEnabled(false);
		webView.setVerticalScrollBarEnabled(false);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebViewClient(new MyWebViewClient());
		webView.loadUrl(getOauthUrl());
		pd = new ProgressDialog(this);
		pd.requestWindowFeature(Window.FEATURE_NO_TITLE);
		pd.setMessage("正在载入...");

	}

	/**
	 * 获取OauthUrl
	 * 
	 * @return
	 */
	@SuppressWarnings("static-access")
	private String getOauthUrl() {
		CookieSyncManager.createInstance(this);
		WeiboParameters parameters = new WeiboParameters();
		parameters.add("client_id", Weibo.getInstance().getAppKey());
		parameters.add("response_type", "token");
		parameters.add("redirect_uri", Weibo.getInstance().getRedirectUrl());
		parameters.add("display", "mobile");
		String url = Weibo.URL_OAUTH2_ACCESS_AUTHORIZE + "?"
				+ Utility.encodeUrl(parameters);

		return url;
	}

	private void handleRedirectUrl(WebView view, String url) {
		Bundle values = Utility.parseUrl(url);

		String error = values.getString("error");
		String error_code = values.getString("error_code");

		if (error == null && error_code == null) {
			dialogListener.onComplete(values);
		} else if (error.equals("access_denied")) {
			// 用户或授权服务器拒绝授予数据访问权限
			dialogListener.onCancel();
		} else {
			dialogListener.onWeiboException(new WeiboException(error, Integer.parseInt(error_code)));
		}
	}

	class MyWebViewClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			// 待后台增加对默认重定向地址的支持后修改下面的逻辑
			String s= Weibo.getInstance().getRedirectUrl();
			if (url.startsWith(Weibo.getInstance().getRedirectUrl())) {
				handleRedirectUrl(view, url);
				return true;
			}
			// launch non-dialog URLs in a full browser
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
			return true;
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {

		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			/**
			 * 点击授权，url正确
			 */
			if (url.startsWith(Weibo.getInstance().getRedirectUrl())) {
				handleRedirectUrl(view, url);
				view.stopLoading();
				return;
			}
			pd.show();
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			pd.dismiss();
		}
	}

	@Override
	public void onComplete(Bundle values) {
		/**
		 * 在这里要save the access_token
		 */
		String token = values.getString("access_token");
        String expires_in = values.getString("expires_in");
        String uid = values.getString("uid");
        long start_time = System.currentTimeMillis();
        //保存登录用户的相关信息
		SharedPreferences preferences = getSharedPreferences("token_expires_in",
				Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString("token", token);
		editor.putString("expires_in", expires_in);
		editor.putString("uid", uid);
		editor.putLong("start_time", start_time);
		editor.commit();

		AccessToken accessToken = new AccessToken(token, Weibo.getAppSecret());
		accessToken.setExpiresIn(expires_in);
		Weibo.getInstance().setAccessToken(accessToken);
		this.startActivity(new Intent(this, MainTabActivity.class));
		this.finish();
	}

	@Override
	public void onWeiboException(WeiboException e) {

	}

	@Override
	public void onError(DialogError e) {

	}

	@Override
	public void onCancel() {

	}

}
