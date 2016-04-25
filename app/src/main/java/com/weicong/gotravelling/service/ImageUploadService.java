package com.weicong.gotravelling.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.bmob.BmobProFile;
import com.bmob.btp.callback.GetAccessUrlListener;
import com.bmob.btp.callback.UploadBatchListener;
import com.weicong.gotravelling.MyApplication;
import com.weicong.gotravelling.event.UploadImageEvent;
import com.weicong.gotravelling.model.Route;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.UpdateListener;
import de.greenrobot.event.EventBus;

public class ImageUploadService extends Service {

    public static final String IMAGE_PATH = "service_image_path";

    private List<String> mUrls;

    private List<String> mFilenames;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            List<String> files = intent.getStringArrayListExtra(IMAGE_PATH);
            upload(files);
        }
        return START_STICKY;
    }

    /**
     * 上传图片
     *
     * @param files 图片路径
     */
    private void upload(List<String> files) {
        int size = files.size();
        BmobProFile.getInstance(this).uploadBatch(files.toArray(new String[size]),
                new UploadBatchListener() {
                    @Override
                    public void onSuccess(boolean isFinish, String[] filenames, String[] urls, BmobFile[] bmobFiles) {
                        /**
                         * 通过bmobFiles来获取Url可能为空？
                         */
                        mUrls = new ArrayList<>();
                        mFilenames = Arrays.asList(filenames);
                        getImageUrl(filenames[0], mUrls, 1);
                    }

                    @Override
                    public void onProgress(int i, int i1, int i2, int i3) {

                    }

                    @Override
                    public void onError(int i, String s) {

                    }
                });
    }

    /**
     * 更新路线
     */
    private void updateRoute() {
        final Route route = MyApplication.getInstance().getRoute();
        route.addAll("images", mFilenames);
        route.update(this, route.getObjectId(), new UpdateListener() {
            @Override
            public void onSuccess() {
                if (route.getImages() == null) {
                    route.setImages(new ArrayList<String>());
                }
                route.getImages().addAll(mFilenames);
                EventBus.getDefault().post(new UploadImageEvent(mFilenames, mUrls));
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
    }

    /**
     * 获取图片的Url
     *
     * @param filename 文件名
     * @param urls url列表
     * @param next 下一个的位置
     */
    private void getImageUrl(String filename, final List<String> urls, final int next) {

        BmobProFile.getInstance(this).getAccessURL(filename, new GetAccessUrlListener() {

            @Override
            public void onError(int errorcode, String errormsg) {

            }

            @Override
            public void onSuccess(BmobFile file) {
                urls.add(file.getUrl());
                if (next == mFilenames.size()) {
                    updateRoute();
                } else {
                    getImageUrl(mFilenames.get(next), urls, next+1);
                }
            }
        });
    }
}
