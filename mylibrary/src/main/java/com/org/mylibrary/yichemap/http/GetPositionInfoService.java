package com.org.mylibrary.yichemap.http;

import com.org.mylibrary.yichemap.mode.PositionResult;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by Administrator on 2017/11/10/010.
 */

public interface GetPositionInfoService {
    String BASE_URL = "http://rtls1.palmap.cn:40160";
    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST(".")
    Call<PositionResult> uploadAllBeaconsInfo(@Body RequestBody route);
}
