package org.pktzj.mobilesafe.activity;

import org.pktzj.mobilesafe.R;

/**
 * Created by pktzj on 2016/5/20.
 */
public class Setup1Activity extends BaseSetupActivity{

    @Override
    public void initView() {
        setContentView(R.layout.activity_setup1);

    }

    @Override
    public void newxActivity() {
        startActivity(Setup2Activity.class);
    }

    @Override
    public void prevActivity() {
    }
}
