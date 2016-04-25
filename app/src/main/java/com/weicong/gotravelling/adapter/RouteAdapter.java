package com.weicong.gotravelling.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.weicong.gotravelling.R;
import com.weicong.gotravelling.model.Route;

import java.util.List;

public class RouteAdapter extends BaseAdapter {

    private List<Route> mItems;
    private Context mContext;

    public RouteAdapter(Context context, List<Route> items) {
        mContext = context;
        mItems = items;
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
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_route, null);
            viewHolder = new ViewHolder();
            viewHolder.routeName = (TextView) convertView.findViewById(R.id.tv_route_name);
            viewHolder.numberText = (TextView) convertView.findViewById(R.id.tv_route_number);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.routeName.setText(getItem(position).getName());
        viewHolder.numberText.setText(String.valueOf(position+1));
        return convertView;
    }

    private class ViewHolder {
        TextView routeName;
        TextView numberText;
    }
}
