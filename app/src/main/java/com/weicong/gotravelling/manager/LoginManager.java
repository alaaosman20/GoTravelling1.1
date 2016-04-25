package com.weicong.gotravelling.manager;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.weicong.gotravelling.R;
import com.weicong.gotravelling.model.User;
import com.weicong.gotravelling.util.DialogUtil;
import com.weicong.gotravelling.util.TextUtil;
import com.weicong.gotravelling.view.DownTimerProgress;
import com.weicong.gotravelling.view.FlatButton;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.RequestSMSCodeListener;
import cn.bmob.v3.listener.ResetPasswordByCodeListener;
import cn.bmob.v3.listener.ResetPasswordByEmailListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * Dialog管理类，单例模式
 */
public class LoginManager {

    private static LoginManager sLoginManager;

    private Context mContext;

    private User mUser;

    private LoginListener mLoginListener;

    /**
     * 设置监听器
     *
     * @param listener LoginListener
     */
    public void setLoginListener(LoginListener listener) {
        mLoginListener = listener;
    }

    private LoginManager(Context context) {
        mContext = context;
    }

    /**
     * 获取DialogManager唯一实例
     *
     * @param context 上下文
     * @return DialogManager实例
     */
    public static LoginManager getInstance(Context context) {
        if (sLoginManager == null) {
            sLoginManager = new LoginManager(context);
        }
        return sLoginManager;
    }

    /**
     * 创建登录对话框
     *
     * @return 登录对话框
     */
    public AlertDialog createLoginDialog() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.dialog_login, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.dialog_login_or_register)
                .setView(v);
        final AlertDialog alertDialog = builder.create();

        final FlatButton loginButton = (FlatButton) v.findViewById(R.id.btn_login);
        loginButton.setEnabled(false);

        final TextInputLayout phoneLayout = (TextInputLayout) v.findViewById(R.id.layout_phone);
        final TextInputLayout passwordLayout = (TextInputLayout) v.findViewById(R.id.layout_password);

        phoneLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0 && !TextUtil.isEmpty(
                        passwordLayout.getEditText().getText().toString())
                        && (TextUtil.isEmail(s.toString()) || TextUtil.isPhone(s.toString()))) {
                    loginButton.setEnabled(true);
                } else {
                    loginButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        passwordLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                passwordLayout.setErrorEnabled(false);
                String text = phoneLayout.getEditText().getText().toString();
                if (s.length() > 0 && !TextUtil.isEmpty(text)
                        && (TextUtil.isEmail(text) || TextUtil.isPhone(text))) {
                    loginButton.setEnabled(true);
                } else {
                    loginButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        View helpView = v.findViewById(R.id.iv_help);
        helpView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                createHelpLoginDialog().show();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = phoneLayout.getEditText().getText().toString();
                String password = passwordLayout.getEditText().getText().toString();

                if (password.length() < 6) {
                    passwordLayout.setErrorEnabled(true);
                    passwordLayout.setError("少于6位");
                } else {
                    login(alertDialog, phone, password);
                }

            }
        });

        View registerView = v.findViewById(R.id.btn_register);
        registerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = createRegisterDialog();
                alertDialog.dismiss();
                dialog.show();
            }
        });
        return alertDialog;
    }

    /**
     * 创建注册对话框
     *
     * @return 注册对话框
     */
    public AlertDialog createRegisterDialog() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.dialog_register, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.register)
                .setView(v);
        final AlertDialog alertDialog = builder.create();

        final TextInputLayout phoneLayout = (TextInputLayout) v.findViewById(R.id.layout_phone);
        final TextInputLayout passwordLayout = (TextInputLayout) v.findViewById(R.id.layout_password);
        final TextInputLayout usernameLayout = (TextInputLayout) v.findViewById(R.id.layout_username);

        final FlatButton nextButton = (FlatButton) v.findViewById(R.id.btn_next);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = phoneLayout.getEditText().getText().toString();
                String password = passwordLayout.getEditText().getText().toString();
                String username = usernameLayout.getEditText().getText().toString();

                if (password.length() < 6) {
                    passwordLayout.setErrorEnabled(true);
                    passwordLayout.setError("少于6位");
                } else {
                    if (TextUtil.isEmail(phone)) {
                        register2(alertDialog, phone, password, username);
                    } else {
                        register(phone, password, username);
                        alertDialog.dismiss();
                        AlertDialog nextDialog = createRegisterNextDialog();
                        nextDialog.show();
                    }
                }
            }
        });
        nextButton.setEnabled(false);

        phoneLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0
                        && !TextUtil.isEmpty(passwordLayout.getEditText().getText().toString())
                        && !TextUtil.isEmpty(usernameLayout.getEditText().getText().toString())) {
                    if (TextUtil.isPhone(s.toString())) {
                        nextButton.setText(R.string.next);
                        nextButton.setEnabled(true);
                    } else if (TextUtil.isEmail(s.toString())) {
                        nextButton.setText(R.string.finish_register);
                        nextButton.setEnabled(true);
                    } else {
                        nextButton.setEnabled(false);
                    }
                } else {
                    nextButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        passwordLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                passwordLayout.setErrorEnabled(false);
                if (s.length() > 0
                        && !TextUtil.isEmpty(phoneLayout.getEditText().getText().toString())
                        && !TextUtil.isEmpty(usernameLayout.getEditText().getText().toString())) {
                    String text = phoneLayout.getEditText().getText().toString();
                    if (TextUtil.isEmail(text)) {
                        nextButton.setText(R.string.finish_register);
                        nextButton.setEnabled(true);
                    } else if (TextUtil.isPhone(text)) {
                        nextButton.setText(R.string.next);
                        nextButton.setEnabled(true);
                    } else {
                        nextButton.setEnabled(false);
                    }
                } else {
                    nextButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        usernameLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0
                        && !TextUtil.isEmpty(phoneLayout.getEditText().getText().toString())
                        && !TextUtil.isEmpty(passwordLayout.getEditText().getText().toString())) {
                    String text = phoneLayout.getEditText().getText().toString();
                    if (TextUtil.isEmail(text)) {
                        nextButton.setText(R.string.finish_register);
                        nextButton.setEnabled(true);
                    } else if (TextUtil.isPhone(text)) {
                        nextButton.setText(R.string.next);
                        nextButton.setEnabled(true);
                    } else {
                        nextButton.setEnabled(false);
                    }
                } else {
                    nextButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return alertDialog;
    }

    /**
     * 创建下一步注册对话框
     *
     * @return AlertDialog
     */
    private AlertDialog createRegisterNextDialog() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.dialog_register_next, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.register)
                .setView(v);

        final AlertDialog alertDialog = builder.create();

        final FlatButton reGetCodeButton = (FlatButton) v.findViewById(R.id.btn_re_get_code);
        reGetCodeButton.setEnabled(false);

        final DownTimerProgress progress = (DownTimerProgress) v.findViewById(R.id.progress);
        progress.beginRun();
        progress.setCallback(new DownTimerProgress.Callback() {
            @Override
            public void onFinished() {
                reGetCodeButton.setEnabled(true);
            }
        });

        reGetCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress.reset();
                progress.beginRun();
                reGetCodeButton.setEnabled(false);
                BmobSMS.requestSMSCode(mContext, mUser.getMobilePhoneNumber(),
                        "注册模板", new RequestSMSCodeListener() {

                            @Override
                            public void done(Integer smsId, BmobException ex) {

                                if (ex == null) { //验证码发送成功

                                }
                            }
                        });

            }
        });

        final FlatButton finishButton = (FlatButton) v.findViewById(R.id.btn_finish);
        finishButton.setEnabled(false);


        final TextInputLayout codeLayout = (TextInputLayout) v.findViewById(R.id.layout_code);
        codeLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 6) {
                    finishButton.setEnabled(true);
                } else {
                    finishButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = DialogUtil.createProgressDialog(mContext);
                progressDialog.show();
                String code = codeLayout.getEditText().getText().toString();
                mUser.signOrLogin(mContext, code, new SaveListener() {
                    @Override
                    public void onSuccess() {
                        progressDialog.dismiss();
                        loginFinish();
                        alertDialog.dismiss();
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        progressDialog.dismiss();
                        if (i == 202) {
                            Toast.makeText(mContext, "用户名已被注册", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        return alertDialog;
    }

    /**
     * 创建无法登录对话框
     *
     * @return 无法登录对话框
     */
    private AlertDialog createHelpLoginDialog() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.dialog_help_login, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.can_not_login)
                .setView(v);

        final AlertDialog alertDialog = builder.create();

        View resetPassword = v.findViewById(R.id.btn_reset_password);
        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                createResetPasswordDialog().show();
            }
        });

        View usePhoneCode = v.findViewById(R.id.btn_use_phone_code);
        usePhoneCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                createUsePhoneLoginDialog().show();
            }
        });
        return alertDialog;
    }

    /**
     * 创建使用手机验证码登录对话框
     *
     * @return 手机验证码登录对话框
     */
    private AlertDialog createUsePhoneLoginDialog() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.dialog_use_phone_login, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.login)
                .setView(v);

        final AlertDialog alertDialog = builder.create();

        final TextInputLayout phoneLayout = (TextInputLayout) v.findViewById(R.id.layout_phone);
        final TextInputLayout codeLayout = (TextInputLayout) v.findViewById(R.id.layout_code);
        final FlatButton getCodeButton = (FlatButton) v.findViewById(R.id.btn_get_code);
        final FlatButton loginButton = (FlatButton) v.findViewById(R.id.btn_login);
        final FlatButton reGetCodeButton = (FlatButton) v.findViewById(R.id.btn_re_get_code);
        final DownTimerProgress progress = (DownTimerProgress) v.findViewById(R.id.progress);

        getCodeButton.setEnabled(false);
        loginButton.setEnabled(false);
        reGetCodeButton.setEnabled(false);
        reGetCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress.reset();
                progress.beginRun();
                reGetCodeButton.setEnabled(false);
                String phone = phoneLayout.getEditText().getText().toString();
                BmobSMS.requestSMSCode(mContext, phone,
                        "登录模板", new RequestSMSCodeListener() {

                            @Override
                            public void done(Integer smsId, BmobException ex) {

                                if (ex == null) { //验证码发送成功

                                }
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
                BmobSMS.requestSMSCode(mContext, phone, "登录模板",
                        new RequestSMSCodeListener() {

                            @Override
                            public void done(Integer integer, BmobException e) {
                                if (e == null) {//验证码发送成功

                                }
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
                        loginButton.setEnabled(true);
                    }
                } else {
                    getCodeButton.setEnabled(false);
                    reGetCodeButton.setEnabled(false);
                    loginButton.setEnabled(false);
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
                    loginButton.setEnabled(true);
                } else {
                    loginButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = DialogUtil.createProgressDialog(mContext);
                progressDialog.show();
                String phone = phoneLayout.getEditText().getText().toString();
                String code = codeLayout.getEditText().getText().toString();
                BmobUser.loginBySMSCode(mContext, phone, code,
                        new LogInListener<User>() {

                            @Override
                            public void done(User user, BmobException e) {
                                progressDialog.dismiss();
                                if (user != null) {
                                    alertDialog.dismiss();
                                    loginFinish();
                                } else {
                                    Toast.makeText(mContext, "验证码错误",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        return alertDialog;
    }

    /**
     * 创建重置密码对话框
     *
     * @return 重置密码对话框
     */
    private AlertDialog createResetPasswordDialog() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.dialog_reset_password, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.reset_password)
                .setView(v);

        final AlertDialog alertDialog = builder.create();

        View emailButton = v.findViewById(R.id.btn_email);
        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                createResetByEmailDialog().show();
            }
        });

        View phoneButton = v.findViewById(R.id.btn_phone);
        phoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                createResetByPhoneDialog().show();
            }
        });

        return alertDialog;
    }

    /**
     * 创建通过邮箱进行密码重置的对话框
     *
     * @return 对话框
     */
    private AlertDialog createResetByEmailDialog() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.dialog_reset_by_email, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.reset_password)
                .setView(v);

        final AlertDialog alertDialog = builder.create();

        final TextInputLayout emailLayout = (TextInputLayout) v.findViewById(R.id.layout_email);
        final FlatButton okButton = (FlatButton) v.findViewById(R.id.btn_ok);
        okButton.setEnabled(false);

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
                final ProgressDialog progressDialog = DialogUtil.createProgressDialog(mContext);
                progressDialog.show();
                String email = emailLayout.getEditText().getText().toString();
                BmobUser.resetPasswordByEmail(mContext, email,
                        new ResetPasswordByEmailListener() {
                            @Override
                            public void onSuccess() {
                                progressDialog.dismiss();
                                alertDialog.dismiss();
                                Toast.makeText(mContext, "请求成功，请到邮箱进行密码重置操作", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(int code, String e) {
                                progressDialog.dismiss();
                                Toast.makeText(mContext, "重置密码失败:" + e, Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        return alertDialog;
    }

    /**
     * 创建通过手机号码进行密码重置的对话框
     *
     * @return 对话框
     */
    private AlertDialog createResetByPhoneDialog() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.dialog_reset_by_phone, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.reset_password)
                .setView(v);

        final AlertDialog alertDialog = builder.create();

        final TextInputLayout phoneLayout = (TextInputLayout) v.findViewById(R.id.layout_phone);
        final TextInputLayout codeLayout = (TextInputLayout) v.findViewById(R.id.layout_code);
        final TextInputLayout passwordLayout = (TextInputLayout) v.findViewById(R.id.layout_password);

        final FlatButton getCodeButton = (FlatButton) v.findViewById(R.id.btn_get_code);
        final FlatButton reGetCodeButton = (FlatButton) v.findViewById(R.id.btn_re_get_code);
        final FlatButton okButton = (FlatButton) v.findViewById(R.id.btn_ok);
        getCodeButton.setEnabled(false);
        reGetCodeButton.setEnabled(false);
        okButton.setEnabled(false);

        final DownTimerProgress progress = (DownTimerProgress) v.findViewById(R.id.progress);

        reGetCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress.reset();
                progress.beginRun();
                reGetCodeButton.setEnabled(false);
                String phone = phoneLayout.getEditText().getText().toString();
                BmobSMS.requestSMSCode(mContext, phone, "密码重置模板",
                        new RequestSMSCodeListener() {

                            @Override
                            public void done(Integer smsId, BmobException ex) {

                                if (ex == null) { //验证码发送成功

                                }
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
                BmobSMS.requestSMSCode(mContext, phone, "密码重置模板",
                        new RequestSMSCodeListener() {

                            @Override
                            public void done(Integer integer, BmobException e) {
                                if (e == null) {//验证码发送成功

                                }
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
                    if (codeLayout.getEditText().getText().length() == 6 &&
                            !TextUtil.isEmpty(passwordLayout.getEditText().getText().toString())) {
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
                        .getText().toString()) &&
                        !TextUtil.isEmpty(passwordLayout.getEditText().getText().toString())) {
                    okButton.setEnabled(true);
                } else {
                    okButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        passwordLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                passwordLayout.setErrorEnabled(false);
                String code = codeLayout.getEditText().getText().toString();
                String phone = phoneLayout.getEditText().getText().toString();
                if (!TextUtil.isEmpty(s.toString())
                        && code.length() == 6
                        && TextUtil.isPhone(phone)) {
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
                String code = codeLayout.getEditText().getText().toString();
                String password = passwordLayout.getEditText().getText().toString();

                if (password.length() < 6) {
                    passwordLayout.setErrorEnabled(true);
                    passwordLayout.setError("少于6位");
                } else {
                    final ProgressDialog progressDialog = DialogUtil.createProgressDialog(mContext);
                    progressDialog.show();
                    BmobUser.resetPasswordBySMSCode(mContext, code, password, new ResetPasswordByCodeListener() {

                        @Override
                        public void done(BmobException ex) {
                            progressDialog.dismiss();
                            alertDialog.dismiss();
                            if(ex == null){
                                Toast.makeText(mContext, "密码重置成功", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(mContext, "重置失败：" + ex.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
        return alertDialog;
    }

    /**
     * 手机号码注册
     *
     * @param phone    电话号码
     * @param password 密码
     * @param username 用户名
     */
    private void register(String phone, String password, String username) {
        mUser = new User();
        mUser.setUsername(username);
        mUser.setPassword(password);
        mUser.setMobilePhoneNumber(phone);
        BmobSMS.requestSMSCode(mContext, phone, "注册模板", new RequestSMSCodeListener() {

            @Override
            public void done(Integer smsId, BmobException ex) {

                if (ex == null) { //验证码发送成功

                }
            }
        });
    }

    /**
     * 邮箱注册
     *
     * @param dialog   对话框
     * @param email    邮箱
     * @param password 密码
     * @param username 用户名
     */
    private void register2(final AlertDialog dialog, String email, String password, String username) {
        final ProgressDialog progressDialog = DialogUtil.createProgressDialog(mContext);
        progressDialog.show();
        mUser = new User();
        mUser.setUsername(username);
        mUser.setPassword(password);
        mUser.setEmail(email);
        mUser.signUp(mContext, new SaveListener() {
            @Override
            public void onSuccess() {
                progressDialog.dismiss();
                loginFinish();
                dialog.dismiss();
            }

            @Override
            public void onFailure(int i, String s) {
                progressDialog.dismiss();
                if (i == 202) { //用户名已被注册
                    Toast.makeText(mContext, "用户名已被注册 ", Toast.LENGTH_SHORT).show();
                } else if (i == 203) { //邮箱已被注册
                    Toast.makeText(mContext, "邮箱已被注册", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 手机号码登录
     *
     * @param phone    手机号码
     * @param password 密码
     */
    private void login(final AlertDialog dialog, String phone, String password) {
        final ProgressDialog progressDialog = DialogUtil.createProgressDialog(mContext);
        progressDialog.show();
        BmobUser.loginByAccount(mContext, phone, password, new LogInListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                progressDialog.dismiss();
                if (user != null) {
                    loginFinish();
                    dialog.dismiss();
                } else {
                    Toast.makeText(mContext, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 登录成功
     */
    private void loginFinish() {
        UserManager.LOGIN = true;
        if (mLoginListener != null) {
            mLoginListener.onFinish();
        }
    }

    public interface LoginListener {
        void onFinish();
    }

    /**
     * 清除
     */
    public static void clear() {
        sLoginManager = null;
    }
}
