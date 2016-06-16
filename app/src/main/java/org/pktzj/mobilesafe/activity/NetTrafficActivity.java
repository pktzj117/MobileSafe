package org.pktzj.mobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
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
import android.widget.TextView;

import org.pktzj.mobilesafe.R;
import org.pktzj.mobilesafe.domain.APPBean;
import org.pktzj.mobilesafe.engine.APPMangerEngine;
import org.pktzj.mobilesafe.engine.NetTrafficEngine;

import java.util.ArrayList;
import java.util.List;

public class NetTrafficActivity extends Activity {

    private static final int LOADING = 1;
    private static final int FINISHED = 2;
    private ListView lv_nettraffic;
    private ProgressBar pb_nettraffic;
    private List<APPBean> apps = new ArrayList<APPBean>();
    private PackageManager am;
    private MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOADING:
                    lv_nettraffic.setVisibility(View.INVISIBLE);
                    pb_nettraffic.setVisibility(View.VISIBLE);
                    break;
                case FINISHED:
                    adapter.notifyDataSetChanged();
                    pb_nettraffic.setVisibility(View.INVISIBLE);
                    lv_nettraffic.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
        }
    };
    private void initData() {
        am = getPackageManager();
        new Thread(){
            @Override
            public void run() {
                handler.obtainMessage(LOADING).sendToTarget();

                apps = APPMangerEngine.getAllAPK(getApplicationContext());

                for (int i = 0;i < apps.size() ; i ++) {
                    List<String> list = NetTrafficEngine.QueryNetTraffic(apps.get(i).getUid());
                    if (list.size() < 2) {
                        apps.remove(i);
                        i--;
                        continue;
                    }
                    apps.get(i).setRcv(list.get(0));
                    apps.get(i).setSnd(list.get(1));
                }
                handler.obtainMessage(FINISHED).sendToTarget();
            }
        }.start();
        adapter = new MyAdapter();
        lv_nettraffic.setAdapter(adapter);
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return apps.size();
        }

        @Override
        public Object getItem(int position) {
            return apps.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Item item = null;
            if (convertView == null) {
                item = new Item();
                convertView = convertView.inflate(getApplicationContext(), R.layout.item_nettraffic_listview, null);
                item.appImage = (ImageView) convertView.findViewById(R.id.iv_icon_nettraffic);
                item.textView = (TextView) convertView.findViewById(R.id.tv_apkname_nettraffic);
                item.btImage = (ImageView) convertView.findViewById(R.id.iv_sel_nettraffic);
                convertView.setTag(item);
            } else {
                item = (Item) convertView.getTag();
            }
            final APPBean bean = (APPBean) getItem(position);
            item.appImage.setImageDrawable(bean.getIcon());
            item.textView.setText(bean.getAppName());
            item.btImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<String> list = NetTrafficEngine.QueryNetTraffic(bean.getUid());
                    AlertDialog.Builder builder = new AlertDialog.Builder(NetTrafficActivity.this);
                    builder.setTitle("流量详情")
                            .setMessage("rcv:" + Formatter.formatFileSize(NetTrafficActivity.this, Long.parseLong(bean.getRcv())) +
                                    "   snd:" + Formatter.formatFileSize(NetTrafficActivity.this, Long.parseLong(bean.getSnd())));
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            });
            return convertView;
        }
        private class Item{
            private ImageView appImage;
            private TextView textView;
            private ImageView btImage;
        }
    }
    private void initView() {
        setContentView(R.layout.activity_net_traffic);
        lv_nettraffic = (ListView) findViewById(R.id.lv_nettraffic);
        pb_nettraffic = (ProgressBar) findViewById(R.id.pb_nettraffic);
    }
}
