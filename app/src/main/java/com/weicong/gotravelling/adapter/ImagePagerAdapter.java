package com.weicong.gotravelling.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.weicong.gotravelling.R;
import com.weicong.gotravelling.util.ImageLoaderUtil;

import java.util.ArrayList;
import java.util.List;

public class ImagePagerAdapter extends PagerAdapter {

	/**
	 * 图片视图缓存列表
	 */
	private ArrayList<ImageView> mImageViewCacheList;

	private List<String> mUrls;

	private Context mContext;

	public ImagePagerAdapter(Context context, List<String> urls) {
		mContext = context;
		mUrls = urls;
		mImageViewCacheList = new ArrayList<>();
	}

	@Override
	public int getCount() {
		return Integer.MAX_VALUE;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		ImageView view = (ImageView) object;
		container.removeView(view);
		mImageViewCacheList.add(view);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		String imageUrl = mUrls.get(position % mUrls.size());
		ImageView imageView = null;
		if (mImageViewCacheList.isEmpty()) {
			imageView = new ImageView(mContext);
			imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

		} else {
			imageView = mImageViewCacheList.remove(0);
		}
		container.addView(imageView);
		imageView.setImageResource(R.drawable.photo_loading);
		ImageLoaderUtil.displayImage(imageUrl, imageView, null);
		return imageView;
	}

}
