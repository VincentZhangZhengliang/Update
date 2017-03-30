package com.vincent.update;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.cundong.utils.PatchUtils;

import java.io.File;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String PATH = Environment.getExternalStorageDirectory() + File.separator;
    private static final String TAG = "MainActivity";
    public static final String APP_DEBUG_PATCH = "app-release.patch";
    public static final String APP_DEBUG_NEW_APK = "app-release-new.apk";


    //加载增量更新的代码
    static {
        System.loadLibrary("ApkPatchLibrary");
    }

    private Button mUpdate;
    private int mVersionCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUpdate = (Button) findViewById(R.id.id_btn);
        mUpdate.setOnClickListener(this);

        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            mVersionCode = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_btn:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);

                } else {
                    update2();
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 0:
                if (grantResults.length > 0 && grantResults[0]
                        == PackageManager.PERMISSION_GRANTED) {
                    update2();
                }
                break;
        }


    }

    private void update2() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("增量更新提示");
        builder.setMessage("是否更新使用bsdiff产生的补丁");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String url = "http://192.168.1.103:8080/safeguard/app-release.patch";
                downloadApk(MainActivity.this, url, "下载中。。。", "我的应用");
            }
        });
        builder.setNegativeButton("暂不更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }


    private void update() {
        File file = new File(Environment.getExternalStorageDirectory(), APP_DEBUG_PATCH);
        if (file.exists()) {
            final String oldApkPath =
                    ApkUtils.getSourceApkPath(MainActivity.this, "com.vincent.update");

            final String newApkPath = PATH + APP_DEBUG_NEW_APK;

            final String patchPath = PATH + APP_DEBUG_PATCH;

            Log.i(TAG, "oldApkPath:" + oldApkPath);
            Log.i(TAG, "newApkPath:" + newApkPath);
            Log.i(TAG, "patchPath:" + patchPath);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("增量更新提示");
            builder.setMessage("是否更新使用bsdiff产生的补丁");
            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            // String oldApkPath, String newApkPath, String patchPath
                            try {

                                final int result = PatchUtils.patch(oldApkPath, newApkPath, patchPath);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (result == 0) {
                                            Toast.makeText(getApplicationContext(), "合并apk成功,请安装", Toast.LENGTH_SHORT)
                                                    .show();
                                            ApkUtils.installApk(MainActivity.this, newApkPath);
                                        } else {
                                            Toast.makeText(getApplicationContext(), "合并apk失败", Toast.LENGTH_SHORT)
                                                    .show();
                                        }
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            });
            builder.setNegativeButton("暂不更新", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
        }
    }


    public void downloadApk(Context context, String downLoadUrl,
                            String description, String infoName) {

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downLoadUrl));

        request.setTitle(infoName);
        request.setDescription(description);

        //在通知栏显示下载进度
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        //设置保存下载apk保存路径     默认位置: /storage/sdcard0/Download
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,"app-release.patch");

        Context appContext = context.getApplicationContext();
        DownloadManager manager = (DownloadManager)
                appContext.getSystemService(Context.DOWNLOAD_SERVICE);
        //进入下载队列
        manager.enqueue(request);
    }
}
