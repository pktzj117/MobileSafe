package org.pktzj.mobilesafe.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import org.pktzj.mobilesafe.R;
import org.pktzj.mobilesafe.engine.PhoneLocationEngine;
import org.pktzj.mobilesafe.utils.MyConstants;
import org.pktzj.mobilesafe.utils.SPTool;

public class PhoneLocationService extends Service {
    private static final String TAG = "PhoneLocationService";
    private TelephonyManager tm;
    private WindowManager.LayoutParams mParams ;
    private WindowManager wm ;
    private View view;
    private PhoneStateListener listener;
    private float startX;
    private float startY;

    public PhoneLocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "PhoneLocationService is run");
        initParams();

        wm = (WindowManager) getSystemService(WINDOW_SERVICE);

        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        listener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                super.onCallStateChanged(state, incomingNumber);
                switch (state) {
                    case TelephonyManager.CALL_STATE_IDLE:
                        //关闭吐司
                        closeLocationToast();
                        break;
                    case TelephonyManager.CALL_STATE_RINGING:
                        showLocationToast(incomingNumber);
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        //关闭吐司
                        closeLocationToast();
                        break;
                    default:
                        break;
                }
            }
        };
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    private void initParams() {
        // XXX This should be changed to use a Dialog, with a Theme.Toast
        // defined that sets up the layout params appropriately.
        mParams = new WindowManager.LayoutParams();
        mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.format = PixelFormat.TRANSLUCENT;
        mParams.windowAnimations = android.R.style.Animation_Toast;
        mParams.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;  //类型
        mParams.setTitle("Toast");
        mParams.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                //| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                //| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        ;
        //对齐方式左上角
        mParams.gravity = Gravity.LEFT | Gravity.TOP;
        mParams.x = 0;
        mParams.y = 0;
        mParams.setTitle("Toast");
    }
    private void showLocationToast(String incomingNumber) {


        view = View.inflate(this, R.layout.tost_phone_location, null);

        TextView tv_phonelocation = (TextView) view.findViewById(R.id.tv_phonelocation_toast);

        tv_phonelocation.setText(PhoneLocationEngine.locationQuery(this,incomingNumber));

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getRawX();
                        startY = event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        // 新的x y坐标
                        float rawX = event.getRawX();
                        float rawY = event.getRawY();

                        float dx = rawX - startX;
                        float dy = rawY - startY;

                        mParams.x += dx;
                        mParams.y += dy;


                        //更新吐司位置
                        wm.updateViewLayout(view,mParams);

                        break;
                    case MotionEvent.ACTION_UP:
                        Point size = new Point();
                        wm.getDefaultDisplay().getSize(size);
                        if (mParams.x < 0) {
                            mParams.x = 0;
                        } else if (mParams.x + view.getWidth() > size.x) {
                            mParams.x = size.x - v.getWidth();
                        }
                        if (mParams.y < 0) {
                            mParams.y = 0;
                        } else if (mParams.y + view.getHeight() > size.x) {
                            mParams.y = size.y - v.getHeight();
                        }
                        SPTool.putString(getApplicationContext(), MyConstants.TOASTX, mParams.x + "");
                        SPTool.putString(getApplicationContext(), MyConstants.TOASTY, mParams.y + "");
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

        wm.addView(view, mParams);
    }



    private void closeLocationToast() {
        if (view != null) {
            wm.removeView(view);
            view = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        tm.listen(listener, PhoneStateListener.LISTEN_NONE);
    }

}
