package com.weicong.gotravelling.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.weicong.gotravelling.R;
import com.weicong.gotravelling.adapter.FragmentAdapter;
import com.weicong.gotravelling.fragment.UserDetailsFragment;
import com.weicong.gotravelling.fragment.UserHomeFragment;
import com.weicong.gotravelling.manager.UserManager;

import java.util.ArrayList;
import java.util.List;

public class UserActivity extends SwipeBackBaseActivity {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private List<Fragment> mFragments = new ArrayList<>();
    private List<String> mTitles = new ArrayList<>();
    private UserManager mUserManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        mUserManager = UserManager.getInstance(this);

        initViews();
    }

    private void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(mUserManager.getUsername());
        setSupportActionBar(toolbar);

        mTitles.add("个人主页");
        mTitles.add("详情信息");

        mTabLayout = (TabLayout) findViewById(R.id.tabLayout);
        mTabLayout.addTab(mTabLayout.newTab().setText(mTitles.get(0)));
        mTabLayout.addTab(mTabLayout.newTab().setText(mTitles.get(1)));

        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mFragments.add(new UserHomeFragment());
        mFragments.add(new UserDetailsFragment());

        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager(),
                mFragments, mTitles);
        mViewPager.setAdapter(adapter);

        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabsFromPagerAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_edit) {
            Intent intent = new Intent(this, EditInfoActivity.class);
            startActivity(intent);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
