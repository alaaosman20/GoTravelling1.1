package com.weicong.gotravelling.model;

import cn.bmob.v3.BmobObject;

public class Sight extends BmobObject {

    public static final String NAME = "sight_name";
    public static final String ADDRESS = "sight_address";
    public static final String LONGITUDE = "sight_longitude";
    public static final String LATITUDE = "sight_latitude";
    public static final String DETAILURL = "sight_detail_url";
    public static final String UID = "sight_uid";

    /**
     * 景点的名称
     */
    private String name;

    /**
     * 景点地址
     */
    private String address;

    /**
     * 经度
     */
    private Double longitude;

    /**
     * 纬度
     */
    private Double latitude;

    /**
     * 是否签到
     */
    private Boolean check;

    /**
     * 签到时间
     */
    private String checkTime;

    /**
     * 景点详情url
     */
    private String detailUrl;

    /**
     * 景点所属的日程
     */
    private Daily daily;

    /**
     * 景点位置
     */
    private Integer index;

    /**
     * 唯一标识
     */
    private String uid;

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

    public Boolean getCheck() {
        return check;
    }

    public void setCheck(Boolean check) {
        this.check = check;
    }

    public String getCheckTime() {
        return checkTime;
    }

    public void setCheckTime(String checkTime) {
        this.checkTime = checkTime;
    }

    public String getDetailUrl() {
        return detailUrl;
    }

    public void setDetailUrl(String detailUrl) {
        this.detailUrl = detailUrl;
    }

    public Daily getDaily() {
        return daily;
    }

    public void setDaily(Daily daily) {
        this.daily = daily;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
