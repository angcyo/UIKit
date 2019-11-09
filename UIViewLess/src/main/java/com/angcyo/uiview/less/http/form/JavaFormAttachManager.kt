package com.angcyo.uiview.less.http.form

import com.angcyo.http.CacheInterceptor
import com.angcyo.lib.L
import com.angcyo.uiview.less.http.form.FormManager.Companion.formLog
import com.angcyo.uiview.less.kotlin.*
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019-7-25
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */

class JavaFormAttachManager(
    uploadUrl: String = "file/uploadImages.json".toUrl(),
    formKey: String = "file"
) :
    FormAttachManager(uploadUrl, formKey) {

    override fun uploadInner(filePath: String) {
        //super.uploadInner(filePath)

        val targetFile = File(filePath)

        if (!targetFile.exists()) {
            L.i("文件不存在:$filePath 跳过.")
            startUpload(keyIndex, uploadIndex + 1)
            return
        }

        val okHttpClient = app().buildOkHttpClient()

        //秒传检查
        val checkRequest = Request.Builder()
            .url(uploadUrl)
            .header(CacheInterceptor.HEADER_NO_CACHE, "yes")
            .method(
                "POST",
                MultipartBody.Builder()
                    .addFormDataPart("md5Check", targetFile.md5())
                    .setType(MultipartBody.FORM)
                    .build()
            )
            .build()

        val checkResponse = okHttpClient.newCall(checkRequest).execute()

        if (checkResponse.isSuccessful) {
            val bodyString = checkResponse.body?.string() ?: "{}"

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
        val fileBody = targetFile.asRequestBody("multipart/form-data".toMediaTypeOrNull())

        //上传表单
        val request = Request.Builder()
            .url(uploadUrl)
            .header(CacheInterceptor.HEADER_NO_CACHE, "yes")
            .method(
                "POST",
                MultipartBody.Builder().run {
                    addFormDataPart(formKey, targetFile.name, fileBody)
                    setType(MultipartBody.FORM)
                    onBuildFormDataPart(this)
                    build()
                }
            )
            .build()

        val response = okHttpClient.newCall(request).execute()

        // once
        val bodyString = response.body?.string() ?: ""

        formLog("文件上传返回:$filePath -> $bodyString".apply {
            L.i(this)
        })

        if (response.isSuccessful) {
            val parseBean = parseBody(filePath, bodyString)

            if (parseBean.fileId > 0) {
                uploadSuccess(parseBean)
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

        val code = resultJson.getInt("code")
        val data = resultJson.getJson("data")
        val path = data?.getString("filePath")?.toUrl()
        val id = data?.getLong("id") ?: -1

        if (code in 200..299 && data?.getLong("id") ?: -1 > 0) {
            return ParseBean(id, path ?: "", filePath)
        }
        return ParseBean(localPath = filePath)
    }
}