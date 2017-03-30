package com.vincent.update;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.cundong.utils.PatchUtils;

import java.io.File;

import static com.vincent.update.MainActivity.APP_DEBUG_NEW_APK;
import static com.vincent.update.MainActivity.APP_DEBUG_PATCH;
import static com.vincent.update.MainActivity.PATH;

public class DownloadReceiver extends BroadcastReceiver {

    private static final String TAG = "DownloadReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "onReceive: download finish");
        installApk(context);
    }

    private void installApk(Context context) {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), APP_DEBUG_PATCH);
        if (file.exists()) {
            final String oldApkPath =
                    ApkUtils.getSourceApkPath(context, "com.vincent.update");

            final String newApkPath = PATH + APP_DEBUG_NEW_APK;
//            final String patchPath = PATH + APP_DEBUG_PATCH;
            final String patchPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + APP_DEBUG_PATCH;

            Log.i(TAG, "oldApkPath:" + oldApkPath);
            Log.i(TAG, "newApkPath:" + newApkPath);
            Log.i(TAG, "patchPath:" + patchPath);

            final int result = PatchUtils.patch(oldApkPath, newApkPath, patchPath);

            if (result == 0) {
                Toast.makeText(context, "合并apk成功,请安装", Toast.LENGTH_SHORT)
                        .show();
                ApkUtils.installApk(context, newApkPath);
            } else {
                Toast.makeText(context, "合并apk失败", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }
}
