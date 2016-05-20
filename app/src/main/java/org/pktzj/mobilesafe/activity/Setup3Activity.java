package org.pktzj.mobilesafe.activity;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.pktzj.mobilesafe.R;
import org.pktzj.mobilesafe.utils.MyConstants;
import org.pktzj.mobilesafe.utils.SPTool;


/**
 * Created by pktzj on 2016/5/20.
 */
public class Setup3Activity extends BaseSetupActivity {

    private EditText et_safenum;

    @Override
    public void initView() {
        setContentView(R.layout.activity_setup3);
        et_safenum = (EditText) findViewById(R.id.et_safenum_setup3);
    }

    @Override
    protected void initData() {
        super.initData();
        et_safenum.setText((SPTool.getSring(this, MyConstants.SAFENUM, "")));
    }

    public void setsafenum(View view) {

    }

    @Override
    public void next(View view) {
        String safenum = et_safenum.getText().toString();
        SPTool.putSring(this, MyConstants.SAFENUM,safenum);
        if (TextUtils.isEmpty(SPTool.getSring(this, MyConstants.SAFENUM, ""))) {
            Toast.makeText(Setup3Activity.this, "请先设置安全号码!", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d(MyConstants.TAG, "safenum: " + safenum);
        super.next(view);
    }

    @Override
    public void newxActivity() {
        startActivity(Setup4Activity.class);
    }

    @Override
    public void prevActivity() {
        startActivity(Setup2Activity.class);
    }
}
