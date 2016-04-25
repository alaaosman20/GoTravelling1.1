package com.weicong.gotravelling.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.weicong.gotravelling.R;
import com.weicong.gotravelling.model.Route;

import java.util.List;

public class SearchRouteAdapter extends BaseAdapter {

    private Context mContext;
    private List<Route> mItems;

    public SearchRouteAdapter(Context context, List<Route> list) {
        mContext = context;
        mItems = list;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Route getItem(int position) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_search_route, null);

            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Route route = getItem(position);
        viewHolder.name.setText(route.getName());
        viewHolder.description.setText(route.getDescription());

        return convertView;
    }

    class ViewHolder {
        TextView name;
        TextView description;

        public ViewHolder(View view) {
            name = (TextView) view.findViewById(R.id.tv_route_name);
            description = (TextView) view.findViewById(R.id.tv_route_description);
        }
    }
}
