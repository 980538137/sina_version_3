package cn.edu.nuc.weibo.db;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import cn.edu.nuc.weibo.bean.Retweeted_Status;
import cn.edu.nuc.weibo.bean.Status;
import cn.edu.nuc.weibo.bean.User;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class WeiboHomeService {
	private DBHelper dbHelper = null;

	public WeiboHomeService(Context context) {
		dbHelper = new DBHelper(context);
	}

	/**
	 * 插入主页home信息
	 * 
	 * @param status
	 */
	public void insertHomeInfo(List<Status> statuses) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		//db.execSQL("sql");
		if (statuses != null) {
			for (Status status : statuses) {
				ContentValues values = new ContentValues();
				values.put("id", status.getId());
				values.put("mid", status.getMid());
				values.put("portrait", status.getUser().getProfile_image_url());
				values.put("username", status.getUser().getName());
				values.put("wb_time", status.getCreated_at());
				values.put("wb_content", status.getText());
				if (status.getThumbnail_pic() != null) {
					values.put("wb_content_pic", status.getThumbnail_pic());
					values.put("wb_middle_pic", status.getBmiddle_pic());
				}
				values.put("wb_from", status.getSource());
				values.put("wb_redirect", status.getReposts_count());
				values.put("wb_comment", status.getReposts_count());
				values.put("wb_attitude", status.getReposts_count());
				if (status.getRetweeted_Status() != null) {
					values.put("wb_subcontent", status.getRetweeted_Status()
							.getText());
					values.put("wb_subcontent_subpic", status.getRetweeted_Status()
							.getThumbnail_pic());
					values.put("wb_submiddle_subpic", status.getRetweeted_Status()
							.getBmiddle_pic());
					values.put("wb_subfrom", status.getRetweeted_Status()
							.getSource());
					values.put("wb_subredirect", status.getRetweeted_Status()
							.getReposts_count());
					values.put("wb_subcomment", status.getRetweeted_Status()
							.getComments_count());
					values.put("wb_subattitude", status.getRetweeted_Status()
							.getAttitudes_count());
					
				}
				if (status.getUser().isVerified()) {
					values.put("verified", 1);
				}else {
					values.put("verified", 0);
				}
				values.put("verified_type", status.getUser().getVerified_type());
				db.insert(DBInfo.Table.HOME_TABLE, null, values);
			}

			db.close();
		}
		
	}
	
	public void save(Object object) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException{
		
		String sql = "insert into ";
		Class c = object.getClass();
		Field[] fields = c.getDeclaredFields();
		Method[] methods = c.getDeclaredMethods();
		String className = c.getName();
		String tableName = className.substring(className.lastIndexOf(".")+1, className.length());
		sql += tableName + " (";
		for(Field field:fields){
			sql += field.getName() + ",";
		}
		sql = sql.substring(0, sql.length() - 1) + ") values(";
		for(Method method:methods){
			String methodName = method.getName();
			if (methodName.startsWith("get") && !methodName.startsWith("getClass")) {
				Object value = method.invoke(object);
				if (value instanceof String) {
					sql += "\"" + value + "\"";
				}else {
					
				}
			}
		}
		
		
	}
	
	
	public void deleteHomeInfo(){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.delete(DBInfo.Table.HOME_TABLE, null, null);
	}

	/**
	 * 查询主页home的所有信息
	 * 
	 * @return
	 */
	public List<Status> selectHomeInfo() {
		String[] columns = { "id","mid","portrait", "username", "wb_time", "wb_content",
				"wb_content_pic", "wb_middle_pic","wb_from", "wb_redirect", "wb_comment",
				"wb_attitude", "wb_subcontent", "wb_subcontent_subpic","wb_submiddle_subpic",
				"wb_subfrom", "wb_subredirect", "wb_subcomment",
				"wb_subattitude","verified","verified_type"};
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		List<Status> statuses = null;
		Cursor cursor = db.query(DBInfo.Table.HOME_TABLE, columns, null, null,
				null, null, null);
		if (cursor != null && cursor.getCount() > 0) {
			statuses = new ArrayList<Status>();
			Status status = null;
			while (cursor.moveToNext()) {
				status = new Status();
				status.setId(cursor.getLong(cursor.getColumnIndex(columns[0])));
				status.setMid(cursor.getString(cursor.getColumnIndex(columns[1])));
				User user = new User();
				user.setProfile_image_url(cursor.getString(cursor.getColumnIndex(columns[2])));
				user.setName(cursor.getString(cursor.getColumnIndex(columns[3])));
				int verified = cursor.getInt(cursor.getColumnIndex("verified"));
				if (verified == 1) {
					user.setVerified(true);
				}else if (verified == 0) {
					user.setVerified(false);
				}
				user.setVerified_type(cursor.getInt(cursor.getColumnIndex("verified_type")));
				status.setUser(user);
				status.setCreated_at(cursor.getString(cursor
						.getColumnIndex(columns[4])));
				status.setText(cursor.getString(cursor.getColumnIndex(columns[5])));
				
				status.setThumbnail_pic(cursor.getString(cursor
						.getColumnIndex(columns[6])));
				status.setBmiddle_pic(cursor.getString(cursor.getColumnIndex(columns[7])));
				status.setSource(cursor.getString(cursor.getColumnIndex(columns[8])));
				status.setReposts_count(cursor.getInt(cursor
						.getColumnIndex(columns[9])));
				status.setComments_count(cursor.getInt(cursor
						.getColumnIndex(columns[10])));
				status.setAttitudes_count(cursor.getInt(cursor
						.getColumnIndex(columns[11])));

				Retweeted_Status retweeted_Status = new Retweeted_Status();
				retweeted_Status.setText(cursor.getString(cursor
						.getColumnIndex(columns[12])));
				retweeted_Status.setThumbnail_pic(cursor.getString(cursor
						.getColumnIndex(columns[13])));
				retweeted_Status.setBmiddle_pic(cursor.getString(cursor.getColumnIndex(columns[14])));
				retweeted_Status.setSource(cursor.getString(cursor
						.getColumnIndex(columns[15])));
				retweeted_Status.setReposts_count(cursor.getInt(cursor
						.getColumnIndex(columns[16])));
				retweeted_Status.setComments_count(cursor.getInt(cursor
						.getColumnIndex(columns[17])));
				retweeted_Status.setAttitudes_count(cursor.getInt(cursor
						.getColumnIndex(columns[18])));
				if (retweeted_Status.getText() == null) {
					status.setRetweeted_Status(null);
				}else {
					status.setRetweeted_Status(retweeted_Status);
				}
				
				statuses.add(status);
			}
			cursor.close();
			db.close();
			return statuses;
		}
		return null;
	}

}
