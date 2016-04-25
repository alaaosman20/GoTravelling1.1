package com.weicong.gotravelling.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import com.weicong.gotravelling.R;
import com.weicong.gotravelling.adapter.FragmentAdapter;
import com.weicong.gotravelling.fragment.RouteCollectionFragment;
import com.weicong.gotravelling.fragment.SightCollectionFragment;

import java.util.ArrayList;
import java.util.List;

public class CollectionActivity extends SwipeBackBaseActivity {

    private List<String> mTitles = new ArrayList<>();
    private List<Fragment> mFragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);

        initViews();
    }

    /**
     * 初始化View
     */
    private void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.collection_file);
        setSupportActionBar(toolbar);

        mTitles.add("景点");
        mTitles.add("路线");

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText(mTitles.get(0)));
        tabLayout.addTab(tabLayout.newTab().setText(mTitles.get(1)));

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        mFragments.add(new SightCollectionFragment());
        mFragments.add(new RouteCollectionFragment());

        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager(),
                mFragments, mTitles);
        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabsFromPagerAdapter(adapter);
    }
}
