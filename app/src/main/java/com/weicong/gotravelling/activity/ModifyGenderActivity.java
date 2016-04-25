package com.weicong.gotravelling.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;

import com.weicong.gotravelling.R;
import com.weicong.gotravelling.event.GenderEvent;
import com.weicong.gotravelling.manager.UserManager;
import com.weicong.gotravelling.util.DialogUtil;

import de.greenrobot.event.EventBus;

public class ModifyGenderActivity extends SwipeBackBaseActivity {

    private RadioButton mMaleButton;
    private RadioButton mFemaleButton;

    private boolean mGender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_gender);

        initViews();
    }

    /**
     * 初始化View
     */
    private void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.modify_gender);
        setSupportActionBar(toolbar);

        mMaleButton = (RadioButton) findViewById(R.id.rb_male);
        mFemaleButton = (RadioButton) findViewById(R.id.rb_female);

        mGender = UserManager.getInstance(this).getGender();
        if (mGender) {
            mFemaleButton.setChecked(true);
        } else {
            mMaleButton.setChecked(true);
        }

        View maleLayout = findViewById(R.id.rl_male);
        maleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGender = false;
                mFemaleButton.setChecked(false);
                mMaleButton.setChecked(true);
            }
        });

        View femaleLayout = findViewById(R.id.rl_female);
        femaleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGender = true;
                mMaleButton.setChecked(false);
                mFemaleButton.setChecked(true);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_completed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_completed) {
            final ProgressDialog progressDialog = DialogUtil.createProgressDialog(this);
            progressDialog.show();

            UserManager.getInstance(this).setGender(mGender, new UserManager.OnUpdateListener() {
                @Override
                public void onSuccess() {
                    EventBus.getDefault().post(new GenderEvent());
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
