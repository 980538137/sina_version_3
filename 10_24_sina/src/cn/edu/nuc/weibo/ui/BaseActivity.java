package cn.edu.nuc.weibo.ui;

import cn.edu.nuc.weibo.R;
import cn.edu.nuc.weibo.logic.MainService;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class BaseActivity extends Activity {
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			alertExit();
		}
		return true;
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, "切换用户");
		menu.add(0,1,0,"退出");
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			Toast.makeText(this, "切换用户", Toast.LENGTH_SHORT).show();
			break;
		case 1:
			Toast.makeText(this, "退出", Toast.LENGTH_SHORT).show();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
    /**
     * 退出系统对话框
     */
	private void alertExit() {
		Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.caution).setMessage(R.string.caution_content)
				.setPositiveButton(R.string.ok, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						MainService.appExit(BaseActivity.this);
						//杀死当前线程
						android.os.Process.killProcess(android.os.Process.myPid());
					}
				}).setNegativeButton(R.string.cancel, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		AlertDialog alertDialog = builder.create();
		alertDialog.show();
	}

}
