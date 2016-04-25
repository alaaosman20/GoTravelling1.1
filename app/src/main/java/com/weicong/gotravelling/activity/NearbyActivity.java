package com.weicong.gotravelling.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;

import com.baidu.mapapi.radar.RadarNearbyInfo;
import com.baidu.mapapi.radar.RadarNearbyResult;
import com.weicong.gotravelling.R;
import com.weicong.gotravelling.adapter.NearbyAdapter;
import com.weicong.gotravelling.manager.MapAPIManager;
import com.weicong.gotravelling.model.User;
import com.weicong.gotravelling.view.RefreshCircleProgress;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.GetListener;

public class NearbyActivity extends SwipeBackBaseActivity {

    private RefreshCircleProgress mRefreshCircleProgress;

    private ListView mListView;

    private List<User> mUsers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby);

        initViews();
    }

    /**
     * 初始化View
     */
    private void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.action_nearby);
        setSupportActionBar(toolbar);

        mRefreshCircleProgress = (RefreshCircleProgress) findViewById(R.id.refresh_progress);
        mRefreshCircleProgress.setColorSchemeResources(R.color.colorPrimary);
        mRefreshCircleProgress.setRefreshing(true);

        mListView = (ListView) findViewById(R.id.listView);

        MapAPIManager.getInstance(this).radarSearch(new MapAPIManager.OnMapAPIListener() {
            @Override
            public void onSuccess(RadarNearbyResult result) {
                if (result.infoList.isEmpty()) {
                    mRefreshCircleProgress.setRefreshing(false);
                } else {
                    queryUser(result.infoList.get(0).userID, result.infoList, 1);
                }
            }

            @Override
            public void onError(String s) {
                mRefreshCircleProgress.setRefreshing(false);
            }
        });
    }

    /**
     * 根据objectId获取用户
     *
     * @param objectId 用户id
     * @param list 数据列表
     * @param next 下一个位置
     */
    private void queryUser(String objectId, final List<RadarNearbyInfo> list, final int next) {
        BmobQuery<User> query = new BmobQuery<>();
        query.getObject(this, objectId, new GetListener<User>() {
            @Override
            public void onSuccess(User user) {
                mUsers.add(user);
                if (next == list.size()) {
                    loadFinish();
                } else {
                    queryUser(list.get(next).userID, list, next+1);
                }
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
    }

    /**
     * 获取数据结束
     */
    private void loadFinish() {
        NearbyAdapter adapter = new NearbyAdapter(this, mUsers);
        mListView.setAdapter(adapter);
        mRefreshCircleProgress.setRefreshing(false);
    }
}
