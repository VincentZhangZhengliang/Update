package com.vincent.update;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

public class OpenAppReceiver extends BroadcastReceiver {
    String packname = "com.vincent.update";
    private static final String TAG = "OpenAppReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "onReceive: +++++++++++++");
        if (intent.getAction()
                .equals("android.intent.action.PACKAGE_REMOVED")) {
            try {
                if (intent.getDataString()
                        .contains(packname)) {
                    Intent myIntent = new Intent();
                    PackageManager pm = context.getPackageManager();
                    try {
                        myIntent = pm.getLaunchIntentForPackage(intent.getDataString()
                                .substring(8));
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(myIntent);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
