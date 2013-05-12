package cn.edu.nuc.weibo.ui;

import cn.edu.nuc.weibo.R;
import cn.edu.nuc.weibo.loadimg.AsyncImageLoader;
import cn.edu.nuc.weibo.loadimg.ImageCallback;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class OriginalPicActivity extends Activity {
	private ImageView iv_original_pic = null;
	private ProgressBar pb_original_pic = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_display);
		//获取上一个activity传过来的URL
		Intent intent = getIntent();
		String original_picURL = intent.getStringExtra("original_picURL");
		
		iv_original_pic = (ImageView) this.findViewById(R.id.iv_original_pic);
		pb_original_pic = (ProgressBar) this.findViewById(R.id.pb_original_pic);
		//异步加载图片
		AsyncImageLoader asyncImageLoader = new AsyncImageLoader(this);
		iv_original_pic.setImageBitmap(asyncImageLoader.get(original_picURL, new ImageCallback() {
			
			@Override
			public void imageLoad(String urlStr, Bitmap bitmap) {
				// TODO Auto-generated method stub
				pb_original_pic.setVisibility(View.GONE);
				iv_original_pic.setImageBitmap(bitmap);
			}
		}));
		
		iv_original_pic.setOnTouchListener(new ImageViewOnTouchListener());
	}
	class ImageViewOnTouchListener implements OnTouchListener{

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:
				
				
				break;
			case MotionEvent.ACTION_POINTER_DOWN:
				
				break;
			case MotionEvent.ACTION_MOVE:
				
				break;
			case MotionEvent.ACTION_POINTER_UP:
				
				break;
			case MotionEvent.ACTION_UP:
				
				break;

			default:
				break;
			}
			return true;
		}
		
	}

}
