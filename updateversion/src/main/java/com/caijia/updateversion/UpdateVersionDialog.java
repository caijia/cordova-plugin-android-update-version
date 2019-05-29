package com.caijia.updateversion;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;

import com.caijia.download.CallbackInfo;
import com.caijia.download.DownloadListener;
import com.caijia.download.FileDownloader;
import com.caijia.download.FileRequest;

import java.io.File;

public class UpdateVersionDialog extends AlertDialog {

    private static final String APK_DIR = "apkDir";
    private WaterProgress waterProgress;
    private String downloadUrl;
    private int previousPercent;
    private FileDownloader fileDownloader;

    public UpdateVersionDialog(Context context, String downloadUrl) {
        super(context, R.style.Dialog_update_version);
        this.downloadUrl = downloadUrl;
    }

    public static void install(Context context, String apkPath) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(
                getFilePathUri(context, apkPath), "application/vnd.android.package-archive");
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        context.startActivity(intent);
    }

    public static Uri getFilePathUri(Context context, String saveFilePath) {
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri =
                    FileProvider.getUriForFile(
                            context,
                            context.getPackageName() + ".provider",
                            new File(saveFilePath)
                    );
        } else {
            uri = Uri.fromFile(new File(saveFilePath));
        }
        return uri;
    }

    public static File getDiskCacheDir(Context context, String dir) {
        // Check if media is mounted or storage is built-in, if so, try and use external cache dir
        // otherwise use internal cache dir
        final String cachePath =
                Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ||
                        !Environment.isExternalStorageRemovable() ? getExternalCacheDir(context)
                        .getPath() :
                        context.getCacheDir().getPath();

        File dirFile = new File(cachePath + File.separator + dir);
        dirFile.mkdirs();
        return dirFile;
    }

    @TargetApi(Build.VERSION_CODES.FROYO)
    public static File getExternalCacheDir(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            return context.getExternalCacheDir();
        }

        // Before Froyo we need to construct the external cache dir ourselves
        final String cacheDir = "/Android/data/" + context.getPackageName() + "/cache/";
        return new File(Environment.getExternalStorageDirectory().getPath() + cacheDir);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_dialog_update_version);
        waterProgress = findViewById(R.id.water_progress);
        init();
    }

    public void init() {
        setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (fileDownloader != null) {
                    fileDownloader.pause();
                }
            }
        });
        FileRequest fileRequest = new FileRequest.Builder().url(downloadUrl).build();

        String savePath = getDiskCacheDir(getContext(), APK_DIR).getAbsolutePath();
        fileDownloader =
                new FileDownloader.Builder().saveFileDirPath(savePath).fileRequest(fileRequest)
                        .build();

        fileDownloader.download(
                new DownloadListener() {

                    @Override
                    public void onStart(CallbackInfo callbackInfo) {
                    }

                    @Override
                    public void onPrepared(CallbackInfo callbackInfo) {
                    }

                    @Override
                    public void onDownloading(CallbackInfo callbackInfo) {
                        long fileSize = callbackInfo.getFileSize();
                        long downloadSize = callbackInfo.getDownloadSize();
                        int percent = (int) ((downloadSize * 100f) / fileSize);
                        if (percent - previousPercent >= 1) {
                            waterProgress.setProgress(percent);
                        }
                        previousPercent = percent;
                    }

                    @Override
                    public void onComplete(CallbackInfo callbackInfo) {
                        dismiss();
                        install(getContext(), callbackInfo.getSavePath());
                    }

                    @Override
                    public void onPausing(CallbackInfo callbackInfo) {
                    }

                    @Override
                    public void onPause(CallbackInfo callbackInfo) {
                    }
                });
    }
}
