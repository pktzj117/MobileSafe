package org.pktzj.mobilesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import org.pktzj.mobilesafe.R;

public class HomeActivity extends Activity {
    private GridView gv_menus_home;

    private int icons[] = {
            R.drawable.safe,
            R.drawable.callmsgsafe,
            R.drawable.app,
            R.drawable.taskmanager,
            R.drawable.netmanager,
            R.drawable.trojan,
            R.drawable.sysoptimize,
            R.drawable.atools,
            R.drawable.settings,
    };
    private String names[] = {
            "手机防盗",
            "通讯卫士",
            "软件管家",
            "进程管理",
            "流量统计",
            "病毒查杀",
            "缓存清除",
            "高级工具",
            "设置中心",
    };

    private MyAdapter adapter;//GridView的适配器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initDate();
    }

    private void initDate() {
        adapter = new MyAdapter();
        gv_menus_home.setAdapter(adapter);
    }

    private void initView() {
        setContentView(R.layout.activity_home);
        gv_menus_home = (GridView) findViewById(R.id.gv_menus_home);
    }

    private class MyAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return icons.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(getApplicationContext(), R.layout.item_gridview_home, null);
            ImageView iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
            TextView tv_name = (TextView) view.findViewById(R.id.tv_name);

            //设置数据
            iv_icon.setImageResource(icons[position]);
            tv_name.setText(names[position]);

            return view;
        }
    }
}
