package com.kaycloud.mobileassistant.http.downloadprogress;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * @author: kaycloud
 * @date: 2017/10/20.
 */

public class DownloadProgressInterceptor implements Interceptor {

    private DownloadProgressListener mDownloadProgressListener;

    public DownloadProgressInterceptor(DownloadProgressListener listener) {
        this.mDownloadProgressListener = listener;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());

        return originalResponse.newBuilder()
                .body(new DownloadProgressResponseBody(originalResponse.body()
                        ,mDownloadProgressListener))
                .build();
    }
}
