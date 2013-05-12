package cn.edu.nuc.weibo.bean;

import java.util.Map;

public class Task {
	private int taskId;
	private Map<String, Object> taskParams;
	
	public static final int WEIBO_STATUSES_FRIENDS_TIMELINE = 1;// 获取当前登录用户所关注用户的最新微博
	public static final int WEIBO_USER_INFO = 2;// 获取用户信息
	public static final int WEIBO_STATUSES_UPDATE = 3;//发布一条微博
	public static final int WEIBO_STATUSES_UPLOAD = 4;//发布一条带图片的微博
	public static final int WEIBO_STATUSES_DESTROY = 5;//删除一条微博
	public static final int WEIBO_STATUSES_COMMENT = 6;//评论微博
	public static final int WEIBO_STATUSES_REPOST = 7;//转发微博
	public static final int WEIBO_FAVORITE_CREATE = 8;//收藏微博
	public static final int WEIBO_FAVORITE_DESTROY = 9;//取消收藏微博
	
	public static final int WEIBO_MSG_AT_WB = 10;//获取@当前用户的微博
	public static final int WEIBO_MSG_AT_COMMENT = 11;//获取@当前用户的微博
	public static final int WEIBO_MSG_COMMENT_BY_ME = 12;//获取当前用户收到的评论
	public static final int WEIBO_MSG_COMMENT_TO_ME = 13;//获取当前用户收到的评论
	public static final int WEIBO_MSG_MESSAGE = 14;//当前用户的私信
	public static final int WEIBO_MSG_NOTIFICATION = 15;//当前用户的通知
	
	public static final int WEIBO_EMOTIONS = 16;//获取官方表情
	
	public static final int WEIBO_PLACE_USER_TIMELINE = 17;//获取某个用户的位置动态 
	
	
	public Task(int taskId,Map<String, Object> taskParams){
		this.taskId = taskId;
		this.taskParams = taskParams;
	}
	public int getTaskId() {
		return taskId;
	}
	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}
	public Map<String, Object> getTaskParams() {
		return taskParams;
	}
	public void setTaskParams(Map<String, Object> taskParams) {
		this.taskParams = taskParams;
	}
	

}
