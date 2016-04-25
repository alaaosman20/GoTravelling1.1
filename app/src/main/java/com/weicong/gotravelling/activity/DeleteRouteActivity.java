package com.weicong.gotravelling.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.weicong.gotravelling.R;
import com.weicong.gotravelling.adapter.RouteAdapter;
import com.weicong.gotravelling.manager.RouteManager;
import com.weicong.gotravelling.util.DialogUtil;

import java.util.ArrayList;

import cn.bmob.v3.BmobObject;

public class DeleteRouteActivity extends SwipeBackBaseActivity {

    private ListView mListView;
    private RouteAdapter mAdapter;

    private RouteManager mRouteManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_route);

        mRouteManager = RouteManager.getInstance(this);

        initViews();
    }

    /**
     * 初始化View
     */
    private void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mListView = (ListView) findViewById(R.id.listView);
        mAdapter = new RouteAdapter(this, mRouteManager.getRoutes());
        mListView.setAdapter(mAdapter);
        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_delete, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(0, R.anim.out_from_right);
            return true;
        } else if (item.getItemId() ==R.id.action_delete) {
            deleteRoute();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 删除路线
     */
    private void deleteRoute() {

        ArrayList<BmobObject> deleteRoutes = new ArrayList<>();
        for (int i = 0; i < mAdapter.getCount(); i++) {
            if (mListView.isItemChecked(i)) {
                deleteRoutes.add(mAdapter.getItem(i));
            }
        }
        if (deleteRoutes.size() == 0) {
            Snackbar.make(findViewById(R.id.root), "请先选择要删除的路线（可多选）", Snackbar.LENGTH_SHORT).show();
        } else {
            final ProgressDialog progressDialog = DialogUtil.createProgressDialog(this);
            progressDialog.show();
            RouteManager.getInstance(this).deleteRoutes(deleteRoutes, new RouteManager.OnRouteListener() {
                @Override
                public void onSuccess(String s) {
                    progressDialog.dismiss();
                    mAdapter = new RouteAdapter(DeleteRouteActivity.this,
                            mRouteManager.getRoutes());
                    mListView.setAdapter(mAdapter);
                    finish();
                    overridePendingTransition(0, R.anim.out_from_right);
                }

                @Override
                public void onError(String s) {
                    progressDialog.dismiss();
                    Snackbar.make(findViewById(R.id.root), "删除失败", Snackbar.LENGTH_SHORT).show();
                }
            });
        }
    }
}
