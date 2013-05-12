package cn.edu.nuc.weibo.adapter;

import java.util.List;

import cn.edu.nuc.weibo.R;
import cn.edu.nuc.weibo.bean.Retweeted_Status;
import cn.edu.nuc.weibo.bean.Status;
import cn.edu.nuc.weibo.loadimg.SimpleImageLoader;
import cn.edu.nuc.weibo.parsewb.SimpleParseManager;
import cn.edu.nuc.weibo.ui.OriginalPicActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.text.Html;
import android.util.FloatMath;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;

public class WeiboAdapter extends BaseAdapter {
	public List<Status> statuses = null;
	private LayoutInflater inflater = null;
	private Context context = null;
	public static final int PORTRAIT_IMG = 1;
	public static final int CONTENT_IMG = 2;

	private Dialog imgDialog = null;
	private View dialog_view = null;
	private ImageView iv_pic = null;
	private ProgressBar pb_pic = null;
	private Button btn_original_pic = null;

	public WeiboAdapter(List<Status> statuses, Context context) {
		super();
		this.statuses = statuses;
		inflater = LayoutInflater.from(context);
		this.context = context;

		imgDialog = new Dialog(context, R.style.image_dialog);
		dialog_view = View.inflate(context, R.layout.image_dialog, null);
		iv_pic = (ImageView) dialog_view.findViewById(R.id.iv_pic);
		pb_pic = (ProgressBar) dialog_view.findViewById(R.id.pb_pic);
		btn_original_pic = (Button) dialog_view
				.findViewById(R.id.btn_orignal_pic);
		imgDialog.setContentView(dialog_view);
	}

	@Override
	public int getCount() {
		return statuses == null ? null : statuses.size();
	}

	@Override
	public Object getItem(int position) {
		return statuses == null ? null : statuses.get(position);
	}

	@Override
	public long getItemId(int position) {
		return statuses == null ? null : statuses.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return createViewFromResource(position, convertView);
	}

	private View createViewFromResource(int position, View convertView) {
		WeiboHolder weiboHolder = null;
		Status status = statuses.get(position);
		if (null == convertView) {
			convertView = inflater.inflate(R.layout.weibo_item, null);
			weiboHolder = new WeiboHolder();
			findViews(weiboHolder, convertView);
			convertView.setTag(weiboHolder);
		} else {
			weiboHolder = (WeiboHolder) convertView.getTag();
		}
		setValues(status, weiboHolder, convertView);

		return convertView;
	}

	private void findViews(WeiboHolder weiboHolder, View convertView) {
		weiboHolder.iv_portrait = (ImageView) convertView
				.findViewById(R.id.iv_portrait);
		weiboHolder.iv_portrait_v_blue = (ImageView) convertView
				.findViewById(R.id.iv_portrait_v_blue);
		weiboHolder.iv_portrait_v_red = (ImageView) convertView
				.findViewById(R.id.iv_portrait_v_red);
		weiboHolder.iv_portrait_v_yellow = (ImageView) convertView
				.findViewById(R.id.iv_portrait_v_yellow);
		weiboHolder.tv_wb_username = (TextView) convertView
				.findViewById(R.id.tv_wb_username);
		weiboHolder.iv_wb_crown = (ImageView) convertView
				.findViewById(R.id.iv_wb_crown);
		weiboHolder.tv_wb_time = (TextView) convertView
				.findViewById(R.id.tv_wb_time);
		weiboHolder.tv_wb_item_content = (TextView) convertView
				.findViewById(R.id.tv_wb_item_content);
		weiboHolder.iv_wb_item_content_pic = (ImageView) convertView
				.findViewById(R.id.iv_wb_item_content_pic);
		weiboHolder.tv_wb_item_subcontent = (TextView) convertView
				.findViewById(R.id.tv_wb_item_subcontent);
		weiboHolder.iv_wb_item_subcontent_subpic = (ImageView) convertView
				.findViewById(R.id.iv_wb_item_subcontent_subpic);
		weiboHolder.tv_wb_item_from_where = (TextView) convertView
				.findViewById(R.id.tv_wb_item_from_where);
		weiboHolder.tv_wb_item_redirect = (TextView) convertView
				.findViewById(R.id.tv_wb_item_redirect);
		weiboHolder.tv_wb_item_comment = (TextView) convertView
				.findViewById(R.id.tv_wb_item_comment);
		weiboHolder.tv_wb_item_attitude = (TextView) convertView
				.findViewById(R.id.tv_wb_item_attitude);
	}

	private void setValues(Status status, WeiboHolder weiboHolder,
			View convertView) {
		final String portraitURl = status.getUser().getProfile_image_url();
		SimpleImageLoader.showImg(weiboHolder.iv_portrait, portraitURl,
				context, PORTRAIT_IMG);

		if (status.getUser().isVerified()
				&& status.getUser().getVerified_type() == 0) {
			weiboHolder.iv_portrait_v_red.setVisibility(View.GONE);
			weiboHolder.iv_portrait_v_blue.setVisibility(View.GONE);
			weiboHolder.iv_portrait_v_yellow.setVisibility(View.VISIBLE);
		}else if (status.getUser().isVerified()
				&& status.getUser().getVerified_type() != 0) {
			weiboHolder.iv_portrait_v_red.setVisibility(View.GONE);
			weiboHolder.iv_portrait_v_yellow.setVisibility(View.GONE);
			weiboHolder.iv_portrait_v_blue.setVisibility(View.VISIBLE);
		}else {
			weiboHolder.iv_portrait_v_red.setVisibility(View.GONE);
			weiboHolder.iv_portrait_v_yellow.setVisibility(View.GONE);
			weiboHolder.iv_portrait_v_blue.setVisibility(View.GONE);
		}

		weiboHolder.tv_wb_username.setText(status.getUser().getName());

		try {
			SimpleParseManager.parseWeibo(weiboHolder.tv_wb_item_content,
					status.getText());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		try {
			SimpleParseManager.parseTime(weiboHolder.tv_wb_time, status.getCreated_at());
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		String imgURl = status.getThumbnail_pic();
		if (imgURl == null) {
			weiboHolder.iv_wb_item_content_pic.setVisibility(View.GONE);
		} else {
			weiboHolder.iv_wb_item_content_pic.setVisibility(View.VISIBLE);
			SimpleImageLoader.showImg(weiboHolder.iv_wb_item_content_pic,
					imgURl, context, CONTENT_IMG);
			weiboHolder.iv_wb_item_content_pic
					.setOnClickListener(new PicOnClickListener(status
							.getBmiddle_pic(), status.getOriginal_pic()));

		}

		Retweeted_Status retweeted_Status = status.getRetweeted_Status();
		if (retweeted_Status != null) {

			convertView.findViewById(R.id.retweeted_layout).setVisibility(
					View.VISIBLE);
			try {
				SimpleParseManager.parseWeibo(
						weiboHolder.tv_wb_item_subcontent, status
								.getRetweeted_Status().getText());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			String sub_imgURl = retweeted_Status.getThumbnail_pic();

			if (sub_imgURl != null) {
				weiboHolder.iv_wb_item_subcontent_subpic
						.setImageResource(R.drawable.preview_pic_loading);
				SimpleImageLoader.showImg(
						weiboHolder.iv_wb_item_subcontent_subpic, sub_imgURl,
						context, CONTENT_IMG);
				weiboHolder.iv_wb_item_subcontent_subpic
						.setOnClickListener(new PicOnClickListener(status.getRetweeted_Status().
								getBmiddle_pic(), status.getRetweeted_Status().getOriginal_pic()));

			} else {
				weiboHolder.iv_wb_item_subcontent_subpic
						.setVisibility(View.GONE);
			}
		} else {
			convertView.findViewById(R.id.retweeted_layout).setVisibility(
					View.GONE);
		}
		weiboHolder.tv_wb_item_from_where.setText(Html.fromHtml(status
				.getSource()));
		weiboHolder.tv_wb_item_redirect.setText(status.getReposts_count() + "");
		weiboHolder.tv_wb_item_comment.setText(status.getComments_count() + "");
		weiboHolder.tv_wb_item_attitude.setText(status.getAttitudes_count()
				+ "");
	}

	private class WeiboHolder {
		ImageView iv_portrait;
		ImageView iv_portrait_v_blue;
		ImageView iv_portrait_v_red;
		ImageView iv_portrait_v_yellow;
		TextView tv_wb_username;
		ImageView iv_wb_crown;
		TextView tv_wb_time;
		TextView tv_wb_item_content;
		ImageView iv_wb_item_content_pic;
		TextView tv_wb_item_subcontent;
		ImageView iv_wb_item_subcontent_subpic;
		TextView tv_wb_item_from_where;
		TextView tv_wb_item_redirect;
		TextView tv_wb_item_comment;
		TextView tv_wb_item_attitude;
	}
    //在加载更多微博时，调用此方法更新adapter
	public void refresh(List<Status> new_statuses) {
		statuses.addAll(new_statuses);
		this.notifyDataSetChanged();

	}
    //图片点击监听类
	class PicOnClickListener implements OnClickListener {
		String bmiddle_picURL = null;
		String original_picURL = null;

		public PicOnClickListener(String bmiddle_picURL, String original_picURL) {
			this.bmiddle_picURL = bmiddle_picURL;
			this.original_picURL = original_picURL;
		}

		@Override
		public void onClick(View v) {
			iv_pic.setOnTouchListener(new ImageViewOnTouchListener());
			pb_pic.setVisibility(View.VISIBLE);
			btn_original_pic.setVisibility(View.GONE);
			// btn_original_pic.setOnClickListener(new
			// OriginalPicOnclickListener(
			// original_picURL));
			imgDialog.show();
			SimpleImageLoader.showWbContentImg(iv_pic, bmiddle_picURL, pb_pic,
					btn_original_pic, context);
		}

	}

	class OriginalPicOnclickListener implements OnClickListener {
		String original_picURL = null;

		public OriginalPicOnclickListener(String original_picURL) {
			this.original_picURL = original_picURL;
		}

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(context, OriginalPicActivity.class);
			intent.putExtra("original_picURL", original_picURL);
			context.startActivity(intent);

		}
	}

	// 图片拖拽，缩放监听类
	class ImageViewOnTouchListener implements OnTouchListener {
		private Matrix matrix = new Matrix();
		private Matrix savedMatrix = new Matrix();

		private PointF start = new PointF();
		private PointF mid = new PointF();

		private static final int NONE = 1;
		private static final int DRAG = 2;
		private static final int ZOOM = 3;
		private int mode = NONE;

		private float oldDistance;
		private float newDistance;

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			ImageView imageView = (ImageView) v;
			System.out.println(imageView.getImageMatrix().toString());
			imageView.setScaleType(ScaleType.MATRIX);
			switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:
				start.set(event.getX(), event.getY());
				matrix.postScale(1, 1);
				savedMatrix.set(matrix);
				mode = DRAG;
				break;
			case MotionEvent.ACTION_POINTER_DOWN:
				oldDistance = spacing(event);
				if (oldDistance > 10) {
					savedMatrix.set(matrix);
					midPoint(mid, event);
					mode = ZOOM;
				}
				break;
			case MotionEvent.ACTION_MOVE:
				if (mode == DRAG) {
					matrix.set(savedMatrix);
					matrix.postTranslate(event.getX() - start.x, event.getY()
							- start.y);

				} else if (mode == ZOOM) {
					newDistance = spacing(event);
					if (newDistance > 10) {
						float scale = newDistance / oldDistance;
						
						matrix.set(savedMatrix);
						matrix.postScale(scale, scale, mid.x, mid.y);

					}
				}
				break;
			case MotionEvent.ACTION_POINTER_UP:
				mode = NONE;
				break;
			case MotionEvent.ACTION_UP:

				break;

			default:
				break;
			}
			imageView.setImageMatrix(matrix);
			return true;
		}

	}

	// 计算两点间的距离
	private float spacing(MotionEvent event) {
		float x = event.getX(1) - event.getX(0);
		float y = event.getY(1) - event.getY(0);
		return FloatMath.sqrt(x * x + y * y);
	}

	// 计算两点间的中点
	private void midPoint(PointF pointF, MotionEvent event) {
		float x = event.getX(1) + event.getX(0);
		float y = event.getY(1) + event.getY(0);
		pointF.set(x / 2, y / 2);
	}
}
