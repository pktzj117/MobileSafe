package org.pktzj.mobilesafe.activity;

import org.pktzj.mobilesafe.R;

/**
 * Created by pktzj on 2016/5/20.
 */
public class Setup4Activity extends BaseSetupActivity{

    @Override
    public void initView() {
        setContentView(R.layout.activity_setup4);

    }

    @Override
    public void newxActivity() {
        startActivity(LostFoundActivity.class);
    }

    @Override
    public void prevActivity() {
        startActivity(Setup3Activity.class);
    }
}
