package com.angcyo.http;

import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpHeaders;
import okio.Buffer;
import okio.BufferedSource;

import java.io.IOException;

import static com.angcyo.http.log.HttpLogFileInterceptor.*;

/**
 * Email:angcyo@126.com
 *
 * @author angcyo
 * @date 2019/07/01
 */
public class RetryInterceptor implements Interceptor {
    OnRetryIntercept retryIntercept;

    public RetryInterceptor() {
    }

    public RetryInterceptor(OnRetryIntercept retryIntercept) {
        this.retryIntercept = retryIntercept;
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request originRequest = chain.request();

        if (retryIntercept == null) {
            return chain.proceed(originRequest);
        }

        Response originResponse = null;
        try {
            originResponse = chain.proceed(originRequest);

            String body = plainTextBody(originResponse);
            if (body != null) {
                if (retryIntercept.needRetry(originRequest, null, body, 0)) {
                    retry(chain, originRequest, originResponse);
                }
            }
        } catch (Exception e) {
            if (retryIntercept.needRetry(originRequest, e, null, 0)) {
                retry(chain, originRequest, originResponse);
            }
        }

        return originResponse;
    }

    private String plainTextBody(Response originResponse) throws IOException {
        String result = null;

        ResponseBody responseBody = originResponse.body();

        if (!HttpHeaders.hasBody(originResponse)) {
        } else if (bodyEncoded(originResponse.headers())) {
        } else {
            BufferedSource source = responseBody.source();
            // Buffer the entire body.
            source.request(Long.MAX_VALUE);
            Buffer buffer = source.getBuffer();

            if (isPlaintext(buffer)) {
                result = buffer.clone().readString(UTF8);
            }
        }

        return result;
    }

    private Response retry(@NonNull Chain chain, Request originRequest, Response originResponse) {
        Response response = originResponse;
        Request retryRequest = originRequest;
        int retryCount = retryIntercept.getRetryCount();

        int index = 0;
        while (index++ < retryCount) {
            try {
                retryRequest = retryIntercept.getRetryRequest(originRequest);
                response = chain.proceed(retryRequest);

                String body = plainTextBody(originResponse);
                if (body != null) {
                    if (retryIntercept.needRetry(retryRequest, null, body, index)) {
                        continue;
                    } else {
                        break;
                    }
                }
            } catch (Exception e) {
                if (retryIntercept.needRetry(retryRequest, e, null, index)) {
                    continue;
                } else {
                    break;
                }
            }
        }
        return response;
    }

    public interface OnRetryIntercept {

        /**
         * 需要重试的次数
         */
        int getRetryCount();

        /**
         * 是否需要重试, 可以在此方法中 执行sleep操作
         */
        boolean needRetry(@NonNull Request request, @Nullable Exception exception, @Nullable String bodyString, int count);

        /**
         * 可以更新请求
         */
        Request getRetryRequest(@NonNull Request originRequest);
    }

    public static class SimpleRetryIntercept implements OnRetryIntercept {

        long sleepTime = 1000;

        public SimpleRetryIntercept(long sleepTime) {
            this.sleepTime = sleepTime;
        }

        public SimpleRetryIntercept() {
        }

        @Override
        public int getRetryCount() {
            return 5;
        }

        @Override
        public boolean needRetry(@NonNull Request request, @Nullable Exception exception, @Nullable String bodyString, int count) {
            boolean result = false;
            result = HttpSubscriber.isNetworkException(exception);
            if (!TextUtils.isEmpty(bodyString)) {

            }
            if (result) {
                SystemClock.sleep(sleepTime);
            }
            return result;
        }

        @Override
        public Request getRetryRequest(@NonNull Request originRequest) {
            return originRequest;
        }
    }
}
