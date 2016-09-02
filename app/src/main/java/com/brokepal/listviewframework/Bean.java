package com.brokepal.listviewframework;

/**
 * Created by Administrator on 2016/8/31.
 */
public class Bean {
    private String title;
    private String describe;
    private String time;
    private String phone;
    public Bean(){

    }

    public Bean(String title, String describe, String time, String phone) {
        this.title = title;
        this.describe = describe;
        this.time = time;
        this.phone = phone;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "Bean{" +
                "title='" + title + '\'' +
                ", describe='" + describe + '\'' +
                ", time='" + time + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
