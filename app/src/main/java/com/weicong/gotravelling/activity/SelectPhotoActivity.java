package com.weicong.gotravelling.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.Toast;

import com.weicong.gotravelling.R;
import com.weicong.gotravelling.adapter.ImageAdapter;
import com.weicong.gotravelling.service.ImageUploadService;
import com.weicong.gotravelling.view.RefreshCircleProgress;

import java.util.ArrayList;

public class SelectPhotoActivity extends SwipeBackBaseActivity {

    private GridView mGridView;

    private ArrayList<ImageAdapter.Photo> mImages = new ArrayList<>();

    private ImageAdapter mAdapter;

    private RefreshCircleProgress mRefreshProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_photo);

        initViews();
    }

    /**
     * 初始化View
     */
    private void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.select_photo);
        setSupportActionBar(toolbar);

        mGridView = (GridView) findViewById(R.id.gridView);
        mRefreshProgress = (RefreshCircleProgress) findViewById(R.id.refresh_progress);
        mRefreshProgress.setColorSchemeResources(R.color.colorPrimary);
        mRefreshProgress.setRefreshing(true);

        getImages();

    }

    /**
     * 获取本地图片
     */
    private void getImages() {
        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
        ImageAdapter.Photo photo;
        while (cursor.moveToNext()) {
            photo = new ImageAdapter.Photo();
            byte[] data = cursor.getBlob(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            photo.path = new String(data, 0, data.length - 1);
            mImages.add(photo);
        }
        cursor.close();

        mAdapter = new ImageAdapter(this, mImages);
        mGridView.setAdapter(mAdapter);

        mRefreshProgress.setRefreshing(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_upload, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_upload) {
            scanPhoto();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 扫描哪些图片需要上传
     */
    private void scanPhoto() {
        ArrayList<String> files = new ArrayList<>();

        for (ImageAdapter.Photo photo : mImages) {
            if (photo.select) {
                files.add(photo.path);
            }
        }

        if (files.size() == 0) {
            Snackbar.make(findViewById(R.id.root), R.string.select_photo_empty, Snackbar.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(this, ImageUploadService.class);
            intent.putStringArrayListExtra(ImageUploadService.IMAGE_PATH, files);
            startService(intent);
            Toast.makeText(this, R.string.uploading_image, Toast.LENGTH_SHORT).show();
            finish();
            overridePendingTransition(R.anim.still, R.anim.out_from_right);
        }
    }

}
