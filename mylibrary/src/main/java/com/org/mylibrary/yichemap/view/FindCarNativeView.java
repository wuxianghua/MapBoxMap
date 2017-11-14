package com.org.mylibrary.yichemap.view;

import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.services.commons.geojson.FeatureCollection;
import com.org.mylibrary.yichemap.mode.LocationInfo;

/**
 * Created by Administrator on 2017/10/10/010.
 */

public interface FindCarNativeView {
    //添加有车位图层
    void addParkingCarLayer(FeatureCollection featureCollection);

    //添加有车位图层
    void addInvalidParkingCarLayer(FeatureCollection featureCollection);

    void changeCarParkingNum(int carNum, int noCarNum);

    //展示定位图标
    void showLocationIcon(LocationInfo locationInfo);

    //更新地图状态
    void updateMapCamera(CameraPosition position);

    //清除到航线
    void clearRoute();

    //添加定位点
    void addLocationMark(LatLng location);

    //导航完成，回复UI
    void resetBeforeNavi();

    //显示定位是否成功
    void setLocationSuccess(boolean isLocation);

    //重新规划路线
    void rePlanRoute(double x, double y);

    //保存车位信息
    void saveParkingInfo();
}
