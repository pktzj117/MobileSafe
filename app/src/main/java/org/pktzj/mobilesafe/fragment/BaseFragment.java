package org.pktzj.mobilesafe.fragment;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import org.pktzj.mobilesafe.dao.AppLockDAO;
import org.pktzj.mobilesafe.domain.APPBean;

import java.util.List;


/**
 * Created by pktzj on 2016/6/10.
 */

public class BaseFragment extends android.support.v4.app.Fragment {
    private static final int LOADING = 1;
    private static final int FINISHED = 2;

    ;
    private AppLockDAO appLockDAO;
    private List<String> allLockapps;

    protected boolean islocked = true;
    private List<APPBean> allAPK;

    public BaseFragment(List<APPBean> allAPK) {
        this.allAPK = allAPK;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData();
        initEvent();
    }

    private void initEvent() {

    }


    /**
     * @param iv_lock
     *     加锁的按钮
     * @param convertView
     *     item的根布局
     * @param packName
     *     包名
     */
    public void setImageViewEventAndBg(ImageView iv_lock, View convertView, String packName){

    }
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case LOADING:

                    break;
                case FINISHED:

                    break;
                default:
                    break;
            }
        }
    };

    private void initData() {
        appLockDAO = new AppLockDAO(getActivity());
        loaddata();
    }

    private void loaddata() {
        new Thread() {

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void run() {
                handler.obtainMessage(LOADING).sendToTarget();
                //SystemClock.sleep(2000);

                islocked();
                //获得所有应用

                //从数据库中获得所有加锁应用
                allLockapps = appLockDAO.getAllLock();
                for (APPBean appBean : allAPK) {

                }
                handler.obtainMessage(FINISHED).sendToTarget();
            }
        }.start();
    }

    protected void islocked() {
        this.islocked = false;
    }


}
