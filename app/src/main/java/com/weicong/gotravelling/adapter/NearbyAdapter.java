package com.weicong.gotravelling.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.weicong.gotravelling.R;
import com.weicong.gotravelling.model.User;

import java.util.List;

public class NearbyAdapter extends BaseAdapter {

    private Context mContext;

    private List<User> mItems;

    public NearbyAdapter(Context context, List<User> list) {
        mContext = context;
        mItems = list;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public User getItem(int position) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_nearby, null);
            viewHolder = new ViewHolder(convertView);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.number.setText(String.valueOf(position+1));
        viewHolder.name.setText(mItems.get(position).getUsername());

        return convertView;
    }

    static class ViewHolder {
        TextView name;
        TextView number;

        ViewHolder(View view) {
            name = (TextView) view.findViewById(R.id.tv_name);
            number = (TextView) view.findViewById(R.id.tv_number);
        }
    }
}
