package org.pktzj.mobilesafe.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.pktzj.mobilesafe.R;
import org.pktzj.mobilesafe.domain.TaskBean;
import org.pktzj.mobilesafe.engine.TaskManagerEngine;
import org.pktzj.mobilesafe.utils.MyConstants;
import org.pktzj.mobilesafe.utils.SPTool;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.pktzj.mobilesafe.R.id.pb_taskmanager;

public class TaskManagerActivity extends Activity {

    private static final int LOADING = 1;
    private static final int FINISHED = 2;
    private boolean initflag;
    private boolean showsysapp;
    private ListView lv_task;
    private ProgressBar pb_task;
    private TextView tv_tasknum;
    private TextView tv_memsize;
    private TextView tv_tasktitle;
    private List<TaskBean> userAPK = new ArrayList<TaskBean>();
    private List<TaskBean> sysAPK = new ArrayList<TaskBean>();
    private List<TaskBean> availAPKInfos = new ArrayList<TaskBean>();
    private MyAdapter adapter;
    private TaskBean clickbean;
    private Button bt_clear;
    private Button bt_seall;
    private Button bt_resel;
    private Button bt_setting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
        initData();
        initEvent();
    }

    private void initEvent() {
        lv_task.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("click", "listview click  " + position);
                if (position != userAPK.size()) {
                    clickbean = (TaskBean) lv_task.getItemAtPosition(position);
                    clickbean.setChecked(!clickbean.isChecked());
                    adapter.notifyDataSetChanged();
                }
            }
        });
        lv_task.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem >= userAPK.size()) {
                    tv_tasktitle.setText("系统进程(" + sysAPK.size() + ")");
                } else {
                    tv_tasktitle.setText("用户进程(" + userAPK.size() + ")");
                }
            }
        });

        //按键事件绑定
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.bt_clear_task:
                        cleartask();
                        break;
                    case R.id.bt_seall_task:
                        selectAll();
                        break;
                    case R.id.bt_resel_task:
                        reselect();
                        break;
                    case R.id.bt_setting_task:
                        Intent intent = new Intent(TaskManagerActivity.this, SettingTaskManagerActivity.class);
                        startActivity(intent);
                        break;
                    default:
                        break;
                }
            }
        };
        bt_clear.setOnClickListener(listener);
        bt_seall.setOnClickListener(listener);
        bt_resel.setOnClickListener(listener);
        bt_setting.setOnClickListener(listener);
    }

    private void cleartask() {
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (TaskBean bean : availAPKInfos) {
            if (bean.isChecked()) {
                am.killBackgroundProcesses(bean.getPackName());
            }
        }
        loaddata();
    }

    private void reselect() {
        Iterator<TaskBean> iterator = userAPK.iterator();
        while (iterator.hasNext()) {
            TaskBean bean = iterator.next();
            if (!bean.getPackName().equals(TaskManagerActivity.this.getPackageName())) {
                bean.setChecked(!bean.isChecked());
            }
        }
        iterator = sysAPK.iterator();
        while (iterator.hasNext()) {
            TaskBean bean = iterator.next();
            bean.setChecked(!bean.isChecked());
        }
        adapter.notifyDataSetChanged();
    }

    private void selectAll() {
        Iterator<TaskBean> iterator = userAPK.iterator();
        while (iterator.hasNext()) {
            TaskBean bean = iterator.next();
            if (!bean.getPackName().equals(TaskManagerActivity.this.getPackageName())) {
                bean.setChecked(true);
            }
        }
        iterator = sysAPK.iterator();
        while (iterator.hasNext()) {
            TaskBean bean = iterator.next();
            bean.setChecked(true);
        }
        adapter.notifyDataSetChanged();
    }

    private void initData() {
        showsysapp = SPTool.getboolean(TaskManagerActivity.this, MyConstants.SHOWSYSAPP, false);
        Log.d("showapp", showsysapp + "");
        loaddata();
        adapter = new MyAdapter();
        lv_task.setAdapter(adapter);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOADING:
                    if (!initflag) {
                        initflag = true;
                        pb_task.setVisibility(View.VISIBLE);
                        lv_task.setVisibility(View.GONE);
                    }
                    break;
                case FINISHED:
                    tv_tasktitle.setVisibility(View.VISIBLE);
                    pb_task.setVisibility(View.GONE);
                    lv_task.setVisibility(View.VISIBLE);
                    tv_tasknum.setText("总进程数(" + availAPKInfos.size() + ")");
                    tv_memsize.setText("可用运存:" +
                            Formatter.formatFileSize(TaskManagerActivity.this, TaskManagerEngine.getAvailMemSize(TaskManagerActivity.this))
                            + "/" +
                            Formatter.formatFileSize(TaskManagerActivity.this, TaskManagerEngine.getTotalMemSize(TaskManagerActivity.this)));
                    tv_tasktitle.setText("用户进程(" + userAPK.size() + ")");
                    adapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    };

    private void loaddata() {
        new Thread() {
            @Override
            public void run() {
                handler.obtainMessage(LOADING).sendToTarget();
                userAPK.clear();
                sysAPK.clear();
                availAPKInfos = TaskManagerEngine.getAvailAPKInfo(TaskManagerActivity.this);
                for (TaskBean bean : availAPKInfos) {
                    if (bean.isROMAPP()) {
                        if (showsysapp) {
                            sysAPK.add(bean);
                        }
                    } else {
                        userAPK.add(bean);
                        for (TaskBean bean1 : userAPK) {
                            Log.d("pakagename", bean1.getPackName());
                        }
                    }
                }
                handler.obtainMessage(FINISHED).sendToTarget();
            }
        }.start();
    }

    private void initView() {
        setContentView(R.layout.activity_task_manager);
        lv_task = (ListView) findViewById(R.id.lv_taskmanager);
        pb_task = (ProgressBar) findViewById(pb_taskmanager);
        tv_tasknum = (TextView) findViewById(R.id.tv_tasknum_taskmanager);
        tv_memsize = (TextView) findViewById(R.id.tv_memsize_taskmanager);
        tv_tasktitle = (TextView) findViewById(R.id.tv_title_taskmanger);
        bt_clear = (Button) findViewById(R.id.bt_clear_task);
        bt_seall = (Button) findViewById(R.id.bt_seall_task);
        bt_resel = (Button) findViewById(R.id.bt_resel_task);
        bt_setting = (Button) findViewById(R.id.bt_setting_task);

    }

    private class ItemView {
        private ImageView iv_icon_item;
        private TextView tv_name_item;
        private TextView tv_mem_item;
        private CheckBox cb_sel_item;
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (showsysapp) {
                return availAPKInfos.size() + 1;
            } else {
                return userAPK.size();
            }
        }

        @Override
        public Object getItem(int position) {
            if (position == userAPK.size()) {
                return null;
            } else if (position < userAPK.size()) {
                return userAPK.get(position);
            } else {
                return sysAPK.get(position - userAPK.size() - 1);
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (position == userAPK.size() && showsysapp) {
                TextView textView = new TextView(TaskManagerActivity.this);
                textView.setTextColor(Color.WHITE);
                textView.setBackgroundColor(Color.GRAY);
                textView.setText("系统进程(" + sysAPK.size() + ")");
                return textView;
            } else {
                ItemView itemView = null;
                if (convertView != null && convertView instanceof RelativeLayout) {
                    itemView = (ItemView) convertView.getTag();
                } else {
                    itemView = new ItemView();
                    convertView = View.inflate(TaskManagerActivity.this, R.layout.item_taskmanager_listview, null);
                    itemView.iv_icon_item = (ImageView) convertView.findViewById(R.id.iv_icon_taskmanager);
                    itemView.tv_name_item = (TextView) convertView.findViewById(R.id.tv_apkname_taskmanager);
                    itemView.tv_mem_item = (TextView) convertView.findViewById(R.id.tv_apkmem_taskmanager);
                    itemView.cb_sel_item = (CheckBox) convertView.findViewById(R.id.cb_sel_taskmanger);

                    convertView.setTag(itemView);
                }
                final TaskBean bean = (TaskBean) getItem(position);

                itemView.iv_icon_item.setImageDrawable(bean.getIcon());
                itemView.tv_name_item.setText(bean.getAppName());
                itemView.tv_mem_item.setText(Formatter.formatFileSize(TaskManagerActivity.this, bean.getSize()));

                if (TaskManagerActivity.this.getPackageName().equals(bean.getPackName())) {
                    itemView.cb_sel_item.setVisibility(View.GONE);
                    bean.setChecked(false);
                } else {
                    itemView.cb_sel_item.setVisibility(View.VISIBLE);
                    itemView.cb_sel_item.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            bean.setChecked(isChecked);
                        }
                    });
                    itemView.cb_sel_item.setChecked(bean.isChecked());
                }
                return convertView;
            }
        }
    }
}
