package org.pktzj.mobilesafe;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

import org.pktzj.mobilesafe.domain.ContactBean;
import org.pktzj.mobilesafe.engine.readContactEngine;
import org.pktzj.mobilesafe.utils.MyConstants;

import java.util.List;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {

        super(Application.class);


    }

    public void test(){
        List<ContactBean> contacts = readContactEngine.readContarts(getContext());
        for (ContactBean contact : contacts) {
            Log.d(MyConstants.TAG, contact.toString());
        }
    }

}