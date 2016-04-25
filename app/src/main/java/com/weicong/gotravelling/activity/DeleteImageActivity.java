package com.weicong.gotravelling.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.GridView;

import com.bmob.BmobProFile;
import com.bmob.btp.callback.DeleteFileListener;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.weicong.gotravelling.MyApplication;
import com.weicong.gotravelling.R;
import com.weicong.gotravelling.adapter.DeleteImageAdapter;
import com.weicong.gotravelling.model.Route;
import com.weicong.gotravelling.util.DialogUtil;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.listener.UpdateListener;

public class DeleteImageActivity extends SwipeBackBaseActivity {

    private GridView mGridView;

    private DeleteImageAdapter mAdapter;

    private FloatingActionButton mDeleteButton;

    private List<DeleteImageAdapter.Photo> mImages = new ArrayList<>();

    private ProgressDialog mProgressDialog;

    private Route mRoute;

    private ArrayList<String> mDeleteUrls = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_image);

        List<String> list = getIntent().getStringArrayListExtra("urls");
        DeleteImageAdapter.Photo photo;
        for (String url : list) {
            photo = new DeleteImageAdapter.Photo();
            photo.path = url;
            mImages.add(photo);
        }

        mRoute = MyApplication.getInstance().getRoute();

        mProgressDialog = DialogUtil.createProgressDialog(this);

        initViews();
    }

    /**
     * 初始化View
     */
    private void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.action_delete);
        setSupportActionBar(toolbar);

        mDeleteButton = (FloatingActionButton) findViewById(R.id.action_delete);
        mDeleteButton.setIconDrawable(new IconicsDrawable(this,
                GoogleMaterial.Icon.gmd_delete).colorRes(R.color.white));
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DeleteImageActivity.this);
                builder.setTitle(R.string.action_delete)
                        .setMessage(R.string.delete_image_message)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                delete();
                            }
                        })
                        .setNegativeButton(R.string.cancel, null);
                builder.create().show();

            }
        });

        mDeleteButton.setEnabled(false);
        
        mGridView = (GridView) findViewById(R.id.gridView);
        mAdapter = new DeleteImageAdapter(this, mImages);
        mAdapter.setOnShowListener(new DeleteImageAdapter.OnShowListener() {
            @Override
            public void onShow(boolean show) {
                mDeleteButton.setEnabled(show);
            }
        });
        mGridView.setAdapter(mAdapter);
    }

    /**
     * 删除图片
     */
    private void delete() {
        mProgressDialog.show();
        List<String> images = mRoute.getImages();
        List<String> deleteFilename = new ArrayList<>();
        DeleteImageAdapter.Photo photo;
        for (int i = 0; i < mImages.size(); i++) {
            photo = mImages.get(i);
            if (photo.select) {
                deleteFilename.add(images.get(i));
                mDeleteUrls.add(photo.path);
            }
        }

        realDeleteImage(deleteFilename.get(0), deleteFilename, 1);

    }

    /**
     * 发起删除图片的请求
     *
     * @param filename 文件名
     * @param images 图片列表
     * @param next 下一个图片坐标
     */
    private void realDeleteImage(String filename, final List<String> images, final int next) {
        BmobProFile.getInstance(this).deleteFile(filename, new DeleteFileListener() {

            @Override
            public void onError(int errorcode, String errormsg) {
                Log.d("weicong", errormsg);
                mProgressDialog.dismiss();
            }

            @Override
            public void onSuccess() {
                if (next == images.size()) {
                    deleteFinish(images);
                } else {
                    realDeleteImage(images.get(next), images, next+1);
                }
            }
        });
    }

    /**
     * 删除图片结束
     */
    private void deleteFinish(List<String> images) {
        mRoute.removeAll("images", images);
        mRoute.update(this, mRoute.getObjectId(), new UpdateListener() {
            @Override
            public void onSuccess() {
                mProgressDialog.dismiss();

                Intent intent = new Intent();
                intent.putStringArrayListExtra("delete", mDeleteUrls);
                setResult(RESULT_OK, intent);
                finish();
                overridePendingTransition(R.anim.still, R.anim.out_from_right);
            }

            @Override
            public void onFailure(int i, String s) {
                mProgressDialog.dismiss();
            }
        });
    }
}
