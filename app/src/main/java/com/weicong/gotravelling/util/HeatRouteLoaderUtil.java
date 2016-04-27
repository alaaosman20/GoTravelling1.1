package com.weicong.gotravelling.util;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Base64;

import com.weicong.gotravelling.MyApplication;
import com.weicong.gotravelling.model.Route;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 * Created by alley_qiu on 2016/4/26.
 */
public class HeatRouteLoaderUtil {

    /**
     * 保存热门路线到本地
     * @param routes
     */
    public static void savedHeatRoute(List<Route> routes) {
        String listString = list2String(routes);
        /*
        将转化成字符的list写入Preferences
         */
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MyApplication.getInstance()).edit();
        editor.putString("heat_routes", listString);
        editor.commit();
    }

    /**
     *
     * @return
     */
    public static List loadHeatRoute() {
        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(MyApplication.getInstance());
        String routeBase64String = spf.getString("heat_routes", "");
        List list = string2List(routeBase64String);
        return list;
    }
    /**
     *  将list转化为Base64String
     * @param list
     * @return
     */
    public static String list2String(List list) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = null;
        try {
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            objectOutputStream.writeObject(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String listString = new String(Base64.encode(byteArrayOutputStream.toByteArray(), Base64.DEFAULT));
        try {
            objectOutputStream.close();
            byteArrayOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listString;
    }

    /**
     * 将SharePreference存的String转为List
     */
    public static List string2List(String string) {
        byte[] bytes = Base64.decode(string.getBytes(), Base64.DEFAULT);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        ObjectInputStream objectInputStream = null;
        List list = null;
        try {
            objectInputStream = new ObjectInputStream(byteArrayInputStream);
            list = (List)objectInputStream.readObject();
            objectInputStream.close();
            byteArrayInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
