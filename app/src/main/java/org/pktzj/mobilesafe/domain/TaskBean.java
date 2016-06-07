package org.pktzj.mobilesafe.domain;

import android.graphics.drawable.Drawable;

/**
 * Created by pktzj on 2016/6/4.
 */

public class TaskBean {
    private boolean isUSEAPP;//是否存在SD卡中
    private boolean isROMAPP;//是否存在ROM中
    private Drawable icon;//应用图标
    private String appName;//应用名称
    private String packName;//包名
    private long size; //占用的控件
    private boolean isChecked;

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public boolean isUSEAPP() {
        return isUSEAPP;
    }

    public void setUSEAPP(boolean USEAPP) {
        isUSEAPP = USEAPP;
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

    @Override
    public String toString() {
        return "TaskBean{" +
                "isUSEAPP=" + isUSEAPP +
                ", isROMAPP=" + isROMAPP +
                ", icon=" + icon +
                ", appName='" + appName + '\'' +
                ", packName='" + packName + '\'' +
                ", size=" + size +
                ", isChecked=" + isChecked +
                '}';
    }
}
