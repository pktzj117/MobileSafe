package org.pktzj.mobilesafe.domain;

/**
 * Created by pktzj on 2016/5/27.
 */
public class BlackBean {
    private int _id;
    private String phone;
    private int mode;

    public BlackBean() {
    }

    public BlackBean(String phone, int mode) {
        this.phone = phone;
        this.mode = mode;
    }

    public BlackBean(int _id, String phone, int mode) {
        this(phone, mode);
        this._id = _id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    @Override
    public String toString() {
        return "BlackBean{" +
                "_id=" + _id +
                ", phone='" + phone + '\'' +
                ", mode=" + mode +
                '}';
    }
}
