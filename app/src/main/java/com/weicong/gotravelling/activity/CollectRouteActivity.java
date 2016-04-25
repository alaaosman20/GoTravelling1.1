package com.weicong.gotravelling.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.weicong.gotravelling.R;
import com.weicong.gotravelling.adapter.SearchRouteAdapter;
import com.weicong.gotravelling.manager.RouteManager;
import com.weicong.gotravelling.model.Route;
import com.weicong.gotravelling.model.User;
import com.weicong.gotravelling.util.DialogUtil;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.FindListener;

public class CollectRouteActivity extends SwipeBackBaseActivity {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ListView mListView;
    private List<Route> mItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect_route);

        initViews();
    }

    /**
     * 初始化View
     */
    private void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.collect_route);
        setSupportActionBar(toolbar);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.setEnabled(false);

        mListView = (ListView) findViewById(R.id.listView);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                copyRoute(position);
            }
        });

        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });

        getCollectRoute();
    }

    /**
     * 从后台获取数据（收藏的路线）
     */
    private void getCollectRoute() {
        BmobQuery<Route> query = new BmobQuery<>();
        User user = BmobUser.getCurrentUser(this, User.class);
        query.addWhereRelatedTo("collectRoutes", new BmobPointer(user));
        query.findObjects(this, new FindListener<Route>() {
            @Override
            public void onSuccess(List<Route> list) {
                mItems = list;
                mListView.setAdapter(new SearchRouteAdapter(CollectRouteActivity.this, mItems));
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onError(int i, String s) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    /**
     * 复制路线
     */
    private void copyRoute(final int position) {
        final ProgressDialog progressDialog = DialogUtil.createProgressDialog(this);
        progressDialog.show();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.copy_route_title)
                .setMessage(R.string.copy_route_message)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Route route = mItems.get(position);
                        RouteManager.getInstance(CollectRouteActivity.this).copyRoute(route, new RouteManager.OnRouteListener() {
                            @Override
                            public void onSuccess(String s) {
                                progressDialog.dismiss();
                                finish();
                                overridePendingTransition(0, R.anim.out_from_right);

                            }

                            @Override
                            public void onError(String s) {

                            }
                        });
                    }
                })
                .setNegativeButton(R.string.cancel, null);
        builder.create().show();
    }
}
