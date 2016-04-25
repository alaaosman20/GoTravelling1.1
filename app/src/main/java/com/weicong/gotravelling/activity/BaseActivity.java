package com.weicong.gotravelling.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.weicong.gotravelling.MyApplication;
import com.weicong.gotravelling.R;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApplication.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        MyApplication.removeActivity(this);
        super.onDestroy();
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.in_from_right, R.anim.still);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.still, R.anim.out_from_right);
    }
}
