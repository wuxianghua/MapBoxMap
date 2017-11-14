package com.org.mylibrary.yichemap.http;
import com.org.mylibrary.yichemap.mode.CarParkingInfos;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Administrator on 2017/10/10/010.
 */

public interface GetCarParkingInfoService {
    String BASE_URL = "http://parking.ipalmap.com";
    @GET("/parking/api/search/status")
    Call<CarParkingInfos> getCarParkingStatus(@Query("appkey") String appkey,
                                              @Query("mapId") int mapId,
                                              @Query("floorId") int floorId);
}
