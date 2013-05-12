package cn.edu.nuc.weibo.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.edu.nuc.weibo.R;
import cn.edu.nuc.weibo.adapter.WeiboAdapter;
import cn.edu.nuc.weibo.bean.Status;
import cn.edu.nuc.weibo.bean.Task;
import cn.edu.nuc.weibo.logic.IWeiboActivity;
import cn.edu.nuc.weibo.logic.MainService;
import cn.edu.nuc.weibo.view.PullToRefreshListView;
import cn.edu.nuc.weibo.view.PullToRefreshListView.OnRefreshListener;
import cn.edu.nuc.weibo.view.PullToRefreshListView.onLoadOldListener;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

public class HomeActivity extends BaseActivity implements IWeiboActivity {
	private PullToRefreshListView lv_weibo = null;
	// ListView尾部view
//	private View lv_footer_view = null;
	private Button btn_say = null;
	private Button btn_refresh = null;
	private LinearLayout ll_loading = null;
	//private LinearLayout ll_loading_more = null;
	//private TextView tv_more = null;
	private long max_id = 0;
	private WeiboAdapter adapter = null;
	private Animation rotateAnimation = null;

	public static final int INITIATE = 1;
	public static final int MORE_NEW = 2;
	public static final int MORE_OLD = 3;
	private int current_state = INITIATE;
	private List<Status> statuses = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		init();
	}

	@Override
	public void init() {
		// 刷新旋转动画
		rotateAnimation = AnimationUtils.loadAnimation(HomeActivity.this,
				R.anim.refresh_animation);
		// 正在加载布局
		ll_loading = (LinearLayout) this.findViewById(R.id.ll_loading);
		lv_weibo = (PullToRefreshListView) this.findViewById(R.id.lv_weibo);
		lv_weibo.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				current_state = MORE_NEW;
				newTask(current_state, max_id);
			}
		});
		lv_weibo.setOnLoadOldListener(new onLoadOldListener() {
			
			@Override
			public void onLoadOld() {
				current_state = MORE_OLD;
				newTask(current_state, max_id);
			}
		});
		lv_weibo.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Status status = adapter.statuses.get(position - 1);
				Intent intent = new Intent(HomeActivity.this,
						DetailWeiboActivity.class);
				intent.putExtra("status", status);
				HomeActivity.this.startActivity(intent);
			}
		});


		btn_refresh = (Button) this.findViewById(R.id.btn_refresh);
		btn_refresh.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				btn_refresh.setAnimation(rotateAnimation);
				current_state = MORE_NEW;
				newTask(current_state, max_id);
			}
		});
		btn_say = (Button) this.findViewById(R.id.btn_say);
		btn_say.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HomeActivity.this,NewWeiboActivity.class);
				startActivityForResult(intent, 0);
			}
		});
		lv_weibo.setCacheColorHint(Color.WHITE);
		newTask(INITIATE, max_id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void refresh(Object... params) {
		statuses = (ArrayList<Status>) params[0];
		if (statuses != null) {
			max_id = Long.parseLong(statuses.get(statuses.size() - 1).getMid()) - 1;
			switch (current_state) {
			case INITIATE:
				ll_loading.setVisibility(View.GONE);
				adapter = new WeiboAdapter(statuses, this);
				lv_weibo.setAdapter(adapter);
				break;
			case MORE_NEW:
				btn_refresh.clearAnimation();
				adapter = new WeiboAdapter(statuses, this);
				lv_weibo.setAdapter(adapter);
				lv_weibo.onRefreshComplete();
				break;
			case MORE_OLD:
				adapter.refresh(statuses);
				lv_weibo.setSelection(adapter.getCount() - 20);
				lv_weibo.resetFooter();
//				tv_more.setVisibility(View.VISIBLE);
//				ll_loading_more.setVisibility(View.INVISIBLE);
				break;
			}
		}
	}
    /**
     * 新建任务
     * @param current_state
     * @param max_id
     */
	private void newTask(int current_state,long max_id) {
		Task task = null;
		HashMap<String, Object> taskParams = new HashMap<String, Object>();
		switch (current_state) {
		case INITIATE:
			taskParams.put("state", INITIATE);
//	        MainService.addActivity(this);
			this.startService(new Intent(this, MainService.class));
			break;
		case MORE_NEW:
			taskParams.put("state", MORE_NEW);
			break;
		case MORE_OLD:
			taskParams.put("state", MORE_OLD);
			taskParams.put("max_id", String.valueOf(max_id));
			break;
		}
		task = new Task(Task.WEIBO_STATUSES_FRIENDS_TIMELINE, taskParams);
		MainService.addTask(task);
		MainService.addActivity(this);
	}

}
