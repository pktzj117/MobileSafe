package org.pktzj.mobilesafe.domain;

/**
 * Created by pktzj on 2016/5/20.
 */
public class ContactBean {
    private String name;
    private String number;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return "ContactBean{" +
                "name='" + name + '\'' +
                ", number='" + number + '\'' +
                '}';
    }
}
