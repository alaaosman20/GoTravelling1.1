package com.weicong.gotravelling.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.weicong.gotravelling.R;
import com.weicong.gotravelling.manager.BmobManager;
import com.weicong.gotravelling.manager.UserManager;
import com.weicong.gotravelling.model.SearchSight;
import com.weicong.gotravelling.util.DialogUtil;

public class SightShowMapActivity extends SwipeBackBaseActivity {

    private MapView mMapView;

    private String mName;
    private String mDetailUrl;
    private String mAddress;
    private Double mLongitude;
    private Double mLatitude;
    private String mUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sight_show_map);

        mName = getIntent().getStringExtra(SearchSight.NAME);
        mLongitude = getIntent().getDoubleExtra(SearchSight.LONGITUDE, 0.0);
        mLatitude = getIntent().getDoubleExtra(SearchSight.LATITUDE, 0.0);
        mAddress = getIntent().getStringExtra(SearchSight.ADDRESS);
        mUid = getIntent().getStringExtra(SearchSight.UID);
        mDetailUrl = getIntent().getStringExtra(SearchSight.DETAILURL);

        initViews();
    }

    /**
     * 初始化View
     */
    private void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(mName);
        setSupportActionBar(toolbar);

        mMapView = (MapView)findViewById(R.id.mapView);
        BaiduMap baiduMap = mMapView.getMap();

        //定义Maker坐标点
        LatLng point = new LatLng(mLatitude, mLongitude);
        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.icon_show_location);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(point)
                .icon(bitmap);
        //在地图上添加Marker，并显示
        baiduMap.addOverlay(option);

        MapStatusUpdate update = MapStatusUpdateFactory.newLatLngZoom(point, 13);
        baiduMap.setMapStatus(update);

        FloatingActionButton collectFAB = (FloatingActionButton) findViewById(R.id.fab_collect);
        collectFAB.setImageDrawable(new IconicsDrawable(this,
                GoogleMaterial.Icon.gmd_star).colorRes(R.color.white));
        collectFAB.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = DialogUtil.createProgressDialog(SightShowMapActivity.this);
                progressDialog.show();
                SearchSight searchSight = new SearchSight();
                searchSight.setName(mName);
                searchSight.setUid(mUid);
                searchSight.setLongitude(mLongitude);
                searchSight.setLatitude(mLatitude);
                searchSight.setDetailsUrl(mDetailUrl);
                searchSight.setAddress(mAddress);

                BmobManager.getInstance(SightShowMapActivity.this).addCollectSight(
                        searchSight, new BmobManager.IBmobResponse() {
                            @Override
                            public void onSuccess(String data) {
                                progressDialog.dismiss();
                                View view = findViewById(R.id.root);
                                Snackbar.make(view, "收藏成功", Snackbar.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(String error) {
                                progressDialog.dismiss();
                                View view = findViewById(R.id.root);
                                Snackbar.make(view, "该景点已经收藏", Snackbar.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        if (!UserManager.LOGIN) {
            collectFAB.setVisibility(View.GONE);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem homeItem = menu.add(0, 1, 0, R.string.action_home);
        homeItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 1) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            overridePendingTransition(R.anim.still, R.anim.out_from_right);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }
}
