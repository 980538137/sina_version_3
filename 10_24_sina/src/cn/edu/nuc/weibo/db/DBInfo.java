package cn.edu.nuc.weibo.db;

public class DBInfo {
	public static class DB {
		public static final String DB_NAME = "sina_weibo";
		public static final int VERSION = 1;

	}

	public static class Table {
		public static final String HOME_TABLE = "home_table";
		public static final String HOME_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS "
				+ HOME_TABLE
				+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT,id LONG,mid TEXT,portrait TEXT,username TEXT,wb_time TEXT,wb_content TEXT,wb_content_pic TEXT,wb_middle_pic TEXT,wb_subcontent TEXT,wb_subcontent_subpic TEXT,wb_submiddle_subpic TEXT,wb_subfrom TEXT,wb_subredirect INTEGER,wb_subcomment INTEGER,wb_subattitude INTEGER,wb_from TEXT,wb_redirect INTEGER,wb_comment INTEGER,wb_attitude INTEGER,verified INTEGER,verified_type INTEGER)";
		
		public static final String HOME_TABLE_DROP = "DROP TABLE" + HOME_TABLE;
	}

}
