package com.org.mylibrary.yichemap.http;

import android.os.Environment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by stone on 2017/7/13.
 */

public class CarServiceFactory {
    private final Gson mGsonDateFormat;

    private CarServiceFactory() {
        mGsonDateFormat = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd hh:mm:ss")
                .create();
    }

    private final static long DEFAULT_TIMEOUT = 1;

    public OkHttpClient getOkhttpClient() {
        //定制OkHttp
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        //设置超时时间
        httpClientBuilder.connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        httpClientBuilder.writeTimeout(DEFAULT_TIMEOUT,TimeUnit.SECONDS);
        httpClientBuilder.readTimeout(DEFAULT_TIMEOUT,TimeUnit.SECONDS);
        //设置缓存
        File httpCacheDirectory = Environment.getDownloadCacheDirectory();
        httpClientBuilder.cache(new Cache(httpCacheDirectory,10*1024*1024));
        return httpClientBuilder.build();
    }

    private static class SingletonHolder {
        private static final CarServiceFactory INSTANCE = new CarServiceFactory();
    }

    public static CarServiceFactory getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public <S> S createService(Class<S> serviceClass) {
        String baseUrl = "";
        try {
            Field field = serviceClass.getField("BASE_URL");
            baseUrl = (String) field.get(serviceClass);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(getOkhttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(serviceClass);
    }
}
