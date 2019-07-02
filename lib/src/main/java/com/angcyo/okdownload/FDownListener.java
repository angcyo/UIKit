package com.angcyo.okdownload;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import com.liulishuo.okdownload.DownloadTask;

import java.util.UUID;

/**
 * 监听事件, 主线程回调
 */
public class FDownListener {

    String uuid;
    String url;

    /**
     * 当任务end之后, 移除listener
     * 暂停/取消/完成 都会触发end
     */
    boolean removeOnTaskEnd;

    public FDownListener() {
        this(true);
    }

    public FDownListener(boolean removeOnTaskEnd) {
        this(null, removeOnTaskEnd);
    }

    public FDownListener(String url, boolean removeOnTaskEnd) {
        uuid = UUID.randomUUID().toString();
        this.removeOnTaskEnd = removeOnTaskEnd;
        this.url = url;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isRemoveOnTaskEnd() {
        return removeOnTaskEnd;
    }

    public void setRemoveOnTaskEnd(boolean removeOnTaskEnd) {
        this.removeOnTaskEnd = removeOnTaskEnd;
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