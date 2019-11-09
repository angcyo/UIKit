package com.angcyo.uiview.less.http.form

import android.net.Uri
import com.angcyo.http.Http
import com.angcyo.http.HttpSubscriber
import com.angcyo.lib.L
import com.angcyo.uiview.less.kotlin.app
import com.angcyo.uiview.less.kotlin.isFileExists
import com.angcyo.uiview.less.kotlin.queryParameter
import rx.Observable
import rx.Subscription

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/05/21
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
abstract class IAttachManager {

    /**2019-9-4 存储当前关联的 [FormData]*/
    var formData: FormData? = null

    /**
     * 上传结束
     * */
    open var onUploadEnd: (keyList: MutableList<String>, valueList: MutableList<String>) -> Unit =
        { keyList, valueList ->
            L.i("上传结束:$keyList\n$valueList")
        }

    open var onUploadEnd2: (parseBeanList: List<ParseBean>) -> Unit =
        {

        }

    /**
     * 上传失败
     * */
    open var onUploadError: (e: Exception) -> Unit = { e ->
        L.e("$e")
    }

    /**上传进度回调*/
    var onUploadProgress: (progress: Int, count: Int) -> Unit = { progress, count ->
        L.i("上传中:$progress/$count")
    }

    abstract fun startUploadAttach(formData: FormData? = null, attachJson: String)
}

data class ParseBean(
    var fileId: Long = -1,
    var filePath: String = "",//url, 需要拼接api base url

    var localPath: String = ""//本地路径
)

public fun String?.toUrl(fileId: Long = -1): String {
    if (this == null) {
        return ""
    }

    if (isFileExists()) {
        return this
    }

    var result: String = this
    if (!toLowerCase().startsWith("http")) {
        val baseUrl = app().baseUrl

        result = when {
            /* xx.com/ /api/xx */
            baseUrl.endsWith("/") && startsWith("/") -> "${baseUrl.substring(
                0,
                baseUrl.length - 1
            )}$this"
            /* xx.com api/xx */
            !baseUrl.endsWith("/") && !startsWith("/") -> "$baseUrl/$this"
            /* xx.com/ api/xx or xx.com /api/xx*/
            else -> "$baseUrl$this"
        }
    }

    if (fileId >= 0) {
        val uri = Uri.parse(this)
        val param = "${FormAttachManager.KEY_FILE_ID}=${fileId}"

        if (uri.query?.isNullOrEmpty() != false) {
            //url 没有 查询参数
            result = "${result}?${param}"
        } else {
            val oldFileId = result.queryParameter(FormAttachManager.KEY_FILE_ID)
            if (oldFileId?.isNullOrEmpty() != false) {
                //没有fileId参数
                result = "${result}&${param}"
            } else {
                //有fileId参数
                result = result.replace("${FormAttachManager.KEY_FILE_ID}=${oldFileId}", param)
            }
        }
    }

    return result
}

public fun <T> Observable<T>.load(onEnd: (data: T?, error: Throwable?) -> Unit = { _, _ -> }): Subscription {
    return compose(Http.defaultTransformer())
        .subscribe(object : HttpSubscriber<T>() {
            override fun onEnd(data: T?, error: Throwable?) {
                super.onEnd(data, error)
                onEnd(data, error)
            }
        })
}