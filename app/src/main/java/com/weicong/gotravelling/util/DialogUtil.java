package com.weicong.gotravelling.util;

import android.app.ProgressDialog;
import android.content.Context;

public class DialogUtil {

    /**
     * 创建加载对话框
     *
     * @param context 上下文
     * @return 加载对话框
     */
    public static ProgressDialog createProgressDialog(Context context) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        return progressDialog;
    }
}
