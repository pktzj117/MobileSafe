package org.pktzj.mobilesafe.domain;

import java.util.List;

/**
 * Created by pktzj on 2016/6/4.
 */

public class SMSBean {
    public String count;
    public List<SMSES> smses;

    public class SMSES {
        public String address;
        public String body;
        public String date;
        public String type;
    }
}
