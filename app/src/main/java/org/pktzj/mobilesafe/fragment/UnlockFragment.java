package org.pktzj.mobilesafe.fragment;

import org.pktzj.mobilesafe.domain.APPBean;

import java.util.List;

/**
 * Created by pktzj on 2016/6/11.
 */

public class UnlockFragment extends BaseFragment{
    public UnlockFragment(List<APPBean> allAPK) {
        super(allAPK);
    }

    @Override
    protected void islocked() {
        this.islocked = false;
    }
}
