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

import com.weicong.gotravelling.MyApplication;
import com.weicong.gotravelling.R;
import com.weicong.gotravelling.manager.MapAPIManager;
import com.weicong.gotravelling.manager.UserManager;
import com.weicong.gotravelling.model.Note;
import com.weicong.gotravelling.model.Route;
import com.weicong.gotravelling.util.DialogUtil;
import com.weicong.gotravelling.util.SPUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.bmob.v3.listener.SaveListener;

public class EditNoteActivity extends SwipeBackBaseActivity {

    private Route mRoute;

    private EditText mEditText;
    private TextView mLengthText;

    private MenuItem mCompletedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        mRoute = MyApplication.getInstance().getRoute();

        initViews();
    }

    /**
     * 初始化View
     */
    private void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.add_note);
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
        String s = length + "/200";
        return s;
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
            final Note note = new Note();
            String content = mEditText.getText().toString();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String date = format.format(new Date());
            note.setContent(content);
            note.setRoute(mRoute);
            note.setDate(date);
            note.setAddress(SPUtil.get(this, MapAPIManager.ADDRESS, "").toString());
            note.save(this, new SaveListener() {
                @Override
                public void onSuccess() {
                    progressDialog.dismiss();
                    back(note);
                }

                @Override
                public void onFailure(int i, String s) {
                    progressDialog.dismiss();
                }
            });
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 返回
     *
     * @param note 旅游小记
     */
    private void back(Note note) {
        Intent intent = new Intent();
        intent.putExtra(Note.ID, note.getObjectId());
        intent.putExtra(Note.CONTENT, note.getContent());
        intent.putExtra(Note.DATE, note.getDate());
        intent.putExtra(Note.ADDRESS, note.getAddress());
        setResult(RESULT_OK, intent);
        finish();
        overridePendingTransition(R.anim.still, R.anim.out_from_right);
    }
}
