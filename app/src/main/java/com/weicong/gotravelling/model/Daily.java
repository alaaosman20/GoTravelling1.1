package com.weicong.gotravelling.model;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobRelation;

public class Daily extends BmobObject {

    /**
     * 标记当前是第几天
     */
    private Integer day;

    /**
     * 日程备注
     */
    private String remark;

    /**
     * 日程所属路线
     */
    private Route route;

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }
}
