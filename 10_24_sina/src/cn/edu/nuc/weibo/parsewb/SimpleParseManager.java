package cn.edu.nuc.weibo.parsewb;

import cn.edu.nuc.weibo.app.WeiboApplication;
import cn.edu.nuc.weibo.parsewb.ParseTimeManager.ParseTimeCallback;
import android.text.SpannableStringBuilder;
import android.widget.EditText;
import android.widget.TextView;

public class SimpleParseManager {
	public static void parseWeiboContent(final EditText editText,String wb_content){
		WeiboParseManager weiboParseManager = WeiboApplication.weiboParseManager;
		try {
			editText.setText(weiboParseManager.parseWeibo(wb_content,
					new WeiboParseCallback() {

						@Override
						public void refresh(String weibo,
								SpannableStringBuilder spannableStringBuilder) {
							editText.setText(spannableStringBuilder);
						}
					}));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	//解析微博中的@，话题，表情
	public static void parseWeibo(final TextView textView, String wb_content)
			throws InterruptedException {
		WeiboParseManager weiboParseManager = WeiboApplication.weiboParseManager;
		textView.setText(weiboParseManager.parseWeibo(wb_content,
				new WeiboParseCallback() {

					@Override
					public void refresh(String weibo,
							SpannableStringBuilder spannableStringBuilder) {
						textView.setText(spannableStringBuilder);
					}
				}));
	}
    //解析微博发布时间
	public static void parseTime(final TextView textView, String create_at)
			throws InterruptedException {
		ParseTimeManager parseTimeManager = WeiboApplication.parseTimeManager;
		parseTimeManager.parseTime(create_at, new ParseTimeCallback() {
			@Override
			public void refresh(String result) {
				textView.setText(result);
			}
		});
	}

}
