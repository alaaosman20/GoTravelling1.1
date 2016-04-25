package com.weicong.gotravelling.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.weicong.gotravelling.R;
import com.weicong.gotravelling.util.ImageLoaderUtil;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class DeleteImageAdapter extends BaseAdapter {

    private Context mContext;

    private List<Photo> mItems;

    static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

    public DeleteImageAdapter(Context context, List<Photo> list) {
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_delete_image, null);
            viewHolder = new ViewHolder(convertView);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final Photo photo = getItem(position);
        ImageLoaderUtil.displayImage(photo.path, viewHolder.imageView,
                new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
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

        if (photo.select) {
            viewHolder.select.setVisibility(View.VISIBLE);
        } else {
            viewHolder.select.setVisibility(View.GONE);
        }

        final View view = viewHolder.select;
        viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (photo.select) {
                    view.setVisibility(View.GONE);
                    photo.select = false;
                } else {
                    view.setVisibility(View.VISIBLE);
                    photo.select = true;
                }
                if (mListener != null) {
                    if (scanImage() != 0) {
                        mListener.onShow(true);
                    } else {
                        mListener.onShow(false);
                    }
                }
            }
        });

        return convertView;
    }

    /**
     * 扫描那些图片被选中
     *
     * @return 图片选中的数量
     */
    private int scanImage() {
        int count = 0;
        for (Photo photo : mItems) {
            if (photo.select)
                count++;
        }

        return count;
    }

    class ViewHolder {
        ImageView imageView;
        View select;

        public ViewHolder(View view) {
            imageView = (ImageView) view.findViewById(R.id.iv_image);
            select = view.findViewById(R.id.iv_select);
        }
    }

    public static class Photo {
        public String path;
        public boolean select;
    }

    private OnShowListener mListener;

    public void setOnShowListener(OnShowListener listener) {
        mListener = listener;
    }

    public interface OnShowListener {
        void onShow(boolean show);
    }
}
