package com.weicong.gotravelling.manager;

import android.content.Context;

import com.bmob.BmobProFile;
import com.bmob.btp.callback.DeleteFileListener;
import com.bmob.btp.callback.DownloadListener;
import com.bmob.btp.callback.UploadListener;
import com.weicong.gotravelling.model.SearchSight;
import com.weicong.gotravelling.model.User;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * Bmob后台管理类，单例模式
 */
public class BmobManager {

    public interface IBmobResponse {
        void onSuccess(String data);
        void onError(String error);
    }

    private static BmobManager sBmobManager;

    private Context mContext;

    private BmobManager(Context context) {
        mContext = context;
    }

    public static BmobManager getInstance(Context context) {
        if (sBmobManager == null) {
            sBmobManager = new BmobManager(context);
        }

        return sBmobManager;
    }

    /**
     * 下载图片
     *
     * @param filename 文件名
     * @param response 回调接口
     */
    public void download(String filename, final IBmobResponse response) {
        BmobProFile.getInstance(mContext).download(filename, new DownloadListener() {

            @Override
            public void onSuccess(String fullPath) {
                response.onSuccess(fullPath);
            }

            @Override
            public void onProgress(String localPath, int percent) {

            }

            @Override
            public void onError(int statuscode, String errormsg) {
                response.onError(errormsg);
            }
        });
    }

    /**
     * 文件上传
     *
     * @param localPath 本地路径
     * @param response 回调接口
     */
    public void upload(String localPath, final IBmobResponse response) {
        BmobProFile.getInstance(mContext).upload(localPath, new UploadListener() {

            @Override
            public void onSuccess(String fileName, String url, BmobFile file) {
                response.onSuccess(fileName);
            }

            @Override
            public void onProgress(int progress) {

            }

            @Override
            public void onError(int statuscode, String errormsg) {
                response.onError(errormsg);
            }
        });
    }

    /**
     * 删除文件
     *
     * @param filename 文件名
     */
    public void delete(String filename) {
        BmobProFile.getInstance(mContext).deleteFile(filename, new DeleteFileListener() {

            @Override
            public void onError(int errorcode, String errormsg) {

            }

            @Override
            public void onSuccess() {

            }
        });
    }

    /**
     * 清除
     */
    public static void clear() {
        sBmobManager = null;
    }

    /**
     * 收藏景点
     *
     * @param searchSight 景点收藏
     * @param response 回调函数
     */
    public void addCollectSight(final SearchSight searchSight, final IBmobResponse response) {
        BmobQuery<SearchSight> query = new BmobQuery<>();
        User user = BmobUser.getCurrentUser(mContext, User.class);
        query.addWhereRelatedTo("collectSights", new BmobPointer(user));
        query.findObjects(mContext, new FindListener<SearchSight>() {
            @Override
            public void onSuccess(List<SearchSight> list) {
                int i = 0;
                for (; i < list.size(); i++) {
                    if (list.get(i).getUid().equals(searchSight.getUid())) {
                        break;
                    }
                }
                if (i == list.size()) { //该用户没有收藏该景点
                    BmobQuery<SearchSight> query = new BmobQuery<>();
                    query.addWhereEqualTo("uid", searchSight.getUid());
                    query.findObjects(mContext, new FindListener<SearchSight>() {
                        @Override
                        public void onSuccess(List<SearchSight> list) {
                            if (list.size() == 0) {
                                saveCollectSight(searchSight, response);
                            } else {

                                addCollectSightImp(list.get(0), response);
                            }
                        }

                        @Override
                        public void onError(int i, String s) {

                        }
                    });

                } else {
                    //已经收藏了该景点
                    response.onError(null);
                }

            }

            @Override
            public void onError(int i, String s) {
                response.onError(s);
            }
        });
    }

    /**
     * 保存景点
     *
     * @param searchSight 景点
     * @param response 回调函数
     */
    private void saveCollectSight(final SearchSight searchSight, final IBmobResponse response) {
        searchSight.save(mContext, new SaveListener() {
            @Override
            public void onSuccess() {
                addCollectSightImp(searchSight, response);
            }

            @Override
            public void onFailure(int i, String s) {

                response.onError(s);
            }
        });
    }

    /**
     * 添加到收藏景点列表中
     *
     * @param searchSight 景点
     * @param response 回调
     */
    private void addCollectSightImp(SearchSight searchSight, final IBmobResponse response) {
        UserManager.getInstance(mContext).addCollectSight(searchSight,
                new UserManager.OnUpdateListener() {
                    @Override
                    public void onSuccess() {
                        response.onSuccess(null);
                    }

                    @Override
                    public void onError() {
                        response.onError(null);
                    }
                });
    }

}
