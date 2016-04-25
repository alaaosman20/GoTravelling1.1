package com.weicong.gotravelling.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.weicong.gotravelling.MyApplication;
import com.weicong.gotravelling.R;
import com.weicong.gotravelling.SwipeBackLayout;
import com.weicong.gotravelling.adapter.CommentAdapter;
import com.weicong.gotravelling.manager.UserManager;
import com.weicong.gotravelling.model.Route;
import com.weicong.gotravelling.model.RouteComment;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.FindListener;

public class CommentActivity extends SwipeBackBaseActivity {

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private ListView mListView;

    private CommentAdapter mAdapter;

    private List<RouteComment> mItems;

    private Route mRoute;

    private boolean mAddComment = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        mRoute = MyApplication.getInstance().getRoute();

        SwipeBackLayout swipeBackLayout = getSwipeBackLayout();
        swipeBackLayout.addSwipeListener(new SwipeBackLayout.SwipeListener() {
            @Override
            public void onScrollStateChange(int state, float scrollPercent) {

            }

            @Override
            public void onEdgeTouch(int edgeFlag) {

            }

            @Override
            public void onScrollOverThreshold() {
                if (mAddComment) {
                    setResult(RESULT_OK);
                } else {
                    setResult(RESULT_CANCELED);
                }
            }
        });

        initViews();
    }

    /**
     * 初始化View
     */
    private void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.comment);
        setSupportActionBar(toolbar);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getComment();
            }
        });
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
            }
        });

        mListView = (ListView) findViewById(R.id.listView);
        mListView.setEnabled(false);

        FloatingActionButton commentButton = (FloatingActionButton) findViewById(R.id.action_comment);
        commentButton.setIconDrawable(new IconicsDrawable(this,
                GoogleMaterial.Icon.gmd_comment).colorRes(R.color.white));
        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CommentActivity.this, WriteCommentActivity.class);
                startActivityForResult(intent, 0);
                overridePendingTransition(R.anim.in_from_right, R.anim.still);
            }
        });

        if (!UserManager.LOGIN) {
            commentButton.setVisibility(View.GONE);
        }
        getComment();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            mAddComment = true;
            String username = data.getStringExtra(RouteComment.USERNAME);
            String date = data.getStringExtra(RouteComment.TIME);
            String image = data.getStringExtra(RouteComment.IMAGE);
            String content = data.getStringExtra(RouteComment.CONTENT);
            RouteComment comment = new RouteComment();
            comment.setUsername(username);
            comment.setImage(image);
            comment.setTime(date);
            comment.setContent(content);
            mItems.add(0, comment);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed() {
        if (mAddComment) {
            setResult(RESULT_OK);
        } else {
            setResult(RESULT_CANCELED);
        }
        finish();
        overridePendingTransition(0, R.anim.out_from_right);
    }

    /**
     * 获取评论数据
     */
    private void getComment() {
        BmobQuery<RouteComment> query = new BmobQuery<>();
        query.addWhereEqualTo("route", new BmobPointer(mRoute));
        query.order("-createdAt");
        query.findObjects(this, new FindListener<RouteComment>() {
            @Override
            public void onSuccess(List<RouteComment> list) {
                mItems = list;
                mAdapter = new CommentAdapter(CommentActivity.this, mItems);
                mListView.setAdapter(mAdapter);
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }
}
