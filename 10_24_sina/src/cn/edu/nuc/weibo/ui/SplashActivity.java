package cn.edu.nuc.weibo.ui;


import cn.edu.nuc.weibo.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;

public class SplashActivity extends Activity {
	private ImageView iv_splash = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		iv_splash = (ImageView) this.findViewById(R.id.iv_splash);
		Animation animation = new AlphaAnimation(1.0f, 1.0f);
		animation.setDuration(2000);
		animation.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
				startActivity(intent);
				finish();
			}
		});
		iv_splash.setAnimation(animation);
	}

}
