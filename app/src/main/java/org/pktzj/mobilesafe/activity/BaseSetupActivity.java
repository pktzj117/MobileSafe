package org.pktzj.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import org.pktzj.mobilesafe.R;

/**
 * Created by pktzj on 2016/5/20.
 */
public abstract class BaseSetupActivity extends Activity {
    private GestureDetector detector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化控件
        initView();
        //初始化手势
        initGestyres();
        //初始化数据
        initData();
        //初始化事件
        initEvet();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    private void initGestyres() {
        detector = new GestureDetector(this, new MyOnGestureListener());
    }

    protected void initEvet() {
    }

    protected void initData() {
    }

    public abstract void initView();

    public void startActivity(Class clazz) {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
        finish();
    }

    public void next(View view) {
        //切换到下一个界面
        newxActivity();
        //切换动画
        nextAnimation();
    }

    public void nextAnimation() {
        overridePendingTransition(R.anim.next_in, R.anim.next_out);
    }

    public abstract void newxActivity();

    public void prev(View view) {
        //切换到上一个界面
        prevActivity();
        //切换动画
        prevAnimation();
    }

    private void prevAnimation() {
        overridePendingTransition(R.anim.prev_in, R.anim.prev_out);
    }

    public abstract void prevActivity();

    private class MyOnGestureListener implements GestureDetector.OnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (Math.abs(velocityX) > 300) {
                float dx = e1.getX() - e2.getX();
                if (Math.abs(dx) > 200) {
                    if (dx > 0) {
                        next(null);
                    } else {
                        prev(null);
                    }
                }
            }
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

    }
}

