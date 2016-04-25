package com.weicong.gotravelling.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.bmob.BmobProFile;
import com.bmob.btp.callback.GetAccessUrlListener;
import com.weicong.gotravelling.MyApplication;
import com.weicong.gotravelling.R;
import com.weicong.gotravelling.manager.RouteManager;
import com.weicong.gotravelling.manager.UserManager;
import com.weicong.gotravelling.model.Route;
import com.weicong.gotravelling.model.RouteComment;
import com.weicong.gotravelling.model.User;
import com.weicong.gotravelling.util.DialogUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;

public class WriteCommentActivity extends SwipeBackBaseActivity {

    private Route mRoute;

    private EditText mEditText;
    private TextView mLengthText;

    private MenuItem mCompletedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_comment);

        mRoute = MyApplication.getInstance().getRoute();

        initViews();
    }

    /**
     * 初始化View
     */
    private void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.add_comment);
        setSupportActionBar(toolbar);

        mEditText = (EditText) findViewById(R.id.et_content);
        mEditText.setText(UserManager.getInstance(this).getDescription());

        mLengthText = (TextView) findViewById(R.id.tv_length);

        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mLengthText.setText(generateLengthText(s.length()));
                if (s.length() > 200 || s.length() < 1) {
                    mCompletedItem.setEnabled(false);
                } else {
                    mCompletedItem.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private String generateLengthText(int length) {
        return length + "/200";
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_completed, menu);
        mCompletedItem = menu.findItem(R.id.action_completed);
        mCompletedItem.setEnabled(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_completed) {
            final ProgressDialog progressDialog = DialogUtil.createProgressDialog(this);
            progressDialog.show();
            final RouteComment comment = new RouteComment();
            String content = mEditText.getText().toString();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String date = format.format(new Date());
            comment.setContent(content);
            comment.setRoute(mRoute);
            comment.setTime(date);
            User user = UserManager.getInstance(this).getUser();
            comment.setUsername(user.getUsername());

            if (user.getImage() != null) {
                BmobProFile.getInstance(this).getAccessURL(user.getImage(), new GetAccessUrlListener() {
                    @Override
                    public void onSuccess(BmobFile bmobFile) {
                        comment.setImage(bmobFile.getUrl());
                        addComment(comment, progressDialog);
                    }

                    @Override
                    public void onError(int i, String s) {

                    }
                });
            } else {
                addComment(comment, progressDialog);
            }

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 添加评论
     *
     * @param comment 评论
     * @param progressDialog 环形对话框
     */
    private void addComment(final RouteComment comment, final ProgressDialog progressDialog) {
        comment.save(this, new SaveListener() {
            @Override
            public void onSuccess() {
                RouteManager.getInstance(WriteCommentActivity.this).addComment(mRoute,
                        new RouteManager.OnRouteListener() {
                            @Override
                            public void onSuccess(String s) {
                                progressDialog.dismiss();
                                back(comment);
                            }

                            @Override
                            public void onError(String s) {
                                progressDialog.dismiss();
                            }
                        });

            }

            @Override
            public void onFailure(int i, String s) {
                progressDialog.dismiss();
            }
        });
    }

    /**
     * 返回
     *
     * @param comment 评论
     */
    private void back(RouteComment comment) {
        Intent intent = new Intent();
        intent.putExtra(RouteComment.USERNAME, comment.getUsername());
        intent.putExtra(RouteComment.CONTENT, comment.getContent());
        intent.putExtra(RouteComment.TIME, comment.getTime());
        intent.putExtra(RouteComment.IMAGE, comment.getImage());
        setResult(RESULT_OK, intent);
        finish();
        overridePendingTransition(R.anim.still, R.anim.out_from_right);
    }
}
