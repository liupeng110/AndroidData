package com.example.processservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by LiuLei on 2017/6/15.
 */

public class ServiceRestartBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "ServiceRestartBroadcast";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: BroadcastReceiver has stopped,and BroadcastReceiver has recreate Service");
        Intent i = new Intent(context,RestartService.class);
        context.startService(i);
    }
}
