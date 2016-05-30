package org.pktzj.mobilesafe.activity;

import org.pktzj.mobilesafe.domain.ContactBean;
import org.pktzj.mobilesafe.engine.readContactEngine;

import java.util.List;

/**
 * Created by pktzj on 2016/5/20.
 */
public class CallLogActivity extends TelCallSMSBaseActivity {

    @Override
    protected List<ContactBean> getContactBeen() {
        return readContactEngine.readCallLog(CallLogActivity.this);
    }
}
