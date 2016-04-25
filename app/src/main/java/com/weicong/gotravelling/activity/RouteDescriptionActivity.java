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

import com.weicong.gotravelling.R;
import com.weicong.gotravelling.manager.RouteManager;
import com.weicong.gotravelling.model.Route;
import com.weicong.gotravelling.util.DialogUtil;

import cn.bmob.v3.listener.UpdateListener;

public class RouteDescriptionActivity extends SwipeBackBaseActivity {

    private EditText mDescriptionText;
    private TextView mLengthText;

    private MenuItem mCompletedItem;

    private Route mRoute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_description);

        int position = getIntent().getIntExtra(RouteManager.POSITION, 0);
        mRoute = RouteManager.getInstance(this).getRoute(position);

        initViews();
    }

    /**
     * 初始化View
     */
    private void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.modify_route_description);
        setSupportActionBar(toolbar);

        mDescriptionText = (EditText) findViewById(R.id.et_description);
        mDescriptionText.setText(mRoute.getDescription());

        mLengthText = (TextView) findViewById(R.id.tv_length);

        mDescriptionText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mLengthText.setText(generateLengthText(s.length()));
                if (s.length() > 200) {
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_completed) {
            final ProgressDialog progressDialog = DialogUtil.createProgressDialog(this);
            progressDialog.show();
            final String description = mDescriptionText.getText().toString();
            mRoute.setDescription(description);
            mRoute.update(this, new UpdateListener() {
                @Override
                public void onSuccess() {
                    Intent intent = new Intent();
                    intent.putExtra("text", description);
                    setResult(RESULT_OK, intent);
                    progressDialog.dismiss();
                    finish();
                    overridePendingTransition(R.anim.still, R.anim.out_from_right);
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
}
