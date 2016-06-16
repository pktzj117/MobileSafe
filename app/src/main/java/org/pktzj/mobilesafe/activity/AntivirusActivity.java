package org.pktzj.mobilesafe.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.pktzj.mobilesafe.R;
import org.pktzj.mobilesafe.domain.APPBean;
import org.pktzj.mobilesafe.engine.APPMangerEngine;
import org.pktzj.mobilesafe.engine.AntivirusEngine;
import org.pktzj.mobilesafe.utils.MD5Utils;

import java.util.List;

public class AntivirusActivity extends Activity {

    private static final int START = 1;
    private static final int SCANNING = 2;
    private static final int FINISHED = 3;
    private ImageView iv_scanning;
    private RotateAnimation rotateAnimation;
    private AlphaAnimation alphaAnimation;
    private AppVirusBean appVirusBean;
    private LinearLayout ll_appname;
    private TextView tv_apppackname;
    private ProgressBar pb_scan;
    private List<APPBean> allAPK;
    private int progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initAnim();
        initData();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case START:
                    iv_scanning.startAnimation(rotateAnimation);
                    break;
                case SCANNING:
                    pb_scan.setMax(allAPK.size());
                    pb_scan.setProgress(progress);
                    if (TextUtils.isEmpty(appVirusBean.getNappPackName())) {
                        tv_apppackname.setText("扫描完成");
                    } else {
                        tv_apppackname.setText(appVirusBean.getNappPackName());
                    }
                    TextView textView = new TextView(AntivirusActivity.this);
                    textView.setText(appVirusBean.getAppPackName());
                    if (appVirusBean.isVirus()) {
                        textView.setTextColor(Color.RED);
                    } else {
                        textView.setTextColor(Color.BLACK);
                    }
                    ll_appname.addView(textView, 0);
                    break;
                case FINISHED:
                    iv_scanning.clearAnimation();
                    break;
                default:
                    break;
            }
        }
    };

    private class AppVirusBean {
        private String appPackName;
        private String nappPackName;
        private boolean isVirus;

        public String getAppPackName() {
            return appPackName;
        }

        public void setAppPackName(String appPackName) {
            this.appPackName = appPackName;
        }

        public boolean isVirus() {
            return isVirus;
        }

        public void setVirus(boolean virus) {
            isVirus = virus;
        }

        public String getNappPackName() {
            return nappPackName;
        }

        public void setNappPackName(String nappPackName) {
            this.nappPackName = nappPackName;
        }
    }

    private void initData() {
        //扫描病毒
        new Thread() {

            @Override
            public void run() {
                handler.obtainMessage(START).sendToTarget();
                allAPK = APPMangerEngine.getAllAPK(AntivirusActivity.this);

                progress = 0;
                appVirusBean = new AppVirusBean();
                for (int i = 0; i < allAPK.size(); i++) {
                    APPBean bean = allAPK.get(i);
                    appVirusBean.setAppPackName(bean.getPackName());
                    String fileMD5 = MD5Utils.getFileMD5(bean.getPath());
                    appVirusBean.setVirus(AntivirusEngine.isVirus(AntivirusActivity.this, fileMD5));
                    Log.d("foundvirus", "apppath" + bean.getPath() + "    isvirus:" + appVirusBean.isVirus);
                    if (i != allAPK.size() - 1) {
                        appVirusBean.setNappPackName(allAPK.get(i + 1).getPackName());
                    } else {
                        appVirusBean.setNappPackName("");
                    }
                    progress ++;

                    handler.obtainMessage(SCANNING).sendToTarget();
                    SystemClock.sleep(50);
                }
                handler.obtainMessage(FINISHED).sendToTarget();
            }
        }.start();
    }

    private void initAnim() {
        rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(1000);
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        rotateAnimation.setInterpolator(new Interpolator() {
            @Override
            public float getInterpolation(float input) {
                return input;
            }
        });
    }

    private void initView() {
        setContentView(R.layout.activity_antivirus);

        iv_scanning = (ImageView) findViewById(R.id.iv_scanning_antivirus);
        pb_scan = (ProgressBar) findViewById(R.id.pb_scan_antivirus);
        tv_apppackname = (TextView) findViewById(R.id.tv_appname_antivirus);
        ll_appname = (LinearLayout) findViewById(R.id.sl_scanningname_antivirus);
    }
}
