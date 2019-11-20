package com.angcyo.http

import com.angcyo.http.type.TypeBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*
import rx.Observable
import rx.Subscription
import java.lang.reflect.Type
import java.nio.charset.Charset

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/11/07
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */

interface Api {

    /*------------以下是[POST]请求-----------------*/

    /**Content-Type: application/json;charset=UTF-8*/
    @POST
    fun post(
        @Url url: String,
        @Body json: JsonElement = JsonObject(),
        @QueryMap queryMap: HashMap<String, Any> = hashMapOf()
    ): Observable<Response<JsonElement>>

    /*------------以下是[GET]请求-----------------*/

    /**Content-Type: application/json;charset=UTF-8*/
    @GET
    fun get(
        @Url url: String,
        @QueryMap queryMap: HashMap<String, Any> = hashMapOf()
    ): Observable<Response<JsonElement>>
}

/**基础配置, 必须*/
object ApiConfig {
    var onGetHttpClient: (() -> OkHttpClient)? = null
    var onGetHttpBaseUrl: (() -> String)? = null
}

//<editor-fold desc="快速请求">

/**
 * 通用接口请求
 * */
fun api(): Api {
    val client = ApiConfig.onGetHttpClient?.invoke()
    val baseUrl = ApiConfig.onGetHttpBaseUrl?.invoke()

    if (client == null) {
        throw NullPointerException("请先配置[ApiConfig.onGetHttpClient]")
    }

    if (baseUrl == null) {
        throw NullPointerException("请先配置[ApiConfig.onGetHttpBaseUrl]")
    }

    /*如果单例API对象的话, 就需要在动态切换BaseUrl的时候, 重新创建. 否则不会生效*/
    return Http.create(
        Http.builder(client, baseUrl).build(),
        Api::class.java
    )
}

/**别名函数*/
fun http(): Api = api()

fun connectUrl(host: String?, url: String?): String {
    val h = host?.trimEnd('/') ?: ""
    val u = url?.trimStart('/') ?: ""
    return "$h/$u"
}

fun http(config: RequestConfig.() -> Unit): Observable<Response<JsonElement>> {
    val requestConfig = RequestConfig(GET)
    requestConfig.config()

    if (!requestConfig.url.startsWith("http")) {
        requestConfig.url =
            connectUrl(ApiConfig.onGetHttpBaseUrl?.invoke(), requestConfig.url)
    }

    return if (requestConfig.method == POST) {
        http().post(requestConfig.url, requestConfig.body, requestConfig.query)
    } else {
        http().get(requestConfig.url, requestConfig.query)
    }
}

fun get(config: RequestConfig.() -> Unit): Observable<Response<JsonElement>> {
    return http(config)
}

fun post(config: RequestConfig.() -> Unit): Observable<Response<JsonElement>> {
    return http {
        method = POST
        this.config()
    }
}

const val GET = 1
const val POST = 2

data class RequestConfig(
    var method: Int = GET,
    var url: String = "",
    var body: JsonElement = JsonObject(),
    var query: HashMap<String, Any> = hashMapOf()
)

//</editor-fold>

//<editor-fold desc="拉取扩展方法">

/**读取ResponseBody中的字符串*/
private fun ResponseBody?.readString(charsetName: String = "UTF-8"): String {
    if (this == null) {
        return ""
    }
    val source = source()
    source.request(Long.MAX_VALUE)
    val buffer = source.buffer
    val charset: Charset = Charset.forName(charsetName)
    return buffer.clone().readString(charset)
}

private fun JsonElement.getInt(key: String): Int {
    if (this is JsonObject) {
        return this.getInt(key)
    }
    return 0
}

private fun JsonObject.getString(key: String): String? {
    val element = get(key)
    if (element is JsonPrimitive) {
        if (element.isString) {
            return element.asString
        }
    }
    return null
}

/**
 * Map<String, List<String>>
 * <pre>
 * type(Map::class.java) {
 *     addTypeParam(String::class.java)
 *     beginSubType(List::class.java)
 *     addTypeParam(String::class.java)
 *     endSubType()
 * }
 * </pre>
 * */
fun type(raw: Class<*>, config: TypeBuilder.() -> Unit = {}): Type {
    return TypeBuilder.newInstance(raw).run {
        config.invoke(this)
        build()
    }
}

/**
 * Bean::class.java.typeIn(List::class.java)
 * */
fun Class<*>.typeIn(raw: Class<*>): Type = type(raw, this)

/**
 * List::class.java.typeOf(Bean::class.java)
 * */
fun Class<*>.typeOf(type: Class<*>): Type = type(this, type)

/**
 * List<String>;
 * Bean<Data>;
 */
fun type(raw: Class<*>, type: Class<*>): Type {
    return TypeBuilder.build(raw, type)
}

fun Observable<Response<JsonElement>>.fetch(
    onEnd: (data: Response<JsonElement>?, error: Throwable?) -> Unit = { _, _ -> }
): Subscription {
    return compose(Http.defaultTransformer())
        .subscribe(object : HttpSubscriber<Response<JsonElement>>() {
            override fun onEnd(data: Response<JsonElement>?, error: Throwable?) {
                super.onEnd(data, error)
                onEnd(data, error)
            }
        })
}

fun <T> Observable<Response<JsonElement>>.fetchBean(
    type: Type,
    onEnd: (data: T?, error: Throwable?) -> Unit = { _, _ -> }
): Subscription {
    return compose(Http.defaultTransformer())
        .map {
            it.toBean<T>(type)
        }
        .subscribe(object : HttpSubscriber<T>() {
            override fun onEnd(data: T?, error: Throwable?) {
                super.onEnd(data, error)
                onEnd(data, error)
            }
        })
}

fun <T> Response<JsonElement>.toBean(type: Type): T? {
    return if (isSuccessful && body() != null) {
        when (val bodyJson = Json.to(body())) {
            null -> null
            else -> Json.from<T>(bodyJson, type)
        }
    } else {
        when (val bodyJson = errorBody()?.readString()) {
            null -> null
            else -> Json.from<T>(bodyJson, type)
        }
    }
}

//</editor-fold>

//<editor-fold desc="请求是否成功扩展方法">

/**http 状态成功, 并且逻辑code也是成功的*/
fun Response<JsonElement>?.isSuccess(): Pair<Boolean, String?> {
    var code = 0
    var msg: String? = null

    this?.apply {
        if (isSuccessful && this.body()?.getInt("code") ?: 0 in 200..299) {
            code = 200
        } else {
            msg = (this.body() as? JsonObject)?.getString("msg")
        }
    }

    return Pair(code in 200..299, msg)
}

fun okhttp3.Response?.isSuccess(): Pair<Boolean, String?> {
    val code = 0
    var msg: String? = null

    this?.apply {
        if (isSuccessful) {
            return body.isSuccess()
        } else {
            msg = message
        }
    }

    return Pair(code in 200..299, msg)
}

fun ResponseBody?.isSuccess(): Pair<Boolean, String?> {
    var code = 0
    var msg: String? = null

    this?.apply {
        val jsonObject = Json.from(readString(), JsonObject::class.java)

        if (jsonObject.getInt("code") in 200..299) {
            code = 200
        } else {
            msg = jsonObject.getString("msg")
        }
    }

    return Pair(code in 200..299, msg)
}

/**网络状态异常信息*/
fun Throwable?.isNetworkException() = this?.run {
    HttpSubscriber.isNetworkException(this)
} ?: false

//</editor-fold>
