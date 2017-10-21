package com.kaycloud.mobileassistant.http.downloadprogress;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * 扩展ResponseBody，实现下载进度监听
 * @author: kaycloud
 * @date: 2017/10/20.
 */

public class DownloadProgressResponseBody extends ResponseBody {

    private ResponseBody mResponseBody;
    private DownloadProgressListener mProgressListener;
    private BufferedSource mBufferedSource;

    public DownloadProgressResponseBody(ResponseBody responseBody, DownloadProgressListener listener) {
        this.mResponseBody = responseBody;
        this.mProgressListener = listener;
    }

    @Override
    public MediaType contentType() {
        return mResponseBody.contentType();
    }

    @Override
    public long contentLength() {
        return mResponseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if (mBufferedSource == null) {
            mBufferedSource = Okio.buffer(source(mResponseBody.source()));
        }
        return mBufferedSource;
    }

    private Source source(Source source) {
        return new ForwardingSource(source) {
            long totalBytesRead = 0L;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                totalBytesRead += bytesRead != -1 ? bytesRead : 0;

                if (null != mProgressListener) {
                    mProgressListener.onProgress(totalBytesRead,mResponseBody.contentLength()
                            ,bytesRead == -1);
                }
                return bytesRead;
            }
        };
    }


}
