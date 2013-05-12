package cn.edu.nuc.weibo.loadimg;

import android.graphics.Bitmap;
/**
 * 回调接口
 * @author songguoxing
 *
 */
public interface ImageCallback {
	public abstract void imageLoad(String urlStr,Bitmap bitmap);

}
