package com.palmap.nagrand.support.util

/**
 * Created by wtm on 2017/7/7.
 */
object DataConvertUtils {

    /**
     * 墨卡托投影转经纬度坐标
     *
     * @param
     * @return
     */
    fun webMercator2LatLng(x1: Double, y1: Double): DoubleArray {
        val x = x1 / 20037508.34 * 180.0
        var y = y1 / 20037508.34 * 180.0
        y = 180 / Math.PI * (2 * Math.atan(Math.exp(y * Math.PI / 180)) - Math.PI / 2)
        return doubleArrayOf(y, x)
    }

    /**
     * 经纬度转墨卡托
     * @param lat
     * @param lng
     * @return
     */
    fun latlng2WebMercator(lat: Double, lng: Double): DoubleArray {
        val x = lng * 20037508.34 / 180
        var y = Math.log(Math.tan((90 + lat) * Math.PI / 360)) / (Math.PI / 180)
        y = y * 20037508.34 / 180
        return doubleArrayOf(x, y)
    }

}
