package com.weicong.gotravelling.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.kale.activityoptions.ActivityCompatICS;
import com.kale.activityoptions.ActivityOptionsCompatICS;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.ImageDownloader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.weicong.gotravelling.R;
import com.weicong.gotravelling.activity.ShowPhotoActivity;
import com.weicong.gotravelling.util.ImageLoaderUtil;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ImageAdapter extends BaseAdapter {

    private Context mContext;

    private List<Photo> mItems;

    static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

    public ImageAdapter(Context context, List<Photo> list) {
        mContext = context;
        mItems = list;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Photo getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_select_photo, null);
            viewHolder = new ViewHolder(convertView);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String url = ImageDownloader.Scheme.FILE.wrap(getItem(position).path);
        viewHolder.select.setChecked(getItem(position).select);

        viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ShowPhotoActivity.class);
                intent.putExtra(ShowPhotoActivity.PATH, getItem(position).path);
                ActivityOptionsCompatICS options = ActivityOptionsCompatICS.makeScaleUpAnimation(v,
                        0, 0, //拉伸开始的坐标
                        v.getMeasuredWidth(), v.getMeasuredHeight());//初始的宽高
                ActivityCompatICS.startActivity((Activity) mContext, intent, options.toBundle());
            }
        });

        viewHolder.select.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mItems.get(position).select = isChecked;
            }
        });

        final CheckBox select = viewHolder.select;
        ImageLoaderUtil.displayImage(url, viewHolder.imageView,
                new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        select.setVisibility(View.VISIBLE);
                        if (loadedImage != null) {
                            ImageView imageView = (ImageView) view;
                            // 是否第一次显示
                            boolean firstDisplay = !displayedImages.contains(imageUri);
                            if (firstDisplay) {
                                // 图片淡入效果
                                FadeInBitmapDisplayer.animate(imageView, 500);
                                displayedImages.add(imageUri);
                            }
                        }
                    }
                });

        return convertView;
    }

    class ViewHolder {
        ImageView imageView;
        CheckBox select;

        public ViewHolder(View view) {
            imageView = (ImageView) view.findViewById(R.id.iv_image);
            select = (CheckBox) view.findViewById(R.id.cb_select);
        }
    }

    public static class Photo {
        public String path;
        public boolean select;
    }
}
