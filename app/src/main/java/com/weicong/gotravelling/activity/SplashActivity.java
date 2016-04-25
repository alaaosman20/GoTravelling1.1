package com.weicong.gotravelling.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.bmob.BmobProFile;
import com.bmob.btp.callback.GetAccessUrlListener;
import com.weicong.gotravelling.MyApplication;
import com.weicong.gotravelling.R;
import com.weicong.gotravelling.model.Route;
import com.weicong.gotravelling.util.TextUtil;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.FindListener;

public class SplashActivity extends AppCompatActivity {

    private List<Route> mItems;

    private List<String> mImages;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getHeatRoute();
    }

    /**
     * 获取热门路线
     */
    private void getHeatRoute() {
        /*
        这里每次打开都要去访问数据库，严重影响速度，在应用退出时永久存储，下次开启时读取
         */
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
                    toMainActivity();
                }

            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    /**
     * 跳转到MainActivity
     */
    private void toMainActivity() {
        MyApplication.getInstance().putData("heat_route", mItems);
        MyApplication.getInstance().putData("images", mImages);

        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.scale_and_fade_in_middle, R.anim.still);
        finish();
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
            toMainActivity();
        } else {
            getImageUrl(position+1);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.scale_and_fade_from_middle);
    }
}
