package cn.edu.nuc.weibo.loadimg;

import cn.edu.nuc.weibo.R;
import cn.edu.nuc.weibo.adapter.WeiboAdapter;
import cn.edu.nuc.weibo.app.WeiboApplication;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class SimpleImageLoader {
	/**
	 * 显示微博中图片的缩略图
	 * @param imageView
	 * @param imgURL
	 * @param context
	 * @param flag
	 */
	public static void showImg(final ImageView imageView, String imgURL,
			Context context, int flag) {
		AsyncImageLoader asyncImageLoader = WeiboApplication.asyncImageLoader;
		imageView.setTag(imgURL);
		Bitmap bitmap = asyncImageLoader.get(imgURL,new ImageCallback() {

			@Override
			public void imageLoad(String urlStr, Bitmap bitmap) {
				// TODO Auto-generated method stub
				if (urlStr.equals(imageView.getTag())) {
					imageView.setImageBitmap(bitmap);
				}
			}
		});
		switch (flag) {
		case WeiboAdapter.PORTRAIT_IMG:
			if (bitmap == null) {
				imageView.setImageResource(R.drawable.portrait);
			} else {
				imageView.setImageBitmap(bitmap);
			}
			break;
		case WeiboAdapter.CONTENT_IMG:
			if (bitmap == null) {
				imageView.setImageResource(R.drawable.preview_pic_loading);
			} else {
				imageView.setImageBitmap(bitmap);
			}
			break;
		}
	}
    /**
     * 显示微博中的中等大小图片
     * @param imageView
     * @param imgURL
     * @param pb
     * @param btn
     * @param context
     */
	public static void showWbContentImg(final ImageView imageView,
			final String imgURL, final ProgressBar pb, final Button btn,
			Context context) {
		AsyncImageLoader asyncImageLoader = WeiboApplication.asyncImageLoader;
		imageView.setTag(imgURL);
		Bitmap bitmap = asyncImageLoader.get(imgURL,
				new ImageCallback() {
 
					@Override
					public void imageLoad(String urlStr, Bitmap bitmap) {
						// TODO Auto-generated method stub
						pb.setVisibility(View.GONE);
//						btn.setVisibility(View.VISIBLE);
						imageView.setVisibility(View.VISIBLE);
						if (imgURL.equals(imageView.getTag().toString())) {
							imageView.setImageBitmap(bitmap);
						}

					}
				});
		imageView.setImageBitmap(bitmap);
	}
}
