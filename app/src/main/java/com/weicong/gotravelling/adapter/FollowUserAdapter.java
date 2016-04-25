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
import com.weicong.gotravelling.activity.CreatorActivity;
import com.weicong.gotravelling.model.User;

import java.util.List;

public class FollowUserAdapter extends RecyclerView.Adapter<FollowUserAdapter.MyViewHolder> {

    private List<User> mItems;

    private Context mContext;

    public FollowUserAdapter(Context context, List<User> list) {
        mContext = context;
        mItems = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        return new MyViewHolder(inflater.inflate(R.layout.item_follow_user, null));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        User user = mItems.get(position);
        holder.username.setText(user.getUsername());
        holder.intro.setText(user.getIntro());
        holder.clickView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.getInstance().setUser(mItems.get(position));
                Intent intent = new Intent(mContext, CreatorActivity.class);
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

        TextView username;
        TextView intro;
        View clickView;

        public MyViewHolder(View view) {
            super(view);

            username = (TextView) view.findViewById(R.id.tv_username);
            intro = (TextView) view.findViewById(R.id.tv_intro);
            clickView = view.findViewById(R.id.ll_click);
        }
    }
}
