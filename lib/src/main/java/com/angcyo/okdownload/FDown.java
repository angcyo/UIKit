package com.angcyo.okdownload;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.angcyo.lib.L;
import com.liulishuo.okdownload.*;
import com.liulishuo.okdownload.StatusUtil.Status;
import com.liulishuo.okdownload.core.Util;
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo;
import com.liulishuo.okdownload.core.cause.EndCause;
import com.liulishuo.okdownload.core.cause.ResumeFailedCause;
import com.liulishuo.okdownload.core.dispatcher.DownloadDispatcher;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * https://github.com/lingochamp/okdownload 1.0.5 2018-11-27
 * <p>
 * Email:angcyo@126.com
 *
 * @author angcyo
 * @date 2018/11/27
 */
public class FDown {
    public static final String TAG = "FDown";

    static Context app;

    public static void init(@NonNull Context context) {
        app = context.getApplicationContext();
        try {
            Field contextField = OkDownloadProvider.class.getDeclaredField("context");
            contextField.setAccessible(true);
            Object contextObj = contextField.get(null);
            if (contextObj == null) {
                contextField.set(null, app);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        //https://github.com/lingochamp/okdownload/wiki/Advanced-Use-Guideline
        //OkDownload.with().setMonitor(monitor);

        DownloadDispatcher.setMaxParallelRunningCount(5);

        //RemitStoreOnSQLite.setRemitToDBDelayMillis(3000);

        //OkDownload.with().breakpointStore().remove(taskId);//移除断点


        //OkDownload.Builder builder = new OkDownload.Builder(context)
        //.downloadStore(downloadStore)
        //.callbackDispatcher(callbackDispatcher)
        //.downloadDispatcher(downloadDispatcher)
        //.connectionFactory(connectionFactory)
        //.outputStreamFactory(outputStreamFactory)
        //.downloadStrategy(downloadStrategy)
        //.processFileStrategy(processFileStrategy)
        //.monitor(monitor);

        //OkDownload.setSingletonInstance(builder.build());
    }

    public static DownloadTask down(String url, DownloadListener listener) {
        return down(url, new File(defaultDownloadPath(url)), listener);
    }

    public static DownloadTask down(String url, String filePath, DownloadListener listener) {
        return down(url, new File(filePath), listener);
    }

//    public static DownloadTask down(String url, File targetFile, DownloadListener listener) {
//        DownloadTask task = new DownloadTask.Builder(url, targetFile)
//                //.setFilename(filename)
//                // the minimal interval millisecond for callback progress
//                .setMinIntervalMillisCallbackProcess(1_000)//回调间隔时间
//                // do re-download even if the task has already been completed in the past.
//                .setPassIfAlreadyCompleted(false) //如果已完成, 是否跳过下载
//                //.setConnectionCount(5) //将文件分成几块下载
//                .build();
//
//        task.enqueue(listener);
//
//        // cancel
//        //task.cancel();
//
//        // execute task synchronized
//        //task.execute(listener);
//
//        return task;
//    }

    public static DownloadTask newTask(String url, File targetFile) {
        DownloadTask task = new DownloadTask.Builder(url, targetFile)
                //.setFilename(filename)  //强制文件名
                // the minimal interval millisecond for callback progress
                .setMinIntervalMillisCallbackProcess(1_000)//回调间隔时间
                // do re-download even if the task has already been completed in the past.
                .setPassIfAlreadyCompleted(true) //如果已完成, 是否跳过下载
                //.setConnectionCount(5) //将文件分成几块下载
                //.setAutoCallbackToUIThread(false) //在主线程接收事件
                .build();

        return task;
    }

    /**
     * 获取一个已经存在的任务, 如果不存在则创建新的任务
     */
    public static DownloadTask get(String url, File targetFile) {
        DownloadTask task = newTask(url, targetFile);

        DownloadTask sameTask = OkDownload.with().downloadDispatcher().findSameTask(task);
        if (sameTask == null) {
            return task;
        } else {
            return sameTask;
        }
    }

    public static DownloadTask down(String url, File targetFile, DownloadListener listener) {
        DownloadTask task = newTask(url, targetFile);

        DownloadTask sameTask = OkDownload.with().downloadDispatcher().findSameTask(task);
        if (sameTask != null) {
            DownloadListener sameTaskListener = sameTask.getListener();
            L.i("已有相同的任务在执行:" + StatusUtil.getStatus(sameTask) + " " + sameTaskListener);
            if (sameTaskListener instanceof HostUnifiedListenerManager.HostDownloadListener) {
                ((HostUnifiedListenerManager.HostDownloadListener) sameTaskListener)
                        .getUnifiedListenerManager()
                        .attachListener(sameTask, listener);
            }
            return sameTask;
        }

        // all attach or detach is based on the id of Task in fact.
        HostUnifiedListenerManager manager = new HostUnifiedListenerManager();

        //        DownloadListener listener1 = new DownloadListener1();
        //        DownloadListener listener2 = new DownloadListener2();
        //        DownloadListener listener3 = new DownloadListener3();
        //        DownloadListener listener4 = new DownloadListener4();


        //        manager.attachListener(task, listener1);
        //        manager.attachListener(task, listener2);

        //        manager.detachListener(task, listener2);

        // all listeners added for this task will be removed when task is end.
        manager.addAutoRemoveListenersWhenTaskEnd(task.getId());//任务结束后, 自动清理Listener

        // enqueue task to start.
        //manager.enqueueTaskWithUnifiedListener(task, listener3);

        //manager.attachListener(task, listener4);

        manager.attachListener(task, listener);

        task.enqueue(manager.getHostListener());

        return task;
    }

    /**
     * 取消所有下载
     */
    public static void cancelAll() {
        OkDownload.with().downloadDispatcher().cancelAll();
    }

    public static void cancel(int id) {
        OkDownload.with().downloadDispatcher().cancel(id);
    }

    /**
     * 获取任务状态
     */
    public static Status getStatus(DownloadTask task) {
        Status status = StatusUtil.getStatus(task);
        return status;
    }

    public static Status getStatus(String url, String parentPath, String filename) {
        Status status = StatusUtil.getStatus(url, parentPath, filename);
        return status;
    }

    public static boolean isCompleted(DownloadTask task) {
        boolean isCompleted = StatusUtil.isCompleted(task);
        return isCompleted;
    }


    public static Status isCompletedOrUnknown(DownloadTask task) {
        Status status = StatusUtil.isCompletedOrUnknown(task);
        return status;
    }

    public static boolean isCompleted(String url, String parentPath, String filename) {
        boolean isCompleted = StatusUtil.isCompleted(url, parentPath, filename);
        return isCompleted;
    }

    public static BreakpointInfo getBreakpointInfo(int taskId) {
        // Note: the info will be deleted since task is completed download for data health lifecycle
        BreakpointInfo info = OkDownload.with().breakpointStore().get(taskId);
        return info;
    }

    public static BreakpointInfo getBreakpointInfo(String url, String parentPath, String filename) {
        BreakpointInfo info = StatusUtil.getCurrentInfo(url, parentPath, filename);
        return info;
    }

    public static String defaultDownloadPath(String url) {
        return Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator + app.getPackageName() + "/FDown/" + getFileNameFromUrl(url);
    }

    /**
     * 获取文件名, 在url中
     */
    private static String getFileNameFromUrl(String url) {
        String fileName = "unknown";
        try {

            String nameFrom = getFileNameFrom(url);
            if (!TextUtils.isEmpty(nameFrom)) {
                fileName = nameFrom;
            }

            Uri parse = Uri.parse(url);
            Set<String> parameterNames = parse.getQueryParameterNames();
            if (parameterNames.isEmpty()) {

            } else {
                String param = "";
                for (String s : parameterNames) {
                    param = parse.getQueryParameter(s);
                    try {
                        if (/*s.contains("name") ||*/ param.contains("name=")) {
                            break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                try {
                    fileName = param.split("name=")[1];
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileName;
    }

    private static String getFileNameFrom(String url) {
        String result = "";
        try {
            url = url.split("\\?")[0];
            int indexOf = url.lastIndexOf('/');
            if (indexOf != -1) {
                result = url.substring(indexOf + 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private static void log() {
//        DownloadTask task = new DownloadTask.Builder(MainActivity.Companion.getUrl(),
//                new File(defaultDownloadPath(MainActivity.Companion.getUrl()))).build();
//
//        Log.w("tag", task.getId() + "");
    }


    /**
     * 监听事件, 主线程回调
     */
    public static class FDownListener implements DownloadListener {

        @Override
        public void taskStart(@NonNull DownloadTask task) {
            //1
            String name = task.getFilename();
            if (task.getFile() != null) {
                name = task.getFile().getAbsolutePath();
            }
            L.d(TAG, "准备下载:\n" + task.getUrl() + "->" + name);
            onTaskStart(task);

        }

        @Override
        public void connectTrialStart(@NonNull DownloadTask task, @NonNull Map<String, List<String>> requestHeaderFields) {
            //2
        }

        @Override
        public void connectTrialEnd(@NonNull DownloadTask task, int responseCode, @NonNull Map<String, List<String>> responseHeaderFields) {
            //3
        }

        @Override
        public void downloadFromBeginning(@NonNull DownloadTask task, @NonNull BreakpointInfo info, @NonNull ResumeFailedCause cause) {
            //4_1 从开始的位置下载
        }

        @Override
        public void downloadFromBreakpoint(@NonNull DownloadTask task, @NonNull BreakpointInfo info) {
            //4_2 从断点的位置开始下载
        }

        @Override
        public void connectStart(@NonNull DownloadTask task, int blockIndex, @NonNull Map<String, List<String>> requestHeaderFields) {
            //5
        }

        @Override
        public void connectEnd(@NonNull DownloadTask task, int blockIndex, int responseCode, @NonNull Map<String, List<String>> responseHeaderFields) {
            //6
        }

        @Override
        public void fetchStart(@NonNull DownloadTask task, int blockIndex, long contentLength) {
            //7
        }

        @Override
        public void fetchProgress(@NonNull DownloadTask task, int blockIndex, long increaseBytes) {
            //8
            BreakpointInfo info = StatusUtil.getCurrentInfo(task);
            if (info != null) {
                long totalLength = info.getTotalLength();
                long totalOffset = info.getTotalOffset();
                int percent = (int) (totalOffset * 100 / totalLength);

                onTaskProgress(task, totalLength, totalOffset, percent, increaseBytes);

                StringBuilder builder = new StringBuilder();
                builder.append("\n下载进度:");
                builder.append(task.getUrl());
                builder.append("\n总大小:");
                builder.append(totalLength);
                builder.append(" ");
                builder.append(FileUtils.formatFileSize(totalLength));

                builder.append(" 已下载:");
                builder.append(totalOffset);
                builder.append(" ");
                builder.append(FileUtils.formatFileSize(totalOffset));
                builder.append(" ");
                builder.append(percent);
                builder.append("%");

                builder.append(" 新增:");
                builder.append(increaseBytes);
                builder.append(" ");
                builder.append(FileUtils.formatFileSize(increaseBytes));

                L.d(TAG, builder.toString());
            }
        }

        @Override
        public void fetchEnd(@NonNull DownloadTask task, int blockIndex, long contentLength) {
            //9
            L.i(TAG, "下载完成:" + blockIndex + " " + contentLength + " " + Util.humanReadableBytes(contentLength, false));
        }

        @Override
        public void taskEnd(@NonNull DownloadTask task, @NonNull EndCause cause, @Nullable Exception realCause) {
            //10
            try {
                L.i(TAG, "任务结束:" + cause + " " + task.getFile().getAbsolutePath() + " " + Util.humanReadableBytes(task.getInfo().getTotalLength(), false));
            } catch (Exception e) {

            }
            if (cause == EndCause.ERROR) {
                if (realCause != null) {
                    realCause.printStackTrace();
                } else {
                    L.e(TAG, "下载失败:" + task.getUrl());
                }
            }

            onTaskEnd(task, cause == EndCause.COMPLETED, realCause);
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
}
