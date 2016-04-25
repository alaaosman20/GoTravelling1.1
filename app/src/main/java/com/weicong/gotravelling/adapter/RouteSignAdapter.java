package com.weicong.gotravelling.adapter;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.weicong.gotravelling.R;
import com.weicong.gotravelling.model.Daily;
import com.weicong.gotravelling.model.Sight;

import java.util.List;
import java.util.Map;

public class RouteSignAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    private List<Daily> mParents;
    private Map<Integer, List<Sight>> mSights;

    public RouteSignAdapter(Context context, List<Daily> parents, Map<Integer, List<Sight>> sights) {
        mContext = context;
        mParents = parents;
        mSights = sights;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        Daily daily = mParents.get(groupPosition);
        return (mSights.get(daily.getDay()).get(childPosition));
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        SightHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_sight_check, null);
            holder = new SightHolder(convertView);

            convertView.setTag(holder);
        } else {
            holder = (SightHolder) convertView.getTag();
        }
        Sight sight = (Sight) getChild(groupPosition, childPosition);
        holder.name.setText(sight.getName());
        boolean check = false;
        if (sight.getCheck() != null) {
            check = sight.getCheck().booleanValue();
        }
        holder.check.setChecked(check);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        Daily daily = mParents.get(groupPosition);
        return mSights.get(daily.getDay()).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mParents.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return mParents.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        DailyHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_daily_check, null);
            holder = new DailyHolder(convertView);

            convertView.setTag(holder);
        } else {
            holder = (DailyHolder) convertView.getTag();
        }

        Daily daily = (Daily) getGroup(groupPosition);
        String s = "第" + (daily.getDay() + 1) + "天日程";
        holder.name.setText(s);
        holder.remark.setText(daily.getRemark());

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    class DailyHolder {
        TextView name;
        TextView remark;

         public DailyHolder(View view) {
            name = (TextView) view.findViewById(R.id.tv_daily_name);
            remark = (TextView) view.findViewById(R.id.tv_remark);
        }
    }

    class SightHolder {
        TextView name;
        CheckBox check;

        public SightHolder(View view) {
            name = (TextView) view.findViewById(R.id.tv_sight_name);
            check = (CheckBox) view.findViewById(R.id.cb_check);
        }
    }
}
