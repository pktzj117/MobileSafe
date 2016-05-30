package org.pktzj.mobilesafe.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.pktzj.mobilesafe.R;
import org.pktzj.mobilesafe.domain.ContactBean;
import org.pktzj.mobilesafe.utils.MyConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pktzj on 2016/5/20.
 */
public abstract class TelCallSMSBaseActivity extends Activity {
    private static final int LOADING = 1;
    private static final int FINISH = 2;
    private ListView lv_contact;
    private ProgressDialog dialog;
    private List<ContactBean> contacts = new ArrayList<ContactBean>();
    private MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initEvent();
    }

    private void initEvent() {
        lv_contact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ContactBean contact = contacts.get(position);
                //获取号码
                String phone = contact.getNumber();
                Intent datas = new Intent();
                datas.putExtra(MyConstants.SELNUM, phone);
                //设置数据
                setResult(MyConstants.CONTACTSQCODE,datas);
                //关闭自己
                finish();
            }
        });
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOADING:
                    dialog = new ProgressDialog(TelCallSMSBaseActivity.this);
                    dialog.setTitle("获取联系人列表");
                    dialog.setMessage("正在玩命加载中......");
                    dialog.show();
                    break;
                case FINISH:
                    adapter.notifyDataSetChanged();
                    if (dialog != null) {
                        dialog.dismiss();
                        dialog = null;
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private void initData() {
        new Thread() {
            @Override
            public void run() {
                //显示获取数据的进度
                Message msg = Message.obtain();
                msg.what = LOADING;
                handler.sendMessage(msg);

                SystemClock.sleep(2000);
                contacts = getContactBeen();

                msg = Message.obtain();
                msg.what = FINISH;
                handler.sendMessage(msg);
            }
        }.start();
    }

    protected abstract List<ContactBean> getContactBeen();

    private void initView() {
        setContentView(R.layout.activity_contact);
        lv_contact = (ListView) findViewById(R.id.lv_contact);
        adapter = new MyAdapter();
        lv_contact.setAdapter(adapter);
    }

    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return contacts.size();
        }

        @Override
        public Object getItem(int position) {
            return contacts.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(getApplicationContext(), R.layout.item_listview_contact, null);
            TextView tv_name = (TextView) view.findViewById(R.id.tv_name_contacts);
            TextView tv_num = (TextView) view.findViewById(R.id.tv_num_contacts);

            tv_name.setText(contacts.get(position).getName());
            tv_num.setText(contacts.get(position).getNumber());
            return view;
        }
    }
}
