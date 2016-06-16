package org.pktzj.mobilesafe.domain;

import android.graphics.drawable.Drawable;

/**
 * Created by pktzj on 2016/6/4.
 */

public class APPBean {
    private String uid;
    private String path;
    private boolean isSDAPP;//是否存在SD卡中
    private boolean isROMAPP;//是否存在ROM中
    private Drawable icon;//应用图标
    private String appName;//应用名称
    private String packName;//包名
    private long size; //占用的控件
    private String rcv;   //接收的流量
    private String snd;   //发送网络流量

    public boolean isSDAPP() {
        return isSDAPP;
    }

    public void setSDAPP(boolean SDAPP) {
        isSDAPP = SDAPP;
    }

    public boolean isROMAPP() {
        return isROMAPP;
    }

    public void setROMAPP(boolean ROMAPP) {
        isROMAPP = ROMAPP;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackName() {
        return packName;
    }

    public void setPackName(String packName) {
        this.packName = packName;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getRcv() {
        return rcv;
    }

    public void setRcv(String rcv) {
        this.rcv = rcv;
    }

    public String getSnd() {
        return snd;
    }

    public void setSnd(String snd) {
        this.snd = snd;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "APPBean{" +
                "isSDAPP=" + isSDAPP +
                ", isROMAPP=" + isROMAPP +
                ", icon=" + icon +
                ", appName='" + appName + '\'' +
                ", packName='" + packName + '\'' +
                ", size=" + size +
                '}';
    }
}
