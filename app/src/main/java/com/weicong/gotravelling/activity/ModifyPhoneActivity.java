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
import com.weicong.gotravelling.event.PhoneEvent;
import com.weicong.gotravelling.manager.UserManager;
import com.weicong.gotravelling.util.DialogUtil;
import com.weicong.gotravelling.util.TextUtil;
import com.weicong.gotravelling.view.DownTimerProgress;
import com.weicong.gotravelling.view.FlatButton;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.RequestSMSCodeListener;
import cn.bmob.v3.listener.VerifySMSCodeListener;
import de.greenrobot.event.EventBus;

public class ModifyPhoneActivity extends SwipeBackBaseActivity {

    private UserManager mUserManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_phone);

        mUserManager = UserManager.getInstance(this);

        initViews();
    }

    /**
     * 初始化View
     */
    private void initViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.modify_phone);
        setSupportActionBar(toolbar);

        ImageView phoneImage = (ImageView) findViewById(R.id.iv_phone);
        phoneImage.setImageDrawable(new IconicsDrawable(this,
                GoogleMaterial.Icon.gmd_phone_android).colorRes(R.color.icon_blue));

        View phoneLayout = findViewById(R.id.rl_phone);
        phoneLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPhoneDialog();
            }
        });

        SwitchCompat validate = (SwitchCompat) findViewById(R.id.sc_validate);
        validate.setChecked(mUserManager.getPhoneValidate());
        TextView phoneText = (TextView) findViewById(R.id.tv_phone);
        phoneText.setText(mUserManager.getPhone());
    }

    /**
     * 显示对话框
     */
    private void showPhoneDialog() {
        View v = getLayoutInflater().inflate(R.layout.dialog_validate_phone, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.modify_phone)
                .setView(v);

        final AlertDialog dialog = builder.create();
        dialog.show();

        final TextInputLayout phoneLayout = (TextInputLayout) v.findViewById(R.id.layout_phone);
        final TextInputLayout codeLayout = (TextInputLayout) v.findViewById(R.id.layout_code);
        final FlatButton getCodeButton = (FlatButton) v.findViewById(R.id.btn_get_code);
        final FlatButton okButton = (FlatButton) v.findViewById(R.id.btn_ok);
        final FlatButton reGetCodeButton = (FlatButton) v.findViewById(R.id.btn_re_get_code);
        final DownTimerProgress progress = (DownTimerProgress) v.findViewById(R.id.progress);

        getCodeButton.setEnabled(false);
        okButton.setEnabled(false);
        reGetCodeButton.setEnabled(false);
        reGetCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress.reset();
                progress.beginRun();
                reGetCodeButton.setEnabled(false);
                String phone = phoneLayout.getEditText().getText().toString();
                BmobSMS.requestSMSCode(ModifyPhoneActivity.this, phone, "手机号码验证",
                        new RequestSMSCodeListener() {

                            @Override
                            public void done(Integer smsId, BmobException ex) {

                            }
                        });

            }
        });

        getCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCodeButton.setVisibility(View.GONE);
                progress.setVisibility(View.VISIBLE);
                reGetCodeButton.setVisibility(View.VISIBLE);
                String phone = phoneLayout.getEditText().getText().toString();
                BmobSMS.requestSMSCode(ModifyPhoneActivity.this, phone, "手机号码验证",
                        new RequestSMSCodeListener() {

                            @Override
                            public void done(Integer integer, BmobException e) {

                            }

                        });
                progress.beginRun();
            }
        });

        progress.setCallback(new DownTimerProgress.Callback() {
            @Override
            public void onFinished() {
                reGetCodeButton.setEnabled(true);
            }
        });

        phoneLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtil.isPhone(s.toString())) {
                    getCodeButton.setEnabled(true);
                    reGetCodeButton.setEnabled(true);
                    if (codeLayout.getEditText().getText().length() == 6) {
                        okButton.setEnabled(true);
                    }
                } else {
                    getCodeButton.setEnabled(false);
                    reGetCodeButton.setEnabled(false);
                    okButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        codeLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 6 && TextUtil.isPhone(phoneLayout.getEditText()
                        .getText().toString())) {
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
                final ProgressDialog progressDialog = DialogUtil.createProgressDialog(ModifyPhoneActivity.this);
                progressDialog.show();
                final String phone = phoneLayout.getEditText().getText().toString();
                String code = codeLayout.getEditText().getText().toString();
                BmobSMS.verifySmsCode(ModifyPhoneActivity.this, phone, code,
                        new VerifySMSCodeListener() {

                            @Override
                            public void done(BmobException ex) {

                                if (ex == null) {//短信验证码已验证成功
                                    UserManager.getInstance(ModifyPhoneActivity.this).setPhone(phone,
                                            new UserManager.OnUpdateListener() {
                                                @Override
                                                public void onSuccess() {
                                                    EventBus.getDefault().post(new PhoneEvent());
                                                    dialog.dismiss();
                                                    progressDialog.dismiss();
                                                    finish();
                                                    overridePendingTransition(0, R.anim.out_from_right);
                                                }

                                                @Override
                                                public void onError() {
                                                    progressDialog.dismiss();
                                                }
                                            });
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(ModifyPhoneActivity.this, "验证码错误",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}
