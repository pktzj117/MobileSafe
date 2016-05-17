package org.pktzj.mobilesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;

import org.pktzj.mobilesafe.R;

public class SplashActivity extends Activity {

    private RelativeLayout rl_root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initAnimation();
    }

    private void initAnimation() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        alphaAnimation.setDuration(3000);
        alphaAnimation.setFillAfter(true);
        rl_root.startAnimation(alphaAnimation);
    }

    private void initView() {
        setContentView(R.layout.activity_splash);
        rl_root = (RelativeLayout)findViewById(R.id.rl_root);
    }
}
