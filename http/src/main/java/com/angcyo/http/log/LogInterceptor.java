package com.angcyo.http.log;

/**
 * author: baiiu
 * date: on 16/8/31 19:40
 * description:
 */
public class LogInterceptor implements HttpLoggingInterceptorM.Logger {

    public static String INTERCEPTOR_TAG_STR = "OkHttp";
    int maxLength = 1024;

    public LogInterceptor() {
    }

    public LogInterceptor(String tag) {
        INTERCEPTOR_TAG_STR = tag;
    }

    public LogInterceptor(String tag, int maxLength) {
        INTERCEPTOR_TAG_STR = tag;
        this.maxLength = maxLength;
    }

    @Override
    public void log(String message, @LogUtil.LogType int type) {
        if (message != null) {
            int length = message.length();
            if (length > maxLength) {
                message = message.substring(0, maxLength);
                message += " 消息太长,剩余" + (length - maxLength);
            }
        }
        LogUtil.printLog(false, type, INTERCEPTOR_TAG_STR, message);
    }
}