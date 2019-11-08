package com.angcyo.http

import com.angcyo.http.type.TypeBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.http.*
import rx.Observable
import rx.Subscription
import java.lang.reflect.Type

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

fun Observable<Response<JsonElement>>.fetch(onEnd: (data: Response<JsonElement>?, error: Throwable?) -> Unit = { _, _ -> }): Subscription {
    return compose(Http.defaultTransformer())
        .subscribe(object : HttpSubscriber<Response<JsonElement>>() {
            override fun onEnd(data: Response<JsonElement>?, error: Throwable?) {
                super.onEnd(data, error)
                onEnd(data, error)
            }
        })
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
        null
    }
}

/**网络状态异常信息*/
fun Throwable?.isNetworkException() = this?.run {
    HttpSubscriber.isNetworkException(this)
} ?: false