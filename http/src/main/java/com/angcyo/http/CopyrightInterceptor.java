package com.angcyo.http;

import android.os.Build;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 * Email:angcyo@126.com
 *
 * @author angcyo
 * @date 2019/01/17
 * Copyright (c) 2019 Shenzhen O&M Cloud Co., Ltd. All rights reserved.
 */
public class CopyrightInterceptor implements Interceptor {


    StringBuilder builder;

    public CopyrightInterceptor() {
        builder = new StringBuilder();
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        builder.delete(0, builder.length());

        builder.append(Build.VERSION.RELEASE).append("/");
        builder.append(Build.VERSION.SDK_INT).append(" ");
        builder.append("v:").append(Build.MANUFACTURER).append(" ");
        builder.append("m:").append(Build.MODEL).append(" ");
        builder.append("d:").append(Build.DEVICE).append(" ");
        builder.append("h:").append(Build.HARDWARE).append(" ");

        Request original = chain.request();
        Request.Builder requestBuilder = original.newBuilder()
                .header("Copyright-Robi", " https://github.com/angcyo")
                .header("Device", builder.toString());
        Request request = requestBuilder.build();
        return chain.proceed(request);
    }
}
