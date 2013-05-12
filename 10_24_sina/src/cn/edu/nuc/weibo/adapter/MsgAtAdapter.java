package cn.edu.nuc.weibo.adapter;

import java.util.List;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import cn.edu.nuc.weibo.R;
import cn.edu.nuc.weibo.bean.Status;
import cn.edu.nuc.weibo.loadimg.SimpleImageLoader;
import cn.edu.nuc.weibo.parsewb.SimpleParseManager;

public class MsgAtAdapter extends MyBaseAdapter {
	private List<Status> statuses = null;
	private Context context = null;
	private LayoutInflater inflater = null;
	public static final int PORTRAIT_IMG = 1;
	public static final int CONTENT_IMG = 2;

	public MsgAtAdapter(Context context) {
		super(context);
		this.context = context;
	}

	public MsgAtAdapter(List<Status> statuses, Context context) {
		super(context);
		this.statuses = statuses;
		this.context = context;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return statuses == null ? 0 : statuses.size();
	}

	@Override
	public Object getItem(int position) {
		return statuses == null ? null : statuses.get(position);
	}

	@Override
	public long getItemId(int position) {
		return statuses == null ? null : statuses.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return createViewFromResource(position, convertView);
	}
    //创建View
	private View createViewFromResource(int position, View convertView) {
		MsgAtHolder holder = null;
		Status status = statuses.get(position);
		if (null == convertView) {
			convertView = inflater.inflate(R.layout.msg_at_item, null);
			holder = new MsgAtHolder();
			findViews(holder, convertView);
			convertView.setTag(holder);
		} else {
			holder = (MsgAtHolder) convertView.getTag();
		}
		try {
			setValues(status, holder);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return convertView;
	}
    
	private void findViews(MsgAtHolder holder, View convertView) {
		holder.iv_msg_at_portrait = (ImageView) convertView
				.findViewById(R.id.iv_msg_at_portrait);
		holder.iv_msg_at_portrait_v_blue = (ImageView) convertView
				.findViewById(R.id.iv_msg_at_portrait_v_blue);
		holder.iv_msg_at_portrait_v_red = (ImageView) convertView
				.findViewById(R.id.iv_msg_at_portrait_v_red);
		holder.iv_msg_at_portrait_v_yellow = (ImageView) convertView
				.findViewById(R.id.iv_msg_at_portrait_v_yellow);
		holder.tv_msg_at_username = (TextView) convertView
				.findViewById(R.id.tv_msg_at_username);
		holder.tv_msg_at_time = (TextView) convertView
				.findViewById(R.id.tv_msg_at_time);
		holder.tv_msg_at_content = (TextView) convertView
				.findViewById(R.id.tv_msg_at_content);
		holder.iv_msg_at_content_pic = (ImageView) convertView
				.findViewById(R.id.iv_msg_at_content_pic);
		holder.tv_msg_at_from_where = (TextView) convertView
				.findViewById(R.id.tv_msg_at_from_where);
	}

	private void setValues(Status status, MsgAtHolder holder)
			throws InterruptedException {
		final String portraitURl = status.getUser().getProfile_image_url();
		SimpleImageLoader.showImg(holder.iv_msg_at_portrait, portraitURl,
				context, PORTRAIT_IMG);

		if (status.getUser().isVerified()&& status.getUser().getVerified_type() == 0) {
			holder.iv_msg_at_portrait_v_blue.setVisibility(View.GONE);
			holder.iv_msg_at_portrait_v_red.setVisibility(View.GONE);
			holder.iv_msg_at_portrait_v_yellow.setVisibility(View.VISIBLE);
		}else if (status.getUser().isVerified()&& status.getUser().getVerified_type() != 0) {
			holder.iv_msg_at_portrait_v_yellow.setVisibility(View.GONE);
			holder.iv_msg_at_portrait_v_red.setVisibility(View.GONE);
			holder.iv_msg_at_portrait_v_blue.setVisibility(View.VISIBLE);
		}else {
			holder.iv_msg_at_portrait_v_yellow.setVisibility(View.GONE);
			holder.iv_msg_at_portrait_v_red.setVisibility(View.GONE);
			holder.iv_msg_at_portrait_v_blue.setVisibility(View.GONE);
		}
		holder.tv_msg_at_username.setText(status.getUser().getName());
		SimpleParseManager.parseWeibo(holder.tv_msg_at_content,
				status.getText());
		SimpleParseManager.parseTime(holder.tv_msg_at_time,
				status.getCreated_at());

		String imgURl = status.getThumbnail_pic();
		if (imgURl == null) {
			holder.iv_msg_at_content_pic.setVisibility(View.GONE);
		} else {
			holder.iv_msg_at_content_pic.setVisibility(View.VISIBLE);
			SimpleImageLoader.showImg(holder.iv_msg_at_content_pic, imgURl,
					context, CONTENT_IMG);
			holder.iv_msg_at_content_pic
					.setOnClickListener(new PicOnClickListener(status
							.getBmiddle_pic(), status.getOriginal_pic()));

		}
		holder.tv_msg_at_from_where.setText(Html.fromHtml(status.getSource()));
	}

	private class MsgAtHolder {
		ImageView iv_msg_at_portrait;
		ImageView iv_msg_at_portrait_v_blue;
		ImageView iv_msg_at_portrait_v_red;
		ImageView iv_msg_at_portrait_v_yellow;
		TextView tv_msg_at_username;
		TextView tv_msg_at_time;
		TextView tv_msg_at_content;
		ImageView iv_msg_at_content_pic;
		TextView tv_msg_at_from_where;
	}
	public void refresh(List<Status> new_statuses){
		statuses.addAll(new_statuses);
		this.notifyDataSetChanged();
		
	}

}
