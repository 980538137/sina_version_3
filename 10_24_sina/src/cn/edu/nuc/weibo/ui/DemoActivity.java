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
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class DemoActivity extends Activity implements OnClickListener{
	private Button btn_auth = null;
	private Button btn_send = null;
	private Button btn_public = null;
	private Button btn_wb = null;
	private EditText et_content = null;
	private TextView tv_content = null;
	private Weibo weibo = null;
	private String token = null;
	//private String expires_in = null;
	private SharedPreferences sp = null;
	private static final String CONSUMER_KEY = "3329619157";
	private static final String CONSUMER_SECRET = "ac02f6f29d103846d59af19eb2242ac9";
	private static final String MREDIRECTURL  = "http://www.baidu.com";
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		weibo = Weibo.getInstance();
        et_content = (EditText) this.findViewById(R.id.et_content);
        btn_auth = (Button) this.findViewById(R.id.btn_oauth);
        btn_wb  = (Button) this.findViewById(R.id.btn_oauth_wb);
        btn_send = (Button) this.findViewById(R.id.btn_send);
        btn_public = (Button) this.findViewById(R.id.btn_get_timeline);
        tv_content = (TextView) this.findViewById(R.id.tv_content);
        btn_auth.setOnClickListener(this);
        btn_wb.setOnClickListener(this);
        btn_send.setOnClickListener(this);
        btn_public.setOnClickListener(this);
        sp = this.getSharedPreferences("token", Context.MODE_PRIVATE);
        token = sp.getString("token", "");
        if (token != null&&!TextUtils.equals(token, "")) {
			btn_auth.setVisibility(View.GONE);
			btn_wb.setVisibility(View.GONE);
			weibo.setAccessToken(new AccessToken(token, CONSUMER_SECRET));
		}
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	// TODO Auto-generated method stub
    	if (resultCode == RESULT_OK) {
			btn_auth.setVisibility(View.GONE);
			btn_wb.setVisibility(View.GONE);
		}
    }
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_oauth:
			weibo.setupConsumerConfig(CONSUMER_KEY, CONSUMER_SECRET);
			weibo.setRedirectUrl(MREDIRECTURL);
			weibo.authorize(DemoActivity.this, new MyWeiboDialogListener());
			break;
		case R.id.btn_oauth_wb:
			weibo.setupConsumerConfig(CONSUMER_KEY, CONSUMER_SECRET);
			weibo.setRedirectUrl(MREDIRECTURL);
			Utility.setAuthorization(new Oauth2AccessTokenHeader());
			startActivityForResult(new Intent(DemoActivity.this,WebViewActivity.class),1);
			break;
		case R.id.btn_send:
			if (weibo.getAccessToken() == null) {
				Toast.makeText(this, "请先进行认证!", Toast.LENGTH_SHORT).show();
			}else {
				Utility.setAuthorization(new Oauth2AccessTokenHeader());
				WeiboParameters bundle = new WeiboParameters();
				bundle.add("source", weibo.getAppKey());
				bundle.add("status", et_content.getText().toString());
				String url = weibo.getSERVER() +"statuses/update.json";
				String msg;
				try {
					msg = weibo.request(this, url, bundle, Utility.HTTPMETHOD_POST, weibo.getAccessToken());
					Toast.makeText(this, "发表成功", Toast.LENGTH_SHORT).show();
					System.out.println(msg);
				} catch (WeiboException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			break;
		case R.id.btn_get_timeline:
			String url = weibo.getSERVER() +"statuses/public_timeline.json";
			try {
				WeiboParameters bundle = new WeiboParameters();
				bundle.add("source", weibo.getAppKey());
				String msg = weibo.request(this, url, bundle, Utility.HTTPMETHOD_GET, weibo.getAccessToken());
				tv_content.setText(msg);
				System.out.println(msg);
			} catch (WeiboException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;

		default:
			break;
		}
	}
	
	class MyWeiboDialogListener implements WeiboDialogListener{

		@Override
		public void onComplete(Bundle values) {
			// TODO Auto-generated method stub
			//获取accesstoken
			token = values.getString("access_token");			
			//expires_in = values.getString("expires_in");
			Editor editor = sp.edit();
			editor.putString("token", token);
			editor.commit();
			AccessToken accessToken = new AccessToken(token, CONSUMER_SECRET);
			//accessToken.setToken(expires_in);
			weibo.setAccessToken(accessToken);
			Toast.makeText(DemoActivity.this, "授权成功！", Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(DemoActivity.this, DemoActivity.class);
			startActivity(intent);
		}

		@Override
		public void onWeiboException(WeiboException e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onError(DialogError e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onCancel() {
			// TODO Auto-generated method stub
			
		}
		
	}
	
}