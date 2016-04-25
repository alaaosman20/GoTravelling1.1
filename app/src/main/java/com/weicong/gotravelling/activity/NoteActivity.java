package com.weicong.gotravelling.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.getbase.floatingactionbutton.AddFloatingActionButton;
import com.weicong.gotravelling.MyApplication;
import com.weicong.gotravelling.R;
import com.weicong.gotravelling.adapter.NoteAdapter;
import com.weicong.gotravelling.model.Note;
import com.weicong.gotravelling.model.Route;
import com.weicong.gotravelling.util.DialogUtil;
import com.weicong.gotravelling.view.RefreshCircleProgress;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;

public class NoteActivity extends SwipeBackBaseActivity {

    private RefreshCircleProgress mRefreshCircleProgress;

    private RecyclerView mRecyclerView;

    private NoteAdapter mAdapter;
    private List<Note> mItems;

    private Route mRoute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

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
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(createCallback());
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        AddFloatingActionButton addButton = (AddFloatingActionButton) findViewById(R.id.fab_add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NoteActivity.this, EditNoteActivity.class);
                startActivityForResult(intent, 0);
                overridePendingTransition(R.anim.in_from_right, R.anim.still);
            }
        });

        getNotes();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            String id = data.getStringExtra(Note.ID);
            String content = data.getStringExtra(Note.CONTENT);
            String date = data.getStringExtra(Note.DATE);
            String address = data.getStringExtra(Note.ADDRESS);

            Note note = new Note();
            note.setObjectId(id);
            note.setContent(content);
            note.setDate(date);
            note.setAddress(address);

            mItems.add(0, note);
            mAdapter.notifyDataSetChanged();
        }
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
                mAdapter = new NoteAdapter(NoteActivity.this, mItems);
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

    public ItemTouchHelper.Callback createCallback() {
        return new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView,
                                        RecyclerView.ViewHolder viewHolder) {
                return makeMovementFlags(0, ItemTouchHelper.START | ItemTouchHelper.END);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                deleteNote(viewHolder.getAdapterPosition());
            }

            @Override
            public boolean isLongPressDragEnabled() {
                return false;
            }

            @Override
            public boolean isItemViewSwipeEnabled() {
                return true;
            }

        };

    }

    /**
     * 删除游记
     *
     * @param position 位置
     */
    private void deleteNote(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.action_delete)
                .setMessage(R.string.delete_note_message)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        realDeleteNote(position);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAdapter.notifyDataSetChanged();
                    }
                })
                .setCancelable(false);
        builder.create().show();
    }

    /**
     * 真正删除旅游小记
     *
     * @param position 位置
     */
    private void realDeleteNote(final int position) {
        final ProgressDialog progressDialog = DialogUtil.createProgressDialog(this);
        progressDialog.show();
        Note note = mItems.get(position);
        note.delete(this, new DeleteListener() {
            @Override
            public void onSuccess() {
                mAdapter.delete(position);
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(int i, String s) {
                progressDialog.dismiss();
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
