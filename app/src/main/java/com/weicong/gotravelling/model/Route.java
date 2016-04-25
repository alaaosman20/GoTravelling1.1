package com.weicong.gotravelling.model;

import java.util.List;

import cn.bmob.v3.BmobObject;

public class Route extends BmobObject {

    /**
     * 路线状态：计划中
     */
    public static final int PLANNING = 0x1;

    /**
     * 路线状态：已结束
     */
    public static final int FINISHED = 0x3;

    /**
     * 路线状态：途中
     */
    public static final int TRAVELLING = 0x7;

    /**
     * 路线的创造者
     */
    private User creator;

    /**
     * 路线的名称
     */
    private String name;

    /**
     * 路线描述
     */
    private String description;

    /**
     * 路线状态
     */
    private Integer status;

    /**
     * 路线的图片
     */
    private List<String> images;

    /**
     * 路线天数
     */
    private Integer days;

    /**
     * 点赞数目
     */
    private Integer favor;

    /**
     * 多少人关注
     */
    private Integer followNum;

    /**
     * 多少人评论
     */
    private Integer commentNum;

    /**
     * 多少人看过
     */
    private Integer watchNum;

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }

    public Integer getFavor() {
        return favor;
    }

    public void setFavor(Integer favor) {
        this.favor = favor;
    }

    public Integer getWatchNum() {
        return watchNum;
    }

    public void setWatchNum(Integer watchNum) {
        this.watchNum = watchNum;
    }

    public Integer getFollowNum() {
        return followNum;
    }

    public void setFollowNum(Integer followNum) {
        this.followNum = followNum;
    }

    public Integer getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(Integer commentNum) {
        this.commentNum = commentNum;
    }
}
