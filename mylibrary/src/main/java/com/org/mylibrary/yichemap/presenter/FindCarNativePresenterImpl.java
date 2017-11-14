package com.org.mylibrary.yichemap.presenter;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.services.api.utils.turf.TurfMeasurement;
import com.mapbox.services.commons.geojson.Feature;
import com.mapbox.services.commons.geojson.FeatureCollection;
import com.mapbox.services.commons.geojson.LineString;
import com.mapbox.services.commons.models.Position;
import com.org.mylibrary.yichemap.config.Constant;
import com.org.mylibrary.yichemap.http.CarServiceFactory;
import com.org.mylibrary.yichemap.http.GetCarParkingInfoService;
import com.org.mylibrary.yichemap.mode.CarParkingInfo;
import com.org.mylibrary.yichemap.mode.CarParkingInfos;
import com.org.mylibrary.yichemap.mode.LocationInfo;
import com.org.mylibrary.yichemap.mode.PositionResult;
import com.org.mylibrary.yichemap.utils.CoordinateUtils;
import com.org.mylibrary.yichemap.utils.PositionUtil;
import com.org.mylibrary.yichemap.utils.ThreadManager;
import com.org.mylibrary.yichemap.view.FindCarNativeView;
import com.org.mylibrary.yichemap.widget.LatLngEvaluator;
import com.palmap.astar.navi.AStarPath;
import com.palmap.astar.navi.AStarVertex;
import com.palmap.core.data.PlanarGraph;
import com.palmap.nagrand.support.util.DataConvertUtils;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.operation.distance.DistanceOp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2017/10/10/010.
 */

public class FindCarNativePresenterImpl implements FindCarNativePresenter{

    private static final String TAG = FindCarNativePresenterImpl.class.getSimpleName();
    private Context mContext;
    private Handler handler;
    private ValueAnimator markerAnimator;
    private LatLngEvaluator evaluator = new LatLngEvaluator();
    //定位点
    private LatLng locationLatLng = null;
    //导航点的个数
    private int count;
    //导航是否完成
    private boolean routeFinished = false;
    private FindCarNativeView mFindCarNativeView;
    //到航线上坐标点的集合
    private List<LatLng> routePoints = new ArrayList<>();
    //获取停车位数据
    private GetCarParkingInfoService mGetCarParkingInfoService;
    //是否开始导航
    private boolean isStartNavi;
    //终点
    private LatLng endPoint;
    //距离终点的距离
    private double destationDistance;
    //所有停车的车位
    private List<Integer> parkingCars;
    //没有停车的车位
    private List<Integer> noParkingCars;
    //无效的停车位
    private List<Integer> invalidParkingCars;
    private List<Integer> oldInvalidParkCars;
    //上次网络请求没有停车的车位
    private List<Integer> oldNoParkingCars;
    //上次网络请求没有停车的车位
    private List<Integer> oldParkingCars;
    //没有停车位的FeatureCollection
    private FeatureCollection noCarFeatureCollection;
    //无效停车位的FeatureCollection
    private FeatureCollection invalidCarFeatureCollection;
    //有停车位的Features
    private List<Feature> carFeature;
    //有停车位的Features
    private List<Feature> invalidCarFeature;
    //没有停车位的Features
    private List<Feature> noCarFeatures;
    //路线数据
    List<AStarPath> routes;
    //定位经纬度
    private LatLng latLng;
    //导航吸附距离
    private final int nearLength = 10;
    //缩放级别
    private double scale;
    Call<CarParkingInfos> carParkingStatus;
    List<CarParkingInfo> carportInfos;
    private PlanarGraph mParkData;
    //定位工具类
    private PositionUtil pos;
    //构造方法
    public FindCarNativePresenterImpl(PlanarGraph parkData) {
        mParkData = parkData;
    }

    @Override
    public void attachView(Context context, FindCarNativeView findCarNativeView) {
        mContext = context;
        mFindCarNativeView = findCarNativeView;
        pos = new PositionUtil(context);
    }

    @Override
    public void onCreate() {
        initData();
        if (mGetCarParkingInfoService == null) {
            mGetCarParkingInfoService = CarServiceFactory.getInstance().createService(GetCarParkingInfoService.class);
        }
        Observable.interval(2000,60000, TimeUnit.MILLISECONDS).subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {
                carParkingStatus = mGetCarParkingInfoService.getCarParkingStatus(Constant.appkey, Constant.mapId, Constant.floorId);
                carParkingStatus.enqueue(new Callback<CarParkingInfos>() {
                    @Override
                    public void onResponse(Call<CarParkingInfos> call, Response<CarParkingInfos> response) {
                        if (response.body() == null) return;
                        carportInfos = response.body().carportInfos;
                        if (carportInfos == null || carportInfos.size() == 0) return;
                        ThreadManager.getNormalPool().execute(new Runnable() {
                            @Override
                            public void run() {
                                resolutionParkingData(carportInfos);
                            }
                        });
                    }

                    @Override
                    public void onFailure(Call<CarParkingInfos> call, Throwable t) {
                        Log.e(TAG,"执行失败");
                    }
                });
            }
        });
        getLocation();
    }
    //初始化数据
    private void

    initData() {
        parkingCars = new ArrayList<>();
        noParkingCars = new ArrayList<>();
        carFeature = new ArrayList<>();
        noCarFeatures = new ArrayList<>();
        oldNoParkingCars = new ArrayList<>();
        oldParkingCars = new ArrayList<>();
        invalidParkingCars = new ArrayList<>();
        invalidCarFeature = new ArrayList<>();
        oldInvalidParkCars = new ArrayList<>();
        locationInfo = new LocationInfo();
        latLng = new LatLng();
    }

    //解析停车位数据,放在子线程进行处理
    public void resolutionParkingData(List<CarParkingInfo> carParkingInfos) {
        parkingCars.clear();
        noParkingCars.clear();
        invalidParkingCars.clear();
        for (CarParkingInfo carParkingInfo : carParkingInfos) {
            if (carParkingInfo.occupied == 1) {
                parkingCars.add(carParkingInfo.poiId);
            }else if (carParkingInfo.occupied == 0){
                noParkingCars.add(carParkingInfo.poiId);
            }else if (carParkingInfo.occupied == -1) {
                invalidParkingCars.add(carParkingInfo.poiId);
            }
        }
        if (noParkingCars.size() == oldNoParkingCars.size()&&oldInvalidParkCars.size() == invalidParkingCars.size()) {
            return;
        }
        if (noParkingCars.size() != 0) {
            oldNoParkingCars.clear();
            oldNoParkingCars.addAll(noParkingCars);
        }
        if (parkingCars.size() != 0) {
            oldParkingCars.clear();
            oldParkingCars.addAll(parkingCars);
        }
        if (invalidParkingCars.size() != 0) {
            oldInvalidParkCars.clear();
            oldInvalidParkCars.addAll(invalidParkingCars);
        }
        if (mParkData == null || mParkData.getDataMap() == null ||mParkData.getDataMap().get("Area")==null) return;
        for (Feature feature : mParkData.getDataMap().get("Area").getFeatures()) {
            int featureId = Integer.parseInt(feature.getProperty("id").toString());
            if (parkingCars.contains(featureId)) {
                carFeature.add(feature);
            }else if (noParkingCars.contains(featureId)){
                noCarFeatures.add(feature);
            }else if (invalidParkingCars.contains(featureId)) {
                invalidCarFeature.add(feature);
            }
        }
        if (noCarFeatures.size() != 0) {
            noCarFeatureCollection = FeatureCollection.fromFeatures(noCarFeatures);
            mFindCarNativeView.addParkingCarLayer(noCarFeatureCollection);
        }
        if (invalidCarFeature.size() != 0) {
            invalidCarFeatureCollection = FeatureCollection.fromFeatures(invalidCarFeature);
            mFindCarNativeView.addInvalidParkingCarLayer(invalidCarFeatureCollection);
        }
        mFindCarNativeView.changeCarParkingNum(parkingCars.size(),noParkingCars.size());
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDestroy() {
        if (carportInfos != null) {
            carportInfos.clear();
            carportInfos = null;
        }
        if (routes != null) {
            routes.clear();
            routes = null;
        }
        if (noCarFeatures != null) {
            noCarFeatures.clear();
            noCarFeatures = null;
        }
        if (invalidCarFeature != null) {
            invalidCarFeature.clear();
            invalidCarFeature = null;
        }
        if (carFeature != null) {
            carFeature.clear();
            carFeature = null;
        }
        if (routePoints != null) {
            routePoints.clear();
            routePoints = null;
        }
        if (invalidCarFeatureCollection != null) {
            invalidCarFeatureCollection = null;
        }
        if (noCarFeatureCollection != null) {
            noCarFeatureCollection = null;
        }
        if (oldNoParkingCars != null) {
            oldNoParkingCars.clear();
            oldNoParkingCars = null;
        }
        if (invalidParkingCars != null) {
            invalidParkingCars.clear();
            invalidParkingCars = null;
        }
        if (parkingCars != null) {
            parkingCars.clear();
            parkingCars = null;
        }
        if (evaluator != null) {
            evaluator = null;
        }
        pos.unPwebViewRegistertation();
        pos.exitBeaconScan();
        carParkingStatus = null;
        latLng = null;
    }

    @Override
    public void startMovcNavi(FeatureCollection featureCollection, LatLng coordinate) {
        animLocationMark(coordinate);
        routePoints.clear();
        List<Feature> features = featureCollection.getFeatures();
        for (int i = 0; i < features.size(); i++) {
            LineString lineString = (LineString) features.get(i).getGeometry();
            for (Position position : lineString.getCoordinates()) {
                LatLng l = new LatLng(
                        position.getLatitude(),
                        position.getLongitude());
                if (!routePoints.contains(l)) {
                    routePoints.add(l);
                }
            }
        }
        startMovcCarNavi();
    }
    //结束导航
    @Override
    public void stopNavi() {
        isStartNavi = false;
        routes = null;
        scale = 18;
        CameraPosition position = new CameraPosition.Builder()
                .target(newLatLng)
                .zoom(scale)
                .bearing(0) // Rotate the camera
                .tilt(45)
                .build();
        mFindCarNativeView.updateMapCamera(position);
    }

    //结束模拟导航
    @Override
    public void stopMovcNavi() {
        handler.removeCallbacksAndMessages(null);
        markerAnimator.cancel();
        routes = null;
        scale = 18;
        CameraPosition position = new CameraPosition.Builder()
                .target(newLatLng)
                .zoom(scale)
                .bearing(0) // Rotate the camera
                .tilt(45)
                .build();
        mFindCarNativeView.updateMapCamera(position);
    }

    @Override
    public void showSearchPark(LatLng latLng) {
        scale = 18;
        CameraPosition position = new CameraPosition.Builder()
                .target(latLng)
                .zoom(scale)
                .bearing(0) // Rotate the camera
                .tilt(45)
                .build();
        mFindCarNativeView.updateMapCamera(position);
    }


    double x;
    double y;
    private LocationInfo locationInfo;
    @Override
    public void getLocation() {
        pos.setOnUpdateLocationData(new PositionUtil.OnUpdateLocationData() {
            @Override
            public void updateLocation(String state, final PositionResult positionResult) {
                if ("OK".equals(state)) {
                    ThreadManager.getNormalPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            x = positionResult.x;
                            y = positionResult.y;
                            locationInfo.x = positionResult.x;
                            locationInfo.y = positionResult.y;
                            double[] doubles = CoordinateUtils.mercator2Lonlat(x, y);
                            latLng.setLatitude(doubles[1]);
                            latLng.setLongitude(doubles[0]);
                            locationInfo.logtitude = latLng.getLongitude();
                            locationInfo.latitude = latLng.getLatitude();
                            if (isStartNavi == true) {
                                double distance = testCalcDistance(latLng);
                                if (distance < nearLength) {
                                    latLng = getMinLatLng(latLng);
                                } else if (distance > 15){
                                    mFindCarNativeView.rePlanRoute(x,y);
                                }
                                mFindCarNativeView.showLocationIcon(locationInfo);
                                startNaviEngine(latLng.getLatitude(),latLng.getLongitude());
                            }else {
                                mFindCarNativeView.showLocationIcon(locationInfo);
                            }
                            mFindCarNativeView.setLocationSuccess(true);
                        }
                    });
                }
            }
        });
        pos.start();
    }

    @Override
    public void startNavi(FeatureCollection featureCollection, LatLng coordinate) {
        isStartNavi = true;
        routeFinished = false;
        routePoints.clear();
        List<Feature> features = featureCollection.getFeatures();
        for (int i = 0; i < features.size(); i++) {
            LineString lineString = (LineString) features.get(i).getGeometry();
            for (Position position : lineString.getCoordinates()) {
                LatLng l = new LatLng(
                        position.getLatitude(),
                        position.getLongitude());
                if (!routePoints.contains(l)) {
                    routePoints.add(l);
                }
            }
        }
        endPoint = routePoints.get(routePoints.size()-1);
    }

    private void startMovcCarNavi() {
        if (handler == null) {
            handler = new Handler();
        } else {
            handler.removeCallbacksAndMessages(null);
            count = 0;
            routeFinished = false;
        }
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final Runnable that = this;
                if (!routeFinished && (routePoints.size() - 1) >= count) {
                    final LatLng endLatLng = routePoints.get(count);
                    double angle = computeHeading(locationLatLng, endLatLng);
                    scale = 20;
                    CameraPosition position = new CameraPosition.Builder()
                            .target(routePoints.get(count))
                            .zoom(scale)
                            .bearing(angle) // Rotate the camera
                            .tilt(45)
                            .build();
                    mFindCarNativeView.updateMapCamera(position);
                    animLocationMark(endLatLng);
                    count++;
                    handler.postDelayed(that, 500);
                } else {
                    routeFinished = true;
                    Toast.makeText(mContext, "导航完成 !", Toast.LENGTH_SHORT).show();
                    routes = null;
                    mFindCarNativeView.clearRoute();
                    mFindCarNativeView.resetBeforeNavi();
                    mFindCarNativeView.saveParkingInfo();
                }
            }
        };
        handler.post(runnable);
    }

    private void animLocationMark(final LatLng location) {

        if (locationLatLng == null) {
            locationLatLng = location;
            mFindCarNativeView.addLocationMark(location);
            return;
        }

        if (locationLatLng.equals(location)) {
            return;
        }

        if (markerAnimator != null) {
            markerAnimator.cancel();
            markerAnimator = null;
        }
        final LatLng oldLatLng = locationLatLng;
        markerAnimator = ValueAnimator.ofFloat(0, 1);
        markerAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                LatLng p = evaluator.evaluate(animation.getAnimatedFraction(), oldLatLng, location);
                mFindCarNativeView.addLocationMark(p);
            }
        });
        markerAnimator.setInterpolator(new LinearInterpolator());
        markerAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                locationLatLng = location;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                locationLatLng = location;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        markerAnimator.setDuration(500);
        markerAnimator.start();
    }

    public static double computeHeading(LatLng from, LatLng to) {
        return TurfMeasurement.bearing(
                Position.fromCoordinates(from.getLongitude(), from.getLatitude()),
                Position.fromCoordinates(to.getLongitude(), to.getLatitude())
        );
    }

    /*//获取定位的位置
    LocClient client;
    @Override
    public void getLocation() {
        if(client == null) {
            client = new DefaultLocClient(mContext, "NDdlMDMyZGEtZDhmMi00NmQyLWEyZTEtM2E1MTljMGM3ZjFi", "sTWxGxyfXG5RZebbUryCEvhDjVCMi6YvIiPUg7+YwKI");
        }
        client.setOnLocResultReceivedListener(new LocClient.OnLocResultReceivedListener() {
            @Override
            public void OnSuccess(int type, final Map<String, String> result) {
                //楼宇ID
                String buildingId = result.get("building_id");
                //楼层
                int floor = Integer.parseInt(result.get("floor"));
                //发送报文时的时间戳
                long timestamp = Long.parseLong(result.get("timestamp"));
                switch (type) {
                    case LocClient.TYPE_FLOOR:
                        //楼层切换时的结果
                        break;
                    case LocClient.TYPE_LOC:
                        //定位成功返回的结果
                        ThreadManager.getNormalPool().execute(new Runnable() {
                            @Override
                            public void run() {
                                double x = Float.parseFloat(result.get("x"));
                                double y = Float.parseFloat(result.get("y"));
                                x =  (12697074.245 + (x/7.850));
                                y =  (2588966.542 - (y/7.850));
                                double[] doubles = CoordinateUtils.mercator2Lonlat(x, y);
                                latLng.setLatitude(doubles[1]);
                                latLng.setLongitude(doubles[0]);
                                if (isStartNavi == true) {
                                    double distance = testCalcDistance(latLng);
                                    if (distance < nearLength) {
                                        latLng = getMinLatLng(latLng);
                                    } else if (distance > 15){
                                        mFindCarNativeView.rePlanRoute(x,y);
                                    }
                                    mFindCarNativeView.showLocationIcon(latLng.getLatitude(),latLng.getLongitude(),x,y);
                                    startNaviEngine(latLng.getLatitude(),latLng.getLongitude());
                                }else {
                                    mFindCarNativeView.showLocationIcon(latLng.getLatitude(),latLng.getLongitude(),x,y);
                                }
                                mFindCarNativeView.setLocationSuccess(true);
                            }
                        });
                        break;
                }
            }

            @Override
            public void OnFailed(String code, String message) {
                ThreadManager.getNormalPool().execute(new Runnable() {
                    @Override
                    public void run() {
                        double x = 783.267333984375;
                        double y = 282.28619384765625;
                        x =  (12697074.245 + (x/7.850));
                        y =  (2588966.542 - (y/7.850));
                        double[] doubles = CoordinateUtils.mercator2Lonlat(x, y);
                        latLng.setLatitude(doubles[1]);
                        latLng.setLongitude(doubles[0]);
                        if (isStartNavi == true) {
                            double distance = testCalcDistance(latLng);
                            if (distance < nearLength) {
                                latLng = getMinLatLng(latLng);
                            } else if (distance > 15){
                                mFindCarNativeView.rePlanRoute(x,y);
                            }
                            mFindCarNativeView.showLocationIcon(latLng.getLatitude(),latLng.getLongitude(),x,y);
                            startNaviEngine(latLng.getLatitude(),latLng.getLongitude());
                        }else {
                            mFindCarNativeView.showLocationIcon(latLng.getLatitude(),latLng.getLongitude(),x,y);
                        }
                        mFindCarNativeView.setLocationSuccess(true);
                    }
                });
            }
        });
        client.start();
    }*/

    private double testCalcDistance(LatLng latLng) {
        if (routes == null || routes.size() == 0 || null == latLng) {
            //toast("testCalcDistance错误 ！ ！ ！");
            return 0;
        }
        double[] ps = DataConvertUtils.INSTANCE.latlng2WebMercator(latLng.getLatitude(), latLng.getLongitude());

        double minDistance = Double.MAX_VALUE;

        GeometryFactory factory = new GeometryFactory();

        Point point = factory.createPoint(new com.vividsolutions.jts.geom.Coordinate(ps[0], ps[1]));

        //com.vividsolutions.jts.geom.LineString lineString = null;
        for (AStarPath aStarPath : routes) {
            AStarVertex from = aStarPath.getFrom();
            AStarVertex to = aStarPath.getTo();
            com.vividsolutions.jts.geom.LineString line =
                    factory.createLineString(new com.vividsolutions.jts.geom.Coordinate[]{
                            new com.vividsolutions.jts.geom.Coordinate(((Point) from.getVertex().getShape()).getX(), ((Point) from.getVertex().getShape()).getY()),
                            new com.vividsolutions.jts.geom.Coordinate(((Point) to.getVertex().getShape()).getX(), ((Point) to.getVertex().getShape()).getY())
                    });

            double d = line.distance(point);
            Log.e(TAG, "计算出HEHEH: " + d);
            if (d < minDistance) {
                minDistance = d;
                //lineString = line;
            }
        }
        return minDistance;
    }
    private LatLng getMinLatLng(LatLng latLng) {
        if (routes == null || routes.size() == 0 || null == latLng) {
            return latLng;
        }
        double[] ps = DataConvertUtils.INSTANCE.latlng2WebMercator(latLng.getLatitude(), latLng.getLongitude());

        double minDistance = Double.MAX_VALUE;

        GeometryFactory factory = new GeometryFactory();

        Point point = factory.createPoint(new com.vividsolutions.jts.geom.Coordinate(ps[0], ps[1]));
        com.vividsolutions.jts.geom.LineString lineString = null;

        for (AStarPath aStarPath : routes) {
            AStarVertex from = aStarPath.getFrom();
            AStarVertex to = aStarPath.getTo();
            com.vividsolutions.jts.geom.LineString line =
                    factory.createLineString(new com.vividsolutions.jts.geom.Coordinate[]{
                            new com.vividsolutions.jts.geom.Coordinate(((Point) from.getVertex().getShape()).getX(), ((Point) from.getVertex().getShape()).getY()),
                            new com.vividsolutions.jts.geom.Coordinate(((Point) to.getVertex().getShape()).getX(), ((Point) to.getVertex().getShape()).getY())
                    });

            double d = line.distance(point);
            Log.e(TAG, "计算出HAAH: " + d);
            if (d < minDistance) {
                minDistance = d;
                lineString = line;
            }
        }
        DistanceOp d = new DistanceOp(point, lineString);
        double[] result = null;
        com.vividsolutions.jts.geom.Coordinate[] nearestPoints = d.nearestPoints();
        if (nearestPoints != null && nearestPoints.length > 1) {
            com.vividsolutions.jts.geom.Coordinate p = nearestPoints[1];
            result = DataConvertUtils.INSTANCE.webMercator2LatLng(p.x, p.y);
        }
        return new LatLng(result[0],result[1]);
    }

    @Override
    public boolean canParkCar(int poiId) {
        return noParkingCars.contains(poiId);
    }

    @Override
    public void setRouteLineData(List<AStarPath> routes) {
        this.routes = routes;
    }

    @Override
    public void setAcceData(float[] acceData) {
        pos.setAccelerotate(acceData);
    }

    private LatLng newLatLng;
    private void startNaviEngine(double x,double y) {
        newLatLng = new LatLng(x,y);
        destationDistance = newLatLng.distanceTo(endPoint);
        if (!routeFinished && destationDistance>6) {
            CameraPosition position = new CameraPosition.Builder()
                    .target(newLatLng)
                    .bearing(0) // Rotate the camera
                    .tilt(45)
                    .build();
            mFindCarNativeView.updateMapCamera(position);
        }else {
            routeFinished = true;
            routes = null;
            isStartNavi = false;
            mFindCarNativeView.clearRoute();
            mFindCarNativeView.resetBeforeNavi();
            mFindCarNativeView.saveParkingInfo();
        }
    }
}
