package cn.edu.nuc.weibo.parsewb;

import android.text.SpannableStringBuilder;

public interface WeiboParseCallback {
	public void refresh(String weibo,
			SpannableStringBuilder spannableStringBuilder);

}
