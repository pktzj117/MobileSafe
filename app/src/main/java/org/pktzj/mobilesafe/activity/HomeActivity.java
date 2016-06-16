package org.pktzj.mobilesafe.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.pktzj.mobilesafe.R;
import org.pktzj.mobilesafe.service.LostFindService;
import org.pktzj.mobilesafe.utils.MD5Utils;
import org.pktzj.mobilesafe.utils.MyConstants;
import org.pktzj.mobilesafe.utils.SPTool;

public class HomeActivity extends Activity {
    private static final String TAG = "HomeActivity";
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initDate();
        initEvent();
    }

    private void initEvent() {
        MyAdapter adapter = new MyAdapter();
        gv_menus_home.setAdapter(adapter);
        gv_menus_home.setOnItemClickListener(new MyOnClickListener());
    }

    private void initDate() {
        if (SPTool.getboolean(HomeActivity.this, MyConstants.ISSETUP, false)) {
            Intent service = new Intent(HomeActivity.this, LostFindService.class);
            startService(service);
        }
    }

    private void initView() {
        setContentView(R.layout.activity_home);
        gv_menus_home = (GridView) findViewById(R.id.gv_menus_home);
    }

    private class MyAdapter extends BaseAdapter {
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

    private class MyOnClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (position) {
                case 0://"手机防盗":
                    String password = SPTool.getString(getApplicationContext(), MyConstants.PASSWORD , "");
                    if (TextUtils.isEmpty(password)) {
                        MySetPassDialog();
                    } else {
                        MyEnterPassDialog(password);
                    }
                    break;
                case 1://"通讯卫士":
                    startActivity(TelSmsSafeActivity.class);
                    break;
                case 2://"软件管家":
                    startActivity(APPManagerActivity.class);
                    break;
                case 3://"进程管理":
                    startActivity(TaskManagerActivity.class);
                    break;
                case 4://"流量统计":
                    startActivity(NetTrafficActivity.class);
                    break;
                case 5://"病毒查杀":
                    startActivity(AntivirusActivity.class);
                    break;
                case 6://"缓存清除":
                    startActivity(CleanCacheActivity.class);
                    break;
                case 7://"高级工具":
                    startActivity(AdvanceToolActivity.class);
                    break;
                case 8://"设置中心":
                    startActivity(SettingCenter.class);
                    break;
            }
        }
    }


    private void startActivity(Class clazz) {
        Intent intent = new Intent(HomeActivity.this, clazz);
        startActivity(intent);
    }

    private void MyEnterPassDialog(final String pd) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(this, R.layout.dialog_enter_password, null);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        final EditText et_password = (EditText) view.findViewById(R.id.et_password);
        final Button bt_enter_enterpassword = (Button) view.findViewById(R.id.bt_enter_enterpass);
        final Button bt_cancel_enterpassword = (Button) view.findViewById(R.id.bt_cancel_enterpass);
        bt_enter_enterpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //确定比对密码
                String enterpassword = et_password.getText().toString();

                if (TextUtils.isEmpty(enterpassword) || enterpassword.equals(pd)) {
                    Toast.makeText(getApplicationContext(), "输入密码为空!", Toast.LENGTH_SHORT).show();
                    et_password.setText("");
                } else {
                    //保存密码
                    String password = MD5Utils.md5(MD5Utils.md5(enterpassword));
                    if (password.equals(pd)) {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), "登录成功!", Toast.LENGTH_SHORT).show();
                        loadlostfoud();
                    } else {
                        Toast.makeText(getApplicationContext(), "密码错误!", Toast.LENGTH_SHORT).show();
                        et_password.setText("");
                    }
                }
            }
        });
        bt_cancel_enterpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    private void loadlostfoud() {
        Intent intent = new Intent(HomeActivity.this, LostFindActivity.class);
        startActivity(intent);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void MySetPassDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = View.inflate(this, R.layout.dialog_set_password, null);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        final EditText et_passwordone = (EditText) view.findViewById(R.id.et_passwordone);
        final EditText et_passwordtwo = (EditText) view.findViewById(R.id.et_passwordtwo);
        final Button bt_enter_setpassword = (Button) view.findViewById(R.id.bt_enter_setpass);
        final Button bt_cancel_setpassword = (Button) view.findViewById(R.id.bt_cancel_setpass);
        bt_enter_setpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //确定比对密码
                String passwordone = et_passwordone.getText().toString();
                String passwordtwo = et_passwordtwo.getText().toString();
                if (TextUtils.isEmpty(passwordone) || TextUtils.isEmpty(passwordtwo)) {
                    Toast.makeText(getApplicationContext(), "输入密码不能为空!", Toast.LENGTH_SHORT).show();
                } else if (!passwordone.equals(passwordtwo)) {
                    Toast.makeText(getApplicationContext(), "两次输入密码不一致!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "密码设置成功!", Toast.LENGTH_SHORT).show();
                    //保存密码
                    String password = MD5Utils.md5(MD5Utils.md5(passwordone));
                    Log.d(TAG, "password: " + password);
                    SPTool.putString(getApplicationContext(), MyConstants.PASSWORD, password);
                    dialog.dismiss();
                }
            }
        });
        bt_cancel_setpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
