package com.kaycloud.mobileassistant.http.api;

import com.kaycloud.mobileassistant.http.entity.UpdateInfoEntity;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * @author: kaycloud
 * @date: 2017/10/19.
 */

public interface UpdateService {

    @GET("/{path}")
    Observable<UpdateInfoEntity> getUpdateInfo(@Path("path")String path);

    @GET("/{path}")
    Observable<ResponseBody> getApk(@Path("path")String path);
}
