package com.weicong.gotravelling.model;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * 用户类
 */
public class User extends BmobUser {

    /**
     * 一句话介绍

     */
    private String intro;

    /**
     * 个人介绍
     */
    private String description;

    /**
     * 性别
     */
    private Boolean gender;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 头像（路径）
     */
    private String image;

    /**
     * 所在城市
     */
    private String city;

    /**
     * 关注的人
     */
    private BmobRelation followUsers;

    /**
     * 关注的路线
     */
    private BmobRelation followRoutes;

    /**
     * 收藏的路线
     */
    private BmobRelation collectRoutes;

    /**
     * 收藏的景点
     */
    private BmobRelation collectSights;

    /**
     * 评论过的路线
     */
    private BmobRelation commentRoutes;

    /**
     * 签到过的景点
     */
    private BmobRelation signSights;

    /**
     * 我的路线的数目
     */
    private Integer routeNum;

    /**
     * 我关注的人的数目
     */
    private Integer followNum;

    /**
     * 评论过的路线的数目
     */
    private Integer commentRoutesNum;

    /**
     * 签到的景点的数目
     */
    private Integer signSightsNum;

    /**
     * 标记是否开始了路线
     */
    private Boolean startRoute;

    /**
     * 已经结束的路线的数目
     */
    private Integer finishedRouteNum;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getGender() {
        return gender;
    }

    public void setGender(Boolean gender) {
        this.gender = gender;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public BmobRelation getFollowUsers() {
        return followUsers;
    }

    public void setFollowUsers(BmobRelation followUsers) {
        this.followUsers = followUsers;
    }

    public BmobRelation getFollowRoutes() {
        return followRoutes;
    }

    public void setFollowRoutes(BmobRelation followRoutes) {
        this.followRoutes = followRoutes;
    }

    public BmobRelation getCollectRoutes() {
        return collectRoutes;
    }

    public void setCollectRoutes(BmobRelation collectRoutes) {
        this.collectRoutes = collectRoutes;
    }

    public BmobRelation getCollectSights() {
        return collectSights;
    }

    public void setCollectSights(BmobRelation collectSights) {
        this.collectSights = collectSights;
    }

    public BmobRelation getCommentRoutes() {
        return commentRoutes;
    }

    public void setCommentRoutes(BmobRelation commentRoutes) {
        this.commentRoutes = commentRoutes;
    }

    public BmobRelation getSignSights() {
        return signSights;
    }

    public void setSignSights(BmobRelation signSights) {
        this.signSights = signSights;
    }

    public Integer getRouteNum() {
        return routeNum;
    }

    public void setRouteNum(Integer routeNum) {
        this.routeNum = routeNum;
    }

    public Integer getFollowNum() {
        return followNum;
    }

    public void setFollowNum(Integer followNum) {
        this.followNum = followNum;
    }

    public Integer getCommentRoutesNum() {
        return commentRoutesNum;
    }

    public void setCommentRoutesNum(Integer commentRoutesNum) {
        this.commentRoutesNum = commentRoutesNum;
    }

    public Integer getSignSightsNum() {
        return signSightsNum;
    }

    public void setSignSightsNum(Integer signSightsNum) {
        this.signSightsNum = signSightsNum;
    }

    public Boolean getStartRoute() {
        return startRoute;
    }

    public void setStartRoute(Boolean startRoute) {
        this.startRoute = startRoute;
    }

    public Integer getFinishedRouteNum() {
        return finishedRouteNum;
    }

    public void setFinishedRouteNum(Integer finishedRouteNum) {
        this.finishedRouteNum = finishedRouteNum;
    }
}
