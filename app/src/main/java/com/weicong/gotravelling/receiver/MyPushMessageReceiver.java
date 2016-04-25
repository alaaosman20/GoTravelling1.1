package com.weicong.gotravelling.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;

import com.weicong.gotravelling.MyApplication;
import com.weicong.gotravelling.model.PushMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.bmob.push.PushConstants;

public class MyPushMessageReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean receive = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean("push_message", true);
        if(intent.getAction().equals(PushConstants.ACTION_MESSAGE) && receive){
            String s = intent.getStringExtra(PushConstants.EXTRA_PUSH_MESSAGE_STRING);
            try {
                JSONObject object = new JSONObject(s);
                PushMessage pushMessage = new PushMessage();
                String content = object.getString("alert");
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                pushMessage.setDate(format.format(new Date()));
                pushMessage.setContent(content);
                MyApplication.getInstance().addPushMessage(pushMessage);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
