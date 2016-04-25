package com.weicong.gotravelling.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.weicong.gotravelling.R;
import com.weicong.gotravelling.model.RouteComment;
import com.weicong.gotravelling.util.ImageLoaderUtil;

import java.util.List;

public class CommentAdapter extends BaseAdapter {

    private Context mContext;
    private List<RouteComment> mItems;

    public CommentAdapter(Context context, List<RouteComment> list) {
        mContext = context;
        mItems = list;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public RouteComment getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_comment, null);

            viewHolder = new ViewHolder(convertView);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        RouteComment comment = getItem(position);
        viewHolder.number.setText(String.valueOf(position + 1));
        viewHolder.username.setText(comment.getUsername());
        viewHolder.content.setText(comment.getContent());
        viewHolder.date.setText(comment.getTime());
        if (comment.getImage() != null) {
            ImageLoaderUtil.displayHeadImage(comment.getImage(), viewHolder.head);
        }

        return convertView;
    }

    private class ViewHolder {
        TextView number;
        TextView content;
        TextView date;
        TextView username;
        ImageView head;

        public ViewHolder(View view) {
            number = (TextView) view.findViewById(R.id.tv_number);
            username = (TextView) view.findViewById(R.id.tv_username);
            content = (TextView) view.findViewById(R.id.tv_content);
            date = (TextView) view.findViewById(R.id.tv_date);

            head = (ImageView) view.findViewById(R.id.iv_head_image);
        }
    }
}
