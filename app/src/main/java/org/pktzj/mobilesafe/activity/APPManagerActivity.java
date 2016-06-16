package org.pktzj.mobilesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
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
    private TextView tv_apptitle;
    private TextView tv_sdcardsize;
    private TextView tv_romsize;
    private PopupWindow popupWindow;
    private boolean isPopShow;
    private View popView;
    private ScaleAnimation sa;
    private APPBean clickBean;
    private PackageManager pm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initEvent();
    }

    private void initEvent() {
        lv_app.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // 获取当前点击的位置的值，如果点击标签不做处理
                // 如果点击的是系统软件标签位置，不做处理
                if (position == sdAPK.size()) {
                    return;
                }
                // 点击是条目的信息，获取当前点击位置的信息

                // 获取数据listview.getItemAtPosition 本质调用adapter.getItem();
                clickBean = (APPBean) lv_app.getItemAtPosition(position);
                int[] location = new int[2];
                view.getLocationInWindow(location);
                showPopupWindow(view, location[0] + 130, location[1]);
            }
        });
        lv_app.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                closePopup();
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem <= (sdAPK.size())) {
                    tv_apptitle.setVisibility(View.VISIBLE);
                    tv_apptitle.setText("用户软件(" + sdAPK.size() + ")");
                } else {
                    tv_apptitle.setVisibility(View.VISIBLE);
                    tv_apptitle.setText("系统软件(" + romAPK.size() + ")");
                }
            }
        });


    }

    private void initPopup() {
        popView = View.inflate(APPManagerActivity.this, R.layout.popup_itemclick_appmanager, null);
        LinearLayout ll_remove_popup = (LinearLayout) popView.findViewById(R.id.ll_remove_popup);
        LinearLayout ll_setup_popup = (LinearLayout) popView.findViewById(R.id.ll_setup_popup);
        LinearLayout ll_share_popup = (LinearLayout) popView.findViewById(R.id.ll_share_popup);
        LinearLayout ll_setting_popup = (LinearLayout) popView.findViewById(R.id.ll_setting_popup);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.ll_remove_popup://卸载
                        removeAPK();
                        break;
                    case R.id.ll_setup_popup://启动
                        startAPK();
                        break;
                    case R.id.ll_share_popup://分享
                        shareAPK();
                        break;
                    case R.id.ll_setting_popup://设置
                        setAPK();
                        break;
                    default:
                        break;
                }
                closePopup();// 关闭原来的弹出窗体
            }
        };

        ll_remove_popup.setOnClickListener(listener);
        ll_setup_popup.setOnClickListener(listener);
        ll_share_popup.setOnClickListener(listener);
        ll_setting_popup.setOnClickListener(listener);

        popupWindow = new PopupWindow(popView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);//popView.getHeight()
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // 弹出窗体的动画
        sa = new ScaleAnimation(0, 1, 0.5f, 1, Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        sa.setDuration(300);
    }

    private void setAPK() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + clickBean.getPackName()));
        startActivity(intent);
    }

    private void shareAPK() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "fenxiang");
        startActivity(intent);
    }

    private void startAPK() {
        //通过包名获得意图
        Intent launchIntentForPackage = pm.getLaunchIntentForPackage(clickBean.getPackName());
        startActivity(launchIntentForPackage);
    }

    private void removeAPK() {
        if (clickBean.isSDAPP()) {
            Intent intent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE);
            intent.setData(Uri.parse("package:" + clickBean.getPackName()));
            startActivity(intent);
        }
    }

    private void showPopupWindow(View parrent, int x, int y) {
        closePopup();// 关闭原来的弹出窗体
        popupWindow.showAtLocation(parrent, Gravity.LEFT | Gravity.TOP, x, y);
        popView.startAnimation(sa);
        isPopShow = true;

    }

    private void closePopup() {
        if (popupWindow != null && isPopShow == true) {
            popupWindow.dismiss();
        }
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
                    tv_sdcardsize.setText("ROM可用空间: " + Formatter.formatFileSize(getApplicationContext(), APPMangerEngine.getSDcardFree()));
                    tv_romsize.setText("SD卡可用空间: " + Formatter.formatFileSize(getApplicationContext(), APPMangerEngine.getRomFree()));
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    private void initData() {
        loaddata();
        pm = getPackageManager();
        adapter = new MyAdapter();

        lv_app.setAdapter(adapter);

        initPopup();
    }

    private void loaddata() {
        new Thread() {
            @Override
            public void run() {
                handler.obtainMessage(LOADING).sendToTarget();
                //加载数据
                allAPK = APPMangerEngine.getAllAPK(APPManagerActivity.this);
                romAPK.clear();
                sdAPK.clear();
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
    }

    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return romAPK.size() + sdAPK.size() + 1;
        }

        @Override
        public Object getItem(int position) {
            if (position < sdAPK.size()) {
                return sdAPK.get(position);
            } else {
                return romAPK.get(position - sdAPK.size() - 1);
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (position == sdAPK.size()) {
                TextView textView = new TextView(APPManagerActivity.this);
                textView.setClickable(false);
                textView.setText("系统软件(" + romAPK.size() + ")");
                textView.setTextColor(Color.WHITE);
                textView.setBackgroundColor(Color.GRAY);
                return textView;
            } else {
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
                itemapk.tv_apksize.setText(Formatter.formatFileSize(getApplicationContext(), bean.getSize()));

            }

            return convertView;
        }
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
        tv_apptitle = (TextView) findViewById(R.id.tv_title_appmanger);
        tv_sdcardsize = (TextView) findViewById(R.id.tv_sdcardsize_appmanager);
        tv_romsize = (TextView) findViewById(R.id.tv_romsize_appmanager);
    }
}
