package cn.edu.nuc.weibo.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.edu.nuc.weibo.R;
import cn.edu.nuc.weibo.bean.Geos;
import cn.edu.nuc.weibo.bean.Task;
import cn.edu.nuc.weibo.logic.IWeiboActivity;
import cn.edu.nuc.weibo.logic.MainService;
import cn.edu.nuc.weibo.parsewb.EmotionsParse;
import cn.edu.nuc.weibo.parsewb.IDs;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class NewWeiboActivity extends Activity implements IWeiboActivity,
		OnClickListener {
	private static final int MAX_NUM = 140;
	private TextView tv_text_limit = null;
	private EditText et_mblog = null;
	private Button btn_title_back = null;
	private Button btn_title_send = null;
	private LinearLayout ll_text_limit_unit = null;
	private LinearLayout ll_location_loading = null;

	private ImageButton ib_insert_location = null;
	private ImageButton ib_insert_pic = null;
	private ImageButton ib_insert_topic = null;
	private ImageButton ib_insert_at = null;
	private ImageButton ib_insert_face = null;
	private static final int CAMERA_WITH_DATA = 1;
	private static final int PHOTO_PICK_WITH_DATA = 2;
	private ImageView iv_insert_pic = null;
	private Bitmap pick_bitmap = null;
	private Bitmap small_bitmap = null;
	// private Bitmap camera_bitmap = null;
	private String file = null;
	private String path = null;
	private String locationStr = null;
	private String longitude = null;
	private String latitude = null;
	private TextView tv_location = null;

	private static final int LOCATION_ON = 1;
	private static final int LOCATION_OFF = 2;
	private int location_state = LOCATION_OFF;

	private ProgressDialog pd_send = null;

	private GridView gv_newwb_emotions = null;
	private String[] emotionNames = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_weibo);
		path = Environment.getExternalStorageDirectory() + "/temp.jpg";
		
		init();
		
	}

	@Override
	public void init() {
		iv_insert_pic = (ImageView) this.findViewById(R.id.iv_insert_pic);
		tv_text_limit = (TextView) this.findViewById(R.id.tv_text_limit);

		et_mblog = (EditText) this.findViewById(R.id.et_mblog);
		et_mblog.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				gv_newwb_emotions.setVisibility(View.GONE);
				ib_insert_face.setImageResource(R.drawable.ib_insert_face);
				return false;
			}
		});
		et_mblog.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				tv_text_limit.setText(MAX_NUM - s.length() + "");
			}
		});

		tv_location = (TextView) this.findViewById(R.id.tv_location);
		ll_location_loading = (LinearLayout) this
				.findViewById(R.id.ll_location_loading);

		ll_text_limit_unit = (LinearLayout) this
				.findViewById(R.id.ll_text_limit_unit);
		ll_text_limit_unit.setOnClickListener(this);
		btn_title_back = (Button) this.findViewById(R.id.btn_title_back);
		btn_title_back.setOnClickListener(this);
		btn_title_send = (Button) this.findViewById(R.id.btn_title_send);
		btn_title_send.setOnClickListener(this);
		ib_insert_location = (ImageButton) this
				.findViewById(R.id.ib_insert_location);
		ib_insert_location.setOnClickListener(this);
		ib_insert_pic = (ImageButton) this.findViewById(R.id.ib_insert_pic);
		ib_insert_pic.setOnClickListener(this);
		ib_insert_topic = (ImageButton) this.findViewById(R.id.ib_insert_topic);
		ib_insert_topic.setOnClickListener(this);
		ib_insert_at = (ImageButton) this.findViewById(R.id.ib_insert_at);
		ib_insert_at.setOnClickListener(this);
		ib_insert_face = (ImageButton) this.findViewById(R.id.ib_insert_face);
		ib_insert_face.setOnClickListener(this);
        //创建ProgressDialog
		pd_send = new ProgressDialog(this);
		pd_send.setMessage("正在发送...");

		gv_newwb_emotions = (GridView) this
				.findViewById(R.id.gv_newwb_emotions);
		
		gv_newwb_emotions.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				String emotion_name = emotionNames[arg2];
				et_mblog.append(emotion_name);
				
				SpannableStringBuilder builder = parseWbContent(et_mblog.getText().toString());
				et_mblog.setText(builder);
				et_mblog.setSelection(et_mblog.length());
			}
		});
		addEmotions();
		emotionNames = this.getResources().getStringArray(R.array.defualt_emotions);
	}
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
		gv_newwb_emotions.setAdapter(adapter);
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
			if (gv_newwb_emotions.getVisibility() == View.VISIBLE) {
				gv_newwb_emotions.setVisibility(View.GONE);
				ib_insert_face.setImageResource(R.drawable.ib_insert_face);
				return true;
			}
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK) {
			return;
		}
		switch (requestCode) {
		case CAMERA_WITH_DATA:
			file = path;
			small_bitmap = Bitmap.createScaledBitmap(
					BitmapFactory.decodeFile(file), 100, 100, true);
			break;
		case PHOTO_PICK_WITH_DATA:
			Uri selectedImgUri = data.getData();
			if (pick_bitmap != null && !pick_bitmap.isRecycled()) {
				pick_bitmap.recycle();
			}
			if (small_bitmap != null && !small_bitmap.isRecycled()) {
				small_bitmap.recycle();
			}
			if (selectedImgUri != null) {
				try {
					pick_bitmap = BitmapFactory
							.decodeStream(getContentResolver().openInputStream(
									selectedImgUri));
					// 获取多媒体图片的实际路劲
					file = getImagePath(selectedImgUri);
					float width = pick_bitmap.getWidth();
					float height = pick_bitmap.getHeight();
					float scale_width = 50 / width;
					float scale_height = 50 / height;
					Matrix matrix = new Matrix();
					matrix.postScale(scale_width, scale_height);
					small_bitmap = Bitmap.createBitmap(pick_bitmap, 0, 0,
							(int) width, (int) height, matrix, true);
					pick_bitmap.recycle();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
			break;
		}
		iv_insert_pic.setImageBitmap(small_bitmap);
		iv_insert_pic.setVisibility(View.VISIBLE);
	}

	// 获取多媒体图片的实际路劲
	private String getImagePath(Uri uri) {
		String file = null;
		ContentResolver contentResolver = this.getContentResolver();
		Cursor cursor = contentResolver.query(uri, new String[] { "_data" },
				null, null, null);
		if (cursor.moveToNext()) {
			file = cursor.getString(cursor.getColumnIndex("_data"));
		}
		return file;
	}

	@Override
	public void refresh(Object... params) {
		pd_send.dismiss();
		if (params[1].equals("update") || params[1].equals("upload")) {
			Toast.makeText(this, "发送成功", Toast.LENGTH_SHORT).show();
			this.finish();
		} else if (params[1].equals("place")) {
			Geos geos = (Geos) params[0];
			locationStr = geos.getAddress();
			longitude = geos.getLongitude();
			latitude = geos.getLatitude();

			ib_insert_location
					.setImageResource(R.drawable.btn_insert_location_nor_2);
			ll_location_loading.setVisibility(View.GONE);
			tv_location.setVisibility(View.VISIBLE);
			tv_location.setText(locationStr);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_text_limit_unit:
			if (et_mblog.length() > 0) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						NewWeiboActivity.this);
				builder.setTitle("注意");
				builder.setMessage("清除文字?");
				builder.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								et_mblog.setText("");
							}
						});
				builder.setNegativeButton("取消",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});
				builder.create().show();
			}
			break;
		case R.id.btn_title_back:
			finish();
			break;
		case R.id.btn_title_send:
			if (Integer.parseInt((String) tv_text_limit.getText()) < 0) {
				Toast.makeText(NewWeiboActivity.this, "输入字数超出限制",
						Toast.LENGTH_SHORT).show();
			} else if (et_mblog.length() == 0) {
				Toast.makeText(NewWeiboActivity.this, "您还没有输入任何内容",
						Toast.LENGTH_SHORT).show();
			} else {
				pd_send.show();
				Task task = null;
				HashMap<String, Object> taskParams = new HashMap<String, Object>();
				taskParams.put("status", et_mblog.getText().toString());
				if (longitude != null && latitude != null) {
					taskParams.put("lat", latitude);
					taskParams.put("long", longitude);
				}
				if (file != null) {
					taskParams.put("file", file);
					task = new Task(Task.WEIBO_STATUSES_UPLOAD, taskParams);
				} else {
					task = new Task(Task.WEIBO_STATUSES_UPDATE, taskParams);
				}
				MainService.addTask(task);
				MainService.addActivity(NewWeiboActivity.this);
			}
			break;
		case R.id.ib_insert_location:
			if (location_state == LOCATION_OFF) {
				location_state = LOCATION_ON;
				HashMap<String, Object> taskParams = new HashMap<String, Object>();
				SharedPreferences sp = this.getSharedPreferences(
						"token_expires_in", Context.MODE_PRIVATE);
				String uid = sp.getString("uid", "");
				taskParams.put("uid", uid);
				Task task = new Task(Task.WEIBO_PLACE_USER_TIMELINE, taskParams);
				MainService.addTask(task);
				MainService.addActivity(NewWeiboActivity.this);
				ll_location_loading.setVisibility(View.VISIBLE);

			} else if (location_state == LOCATION_ON) {
				location_state = LOCATION_OFF;
				ib_insert_location
						.setImageResource(R.drawable.btn_insert_location_nor);
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("注意");
				builder.setMessage("是否删除地理信息?");
				builder.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								latitude = null;
								longitude = null;
								tv_location.setVisibility(View.GONE);
								ll_location_loading.setVisibility(View.GONE);
							}
						});
				builder.setNegativeButton("取消",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});
				builder.create().show();
			}

			break;
		case R.id.ib_insert_pic:
			AlertDialog.Builder builder = new AlertDialog.Builder(
					NewWeiboActivity.this);
			builder.setTitle("设置");
			builder.setItems(new String[] { "相机拍摄", "手机相册" },
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Intent intent = null;
							switch (which) {
							case 0:
								intent = new Intent(
										MediaStore.ACTION_IMAGE_CAPTURE);
								intent.putExtra(MediaStore.EXTRA_OUTPUT,
										Uri.fromFile(new File(path)));
								startActivityForResult(intent, CAMERA_WITH_DATA);
								break;
							case 1:
								intent = new Intent();
								intent.setType("image/*");
								intent.setAction(Intent.ACTION_GET_CONTENT);
								startActivityForResult(intent,
										PHOTO_PICK_WITH_DATA);
								break;
							}
						}
					});
			builder.create().show();
			break;
		case R.id.ib_insert_topic:
			et_mblog.append("#请输入话题名称#");
			et_mblog.setSelection(et_mblog.length() - 9 + 1,
					et_mblog.length() - 9 + 8);
			break;
		case R.id.ib_insert_at:
			et_mblog.append("@请输入用户名");
			et_mblog.setSelection(et_mblog.length() - 7 + 1, et_mblog.length());
			break;
		case R.id.ib_insert_face:
			InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			if (gv_newwb_emotions.getVisibility() == View.GONE) {
				gv_newwb_emotions.setVisibility(View.VISIBLE);
				ib_insert_face.setImageResource(R.drawable.ib_insert_keyboard);
				manager.hideSoftInputFromWindow(et_mblog.getWindowToken(), 0);
			}else if (gv_newwb_emotions.getVisibility() == View.VISIBLE) {
				gv_newwb_emotions.setVisibility(View.GONE);
				ib_insert_face.setImageResource(R.drawable.ib_insert_face);
				manager.showSoftInput(et_mblog, 0);
			}
			break;
		}
	}
	

}