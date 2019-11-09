package com.angcyo.uiview.less.http.form

import com.angcyo.http.CacheInterceptor
import com.angcyo.lib.L
import com.angcyo.uiview.less.http.form.FormManager.Companion.formLog
import com.angcyo.uiview.less.kotlin.*
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import okhttp3.MediaType.Companion.toMediaTypeOrNull

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019-7-25
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */

class SaasFormAttachManager(
    uploadUrl: String = "api/Attachment/Upload".toUrl(),
    formKey: String = "file"
) :
    FormAttachManager(uploadUrl, formKey) {

    /**秒传检查接口*/
    var checkCodeUrl = "api/Attachment/Code".toUrl()

    override fun uploadInner(filePath: String) {
        //super.uploadInner(filePath)

        val targetFile = File(filePath)

        if (!targetFile.exists()) {
            L.i("文件不存在:$filePath 跳过.")
            startUpload(keyIndex, uploadIndex + 1)
            return
        }

        val md5 = targetFile.md5()

        val okHttpClient = app().buildOkHttpClient()

        //秒传检查
        val checkRequest = Request.Builder()
            .header(CacheInterceptor.HEADER_NO_CACHE, "yes")
            .url("$checkCodeUrl:$md5")
            .get()
            .build()

        val checkResponse = okHttpClient.newCall(checkRequest).execute()

        if (checkResponse.isSuccessful) {
            // once
            val bodyString = checkResponse.body?.string() ?: ""
            val parseBean = parseBody(filePath, bodyString)

            if (parseBean.fileId > 0) {
                formLog("文件秒传:$filePath -> ${parseBean.filePath}".apply {
                    L.i(this)
                })
                uploadSuccess(parseBean)
                return
            }
        }

        //上传文件
        val fileBody = targetFile.asRequestBody(filePath.mimeType()?.toMediaTypeOrNull())

        //上传表单
        val request = Request.Builder()
            .url(uploadUrl)
            .method(
                "POST",
                MultipartBody.Builder().run {
                    addFormDataPart(formKey, targetFile.name, fileBody)
                    //.setType(MediaType.parse("application/json; charset=utf-8")!!)
                    setType(MultipartBody.FORM)
                    onBuildFormDataPart(this)
                    build()
                }
            )
            .addHeader("Accept", "application/json")
            .addHeader("x-data-Scope", md5)
            .addHeader(CacheInterceptor.HEADER_NO_CACHE, "yes")
            .build()

        val response = okHttpClient.newCall(request).execute()

        // once
        val bodyString = response.body?.string() ?: ""

        formLog("文件上传返回:$filePath -> $bodyString".apply {
            L.i(this)
        })

        if (response.isSuccessful) {
            val parseBean = parseBody(filePath, bodyString)
            val attachmentId = parseBean.fileId

            if (attachmentId > 0) {
                uploadSuccess(parseBean)
                return
            } else {
                onUploadError(IllegalStateException("$response"))
            }
        } else {
            onUploadError(IllegalStateException("$response"))
        }
    }

    override fun parseBody(filePath: String, body: String): ParseBean {
        val resultJson = when {
            body.isJsonObject() -> body.fromJson(JsonObject::class.java)
            body.isJsonArray() -> body.fromJson(JsonArray::class.java).get(0) as JsonObject
            else -> throw IllegalStateException("格式异常,非[json]格式:$body")
        }

        val attachmentId = resultJson.getLong("AttachmentId")
        val url = resultJson.getString("Url")?.toUrl()

        if (attachmentId > 0) {
            return ParseBean(attachmentId, url ?: "", filePath)
        }
        return ParseBean(localPath = filePath)
    }
}