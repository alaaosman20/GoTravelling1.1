package com.weicong.gotravelling.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.weicong.gotravelling.MyApplication;
import com.weicong.gotravelling.R;
import com.weicong.gotravelling.activity.SearchRouteDetailActivity;
import com.weicong.gotravelling.model.Route;

import java.util.List;

public class CollectRouteAdapter extends RecyclerView.Adapter<CollectRouteAdapter.MyViewHolder> {

    private List<Route> mItems;

    private Context mContext;

    public CollectRouteAdapter(Context context, List<Route> list) {
        mContext = context;
        mItems = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        return new MyViewHolder(inflater.inflate(R.layout.item_collect_route, null));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        Route route = mItems.get(position);
        holder.name.setText(route.getName());
        holder.description.setText(route.getDescription());
        holder.clickView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.getInstance().setRoute(mItems.get(position));
                Intent intent = new Intent(mContext, SearchRouteDetailActivity.class);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void delete(int position) {
        mItems.remove(position);
        notifyItemRemoved(position);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView description;
        View clickView;

        public MyViewHolder(View view) {
            super(view);

            name = (TextView) view.findViewById(R.id.tv_route_name);
            description = (TextView) view.findViewById(R.id.tv_route_description);
            clickView = view.findViewById(R.id.ll_click);
        }
    }
}
