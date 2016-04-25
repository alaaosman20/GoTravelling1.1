package com.weicong.gotravelling.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.weicong.gotravelling.R;
import com.weicong.gotravelling.fragment.SettingsFragment;

public class SettingsActivity extends SwipeBackBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initViews();
    }

    /**
     * 初始化View
     */
    private void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.settings);
        setSupportActionBar(toolbar);
        
        getFragmentManager().beginTransaction()
                .add(R.id.container, new SettingsFragment()).commit();
    }
}
