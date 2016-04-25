package com.weicong.gotravelling.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.weicong.gotravelling.MyApplication;
import com.weicong.gotravelling.R;
import com.weicong.gotravelling.adapter.PushMessageAdapter;
import com.weicong.gotravelling.model.PushMessage;

import java.util.List;

/**
 * 通知功能还没实现
 */
public class NotificationActivity extends SwipeBackBaseActivity {

    private PushMessageAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        initViews();
    }

    /**
     * 初始化View
     */
    private void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.notification);
        setSupportActionBar(toolbar);

        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setEnabled(false);
        List<PushMessage> pushMessages = MyApplication.getInstance().getPushMessage();
        mAdapter = new PushMessageAdapter(this, pushMessages);
        if (pushMessages.size() == 0) {
            findViewById(R.id.iv_empty).setVisibility(View.VISIBLE);
            findViewById(R.id.tv_empty).setVisibility(View.VISIBLE);
        } else {
            listView.setAdapter(mAdapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem homeItem = menu.add(0, 1, 0, R.string.action_clear);
        homeItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 1) {
            MyApplication.getInstance().getPushMessage().clear();
            mAdapter.notifyDataSetChanged();
            findViewById(R.id.iv_empty).setVisibility(View.VISIBLE);
            findViewById(R.id.tv_empty).setVisibility(View.VISIBLE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
