package com.darkguyy.chargealert;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootCompletedReceiver extends BroadcastReceiver {
    private static final String TAG = BootCompletedReceiver.class.getName();

    public BootCompletedReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()) ||
                Intent.ACTION_MY_PACKAGE_REPLACED.equals(intent.getAction())) {
            MainService.startIfEnabled(context);
        }
       // Toast.makeText(context,"Hello world!!",Toast.LENGTH_LONG).show();


    }
}
