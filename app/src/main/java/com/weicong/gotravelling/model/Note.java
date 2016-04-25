package com.weicong.gotravelling.model;

import cn.bmob.v3.BmobObject;

public class Note extends BmobObject {

    public static final String CONTENT = "note_content";
    public static final String DATE = "note_date";
    public static final String ADDRESS = "note_address";
    public static final String ID = "note_id";
    /**
     * 内容
     */
    private String content;

    /**
     * 日期
     */
    private String date;

    /**
     * 地址
     */
    private String address;

    /**
     * 游记所属路线
     */
    private Route route;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }
}
