package org.pktzj.mobilesafe.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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

import java.util.ArrayList;
import java.util.List;

import static org.pktzj.mobilesafe.R.id.pb_taskmanager;

public class TaskManagerActivity extends AppCompatActivity {

    private static final int LOADING = 1;
    private static final int FINISHED = 2;
    private ListView lv_task;
    private ProgressBar pb_task;
    private TextView tv_tasknum;
    private TextView tv_memsize;
    private TextView tv_tasktitle;
    private List<TaskBean> userAPK = new ArrayList<TaskBean>();
    private List<TaskBean> sysAPK = new ArrayList<TaskBean>();
    private List<TaskBean> availAPKInfos = new ArrayList<TaskBean>();
    private MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initEvent();
    }

    private void initEvent() {


    }

    private void initData() {
        loaddata();
        adapter = new MyAdapter();
        lv_task.setAdapter(adapter);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOADING:
                    pb_task.setVisibility(View.VISIBLE);
                    lv_task.setVisibility(View.GONE);
                    break;
                case FINISHED:
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
                        sysAPK.add(bean);
                    } else {
                        userAPK.add(bean);
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
            return availAPKInfos.size() + 1;
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
            if (position == userAPK.size()) {
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

                itemView.cb_sel_item.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        bean.setChecked(isChecked);
                        Log.d("TaskManager", "bean" + bean.toString());
                        for (TaskBean taskBean : userAPK) {
                            Log.d("TaskManager", "userAPK" + taskBean.toString());
                        }
                        for (TaskBean taskBean : sysAPK) {
                            Log.d("TaskManager", "sysAPK" + taskBean.toString());
                        }
                    }
                });
                itemView.cb_sel_item.setChecked(bean.isChecked());
                return convertView;
            }
        }
    }

}
