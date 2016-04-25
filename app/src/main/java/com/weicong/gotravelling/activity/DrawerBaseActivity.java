package com.weicong.gotravelling.activity;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bmob.BmobPro;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.weicong.gotravelling.MyApplication;
import com.weicong.gotravelling.R;
import com.weicong.gotravelling.event.HeadImageEvent;
import com.weicong.gotravelling.event.IntroEvent;
import com.weicong.gotravelling.manager.LoginManager;
import com.weicong.gotravelling.manager.UserManager;
import com.weicong.gotravelling.service.ImageUploadService;

import de.greenrobot.event.EventBus;

public class DrawerBaseActivity extends BaseActivity {

    protected Drawer mDrawer;

    private Toolbar mToolbar;
    private Bundle mSavedInstanceState;

    private PrimaryDrawerItem mSettingsItem;
    private PrimaryDrawerItem mExitItem;

    private PrimaryDrawerItem mLoginOrRegisterItem;
    private PrimaryDrawerItem mQQLoginItem;
    private PrimaryDrawerItem mWeiboLoginItem;
    private PrimaryDrawerItem mWeixinLoginItem;

    private PrimaryDrawerItem mAccountItem;
    private PrimaryDrawerItem mFollowItem;
    private PrimaryDrawerItem mFavoriteItem;
    private PrimaryDrawerItem nRouteItem;
    private PrimaryDrawerItem mSignItem;

    /**
     * 标记是否已经初始化抽屉菜单
     */
    private boolean mIsInitial = false;

    /**
     * 通知菜单按钮
     */
    protected MenuItem mNotificationItem;

    /**
     * 头像
     */
    private ImageView mHeadImageView;

    /**
     * 一句话介绍
     */
    private TextView mIntroText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    public void onEventMainThread(HeadImageEvent event) {
        UserManager.getInstance(this).setHeadImage(mHeadImageView);
    }

    public void onEventMainThread(IntroEvent event) {
        mIntroText.setText(UserManager.getInstance(this).getIntro());
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    /**
     * 初始化Drawer，在子Activity中调用
     *
     * @param toolbar            ToolBar
     * @param savedInstanceState savedInstanceState
     */
    protected void initDrawer(Toolbar toolbar, Bundle savedInstanceState) {
        mToolbar = toolbar;
        mSavedInstanceState = savedInstanceState;

        if (!mIsInitial) {
            mIsInitial = true;
            initDrawerMenuItem();
        }

        if (UserManager.LOGIN) {
            hasLoginedMenu(toolbar, savedInstanceState);
        } else {
            notLoginMenu(toolbar, savedInstanceState);
        }
    }

    /**
     * 初始化抽屉菜单
     */
    private void initDrawerMenuItem() {
        mSettingsItem = new PrimaryDrawerItem()
                .withName(R.string.drawer_item_settings)
                .withIcon(new IconicsDrawable(this,
                        GoogleMaterial.Icon.gmd_settings)
                        .paddingDp(2)
                        .colorRes(R.color.blue));

        mExitItem = new PrimaryDrawerItem()
                .withName(R.string.drawer_item_exit)
                .withIcon(new IconicsDrawable(this,
                        GoogleMaterial.Icon.gmd_power_settings_new)
                        .paddingDp(2)
                        .colorRes(R.color.red));

        mLoginOrRegisterItem = new PrimaryDrawerItem()
                .withName(R.string.drawer_item_login)
                .withIcon(R.drawable.ic_drawer_login);

        mQQLoginItem = new PrimaryDrawerItem()
                .withName(R.string.drawer_item_qq_login)
                .withIcon(R.drawable.ic_drawer_oauth_qq);

        mWeiboLoginItem = new PrimaryDrawerItem()
                .withName(R.string.drawer_item_weibo_login)
                .withIcon(R.drawable.ic_drawer_oauth_sina);

        mWeixinLoginItem = new PrimaryDrawerItem()
                .withName(R.string.drawer_item_weixin_login)
                .withIcon(R.drawable.ic_drawer_oauth_wechat);

        mAccountItem = new PrimaryDrawerItem()
                .withName(R.string.drawer_item_account)
                .withIcon(R.drawable.ic_drawer_login);

        mFollowItem = new PrimaryDrawerItem()
                .withName(R.string.drawer_item_follow)
                .withIcon(new IconicsDrawable(this,
                        GoogleMaterial.Icon.gmd_visibility)
                        .paddingDp(1)
                        .colorRes(R.color.light_blue));

        mFavoriteItem = new PrimaryDrawerItem()
                .withName(R.string.drawer_item_favorite)
                .withIcon(new IconicsDrawable(this,
                        GoogleMaterial.Icon.gmd_bookmark)
                        .paddingDp(2)
                        .colorRes(R.color.orange));

        nRouteItem = new PrimaryDrawerItem()
                .withName(R.string.drawer_item_route)
                .withIcon(new IconicsDrawable(this,
                        GoogleMaterial.Icon.gmd_navigation)
                        .colorRes(R.color.green));

        mSignItem = new PrimaryDrawerItem()
                .withName(R.string.drawer_item_sign)
                .withIcon(new IconicsDrawable(this,
                        GoogleMaterial.Icon.gmd_edit)
                        .paddingDp(2)
                        .colorRes(R.color.amber));
    }

    /**
     * 已经登录后的抽屉菜单
     *
     * @param toolbar            ToolBar
     * @param savedInstanceState savedInstanceState
     */
    private void hasLoginedMenu(Toolbar toolbar, Bundle savedInstanceState) {

        UserManager userManager = UserManager.getInstance(this);

        View headerView = getLayoutInflater().inflate(R.layout.header_layout, null);
        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawer.closeDrawer();
                Intent intent = new Intent(DrawerBaseActivity.this, UserActivity.class);
                startActivity(intent);
            }
        });

        mHeadImageView = (ImageView) headerView.findViewById(R.id.header_image);
        userManager.setHeadImage(mHeadImageView);

        TextView usernameText = (TextView) headerView.findViewById(R.id.tv_username);
        usernameText.setText(userManager.getUsername());

        mIntroText = (TextView) headerView.findViewById(R.id.tv_description);
        mIntroText.setText(userManager.getIntro());

        mDrawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withActionBarDrawerToggleAnimated(true)
                .withHeader(headerView)
                .withDisplayBelowStatusBar(true)
                .addDrawerItems(
                        mAccountItem,
                        mFollowItem,
                        mFavoriteItem,
                        nRouteItem,
                        mSignItem,
                        new DividerDrawerItem(),
                        mSettingsItem,
                        mExitItem
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (position != -1) {
                            onHasLoginItemClick(position);
                        }
                        return true;
                    }
                })
                .withHeaderClickable(true)
                .withSavedInstance(savedInstanceState)
                .build();
        mDrawer.setSelectionAtPosition(-1);
    }

    /**
     * 还没有登录时的抽屉菜单
     *
     * @param toolbar            ToolBar
     * @param savedInstanceState savedInstanceState
     */
    private void notLoginMenu(Toolbar toolbar, Bundle savedInstanceState) {

        mDrawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withActionBarDrawerToggleAnimated(true)
                .withHeader(R.layout.logo_header)
                .withDisplayBelowStatusBar(true)
                .addDrawerItems(
                        mLoginOrRegisterItem,
                        new SectionDrawerItem().withName(R.string.drawer_item_other_login),
                        mQQLoginItem,
                        mWeixinLoginItem,
                        mWeiboLoginItem,
                        new DividerDrawerItem(),
                        mExitItem
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position,
                                               IDrawerItem drawerItem) {
                        if (position == -1) {
                            return false;
                        }
                        onNotLoginItemClick(position);
                        return true;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .build();
        mDrawer.setSelectionAtPosition(-1);

    }

    /**
     * 更换抽屉菜单
     */
    private void changeDrawer() {
        if (UserManager.LOGIN) {
            hasLoginedMenu(mToolbar, mSavedInstanceState);
            mNotificationItem.setVisible(true);
        } else {
            notLoginMenu(mToolbar, mSavedInstanceState);
            mNotificationItem.setVisible(false);
        }
    }

    /**
     * 登出
     */
    protected void logout() {
        UserManager.logout(this);
        changeDrawer();
    }

    /**
     * 还没登录时点击抽屉菜单
     *
     * @param position 菜单下标
     */
    private void onNotLoginItemClick(int position) {
        mDrawer.closeDrawer();
        mDrawer.setSelectionAtPosition(-1);
        switch (position) {
            case 1: //登录或注册
                LoginManager loginManager = LoginManager.getInstance(this);
                loginManager.setLoginListener(new LoginManager.LoginListener() {
                    @Override
                    public void onFinish() {
                        UserManager.getInstance(DrawerBaseActivity.this).loginSuccess();
                        changeDrawer();
                    }
                });
                loginManager.createLoginDialog().show();
                break;
            case 3:
                Toast.makeText(this, "还没实现该功能", Toast.LENGTH_SHORT).show();
                break;
            case 4:
                Toast.makeText(this, "还没实现该功能", Toast.LENGTH_SHORT).show();
                break;
            case 5:
                Toast.makeText(this, "还没实现该功能", Toast.LENGTH_SHORT).show();
                break;
            case 7: //退出应用
                MyApplication.removeAllActivity();
                android.os.Process.killProcess(android.os.Process.myPid());
                break;
        }
    }

    /**
     * 已经登录时点击抽屉菜单
     *
     * @param position 菜单下标
     */
    private void onHasLoginItemClick(int position) {

        switch (position) {
            case 1:
                Intent intent = new Intent(this, UserActivity.class);
                startActivity(intent);
                break;
            case 2:
                intent = new Intent(this, FollowActivity.class);
                startActivity(intent);
                break;
            case 3:
                intent = new Intent(this, CollectionActivity.class);
                startActivity(intent);
                break;
            case 4:
                intent = new Intent(this, RouteActivity.class);
                startActivity(intent);
                break;
            case 5:
                intent = new Intent(this, RouteSignActivity.class);
                startActivity(intent);
                break;
            case 7:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            case 8:
                if (PreferenceManager.getDefaultSharedPreferences(this)
                        .getBoolean("clear_cache", true)) {
                    BmobPro.getInstance(this).clearCache();
                }
                MyApplication.removeAllActivity();
                intent = new Intent(this, ImageUploadService.class);
                stopService(intent);
                android.os.Process.killProcess(android.os.Process.myPid());
                break;
        }
        mDrawer.closeDrawer();
        mDrawer.setSelectionAtPosition(-1);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState = mDrawer.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {

        if (mDrawer != null && mDrawer.isDrawerOpen()) {
            mDrawer.closeDrawer();
        } else {
            finish();
            overridePendingTransition(0, R.anim.scale_and_fade_from_middle);
        }
    }
}
