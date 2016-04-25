package com.weicong.gotravelling.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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
import com.weicong.gotravelling.R;
import com.weicong.gotravelling.model.Sight;

public class ShowSightMapActivity extends SwipeBackBaseActivity {

    private MapView mMapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sight_show_map);

        initViews();
    }

    /**
     * 初始化View
     */
    private void initViews() {
        String name = getIntent().getStringExtra(Sight.NAME);
        double longitude = getIntent().getDoubleExtra(Sight.LONGITUDE, 0);
        double latitude = getIntent().getDoubleExtra(Sight.LATITUDE, 0);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(name);
        setSupportActionBar(toolbar);

        findViewById(R.id.fab_collect).setVisibility(View.GONE);
        mMapView = (MapView)findViewById(R.id.mapView);
        BaiduMap baiduMap = mMapView.getMap();

        //定义Maker坐标点
        LatLng point = new LatLng(latitude, longitude);
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
