package com.kaycloud.mobileassistant.http.downloadprogress;

/**
 * @author: kaycloud
 * @date: 2017/10/20.
 */

public interface DownloadProgressListener {
    void onProgress(long bytesRead, long contentLength, boolean done);
}
