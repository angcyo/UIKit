package com.angcyo.uiview.less.http.form

import android.text.TextUtils
import com.angcyo.http.Rx
import com.angcyo.lib.L
import com.angcyo.uiview.less.http.form.FormManager.Companion.formLog
import com.angcyo.uiview.less.kotlin.*
import com.angcyo.uiview.less.utils.RUtils
import com.google.gson.internal.LinkedTreeMap
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File


/**
 * 附件上传管理
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/05/21
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */

open class FormAttachManager(val uploadUrl: String, val formKey: String = "file") :
    IAttachManager() {

    companion object {
        /**多附件,请使用此符号分割, 用来拼接项*/
        const val ATTACH_SPLIT = "|"
        /**这种字符开始的key, 需要对路径进行解析*/
        const val ATTACH_PATH_PARSE = "$"
        /**用来拆分项*/
        const val ATTACH_SPLIT_REVERT = "\\|"
        /**拼接在Url后面的id参数key值*/
        const val KEY_FILE_ID = "fileId"
        const val HTTP_PREFIX = "http"

        fun String.isHttpUrl(): Boolean {
            return this.toLowerCase().startsWith(HTTP_PREFIX)
        }

        fun parseAttachJson(json: String): Pair<List<String>, List<String>> {
            try {
                val attachMap = json.fromJson(LinkedTreeMap::class.java)
                return parseAttachJson(
                    attachMap
                )
            } catch (e: Exception) {
                e.printStackTrace()
                return Pair(listOf(), listOf())
            }
        }

        fun parseAttachJson(map: Map<*, *>): Pair<List<String>, List<String>> {
            try {
                val kList = mutableListOf<String>()
                val vList = mutableListOf<String>()

                map.forEach {
                    (it.key as? String)?.apply {
                        kList.add(this)
                        vList.add(it.value as String)
                    }
                }

                return Pair(kList, vList)
            } catch (e: Exception) {
                e.printStackTrace()
                return Pair(listOf(), listOf())
            }
        }
    }

    /**数据*/
    lateinit var attachMap: LinkedTreeMap<*, *>

    val keyList = mutableListOf<String>()
    val valueList = mutableListOf<String>()
    val uploadValueList = mutableListOf<String>()

    val parseBeanList = mutableListOf<ParseBean>()

    //当前key的索引
    var keyIndex = 0

    //当前key对应的 value索引
    var uploadIndex = 0

    /**可以添加其他自定义的表单字段*/
    var onBuildFormDataPart: (MultipartBody.Builder) -> Unit = {}

    override fun startUploadAttach(formData: FormData?, attachJson: String) {
        try {
            this.formData = formData
            reset()

            formLog("需要上传的附件:$attachJson")

            attachMap = attachJson.fromJson(LinkedTreeMap::class.java)

            attachMap.forEach {
                (it.key as? String)?.apply {
                    keyList.add(this)
                    valueList.add(it.value as String)
                }
            }

            startUpload(0, 0)
        } catch (e: Exception) {
            formLog("startUploadAttach:$e")
            onUploadError(e)
        }
    }

    protected fun reset() {
        keyList.clear()
        valueList.clear()
        uploadValueList.clear()
        parseBeanList.clear()
        keyIndex = 0
        uploadIndex = 0
    }

    /**开始上传附件
     * @param keyIndex 第几组
     * @param uploadIndex 第几个
     * */
    protected fun startUpload(keyIndex: Int = 0, uploadIndex: Int = 0) {
        this.keyIndex = keyIndex
        this.uploadIndex = uploadIndex

        if (keyIndex >= keyList.size) {
            //全部完成
            uploadEnd()
        } else {
            if (uploadIndex == 0) {
                //初始化需要上传的文件列表
                uploadValueList.clear()

                val split = valueList[keyIndex].split(ATTACH_SPLIT)

                for (p in split) {
                    if (!TextUtils.isEmpty(p)) {
                        uploadValueList.add(p)
                    }
                }
            }

            //最后一组
            if (uploadIndex >= uploadValueList.size) {
                //最后一组完成
                uploadNext()
            } else {
                uploadStart()
            }
        }
    }

    /**开始上传*/
    private fun uploadStart() {
        val uploadPath = uploadValueList[uploadIndex]

        formLog("准备上传:$uploadPath")

        if (uploadPath.isFileExists()) {
            try {
                uploadInner(uploadPath)
            } catch (e: Exception) {
                e.printStackTrace()
                formLog("uploadStart:${e.printString()}")
                onUploadError(e)
            }
        } else {
            var fileUrlId = -1
            if (uploadPath.isHttpUrl()) {
                fileUrlId = uploadPath.queryParameter(KEY_FILE_ID)?.toInt() ?: -1
                if (fileUrlId == -1) {
                    L.w("$uploadPath 路径中不包含 $KEY_FILE_ID 跳过上传.")
                }
            } else {
                L.w("跳过上传$uploadPath")
            }
            uploadSuccess(
                ParseBean(
                    fileUrlId.toLong(),
                    uploadPath
                )
            )
        }
    }

    /**一组上传完成*/
    private fun uploadNext() {
        valueList[keyIndex] = RUtils.connect(uploadValueList, ",")

        Rx.back {
            //避免递归调用. 防止堆栈溢出.
            startUpload(keyIndex + 1, 0)
        }
    }

    /**所有上传完成*/
    private fun uploadEnd() {
        formLog("上传结束:$keyList\n$valueList".apply { L.i(this) })
        onUploadEnd2(parseBeanList)
        onUploadEnd(keyList, valueList)
    }

    /**
     * 重写此方法, 实现上传文件
     * */
    open fun uploadInner(filePath: String) {
        val targetFile = File(filePath)

        if (!targetFile.exists()) {
            formLog("文件不存在:${filePath} 跳过.".apply { L.i(this) })
            startUpload(keyIndex, uploadIndex + 1)
            return
        }

        val okHttpClient = app_http_client(FormManager.TAG).build()

        val fileBody = targetFile.asRequestBody("multipart/form-data".toMediaTypeOrNull())

        //上传表单
        val request = Request.Builder()
            .url(uploadUrl)
            .method(
                "POST",
                MultipartBody.Builder().addFormDataPart(formKey, targetFile.name, fileBody).build()
            )
            .build()

        val response = okHttpClient.newCall(request).execute()

        // once
        val bodyString = response.body?.string() ?: ""

        if (response.isSuccessful) {
            formLog("上传成功:$bodyString".apply { L.i(this) })
        }

        uploadSuccess(parseBody(filePath, bodyString))
    }

    open fun parseBody(filePath: String, body: String): ParseBean {
        return ParseBean(localPath = filePath)
    }

    /**上传成功, 保存文件id*/
    open fun uploadSuccess(parseBean: ParseBean) {
        parseBeanList.add(parseBean)
        //保存需要的数据
        uploadValueList[uploadIndex] = "${parseBean.fileId}"
        //开始下一个附件上传
        Rx.back {
            //避免递归调用. 防止堆栈溢出.
            startUpload(keyIndex, uploadIndex + 1)
        }
    }
}