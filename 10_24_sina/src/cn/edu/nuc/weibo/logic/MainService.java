package cn.edu.nuc.weibo.logic;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.json.JSONException;

import com.weibo.net.Oauth2AccessTokenHeader;
import com.weibo.net.Utility;
import com.weibo.net.Weibo;
import com.weibo.net.WeiboException;
import com.weibo.net.WeiboParameters;

import cn.edu.nuc.weibo.bean.Comment;
import cn.edu.nuc.weibo.bean.Geo;
import cn.edu.nuc.weibo.bean.Geos;
import cn.edu.nuc.weibo.bean.Status;
import cn.edu.nuc.weibo.bean.Task;
import cn.edu.nuc.weibo.db.WeiboHomeService;
import cn.edu.nuc.weibo.ui.HomeActivity;
import cn.edu.nuc.weibo.ui.MsgActivity;
import cn.edu.nuc.weibo.util.JsonUtils;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;

public class MainService extends Service implements Runnable {
	private static Queue<Task> tasks = new LinkedList<Task>();
	private static List<Activity> activities = new ArrayList<Activity>();
	private boolean isRunning = true;
	private Weibo weibo = null;

	@Override
	public void onCreate() {
		super.onCreate();
		weibo = Weibo.getInstance();
		Utility.setAuthorization(new Oauth2AccessTokenHeader());
		Thread thread = new Thread(this);
		thread.start();
	}

	/**
	 * 添加任务
	 * 
	 * @param task
	 */
	public static void addTask(Task task) {
		tasks.add(task);
	}

	/**
	 * 添加Activity
	 * 
	 * @param activity
	 */
	public static void addActivity(Activity activity) {
		activities.add(activity);
	}

	/**
	 * 
	 * 通过名字获取Activity
	 * 
	 * @param name
	 * @return
	 */
	public Activity getActivityByName(String name) {
		if (!activities.isEmpty()) {
			for (int i = 0; i < activities.size(); i++) {
				Activity activity = activities.get(i);
				if (activity.getClass().getName().indexOf(name) > 0) {
					activities.remove(i);
					return activity;
				}
			}
		}
		return null;
	}

	@Override
	public void run() {
		while (isRunning) {
			if (!tasks.isEmpty()) {
				Task task = null;
				task = tasks.poll();
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
		HashMap<String, Object> taskParams = (HashMap<String, Object>) task
				.getTaskParams();
		String return_msg = null;
		List<Status> statuses = null;
		List<Comment> comments = null;
		WeiboHomeService weiboHomeService = new WeiboHomeService(this);
		switch (task.getTaskId()) {
		case Task.WEIBO_USER_INFO:// 获取用户信息
			return_msg = getUserInfo(weibo, Weibo.getAppKey());
			msg.obj = return_msg;
			break;
		case Task.WEIBO_STATUSES_FRIENDS_TIMELINE:// 获取当前登录用户所关注用户的最新微博
			statuses = getFriendsTimeLine(task, weiboHomeService);
			msg.obj = statuses;
			break;
		case Task.WEIBO_STATUSES_UPDATE:// 发布一条微博
			return_msg = update(weibo, Weibo.getAppKey(),
					(HashMap<String, Object>) task.getTaskParams());
			break;
		case Task.WEIBO_STATUSES_UPLOAD:// 发布一条带图片的的微博
			return_msg = upload(weibo, Weibo.getAppKey(),
					(HashMap<String, Object>) task.getTaskParams());
			break;
		case Task.WEIBO_STATUSES_COMMENT:// 评论微博
			return_msg = comment(weibo, Weibo.getAppKey(),
					(HashMap<String, Object>) task.getTaskParams());
			break;
		case Task.WEIBO_STATUSES_REPOST:// 转发微博
			return_msg = repost(weibo, Weibo.getAppKey(),
					(HashMap<String, Object>) task.getTaskParams());
			break;
		case Task.WEIBO_FAVORITE_CREATE:// 收藏微博
			return_msg = favorite(weibo, Weibo.getAppKey(),
					(HashMap<String, Object>) task.getTaskParams(), true);
			break;
		case Task.WEIBO_FAVORITE_DESTROY:
			return_msg = favorite(weibo, Weibo.getAppKey(),
					(HashMap<String, Object>) task.getTaskParams(), false);
			break;
		case Task.WEIBO_STATUSES_DESTROY:

			break;
		case Task.WEIBO_MSG_AT_WB:// 获取@当前用户的微博
			statuses = getStatusesMentions(weibo, Weibo.getAppKey(), taskParams);
			msg.obj = statuses;
			break;
		case Task.WEIBO_MSG_AT_COMMENT:// 获取@当前用户的评论
			comments = getCommentsMentions(weibo, Weibo.getAppKey(), taskParams);
			msg.obj = comments;
			break;
		case Task.WEIBO_MSG_COMMENT_BY_ME:// 获取当前用户收到的评论和发出的评论
			comments = getCommentsByMe(weibo, Weibo.getAppKey(),
					(HashMap<String, Object>) task.getTaskParams());
			msg.obj = comments;
			break;
		case Task.WEIBO_MSG_COMMENT_TO_ME:
			comments = getCommentsToMe(weibo, Weibo.getAppKey(), taskParams);
			msg.obj = comments;
			break;
		case Task.WEIBO_MSG_MESSAGE:// 当前用户的私信
			break;
		case Task.WEIBO_MSG_NOTIFICATION:// 当前用户的通知
			break;
		case Task.WEIBO_EMOTIONS:
			return_msg = getEmotions();
			break;
		case Task.WEIBO_PLACE_USER_TIMELINE:
			statuses = getPlaceUserTimeline(weibo, Weibo.getAppKey(),
					taskParams);
			Geos geos = getGeoToAddress(weibo, Weibo.getAppKey(), statuses);
			msg.obj = geos;
			break;
		}
		handler.sendMessage(msg);
	}

	Handler handler = new Handler() {
		IWeiboActivity iWeiboActivity = null;

		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case Task.WEIBO_USER_INFO:
				break;
			case Task.WEIBO_STATUSES_FRIENDS_TIMELINE:
				iWeiboActivity = (IWeiboActivity) MainService.this
						.getActivityByName("HomeActivity");
				iWeiboActivity.refresh(msg.obj);
				break;
			case Task.WEIBO_STATUSES_UPDATE:
				iWeiboActivity = (IWeiboActivity) MainService.this
						.getActivityByName("NewWeiboActivity");
				iWeiboActivity.refresh(msg.obj, "update");
				break;
			case Task.WEIBO_STATUSES_UPLOAD:
				iWeiboActivity = (IWeiboActivity) MainService.this
						.getActivityByName("NewWeiboActivity");
				iWeiboActivity.refresh(msg.obj, "upload");
				break;
			case Task.WEIBO_STATUSES_COMMENT:
				iWeiboActivity = (IWeiboActivity) MainService.this
						.getActivityByName("CommentActivity");
				iWeiboActivity.refresh(msg.obj);
				break;
			case Task.WEIBO_FAVORITE_CREATE:
				iWeiboActivity = (IWeiboActivity) MainService.this
						.getActivityByName("DetailWeiboActivity");
				iWeiboActivity.refresh(msg.obj, "create");
				break;
			case Task.WEIBO_FAVORITE_DESTROY:
				iWeiboActivity = (IWeiboActivity) MainService.this
						.getActivityByName("DetailWeiboActivity");
				iWeiboActivity.refresh(msg.obj, "destroy");
				break;
			case Task.WEIBO_MSG_AT_WB:
				iWeiboActivity = (IWeiboActivity) MainService.this
						.getActivityByName("MsgActivity");
				iWeiboActivity.refresh(msg.obj, MsgActivity.AT, "status");
				break;
			case Task.WEIBO_MSG_AT_COMMENT:
				iWeiboActivity = (IWeiboActivity) MainService.this
						.getActivityByName("MsgActivity");
				iWeiboActivity.refresh(msg.obj, MsgActivity.AT, "comment");
				break;
			case Task.WEIBO_MSG_COMMENT_BY_ME:
				iWeiboActivity = (IWeiboActivity) MainService.this
						.getActivityByName("MsgActivity");
				iWeiboActivity.refresh(msg.obj, MsgActivity.COMMENT);
				break;
			case Task.WEIBO_MSG_COMMENT_TO_ME:
				iWeiboActivity = (IWeiboActivity) MainService.this
						.getActivityByName("MsgActivity");
				iWeiboActivity.refresh(msg.obj, MsgActivity.COMMENT);
				break;
			case Task.WEIBO_PLACE_USER_TIMELINE:
				iWeiboActivity = (IWeiboActivity) MainService.this
						.getActivityByName("NewWeiboActivity");
				iWeiboActivity.refresh(msg.obj, "place");
				break;
			case Task.WEIBO_STATUSES_REPOST:
				iWeiboActivity = (IWeiboActivity) MainService.this
						.getActivityByName("RedirectActivity");
				iWeiboActivity.refresh(msg.obj);
				break;
			}
		}
	};

	/**
	 * 获取用户信息
	 * 
	 * @param weibo
	 * @param source
	 * @return
	 * @throws WeiboException
	 */
	private String getUserInfo(Weibo weibo, String source)
			throws WeiboException {
		WeiboParameters parameters = new WeiboParameters();
		parameters.add("source", source);
		parameters.add("screen_name", "song980538137");
		String url_userinfo = Weibo.getSERVER() + "users/show.json";
		return weibo.request(this, url_userinfo, parameters,
				Utility.HTTPMETHOD_GET, weibo.getAccessToken());
	}

	private List<Status> getFriendsTimeLine(Task task,
			WeiboHomeService weiboHomeService) {
		HashMap<String, Object> taskParams = (HashMap<String, Object>) task
				.getTaskParams();
		List<Status> statuses = new ArrayList<Status>();
		int state = (Integer) taskParams.get("state");
		switch (state) {
		case HomeActivity.INITIATE:
			statuses = weiboHomeService.selectHomeInfo();
			if (statuses == null) {
				try {
					statuses = getFriendsTimeLine(weibo, Weibo.getAppKey(),
							taskParams);
				} catch (WeiboException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
				// 将获得的home数据写入数据库
				weiboHomeService.insertHomeInfo(statuses);
			}
			break;
		case HomeActivity.MORE_NEW:
			try {
				statuses = getFriendsTimeLine(weibo, Weibo.getAppKey(),
						taskParams);
			} catch (WeiboException e2) {
				e2.printStackTrace();
			} catch (JSONException e2) {
				e2.printStackTrace();
			}
			// 更新home表
			if (statuses != null) {
				if (statuses.size() != 0) {
					weiboHomeService.deleteHomeInfo();
					weiboHomeService.insertHomeInfo(statuses);
				}
			}
			
			break;
		case HomeActivity.MORE_OLD:
			try {
				statuses = getFriendsTimeLine(weibo, Weibo.getAppKey(),
						taskParams);
			} catch (WeiboException e2) {
				e2.printStackTrace();
			} catch (JSONException e2) {
				e2.printStackTrace();
			}
			break;
		}
		return statuses;
	}

	/**
	 * 获取当前登录用户所关注用户的最新微博
	 * 
	 * @param weibo
	 * @param source
	 * @param taskParams
	 * @return
	 * @throws WeiboException
	 * @throws JSONException
	 */
	private List<Status> getFriendsTimeLine(Weibo weibo, String source,
			HashMap<String, Object> taskParams) throws WeiboException,
			JSONException {
		WeiboParameters parameters = new WeiboParameters();
		parameters.add("source", source);
		parameters.add("screen_name", "song980538137");
		if (taskParams.get("max_id") != null) {
			String max_id = (String) taskParams.get("max_id");
			parameters.add("max_id", max_id);
		}
		String url_friends_timeline = Weibo.getSERVER()
				+ "statuses/friends_timeline.json";
		String statusStr = weibo.request(this, url_friends_timeline,
				parameters, Utility.HTTPMETHOD_GET, weibo.getAccessToken());
		return JsonUtils.parseJsonFromStatuses(statusStr);
	}

	/**
	 * 发布一条微博
	 * 
	 * @param weibo
	 * @param source
	 * @param taskParams
	 * @return
	 * @throws WeiboException
	 */
	private String update(Weibo weibo, String source,
			HashMap<String, Object> taskParams) throws WeiboException {

		String status = (String) taskParams.get("status");
		String lon = (String) taskParams.get("long");
		String lat = (String) taskParams.get("lat");
		WeiboParameters parameters = new WeiboParameters();
		parameters.add("source", source);
		parameters.add("status", status);
		if (!TextUtils.isEmpty(lon)) {
			parameters.add("long", lon);
		}
		if (!TextUtils.isEmpty(lat)) {
			parameters.add("lat", lat);
		}
		String url_update = Weibo.getSERVER() + "statuses/update.json";
		return weibo.request(this, url_update, parameters,
				Utility.HTTPMETHOD_POST, weibo.getAccessToken());
	}

	/**
	 * 发布一条带图片的的微博
	 * 
	 * @param weibo
	 * @param source
	 * @param taskParams
	 * @return
	 * @throws WeiboException
	 */
	private String upload(Weibo weibo, String source,
			HashMap<String, Object> taskParams) throws WeiboException {

		String pic = (String) taskParams.get("file");
		String status = (String) taskParams.get("status");
		String lon = (String) taskParams.get("long");
		String lat = (String) taskParams.get("lat");
		WeiboParameters parameters = new WeiboParameters();
		parameters.add("source", source);
		parameters.add("pic", pic);
		parameters.add("status", status);
		if (!TextUtils.isEmpty(lon)) {
			parameters.add("long", lon);
		}
		if (!TextUtils.isEmpty(lat)) {
			parameters.add("lat", lat);
		}
		String url_upload = Weibo.getSERVER() + "statuses/upload.json";
		return weibo.request(this, url_upload, parameters,
				Utility.HTTPMETHOD_POST, weibo.getAccessToken());
	}

	/**
	 * 删除一条微博
	 * 
	 * @param weibo
	 * @param source
	 * @param taskParams
	 * @return
	 * @throws WeiboException
	 */
	@SuppressWarnings("unused")
	private String statusDestroy(Weibo weibo, String source,
			HashMap<String, Object> taskParams) throws WeiboException {
		WeiboParameters weiboParameters = new WeiboParameters();
		weiboParameters.add("source", source);
		weiboParameters.add("id", weiboParameters.getValue("id"));

		String url_status_destroy = Weibo.getSERVER() + "statuses/destroy.json";
		return weibo.request(this, url_status_destroy, weiboParameters,
				Utility.HTTPMETHOD_POST, weibo.getAccessToken());
	}

	/**
	 * 转发一条微博
	 * 
	 * @param weibo
	 * @param source
	 * @param taskParams
	 * @return
	 * @throws WeiboException
	 */
	private String repost(Weibo weibo, String source,
			HashMap<String, Object> taskParams) throws WeiboException {

		WeiboParameters parameters = new WeiboParameters();
		parameters.add("source", source);
		parameters.add("id", String.valueOf(taskParams.get("id")));
		parameters.add("status", (String) taskParams.get("status"));
		parameters.add("is_comment",
				String.valueOf(taskParams.get("is_comment")));
		String url_repost = Weibo.getSERVER() + "statuses/repost.json";
		return weibo.request(this, url_repost, parameters,
				Utility.HTTPMETHOD_POST, weibo.getAccessToken());
	}

	/**
	 * 评论一条微博
	 * 
	 * @param weibo
	 * @param source
	 * @param taskParams
	 * @return
	 * @throws WeiboException
	 */
	private String comment(Weibo weibo, String source,
			HashMap<String, Object> taskParams) throws WeiboException {
		WeiboParameters parameters = new WeiboParameters();
		parameters.add("source", source);
		parameters.add("comment", (String) taskParams.get("comment"));
		parameters.add("id", String.valueOf(taskParams.get("id")));
		parameters.add("comment_ori", String.valueOf(taskParams.get("comment_ori")));
		String url_comment = Weibo.getSERVER() + "comments/create.json";
		return weibo.request(this, url_comment, parameters,
				Utility.HTTPMETHOD_POST, weibo.getAccessToken());
	}

	/**
	 * 收藏一条微博或取消收藏微博
	 * 
	 * @param weibo
	 * @param source
	 * @param taskParams
	 * @return
	 * @throws WeiboException
	 */
	private String favorite(Weibo weibo, String source,
			HashMap<String, Object> taskParams, boolean isFavorite)
			throws WeiboException {
		WeiboParameters parameters = new WeiboParameters();
		parameters.add("source", source);
		parameters.add("id", String.valueOf(taskParams.get("id")));
		String url = null;
		if (isFavorite) {
			url = Weibo.getSERVER() + "favorites/create.json";
		} else {
			url = Weibo.getSERVER() + "favorites/destroy.json";
		}
		return weibo.request(this, url, parameters, Utility.HTTPMETHOD_POST,
				weibo.getAccessToken());
	}

	/**
	 * 获取官方表情信息
	 * 
	 * @return
	 */
	private String getEmotions() {
		String url_emotions = Weibo.getSERVER() + "emotions.json";
		WeiboParameters bundle_emotions = new WeiboParameters();
		bundle_emotions.add("source", Weibo.getAppKey());
		String return_msg = null;
		try {
			return_msg = weibo.request(this, url_emotions, bundle_emotions,
					Utility.HTTPMETHOD_GET, weibo.getAccessToken());
			String path = "sdcard/emotion.txt";
			try {
				FileWriter fileWriter = new FileWriter(new File(path));
				fileWriter.write(return_msg);
				fileWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.getExternalFilesDir(null);
		} catch (WeiboException e) {
			e.printStackTrace();
		}
		return return_msg;
	}

	/**
	 * 获取@当前用户的微博
	 * 
	 * @param weibo
	 * @param source
	 * @param taskParams
	 * @return
	 * @throws WeiboException
	 * @throws JSONException
	 */
	private List<Status> getStatusesMentions(Weibo weibo, String source,
			HashMap<String, Object> taskParams) throws WeiboException,
			JSONException {
		WeiboParameters weiboParameters = new WeiboParameters();
		weiboParameters.add("source", source);
		if (taskParams != null) {
			if (taskParams.get("max_id") != null) {
				String max_id = (String) taskParams.get("max_id");
				weiboParameters.add("max_id", max_id);
			}
			Integer filter_by_author = (Integer) taskParams
					.get("filter_by_author");
			if (null != filter_by_author) {
				weiboParameters.add("filter_by_author",
						String.valueOf(filter_by_author));
			}
			Integer filter_by_type = (Integer) taskParams.get("filter_by_type");
			if (null != filter_by_type) {
				weiboParameters.add("filter_by_type",
						String.valueOf(filter_by_type));
			}
		}

		String url_statuses_mentions = Weibo.getSERVER()
				+ "statuses/mentions.json";
		String statuses_mentions = weibo
				.request(this, url_statuses_mentions, weiboParameters,
						Utility.HTTPMETHOD_GET, weibo.getAccessToken());
		List<Status> statuses = JsonUtils
				.parseJsonFromStatuses(statuses_mentions);
		return statuses;
	}

	/**
	 * 获取@当前用户的评论
	 * 
	 * @param weibo
	 * @param source
	 * @param taskParams
	 * @return
	 * @throws WeiboException
	 * @throws JSONException
	 */
	private List<Comment> getCommentsMentions(Weibo weibo, String source,
			HashMap<String, Object> taskParams) throws WeiboException,
			JSONException {
		WeiboParameters weiboParameters = new WeiboParameters();
		weiboParameters.add("source", source);
		if (taskParams != null) {
			Integer filter_by_author = (Integer) taskParams
					.get("filter_by_author");
			if (null != filter_by_author) {
				weiboParameters.add("filter_by_author",
						String.valueOf(filter_by_author));
			}
		}
		String url_comments_mentions = Weibo.getSERVER()
				+ "comments/mentions.json";
		String comments_mentions = weibo
				.request(this, url_comments_mentions, weiboParameters,
						Utility.HTTPMETHOD_GET, weibo.getAccessToken());
		List<Comment> comments = JsonUtils
				.parseJsonFromComments(comments_mentions);
		return comments;
	}

	/**
	 * 获取当前用户收到的评论
	 * 
	 * @param weibo
	 * @param source
	 * @param taskParams
	 * @return
	 * @throws WeiboException
	 * @throws JSONException
	 */
	private List<Comment> getCommentsToMe(Weibo weibo, String source,
			HashMap<String, Object> taskParams) throws WeiboException,
			JSONException {
		WeiboParameters weiboParameters = new WeiboParameters();
		weiboParameters.add("source", source);
		if (taskParams != null) {
			// long max_id = (Long) taskParams.get("max_id");
			// if (!TextUtils.isEmpty(String.valueOf(max_id))) {
			// weiboParameters.add("max_id",
			// String.valueOf(taskParams.get("max_id")));
			// }
			Integer filter_by_author = (Integer) taskParams
					.get("filter_by_author");
			if (null != filter_by_author) {
				weiboParameters.add("filter_by_author",
						String.valueOf(filter_by_author));
			}
		}
		String url_comments_tome = Weibo.getSERVER() + "comments/to_me.json";
		String comments_tome = weibo
				.request(this, url_comments_tome, weiboParameters,
						Utility.HTTPMETHOD_GET, weibo.getAccessToken());
		return JsonUtils.parseJsonFromComments(comments_tome);
	}

	/**
	 * 获取当前用户发出的评论
	 * 
	 * @param weibo
	 * @param source
	 * @param taskParams
	 * @return
	 * @throws WeiboException
	 * @throws JSONException
	 */
	private List<Comment> getCommentsByMe(Weibo weibo, String source,
			HashMap<String, Object> taskParams) throws WeiboException,
			JSONException {
		WeiboParameters weiboParameters = new WeiboParameters();
		weiboParameters.add("source", source);
		if (taskParams != null) {
			// long max_id = (Long) taskParams.get("max_id");
			// if (!TextUtils.isEmpty(String.valueOf(max_id))) {
			// weiboParameters.add("max_id",
			// String.valueOf(taskParams.get("max_id")));
			// }
		}
		String url_comments_byme = Weibo.getSERVER() + "comments/by_me.json";
		String comments_byme = weibo
				.request(this, url_comments_byme, weiboParameters,
						Utility.HTTPMETHOD_GET, weibo.getAccessToken());
		return JsonUtils.parseJsonFromComments(comments_byme);
	}

	/**
	 * 获取某个用户的位置动态
	 * 
	 * @param weibo
	 * @param source
	 * @param taskParams
	 * @return
	 * @throws WeiboException
	 * @throws JSONException
	 */
	private List<Status> getPlaceUserTimeline(Weibo weibo, String source,
			HashMap<String, Object> taskParams) throws WeiboException,
			JSONException {
		WeiboParameters weiboParameters = new WeiboParameters();
		weiboParameters.add("source", source);
		weiboParameters.add("uid", (String) taskParams.get("uid"));

		String url_place_user_timeline = Weibo.getSERVER()
				+ "place/user_timeline.json";
		String place_user_timeline = weibo.request(this,
				url_place_user_timeline, weiboParameters,
				Utility.HTTPMETHOD_GET, weibo.getAccessToken());
		return JsonUtils.parseJsonFromStatuses(place_user_timeline);
	}

	private Geos getGeoToAddress(Weibo weibo, String source,
			List<Status> statuses) throws WeiboException, JSONException {
		Status status = statuses.get(0);
		Geo geo = status.getGeo();

		String longitude = geo.getLongitude();
		String latitude = geo.getLatitude();
		String coordinate = latitude + "," + longitude;
		WeiboParameters weiboParameters = new WeiboParameters();
		weiboParameters.add("source", source);
		weiboParameters.add("coordinate", coordinate);

		String url = Weibo.getSERVER() + "location/geo/geo_to_address.json";
		String geoToAddress = weibo.request(this, url, weiboParameters,
				Utility.HTTPMETHOD_GET, weibo.getAccessToken());
		return JsonUtils.parseJsonFromGeos(geoToAddress);
	}

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
		for (Activity activity : activities) {
			if (!activity.isFinishing()) {
				activity.finish();
			}
		}
		// 停止服务
		Intent service = new Intent("cn.edu.nuc.logic.MainService");
		context.stopService(service);
	}

}
