package cn.edu.nuc.weibo.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class TimeUtil {
	public static String format(String timeStr) throws ParseException{
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
		SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yy MM dd HH:mm");
		calendar.setTime(simpleDateFormat.parse(timeStr));
		long currentTime = System.currentTimeMillis();
		long publishTime = calendar.getTimeInMillis();
		long time = currentTime - publishTime;
		if ((time/1000) < 60) {
			return time/1000 + "秒前";
		}else if ((time/1000) >= 60 && (time/1000) < 60*60) {
			return time/1000/60 + "分钟前";
		}else if ((time/1000) >= 60*60 && (time/1000) < 60*60*24) {
			return time/1000/60/60 + "小时前";
		}else if ((time/1000) >= 60*60*24 && (time/1000) < 2*60*60*24) {
			return "昨天";
		}else if ((time/1000) >= 2*60*60*24 && (time/1000) < 3*60*60*24) {
			return "前天";
		}else {
			return simpleDateFormat2.format(calendar.getTime());
		}
		
	}

}
