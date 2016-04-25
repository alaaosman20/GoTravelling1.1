package com.weicong.gotravelling.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.weicong.gotravelling.R;
import com.weicong.gotravelling.activity.ShowSightMapActivity;
import com.weicong.gotravelling.activity.SightDetailActivity;
import com.weicong.gotravelling.model.Sight;

import java.util.List;

public class SightAdapter extends RecyclerView.Adapter<SightAdapter.MyViewHolder> {

    private List<Sight> mItems;

    private Context mContext;

    public SightAdapter(Context context, List<Sight> list) {
        mContext = context;
        mItems = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        return new MyViewHolder(inflater.inflate(R.layout.item_sight, null));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final Sight sight = mItems.get(position);
        holder.name.setText(sight.getName());
        holder.address.setText(sight.getAddress());
        holder.location.setImageDrawable(new IconicsDrawable(mContext,
                GoogleMaterial.Icon.gmd_location_on).colorRes(R.color.red));
        holder.detail.setImageDrawable(new IconicsDrawable(mContext,
                GoogleMaterial.Icon.gmd_label).colorRes(R.color.amber));

        holder.mapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ShowSightMapActivity.class);
                intent.putExtra(Sight.NAME, sight.getName());
                intent.putExtra(Sight.LONGITUDE, sight.getLongitude());
                intent.putExtra(Sight.LATITUDE, sight.getLatitude());
                mContext.startActivity(intent);
            }
        });

        holder.detailView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SightDetailActivity.class);
                intent.putExtra(Sight.NAME, sight.getName());
                intent.putExtra(Sight.DETAILURL, sight.getDetailUrl());
                intent.putExtra(Sight.UID, sight.getUid());
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

    public void move(int from, int to) {
        Sight prev = mItems.remove(from);
        mItems.add(to > from ? to - 1 : to, prev);
        notifyItemMoved(from, to);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView address;
        ImageView location;
        ImageView detail;

        View mapView;
        View detailView;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.tv_sight_name);
            address = (TextView) view.findViewById(R.id.tv_sight_address);
            location = (ImageView) view.findViewById(R.id.iv_location);
            detail = (ImageView) view.findViewById(R.id.iv_detail);

            mapView = view.findViewById(R.id.ll_location);
            detailView = view.findViewById(R.id.ll_detail);
        }
    }
}
