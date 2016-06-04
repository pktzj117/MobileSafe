package org.pktzj.mobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.pktzj.mobilesafe.R;
import org.pktzj.mobilesafe.dao.BlackDAO;
import org.pktzj.mobilesafe.domain.BlackBean;
import org.pktzj.mobilesafe.utils.MyConstants;

import java.util.ArrayList;
import java.util.List;

public class TelSmsSafeActivity extends Activity {
    private static final int LOADING = 1;
    private static final int LOADINGCOMPLETED = 2;
    private static final int NOMOREDATA = 3;
    private List<BlackBean> blacknums = new ArrayList<BlackBean>();
    private TextView tv_blacknum;
    private ListView lv_blacknum;
    private ProgressBar pb_blacknum;
    private BlackDAO blackDAO;
    private MyAdapter adapter;
    private PopupWindow popupWindow;
    private boolean isPopShow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        iniEvent();
        initPopup();

    }

    private void closePopup() {
        if (popupWindow != null && isPopShow == true) {
            popupWindow.dismiss();
        }
    }

    private void initPopup() {
        final View popView = View.inflate(TelSmsSafeActivity.this, R.layout.popup_addblacknum_telsmssafe, null);
        TextView tv_manual = (TextView) popView.findViewById(R.id.tv_popup_manual);
        TextView tv_calllog = (TextView) popView.findViewById(R.id.tv_popup_calllog);
        TextView tv_phonelog = (TextView) popView.findViewById(R.id.tv_popup_phonelog);
        TextView tv_smslog = (TextView) popView.findViewById(R.id.tv_popup_smslog);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.tv_popup_manual://手动添加
                        Toast.makeText(TelSmsSafeActivity.this, "手动添加", Toast.LENGTH_SHORT).show();
                        initDialog(null, "输入黑名单号码");
                        break;
                    case R.id.tv_popup_calllog:
                        Toast.makeText(TelSmsSafeActivity.this, "联系导入", Toast.LENGTH_SHORT).show();
                    {
                        Intent intent = new Intent(TelSmsSafeActivity.this, ContactsActivity.class);
                        startActivityForResult(intent, MyConstants.CONTACTSQCODE);
                    }
                    break;
                    case R.id.tv_popup_phonelog:
                        Toast.makeText(TelSmsSafeActivity.this, "电话导入", Toast.LENGTH_SHORT).show();
                    {
                        Intent intent = new Intent(TelSmsSafeActivity.this, CallLogActivity.class);
                        startActivityForResult(intent, MyConstants.CONTACTSQCODE);
                    }
                    break;
                    case R.id.tv_popup_smslog:
                        Toast.makeText(TelSmsSafeActivity.this, "短信导入", Toast.LENGTH_SHORT).show();
                    {
                        Intent intent = new Intent(TelSmsSafeActivity.this, CallLogActivity.class);
                        startActivityForResult(intent, MyConstants.CONTACTSQCODE);
                    }
                    break;
                    default:
                        break;
                }
            }
        };

        tv_manual.setOnClickListener(listener);
        tv_calllog.setOnClickListener(listener);
        tv_phonelog.setOnClickListener(listener);
        tv_smslog.setOnClickListener(listener);

        popupWindow = new PopupWindow(popView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        isPopShow = true;
    }

    private void initDialog(String content, String hint) {

        AlertDialog.Builder builder = new AlertDialog.Builder(TelSmsSafeActivity.this);
        View view = View.inflate(TelSmsSafeActivity.this, R.layout.dialog_changemode_blacknum, null);
        final EditText et_phone = (EditText) view.findViewById(R.id.et_phone_blacknum);
        final CheckBox cb_phone = (CheckBox) view.findViewById(R.id.cb_phonemode_blacknum);
        final CheckBox cb_sms = (CheckBox) view.findViewById(R.id.cb_smsmode_blacknum);
        Button bt_enter = (Button) view.findViewById(R.id.bt_enter_blacknum);
        Button bt_cancel = (Button) view.findViewById(R.id.bt_cancel_blacknum);
        if (content != null) {
            et_phone.setText(content);
        } else if (hint != null) {
            et_phone.setHint(hint);
        }


        builder.setView(view);
        final AlertDialog dialog = builder.create();
        //设置事件
        bt_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int mode = 0;
                BlackBean blackBean = new BlackBean();
                blackBean.setPhone(et_phone.getText().toString().trim());
                if (cb_phone.isChecked()) {
                    mode = mode | BlackDAO.PHONEMODE;
                }
                if (cb_sms.isChecked()) {
                    mode = mode | BlackDAO.SMSMODE;
                }
                //对数据进行检查后再进行修改
                if (mode == 0) {
                    Toast.makeText(TelSmsSafeActivity.this, "至少选择一种拦截模式!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(blackBean.getPhone())) {
                    Toast.makeText(TelSmsSafeActivity.this, "拦截号码不能为空!", Toast.LENGTH_SHORT).show();
                    return;
                }

                //对数据进行修改  并通知

                blackBean.setMode(mode);
                blacknums.add(0, blackBean);
                blackDAO.dupdate(blackBean);
                //发送消息：数据加载完成
                handler.obtainMessage(LOADINGCOMPLETED).sendToTarget();
                dialog.dismiss();
                closePopup();
            }
        });
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //取消
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            String phone = data.getStringExtra(MyConstants.SELNUM);
            initDialog(phone, null);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOADING:
                    pb_blacknum.setVisibility(View.VISIBLE);
                    tv_blacknum.setVisibility(View.GONE);
                    lv_blacknum.setVisibility(View.GONE);
                    break;
                case LOADINGCOMPLETED:
                    pb_blacknum.setVisibility(View.GONE);
                    tv_blacknum.setVisibility(View.GONE);
                    lv_blacknum.setVisibility(View.VISIBLE);
                    adapter.notifyDataSetChanged();
                    break;
                case NOMOREDATA:
                    lv_blacknum.setVisibility(View.VISIBLE);
                    tv_blacknum.setVisibility(View.GONE);
                    pb_blacknum.setVisibility(View.GONE);
                    Toast.makeText(TelSmsSafeActivity.this, "没有更多数据!", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    private void initData() {

        blackDAO = new BlackDAO(TelSmsSafeActivity.this);

        //加载黑名单数据
        loadMoreData();


        adapter = new MyAdapter();
        lv_blacknum.setAdapter(adapter);

    }

    private void loadMoreData() {
        //查询数据库 耗时的操作放在子线程中实现
        new Thread() {
            @Override
            public void run() {

                //发送消息：正在加载数据
                Message message = Message.obtain();
                message.what = LOADING;
                handler.sendMessage(message);

                //获取数据

                List<BlackBean> moreData = blackDAO.getDatas(blacknums.size(), BlackDAO.PAGENUM);
                if (moreData.size() == 0) {
                    handler.obtainMessage(NOMOREDATA).sendToTarget();
                } else {

                    blacknums.addAll(moreData);
                    //调试演示
                    SystemClock.sleep(1000);

                    //发送消息：数据加载完成
                    handler.obtainMessage(LOADINGCOMPLETED).sendToTarget();
                }
            }
        }.start();
    }

    private void initView() {
        setContentView(R.layout.activity_telsmssafe);
        tv_blacknum = (TextView) findViewById(R.id.tv_blacknum_telsmssafe);
        lv_blacknum = (ListView) findViewById(R.id.lv_blacknum_telsmssafe);
        pb_blacknum = (ProgressBar) findViewById(R.id.pb_blacknum_telsmssafe);
    }

    private void iniEvent() {
        lv_blacknum.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE) {
                    // 判断是否显示最后一条数据，如果显示最后一条数据，那就加载更多的数据
                    // 获取最后显示的数据位置
                    int lostPosition = lv_blacknum.getLastVisiblePosition();
                    if (lostPosition == blacknums.size() - 1) {
                        loadMoreData();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
    }

    class ItemView {
        //显示的黑名单号码
        TextView tv_phone;
        //显示的拦截模式
        TextView tv_mode;
        //显示的删除按钮
        ImageButton ib_delete;
    }

    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            int size = blacknums.size();
            if (size == 0) {
                lv_blacknum.setVisibility(View.GONE);
                tv_blacknum.setVisibility(View.VISIBLE);
                pb_blacknum.setVisibility(View.GONE);
            }

            return size;
        }

        @Override
        public Object getItem(int position) {
            return blacknums.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        private ItemView itemView;

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(TelSmsSafeActivity.this, R.layout.item_blacknum_listview, null);
                itemView = new ItemView();
                itemView.tv_phone = (TextView) convertView.findViewById(R.id.tv_phone_blacknum);
                itemView.tv_mode = (TextView) convertView.findViewById(R.id.tv_mode_blacknum);
                itemView.ib_delete = (ImageButton) convertView.findViewById(R.id.ib_delete_blacknum);
                // 设置标记给convertView
                convertView.setTag(itemView);
            } else {
                // 存在缓存
                itemView = (ItemView) convertView.getTag();
            }


            final BlackBean blacknum = blacknums.get(position);
            itemView.tv_phone.setText("拦截号码: " + blacknum.getPhone());
            switch (blacknum.getMode()) {
                case BlackDAO.ALLMODE:
                    itemView.tv_mode.setText("全部拦截");
                    break;
                case BlackDAO.PHONEMODE:
                    itemView.tv_mode.setText("电话拦截");
                    break;
                case BlackDAO.SMSMODE:
                    itemView.tv_mode.setText("短息拦截");
                    break;
                default:
                    break;
            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(TelSmsSafeActivity.this);
                    View view = View.inflate(TelSmsSafeActivity.this, R.layout.dialog_changemode_blacknum, null);
                    final EditText et_phone = (EditText) view.findViewById(R.id.et_phone_blacknum);
                    final CheckBox cb_phone = (CheckBox) view.findViewById(R.id.cb_phonemode_blacknum);
                    final CheckBox cb_sms = (CheckBox) view.findViewById(R.id.cb_smsmode_blacknum);
                    Button bt_enter = (Button) view.findViewById(R.id.bt_enter_blacknum);
                    Button bt_cancel = (Button) view.findViewById(R.id.bt_cancel_blacknum);

                    //初始化界面
                    et_phone.setText(blacknum.getPhone());
                    if ((blacknum.getMode() & BlackDAO.PHONEMODE) == BlackDAO.PHONEMODE) {
                        cb_phone.setChecked(true);
                    }
                    if ((blacknum.getMode() & BlackDAO.SMSMODE) == BlackDAO.SMSMODE) {
                        cb_sms.setChecked(true);
                    }
                    builder.setView(view);
                    final AlertDialog dialog = builder.create();
                    //设置事件
                    bt_enter.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int mode = 0;
                            BlackBean blackBean = new BlackBean();
                            blackBean.setPhone(et_phone.getText().toString().trim());
                            if (cb_phone.isChecked()) {
                                mode = mode | BlackDAO.PHONEMODE;
                            }
                            if (cb_sms.isChecked()) {
                                mode = mode | BlackDAO.SMSMODE;
                            }
                            //对数据进行检查后再进行修改
                            if (mode == 0) {
                                Toast.makeText(TelSmsSafeActivity.this, "至少选择一种拦截模式!", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if (TextUtils.isEmpty(blackBean.getPhone())) {
                                Toast.makeText(TelSmsSafeActivity.this, "拦截号码不能为空!", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            //对数据进行修改  并通知

                            blackBean.setMode(mode);
                            blacknums.remove(position);
                            blacknums.add(0, blackBean);
                            blackDAO.dupdate(blackBean);
                            adapter.notifyDataSetChanged();
                            dialog.dismiss();
                        }
                    });
                    bt_cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //取消
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
            });

            itemView.ib_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog dialog;
                    AlertDialog.Builder builder = new AlertDialog.Builder(TelSmsSafeActivity.this);
                    builder.setTitle("注意");
                    builder.setMessage("确认是否删除!");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //在数据库中删除此号码信息
                            blackDAO.delete(blacknum.getPhone());
                            //从缓存中删除
                            blacknums.remove(position);

                            adapter.notifyDataSetChanged();
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    dialog = builder.create();
                    dialog.show();
                }
            });

            return convertView;
        }
    }

    /**
     * 添加黑名单按钮点击事件
     *
     * @param view
     */
    public void addblacknum(View view) {

        popupWindow.showAsDropDown(view, 0, view.getHeight() / 5);
    }

}
