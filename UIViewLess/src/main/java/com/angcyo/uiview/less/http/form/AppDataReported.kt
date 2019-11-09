package com.angcyo.uiview.less.http.form

import com.angcyo.http.HttpSubscriber
import com.angcyo.http.Rx
import com.angcyo.lib.L
import com.angcyo.uiview.less.utils.RConcurrentTask
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.CopyOnWriteArraySet
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.max
import kotlin.math.min

/**
 * 数据上报操作类
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/08/27
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */

class AppDataReported {
    companion object {
        val _dataAdapterList = CopyOnWriteArraySet<DataReportAdapter>()

        /**注册用来适配[FormData]的数据上报处理*/
        fun registerDataAdapter(adapter: DataReportAdapter) {
            _dataAdapterList.add(adapter)
        }

        fun unregisterDataAdapter(adapter: DataReportAdapter) {
            _dataAdapterList.remove(adapter)
        }
    }

    /**流程开始 [UIThread]*/
    var onStart: (sum: Int) -> Unit = {}

    /**上传进度 [progress] 0-1f [UIThread]*/
    var onProgress: (progress: Float) -> Unit = {}

    /**流程结束 [UIThread]*/
    var onFinish: (success: Boolean, error: Throwable?) -> Unit = { success, error -> }

    private val running = AtomicBoolean(false)

    /**开始上传数据*/
    fun start(formDataList: List<FormData>) {
        if (running.get()) {
            return
        }
        if (formDataList.isEmpty()) {
            L.w("无数据需要上传.")
            Rx.onMain {
                onFinish(!isError.get(), lastError)
            }
            return
        }

        running.set(true)
        isError.set(false)
        lastError = null
        sum = 0
        progress = 0

        val list = ArrayList(formDataList)
        val adapterList = mutableListOf<DataReportAdapter>()
        list.forEach { formData ->

            val adapter = checkAdapter(formData)
            if (adapter != null) {
                adapterList.add(adapter)
            } else {
                L.w("无关联表单数据处理[Adapter], 将使用[DefaultFormDataAdapter]处理. formDes:${formData.formDes}")
                //throw IllegalStateException("表单数据无法处理: $formData")
                adapterList.add(DefaultFormDataAdapter().apply {
                    this.formData = formData
                })
            }
        }

        startInner(adapterList)
    }

    private fun checkAdapter(formData: FormData): DataReportAdapter? {
        var find: DataReportAdapter? = null
        _dataAdapterList.forEach { adapter ->
            if (adapter.pickFormData(formData)) {
                adapter.formData = formData
                find = adapter
                return find
            }
        }
        return find
    }

    private var sum = 0
    private var progress = 0
    private var isError = AtomicBoolean(false)
    private var lastError: Throwable? = null

    private fun startInner(adapterList: List<DataReportAdapter>) {
        val taskQueue = ConcurrentLinkedQueue<Runnable>()

        adapterList.forEach {
            if (it.checkFormDataValid(it.formData!!)) {
                sum += it.getFormDataCount(it.formData!!)
                taskQueue.add(AdapterRunnable(it))
            } else {
                L.e("表单数据无效:${it.formData!!}")
                return
            }
        }

        Rx.onMain {
            onStart(sum)
        }

        RConcurrentTask(taskQueue, 3) {
            running.set(false)

            Rx.onMain {
                onFinish(!isError.get(), lastError)
            }
        }
    }

    //保证在主线程, 进行的事件通知
    private fun notifyProgress() {
        Rx.onMain {
            onProgress(min(1f, progress * 1f / max(1, sum)))
        }
    }

    inner class AdapterRunnable(val adapter: DataReportAdapter) : Runnable {
        val retryCount = 3

        override fun run() {
            val countDownLatch = CountDownLatch(1)

            doIt(countDownLatch, 0)

            countDownLatch.await()
        }

        private fun doIt(countDownLatch: CountDownLatch, count: Int = 0) {
            adapter.dataReport(adapter.formData!!, {
                progress += it
                notifyProgress()
            }) { success, error ->
                //单个任务上传失败
                if (error != null) {
                    if (HttpSubscriber.isNetworkException(error) && count < retryCount) {
                        //重试3次
                        Rx.back {
                            doIt(countDownLatch, count + 1)
                        }
                    } else {
                        lastError = error
                        isError.set(true)
                        countDownLatch.countDown()
                    }
                } else {
                    countDownLatch.countDown()
                }
            }
        }
    }
}

abstract class DataReportAdapter {

    internal var formData: FormData? = null

    /**表单数据是否有效*/
    open fun checkFormDataValid(formData: FormData): Boolean {
        //toast tip
        return true
    }

    /**
     * 是否需要处理[formData]表单数据
     * @return true [formData] 表单数据将会需要处理, 否则交给下一个 [DataReportAdapter] 处理
     * */
    open fun pickFormData(formData: FormData): Boolean {
        return false
    }

    /**获取表单需要上传的数据次数, 用来显示进度*/
    open fun getFormDataCount(formData: FormData): Int {
        return 1
    }

    /**开始上传数据*/
    open fun dataReport(
        formData: FormData,
        onProgress: (Int) -> Unit,
        onEnd: (success: Boolean, error: Throwable?) -> Unit
    ) {

    }
}

/**默认表单数据上传*/
class DefaultFormDataAdapter : DataReportAdapter() {
    override fun pickFormData(formData: FormData): Boolean {
        return true
    }

    override fun getFormDataCount(formData: FormData): Int {
        return 1 + FormAttachManager.parseAttachJson(
            formData.formAttachJson
        ).second.size
    }

    override fun dataReport(
        formData: FormData,
        onProgress: (Int) -> Unit,
        onEnd: (success: Boolean, error: Throwable?) -> Unit
    ) {
        FormManager(JavaFormAttachManager().apply {
            onUploadProgress = { _, _ ->
                onProgress(1)
            }
        })
            .upload(formData)
            .load { _, error ->
                if (error == null) {
                    onProgress(1)
                }

                onEnd(error == null, error)
            }
    }
}