package com.angcyo.http

import com.angcyo.http.log.LogUtil
import okhttp3.Interceptor
import okhttp3.Interceptor.Chain
import okhttp3.Request
import okhttp3.Response
import java.util.concurrent.CopyOnWriteArraySet

/**
 * 为每一个接口, 在无网络的情况下, 提供缓存功能
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/07/24
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */

open class CacheInterceptor : Interceptor {
    companion object {
        const val TAG = "CacheInterceptor"
        const val HEADER_NO_CACHE = "offline-no-cache"
        const val HEADER_FORCE_CACHE = "offline-force-cache"
    }

    var enableCache = true

    val cacheAdapter = CopyOnWriteArraySet<CacheAdapter>()

    override fun intercept(chain: Chain): Response {
        val originRequest = chain.request()
        if (enableCache && cacheAdapter.isNotEmpty()) {
            if (checkCache(originRequest)) {
                return loadCache(originRequest) ?: chain.proceed(originRequest)
            }

            val response: Response
            try {
                response = chain.proceed(originRequest)
                saveCache(originRequest, response)
            } catch (e: Exception) {
                if (HttpSubscriber.isNetworkException(e)) {
                    if (checkCache(originRequest)) {
                        return loadCache(originRequest) ?: chain.proceed(originRequest)
                    } else {
                        throw e
                    }
                } else {
                    throw e
                }
            }
            return response
        } else {
            return chain.proceed(originRequest)
        }
    }

    private fun checkCache(request: Request): Boolean {
        if (request.header(HEADER_NO_CACHE)?.isNotEmpty() == true) {
            //跳过缓存

            LogUtil.i(TAG, "跳过缓存:${request.url()}")

            return false
        }

        for (adapter in cacheAdapter) {
            if (adapter.checkNeedCache(request)) {
                return true
            }
        }
        return false
    }

    private fun saveCache(request: Request, response: Response) {
        for (adapter in cacheAdapter) {
            adapter.saveCache(request, response)
        }
    }

    private fun loadCache(request: Request): Response? {
        var response: Response? = null
        for (adapter in cacheAdapter) {
            adapter.loadCache(request)?.let {
                response = it
            }
        }

        return response /*?: Response.Builder()
            .request(request)
            .code(200)
            .body(ResponseBody.create(MediaType.parse("application/json; charset=utf-8"), "{}"))
            .build()*/
    }

    fun registerCacheAdapter(adapter: CacheAdapter) {
        cacheAdapter.add(adapter)
    }

    fun unregisterCacheAdapter(adapter: CacheAdapter) {
        cacheAdapter.remove(adapter)
    }
}

abstract class CacheAdapter {

    /**判断当前请求是否需要缓存*/
    open fun checkNeedCache(request: Request): Boolean {
        return false
    }

    open fun loadCache(request: Request): Response? {
        return null
    }

    open fun saveCache(request: Request, response: Response) {

    }
}