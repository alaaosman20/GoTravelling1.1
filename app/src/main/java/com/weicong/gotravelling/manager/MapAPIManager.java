package com.weicong.gotravelling.manager;

import android.content.Context;
import android.preference.PreferenceManager;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.radar.RadarNearbyResult;
import com.baidu.mapapi.radar.RadarNearbySearchOption;
import com.baidu.mapapi.radar.RadarSearchError;
import com.baidu.mapapi.radar.RadarSearchListener;
import com.baidu.mapapi.radar.RadarSearchManager;
import com.baidu.mapapi.radar.RadarUploadInfo;
import com.weicong.gotravelling.model.User;
import com.weicong.gotravelling.util.SPUtil;
import com.weicong.gotravelling.util.TextUtil;

import cn.bmob.v3.BmobUser;

public class MapAPIManager {

    public static final String CITY = "city";
    public static final String ADDRESS = "address";

    private Context mContext;

    private static MapAPIManager sMapAPIManager;

    private LocationClient mLocationClient;

    private RadarSearchManager mManager;

    private RadarSearchListener mListener;

    private double mLongitude;

    private double mLatitude;

    private MapAPIManager(Context context) {
        mContext = context;
        mLocationClient = new LocationClient(mContext.getApplicationContext());
        LocationClientOption option = new LocationClientOption();
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
        mManager = RadarSearchManager.getInstance();
    }

    public static MapAPIManager getInstance(Context context) {
        if (sMapAPIManager == null) {
            sMapAPIManager = new MapAPIManager(context);
        }
        return sMapAPIManager;
    }

    /**
     * 获取当前所在城市
     */
    public void getCity() {
        mLocationClient.registerLocationListener(new BDLocationListener() {
            @Override
            public void onReceiveLocation(BDLocation bdLocation) {
                mLocationClient.stop();

                mLongitude = bdLocation.getLongitude();
                mLatitude = bdLocation.getLatitude();

                if (!TextUtil.isEmpty(bdLocation.getCity())) {
                    SPUtil.put(mContext, CITY, bdLocation.getCity());
                }
                //用户签到时需要当前地址信息
                String address = bdLocation.getProvince() + bdLocation.getCity() +
                        bdLocation.getDistrict();
                SPUtil.put(mContext, ADDRESS, address);
            }
        });
        mLocationClient.start();
    }

    /**
     * 位置信息上传
     */
    public void uploadInfo() {
        boolean setting = PreferenceManager.getDefaultSharedPreferences(mContext)
                .getBoolean("upload_location", true);
        if (UserManager.LOGIN && setting) {
            if (mManager == null) {
                mManager = RadarSearchManager.getInstance();
            }
            User user = BmobUser.getCurrentUser(mContext, User.class);
            mManager.setUserID(user.getObjectId());
            RadarUploadInfo info = new RadarUploadInfo();
            info.comments = user.getUsername();
            LatLng pt = new LatLng(mLatitude, mLongitude);
            info.pt = pt;
            mManager.uploadInfoRequest(info);
        }
    }

    /**
     * 周边位置检索
     *
     * @param listener 回调
     */
    public void radarSearch(final OnMapAPIListener listener) {
        if (mManager == null) {
            mManager = RadarSearchManager.getInstance();
        }
        LatLng pt = new LatLng(mLatitude, mLongitude);
        RadarNearbySearchOption option = new RadarNearbySearchOption()
                .centerPt(pt).radius(2000);
        mListener = new RadarSearchListener() {
            @Override
            public void onGetNearbyInfoList(RadarNearbyResult radarNearbyResult, RadarSearchError radarSearchError) {
                if (radarSearchError == RadarSearchError.RADAR_NO_ERROR) {
                    listener.onSuccess(radarNearbyResult);
                } else {
                    listener.onError(radarSearchError.toString());
                }
                mManager.removeNearbyInfoListener(mListener);
            }

            @Override
            public void onGetUploadState(RadarSearchError radarSearchError) {

            }

            @Override
            public void onGetClearInfoState(RadarSearchError radarSearchError) {

            }
        };
        mManager.addNearbyInfoListener(mListener);
        mManager.nearbyInfoRequest(option);
    }

    /**
     * 释放周边雷达资源
     */
    public void releaseRadar() {
        if (mManager != null) {
            //清除用户信息
            mManager.clearUserInfo();
            //释放资源
            mManager.destroy();
            mManager = null;
        }
    }

    /**
     * 清除
     */
    public static void clear() {
        sMapAPIManager = null;
    }

    public interface OnMapAPIListener {
        void onSuccess(RadarNearbyResult result);
        void onError(String s);
    }
}
