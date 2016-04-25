package com.weicong.gotravelling;

import android.app.Activity;
import android.app.Application;

import com.baidu.mapapi.SDKInitializer;
import com.bmob.BmobConfiguration;
import com.bmob.BmobPro;
import com.weicong.gotravelling.manager.BmobManager;
import com.weicong.gotravelling.manager.LoginManager;
import com.weicong.gotravelling.manager.MapAPIManager;
import com.weicong.gotravelling.manager.RouteManager;
import com.weicong.gotravelling.manager.UserManager;
import com.weicong.gotravelling.model.PushMessage;
import com.weicong.gotravelling.model.Route;
import com.weicong.gotravelling.model.User;
import com.weicong.gotravelling.util.Constant;
import com.weicong.gotravelling.util.ImageLoaderUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.push.BmobPush;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;

public class MyApplication extends Application {

    /**
     * 保存Activity的引用
     */
    private static ArrayList<Activity> sActivitys = new ArrayList<>();

    /**
     * 单例模式
     */
    private static MyApplication sMyApplication;

    /**
     * 暂存数据
     */
    private Route mRoute;

    /**
     * 暂存数据
     */
    private User mUser;

    /**
     * 全局数据
     */
    private Map<String, Object> mData = new HashMap<>();

    /**
     * 推送消息
     */
    private List<PushMessage> mPushMessage = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();

        //获得MyApplication实例
        sMyApplication = this;

        //初始化Bmob SDK
        Bmob.initialize(this, Constant.APPLICATION_ID);

        // 使用推送服务时的初始化操作
        BmobInstallation.getCurrentInstallation(this).save();
        // 启动推送服务
        BmobPush.startWork(this, Constant.APPLICATION_ID);

        //百度地图API初始化
        SDKInitializer.initialize(getApplicationContext());

        //定义缓存目录名称
        BmobConfiguration config = new BmobConfiguration.Builder(this).customExternalCacheDir("GoTravelling").build();
        BmobPro.getInstance(this).initConfig(config);

        //初始化UserManager
        UserManager.getInstance(this);
        //初始化BmobManager
        BmobManager.getInstance(this);

        //获取当前所在城市
        MapAPIManager.getInstance(this).getCity();
        //位置信息上传
        MapAPIManager.getInstance(this).uploadInfo();

        ImageLoaderUtil.initImageLoader(getApplicationContext());
    }

    /**
     * 获取唯一的MyApplication实例
     *
     * @return MyApplication实例
     */
    public static MyApplication getInstance() {
        return sMyApplication;
    }

    /**
     * 添加Activity的引用，在onCreate中调用
     *
     * @param activity Activity
     */
    public static void addActivity(Activity activity) {
        sActivitys.add(activity);
    }

    /**
     * 删除Activity的引用，在onDestroy中调用
     *
     * @param activity Activity
     */
    public static void removeActivity(Activity activity) {
        sActivitys.remove(activity);
    }

    /**
     * 删除Activity的所有引用，当退出应用时调用
     */
    public static void removeAllActivity() {
        for (Activity activity : sActivitys) {

            activity.finish();
        }
        sActivitys.clear();

        clearManagers();
    }

    /**
     * 释放所有管理类的实例
     */
    private static void clearManagers() {
        BmobManager.clear();
        UserManager.clear();
        MapAPIManager.clear();
        LoginManager.clear();
        RouteManager.clear();
    }

    public void setRoute(Route route) {
        mRoute = route;
    }

    public Route getRoute() {
        return mRoute;
    }

    public void setUser(User user) {
        mUser = user;
    }

    public User getUser() {
        return mUser;
    }

    /**
     * 保存全局数据
     *
     * @param key 键
     * @param value 值
     */
    public void putData(String key, Object value) {
        mData.put(key, value);
    }

    /**
     * 获取全局数据
     *
     * @param key 键
     * @return 保存的值
     */
    public Object getData(String key) {
        return mData.get(key);
    }

    /**
     * 移除全局数据
     * @param key 键
     * @return 移除的值
     */
    public Object removeData(String key) {
        return mData.remove(key);
    }

    /**
     * 添加消息推送
     *
     * @param message 消息推送
     */
    public void addPushMessage(PushMessage message) {
        mPushMessage.add(0, message);
    }

    /**
     * 获取所有推送的消息
     *
     * @return List
     */
    public List<PushMessage> getPushMessage() {
        return mPushMessage;
    }
}
