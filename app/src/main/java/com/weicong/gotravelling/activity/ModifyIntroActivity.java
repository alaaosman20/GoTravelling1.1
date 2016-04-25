package com.weicong.gotravelling.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.weicong.gotravelling.R;
import com.weicong.gotravelling.event.IntroEvent;
import com.weicong.gotravelling.manager.UserManager;
import com.weicong.gotravelling.util.DialogUtil;

import de.greenrobot.event.EventBus;

public class ModifyIntroActivity extends SwipeBackBaseActivity {

    private EditText mIntroText;
    private TextView mLengthText;

    private MenuItem mCompletedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_intro);

        initViews();
    }

    /**
     * 初始化View
     */
    private void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.modify_intro);
        setSupportActionBar(toolbar);

        mIntroText = (EditText) findViewById(R.id.et_intro);
        String intro = UserManager.getInstance(this).getIntro();
        if (intro.equals("(添加一句话介绍自己)")) {
            intro = "";
        }
        mIntroText.setText(intro);

        mLengthText = (TextView) findViewById(R.id.tv_length);

        mIntroText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mLengthText.setText(generateLengthText(s.length()));
                if (s.length() > 30) {
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
        String s = length + "/30";
        return s;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_completed, menu);
        mCompletedItem = menu.findItem(R.id.action_completed);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_completed) {
            final ProgressDialog progressDialog = DialogUtil.createProgressDialog(this);
            progressDialog.show();
            String intro = mIntroText.getText().toString();
            UserManager.getInstance(this).setIntro(intro, new UserManager.OnUpdateListener() {
                @Override
                public void onSuccess() {
                    EventBus.getDefault().post(new IntroEvent());
                    progressDialog.dismiss();
                    finish();
                    overridePendingTransition(0, R.anim.out_from_right);
                }

                @Override
                public void onError() {
                    progressDialog.dismiss();
                }
            });
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
