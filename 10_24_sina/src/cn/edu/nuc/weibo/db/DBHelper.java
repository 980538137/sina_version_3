package cn.edu.nuc.weibo.db;

import cn.edu.nuc.weibo.db.DBInfo.DB;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	// 第三个参数CursorFactory指定在执行查询时获得一个游标实例的工厂类,设置为null,代表使用系统默认的工厂类
	public DBHelper(Context context) {
		super(context, DB.DB_NAME, null, DB.VERSION);
	}

	// 用于初次使用软件时生成数据库表
	// 当调用SQLiteOpenHelper的getWritableDatabase()或者getReadableDatabase()方法获取用于操作数据库的SQLiteDatabase实例的时候，
	// 如果数据库不存在，Android系统会自动生成一个数据库，接着调用onCreate()方法，
	// onCreate()方法在初次生成数据库时才会被调用，
	// 在onCreate()方法里可以生成数据库表结构及添加一些应用使用到的初始化数据
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DBInfo.Table.HOME_TABLE_CREATE);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
