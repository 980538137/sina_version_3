package cn.edu.nuc.weibo.adapter;

import cn.edu.nuc.weibo.R;
import cn.edu.nuc.weibo.loadimg.SimpleImageLoader;
import cn.edu.nuc.weibo.ui.OriginalPicActivity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ImageView.ScaleType;

public class MyBaseAdapter extends BaseAdapter {
	private Dialog imgDialog = null;
	private View dialog_view = null;
	private ImageView iv_pic = null;
	private ProgressBar pb_pic = null;
	private Context context = null;
	private Button btn_original_pic = null;

	public MyBaseAdapter(Context context) {
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
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return null;
	}

	// 图片点击监听类
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
