package com.weicong.gotravelling.activity;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.weicong.gotravelling.MyApplication;
import com.weicong.gotravelling.R;
import com.weicong.gotravelling.adapter.NoteAdapter;
import com.weicong.gotravelling.model.Note;
import com.weicong.gotravelling.model.Route;
import com.weicong.gotravelling.view.RefreshCircleProgress;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.FindListener;

public class FinishNoteActivity extends SwipeBackBaseActivity {

    private Route mRoute;

    private RefreshCircleProgress mRefreshCircleProgress;

    private RecyclerView mRecyclerView;

    private NoteAdapter mAdapter;
    private List<Note> mItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish_note);

        mRoute = MyApplication.getInstance().getRoute();

        initViews();
    }

    /**
     * 初始化View
     */
    private void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.note);
        setSupportActionBar(toolbar);

        mRefreshCircleProgress = (RefreshCircleProgress) findViewById(R.id.refresh_progress);
        mRefreshCircleProgress.setColorSchemeResources(R.color.colorPrimary);
        mRefreshCircleProgress.setRefreshing(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        getNotes();
    }

    /**
     * 从后台获取游记数据
     */
    private void getNotes() {
        BmobQuery<Note> query = new BmobQuery<>();
        query.order("-createdAt");
        query.addWhereEqualTo("route", new BmobPointer(mRoute));
        query.findObjects(this, new FindListener<Note>() {
            @Override
            public void onSuccess(List<Note> list) {
                mRefreshCircleProgress.setRefreshing(false);
                mItems = list;
                mAdapter = new NoteAdapter(FinishNoteActivity.this, mItems);
                mAdapter.setOnClickListener(new NoteAdapter.OnClickListener() {
                    @Override
                    public void onClick(int position) {
                        showContent(mItems.get(position).getContent());
                    }
                });
                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    /**
     * 显示游记内容
     *
     * @param content 游记内容
     */
    private void showContent(String content) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.note)
                .setMessage(content);
        builder.create().show();
    }
}
