package com.weicong.gotravelling.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.weicong.gotravelling.R;
import com.weicong.gotravelling.util.DrivingRouteOverlay;

import java.util.ArrayList;
import java.util.List;

public class ShowDailyMapActivity extends SwipeBackBaseActivity {

    private MapView mMapView;
    private BaiduMap mBaidumap;

    private List<Double> mDatas = new ArrayList<>();

    private RoutePlanSearch mSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_daily_map);

        List<String> data = getIntent().getStringArrayListExtra("data");
        for (int i = 0; i < data.size(); i++) {
            mDatas.add(Double.valueOf(data.get(i)));
        }

        initRoutePlanSearch();
        initViews();

    }

    /**
     * 初始化view
     */
    private void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mMapView = (MapView) findViewById(R.id.mapView);
        mBaidumap = mMapView.getMap();

        PlanNode startNode = PlanNode.withLocation(new LatLng(mDatas.get(1), mDatas.get(0)));
        PlanNode endNode = PlanNode.withLocation(new LatLng(mDatas.get(mDatas.size()-1), mDatas.get(mDatas.size()-2)));
        List<PlanNode> lists = new ArrayList<>();
        PlanNode planNode;
        for (int i = 2; i < mDatas.size()-2; i=i+2) {
            planNode = PlanNode.withLocation(new LatLng(mDatas.get(i+1), mDatas.get(i)));
            lists.add(planNode);
        }
        mSearch.drivingSearch(new DrivingRoutePlanOption()
                .from(startNode)
                .passBy(lists)
                .to(endNode));
    }

    /**
     * 初始化路线规划操作
     */
    private void initRoutePlanSearch() {
        mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(new OnGetRoutePlanResultListener() {
            @Override
            public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {

            }

            @Override
            public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {

            }

            @Override
            public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {
                DrivingRouteOverlay overlay = new DrivingRouteOverlay(mBaidumap);
                mBaidumap.setOnMarkerClickListener(overlay);
                overlay.setData(drivingRouteResult.getRouteLines().get(0));
                overlay.addToMap();
                overlay.zoomToSpan();
            }
        });
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
        mSearch.destroy();
    }
}
