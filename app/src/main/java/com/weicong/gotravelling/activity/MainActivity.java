package com.weicong.gotravelling.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.bmob.BmobProFile;
import com.bmob.btp.callback.GetAccessUrlListener;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.weicong.gotravelling.MyApplication;
import com.weicong.gotravelling.R;
import com.weicong.gotravelling.adapter.HomeAdapter;
import com.weicong.gotravelling.manager.MapAPIManager;
import com.weicong.gotravelling.manager.UserManager;
import com.weicong.gotravelling.model.Route;
import com.weicong.gotravelling.util.DividerItemDecoration;
import com.weicong.gotravelling.util.TextUtil;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.FindListener;

public class MainActivity extends DrawerBaseActivity {

    /**
     * 标记是否正在刷新
     */
    private boolean mIsFreshing = false;

    private RecyclerView mRecyclerView;

    private HomeAdapter mHomeAdapter;

    private List<Route> mItems;

    private List<String> mImages;

    public ItemTouchHelper mItemTouchHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //设置Toolbar为ActionBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //初始化Drawer
        initDrawer(toolbar, savedInstanceState);

        initViews();
    }

    /**
     * 初始化View
     */
    private void initViews() {

        FloatingActionButton freshFAB = (FloatingActionButton) findViewById(R.id.fab_fresh);
        freshFAB.setImageDrawable(new IconicsDrawable(this,
                GoogleMaterial.Icon.gmd_autorenew).colorRes(R.color.white));
        freshFAB.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!mIsFreshing) {
                    getHeatRoute();
                }
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        mItemTouchHelper = new ItemTouchHelper(createCallback());
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);

        mItems = (List<Route>) MyApplication.getInstance().removeData("heat_route");
        mImages = (List<String>) MyApplication.getInstance().removeData("images");
        mHomeAdapter = new HomeAdapter(this, mItems, mImages);
        mRecyclerView.setAdapter(mHomeAdapter);
    }

    /**
     * 获取热门路线
     */
    private void getHeatRoute() {
        startRefresh();
        BmobQuery<Route> query = new BmobQuery<>();
        query.addWhereGreaterThan("watchNum", 30);
        query.include("creator");
        query.findObjects(this, new FindListener<Route>() {
            @Override
            public void onSuccess(List<Route> list) {
                mItems = list;
                mImages = new ArrayList<>();
                if (!list.isEmpty()) {
                    getImageUrl(0);
                } else {
                    stopRefresh();
                }

            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    /**
     * 获取头像的可访问地址
     * @param position 位置
     */
    private void getImageUrl(final int position) {
        String imageName = mItems.get(position).getCreator().getImage();
        if (TextUtil.isEmpty(imageName)) {
            mImages.add("");
            isContinue(position);
        } else {
            BmobProFile.getInstance(this).getAccessURL(imageName,
                    new GetAccessUrlListener() {
                        @Override
                        public void onSuccess(BmobFile bmobFile) {
                            mImages.add(bmobFile.getUrl());
                            isContinue(position);
                        }

                        @Override
                        public void onError(int i, String s) {

                        }
                    });
        }

    }

    /**
     * 是否继续获取头像地址
     *
     * @param position 位置
     */
    private void isContinue(int position) {
        if (position + 1 == mItems.size()) {
            stopRefresh();
        } else {
            getImageUrl(position+1);
        }
    }

    /**
     * 开始刷新动画
     */
    private void startRefresh() {
        findViewById(R.id.avloadingIndicatorView).setVisibility(View.VISIBLE);
        mIsFreshing = true;
    }

    /**
     * 停止刷新动画
     */
    private void stopRefresh() {
        mHomeAdapter = new HomeAdapter(this, mItems, mImages);
        mRecyclerView.setAdapter(mHomeAdapter);
        findViewById(R.id.avloadingIndicatorView).setVisibility(View.GONE);
        mIsFreshing = false;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem logoutItem = menu.findItem(R.id.action_logout);
        mNotificationItem = menu.findItem(R.id.action_notification);
        if (UserManager.LOGIN) {
            logoutItem.setVisible(true);
            mNotificationItem.setVisible(true);
        } else {
            logoutItem.setVisible(false);
            mNotificationItem.setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                logout();
                break;
            case R.id.action_search:
                Intent intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
                break;
            case R.id.action_notification:
                intent = new Intent(this, NotificationActivity.class);
                startActivity(intent);
                break;
            case R.id.action_about:
                intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public ItemTouchHelper.Callback createCallback() {
        return new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView,
                                        RecyclerView.ViewHolder viewHolder) {
                return makeMovementFlags(0, ItemTouchHelper.START);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                mHomeAdapter.delete(viewHolder.getAdapterPosition());
            }

            @Override
            public boolean isLongPressDragEnabled() {
                return false;
            }

            @Override
            public boolean isItemViewSwipeEnabled() {
                return true;
            }

        };
    }

    @Override
    protected void onDestroy() {
        MyApplication.getInstance().setRoute(null);
        MyApplication.getInstance().setUser(null);

        //释放周边雷达资源
        MapAPIManager.getInstance(MyApplication.getInstance()).releaseRadar();

        super.onDestroy();
    }
}
