package org.pktzj.mobilesafe.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.pktzj.mobilesafe.R;
import org.pktzj.mobilesafe.activity.SettingCenter;

/**
 * Created by pktzj on 2016/5/24.
 */
public class SettingCenterItemView extends LinearLayout{


    private String[] contents;
    private View item;
    private TextView tv_title;
    private TextView tv_content;
    private CheckBox cb_check;

    /**
     * 代码实例化调用该构造函数
     * @param context
     */
    public SettingCenterItemView(Context context) {
        super(context);
        initView();
    }

    /**
     * 配置文件中，反射实例化设置属性参数
     * @param context
     * @param attrs
     */
    public SettingCenterItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        initEvent();
        String title = attrs.getAttributeValue("http://schemas.android.com/apk/res-auto", "title");
        String content = attrs.getAttributeValue("http://schemas.android.com/apk/res-auto", "content");

        tv_title.setText(title);
        contents = content.split("-");
    }

    private void initEvent() {
        //item相对布局
        item.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                cb_check.setChecked(!cb_check.isChecked());
            }
        });

        cb_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //设置选中的颜色为绿色
                    tv_content.setText(contents[1]);
                    tv_content.setTextColor(Color.GREEN);
                } else {
                    //设置未选中的颜色为红色
                    tv_content.setText(contents[0]);
                    tv_content.setTextColor(Color.RED);
                }
            }
        });
    }

    private void initView() {
        item = View.inflate(getContext(), R.layout.item_settingcenter_view, null);
        //显示标题
        tv_title = (TextView) item.findViewById(R.id.tv_settingcenter_autoupdate_title);
        //显示的内容
        tv_content = (TextView) item.findViewById(R.id.tv_settingcenter_autoupdate_content);
        //设置复选框
        cb_check = (CheckBox) item.findViewById(R.id.cb_settingcenter_autoupdate_checked);

        addView(item);
    }
}
