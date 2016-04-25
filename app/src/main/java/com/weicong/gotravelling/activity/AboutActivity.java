package com.weicong.gotravelling.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.weicong.gotravelling.R;
import com.weicong.gotravelling.util.DialogUtil;

public class AboutActivity extends SwipeBackBaseActivity {

    private Handler mHandler = new Handler();

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        mProgressDialog = DialogUtil.createProgressDialog(this);

        initViews();
    }

    /**
     * 初始化View
     */
    private void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.action_about);
        setSupportActionBar(toolbar);

        Button shareButton = (Button) findViewById(R.id.btn_share_app);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        Button checkButton = (Button) findViewById(R.id.btn_check_app);
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressDialog.show();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mProgressDialog.dismiss();
                    }
                }, 500);
            }
        });

        TextView feedback = (TextView) findViewById(R.id.tv_feedback);
        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
