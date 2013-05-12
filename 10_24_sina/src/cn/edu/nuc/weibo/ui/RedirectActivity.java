package cn.edu.nuc.weibo.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.edu.nuc.weibo.R;
import cn.edu.nuc.weibo.bean.Task;
import cn.edu.nuc.weibo.logic.IWeiboActivity;
import cn.edu.nuc.weibo.logic.MainService;
import cn.edu.nuc.weibo.parsewb.EmotionsParse;
import cn.edu.nuc.weibo.parsewb.IDs;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class RedirectActivity extends Activity implements OnClickListener,IWeiboActivity{
	private static final int MAX_NUM = 140;
	private Button btn_redirect_title_back = null;
	private Button btn_recirect_title_send = null;
	private CheckBox cb_comment_sametime = null;
	private ImageButton ib_redirect_insert_topic = null;
	private ImageButton ib_redirect_insert_at = null;
	private ImageButton ib_redirect_insert_face = null;
	private TextView tv_redirect_text_limit = null;
	private EditText et_redirect_mblog = null;
	private LinearLayout ll_redirect_text_limit_unit = null;
	
	private long id;
	private String status;
	private int is_comment;
	
	private ProgressDialog pd_redirect = null;
	
	private GridView gv_redirect_emotions = null;
	private String[] emotionNames = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weibo_detail_redirect);
		Intent intent = this.getIntent();
		id = intent.getLongExtra("id", 0);
		init();
	}
	//初始化数据
	private void initView(){
		btn_redirect_title_back = (Button) this.findViewById(R.id.btn_redirect_title_back);
		btn_recirect_title_send = (Button) this.findViewById(R.id.btn_redirect_title_send);
		cb_comment_sametime = (CheckBox) this.findViewById(R.id.cb_comment_sametime);
		
		ib_redirect_insert_topic = (ImageButton) this.findViewById(R.id.ib_redirect_insert_topic);
		ib_redirect_insert_at = (ImageButton) this.findViewById(R.id.ib_redirect_insert_at);
		ib_redirect_insert_face = (ImageButton) this.findViewById(R.id.ib_redirect_insert_face);
		tv_redirect_text_limit = (TextView) this.findViewById(R.id.tv_redirect_text_limit);
		et_redirect_mblog = (EditText) this.findViewById(R.id.et_redirect_mblog);
		et_redirect_mblog.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,int count) {}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {}
			@Override
			public void afterTextChanged(Editable s) {
				tv_redirect_text_limit.setText(MAX_NUM - s.length() + "");
			}
		});
		et_redirect_mblog.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				gv_redirect_emotions.setVisibility(View.GONE);
				ib_redirect_insert_face.setImageResource(R.drawable.ib_insert_face);
				return false;
			}
		});
		ll_redirect_text_limit_unit = (LinearLayout) this.findViewById(R.id.ll_redirect_text_limit_unit);
		btn_redirect_title_back.setOnClickListener(this);
		btn_recirect_title_send.setOnClickListener(this);
		ib_redirect_insert_topic.setOnClickListener(this);
		ib_redirect_insert_at.setOnClickListener(this);
		ib_redirect_insert_face.setOnClickListener(this);
		ll_redirect_text_limit_unit.setOnClickListener(this);
		
		pd_redirect = new ProgressDialog(this);
		pd_redirect.setMessage("正在发送...");
		pd_redirect.setCancelable(false);
		
		gv_redirect_emotions = (GridView) this.findViewById(R.id.gv_redirect_emotions);
		gv_redirect_emotions.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				String emotion_name = emotionNames[arg2];
				et_redirect_mblog.append(emotion_name);
				
				SpannableStringBuilder builder = parseWbContent(et_redirect_mblog.getText().toString());
				et_redirect_mblog.setText(builder);
				et_redirect_mblog.setSelection(et_redirect_mblog.length());
			}
		});
		addEmotions();
		emotionNames = this.getResources().getStringArray(R.array.defualt_emotions);
	}
	/**
	 * 添加表情
	 */
	public void addEmotions(){
		ArrayList<HashMap<String, Object>> emotions = new ArrayList<HashMap<String, Object>>();
		
		for (int i = 0; i < IDs.ids.length; i++) {
			HashMap<String, Object> emotion = new HashMap<String, Object>();
			emotion.put("image_item",IDs.ids[i]);
			emotions.add(emotion);
		}
		SimpleAdapter adapter = new SimpleAdapter(this, emotions,
				R.layout.gv_emotions_item, new String[] { "image_item" },
				new int[] { R.id.iv_emotion });
		gv_redirect_emotions.setAdapter(adapter);
	}
	/**
	 * 将微博内容中的表情解析为图片
	 * @param content
	 * @return
	 */
	public SpannableStringBuilder parseWbContent(String content){
		SpannableStringBuilder builder = new SpannableStringBuilder(content);
	    String PATTERN_EMOTION = "\\[[\u4e00-\u9fa5A-Za-z0-9]*\\]";//表情Pattern
	    List<HashMap<String, Object>> list = new ArrayList<HashMap<String,Object>>();
	    EmotionsParse emotionsParse = new EmotionsParse();
	    Pattern pattern = Pattern.compile(PATTERN_EMOTION);
	    Matcher matcher = pattern.matcher(content);
	    while(matcher.find()){
	    	HashMap<String, Object> map = new HashMap<String, Object>();
	    	map.put("phrase", matcher.group());
	    	map.put("start", matcher.start());
	    	map.put("end", matcher.end());
	    	list.add(map);
	    }
	    for (HashMap<String, Object> hashMap : list) {
			String phrase = (String) hashMap.get("phrase");
			Drawable drawable = emotionsParse.getEmotionByName(phrase);
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
			ImageSpan imageSpan = new ImageSpan(drawable);
			builder.setSpan(imageSpan,(Integer) hashMap.get("start"), (Integer) hashMap.get("end"), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		
		return builder;
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			if (gv_redirect_emotions.getVisibility() == View.VISIBLE) {
				gv_redirect_emotions.setVisibility(View.GONE);
				ib_redirect_insert_face.setImageResource(R.drawable.ib_insert_face);
				return true;
			}
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_redirect_title_back:
			this.finish();
			break;
		case R.id.btn_redirect_title_send:
			status = et_redirect_mblog.getText().toString();
			if (TextUtils.isEmpty(status)) {
				status = "转发微博";
			}
			if (cb_comment_sametime.isChecked()) {
				is_comment = 3;
			}else {
				is_comment = 0;
			}
			sendRedirect(id, status, is_comment);
			break;
		case R.id.ib_redirect_insert_topic:
			et_redirect_mblog.append("#请输入话题名称#");
			et_redirect_mblog.setSelection(et_redirect_mblog.length() - 9 + 1,et_redirect_mblog.length() - 9 + 8);
			break;
		case R.id.ib_redirect_insert_at:
			et_redirect_mblog.append("@请输入用户名");
			et_redirect_mblog.setSelection(et_redirect_mblog.length() - 7 + 1,et_redirect_mblog.length());
			break;
		case R.id.ib_redirect_insert_face:
			InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			if (gv_redirect_emotions.getVisibility() == View.GONE) {
				gv_redirect_emotions.setVisibility(View.VISIBLE);
				ib_redirect_insert_face.setImageResource(R.drawable.ib_insert_keyboard);
				manager.hideSoftInputFromWindow(et_redirect_mblog.getWindowToken(), 0);
			}else if (gv_redirect_emotions.getVisibility() == View.VISIBLE) {
				gv_redirect_emotions.setVisibility(View.GONE);
				ib_redirect_insert_face.setImageResource(R.drawable.ib_insert_face);
				manager.showSoftInput(et_redirect_mblog, 0);
			}
			break;
		case R.id.ll_redirect_text_limit_unit:
			if (et_redirect_mblog.length() > 0) {
				AlertDialog.Builder builder = new AlertDialog.Builder(RedirectActivity.this);
				builder.setTitle("注意");
				builder.setMessage("清除文字?");
				builder.setPositiveButton("确定",new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,int which) {
						et_redirect_mblog.setText("");
					}
				});
				builder.setNegativeButton("取消",new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,int which) {
						dialog.dismiss();
					}
				});
				builder.create().show();
			}
			break;
		}
	}
	//初始化数据
	@Override
	public void init() {
		initView();
	}
	//回调刷新界面
	@Override
	public void refresh(Object... params) {
		pd_redirect.dismiss();
		Toast.makeText(this, "转发成功", Toast.LENGTH_SHORT).show();
		this.finish();
	}
	/**
	 * 发送转发信息
	 * @param id
	 * @param status
	 * @param is_comment
	 */
	private void sendRedirect(long id,String status,int is_comment){
		if (Integer.parseInt((String) tv_redirect_text_limit.getText()) < 0) {
			Toast.makeText(RedirectActivity.this, "输入字数超出限制",Toast.LENGTH_SHORT).show();
		} else {
			pd_redirect.show();
			HashMap<String, Object> taskParams = new HashMap<String, Object>();
			taskParams.put("id", id);
			taskParams.put("status", status);
			taskParams.put("is_comment", is_comment);
			Task task = new Task(Task.WEIBO_STATUSES_REPOST, taskParams);
			MainService.addTask(task);
			MainService.addActivity(this);
		}
	}
}
