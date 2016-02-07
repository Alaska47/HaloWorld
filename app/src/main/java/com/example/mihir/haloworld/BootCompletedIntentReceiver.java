package com.example.mihir.haloworld;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootCompletedIntentReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction()) && RegistrationActivity.angel) {
            Intent pushIntent = new Intent(context, MyService.class);
            context.startService(pushIntent);
        }
    }
}