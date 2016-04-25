package com.weicong.gotravelling.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.weicong.gotravelling.R;
import com.weicong.gotravelling.model.SearchSight;

import java.util.List;

public class SearchAdapter extends BaseAdapter {

    private List<SearchSight> mList;
    private Context mContext;

    public SearchAdapter(Context context, List<SearchSight> data) {
        mList = data;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public SearchSight getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_search_item, null);
            viewHolder.nameText = (TextView) convertView.findViewById(R.id.tv_name);
            viewHolder.addressText = (TextView) convertView.findViewById(R.id.tv_address);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        SearchSight sight = getItem(position);
        viewHolder.nameText.setText(sight.getName());
        viewHolder.addressText.setText(sight.getAddress());

        return convertView;
    }

    static class ViewHolder {
        TextView nameText;
        TextView addressText;
    }
}
