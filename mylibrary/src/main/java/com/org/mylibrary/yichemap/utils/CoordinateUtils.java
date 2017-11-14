package com.org.mylibrary.yichemap.utils;

/**
 * Created by eric3 on 2016/12/28.
 */

public class CoordinateUtils {
  static double M_PI = Math.PI;
  //经纬度转墨卡托
  // 经度(lon)，纬度(lat)
  public static double[] lonLat2Mercator(double lon,double lat)
  {
//    lon+=0.014300;
//    lat-=0.01400;
    lon+=0.005155;//深圳参数修正
    lat-=0.002633;
    double[] xy = new double[2];
    double x = lon *20037508.342789/180;
    double y = Math.log(Math.tan((90+lat)*M_PI/360))/(M_PI/180);
    y = y *20037508.34789/180;
    xy[0] = x;
    xy[1] = y;
    return xy;
  }
  //墨卡托转经纬度
  public static double[] mercator2Lonlat(double mercatorX,double mercatorY)
  {
    double[] xy = new double[2];
    double x = mercatorX/20037508.34*180;
    double y = mercatorY/20037508.34*180;
    y= 180/M_PI*(2*Math.atan(Math.exp(y*M_PI/180))-M_PI/2);
    xy[0] = x;
    xy[1] = y;
    return xy;
  }


}
