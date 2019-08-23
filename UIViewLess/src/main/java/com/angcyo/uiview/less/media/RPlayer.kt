package com.angcyo.uiview.less.media

import android.media.AudioManager
import android.media.MediaPlayer
import android.text.TextUtils
import com.angcyo.lib.L
import com.angcyo.uiview.less.utils.ThreadExecutor
import java.util.concurrent.atomic.AtomicInteger

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：播放音乐工具类
 * 创建人员：Robi
 * 创建时间：2017/10/25 16:20
 * 修改人员：Robi
 * 修改时间：2017/10/25 16:20
 * 修改备注：
 * Version: 1.0.0
 */
class RPlayer {
    private var mediaPlay: MediaPlayer? = null

    /**是否循环播放*/
    var isLoop = false

    var onPlayListener: OnPlayerListener? = null

    var audioStreamType = AudioManager.STREAM_MUSIC

    var leftVolume: Float = 0.5f
    var rightVolume: Float = 0.5f

    /**正在播放的url, 播放完成后, 会被置空*/
    var playingUrl = ""

    /**播放的url, 正常播放过的url*/
    var playUrl = ""

    /**当前播放的状态*/
    private var playState: AtomicInteger = AtomicInteger(STATE_INIT)

    companion object {
        //初始化状态
        const val STATE_INIT = 0
        /**正常情况*/
        const val STATE_NORMAL = 1
        /**播放中*/
        const val STATE_PLAYING = 2
        /**停止播放*/
        const val STATE_STOP = 3
        /**资源释放*/
        const val STATE_RELEASE = 4

        const val STATE_PAUSE = 5

        /**播放完成*/
        const val STATE_COMPLETION = 6
        const val STATE_ERROR = -1

        fun stateString(state: Int): String {
            return when (state) {
                STATE_INIT -> "STATE_INIT"
                STATE_NORMAL -> "STATE_NORMAL"
                STATE_PLAYING -> "STATE_PLAYING"
                STATE_STOP -> "STATE_STOP"
                STATE_RELEASE -> "STATE_RELEASE"
                STATE_PAUSE -> "STATE_PAUSE"
                STATE_COMPLETION -> "STATE_COMPLETION"
                STATE_ERROR -> "STATE_ERROR"
                else -> "UNKNOWN"
            }
        }
    }

    private var seekToPosition = -1

    @Synchronized
    fun init() {
        if (mediaPlay == null) {
            mediaPlay = MediaPlayer()
        }
    }

    //开始播放
    private fun startPlayInner(mediaPlay: MediaPlayer) {
        setPlayState(STATE_PLAYING)
        startProgress()
        mediaPlay.start()
    }

    /**@param url 可以有效的网络, 和有效的本地地址*/
    fun startPlay(url: String) {
        if (playingUrl == url) {
            if (isPlayCall()) {

            } else {
                mediaPlay?.let {
                    startPlayInner(it)
                }
            }
            return
        } else {
            if (playState.get() != STATE_INIT) {
                stopPlay()
            }
        }
        if (mediaPlay == null) {
            init()
        }
        mediaPlay?.let {
            it.isLooping = isLoop
            it.setAudioStreamType(audioStreamType)
            it.setVolume(leftVolume, rightVolume)

            it.setOnErrorListener { mp, what, extra ->
                //L.e("call: startPlay -> $what $extra")
                setPlayState(STATE_ERROR)
                onPlayListener?.onPlayError(what, extra)

                it.reset()
                true
            }
            it.setOnCompletionListener {
                setPlayState(STATE_COMPLETION)
                onPlayListener?.onPlayCompletion(it.duration)
                it.reset()
            }
            it.setOnPreparedListener {
                //L.e("call: startPlay -> onPrepared ${it.duration}")
                onPlayListener?.onPreparedCompletion(it.duration)
                if (playState.get() == STATE_NORMAL) {
                    startPlayInner(it)
                    playSeekTo(seekToPosition)
                }
            }
            it.setDataSource(url)
            playingUrl = url
            playUrl = url

            setPlayState(STATE_NORMAL)
            it.prepareAsync()
        }
    }

    /**停止播放, 不释放资源, 下次可以重新setDataSource*/
    fun stopPlay() {

        mediaPlay?.let {
            if (isPlaying() && it.isPlaying) {
                it.stop()
            }
            it.reset()
        }

        setPlayState(STATE_STOP)
    }

    /**
     * 暂停播放
     * */
    fun pausePlay() {

        mediaPlay?.let {
            if (isPlaying()) {
                it.pause()
            }
        }

        setPlayState(STATE_PAUSE)
    }

    /**
     * 恢复播放
     * */
    fun resumePlay() {

        mediaPlay?.let {
            if (isPause()) {
                it.start()
            }
        }

        setPlayState(STATE_PLAYING)
    }

    /**
     * 重新播放
     * */
    fun replay() {
        playingUrl = ""
        startPlay(playUrl)
    }

    /**释放资源, 下次需要重新创建*/
    fun release() {
        setPlayState(STATE_RELEASE)
        stopPlay()
        mediaPlay?.let {
            it.release()
        }
        mediaPlay = null
    }

    /**设置音量*/
    fun setVolume(value: Float) {
        leftVolume = value
        rightVolume = value
        mediaPlay?.let {
            it.setVolume(value, value)
        }
    }

    /**
     * 多次点击同一视图,自动处理 暂停/恢复/播放
     * */
    fun click(url: String? = null) {
        if (isPlaying()) {
            pausePlay()
        } else if (isPause()) {
            resumePlay()
        } else {
            if (!TextUtils.isEmpty(url)) {
                startPlay(url!!)
            }
        }
    }

    /**正在播放中, 解析也完成了*/
    fun isPlaying() = playState.get() == STATE_PLAYING

    /**是否调用了播放, 但是有可能还在解析数据中*/
    fun isPlayCall() = (playState.get() == STATE_PLAYING || playState.get() == STATE_NORMAL)

    fun isPause() = playState.get() == STATE_PAUSE

    fun playState() = playState.get()

    private fun setPlayState(state: Int) {
        playUrl = playingUrl

        val oldState = playState.get()
        playState.set(state)

        when (state) {
            STATE_STOP, STATE_RELEASE, STATE_ERROR, STATE_COMPLETION -> playingUrl = ""
        }

        L.i("RPlayer: onPlayStateChange -> ${stateString(oldState)}->${stateString(state)}")

        if (oldState != state) {
            onPlayListener?.onPlayStateChange(playUrl, oldState, state)
        }
    }

    /**播放中的进度, 毫秒*/
    var currentPosition = 0

    /*开始进度读取*/
    private fun startProgress() {
        Thread(Runnable {
            while ((isPlayCall() || isPause()) &&
                mediaPlay != null &&
                onPlayListener != null
            ) {
                ThreadExecutor.instance().onMain {
                    if (isPlaying() && mediaPlay != null) {
                        currentPosition = mediaPlay!!.currentPosition
                        L.d("RPlayer: startProgress -> $currentPosition:${mediaPlay!!.duration}")
                        onPlayListener?.onPlayProgress(currentPosition, mediaPlay!!.duration)
                    }
                }
                try {
                    Thread.sleep(300)
                } catch (e: Exception) {
                }
            }
        }).apply {
            start()
        }
    }

    interface OnPlayerListener {
        /**@param duration 媒体总时长 毫秒*/
        fun onPreparedCompletion(duration: Int)

        /**
         * 播放进度回调, 毫秒
         * @param progress 当前播放多少毫秒
         * @param duration 总共多少毫秒
         * */
        fun onPlayProgress(progress: Int, duration: Int)

        /**播放完成, 毫秒*/
        fun onPlayCompletion(duration: Int)

        /**播放错误*/
        fun onPlayError(what: Int, extra: Int)

        /**播放状态回调*/
        fun onPlayStateChange(playUrl: String, from: Int, to: Int)

    }

    fun playSeekTo(msec: Int /*毫秒*/) {
        seekToPosition = msec
        if (msec >= 0 && playState.get() == STATE_PLAYING) {
            mediaPlay?.let {
                it.seekTo(msec)
                seekToPosition = -1
            }
        }
    }
}

abstract class SimplePlayerListener : RPlayer.OnPlayerListener {
    override fun onPreparedCompletion(duration: Int) {
    }

    /**
     * @see com.angcyo.uiview.less.media.RPlayer.OnPlayerListener#onPlayProgress(Int, Int)
     * */
    override fun onPlayProgress(progress: Int, duration: Int) {
    }

    override fun onPlayCompletion(duration: Int) {
    }

    override fun onPlayError(what: Int, extra: Int) {
    }

    override fun onPlayStateChange(playUrl: String, from: Int, to: Int) {
    }
}