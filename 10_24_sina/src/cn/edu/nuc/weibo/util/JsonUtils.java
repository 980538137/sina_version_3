package cn.edu.nuc.weibo.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.edu.nuc.weibo.bean.Comment;
import cn.edu.nuc.weibo.bean.Geo;
import cn.edu.nuc.weibo.bean.Geos;
import cn.edu.nuc.weibo.bean.Retweeted_Status;
import cn.edu.nuc.weibo.bean.Status;
import cn.edu.nuc.weibo.bean.User;
import cn.edu.nuc.weibo.bean.Visiable;

import com.google.gson.Gson;

public class JsonUtils {
	/**
	 * 解析服务器返回的comments Json数据
	 * @param jsonData
	 * @return
	 * @throws JSONException
	 */
	public static List<Comment> parseJsonFromComments(String jsonData) throws JSONException{
    	ArrayList<Comment> comments = new ArrayList<Comment>();
    	JSONObject jo_comments = new JSONObject(jsonData);
    	JSONArray ja_comments = (JSONArray) jo_comments.get("comments");
    	Gson gson = new Gson();
    	for (int i = 0; i < ja_comments.length(); i++) {
			Comment comment = new Comment();
			JSONObject jo_comment = (JSONObject) ja_comments.get(i);
			comment.setCreated_at(jo_comment.getString("created_at"));
			comment.setId(jo_comment.getLong("id"));
			comment.setText(jo_comment.getString("text"));
			comment.setSource(jo_comment.getString("source"));
			comment.setMid(jo_comment.getString("mid"));
			//解析user Json对象
			JSONObject jo_user = jo_comment.getJSONObject("user");
			User user = gson.fromJson(jo_user.toString(), User.class);
			comment.setUser(user);
			//解析status Json对象
			JSONObject jo_status = jo_comment.getJSONObject("status");
			Status status = new Status();
			status.setCreated_at(jo_status.getString("created_at"));
			status.setId(jo_status.getLong("id"));
			status.setMid(jo_status.getString("mid"));
			status.setIdstr(jo_status.getString("idstr"));
			status.setText(jo_status.getString("text"));
			try {
				status.setSource(jo_status.getString("source"));
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
			status.setFavorited(jo_status.getBoolean("favorited"));
			status.setTruncated(jo_status.getBoolean("truncated"));
			status.setIn_reply_to_status_id(jo_status
					.getString("in_reply_to_status_id"));
			status.setIn_reply_to_user_id(jo_status
					.getString("in_reply_to_user_id"));
			status.setIn_reply_to_screen_name(jo_status
					.getString("in_reply_to_screen_name"));
			
			if (jo_status.has("thumbnail_pic")) {
				status.setThumbnail_pic(jo_status.getString("thumbnail_pic"));
			}else {
				status.setThumbnail_pic(null);
			}
			
			if (jo_status.has("bmiddle_pic")) {
				status.setBmiddle_pic(jo_status.getString("bmiddle_pic"));
			}else {
				status.setBmiddle_pic(null);
			}
			if (jo_status.has("original_pic")) {
				status.setOriginal_pic(jo_status.getString("original_pic"));
			}else {
				status.setOriginal_pic(null);
			}

			JSONObject jo_status_user = jo_status.getJSONObject("user");
			User status_user = gson.fromJson(jo_status_user.toString(), User.class);
			status.setUser(status_user);

			JSONObject jo_visible = jo_status.getJSONObject("visible");
			Visiable visiable = gson.fromJson(jo_visible.toString(),Visiable.class);
			status.setVisiable(visiable);

			status.setReposts_count(jo_status.getInt("reposts_count"));
			status.setComments_count(jo_status.getInt("comments_count"));
			status.setAttitudes_count(jo_status.getInt("attitudes_count"));
			status.setMlevel(jo_status.getInt("mlevel"));
			comment.setStatus(status);
			comments.add(comment);
		}
    	return comments;
    }
	/**
	 * 解析服务器返回的friendstimeline  json数据
	 * 
	 * @param jsonData
	 * @return
	 * @throws JSONException
	 */
	public static List<Status> parseJsonFromStatuses(String jsonData)
			throws JSONException {
		ArrayList<Status> statuses = new ArrayList<Status>();
		JSONObject jo_Statuses = new JSONObject(jsonData);
		JSONArray ja_Statuses = (JSONArray) jo_Statuses.get("statuses");
		Gson gson = new Gson();
		for (int i = 0; i < ja_Statuses.length(); i++) {
			Status status = new Status();
			JSONObject jo_status = (JSONObject) ja_Statuses.get(i);
			status.setCreated_at(jo_status.getString("created_at"));
			status.setId(jo_status.getLong("id"));
			status.setMid(jo_status.getString("mid"));
			status.setIdstr(jo_status.getString("idstr"));
			status.setText(jo_status.getString("text"));
			try {
				status.setSource(jo_status.getString("source"));
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
			status.setFavorited(jo_status.getBoolean("favorited"));
			status.setTruncated(jo_status.getBoolean("truncated"));
			status.setIn_reply_to_status_id(jo_status
					.getString("in_reply_to_status_id"));
			status.setIn_reply_to_user_id(jo_status
					.getString("in_reply_to_user_id"));
			status.setIn_reply_to_screen_name(jo_status
					.getString("in_reply_to_screen_name"));
			
			if (jo_status.has("thumbnail_pic")) {
				status.setThumbnail_pic(jo_status.getString("thumbnail_pic"));
			}else {
				status.setThumbnail_pic(null);
			}
			
			if (jo_status.has("bmiddle_pic")) {
				status.setBmiddle_pic(jo_status.getString("bmiddle_pic"));
			}else {
				status.setBmiddle_pic(null);
			}
			if (jo_status.has("original_pic")) {
				status.setOriginal_pic(jo_status.getString("original_pic"));
			}else {
				status.setOriginal_pic(null);
			}
			//解析user Json对象
			JSONObject jo_user = jo_status.getJSONObject("user");
			User user = gson.fromJson(jo_user.toString(), User.class);
			status.setUser(user);
            
			//解析geo Json对象
			if (!jo_status.isNull("geo")) {
				JSONObject jo_geo = jo_status.getJSONObject("geo");
				String type = (String) jo_geo.get("type");
				Object object= jo_geo.get("coordinates");
				String coordinates = object.toString();
				Geo geo = new Geo();
				geo.setType(type);
				String longitude = coordinates.substring(1, coordinates.indexOf(","));
				String latitude = coordinates.substring(coordinates.indexOf(",")+1, coordinates.length()-1);
				geo.setLongitude(longitude);
				geo.setLatitude(latitude);
				status.setGeo(geo);
			}else {
				status.setGeo(null);
			}
			
			
			//解析Retweeted_Status Json对象
			if (jo_status.has("retweeted_status")) {
				try {
					status.setRetweeted_Status(parseRetweeted_Status(jo_status));
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}
			} else {
				status.setRetweeted_Status(null);
			}

			JSONObject jo_visible = jo_status.getJSONObject("visible");
			Visiable visiable = gson.fromJson(jo_visible.toString(),
					Visiable.class);
			status.setVisiable(visiable);

			status.setReposts_count(jo_status.getInt("reposts_count"));
			status.setComments_count(jo_status.getInt("comments_count"));
			status.setAttitudes_count(jo_status.getInt("attitudes_count"));
			status.setMlevel(jo_status.getInt("mlevel"));

			statuses.add(status);
		}
		return statuses;

	}

	/**
	 * 解析RetweetedStatus
	 * 
	 * @param jo_Status
	 * @return
	 * @throws JSONException
	 */
	private static Retweeted_Status parseRetweeted_Status(JSONObject jo_Status)
			throws JSONException {
		Gson gson = new Gson();
		Retweeted_Status retweeted_Status = new Retweeted_Status();
		JSONObject jo_Retweeted_Status = jo_Status
				.getJSONObject("retweeted_status");
		// 解析Retweeted_Status
		retweeted_Status = new Retweeted_Status();
		retweeted_Status.setCreated_at(jo_Retweeted_Status
				.getString("created_at"));
		retweeted_Status.setId(jo_Retweeted_Status.getLong("id"));
		retweeted_Status.setMid(jo_Retweeted_Status.getString("mid"));
		retweeted_Status.setIdstr(jo_Retweeted_Status.getString("idstr"));
		retweeted_Status.setText(jo_Retweeted_Status.getString("text"));
		
		retweeted_Status.setSource(jo_Retweeted_Status.getString("source"));
		
		retweeted_Status.setFavorited(jo_Retweeted_Status
				.getBoolean("favorited"));
		retweeted_Status.setTruncated(jo_Retweeted_Status
				.getBoolean("truncated"));
		retweeted_Status.setIn_reply_to_status_id(jo_Retweeted_Status
				.getString("in_reply_to_status_id"));
		retweeted_Status.setIn_reply_to_user_id(jo_Retweeted_Status
				.getString("in_reply_to_user_id"));
		retweeted_Status.setIn_reply_to_screen_name(jo_Retweeted_Status
				.getString("in_reply_to_screen_name"));
		
		if (jo_Retweeted_Status.has("thumbnail_pic")) {
			retweeted_Status.setThumbnail_pic(jo_Retweeted_Status.getString("thumbnail_pic"));
		}else {
			retweeted_Status.setThumbnail_pic(null);
		}
		
		if (jo_Retweeted_Status.has("bmiddle_pic")) {
			retweeted_Status.setBmiddle_pic(jo_Retweeted_Status.getString("bmiddle_pic"));
		}else {
			retweeted_Status.setBmiddle_pic(null);
		}
		if (jo_Retweeted_Status.has("original_pic")) {
			retweeted_Status.setOriginal_pic(jo_Retweeted_Status.getString("original_pic"));
		}else {
			retweeted_Status.setOriginal_pic(null);
		}

		JSONObject jo_user_Retweeted = jo_Retweeted_Status
				.getJSONObject("user");
		User user1 = gson.fromJson(jo_user_Retweeted.toString(), User.class);
		retweeted_Status.setUser(user1);

		retweeted_Status.setReposts_count(jo_Retweeted_Status
				.getInt("reposts_count"));
		retweeted_Status.setComments_count(jo_Retweeted_Status
				.getInt("comments_count"));
		retweeted_Status.setAttitudes_count(jo_Retweeted_Status
				.getInt("attitudes_count"));
		retweeted_Status.setMlevel(jo_Retweeted_Status.getInt("mlevel"));

		JSONObject jo_visible_Retweeted = jo_Retweeted_Status
				.getJSONObject("visible");
		Visiable visiable_Retweeted = gson.fromJson(
				jo_visible_Retweeted.toString(), Visiable.class);
		retweeted_Status.setVisiable(visiable_Retweeted);

		retweeted_Status.setReposts_count(jo_Retweeted_Status
				.getInt("reposts_count"));
		retweeted_Status.setComments_count(jo_Retweeted_Status
				.getInt("comments_count"));
		retweeted_Status.setAttitudes_count(jo_Retweeted_Status
				.getInt("attitudes_count"));
		retweeted_Status.setMlevel(jo_Retweeted_Status.getInt("mlevel"));
		return retweeted_Status;
	}
	/**
	 * 解析Geos Json对象
	 * @param jsonData
	 * @return
	 * @throws JSONException
	 */
	public static Geos parseJsonFromGeos(String jsonData) throws JSONException{
		Geos geos = new Geos();
		JSONObject jo_geos = new JSONObject(jsonData);
		JSONArray ja_geos = jo_geos.getJSONArray("geos");
		JSONObject jo_geo = null;
		Gson gson = new Gson();
		for (int i = 0; i < ja_geos.length(); i++) {
			jo_geo = (JSONObject) ja_geos.get(i);
			geos = gson.fromJson(jo_geo.toString(), Geos.class);
		}
		return geos;
	} 
}
