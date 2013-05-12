package cn.edu.nuc.weibo.ui;

import cn.edu.nuc.weibo.R;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;

public class MainTabActivity extends TabActivity {
	private TabHost tabHost = null;
	private RadioGroup radioGroup = null;
	private RadioButton rb_home = null;
	private static final String TAG_HOME = "home";
	private static final String TAG_MSG = "msg";
	private static final String TAG_MY_INFO = "myinfo";
	private static final String TAG_SEARCH = "search";
	private static final String TAG_MORE = "more";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.maintabs);
		tabHost = this.getTabHost();
		rb_home = (RadioButton) this.findViewById(R.id.rb_home);
		rb_home.setChecked(true);
		//home界面
		TabHost.TabSpec home = tabHost.newTabSpec(TAG_HOME);
		home.setIndicator(TAG_HOME);
		home.setContent(new Intent(this, HomeActivity.class));
        //msg
		TabHost.TabSpec msg = tabHost.newTabSpec(TAG_MSG);
		msg.setIndicator(TAG_MSG);
		msg.setContent(new Intent(this, MsgActivity.class));
        // myinfo
		TabHost.TabSpec myinfo = tabHost.newTabSpec(TAG_MY_INFO);
		myinfo.setIndicator(TAG_MY_INFO);
		myinfo.setContent(new Intent(this, MyInfoActivity.class));
        //search
		TabHost.TabSpec search = tabHost.newTabSpec(TAG_SEARCH);
		search.setIndicator(TAG_SEARCH);
		search.setContent(new Intent(this, SearchActivity.class));
        //more
		TabHost.TabSpec more = tabHost.newTabSpec(TAG_MORE);
		more.setIndicator(TAG_MORE);
		more.setContent(new Intent(this, MoreActivity.class));
        //将每个TabSpec加入到tabhost
		tabHost.addTab(home);
		tabHost.addTab(msg);
		tabHost.addTab(myinfo);
		tabHost.addTab(search);
		tabHost.addTab(more);
		radioGroup = (RadioGroup) this.findViewById(R.id.rg_main_btns);
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.rb_home:
					tabHost.setCurrentTabByTag(TAG_HOME);
					break;
				case R.id.rb_msg:
					tabHost.setCurrentTabByTag(TAG_MSG);
					break;
				case R.id.rb_myinfo:
					tabHost.setCurrentTabByTag(TAG_MY_INFO);
					break;
				case R.id.rb_search:
					tabHost.setCurrentTabByTag(TAG_SEARCH);
					break;
				case R.id.rb_more:
					tabHost.setCurrentTabByTag(TAG_MORE);
					break;

				default:
					break;
				}
			}
		});
	}
	
	
	

}
