package cn.edu.nuc.weibo.loadimg;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import cn.edu.nuc.weibo.util.MD5Util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImageManager {
	private Map<String, SoftReference<Bitmap>> imgCache = null;
	private Context context = null;

	public ImageManager(Context context) {
		imgCache = new HashMap<String, SoftReference<Bitmap>>();
		this.context = context;
	}
	public boolean contains(String urlStr){
		return imgCache.containsKey(urlStr);
	}

	/**
	 * 从缓存中获取图片
	 * 
	 * @param url
	 * @return
	 */
	public Bitmap getFromCache(String url) {
		Bitmap bitmap = null;
		bitmap = getFromMapCache(url);
		if (bitmap == null) {
			bitmap = getFromFile(url);
		}
		return bitmap;
	}

	/**
	 * 从内存高速缓存中获取图片
	 * 
	 * @param url
	 * @return
	 */
	public Bitmap getFromMapCache(String url) {
		SoftReference<Bitmap> reference = null;
		Bitmap bitmap = null;
		synchronized (this) {
		    reference = imgCache.get(url);	
		}
		if (reference != null) {
			bitmap = reference.get();
		}
		return bitmap;
	}

	/**
	 * 从文件中获取图片
	 * 
	 * @param url
	 * @return
	 */
	public Bitmap getFromFile(String url) {
		String fileName = MD5Util.getMD5String(url);
		return BitmapFactory.decodeFile(context.getFilesDir()+"/"+fileName);
//		File fileDir = context.getFilesDir();
//		File[] files = fileDir.listFiles();
//		List<String> list = new ArrayList<String>();
//		for (int i = 0; i < files.length; i++) {
//			list.add(files[i].toString());
//		}
//		if (list.contains(fileName)) {
//			System.out.println(fileName);
			
			
//			FileInputStream fis = null;
//			try { 
//				fis = context.openFileInput(fileName);
//				if (fis != null) {
//					return BitmapFactory.decodeStream(fis);
//				}
//			} catch (FileNotFoundException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} finally {
//				if (fis != null) {
//					try {
//						fis.close();
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//			}
		//}
		//return null;
	}
    /**
     * 从网络下载图片
     * @param urlStr
     * @return
     */
	public Bitmap downloadImg(String urlStr) {
		try {
			URL url = new URL(urlStr);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			InputStream is = connection.getInputStream();
			String filename = writeToFile(is, MD5Util.getMD5String(urlStr));
			return BitmapFactory.decodeFile(filename);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}
    /**
     * 将图片写入文件
     * @param is
     * @param urlStr
     * @return
     */
	public String writeToFile(InputStream is, String fileName) {
		FileOutputStream fos = null;
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		try {
			fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
			bis = new BufferedInputStream(is);
			bos = new BufferedOutputStream(fos);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = bis.read(buffer)) != -1) {
				bos.write(buffer, 0, length);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bis != null) {
					bis.close();
				}
				if (bos != null) {
					bos.flush();
					bos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return context.getFilesDir() + "/" + fileName;
	}
	public Bitmap safeGet(String urlStr){
		Bitmap bitmap = getFromFile(urlStr);
		if (bitmap != null) {
			synchronized (this) {
				imgCache.put(urlStr, new SoftReference<Bitmap>(bitmap));
			}
			return bitmap;
		}
		return downloadImg(urlStr);
	}

}
