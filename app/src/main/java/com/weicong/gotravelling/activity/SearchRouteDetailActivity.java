package com.weicong.gotravelling.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bmob.BmobProFile;
import com.bmob.btp.callback.GetAccessUrlListener;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.weicong.gotravelling.MyApplication;
import com.weicong.gotravelling.R;
import com.weicong.gotravelling.adapter.ImagePagerAdapter;
import com.weicong.gotravelling.adapter.RouteFinishAdapter;
import com.weicong.gotravelling.manager.RouteManager;
import com.weicong.gotravelling.manager.UserManager;
import com.weicong.gotravelling.model.Daily;
import com.weicong.gotravelling.model.Route;
import com.weicong.gotravelling.model.Sight;
import com.weicong.gotravelling.model.User;
import com.weicong.gotravelling.util.DialogUtil;
import com.weicong.gotravelling.view.ChildViewPager;
import com.weicong.gotravelling.view.RefreshCircleProgress;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.FindListener;

public class SearchRouteDetailActivity extends SwipeBackBaseActivity {

    private Route mRoute;

    private ExpandableListView mExpandableListView;

    private RefreshCircleProgress mRefreshCircleProgress;

    private List<Daily> mParents;
    private Map<Integer, List<Sight>> mSights = new HashMap<>();

    private int mDay = 0;

    private List<String> mImages = new ArrayList<>();

    private ChildViewPager mChildViewPager;
    private ImageView mNoPic;

    private ProgressDialog mProgressDialog;

    private MenuItem mLocationMenu;

    private boolean mRouteIsFinished = false;

    private TextView mFollowText;
    private TextView mCommentText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_route_detail);

        mRoute = MyApplication.getInstance().getRoute();
        if (mRoute.getStatus() != null && mRoute.getStatus() == Route.FINISHED) {
            mRouteIsFinished = true;
        }

        mProgressDialog = DialogUtil.createProgressDialog(this);

        initViews();
    }

    /**
     * 初始化View
     */
    private void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(mRoute.getName());
        setSupportActionBar(toolbar);

        mExpandableListView = (ExpandableListView) findViewById(R.id.expand_list);
        mExpandableListView.setGroupIndicator(null);
        View header = getLayoutInflater().inflate(R.layout.header_finished, null);
        TextView descriptionText = (TextView) header.findViewById(R.id.tv_description);
        descriptionText.setText(mRoute.getDescription());
        mExpandableListView.addHeaderView(header, null, false);
        ImageView followImage = (ImageView) header.findViewById(R.id.iv_follow);
        followImage.setImageDrawable(new IconicsDrawable(this,
                GoogleMaterial.Icon.gmd_visibility).colorRes(R.color.gray));
        mFollowText = (TextView) header.findViewById(R.id.tv_follow_num);
        int follow = 0;
        if (mRoute.getFollowNum() != null) {
            follow = mRoute.getFollowNum();
        }
        mFollowText.setText(String.valueOf(follow));
        ImageView commentImage = (ImageView) header.findViewById(R.id.iv_comment);
        commentImage.setImageDrawable(new IconicsDrawable(this,
                GoogleMaterial.Icon.gmd_comment).colorRes(R.color.gray).paddingDp(2).iconOffsetYDp(2));
        mCommentText = (TextView) header.findViewById(R.id.tv_comment_num);
        int comment = 0;
        if (mRoute.getCommentNum() != null) {
            comment = mRoute.getCommentNum();
        }
        mCommentText.setText(String.valueOf(comment));
        ImageView favorImage = (ImageView) header.findViewById(R.id.iv_favor);
        favorImage.setImageDrawable(new IconicsDrawable(this,
                GoogleMaterial.Icon.gmd_favorite).colorRes(R.color.gray).paddingDp(2).iconOffsetYDp(1));
        final TextView favorNum = (TextView) header.findViewById(R.id.tv_favor_num);
        int favor = 0;
        if (mRoute.getFavor() != null) {
            favor = mRoute.getFavor();
        }
        favorNum.setText(String.valueOf(favor));
        mChildViewPager = (ChildViewPager) header.findViewById(R.id.images_pager);
        mNoPic = (ImageView) header.findViewById(R.id.iv_no_pic);

        View footer = getLayoutInflater().inflate(R.layout.footer_finished, null);
        mExpandableListView.addFooterView(footer, null, false);
        View noteView = footer.findViewById(R.id.rl_note);
        noteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchRouteDetailActivity.this, FinishNoteActivity.class);
                startActivity(intent);
            }
        });
        View commentView = footer.findViewById(R.id.rl_comment);
        commentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchRouteDetailActivity.this, CommentActivity.class);
                startActivityForResult(intent, 0);
                overridePendingTransition(R.anim.in_from_right, R.anim.still);
            }
        });

        mRefreshCircleProgress = (RefreshCircleProgress) findViewById(R.id.refresh_progress);
        mRefreshCircleProgress.setColorSchemeResources(R.color.colorPrimary);
        mRefreshCircleProgress.setRefreshing(true);

        FloatingActionButton collectButton = (FloatingActionButton) findViewById(R.id.action_collect);
        if (mRouteIsFinished) {
            collectButton.setIconDrawable(new IconicsDrawable(this,
                    GoogleMaterial.Icon.gmd_star).colorRes(R.color.white));
        } else {
            collectButton.setIconDrawable(new IconicsDrawable(this,
                    GoogleMaterial.Icon.gmd_visibility).colorRes(R.color.white).paddingDp(2));
        }

        collectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRouteIsFinished) {
                    collect();
                } else {
                    follow();
                }

            }
        });

        FloatingActionButton goodButton = (FloatingActionButton) findViewById(R.id.action_good);
        goodButton.setIconDrawable(new IconicsDrawable(this,
                GoogleMaterial.Icon.gmd_favorite).colorRes(R.color.white).paddingDp(2));
        goodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressDialog.show();
                RouteManager.getInstance(SearchRouteDetailActivity.this).good(mRoute,
                        new RouteManager.OnRouteListener() {
                            @Override
                            public void onSuccess(String s) {
                                favorNum.setText(String.valueOf(mRoute.getFavor()));
                                mProgressDialog.dismiss();
                            }

                            @Override
                            public void onError(String s) {
                                mProgressDialog.dismiss();
                            }
                        });
            }
        });

        User myUser = UserManager.getInstance(this).getUser();
        if (!UserManager.LOGIN || mRoute.getCreator().getObjectId().equals(myUser.getObjectId())) {
            findViewById(R.id.fab_menu).setVisibility(View.GONE);
        }
        getRouteData();
    }

    /**
     * 获取路线的详细数据
     */
    private void getRouteData() {
        setTitle(mRoute.getName());
        BmobQuery<Daily> query = new BmobQuery<>();
        query.addWhereEqualTo("route", new BmobPointer(mRoute));
        query.order("day");
        query.findObjects(this, new FindListener<Daily>() {
            @Override
            public void onSuccess(List<Daily> list) {
                mParents = list;
                if (list.size() == 0) {
                    getImageUrl();
                } else {
                    for (Daily daily : list) {
                        getDailySight(daily);
                    }
                }
            }

            @Override
            public void onError(int i, String s) {
                Snackbar.make(findViewById(R.id.root), s, Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 获取日程里的景点
     *
     * @param daily 日程
     */
    private void getDailySight(final Daily daily) {
        BmobQuery<Sight> query = new BmobQuery<>();
        query.order("index");
        query.addWhereEqualTo("daily", new BmobPointer(daily));
        query.findObjects(this, new FindListener<Sight>() {
            @Override
            public void onSuccess(List<Sight> list) {
                mDay++;
                mSights.put(daily.getDay(), list);
                if (mDay == mParents.size()) {
                    getImageUrl();
                }
            }

            @Override
            public void onError(int i, String s) {
                Snackbar.make(findViewById(R.id.root), s, Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 加载数据结束
     */
    private void loadFinished() {
        RouteFinishAdapter adapter = new RouteFinishAdapter(this, mParents, mSights);
        mExpandableListView.setAdapter(adapter);
        mChildViewPager.setAdapter(new ImagePagerAdapter(this, mImages));
        mChildViewPager.setCurrentItem(mImages.size() * 50);
        mRefreshCircleProgress.setRefreshing(false);

        RouteManager.getInstance(this).watch(mRoute, new RouteManager.OnRouteListener() {
            @Override
            public void onSuccess(String s) {

            }

            @Override
            public void onError(String s) {

            }
        });

        int count = 0;
        for (int i = 0; i < mSights.size(); i++) {
            count += mSights.get(i).size();
        }
        if (count >= 2) {
            mLocationMenu.setVisible(true);
        }
    }

    /**
     * 获取图片的url
     */
    private void getImageUrl() {
        List<String> images = mRoute.getImages();
        if (images != null && images.size() > 0) {
            getImageUrl(images.get(0), images, 1);
        } else {
            mChildViewPager.setVisibility(View.GONE);
            mNoPic.setVisibility(View.VISIBLE);
            loadFinished();
        }
    }

    /**
     * 获取图片的Url
     *
     * @param filename 图片名
     * @param images 图片集合
     * @param next 下一张图片的位置
     */
    private void getImageUrl(String filename, final List<String> images, final int next) {
        BmobProFile.getInstance(this).getAccessURL(filename, new GetAccessUrlListener() {
            @Override
            public void onSuccess(BmobFile bmobFile) {
                mImages.add(bmobFile.getUrl());
                if (next != images.size()) {
                    getImageUrl(images.get(next), images, next + 1);
                } else {
                    loadFinished();
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_route_search, menu);
        mLocationMenu = menu.findItem(R.id.action_location);
        mLocationMenu.setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_location) {
            ProgressDialog progressDialog = DialogUtil.createProgressDialog(this);
            progressDialog.show();

            ArrayList<String> list = new ArrayList<>();

            for (int i = 0; i < mParents.size(); i++) {
                for (Sight sight : mSights.get(i)) {
                    list.add(sight.getLongitude().toString());
                    list.add(sight.getLatitude().toString());
                }
            }
            progressDialog.dismiss();
            Intent intent = new Intent(this, ShowDailyMapActivity.class);
            intent.putStringArrayListExtra("data", list);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.action_creator) {
            MyApplication.getInstance().setUser(mRoute.getCreator());
            Intent intent = new Intent(this, CreatorActivity.class);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.action_share) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain"); // 纯文本
            intent.putExtra(Intent.EXTRA_SUBJECT, "同享旅行");
            String s = "一款旅游路线记录与分享的Android应用，支持路线规划和分享、旅游过程记录和旅游社交等功能，" +
                    "旨在为用户提供一个方便快捷的旅游私人订制、记录和分享的应用。";
            intent.putExtra(Intent.EXTRA_TEXT, s);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(Intent.createChooser(intent, "分享"));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 收藏
     */
    private void collect() {
        mProgressDialog.show();
        RouteManager.getInstance(SearchRouteDetailActivity.this).collect(mRoute, new RouteManager.OnRouteListener() {
            @Override
            public void onSuccess(String s) {
                mProgressDialog.dismiss();
                Toast.makeText(SearchRouteDetailActivity.this, "收藏成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String s) {
                mProgressDialog.dismiss();
                if (s != null) {
                    Toast.makeText(SearchRouteDetailActivity.this, s, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 关注
     */
    private void follow() {
        mProgressDialog.show();
        RouteManager.getInstance(SearchRouteDetailActivity.this).followRoute(mRoute, new RouteManager.OnRouteListener() {
            @Override
            public void onSuccess(String s) {
                mProgressDialog.dismiss();
                mFollowText.setText(String.valueOf(mRoute.getFollowNum()));
                Toast.makeText(SearchRouteDetailActivity.this, "关注成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String s) {
                mProgressDialog.dismiss();
                if (s != null) {
                    Toast.makeText(SearchRouteDetailActivity.this, s, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            mCommentText.setText(String.valueOf(mRoute.getCommentNum()));
        }
    }
}
