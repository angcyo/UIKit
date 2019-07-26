package com.angcyo.uiview.less.kotlin

import com.angcyo.http.Http
import com.angcyo.http.log.HttpLogFileInterceptor
import com.angcyo.uiview.less.utils.RLogFile
import okhttp3.OkHttpClient

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/05/06
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */

public fun app_http_client(logTag: String = "请求"): OkHttpClient.Builder {
    val defaultOkHttpClientBuilder = Http.defaultOkHttpClient(logTag)

    val httpLogFileInterceptor = HttpLogFileInterceptor(object : HttpLogFileInterceptor.OnHttpLogIntercept {
        override fun onHttpRequestLogIntercept(requestString: String) {
            RLogFile.log("http", requestString)
        }

        override fun onHttpResponseLogIntercept(responseString: String) {
            RLogFile.log("http", responseString)
        }
    })

//    val tokenInterceptor = TokenInterceptor(app().tokenListener)
//
//    defaultOkHttpClientBuilder.addInterceptor(httpLogFileInterceptor)
//    app().buildCacheInterceptor()?.let {
//        defaultOkHttpClientBuilder.addInterceptor(it)
//    }
//
//    defaultOkHttpClientBuilder.addInterceptor(tokenInterceptor)

    return defaultOkHttpClientBuilder
}
//
//public fun <T> request(service: Class<T>): T {
//    return Http.create(
//        Http.builder(app().buildOkHttpClient(), app().baseUrl).build(),
//        service
//    )
//}
//
///**
// * 带Loading界面
// * */
//public fun <T> Observable<T>.load(subscriber: WTLoadSubscriber<T>): Subscription {
//    return compose(Http.defaultTransformer()).subscribe(subscriber)
//}
//
//public fun <T> Observable<T>.load(
//    context: Context,
//    subscriptions: CompositeSubscription? = null,
//    onEnd: (data: T?, error: Throwable?) -> Unit = { _, _ -> }
//): Subscription {
//    return compose(Http.defaultTransformer())
//        .subscribe(object : WTLoadSubscriber<T>(context, subscriptions) {
//            override fun onEnd(data: T?, error: Throwable?) {
//                super.onEnd(data, error)
//                onEnd(data, error)
//            }
//        })
//}
//
///**不带界面*/
//public fun <T> Observable<T>.load(subscriber: WTSubscriber<T>): Subscription {
//    return compose(Http.defaultTransformer()).subscribe(subscriber)
//}
//
//public fun <T> Observable<T>.load(onEnd: (data: T?, error: Throwable?) -> Unit = { _, _ -> }): Subscription {
//    return compose(Http.defaultTransformer())
//        .subscribe(object : WTSubscriber<T>() {
//            override fun onEnd(data: T?, error: Throwable?) {
//                super.onEnd(data, error)
//                onEnd(data, error)
//            }
//        })
//}