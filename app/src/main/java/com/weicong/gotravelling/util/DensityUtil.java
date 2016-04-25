package com.weicong.gotravelling.util;

import android.content.Context;
import android.util.TypedValue;

/**
 * 常用单位转换的辅助类
 */
public class DensityUtil {

	/** 
     * dp转px 
     *  
     * @param context 上下文
     * @param dpVal dp值
     * @return px值
     */  
    public static float dp2px(Context context, float dpVal) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
    }
    
    /** 
     * sp转px 
     *  
     * @param context 上下文
     * @param spVal sp值
     * @return px值
     */  
    public static float sp2px(Context context, float spVal) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal, context.getResources().getDisplayMetrics());  
    }
    
    /** 
     * px转dp 
     *  
     * @param context 上下文
     * @param pxVal px值
     * @return dp值
     */  
    public static float px2dp(Context context, float pxVal) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (pxVal / scale);  
    }
    
    /** 
     * px转sp 
     *  
     * @param context 上下文
     * @param pxVal px值
     * @return sp值
     */  
    public static float px2sp(Context context, float pxVal) {  
        return (pxVal / context.getResources().getDisplayMetrics().scaledDensity);  
    }
}
