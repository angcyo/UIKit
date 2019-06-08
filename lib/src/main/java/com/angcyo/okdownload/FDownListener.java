package com.angcyo.okdownload;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.liulishuo.okdownload.DownloadTask;

import java.util.UUID;

/**
 * 监听事件, 主线程回调
 */
public class FDownListener {

    String uuid;

    public FDownListener() {
        uuid = UUID.randomUUID().toString();
    }

    @Override
    public boolean equals(Object obj) {
        return TextUtils.equals(uuid, ((FDownListener) obj).uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    public void onTaskStart(@NonNull DownloadTask task) {

    }

    public void onTaskProgress(@NonNull DownloadTask task,
                               long totalLength /*总大小*/,
                               long totalOffset /*当前下载量*/,
                               int percent /*百分比*/,
                               long increaseBytes /*本次下载量, 速度的意思*/) {

    }

    public void onTaskEnd(@NonNull DownloadTask task,
                          boolean isCompleted /*是否下载完成*/,
                          @Nullable Exception realCause /*失败才有值*/) {

    }
}