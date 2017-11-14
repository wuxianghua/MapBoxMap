package com.org.mylibrary.yichemap.http;

import android.os.Environment;

import com.google.gson.Gson;
import com.org.mylibrary.yichemap.config.Constant;

import java.io.File;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Administrator on 2017/11/13/013.
 */

public class HttpFactory {
    private static final String endpoint = Constant.HOST;
    private static Retrofit retrofit;
    private static Gson gson = new Gson();
    private static WeakHashMap<String, Object> serviceCache = new WeakHashMap<>();

    static {

        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        builder.connectTimeout(15, TimeUnit.SECONDS);

        builder.cache(new Cache(new File(Environment.getExternalStorageDirectory() + Constant.CECHE_FILE), Constant.CECHE_SIZE));

        builder.interceptors().add(new HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.BODY));

        OkHttpClient okHttpClient = builder.build();

        retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(endpoint)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    public static <T> T create(Class<T> service) {
        T t = (T) serviceCache.get(service.getSimpleName());
        if (null == t) {
            long time = System.currentTimeMillis();
            t = retrofit.create(service);
            serviceCache.put(service.getSimpleName(), t);
        }
        return t;
    }

    public static Gson getGson() {
        return gson;
    }
}
