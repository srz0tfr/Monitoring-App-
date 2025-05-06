package com.example.mobilemonitoringapp.backend;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

public class AppInstallReceiver extends BroadcastReceiver {
    private static final String TAG = "AppInstallReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_PACKAGE_ADDED.equals(intent.getAction())) {
            Uri data = intent.getData();
            if (data != null) {
                String packageName = data.getSchemeSpecificPart();
                Log.d(TAG, "App Installed: " + packageName);

                
                Intent serviceIntent = new Intent(context, issue1_helper.class);
                serviceIntent.putExtra("packageName", packageName);
                context.startForegroundService(serviceIntent);
            }
        }
    }
}