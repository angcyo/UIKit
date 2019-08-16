package com.angcyo.http;

import androidx.annotation.NonNull;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.CountDownLatch;

/**
 * Email:angcyo@126.com
 *
 * @author angcyo
 * @date 2019/02/20
 * Copyright (c) 2019 Shenzhen O&M Cloud Co., Ltd. All rights reserved.
 */
public class TokenInterceptor implements Interceptor {
    private static final Charset UTF8 = Charset.forName("UTF-8");

    Charset charset;

    OnTokenListener tokenListener;

    /**
     * 重试次数
     */
    int tryCount = 1;

    StringBuilder responseBodyBuilder = new StringBuilder();

    /**
     * body 允许读取的最大值 64kb
     */
    long maxReadSize = 64 * 1024;

    public TokenInterceptor(OnTokenListener tokenListener) {
        this.tokenListener = tokenListener;
        charset = UTF8;
    }

    public TokenInterceptor(int tryCount, Charset charset, OnTokenListener tokenListener) {
        this.charset = charset;
        this.tokenListener = tokenListener;
        this.tryCount = tryCount;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originRequest = chain.request();

        if (tokenListener == null || tokenListener.ignoreRequest(originRequest)) {
            return chain.proceed(originRequest);
        }

        //拿到正常接口请求的数据
        Request tokenRequest = setToken(originRequest);

        Response originResponse = chain.proceed(tokenRequest);
        Response resultResponse = originResponse;

        if (tokenListener != null) {
            int index = 0;
            while (index++ < tryCount) {
                try {
                    Response oldResponse = resultResponse;
                    resultResponse = doIt(chain, originRequest, resultResponse);
                    if (oldResponse != resultResponse && tryCount > 1) {
                        //返回的结果不一样, 并且重试次数大于1
                        if (isTokenInvalid(resultResponse)) {
                            //再次判断返回结果是否是token失效
                        } else {
                            //token有效, 退出循环
                            break;
                        }
                    }
                } catch (Exception e) {
                    resultResponse = originResponse;
                    break;
                }
            }
        }
        return resultResponse;
    }

    private String getResponseBodyString(Response response) throws IOException {
        if (response == null) {
            return "";
        }

        responseBodyBuilder.delete(0, responseBodyBuilder.length());

        ResponseBody responseBody = response.body();
        if (responseBody != null
                && responseBody.contentType() != null
                && responseBody.contentLength() > 0
                && responseBody.contentLength() < maxReadSize) {

            BufferedSource source = responseBody.source();
            source.request(Long.MAX_VALUE);
            Buffer buffer = source.getBuffer();

            responseBodyBuilder.append(buffer.clone().readString(charset));
        }

        return responseBodyBuilder.toString();
    }

    /**
     * 通过返回结果, 判断token是否过期
     */
    private boolean isTokenInvalid(Response resultResponse) throws IOException {
        return tokenListener != null &&
                tokenListener.isTokenInvalid(resultResponse, getResponseBodyString(resultResponse));
    }

    /**
     * 获取token, 并且重新请求接口
     */
    private Response doIt(Chain chain, Request originRequest, Response resultResponse) throws IOException {
        Response result = resultResponse;
        //判断token是否过期
        if (isTokenInvalid(resultResponse)) {
            CountDownLatch countDownLatch = new CountDownLatch(1);

            //Token失效
            tokenListener.tryGetToken(countDownLatch);

            try {
                countDownLatch.await();
            } catch (Exception e) {
                e.printStackTrace();
            }

            //重新请求本次接口
            Request tokenRequest = setToken(originRequest);
            result = chain.proceed(tokenRequest);
        }
        return result;
    }

    /**
     * 为这个请求, 设置token信息
     **/
    private Request setToken(Request request) {
        Request result = request;
        if (tokenListener != null) {
            result = tokenListener.initToken(request);
        }
        return result;
    }

    public interface OnTokenListener {

        /**
         * 是否要忽略这个请求
         */
        boolean ignoreRequest(@NonNull Request originRequest);

        /**
         * 设置token
         */
        Request initToken(@NonNull Request originRequest);

        /**
         * 根据接口返回值, 判断token是否失效
         *
         * @return true token失效
         */
        boolean isTokenInvalid(@NonNull Response response, @NonNull String bodyString);

        /**
         * 重新获取token
         * 获取token成功之后, 请调用 {@link CountDownLatch#countDown()}
         */
        void tryGetToken(@NonNull CountDownLatch latch);
    }

    public static class TokenListenerAdapter implements OnTokenListener {

        @Override
        public boolean ignoreRequest(@NonNull Request originRequest) {
            return false;
        }

        @Override
        public Request initToken(@NonNull Request originRequest) {
            return originRequest;
        }

        @Override
        public boolean isTokenInvalid(@NonNull Response response, @NonNull String bodyString) {
            return false;
        }

        @Override
        public void tryGetToken(@NonNull CountDownLatch latch) {
            latch.countDown();
        }
    }
}
