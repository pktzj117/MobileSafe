package org.pktzj.mobilesafe.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.pktzj.mobilesafe.R;
import org.pktzj.mobilesafe.domain.APPBean;
import org.pktzj.mobilesafe.engine.APPMangerEngine;

import java.util.ArrayList;
import java.util.List;

public class APPManagerActivity extends Activity {

    private static final int LOADING = 1;
    private static final int FINISHE = 2;
    private ListView lv_app;
    private ProgressBar pb_app;
    List<APPBean> allAPK;
    private List<APPBean> romAPK = new ArrayList<APPBean>();
    private List<APPBean> sdAPK = new ArrayList<APPBean>();
    private BaseAdapter adapter;
    private ItemAPK itemapk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initEvent();
    }

    private void initEvent() {

    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOADING:
                    pb_app.setVisibility(View.VISIBLE);
                    lv_app.setVisibility(View.GONE);
                    break;
                case FINISHE:
                    pb_app.setVisibility(View.GONE);
                    lv_app.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };

    private void initData() {
        new Thread() {
            @Override
            public void run() {
                handler.obtainMessage(LOADING).sendToTarget();
                //加载数据
                allAPK = APPMangerEngine.getAllAPK(APPManagerActivity.this);
                for (APPBean appBean : allAPK) {
                    if (appBean.isROMAPP()) {
                        //系统应用
                        romAPK.add(appBean);
                    } else {
                        //SD卡应用
                        sdAPK.add(appBean);
                    }
                }
                handler.obtainMessage(FINISHE).sendToTarget();
            }
        }.start();

        adapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return romAPK.size() + 1 + sdAPK.size() + 1;
            }

            @Override
            public Object getItem(int position) {
                if (position < sdAPK.size() + 1) {
                    return sdAPK.get(position - 1);
                } else {
                    return romAPK.get(position - 1);
                }
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (position == 0) {
                    TextView textView = new TextView(APPManagerActivity.this);
                    textView.setText("用户软件(" + sdAPK.size() + ")");
                    textView.setTextColor(Color.WHITE);
                    textView.setBackgroundColor(Color.GRAY);
                    return textView;
                } else if (position == sdAPK.size() + 1) {
                    TextView textView = new TextView(APPManagerActivity.this);
                    textView.setText("系统软件(" + romAPK.size() + ")");
                    textView.setTextColor(Color.WHITE);
                    textView.setBackgroundColor(Color.GRAY);
                    return textView;
                }else{
                        itemapk = new ItemAPK();
                    if (convertView != null && convertView instanceof RelativeLayout) {
                        itemapk = (ItemAPK) convertView.getTag();
                    } else {
                        convertView = View.inflate(APPManagerActivity.this, R.layout.item_apkmanager_listview, null);
                        itemapk.iv_apkicon = (ImageView) convertView.findViewById(R.id.iv_icon_apkmanager);
                        itemapk.tv_apkname = (TextView) convertView.findViewById(R.id.tv_apkname_apkmanager);
                        itemapk.tv_apkloca = (TextView) convertView.findViewById(R.id.tv_apkloca_apkmanager);
                        itemapk.tv_apksize = (TextView) convertView.findViewById(R.id.tv_apksize_apkmanger);
                        convertView.setTag(itemapk);
                    }

                    APPBean bean = (APPBean) getItem(position);

                        itemapk.iv_apkicon.setImageDrawable(bean.getIcon());
                        itemapk.tv_apkname.setText(bean.getAppName());
                        itemapk.tv_apkloca.setText(bean.getPackName());
                        itemapk.tv_apksize.setText(Formatter.formatFileSize(getApplicationContext(),bean.getSize()));

                }

                return convertView;
            }
        };
        lv_app.setAdapter(adapter);
    }

    private class ItemAPK {
        private ImageView iv_apkicon;
        private TextView tv_apkname;
        private TextView tv_apkloca;
        private TextView tv_apksize;

    }

    private void initView() {
        setContentView(R.layout.activity_appmanager);
        lv_app = (ListView) findViewById(R.id.lv_appmanager);
        pb_app = (ProgressBar) findViewById(R.id.pb_appmanager);
    }
}
