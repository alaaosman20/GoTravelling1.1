package com.weicong.gotravelling.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.weicong.gotravelling.R;
import com.weicong.gotravelling.adapter.SightAdapter;
import com.weicong.gotravelling.manager.DailyManager;
import com.weicong.gotravelling.manager.RouteManager;
import com.weicong.gotravelling.model.Daily;
import com.weicong.gotravelling.model.Sight;
import com.weicong.gotravelling.util.DialogUtil;
import com.weicong.gotravelling.view.RefreshCircleProgress;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class DailyDetailActivity extends SwipeBackBaseActivity {

    public static final int REQUEST_COLLECT = 100;
    public static final int REQUEST_SEARCH = 101;

    private int mPosition;
    private Daily mDaily;

    private RefreshCircleProgress mRefreshCircleProgress;

    private RecyclerView mRecyclerView;
    private SightAdapter mAdapter;
    private List<Sight> mItems = new ArrayList<>();

    private boolean mIsPlanning;

    private MenuItem mLocationMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_detail);

        mPosition = getIntent().getIntExtra(DailyManager.POSITION, 0);
        mIsPlanning = getIntent().getBooleanExtra(RouteManager.STATUS, true);
        mDaily = DailyManager.getInstance(this).getDaily(mPosition);

        initViews();
    }

    /**
     * 初始化View
     */
    private void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        StringBuilder sb = new StringBuilder();
        sb.append("第").append(mPosition + 1).append("天日程");
        toolbar.setTitle(sb.toString());
        setSupportActionBar(toolbar);

        TextView remarkText = (TextView) findViewById(R.id.tv_remark);
        remarkText.setText(mDaily.getRemark());

        mRefreshCircleProgress = (RefreshCircleProgress) findViewById(R.id.refresh_progress);
        mRefreshCircleProgress.setColorSchemeResources(R.color.colorPrimary);
        mRefreshCircleProgress.setRefreshing(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(createCallback());
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        if (!mIsPlanning) {
            findViewById(R.id.fab_menu).setVisibility(View.GONE);
        }
        FloatingActionButton collectButton = (FloatingActionButton) findViewById(R.id.action_collect);
        collectButton.setIconDrawable(new IconicsDrawable(this,
                GoogleMaterial.Icon.gmd_star).colorRes(R.color.white).paddingDp(1));
        collectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DailyDetailActivity.this, CollectSightActivity.class);
                startActivityForResult(intent, REQUEST_COLLECT);
                overridePendingTransition(R.anim.in_from_right, R.anim.still);
            }
        });

        FloatingActionButton searchButton = (FloatingActionButton) findViewById(R.id.action_search);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DailyDetailActivity.this, SearchSightActivity.class);
                startActivityForResult(intent, REQUEST_SEARCH);
                overridePendingTransition(R.anim.in_from_right, R.anim.still);
            }
        });

        getSight();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            String name = data.getStringExtra(Sight.NAME);
            String address = data.getStringExtra(Sight.ADDRESS);
            Double longitude = data.getDoubleExtra(Sight.LONGITUDE, 0);
            Double latitude = data.getDoubleExtra(Sight.LATITUDE, 0);
            String detailUrl = data.getStringExtra(Sight.DETAILURL);
            String uid = data.getStringExtra(Sight.UID);

            Sight sight = new Sight();
            sight.setDaily(mDaily);
            sight.setName(name);
            sight.setAddress(address);
            sight.setLongitude(longitude);
            sight.setLatitude(latitude);
            sight.setDetailUrl(detailUrl);
            sight.setIndex(mItems.size());
            sight.setUid(uid);
            mItems.add(sight);
            if (mItems.size() >= 2) {
                mLocationMenu.setVisible(true);
            } else {
                mLocationMenu.setVisible(false);
            }
            mAdapter.notifyDataSetChanged();
            addSight(sight);

        }
    }

    /**
     * 增加景点（提交到后台）
     *
     * @param sight 景点
     */
    private void addSight(Sight sight) {
        sight.save(this, new SaveListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
    }

    /**
     * 从后台获取景点数据
     */
    private void getSight() {
        BmobQuery<Sight> query = new BmobQuery<>();
        query.addWhereEqualTo("daily", new BmobPointer(mDaily));
        query.order("index");
        query.findObjects(this, new FindListener<Sight>() {
            @Override
            public void onSuccess(List<Sight> list) {
                mItems = list;
                if (mItems.size() >= 2) {
                    mLocationMenu.setVisible(true);
                } else {
                    mLocationMenu.setVisible(false);
                }
                mAdapter = new SightAdapter(DailyDetailActivity.this, mItems);
                mRecyclerView.setAdapter(mAdapter);
                mRefreshCircleProgress.setRefreshing(false);
            }

            @Override
            public void onError(int i, String s) {
                mAdapter = new SightAdapter(DailyDetailActivity.this, mItems);
                mRecyclerView.setAdapter(mAdapter);
                mRefreshCircleProgress.setRefreshing(false);
            }
        });
    }

    public ItemTouchHelper.Callback createCallback() {
        return new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView,
                                        RecyclerView.ViewHolder viewHolder) {
                return makeMovementFlags(ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                        ItemTouchHelper.START | ItemTouchHelper.END);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                changeSight(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                showDeleteSightDialog(viewHolder.getAdapterPosition());
            }

            @Override
            public boolean isLongPressDragEnabled() {
                return mIsPlanning;
            }

            @Override
            public boolean isItemViewSwipeEnabled() {
                return mIsPlanning;
            }

        };
    }

    /**
     * 显示删除景点对话框
     *
     * @param position 景点位置
     */
    private void showDeleteSightDialog(final int position) {
        String message = "您确定要删除" + mItems.get(position).getName() + "景点吗？";
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.delete_sight)
                .setMessage(message)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteSight(position);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAdapter.notifyDataSetChanged();
                    }
                })
                .setCancelable(false);
        builder.create().show();
    }

    /**
     * 删除景点
     *
     * @param position 景点位置
     */
    private void deleteSight(final int position) {
        final ProgressDialog progressDialog = DialogUtil.createProgressDialog(this);
        progressDialog.show();
        Sight sight = mItems.get(position);
        sight.delete(this, new DeleteListener() {
            @Override
            public void onSuccess() {
                List<BmobObject> objects = new ArrayList<>();
                Sight sight;
                for (int i = position + 1; i < mItems.size(); i++) {
                    sight = mItems.get(i);
                    sight.setIndex(i - 1);
                    objects.add(sight);
                }
                new BmobObject().updateBatch(DailyDetailActivity.this, objects,
                        new UpdateListener() {
                            @Override
                            public void onSuccess() {
                                mAdapter.delete(position);
                                if (mItems.size() >= 2) {
                                    mLocationMenu.setVisible(true);
                                } else {
                                    mLocationMenu.setVisible(false);
                                }
                                progressDialog.dismiss();
                            }

                            @Override
                            public void onFailure(int i, String s) {
                                progressDialog.dismiss();
                            }
                        });
            }

            @Override
            public void onFailure(int i, String s) {
                progressDialog.dismiss();
            }
        });
    }

    /**
     * 修改位置
     * @param i 位置i
     * @param j 位置j
     */
    private void changeSight(int i, int j) {
        Sight sight1 = mItems.get(i);
        Sight sight2 = mItems.get(j);
        Integer index = sight1.getIndex();
        sight1.setIndex(sight2.getIndex());
        sight2.setIndex(index);
        mAdapter.move(i, j);
        List<BmobObject> objects = new ArrayList<>();
        objects.add(sight1);
        objects.add(sight2);
        new BmobObject().updateBatch(this, objects, new UpdateListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_sight_details, menu);
        mLocationMenu = menu.findItem(R.id.action_location);
        mLocationMenu.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_location) {
            ArrayList<String> datas = new ArrayList<>();
            Sight sight;
            for (int i = 0; i < mItems.size(); i++) {
                sight = mItems.get(i);
                datas.add(sight.getLongitude().toString());
                datas.add(sight.getLatitude().toString());
            }
            Intent intent = new Intent(this, ShowDailyMapActivity.class);
            intent.putStringArrayListExtra("data", datas);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
