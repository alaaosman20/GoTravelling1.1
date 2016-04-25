package com.weicong.gotravelling.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;

import com.bmob.BmobProFile;
import com.bmob.btp.callback.GetAccessUrlListener;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.weicong.gotravelling.MyApplication;
import com.weicong.gotravelling.R;
import com.weicong.gotravelling.adapter.RouteImageAdapter;
import com.weicong.gotravelling.event.UploadImageEvent;
import com.weicong.gotravelling.model.Route;
import com.weicong.gotravelling.view.RefreshCircleProgress;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.datatype.BmobFile;
import de.greenrobot.event.EventBus;

public class UploadActivity extends SwipeBackBaseActivity {

    private FloatingActionButton mUploadButton;

    private GridView mGridView;
    private RouteImageAdapter mAdapter;
    private ArrayList<String> mImages = new ArrayList<>();

    private RefreshCircleProgress mRefreshCircleProgress;

    private Route mRoute;

    private MenuItem mDeleteMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        mRoute = MyApplication.getInstance().getRoute();

        EventBus.getDefault().register(this);

        initViews();
    }

    /**
     * 初始化View
     */
    private void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.photo);
        setSupportActionBar(toolbar);

        mUploadButton = (FloatingActionButton) findViewById(R.id.action_upload);
        mUploadButton.setIconDrawable(new IconicsDrawable(this,
                GoogleMaterial.Icon.gmd_file_upload).colorRes(R.color.white));
        mUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UploadActivity.this, SelectPhotoActivity.class);
                startActivity(intent);
            }
        });

        mGridView = (GridView) findViewById(R.id.gridView);
        mRefreshCircleProgress = (RefreshCircleProgress) findViewById(R.id.refresh_progress);
        mRefreshCircleProgress.setColorSchemeResources(R.color.colorPrimary);

        getImageData();
    }

    /**
     * 获取图片数据
     */
    private void getImageData() {
        List<String> images = mRoute.getImages();
        if (images != null && images.size() > 0) {
            mRefreshCircleProgress.setRefreshing(true);
            getImageUrl(images.get(0), images, 1);
        } else {
            mAdapter = new RouteImageAdapter(this, mImages);
            mGridView.setAdapter(mAdapter);
            mRefreshCircleProgress.setVisibility(View.GONE);
        }

    }

    /**
     * 获取图片的Url
     *
     * @param filename 图片名
     * @param images 图片集合
     * @param next 下一张图片的位置
     */
    private void getImageUrl(String filename, final List<String> images, final int next) {
        BmobProFile.getInstance(this).getAccessURL(filename, new GetAccessUrlListener() {
            @Override
            public void onSuccess(BmobFile bmobFile) {
                mImages.add(bmobFile.getUrl());
                if (next != images.size()) {
                    getImageUrl(images.get(next), images, next + 1);
                } else {
                    loadFinish();
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    /**
     * 加载图片数据结束
     */
    private void loadFinish() {
        mRefreshCircleProgress.setRefreshing(false);
        mAdapter = new RouteImageAdapter(this, mImages);
        mGridView.setAdapter(mAdapter);
        mDeleteMenu.setVisible(true);
    }

    public void onEventMainThread(UploadImageEvent event) {
        mImages.addAll(event.getUrl());
        mAdapter.notifyDataSetChanged();
        mDeleteMenu.setVisible(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_delete, menu);
        mDeleteMenu = menu.findItem(R.id.action_delete);
        mDeleteMenu.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_delete) {
            Intent intent = new Intent(this, DeleteImageActivity.class);
            intent.putStringArrayListExtra("urls", mImages);
            startActivityForResult(intent, 0);
            overridePendingTransition(R.anim.in_from_right, R.anim.still);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            List<String> deleteUrls = data.getStringArrayListExtra("delete");
            mImages.removeAll(deleteUrls);
            mAdapter.notifyDataSetChanged();
            if (mImages.isEmpty()) {
                mDeleteMenu.setVisible(false);
            }
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
