package cn.edu.nuc.weibo.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.json.JSONException;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import cn.edu.nuc.weibo.bean.Comment;
import cn.edu.nuc.weibo.bean.Geos;
import cn.edu.nuc.weibo.bean.Status;
import cn.edu.nuc.weibo.bean.Task;
import cn.edu.nuc.weibo.db.WeiboHomeService;
import cn.edu.nuc.weibo.ui.MsgActivity;
import cn.edu.nuc.weibo.util.WeiboUtils;

import com.weibo.net.Oauth2AccessTokenHeader;
import com.weibo.net.Utility;
import com.weibo.net.Weibo;
import com.weibo.net.WeiboException;

public class MainService extends Service implements Runnable {
	private static Queue<Task> mTasks = new LinkedList<Task>();
	private static List<Activity> mActivities = new ArrayList<Activity>();
	private boolean isRunning = true;
	private Weibo mWeibo = null;

	@Override
	public void onCreate() {
		super.onCreate();
		mWeibo = Weibo.getInstance();
		Utility.setAuthorization(new Oauth2AccessTokenHeader());
		Thread mThread = new Thread(this);
		mThread.start();
	}

	/**
	 * 添加任务
	 * 
	 * @param task
	 */
	public static void addTask(Task task) {
		mTasks.add(task);
	}

	/**
	 * 添加Activity
	 * 
	 * @param activity
	 */
	public static void addActivity(Activity activity) {
		mActivities.add(activity);
	}

	/**
	 * 
	 * 通过名字获取Activity
	 * 
	 * @param name
	 * @return
	 */
	public Activity getActivityByName(String name) {
		if (!mActivities.isEmpty()) {
			for (int i = 0; i < mActivities.size(); i++) {
				Activity activity = mActivities.get(i);
				if (activity.getClass().getName().indexOf(name) > 0) {
					mActivities.remove(i);
					return activity;
				}
			}
		}
		return null;
	}

	@Override
	public void run() {
		while (isRunning) {
			if (!mTasks.isEmpty()) {
				Task task = null;
				task = mTasks.poll();
				if (task != null) {
					try {
						doTask(task);
					} catch (WeiboException e) {
						e.printStackTrace();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * 处理任务
	 * 
	 * @param task
	 * @throws WeiboException
	 * @throws JSONException
	 */
	private void doTask(Task task) throws WeiboException, JSONException {
		Message msg = handler.obtainMessage();
		msg.what = task.getTaskId();
		HashMap<String, Object> mTaskParams = (HashMap<String, Object>) task
				.getTaskParams();
		String return_msg = null;
		List<Status> mStatuses = null;
		List<Comment> mComments = null;
		WeiboHomeService mweiboHomeService = new WeiboHomeService(this);
		switch (task.getTaskId()) {
			case Task.WEIBO_USER_INFO :// 获取用户信息
				return_msg = WeiboUtils.getUserInfo(mWeibo, Weibo.getAppKey());
				msg.obj = return_msg;
				break;
			case Task.WEIBO_STATUSES_FRIENDS_TIMELINE :// 获取当前登录用户所关注用户的最新微博
				mStatuses = WeiboUtils
						.getFriendsTimeLine(task, mweiboHomeService);
				msg.obj = mStatuses;
				break;
			case Task.WEIBO_STATUSES_UPDATE :// 发布一条微博
				return_msg = WeiboUtils.update(mWeibo, Weibo.getAppKey(),
						(HashMap<String, Object>) task.getTaskParams());
				break;
			case Task.WEIBO_STATUSES_UPLOAD :// 发布一条带图片的的微博
				return_msg = WeiboUtils.upload(mWeibo, Weibo.getAppKey(),
						(HashMap<String, Object>) task.getTaskParams());
				break;
			case Task.WEIBO_STATUSES_COMMENT :// 评论微博
				return_msg = WeiboUtils.comment(mWeibo, Weibo.getAppKey(),
						(HashMap<String, Object>) task.getTaskParams());
				break;
			case Task.WEIBO_STATUSES_REPOST :// 转发微博
				return_msg = WeiboUtils.repost(mWeibo, Weibo.getAppKey(),
						(HashMap<String, Object>) task.getTaskParams());
				break;
			case Task.WEIBO_FAVORITE_CREATE :// 收藏微博
				return_msg = WeiboUtils.favorite(mWeibo, Weibo.getAppKey(),
						(HashMap<String, Object>) task.getTaskParams(), true);
				break;
			case Task.WEIBO_FAVORITE_DESTROY :
				return_msg = WeiboUtils.favorite(mWeibo, Weibo.getAppKey(),
						(HashMap<String, Object>) task.getTaskParams(), false);
				break;
			case Task.WEIBO_STATUSES_DESTROY :

				break;
			case Task.WEIBO_MSG_AT_WB :// 获取@当前用户的微博
				mStatuses = WeiboUtils.getStatusesMentions(mWeibo,
						Weibo.getAppKey(), mTaskParams);
				msg.obj = mStatuses;
				break;
			case Task.WEIBO_MSG_AT_COMMENT :// 获取@当前用户的评论
				mComments = WeiboUtils.getCommentsMentions(mWeibo,
						Weibo.getAppKey(), mTaskParams);
				msg.obj = mComments;
				break;
			case Task.WEIBO_MSG_COMMENT_BY_ME :// 获取当前用户收到的评论和发出的评论
				mComments = WeiboUtils.getCommentsByMe(mWeibo, Weibo.getAppKey(),
						(HashMap<String, Object>) task.getTaskParams());
				msg.obj = mComments;
				break;
			case Task.WEIBO_MSG_COMMENT_TO_ME :
				mComments = WeiboUtils.getCommentsToMe(mWeibo, Weibo.getAppKey(),
						mTaskParams);
				msg.obj = mComments;
				break;
			case Task.WEIBO_MSG_MESSAGE :// 当前用户的私信
				break;
			case Task.WEIBO_MSG_NOTIFICATION :// 当前用户的通知
				break;
			case Task.WEIBO_EMOTIONS :
				return_msg = WeiboUtils.getEmotions();
				break;
			case Task.WEIBO_PLACE_USER_TIMELINE :
				mStatuses = WeiboUtils.getPlaceUserTimeline(mWeibo,
						Weibo.getAppKey(), mTaskParams);
				Geos geos = WeiboUtils.getGeoToAddress(mWeibo,
						Weibo.getAppKey(), mStatuses);
				msg.obj = geos;
				break;
		}
		handler.sendMessage(msg);
	}

	Handler handler = new Handler() {
		IWeiboActivity iWeiboActivity = null;

		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case Task.WEIBO_USER_INFO :
					break;
				case Task.WEIBO_STATUSES_FRIENDS_TIMELINE :
					iWeiboActivity = (IWeiboActivity) MainService.this
							.getActivityByName("HomeActivity");
					iWeiboActivity.refresh(msg.obj);
					break;
				case Task.WEIBO_STATUSES_UPDATE :
					iWeiboActivity = (IWeiboActivity) MainService.this
							.getActivityByName("NewWeiboActivity");
					iWeiboActivity.refresh(msg.obj, "update");
					break;
				case Task.WEIBO_STATUSES_UPLOAD :
					iWeiboActivity = (IWeiboActivity) MainService.this
							.getActivityByName("NewWeiboActivity");
					iWeiboActivity.refresh(msg.obj, "upload");
					break;
				case Task.WEIBO_STATUSES_COMMENT :
					iWeiboActivity = (IWeiboActivity) MainService.this
							.getActivityByName("CommentActivity");
					iWeiboActivity.refresh(msg.obj);
					break;
				case Task.WEIBO_FAVORITE_CREATE :
					iWeiboActivity = (IWeiboActivity) MainService.this
							.getActivityByName("DetailWeiboActivity");
					iWeiboActivity.refresh(msg.obj, "create");
					break;
				case Task.WEIBO_FAVORITE_DESTROY :
					iWeiboActivity = (IWeiboActivity) MainService.this
							.getActivityByName("DetailWeiboActivity");
					iWeiboActivity.refresh(msg.obj, "destroy");
					break;
				case Task.WEIBO_MSG_AT_WB :
					iWeiboActivity = (IWeiboActivity) MainService.this
							.getActivityByName("MsgActivity");
					iWeiboActivity.refresh(msg.obj, MsgActivity.AT, "status");
					break;
				case Task.WEIBO_MSG_AT_COMMENT :
					iWeiboActivity = (IWeiboActivity) MainService.this
							.getActivityByName("MsgActivity");
					iWeiboActivity.refresh(msg.obj, MsgActivity.AT, "comment");
					break;
				case Task.WEIBO_MSG_COMMENT_BY_ME :
					iWeiboActivity = (IWeiboActivity) MainService.this
							.getActivityByName("MsgActivity");
					iWeiboActivity.refresh(msg.obj, MsgActivity.COMMENT);
					break;
				case Task.WEIBO_MSG_COMMENT_TO_ME :
					iWeiboActivity = (IWeiboActivity) MainService.this
							.getActivityByName("MsgActivity");
					iWeiboActivity.refresh(msg.obj, MsgActivity.COMMENT);
					break;
				case Task.WEIBO_PLACE_USER_TIMELINE :
					iWeiboActivity = (IWeiboActivity) MainService.this
							.getActivityByName("NewWeiboActivity");
					iWeiboActivity.refresh(msg.obj, "place");
					break;
				case Task.WEIBO_STATUSES_REPOST :
					iWeiboActivity = (IWeiboActivity) MainService.this
							.getActivityByName("RedirectActivity");
					iWeiboActivity.refresh(msg.obj);
					break;
			}
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	/**
	 * 退出系统
	 * 
	 * @param context
	 */
	public static void appExit(Context context) {
		// finish所有的activity
		for (Activity activity : mActivities) {
			if (!activity.isFinishing()) {
				activity.finish();
			}
		}
		// 停止服务
		Intent mService = new Intent("cn.edu.nuc.logic.MainService");
		context.stopService(mService);
	}

}
