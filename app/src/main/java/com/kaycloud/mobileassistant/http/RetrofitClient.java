package com.kaycloud.mobileassistant.http;

import com.kaycloud.mobileassistant.http.api.UpdateService;
import com.kaycloud.mobileassistant.http.downloadprogress.DownloadProgressInterceptor;
import com.kaycloud.mobileassistant.http.downloadprogress.DownloadProgressListener;
import com.kaycloud.mobileassistant.http.entity.UpdateInfoEntity;
import com.kaycloud.mobileassistant.utils.FileUtils;

import java.io.File;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author: kaycloud
 * @date: 2017/10/19.
 */

public class RetrofitClient {

    public static final String UPDATE_URL = "http://192.168.123.104:8080";

    public static final int DEFAULT_TIMEOUT = 5;

    private Retrofit mRetrofit;

    private UpdateService mUpdateService;

    /**
     * 没办法根据不同的base_url构造同一个Retrofit对象，因为每个Service的创建都
     */
    private RetrofitClient() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_TIMEOUT,TimeUnit.SECONDS)
                .build();
        mRetrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(UPDATE_URL)
                .build();
        mUpdateService = mRetrofit.create(UpdateService.class);
    }

    /**
     * 静态内部类单例模式，SingletonHolder是private的，除了getInstance无法访问它
     * 同时它是懒汉式的；因为它是静态的，所以同时读取实例的时候不需要进行同步，没有性能缺陷
     */
    private static class SingletonHolder{
        private static final RetrofitClient INSTANCE = new RetrofitClient();
    }

    public static final RetrofitClient getInstance(){
        return SingletonHolder.INSTANCE;
    }


    public void getUpdateInfo(Observer<UpdateInfoEntity> observer) {
        // subscribeOn指定的是onSubscribe这个方法的线程
        // observeOn指定的是onObserve这个方法的运行线程
        mUpdateService.getUpdateInfo("update.json")
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    /**
     * 下载更新文件
     * @param listener  进度回调
     * @param file  保存的文件
     * @param observer  处理的观察者
     */
    public static void downloadApk(DownloadProgressListener listener, final File file
            ,Observer observer) {
        DownloadProgressInterceptor interceptor = new DownloadProgressInterceptor(listener);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .retryOnConnectionFailure(true)
                .connectTimeout(DEFAULT_TIMEOUT,TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(UPDATE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        retrofit.create(UpdateService.class)
                .getApk("appv2.apk")
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .map(new Function<ResponseBody, InputStream>() {
                    @Override
                    public InputStream apply(@NonNull ResponseBody responseBody) throws Exception {
                        return responseBody.byteStream();
                    }
                })
                .observeOn(Schedulers.computation())
                .doOnNext(new Consumer<InputStream>() {
                    @Override
                    public void accept(InputStream inputStream) throws Exception {
                        FileUtils.wirteFile(inputStream, file);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

}
