package com.weicong.gotravelling.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.weicong.gotravelling.MyApplication;
import com.weicong.gotravelling.R;

/**
 * Universal-Image-Loader工具类
 */
public class ImageLoaderUtil {

    /**
     * 加载显示图片时的设置
     */
    private static DisplayImageOptions sOptions = new DisplayImageOptions.Builder()
            .showStubImage(R.drawable.photo_loading)	// 设置图片下载期间显示的图片
            .cacheInMemory(true)						// 设置下载的图片是否缓存在内存中
            .cacheOnDisc(true)							// 设置下载的图片是否缓存在SD卡中
            .build();

    /**
     * 初始化ImageLoader
     *
     * @param context 上下文（在Application里进行初始化）
     *                例如：initImageLoader(getApplicationContext())
     */
    public static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs() // Remove for release app
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }

    /**
     * 显示图片
     *
     * @param url 图片url
     * @param imageView ImageView实例
     */
    public static void displayImage(String url, ImageView imageView,
                                    ImageLoadingListener listener) {
        if (PreferenceManager.getDefaultSharedPreferences(MyApplication.getInstance())
                .getBoolean("no_picture_mode", false)) {
            imageView.setImageResource(R.drawable.photo_loading);
        } else {
            ImageLoader.getInstance().displayImage(url, imageView,
                    sOptions, listener);
        }

    }

    /**
     * 显示头像
     *
     * @param url 图片url
     * @param imageView ImageView实例
     */
    public static void displayHeadImage(String url, ImageView imageView) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.default_avatar)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .build();
        ImageLoader.getInstance().displayImage(url, imageView, options);
    }

    /**
     * 获取Bitmap对象
     *
     * @param url 图片url
     * @return Bitmap对象
     */
    public static Bitmap loadImageSync(String url) {
        return ImageLoader.getInstance().loadImageSync(url, sOptions);
    }
}
