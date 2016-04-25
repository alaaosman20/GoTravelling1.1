package com.weicong.gotravelling.model;

import cn.bmob.v3.BmobObject;

public class RouteComment extends BmobObject {

    public static final String USERNAME = "route_comment_username";
    public static final String IMAGE = "route_comment_image";
    public static final String CONTENT = "route_comment_content";
    public static final String TIME = "route_comment_time";

    /**
     * 该评论的用户名
     */
    private String username;

    /**
     * 用户头像的路径
     */
    private String image;

    /**
     * 评论的内容
     */
    private String content;

    /**
     * 评论时间
     */
    private String time;

    /**
     * 评论所属的路线
     */
    private Route route;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }
}
