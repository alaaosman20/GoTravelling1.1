package com.weicong.gotravelling.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.weicong.gotravelling.R;
import com.weicong.gotravelling.manager.UserManager;
import com.weicong.gotravelling.util.DialogUtil;

public class ModifyPasswordActivity extends SwipeBackBaseActivity {

    private EditText mOldPasswordText;
    private EditText mNewPasswordText;

    private TextInputLayout mOldLayout;
    private TextInputLayout mNewLayout;

    private MenuItem mCompletedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_password);

        initViews();
    }

    /**
     * 初始化View
     */
    private void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.modify_password);
        setSupportActionBar(toolbar);

        ImageView oldImage = (ImageView) findViewById(R.id.iv_old);
        oldImage.setImageDrawable(new IconicsDrawable(this,
                GoogleMaterial.Icon.gmd_lock).paddingDp(1).colorRes(R.color.icon_blue));

        ImageView newImage = (ImageView) findViewById(R.id.iv_new);
        newImage.setImageDrawable(new IconicsDrawable(this,
                GoogleMaterial.Icon.gmd_lock_open).colorRes(R.color.icon_blue));

        mOldLayout = (TextInputLayout) findViewById(R.id.layout_old);
        mOldPasswordText = mOldLayout.getEditText();
        mNewLayout = (TextInputLayout) findViewById(R.id.layout_new);
        mNewPasswordText = mNewLayout.getEditText();

        mOldPasswordText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 6 && mNewPasswordText.getText().length() >= 6) {
                    mCompletedItem.setEnabled(true);
                } else {
                    mCompletedItem.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mNewPasswordText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 6 && mOldPasswordText.getText().length() >= 6) {
                    mCompletedItem.setEnabled(true);
                } else {
                    mCompletedItem.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

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

            String oldPassword = mOldPasswordText.getText().toString();
            String newPassword = mNewPasswordText.getText().toString();
            UserManager.getInstance(this).changePassword(oldPassword, newPassword, new UserManager.OnUpdateListener() {
                @Override
                public void onSuccess() {
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
