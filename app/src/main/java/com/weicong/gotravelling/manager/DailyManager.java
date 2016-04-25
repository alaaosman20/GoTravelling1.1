package com.weicong.gotravelling.manager;

import android.content.Context;

import com.weicong.gotravelling.model.Daily;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 日程管理类，单例模式
 */
public class DailyManager {

    public static final String POSITION = "daily_manager_position";

    private Context mContext;

    private List<Daily> mDailys = new ArrayList<>();

    private static DailyManager sDailyManager = null;

    private DailyManager(Context context) {
        mContext = context;
    }

    public static DailyManager getInstance(Context context) {
        if (sDailyManager == null) {
            sDailyManager = new DailyManager(context);
        }
        return sDailyManager;
    }

    public void setDailys(List<Daily> dailys) {
        mDailys.clear();
        for (int i = 0; i < dailys.size(); i++) {
            mDailys.add(dailys.get(i));
        }
    }

    /**
     * 添加日程
     *
     * @param daily 日程
     */
    public void addDaily(final Daily daily, final OnDailyListener listener) {
        daily.save(mContext, new SaveListener() {
            @Override
            public void onSuccess() {
                mDailys.add(daily);
                listener.onSuccess();
            }

            @Override
            public void onFailure(int i, String s) {
                listener.onError();
            }
        });
    }

    /**
     * 删除日程
     *
     * @param position 日程位置
     * @param listener 回调
     */
    public void deleteDaily(final int position, final OnDailyListener listener) {
        Daily daily = mDailys.get(position);
        daily.delete(mContext, new DeleteListener() {
            @Override
            public void onSuccess() {
                mDailys.remove(position);
                List<BmobObject> objects = new ArrayList<>();
                Daily daily;
                for (int i = position; i < mDailys.size(); i++) {
                    daily = mDailys.get(i);
                    daily.setDay(i);
                    objects.add(daily);
                }
                new BmobObject().updateBatch(mContext, objects, new UpdateListener() {
                    @Override
                    public void onSuccess() {
                        listener.onSuccess();
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        listener.onError();
                    }
                });
            }

            @Override
            public void onFailure(int i, String s) {
                listener.onError();
            }
        });
    }

    /**
     * 交换日程数据
     *
     * @param i 日程下标i
     * @param j 日程下标j
     * @param listener 回调
     */
    public void changeDaily(final int i, final int j, final OnDailyListener listener) {
        int day = mDailys.get(i).getDay();
        mDailys.get(i).setDay(mDailys.get(j).getDay());
        mDailys.get(j).setDay(day);
        List<BmobObject> objects = new ArrayList<>();
        objects.add(mDailys.get(i));
        objects.add(mDailys.get(j));
        new BmobObject().updateBatch(mContext, objects, new UpdateListener() {
            @Override
            public void onSuccess() {
                Daily temp = mDailys.get(i);
                mDailys.set(i, mDailys.get(j));
                mDailys.set(j, temp);
                listener.onSuccess();
            }

            @Override
            public void onFailure(int i, String s) {
                listener.onError();
            }
        });

    }

    public List<Daily> getDailys() {
        List<Daily> dailys = new ArrayList<>();
        for (Daily daily : mDailys) {
            dailys.add(daily);
        }
        return dailys;
    }

    public Daily getDaily(int position) {
        return mDailys.get(position);
    }

    /**
     * 清除
     */
    public static void clear() {
        sDailyManager = null;
    }

    public interface OnDailyListener {
        void onSuccess();
        void onError();
    }
}
