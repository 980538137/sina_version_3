package cn.edu.nuc.weibo.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.edu.nuc.weibo.R;
import cn.edu.nuc.weibo.bean.Comment;
import cn.edu.nuc.weibo.loadimg.SimpleImageLoader;
import cn.edu.nuc.weibo.parsewb.SimpleParseManager;


public class MsgCommentAdapter extends BaseAdapter {
	private List<Comment> comments = null;
	private Context context = null;
	private LayoutInflater inflater = null;
	public static final int PORTRAIT_IMG = 1;
	public static final int CONTENT_IMG = 2;


	public MsgCommentAdapter(List<Comment> comments, Context context) {
		this.comments = comments;
		this.context = context;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return comments == null ? 0 : comments.size();
	}

	@Override
	public Object getItem(int position) {
		return comments == null ? null : comments.get(position);
	}

	@Override
	public long getItemId(int position) {
		return comments == null ? null : comments.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return createViewFromResource(position, convertView);
	}
    //创建View
	private View createViewFromResource(int position, View convertView) {
		MsgAtHolder holder = null;
		Comment comment = comments.get(position);
		if (null == convertView) {
			convertView = inflater.inflate(R.layout.msg_comment_item, null);
			holder = new MsgAtHolder();
			findViews(holder, convertView);
			convertView.setTag(holder);
		} else {
			holder = (MsgAtHolder) convertView.getTag();
		}
		try {
			setValues(comment, holder);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return convertView;
	}
    
	private void findViews(MsgAtHolder holder, View convertView) {
		holder.iv_msg_comment_portrait = (ImageView) convertView
				.findViewById(R.id.iv_msg_comment_portrait);
		holder.iv_msg_comment_portrait_v_blue = (ImageView) convertView
				.findViewById(R.id.iv_msg_comment_portrait_v_blue);
		holder.iv_msg_comment_portrait_v_red = (ImageView) convertView
				.findViewById(R.id.iv_msg_comment_portrait_v_red);
		holder.iv_msg_comment_portrait_v_yellow = (ImageView) convertView
				.findViewById(R.id.iv_msg_comment_portrait_v_yellow);
		holder.tv_msg_comment_username = (TextView) convertView
				.findViewById(R.id.tv_msg_comment_username);
		holder.tv_msg_comment_time = (TextView) convertView
				.findViewById(R.id.tv_msg_comment_time);
		holder.tv_msg_comment_content = (TextView) convertView
				.findViewById(R.id.tv_msg_comment_content);
		holder.tv_msg_comment_weibo_content = (TextView) convertView
				.findViewById(R.id.tv_msg_comment_weibo_content);
	}

	private void setValues(Comment comment, MsgAtHolder holder)
			throws InterruptedException {
		final String portraitURl = comment.getUser().getProfile_image_url();
		SimpleImageLoader.showImg(holder.iv_msg_comment_portrait, portraitURl,
				context, PORTRAIT_IMG);

		if (comment.getUser().isVerified()
				&& comment.getUser().getVerified_type() == 0) {
			holder.iv_msg_comment_portrait_v_blue.setVisibility(View.GONE);
			holder.iv_msg_comment_portrait_v_red.setVisibility(View.GONE);
			holder.iv_msg_comment_portrait_v_yellow.setVisibility(View.VISIBLE);
		}else if (comment.getUser().isVerified()
				&& comment.getUser().getVerified_type() != 0) {
			holder.iv_msg_comment_portrait_v_red.setVisibility(View.GONE);
			holder.iv_msg_comment_portrait_v_yellow.setVisibility(View.GONE);
			holder.iv_msg_comment_portrait_v_blue.setVisibility(View.VISIBLE);
		}else {
			holder.iv_msg_comment_portrait_v_red.setVisibility(View.GONE);
			holder.iv_msg_comment_portrait_v_yellow.setVisibility(View.GONE);
			holder.iv_msg_comment_portrait_v_blue.setVisibility(View.GONE);
		}
		holder.tv_msg_comment_username.setText(comment.getUser().getName());
		SimpleParseManager.parseTime(holder.tv_msg_comment_time,
				comment.getCreated_at());
		SimpleParseManager.parseWeibo(holder.tv_msg_comment_content,
				comment.getText());
	  	SimpleParseManager.parseWeibo(holder.tv_msg_comment_weibo_content, comment.getStatus().getText());
   
		
	}

	private class MsgAtHolder {
		ImageView iv_msg_comment_portrait;
		ImageView iv_msg_comment_portrait_v_blue;
		ImageView iv_msg_comment_portrait_v_red;
		ImageView iv_msg_comment_portrait_v_yellow;
		TextView tv_msg_comment_username;
		TextView tv_msg_comment_time;
		TextView tv_msg_comment_content;
		TextView tv_msg_comment_weibo_content;
	}
}
