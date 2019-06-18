package com.angcyo.uiview.less.kotlin

import android.text.TextUtils
import com.angcyo.okdownload.FDown
import com.angcyo.okdownload.FDownListener
import com.liulishuo.okdownload.DownloadTask

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/06/17
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */

public fun String?.down(
    useNewListener: Boolean = false,
    onTaskStart: (task: DownloadTask) -> Unit = {},
    onTaskProgress: (
        task: DownloadTask,
        totalLength: Long,
        totalOffset: Long,
        percent: Int,
        increaseBytes: Long
    ) -> Unit = { _, _, _, _, _ -> },
    onTaskEnd: (task: DownloadTask, isCompleted: Boolean, realCause: Exception?) -> Unit = { _, _, _ -> }
) {
    if (TextUtils.isEmpty(this)) {
        return
    }

    val oldListener = if (useNewListener) null else FDown.getListener(this)

    val listener = oldListener ?: object : FDownListener() {

        override fun onTaskStart(task: DownloadTask) {
            super.onTaskStart(task)
            onTaskStart(task)
        }

        override fun onTaskProgress(
            task: DownloadTask,
            totalLength: Long,
            totalOffset: Long,
            percent: Int,
            increaseBytes: Long
        ) {
            super.onTaskProgress(task, totalLength, totalOffset, percent, increaseBytes)
            onTaskProgress(task, totalLength, totalOffset, percent, increaseBytes)

        }

        override fun onTaskEnd(task: DownloadTask, isCompleted: Boolean, realCause: Exception?) {
            super.onTaskEnd(task, isCompleted, realCause)
            onTaskEnd(task, isCompleted, realCause)
        }
    }

    FDown.down(this, listener)
}