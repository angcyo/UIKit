package com.angcyo.http;

import android.os.Build;

import java.io.IOException;
import java.util.UUID;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Email:angcyo@126.com
 *
 * @author angcyo
 * @date 2019/01/17
 * Copyright (c) 2019 Shenzhen O&M Cloud Co., Ltd. All rights reserved.
 */
public class CopyrightInterceptor implements Interceptor {

    private static String psuedoID = null;

    //获得独一无二的Psuedo ID
    public static String getUniquePsuedoID() {
        if (psuedoID != null) {
            return psuedoID;
        }

        String result = null;
        String serial = null;

        String m_szDevIDShort = "35" +
                Build.BOARD.length() % 10 + Build.BRAND.length() % 10 +

                Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 +

                Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 +

                Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 +

                Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 +

                Build.TAGS.length() % 10 + Build.TYPE.length() % 10 +

                Build.USER.length() % 10; //13 位

        try {
            serial = android.os.Build.class.getField("SERIAL").get(null).toString();
            //API>=9 使用serial号
            result = new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
        } catch (Exception exception) {
            //serial需要一个初始化
            serial = "serial"; // 随便一个初始化
            //使用硬件信息拼凑出来的15位号码
            result = new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
        }

        psuedoID = result;

        return result;
    }

    public CopyrightInterceptor() {
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        StringBuilder builder = new StringBuilder();

        builder.append(Build.VERSION.RELEASE).append("/");
        builder.append(Build.VERSION.SDK_INT).append(" ");
        builder.append("v:").append(Build.MANUFACTURER).append(" ");
        builder.append("m:").append(Build.MODEL).append(" ");
        builder.append("d:").append(Build.DEVICE).append(" ");
        builder.append("h:").append(Build.HARDWARE).append(" ");

        Request original = chain.request();
        Request.Builder requestBuilder = original.newBuilder()
                .header("Copyright-Robi", " https://github.com/angcyo")
                .header("Psuedo-ID", getUniquePsuedoID())
                .header("Device", builder.toString());
        Request request = requestBuilder.build();
        return chain.proceed(request);
    }
}
