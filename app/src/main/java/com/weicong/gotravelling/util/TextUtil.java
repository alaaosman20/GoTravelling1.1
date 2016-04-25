package com.weicong.gotravelling.util;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.view.inputmethod.InputMethodManager;
import android.app.Activity;
import android.content.Context;
import android.view.WindowManager;

public class TextUtil {

    /**
     * 判断字符串是否为空
     *
     * @param s 字符串
     * @return 字符串空返回true，否则返回false
     */
    public static boolean isEmpty(String s) {
        return TextUtils.isEmpty(s);
    }

    /**
     * 判断是否为邮箱
     *
     * @param s 字符串
     * @return 字符串是邮箱返回true，否则返回false
     */
    public static boolean isEmail(String s) {
        Pattern p = Pattern.compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|" +
                "(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");
        Matcher m = p.matcher(s);
        return m.matches();
    }

    /**
     * 判断是否为电话号码
     *
     * @param s 字符串
     * @return 字符串是电话号码返回true，否则返回false
     */
    public static boolean isPhone(String s) {
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(s);
        return m.matches();
    }

    /**
     * 关闭软键盘
     *
     * @param activity Activity
     */
    public static void closeKeybord(Activity activity) {
        InputMethodManager manager = ((InputMethodManager) activity
                .getSystemService(Context.INPUT_METHOD_SERVICE));
        if (activity.getWindow().getAttributes().softInputMode !=
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (activity.getCurrentFocus() != null)
                manager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
