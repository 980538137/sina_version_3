package cn.edu.nuc.weibo.ui;

import java.util.HashMap;

import cn.edu.nuc.weibo.R;
import cn.edu.nuc.weibo.bean.Retweeted_Status;
import cn.edu.nuc.weibo.bean.Status;
import cn.edu.nuc.weibo.bean.Task;
import cn.edu.nuc.weibo.loadimg.SimpleImageLoader;
import cn.edu.nuc.weibo.logic.IWeiboActivity;
import cn.edu.nuc.weibo.logic.MainService;
import cn.edu.nuc.weibo.parsewb.SimpleParseManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class DetailWeiboActivity extends Activity implements IWeiboActivity,OnClickListener{
	private Button btn_detail_title_back = null;
	private Button btn_detail_title_home = null;
	private ImageView iv_detail_portrait = null;
	private ImageView iv_detail_portrait_v_blue = null;
	//private ImageView iv_detail_portrait_v_red = null;
	private ImageView iv_detail_portrait_v_yellow = null;
	private TextView tv_wb_detail_username = null;
	private TextView tv_wb_detail_content = null;
	private ImageView iv_wb_detail_content_pic = null;
	private TextView tv_wb_detail_subcontent = null;
	private ImageView iv_wb_detail_subcontent_subpic = null;
	private TextView tv_rtwb_detail_redirect = null;
	private TextView tv_rtwb_detail_comment = null;
	private TextView tv_rtwb_detail_attitude = null;
	private TextView tv_wb_detail_time = null;
	private TextView tv_wb_detail_from_where = null;
	private TextView tv_wb_detail_redirect = null;
	private TextView tv_wb_detail_comment = null;
	private TextView tv_wb_detail_attitude = null;
	
	private TextView tvReload = null;
	private TextView tvComment = null;
	private TextView tvRedirect = null;
	private TextView tvFav = null;
	private TextView tvMore = null;

	public static final int PORTRAIT_IMG = 1;
	public static final int CONTENT_IMG = 2;
	
	private boolean isFavorite = true;
	
	private Status status = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weibo_detail);
		Intent intent = this.getIntent();
		status = (Status) intent.getSerializableExtra("status");
		init();
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.btn_detail_title_back:
			this.finish();
			break;
		case R.id.btn_detail_title_home:
			this.finish();
			break;
		case R.id.tvReload:
			
			break;
		case R.id.tvComment:
			intent = new Intent(DetailWeiboActivity.this,CommentActivity.class);
			intent.putExtra("id", status.getId());
			DetailWeiboActivity.this.startActivity(intent);
			break;
		case R.id.tvRedirect:
			intent = new Intent(DetailWeiboActivity.this, RedirectActivity.class);
			intent.putExtra("id", status.getId());
            DetailWeiboActivity.this.startActivity(intent);
			break;
		case R.id.tvFav:
			HashMap<String,Object> taskParams  = new HashMap<String, Object>();
			taskParams.put("id", status.getId());
			if (isFavorite) {
				Task task = new Task(Task.WEIBO_FAVORITE_CREATE, taskParams);
				MainService.addTask(task);
				MainService.addActivity(DetailWeiboActivity.this);
				isFavorite = false;
				
			}else {
				Task task = new Task(Task.WEIBO_FAVORITE_DESTROY, taskParams);
				MainService.addTask(task);
				MainService.addActivity(DetailWeiboActivity.this);
				isFavorite = true;
				
			}
			
			break;
		case R.id.tvMore:
			
			break;
		}
	}

	@Override
	public void init() {
		initView();
	}

	@Override
	public void refresh(Object... params) {
		if ("create".equals(params[1])) {
			Toast.makeText(this, "收藏成功", Toast.LENGTH_SHORT).show();
			tvFav.setText("取消");
		}else if ("destroy".equals(params[1])) {
			Toast.makeText(this, "取消收藏", Toast.LENGTH_SHORT).show();
			tvFav.setText("收藏");
		}
		
	}
	private void initView() {
		btn_detail_title_back = (Button) this
				.findViewById(R.id.btn_detail_title_back);
		btn_detail_title_home = (Button) this
				.findViewById(R.id.btn_detail_title_home);
		iv_detail_portrait = (ImageView) this
				.findViewById(R.id.iv_detail_portrait);
		iv_detail_portrait_v_blue = (ImageView) this
				.findViewById(R.id.iv_detail_portrait_v_blue);
		//iv_detail_portrait_v_red = (ImageView) this
		//		.findViewById(R.id.iv_detail_portrait_v_red);
		iv_detail_portrait_v_yellow = (ImageView) this
				.findViewById(R.id.iv_detail_portrait_v_yellow);
		tv_wb_detail_username = (TextView) this
				.findViewById(R.id.tv_wb_detail_username);
		tv_wb_detail_content = (TextView) this
				.findViewById(R.id.tv_wb_detail_content);
		iv_wb_detail_content_pic = (ImageView) this
				.findViewById(R.id.iv_wb_detail_content_pic);
		tv_wb_detail_subcontent = (TextView) this
				.findViewById(R.id.tv_wb_detail_subcontent);

		tv_rtwb_detail_redirect = (TextView) this
				.findViewById(R.id.tv_rtwb_detail_redirect);
		tv_rtwb_detail_comment = (TextView) this
				.findViewById(R.id.tv_rtwb_detail_comment);
		tv_rtwb_detail_attitude = (TextView) this
				.findViewById(R.id.tv_rtwb_detail_attitude);
		iv_wb_detail_subcontent_subpic = (ImageView) this
				.findViewById(R.id.iv_wb_detail_subcontent_subpic);
		tv_wb_detail_time = (TextView) this
				.findViewById(R.id.tv_wb_detail_time);
		tv_wb_detail_from_where = (TextView) this
				.findViewById(R.id.tv_wb_detail_from_where);
		tv_wb_detail_redirect = (TextView) this
				.findViewById(R.id.tv_wb_detail_redirect);
		tv_wb_detail_comment = (TextView) this
				.findViewById(R.id.tv_wb_detail_comment);
		tv_wb_detail_attitude = (TextView) this
				.findViewById(R.id.tv_wb_detail_attitude);
		tvReload = (TextView) this.findViewById(R.id.tvReload);
		tvComment = (TextView) this.findViewById(R.id.tvComment);
		tvRedirect = (TextView) this.findViewById(R.id.tvRedirect);
		tvFav = (TextView) this.findViewById(R.id.tvFav);
		tvMore = (TextView) this.findViewById(R.id.tvMore);
		
		
		btn_detail_title_back.setOnClickListener(this);
		btn_detail_title_home.setOnClickListener(this);
		tvReload.setOnClickListener(this);
		tvComment.setOnClickListener(this);
		tvRedirect.setOnClickListener(this);
		tvFav.setOnClickListener(this);
		tvMore.setOnClickListener(this);
	
		final String portraitURl = status.getUser().getProfile_image_url();
		SimpleImageLoader.showImg(iv_detail_portrait, portraitURl, this,
				PORTRAIT_IMG);

		if (status.getUser().isVerified()
				&& status.getUser().getVerified_type() == 0) {
			iv_detail_portrait_v_blue.setVisibility(View.GONE);
			iv_detail_portrait_v_yellow.setVisibility(View.VISIBLE);
		}
		if (status.getUser().isVerified()
				&& status.getUser().getVerified_type() != 0) {
			iv_detail_portrait_v_yellow.setVisibility(View.GONE);
			iv_detail_portrait_v_blue.setVisibility(View.VISIBLE);
		}

		tv_wb_detail_username.setText(status.getUser().getName());

		try {
			SimpleParseManager.parseWeibo(tv_wb_detail_content,
					status.getText());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		String imgURl = status.getThumbnail_pic();
		if (imgURl == null) {
			iv_wb_detail_content_pic.setVisibility(View.GONE);
		} else {
			iv_wb_detail_content_pic.setVisibility(View.VISIBLE);
			SimpleImageLoader.showImg(iv_wb_detail_content_pic, imgURl, this,
					CONTENT_IMG);
			// iv_wb_detail_content_pic
			// .setOnClickListener(new PicOnClickListener(status
			// .getBmiddle_pic(), status.getOriginal_pic()));

		}

		Retweeted_Status retweeted_Status = status.getRetweeted_Status();
		if (retweeted_Status != null) {

			this.findViewById(R.id.retweeted_layout)
					.setVisibility(View.VISIBLE);
			tv_wb_detail_subcontent.setText(status.getRetweeted_Status()
					.getText());
			try {
				SimpleParseManager.parseWeibo(tv_wb_detail_subcontent, status
						.getRetweeted_Status().getText());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			String sub_imgURl = retweeted_Status.getThumbnail_pic();

			if (sub_imgURl != null) {
				iv_wb_detail_subcontent_subpic.setVisibility(View.VISIBLE);
				iv_wb_detail_subcontent_subpic
						.setImageResource(R.drawable.preview_pic_loading);
				SimpleImageLoader.showImg(iv_wb_detail_subcontent_subpic,
						sub_imgURl, this, CONTENT_IMG);
				// iv_wb_detail_subcontent_subpic
				// .setOnClickListener(new PicOnClickListener(status
				// .getBmiddle_pic(), status.getOriginal_pic()));

			} else {
				iv_wb_detail_subcontent_subpic.setVisibility(View.GONE);
			}
			tv_rtwb_detail_redirect.setText(status.getRetweeted_Status()
					.getReposts_count() + "");
			tv_rtwb_detail_comment.setText(status.getRetweeted_Status()
					.getComments_count() + "");
			tv_rtwb_detail_attitude.setText(status.getRetweeted_Status()
					.getAttitudes_count() + "");
		} else {
			this.findViewById(R.id.retweeted_layout).setVisibility(View.GONE);
		}
		try {
			SimpleParseManager.parseTime(tv_wb_detail_time,
					status.getCreated_at());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		tv_wb_detail_from_where.setText(Html.fromHtml(status.getSource()));
		tv_wb_detail_redirect.setText(status.getReposts_count() + "");
		tv_wb_detail_comment.setText(status.getComments_count() + "");
		tv_wb_detail_attitude.setText(status.getAttitudes_count() + "");

	}

}
