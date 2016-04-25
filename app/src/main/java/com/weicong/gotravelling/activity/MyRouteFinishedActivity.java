package com.weicong.gotravelling.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.weicong.gotravelling.R;
import com.weicong.gotravelling.adapter.CollectRouteAdapter;
import com.weicong.gotravelling.model.Route;
import com.weicong.gotravelling.model.User;
import com.weicong.gotravelling.view.RefreshCircleProgress;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.FindListener;

public class MyRouteFinishedActivity extends SwipeBackBaseActivity {

    private RefreshCircleProgress mRefreshCircleProgress;

    private RecyclerView mRecyclerView;

    private List<Route> mItems;

    private CollectRouteAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_route);

        initViews();
    }

    /**
     * 初始化View
     */
    private void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("结束的路线");
        setSupportActionBar(toolbar);

        mRefreshCircleProgress = (RefreshCircleProgress) findViewById(R.id.refresh_progress);
        mRefreshCircleProgress.setColorSchemeResources(R.color.colorPrimary);
        mRefreshCircleProgress.setRefreshing(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));

        getData();
    }

    /**
     * 从后台获取数据
     */
    private void getData() {
        User user = BmobUser.getCurrentUser(this, User.class);

        BmobQuery<Route> query = new BmobQuery<>();
        query.addWhereEqualTo("creator", new BmobPointer(user));
        query.addWhereEqualTo("status", Route.FINISHED);
        query.include("creator");
        query.order("-updatedAt");
        query.findObjects(this, new FindListener<Route>() {
            @Override
            public void onSuccess(List<Route> list) {
                mItems = list;
                mAdapter = new CollectRouteAdapter(MyRouteFinishedActivity.this, mItems);
                mRecyclerView.setAdapter(mAdapter);
                mRefreshCircleProgress.setRefreshing(false);
            }

            @Override
            public void onError(int i, String s) {
                mRefreshCircleProgress.setRefreshing(false);
            }
        });
    }
}
