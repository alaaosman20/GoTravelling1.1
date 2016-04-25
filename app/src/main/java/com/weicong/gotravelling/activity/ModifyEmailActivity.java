package com.weicong.gotravelling.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.weicong.gotravelling.R;
import com.weicong.gotravelling.event.EmailEvent;
import com.weicong.gotravelling.manager.UserManager;
import com.weicong.gotravelling.util.DialogUtil;
import com.weicong.gotravelling.util.TextUtil;
import com.weicong.gotravelling.view.FlatButton;

import de.greenrobot.event.EventBus;

public class ModifyEmailActivity extends SwipeBackBaseActivity {

    private UserManager mUserManager;
    private TextView mEmailText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_email);

        mUserManager = UserManager.getInstance(this);

        initViews();
    }

    /**
     * 初始化View
     */
    private void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.modify_email);
        setSupportActionBar(toolbar);

        ImageView emailImage = (ImageView) findViewById(R.id.iv_email);
        emailImage.setImageDrawable(new IconicsDrawable(this,
                GoogleMaterial.Icon.gmd_email).colorRes(R.color.icon_blue));

        View emailLayout = findViewById(R.id.rl_email);
        emailLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEmailDialog();
            }
        });

        SwitchCompat validate = (SwitchCompat) findViewById(R.id.sc_validate);
        Boolean b = mUserManager.getEmailValidate();
        if (b == null) {
            b = false;
        }
        validate.setChecked(b);
        mEmailText = (TextView) findViewById(R.id.tv_email);
        mEmailText.setText(mUserManager.getEmail());
    }

    /**
     * 显示对话框
     */
    private void showEmailDialog() {
        View v = getLayoutInflater().inflate(R.layout.dialog_validate_email, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.modify_email)
                .setView(v);

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        final FlatButton okButton = (FlatButton) v.findViewById(R.id.btn_ok);
        okButton.setEnabled(false);

        final TextInputLayout emailLayout = (TextInputLayout) v.findViewById(R.id.layout_email);
        emailLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtil.isEmail(s.toString())) {
                    okButton.setEnabled(true);
                } else {
                    okButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = DialogUtil.createProgressDialog(ModifyEmailActivity.this);
                progressDialog.show();
                final String email = emailLayout.getEditText().getText().toString();

                UserManager.getInstance(ModifyEmailActivity.this).setEmail(email, new UserManager.OnUpdateListener() {
                    @Override
                    public void onSuccess() {
                        EventBus.getDefault().post(new EmailEvent());
                        mEmailText.setText(mUserManager.getEmail());
                        progressDialog.dismiss();
                        alertDialog.dismiss();
                        Toast.makeText(ModifyEmailActivity.this, "验证成功，请到邮箱中进行激活", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError() {
                        progressDialog.dismiss();
                        Toast.makeText(ModifyEmailActivity.this, "该邮箱已被使用", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
