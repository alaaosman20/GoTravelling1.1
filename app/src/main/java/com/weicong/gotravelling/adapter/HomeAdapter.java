package com.weicong.gotravelling.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.weicong.gotravelling.MyApplication;
import com.weicong.gotravelling.R;
import com.weicong.gotravelling.activity.SearchRouteDetailActivity;
import com.weicong.gotravelling.model.Route;
import com.weicong.gotravelling.util.ImageLoaderUtil;
import com.weicong.gotravelling.util.TextUtil;

import java.text.DecimalFormat;
import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder> {

    private List<Route> mItems;

    private List<String> mImages;

    private Context mContext;

    public HomeAdapter(Context context, List<Route> list, List<String> images) {
        mContext = context;
        mItems = list;
        mImages = images;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        return new MyViewHolder(inflater.inflate(R.layout.item_recycler, null));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        final Route route = mItems.get(position);

        holder.routeName.setText(route.getName());
        holder.routeDescription.setText(route.getDescription());
        int watchNum = route.getWatchNum();
        String s = String.valueOf(watchNum);
        if (watchNum >= 1000) {
            DecimalFormat format = new DecimalFormat("0.0");
            s = format.format(watchNum * 1.0 / 1000) + "k";
        }
        holder.routeFavor.setText(s);
        if (!TextUtil.isEmpty(mImages.get(position))) {
            ImageLoaderUtil.displayHeadImage(mImages.get(position), holder.head);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.getInstance().setRoute(route);
                Intent intent = new Intent(mContext, SearchRouteDetailActivity.class);
                mContext.startActivity(intent);
            }
        });
    }

    public void delete(int position) {
        mItems.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView head;
        TextView routeName;
        TextView routeFavor;
        TextView routeDescription;

        public MyViewHolder(View view) {
            super(view);

            head = (ImageView) view.findViewById(R.id.iv_creator);
            routeName = (TextView) view.findViewById(R.id.tv_route_name);
            routeFavor = (TextView) view.findViewById(R.id.tv_route_favor);
            routeDescription = (TextView) view.findViewById(R.id.tv_route_description);
        }
    }
}
