package com.weicong.gotravelling.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.weicong.gotravelling.R;
import com.weicong.gotravelling.adapter.SearchAdapter;
import com.weicong.gotravelling.model.SearchSight;
import com.weicong.gotravelling.model.Sight;
import com.weicong.gotravelling.model.User;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.FindListener;

public class CollectSightActivity extends SwipeBackBaseActivity {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ListView mListView;
    private List<SearchSight> mItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect_sight);

        initViews();

    }

    /**
     * 初始化View
     */
    private void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.collect_sight);
        setSupportActionBar(toolbar);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.setEnabled(false);

        mListView = (ListView) findViewById(R.id.listView);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SearchSight searchSight = mItems.get(position);
                Intent intent = new Intent();
                intent.putExtra(Sight.NAME, searchSight.getName());
                intent.putExtra(Sight.ADDRESS, searchSight.getAddress());
                intent.putExtra(Sight.LONGITUDE, searchSight.getLongitude());
                intent.putExtra(Sight.LATITUDE, searchSight.getLatitude());
                intent.putExtra(Sight.DETAILURL, searchSight.getDetailUrl());
                intent.putExtra(Sight.UID, searchSight.getUid());
                setResult(RESULT_OK, intent);
                finish();
                overridePendingTransition(R.anim.still, R.anim.out_from_right);
            }
        });

        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });
        getCollectSight();
    }

    /**
     * 从后台获取数据（收藏的景点）
     */
    private void getCollectSight() {
        BmobQuery<SearchSight> query = new BmobQuery<>();
        User user = BmobUser.getCurrentUser(this, User.class);
        query.addWhereRelatedTo("collectSights", new BmobPointer(user));
        query.order("-createdAt");
        query.findObjects(this, new FindListener<SearchSight>() {
            @Override
            public void onSuccess(List<SearchSight> list) {
                mItems = list;
                mListView.setAdapter(new SearchAdapter(CollectSightActivity.this, mItems));
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onError(int i, String s) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }
}
