package com.weicong.gotravelling.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.weicong.gotravelling.R;
import com.weicong.gotravelling.model.PushMessage;

import java.util.List;

public class PushMessageAdapter extends BaseAdapter {

    private Context mContext;

    private List<PushMessage> mItems;

    public PushMessageAdapter(Context context, List<PushMessage> list) {
        mContext = context;
        mItems = list;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public PushMessage getItem(int position) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_message, null);
            viewHolder = new ViewHolder(convertView);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        PushMessage message = getItem(position);
        viewHolder.message.setText(message.getContent());
        viewHolder.date.setText(message.getDate());

        return convertView;
    }

    static class ViewHolder {
        TextView message;
        TextView date;

        ViewHolder(View view) {
            message = (TextView) view.findViewById(R.id.tv_message);
            date = (TextView) view.findViewById(R.id.tv_date);
        }
    }
}
