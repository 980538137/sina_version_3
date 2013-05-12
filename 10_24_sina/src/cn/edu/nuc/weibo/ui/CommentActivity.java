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
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class CommentActivity extends Activity implements OnClickListener,IWeiboActivity{
	private static final int MAX_NUM = 140;
	private Button btn_comment_title_back = null;
	private Button btn_comment_title_send = null;
	private CheckBox cb_sendwb_sametime = null;
	private ImageButton ib_comment_insert_topic = null;
	private ImageButton ib_comment_insert_at = null;
	private ImageButton ib_comment_insert_face = null;
	private TextView tv_comment_text_limit = null;
	private EditText et_comment_mblog = null;
	private LinearLayout ll_comment_text_limit_unit = null;
	
	private long id;
	
	private ProgressDialog pd_comment = null;
	private GridView gv_comment_emotions = null;
	private String[] emotionNames = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weibo_detail_comment);
		Intent intent = getIntent();
		id = intent.getLongExtra("id", 0);
		init();
	}
	@Override
	public void init() {
		btn_comment_title_back = (Button) this.findViewById(R.id.btn_comment_title_back);
		btn_comment_title_send = (Button) this.findViewById(R.id.btn_comment_title_send);
		cb_sendwb_sametime = (CheckBox) this.findViewById(R.id.cb_sendwb_sametime);
		ib_comment_insert_topic = (ImageButton) this.findViewById(R.id.ib_comment_insert_topic);
		ib_comment_insert_at = (ImageButton) this.findViewById(R.id.ib_comment_insert_at);
		ib_comment_insert_face = (ImageButton) this.findViewById(R.id.ib_comment_insert_face);
		tv_comment_text_limit = (TextView) this.findViewById(R.id.tv_comment_text_limit);
		
		et_comment_mblog = (EditText) this.findViewById(R.id.et_comment_mblog);
		et_comment_mblog.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,int count) {}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {}
			@Override
			public void afterTextChanged(Editable s) {
				tv_comment_text_limit.setText(MAX_NUM - s.length() + "");
			}
		});
		et_comment_mblog.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				gv_comment_emotions.setVisibility(View.GONE);
				ib_comment_insert_face.setImageResource(R.drawable.ib_insert_face);
				return false;
			}
		});
		ll_comment_text_limit_unit = (LinearLayout) this.findViewById(R.id.ll_comment_text_limit_unit);
		btn_comment_title_back.setOnClickListener(this);
		btn_comment_title_send.setOnClickListener(this);
		ib_comment_insert_topic.setOnClickListener(this);
		ib_comment_insert_at.setOnClickListener(this);
		ib_comment_insert_face.setOnClickListener(this);
		ll_comment_text_limit_unit.setOnClickListener(this);
		
		pd_comment = new ProgressDialog(this);
		pd_comment.setMessage("正在发送...");
		pd_comment.setCancelable(false);
		
		gv_comment_emotions = (GridView) this
				.findViewById(R.id.gv_comment_emotions);
		
		gv_comment_emotions.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				String emotion_name = emotionNames[arg2];
				et_comment_mblog.append(emotion_name);
				
				SpannableStringBuilder builder = parseWbContent(et_comment_mblog.getText().toString());
				et_comment_mblog.setText(builder);
				et_comment_mblog.setSelection(et_comment_mblog.length());
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
		gv_comment_emotions.setAdapter(adapter);
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
			if (gv_comment_emotions.getVisibility() == View.VISIBLE) {
				gv_comment_emotions.setVisibility(View.GONE);
				ib_comment_insert_face.setImageResource(R.drawable.ib_insert_face);
				return true;
			}
			break;
		}
		return super.onKeyDown(keyCode, event);
	}
	/**
	 * 回调刷新
	 */
	@Override
	public void refresh(Object... params) {
		pd_comment.dismiss();
		this.finish();
		Toast.makeText(this, "评论成功", Toast.LENGTH_SHORT).show();
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_comment_title_back:
			this.finish();
			break;
		case R.id.btn_comment_title_send:
			if (Integer.parseInt((String) tv_comment_text_limit.getText()) < 0) {
				Toast.makeText(this, "输入字数超出限制",Toast.LENGTH_SHORT).show();
			} else if (et_comment_mblog.length() == 0) {
				Toast.makeText(this, "您还没有输入任何内容",Toast.LENGTH_SHORT).show();
			} else {
				pd_comment.show();
				HashMap<String, Object> taskParams = new HashMap<String, Object>();
				taskParams.put("id", id);
				taskParams.put("comment", et_comment_mblog.getText().toString());
				if (cb_sendwb_sametime.isChecked()) {
					taskParams.put("comment_ori", 1);
				}else {
					taskParams.put("comment_ori", 0);
				}
				Task task = new Task(Task.WEIBO_STATUSES_COMMENT, taskParams);
				MainService.addTask(task);
				MainService.addActivity(this);
			}
			
			break;
		case R.id.ib_comment_insert_topic:
			et_comment_mblog.append("#请输入话题名称#");
			et_comment_mblog.setSelection(et_comment_mblog.length() - 9 + 1,et_comment_mblog.length() - 9 + 8);
			break;
		case R.id.ib_comment_insert_at:
			et_comment_mblog.append("@请输入用户名");
			et_comment_mblog.setSelection(et_comment_mblog.length() - 7 + 1,et_comment_mblog.length());
			break;
		case R.id.ib_comment_insert_face:
			InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			if (gv_comment_emotions.getVisibility() == View.GONE) {
				gv_comment_emotions.setVisibility(View.VISIBLE);
				ib_comment_insert_face.setImageResource(R.drawable.ib_insert_keyboard);
				manager.hideSoftInputFromWindow(et_comment_mblog.getWindowToken(), 0);
			}else if (gv_comment_emotions.getVisibility() == View.VISIBLE) {
				gv_comment_emotions.setVisibility(View.GONE);
				ib_comment_insert_face.setImageResource(R.drawable.ib_insert_face);
				manager.showSoftInput(et_comment_mblog, 0);
			}
			break;
		case R.id.ll_comment_text_limit_unit:
			if (et_comment_mblog.length() > 0) {
				AlertDialog.Builder builder = new AlertDialog.Builder(CommentActivity.this);
				builder.setTitle("注意");
				builder.setMessage("清除文字?");
				builder.setPositiveButton("确定",new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,int which) {
						et_comment_mblog.setText("");
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

}
