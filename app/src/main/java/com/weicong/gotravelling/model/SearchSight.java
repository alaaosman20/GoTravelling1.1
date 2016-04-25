package com.weicong.gotravelling.model;

import cn.bmob.v3.BmobObject;

public class SearchSight extends BmobObject {

    public static final String UID = "search_sight_uid";
    public static final String NAME = "search_sight_name";
    public static final String LONGITUDE = "search_sight_longitude";
    public static final String LATITUDE = "search_sight_latitude";
    public static final String ADDRESS = "search_sight_address";
    public static final String DETAILURL = "search_sight_detailurl";

    private String name;

    private String address;

    private String uid;

    private Double longitude;

    private Double latitude;

    private String detailUrl;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getDetailUrl() {
        return detailUrl;
    }

    public void setDetailsUrl(String detailUrl) {
        this.detailUrl = detailUrl;
    }
}
