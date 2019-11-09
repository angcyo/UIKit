package com.angcyo.uiview.less.http.form

import com.angcyo.http.CacheInterceptor
import com.angcyo.http.Http
import com.angcyo.http.Json
import com.angcyo.uiview.less.kotlin.*
import com.angcyo.uiview.less.utils.RLogFile
import com.google.gson.JsonObject
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import rx.Observable
import rx.Observer
import rx.observables.SyncOnSubscribe
import java.util.*
import java.util.concurrent.CountDownLatch
import kotlin.collections.MutableList
import kotlin.collections.forEachIndexed
import kotlin.collections.isNotEmpty
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.set

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/05/21
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */

open class FormManager(val attachManager: IAttachManager? = null) {

    companion object {
        const val TAG = "表单"

        fun formLog(data: String) {
            //附件之后的日志
            RLogFile.log("form", "form.log", data)
        }

        /**从指定json数据中, 获取附件json*/
        fun parseAttachFromJson(json: String): String {
            return if (json.isJson()) {
                val jsonBuilder = Json.json()
                AttachJsonPathParser.read(json) { path, value ->
                    if (value.contains(FormAttachManager.ATTACH_SPLIT) || value.isFileExists()) {
                        jsonBuilder.add(path, value)
                    }
                }
                jsonBuilder.get()
            } else {
                "{}"
            }
        }

        /**合并附件json数据到数据json*/
        fun writeAttachToJson(
            json: String,
            keyList: MutableList<String>,
            valueList: MutableList<String>
        ): String {

            val formJsonObject: JsonObject = json.fromJson(JsonObject::class.java)

            val parsePathMap = mutableMapOf<String, String>()
            keyList.forEachIndexed { index, key ->
                if (key.startsWith(FormAttachManager.ATTACH_PATH_PARSE)) {
                    parsePathMap[key] = valueList[index]
                } else {
                    formJsonObject.addProperty(key, valueList[index])
                }
            }

            var result = formJsonObject.toString()

            if (parsePathMap.isNotEmpty()) {
                //此方式只适用于, 原json包含 attachJson的所有key值. 才能匹配!
                result = AttachJsonPathParser.write(
                    result,
                    parsePathMap
                )
            }

            return result
        }
    }

    /**
     * 上传表单
     * */
    open fun upload(formData: FormData, url: String? = null): Observable<ResponseBody> {
        if (url?.startsWith("http") == true || formData.formUrl?.startsWith("http") == true) {

        } else {
            throw IllegalArgumentException("表单上传地址无效:${formData.formUrl} $url")
        }

        val formUrl = if (url?.startsWith("http") == true) {
            url
        } else {
            formData.formUrl
        }

        return Observable.create(object : SyncOnSubscribe<Int, ResponseBody>() {
            override fun generateState(): Int = 1

            override fun next(state: Int, observer: Observer<in ResponseBody>): Int {
                return if (state > 0) {
                    if (formUrl?.startsWith("http") == true) {
                        val countDownLatch = CountDownLatch(1)

                        //表单上传之前的日志
                        val uuid = UUID.randomUUID().toString()
                        formLog("上传表单1:$uuid:${formData.formJson}")

                        //上传表单附件
                        uploadAttach(formData, { kList, vList ->

                            val okHttpClient = app_http_client(TAG).build()

                            val jsonBody = if (kList.isEmpty()) {
                                formData.formJson
                            } else {
                                writeAttachToJson(
                                    formData.formJson,
                                    kList,
                                    vList
                                )
                            }

                            //附件之后的日志
                            formLog("上传表单2:$uuid:${formData.formJson}")

                            //上传表单
                            val request = Request.Builder()
                                .header(CacheInterceptor.HEADER_NO_CACHE, "yes")
                                .url(formUrl)
                                .method("POST", Http.getJsonBody(jsonBody))
                                .build()

                            val response = okHttpClient.newCall(request).execute()

                            //处理表单接口返回值
                            handleResponse(observer, response)

                            countDownLatch.countDown()
                        }) { exception ->
                            formLog("uploadAttach:$uuid:$exception")
                            observer.onError(IllegalArgumentException("附件上传失败:$exception"))
                            countDownLatch.countDown()
                        }

                        countDownLatch.await()

                        0
                    } else {
                        observer.onError(IllegalArgumentException("无效的表单地址:${formData.formUrl}"))

                        -1
                    }
                } else if (state == 0) {
                    observer.onCompleted()
                    -1
                } else {
                    -1
                }
            }
        })
            .compose(Http.defaultTransformer())
    }

    /**上传表单附件*/
    open fun uploadAttach(
        formData: FormData,
        success: (keyList: MutableList<String>, valueList: MutableList<String>) -> Unit,
        error: (Exception) -> Unit
    ) {

        if (formData.formAttachJson.isJsonEmpty()) {
            //无附件
            success(mutableListOf(), mutableListOf())
            return
        }

        attachManager?.apply {
            onUploadEnd = { keyList, valueList ->
                success(keyList, valueList)
            }

            onUploadError = { e ->
                error(e)
            }

            startUploadAttach(formData, formData.formAttachJson)
        }

        if (attachManager == null) {
            try {
                val attachJsonObject: JsonObject =
                    formData.formAttachJson.fromJson(JsonObject::class.java)

                val kList = mutableListOf<String>()
                val vList = mutableListOf<String>()
                for (entry in attachJsonObject.entrySet()) {
                    kList.add(entry.key)
                    vList.add(entry.value.asString)
                }

                success(kList, vList)
            } catch (e: Exception) {
                success(mutableListOf(), mutableListOf())
            }
        }
    }

    /**处理表单返回的response*/
    open fun handleResponse(observer: Observer<in ResponseBody>, response: Response?) {
        if (response?.isSuccessful == true) {
            observer.onNext(response.body)
        } else {
            val bodyString: String? = response?.body?.readString()
            observer.onError(IllegalArgumentException(bodyString))
        }
    }
}