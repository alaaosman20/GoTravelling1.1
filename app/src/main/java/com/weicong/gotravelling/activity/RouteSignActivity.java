package com.weicong.gotravelling.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.weicong.gotravelling.MyApplication;
import com.weicong.gotravelling.R;
import com.weicong.gotravelling.adapter.RouteSignAdapter;
import com.weicong.gotravelling.manager.UserManager;
import com.weicong.gotravelling.model.Daily;
import com.weicong.gotravelling.model.Route;
import com.weicong.gotravelling.model.Sight;
import com.weicong.gotravelling.util.DialogUtil;
import com.weicong.gotravelling.view.RefreshCircleProgress;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class RouteSignActivity extends SwipeBackBaseActivity {

    private RefreshCircleProgress mRefreshCircleProgress;

    private ExpandableListView mExpandableListView;
    private RouteSignAdapter mAdapter;

    private List<Daily> mParents;
    private Map<Integer, List<Sight>> mSights = new HashMap<>();

    private int mDay = 0;

    private MenuItem mLocationMenu;

    private Route mRoute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_sign);

        initViews();
    }

    /**
     * 初始化View
     */
    private void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRefreshCircleProgress = (RefreshCircleProgress) findViewById(R.id.refresh_progress);
        mRefreshCircleProgress.setColorSchemeResources(R.color.colorPrimary);
        mRefreshCircleProgress.setRefreshing(true);

        mExpandableListView = (ExpandableListView) findViewById(R.id.expand_list);
        mExpandableListView.setGroupIndicator(null);
        mExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Sight sight = mSights.get(groupPosition).get(childPosition);
                sureSign(sight);
                return true;
            }
        });

        FloatingActionButton noteButton = (FloatingActionButton) findViewById(R.id.action_note);
        noteButton.setIconDrawable(new IconicsDrawable(this,
                GoogleMaterial.Icon.gmd_edit).colorRes(R.color.white).paddingDp(3));
        noteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RouteSignActivity.this, NoteActivity.class);
                MyApplication.getInstance().setRoute(mRoute);
                startActivity(intent);
            }
        });

        FloatingActionButton uploadButton = (FloatingActionButton) findViewById(R.id.action_upload);
        uploadButton.setIconDrawable(new IconicsDrawable(this,
                GoogleMaterial.Icon.gmd_photo).colorRes(R.color.white).paddingDp(3));
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RouteSignActivity.this, UploadActivity.class);
                MyApplication.getInstance().setRoute(mRoute);
                startActivity(intent);
            }
        });

        FloatingActionButton overButton = (FloatingActionButton) findViewById(R.id.action_over);
        overButton.setIconDrawable(new IconicsDrawable(this,
                GoogleMaterial.Icon.gmd_pause_circle_outline).colorRes(R.color.white));
        overButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RouteSignActivity.this);
                builder.setTitle(R.string.route_over_title)
                        .setMessage(R.string.route_over_message)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                overRoute();
                            }
                        })
                        .setNegativeButton(R.string.cancel, null);
                builder.create().show();
            }
        });

        getTravellingRoute();
    }

    /**
     * 获取正在进行路线的信息
     */
    private void getTravellingRoute() {
        BmobQuery<Route> query = new BmobQuery<>();
        query.order("-updatedAt");
        query.addWhereEqualTo("creator", UserManager.getInstance(this).getUser());
        query.findObjects(this, new FindListener<Route>() {
            @Override
            public void onSuccess(List<Route> list) {
                int i = 0;

                for (; i < list.size(); i++) {
                    if (list.get(i).getStatus() == null) {
                        continue;
                    } else if (Route.TRAVELLING == list.get(i).getStatus().intValue()) {
                        break;
                    }
                }
                if (i == list.size()) {
                    noRouteStart();
                } else {
                    getRouteData(list.get(i));
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    /**
     * 还没开始任何路线
     */
    private void noRouteStart() {
        mRefreshCircleProgress.setRefreshing(false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.route_finish_notice)
                .setMessage(R.string.no_route_start_message)
                .setCancelable(false)
                .setPositiveButton(R.string.know, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        overridePendingTransition(0, R.anim.out_from_right);
                    }
                });
        builder.create().show();
    }

    /**
     * 获取路线的详细信息
     *
     * @param route 路线
     */
    private void getRouteData(Route route) {
        mRoute = route;
        setTitle(route.getName());
        BmobQuery<Daily> query = new BmobQuery<>();
        query.addWhereEqualTo("route", new BmobPointer(route));
        query.order("day");
        query.findObjects(this, new FindListener<Daily>() {
            @Override
            public void onSuccess(List<Daily> list) {
                mParents = list;
                for (Daily daily : list) {
                    getDailySight(daily);
                }
            }

            @Override
            public void onError(int i, String s) {
                Snackbar.make(findViewById(R.id.root), s, Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 获取日程里的景点
     *
     * @param daily 日程
     */
    private void getDailySight(final Daily daily) {
        BmobQuery<Sight> query = new BmobQuery<>();
        query.order("index");
        query.addWhereEqualTo("daily", new BmobPointer(daily));
        query.findObjects(this, new FindListener<Sight>() {
            @Override
            public void onSuccess(List<Sight> list) {
                mDay++;
                mSights.put(daily.getDay(), list);
                if (mDay == mParents.size()) {
                    mRefreshCircleProgress.setRefreshing(false);
                    initAdapter();
                }
            }

            @Override
            public void onError(int i, String s) {
                Snackbar.make(findViewById(R.id.root), s, Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 初始化适配器
     */
    private void initAdapter() {
        mAdapter = new RouteSignAdapter(this, mParents, mSights);
        mExpandableListView.setAdapter(mAdapter);

        int count = 0;
        for (int i = 0; i < mSights.size(); i++) {
            count += mSights.get(i).size();
        }
        if (count >= 2) {
            mLocationMenu.setVisible(true);
        }
        findViewById(R.id.fab_menu).setVisibility(View.VISIBLE);
    }

    /**
     * 确定是否签到
     *
     * @param sight 景点
     */
    private void sureSign(final Sight sight) {
        if (sight.getCheck() != null && sight.getCheck()) {
            return;
        }
        String message = "确定在" + sight.getName() + "景点签到？";
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.drawer_item_sign)
                .setMessage(message)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        check(sight);
                    }
                })
                .setNegativeButton(R.string.no, null);
        builder.create().show();
    }

    private void check(final Sight sight) {
        final ProgressDialog progressDialog = DialogUtil.createProgressDialog(this);
        progressDialog.show();
        sight.setCheck(true);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String time = format.format(new Date());
        sight.setCheckTime(time);
        sight.update(this, sight.getObjectId(), new UpdateListener() {
            @Override
            public void onSuccess() {
                UserManager.getInstance(RouteSignActivity.this).sightCheck(sight,
                        new UserManager.OnUpdateListener() {
                            @Override
                            public void onSuccess() {
                                progressDialog.dismiss();
                                mAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onError() {
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
            ProgressDialog progressDialog = DialogUtil.createProgressDialog(this);
            progressDialog.show();

            ArrayList<String> list = new ArrayList<>();

            for (int i = 0; i < mParents.size(); i++) {
                for (Sight sight : mSights.get(i)) {
                    list.add(sight.getLongitude().toString());
                    list.add(sight.getLatitude().toString());
                }
            }
            progressDialog.dismiss();
            Intent intent = new Intent(this, ShowDailyMapActivity.class);
            intent.putStringArrayListExtra("data", list);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 结束路线
     */
    private void overRoute() {
        final ProgressDialog progressDialog = DialogUtil.createProgressDialog(this);
        progressDialog.show();
        mRoute.setStatus(Route.FINISHED);
        mRoute.update(this, mRoute.getObjectId(), new UpdateListener() {
            @Override
            public void onSuccess() {
                UserManager.getInstance(RouteSignActivity.this).overRoute(
                        new UserManager.OnUpdateListener() {
                            @Override
                            public void onSuccess() {
                                progressDialog.dismiss();
                                finish();
                                overridePendingTransition(0, R.anim.out_from_right);
                            }

                            @Override
                            public void onError() {
                                progressDialog.dismiss();
                            }
                        }
                );
            }

            @Override
            public void onFailure(int i, String s) {
                progressDialog.dismiss();
            }
        });
    }

    @Override
    protected void onDestroy() {
        MyApplication.getInstance().setRoute(null);
        super.onDestroy();
    }
}
