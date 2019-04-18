package com.angcyo.uiview.less.media;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Looper;

import java.io.File;

/**
 * Android 手机录音工具类
 * <p>
 * Email:angcyo@126.com
 *
 * @author angcyo
 * @date 2019/01/23
 * Copyright (c) 2019 Shenzhen O&M Cloud Co., Ltd. All rights reserved.
 */
public class RRecord {
    public static final int BITRATE_AMR = 2 * 1024 * 8; // bits/sec

    public static final int BITRATE_3GPP = 20 * 1024 * 8; // bits/sec

    private static final String FILE_EXTENSION_AMR = ".amr";

    private static final String FILE_EXTENSION_3GPP = ".3gpp";

    Recorder innerRecorder;

    /**
     * 默认最大录制文件大小
     */
    int mMaxFileSize = 5 * 1024 * 1024;
    RecorderReceiver mReceiver;

    Context context;

    OnRecordListener onRecordListener;


    Handler mainHandler = new Handler(Looper.getMainLooper());

    /**
     * 进度回调检查
     */
    Runnable checkProgressRunnable = new Runnable() {
        @Override
        public void run() {
            int state = innerRecorder.state();

            boolean doNext = false;
            if (state == Recorder.RECORDING_STATE) {
                doNext = true;
                //录制进度
                if (onRecordListener != null) {
                    onRecordListener.onRecordProgress(innerRecorder.progress());
                }
            } else if (state == Recorder.PLAYING_STATE) {
                doNext = true;
                //播放进度
                if (onRecordListener != null) {
                    onRecordListener.onPlayProgress(innerRecorder.progress(), innerRecorder.playProgress());
                }
            }

            if (doNext) {
                mainHandler.postDelayed(this, 300);
            }
        }
    };

    public RRecord(Context context, String folderPath /*保存在那个文件夹*/) {
        this.context = context.getApplicationContext();
        innerRecorder = new Recorder(context.getApplicationContext(), folderPath);

        innerRecorder.setOnStateChangedListener(new Recorder.OnStateChangedListener() {
            @Override
            public void onStateChanged(int state) {
                if (onRecordListener != null) {
                    onRecordListener.onStateChanged(state);
                    if (state == Recorder.RECORDING_STATE) {
                        onRecordListener.onRecordStart();
                        checkProgress();
                    } else if (state == Recorder.PLAYING_STATE) {
                        onRecordListener.onPlayStart();
                        checkProgress();
                    } else if (state == Recorder.IDLE_STATE) {
                        onRecordListener.onRecordEnd();
                        onRecordListener.onPlayEnd();
                    }
                }
            }

            @Override
            public void onError(int error) {
                if (onRecordListener != null) {
                    onRecordListener.onError(error);
                }
            }
        });

        mReceiver = new RecorderReceiver();

        IntentFilter filter = new IntentFilter();
        filter.addAction(RecorderService.RECORDER_SERVICE_BROADCAST_NAME);
        this.context.registerReceiver(mReceiver, filter);
    }

    /**
     * 开始录制, 需要注册 RecorderService 服务哦
     *
     * @param fileName 不包括后缀名
     * @see RecorderService
     */
    public RRecord startRecord(String fileName) {
        int state = innerRecorder.state();
        if (state != Recorder.IDLE_STATE) {
            return this;
        }

        //重置 2019-4-13
        innerRecorder.reset();

        requestAudioFocus(context);

        boolean isHighQuality = false;

        int outputFileFormat = isHighQuality ? MediaRecorder.OutputFormat.AMR_WB
                : MediaRecorder.OutputFormat.AMR_NB;

        innerRecorder.startRecording(outputFileFormat, fileName,
                FILE_EXTENSION_AMR, isHighQuality, mMaxFileSize);
        return this;
    }

    /**
     * 停止录制
     */
    public RRecord stopRecord() {
        abandonAudioFocus(context);
        innerRecorder.stopRecording();
        return this;
    }

    /**
     * 释放资源
     */
    public RRecord release() {
        abandonAudioFocus(context);
        innerRecorder.stop();
        context.unregisterReceiver(mReceiver);
        return this;
    }


    /**
     * 开始回放
     */
    public RRecord startPlayback(String playPath, float percentage) {
        requestAudioFocus(context);
        innerRecorder.startPlayback(playPath, percentage);
        return this;
    }

    /**
     * 结束回放
     */
    public RRecord stopPlayback() {
        abandonAudioFocus(context);
        innerRecorder.stopPlayback();
        return this;
    }

    public RRecord setOnRecordListener(OnRecordListener onRecordListener) {
        this.onRecordListener = onRecordListener;
        return this;
    }

    /**
     * 返回当前保存的录音文件
     */
    public File getSampleFile() {
        return innerRecorder.getmSampleFile();
    }

    /**
     * 回放文件路径
     */
    public String getPlayFilePath() {
        return innerRecorder.getPlayFilePath();
    }

    private void checkProgress() {
        mainHandler.removeCallbacks(checkProgressRunnable);
        mainHandler.post(checkProgressRunnable);
    }

    /**
     * 请求拿到音频焦点
     */
    private static void requestAudioFocus(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);//请求焦点
    }

    /**
     * 释放音频焦点
     */
    private static void abandonAudioFocus(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.abandonAudioFocus(null);//放弃焦点
    }

    private class RecorderReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra(RecorderService.RECORDER_SERVICE_BROADCAST_STATE)) {
                boolean isRecording = intent.getBooleanExtra(
                        RecorderService.RECORDER_SERVICE_BROADCAST_STATE, false);
                innerRecorder.setState(isRecording ? Recorder.RECORDING_STATE : Recorder.IDLE_STATE);
            } else if (intent.hasExtra(RecorderService.RECORDER_SERVICE_BROADCAST_ERROR)) {
                int error = intent.getIntExtra(RecorderService.RECORDER_SERVICE_BROADCAST_ERROR, 0);
                innerRecorder.setError(error);
            }
        }
    }

    public static abstract class OnRecordListener implements Recorder.OnStateChangedListener {

        @Override
        public void onStateChanged(int state) {

        }

        @Override
        public void onError(int error) {

        }

        public void onRecordStart() {

        }

        public void onRecordEnd() {

        }

        /**
         * 录制进度 (秒)
         */
        public void onRecordProgress(int time) {

        }

        public void onPlayStart() {

        }

        public void onPlayEnd() {

        }

        /**
         * 返回播放时长, 和进度比例
         * @param time 秒
         */
        public void onPlayProgress(int time, float progress) {

        }
    }
}
