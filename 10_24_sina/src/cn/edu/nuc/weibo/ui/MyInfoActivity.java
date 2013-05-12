package cn.edu.nuc.weibo.ui;

import cn.edu.nuc.weibo.R;
import cn.edu.nuc.weibo.logic.IWeiboActivity;
import android.os.Bundle;

public class MyInfoActivity extends BaseActivity implements IWeiboActivity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.myinfo);
	}

	@Override
	public void init() {
		
	}

	@Override
	public void refresh(Object... params) {
		
	}

}
