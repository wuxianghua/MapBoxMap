package com.org.mylibrary.yichemap.view;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerView;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.services.commons.geojson.Feature;
import com.mapbox.services.commons.geojson.FeatureCollection;
import com.org.mylibrary.R;
import com.org.mylibrary.indoor.IMapViewController;
import com.org.mylibrary.indoor.MapViewControllerFactory;
import com.org.mylibrary.indoor.Utils;
import com.org.mylibrary.indoor.impl.MapBoxMapViewController;
import com.org.mylibrary.indoor.navigate.INavigateManager;
import com.org.mylibrary.indoor.navigate.impl.MapBoxNavigateManager;
import com.org.mylibrary.yichemap.mode.LocationInfo;
import com.org.mylibrary.yichemap.presenter.FindCarNativePresenter;
import com.org.mylibrary.yichemap.presenter.FindCarNativePresenterImpl;
import com.org.mylibrary.yichemap.utils.SharedPreferenceUtil;
import com.org.mylibrary.yichemap.utils.ThreadManager;
import com.palmap.astar.navi.AStarPath;
import com.palmap.core.MapEngine;
import com.palmap.core.data.PlanarGraph;
import com.palmap.core.overLayer.PulseMarkerViewOptions;
import com.palmap.nagrand.support.util.DataConvertUtils;
import com.palmaplus.nagrand.geos.Coordinate;
import com.vividsolutions.jts.io.ParseException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.palmap.core.util.UtilsKt.loadFromAsset;

/**
 * Created by Administrator on 2017/10/10/010.
 */

public class FindCarNativeActivity extends BaseActivity implements FindCarNativeView,SensorEventListener {
    String TAG = FindCarNativeActivity.class.getSimpleName();
    //地图容器
    private LinearLayout mapViewLayout;
    //显示占用车位
    private TextView mTvCarNum;
    //显示空余车位
    private TextView mTvNoCarNum;
    //导航起点和终点
    private Coordinate start, end;
    //定位点
    private Coordinate mLocation;
    private LatLng mLocationLatLng;
    //路线规划
    private MapBoxNavigateManager navigateManager;
    //presenter
    private FindCarNativePresenter mFindCarNativePresenter;
    private IMapViewController iMapViewController;
    //导航线数据
    private FeatureCollection routeFeatureCollection;
    //是否可以显示定位点
    private boolean isCanShowLocationIcon = false;
    //模拟导航
    private Button startMovcNavi;
    //实际导航
    private Button startNavi;
    //停这里
    private Button findCarEnsure;
    //返回
    private Button findCarBack;
    //停车
    private LinearLayout findCar;
    //找车
    private LinearLayout stopCar;
    //展示车位信息
    private LinearLayout showCarInfo;
    //车位名称
    private TextView showCarName;
    //停车返回
    private Button parkCarBack;
    //是否设置完终点
    private boolean isHaveSetEnd;
    //选择起点弹框
    private RelativeLayout showSelectStart;
    //设为起点
    private Button setStartNormal,setStartSelect;
    //展示导航信息
    private LinearLayout mShowNaviInfo;
    //展示终点信息
    private TextView mShowDestination;
    //终点车位名称
    private String carName;
    //路径规划信息
    private LinearLayout routePlanInfo;
    //终点信息
    private TextView mDestationInfo;
    //是否是模拟导航
    private boolean isMovcNavi;
    //起点和终点Mark
    private MarkerView startMark;
    private MarkerView endMark;
    //定位按钮图标
    private ImageView mLocationButton;
    //传感器数据
    private float[] accelerometerValues = new float[3];
    private float[] magneticFieldValues = new float[3];
    //加速度传感器
    private Sensor mAccelerometer;
    //磁力计传感器
    private Sensor mField;
    //传感器管理器
    private SensorManager mSensorManager;
    //定位图标是否已经显示
    private boolean isShown;
    //是否已经开始模拟导航
    private boolean isStartMovcNavi;
    //2d和3d模式切换
    private TextView mapStyleChange;
    //导航结束提示
    private LinearLayout mHintEndNavi;
    //是否已经开始导航
    private boolean isStartNavi;
    public static PlanarGraph parkData;
    //移动地图
    private Button moveMapLocation;
    private Bundle mSavedInstanceState;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_findcar_native);
        mSavedInstanceState = savedInstanceState;
        initData();
        initView();
        registerSensor();
    }

    private void initData() {
        showDialog();
        ThreadManager.getNormalPool().execute(new Runnable() {
            @Override
            public void run() {
                if (parkData == null) {
                    parkData = new PlanarGraph(loadFromAsset(getApplicationContext(),"h2Date.json"),16);
                }
                parkData.resolveData();
                parkData.loadStyle(
                        MapEngine.INSTANCE.getRenderableByName("default").getRenderer()
                );
                Message message = Message.obtain();
                message.what = 7;
                handler.sendMessage(message);
            }
        });
    }

    //注册传感器
    private void registerSensor() {
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mField, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public static void navigatorThis(Context that) {
        Intent intent = new Intent(that, FindCarNativeActivity.class);
        that.startActivity(intent);
    }

    //初始化地图界面
    private void initMapView(@Nullable Bundle savedInstanceState) {
        iMapViewController = MapViewControllerFactory.createByMapBox(this);
        iMapViewController.attachMap(mapViewLayout, savedInstanceState, new IMapViewController.Action() {
            @Override
            public void onAction() {
                iMapViewController.drawPlanarGraph(parkData);
                iMapViewController.setLocationMarkIcon(R.mipmap.ico_map_location,130,130);
                touchMapView();
            }
        });
        navigateManager = new MapBoxNavigateManager(this, "roadNet.json");
        navigateManager.setNavigateListener(new INavigateManager.Listener<FeatureCollection>() {
            @Override
            public void OnNavigateComplete(INavigateManager.NavigateState state, List<AStarPath> routes, FeatureCollection route) {
                if (state == INavigateManager.NavigateState.OK) {
                    mFindCarNativePresenter.setRouteLineData(routes);
                    showRoute(route);
                }
            }
        });
    }

    private LatLng selectMainParkingPos(String carName) {
        feature = iMapViewController.selectFeature(carName,parkData);
        if (feature == null) {
            Toast.makeText(FindCarNativeActivity.this,"你搜索的车位不存在",Toast.LENGTH_SHORT).show();
            return null;
        }
        LatLng featureCentroid = null;
        try {
            featureCentroid = Utils.getFeatureCentroid(feature);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return featureCentroid;
    }
    //开始模拟导航
    public void startMovcNavi(View view) {
        isHaveSetEnd = false;
        isMovcNavi = true;
        isStartMovcNavi = true;
        iMapViewController.updateLocationOrientation(0);
        startMovcNavi.setVisibility(View.GONE);
        startNavi.setVisibility(View.GONE);
        routePlanInfo.setVisibility(View.GONE);
        parkCarBack.setVisibility(View.GONE);
        showCarInfo.setVisibility(View.GONE);
        findCarEnsure.setVisibility(View.GONE);
        findCarBack.setVisibility(View.GONE);
        mShowNaviInfo.setVisibility(View.VISIBLE);
        mShowDestination.setText("终点"+carName);
        if (start == null || routeFeatureCollection == null) {
            Toast.makeText(FindCarNativeActivity.this,"先添加导航线",Toast.LENGTH_SHORT).show();
        }
        mFindCarNativePresenter.startMovcNavi(routeFeatureCollection,mLocationLatLng);
    }

    //开始导航
    public void startNavi(View view) {
        if (mLocationLatLng.getLongitude() == 0||mLocationLatLng.getLatitude() == 0) {
            Toast.makeText(FindCarNativeActivity.this,"没有定位，无法实际导航",Toast.LENGTH_SHORT).show();
            return;
        }
        if (start == null || routeFeatureCollection == null) {
            Toast.makeText(FindCarNativeActivity.this,"先添加导航线",Toast.LENGTH_SHORT).show();
            return;
        }
        isMovcNavi = false;
        isHaveSetEnd = false;
        isStartNavi = true;
        startMovcNavi.setVisibility(View.GONE);
        startNavi.setVisibility(View.GONE);
        routePlanInfo.setVisibility(View.GONE);
        parkCarBack.setVisibility(View.GONE);
        showCarInfo.setVisibility(View.GONE);
        findCarEnsure.setVisibility(View.GONE);
        findCarBack.setVisibility(View.GONE);
        mShowNaviInfo.setVisibility(View.VISIBLE);
        mShowDestination.setText("终点"+carName);
        mFindCarNativePresenter.startNavi(routeFeatureCollection,mLocationLatLng);
    }

    //找车
    public void findCar(View view) {
        Intent intent = new Intent(this,SearchResultActivity.class);
        startActivityForResult(intent,1);
    }

    //停车返回
    public void parkCarBack(View view) {
        parkCarBack.setVisibility(View.GONE);
        startMovcNavi.setVisibility(View.GONE);
        startNavi.setVisibility(View.GONE);
        routePlanInfo.setVisibility(View.GONE);
        isHaveSetEnd = false;
        if (startMark != null) {
            ((MapBoxMapViewController) iMapViewController).getMapBox().removeMarker(startMark);
        }
        if (endMark != null) {
            ((MapBoxMapViewController) iMapViewController).getMapBox().removeMarker(endMark);
        }
        if (routeFeatureCollection != null) {
            ((MapBoxMapViewController) iMapViewController).showRoute(null);
        }
        findCar.setVisibility(View.VISIBLE);
        stopCar.setVisibility(View.VISIBLE);
    }

    //停车
    public void stopCar(View view) {
        parkCarBack.setVisibility(View.VISIBLE);
        stopCar.setVisibility(View.GONE);
        findCar.setVisibility(View.GONE);
        Toast.makeText(FindCarNativeActivity.this,"请在图上选择绿色车位",Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1) {
            String carNum = data.getStringExtra("carNum");
            carName = carNum;
            feature = iMapViewController.selectFeature(carNum,parkData);
            if (feature == null) {
                Toast.makeText(FindCarNativeActivity.this,"你搜索的车位不存在",Toast.LENGTH_SHORT).show();
                return;
            }
            if (endMark != null) {
                ((MapBoxMapViewController) iMapViewController).getMapBox().removeMarker(endMark);
            }
            LatLng featureCentroid = null;
            try {
                featureCentroid = Utils.getFeatureCentroid(feature);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            double[] doubles = DataConvertUtils.INSTANCE.latlng2WebMercator(featureCentroid.getLatitude(), featureCentroid.getLongitude());
            end = new Coordinate(doubles[0], doubles[1]);
            boolean onKey = data.getBooleanExtra("onKey",false);
            findCar.setVisibility(View.GONE);
            stopCar.setVisibility(View.GONE);
            if (onKey) {
                endMark = ((MapBoxMapViewController) iMapViewController).getMapBox().addMarker(new PulseMarkerViewOptions()
                        .position(featureCentroid)
                        .icon(IconFactory.getInstance(FindCarNativeActivity.this).fromResource(R.mipmap.parkcaricon))
                );
                if (mLocation.getX() == 0 || mLocation.getY() == 0) {
                    showSelectStart.setVisibility(View.VISIBLE);
                    return;
                }
                start = mLocation;
                navigateManager.navigation(
                        mLocation.getX(),
                        mLocation.getY(),
                        iMapViewController.getFloorId(),
                        end.x,
                        end.y,
                        iMapViewController.getFloorId()
                );
            }else {
                findCarBack.setVisibility(View.VISIBLE);
                findCarEnsure.setVisibility(View.VISIBLE);
                showCarInfo.setVisibility(View.VISIBLE);
                showCarName.setText(carNum);
                mFindCarNativePresenter.showSearchPark(featureCentroid);
                endMark = ((MapBoxMapViewController) iMapViewController).getMapBox().addMarker(new PulseMarkerViewOptions()
                        .position(featureCentroid)
                        .icon(IconFactory.getInstance(FindCarNativeActivity.this).fromResource(R.mipmap.parkcaricon))
                );
            }
        }
    }

    private void showRoute(FeatureCollection route) {
        if (!isStartNavi) {
            routeFeatureCollection = route;
            startMovcNavi.setVisibility(View.VISIBLE);
            startNavi.setVisibility(View.VISIBLE);
            routePlanInfo.setVisibility(View.VISIBLE);
            mDestationInfo.setText(carName);
            parkCarBack.setVisibility(View.VISIBLE);
            showCarInfo.setVisibility(View.GONE);
        }
        ((MapBoxMapViewController) iMapViewController).showRoute(route);
    }
    //点击地图
    Feature feature; //点击处的feature
    int category; //点击处的feature的category
    int poiId;   //点击处的feature的poiId
    private void touchMapView() {
        iMapViewController.setOnSingTapListener(new IMapViewController.onSingTapListener() {
            @Override
            public void onAction(final double x, final double y) {
            if (isHaveSetEnd) {
                if (startMark != null) {
                    ((MapBoxMapViewController) iMapViewController).getMapBox().removeMarker(startMark);
                }
                setStartNormal.setVisibility(View.GONE);
                setStartSelect.setVisibility(View.VISIBLE);
                double xy2[] = DataConvertUtils.INSTANCE.latlng2WebMercator(x, y);
                start = new Coordinate(xy2[0], xy2[1]);
                startMark = ((MapBoxMapViewController) iMapViewController).getMapBox().addMarker(new PulseMarkerViewOptions()
                        .position(new LatLng(x, y))
                        .icon(IconFactory.getInstance(FindCarNativeActivity.this).fromResource(R.mipmap.ico_marker_start))
                );
                return;
            }else {
                feature = iMapViewController.selectFeature(x, y);
                if (feature == null) return;
                category = feature.getNumberProperty("category").intValue();
                if(category == 22001000||category == 22002000||category == 22003000||category ==22004000) {
                    poiId = feature.getNumberProperty("id").intValue();
                    if (!mFindCarNativePresenter.canParkCar(poiId)) {
                        Toast.makeText(FindCarNativeActivity.this,"请点击绿色车位",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (endMark != null) {
                        ((MapBoxMapViewController) iMapViewController).getMapBox().removeMarker(endMark);
                    }
                    parkCarBack.setVisibility(View.GONE);
                    findCar.setVisibility(View.GONE);
                    stopCar.setVisibility(View.GONE);
                    showCarInfo.setVisibility(View.VISIBLE);
                    carName = feature.getProperties().get("name").getAsString();
                    showCarName.setText(carName);
                    findCarBack.setVisibility(View.VISIBLE);
                    findCarEnsure.setVisibility(View.VISIBLE);
                    double xy2[] = DataConvertUtils.INSTANCE.latlng2WebMercator(x, y);
                    start = mLocation;
                    end = new Coordinate(xy2[0], xy2[1]);
                    endMark = ((MapBoxMapViewController) iMapViewController).getMapBox().addMarker(new PulseMarkerViewOptions()
                            .position(new LatLng(x, y))
                            .icon(IconFactory.getInstance(FindCarNativeActivity.this).fromResource(R.mipmap.parkcaricon))
                    );
                }
            }
            }
        });
    }

    //初始化view
    private void initView() {
        mapViewLayout = (LinearLayout) findViewById(R.id.layout_mapView);
        mTvNoCarNum = (TextView) findViewById(R.id.tv_nocar_num);
        mTvCarNum = (TextView) findViewById(R.id.tv_car_num);
        startNavi = (Button) findViewById(R.id.start_navi);
        startMovcNavi = (Button) findViewById(R.id.start_movc_navi);
        findCarEnsure = (Button) findViewById(R.id.find_car_ensure);
        findCarBack = (Button) findViewById(R.id.find_car_back);
        findCar = (LinearLayout) findViewById(R.id.find_car);
        stopCar = (LinearLayout) findViewById(R.id.stop_car);
        showCarInfo = (LinearLayout) findViewById(R.id.show_car_info);
        showCarName = (TextView) findViewById(R.id.show_car_name);
        parkCarBack = (Button) findViewById(R.id.park_car_back);
        showSelectStart = (RelativeLayout) findViewById(R.id.show_select_start);
        setStartNormal = (Button) findViewById(R.id.set_start_point_normal);
        setStartSelect = (Button) findViewById(R.id.set_start_point_select);
        mShowDestination = (TextView) findViewById(R.id.show_destination_info);
        mShowNaviInfo = (LinearLayout) findViewById(R.id.show_navi_info);
        routePlanInfo = (LinearLayout) findViewById(R.id.plan_route_info);
        mDestationInfo = (TextView) findViewById(R.id.destation_name);
        mLocationButton = (ImageView) findViewById(R.id.map_location);
        mapStyleChange = (TextView) findViewById(R.id.map_style_change);
        mHintEndNavi = (LinearLayout) findViewById(R.id.show_hint_end_navi);
        moveMapLocation = (Button) findViewById(R.id.map_location_change);
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mField = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mLocation = new Coordinate();
        mLocationLatLng = new LatLng();
    }

    //停这里
    public void findCarEnsure(View view) {
        if (mLocation.getX() == 0||mLocation.getY() == 0) {
            showSelectStart.setVisibility(View.VISIBLE);
            return;
        }
        findCarBack.setVisibility(View.GONE);
        findCarEnsure.setVisibility(View.GONE);
        start = mLocation;
        navigateManager.navigation(
                start.x,
                start.y,
                iMapViewController.getFloorId(),
                end.x,
                end.y,
                iMapViewController.getFloorId()
        );
    }

    @Override
    public void addParkingCarLayer(final FeatureCollection featureCollection) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((MapBoxMapViewController) iMapViewController).showCarHover(featureCollection);
            }
        });
        Log.e("xinhua","iscanshowLocation"+isCanShowLocationIcon);
        isCanShowLocationIcon = true;
    }

    @Override
    public void addInvalidParkingCarLayer(final FeatureCollection featureCollection) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((MapBoxMapViewController) iMapViewController).showCarPark(featureCollection);
            }
        });
        Log.e("xinhua","iscanshowLocation"+isCanShowLocationIcon);
        isCanShowLocationIcon = true;
    }

    @Override
    public void changeCarParkingNum(final int carNum, final int noCarNum) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTvCarNum.setText(carNum+"");
                mTvNoCarNum.setText(noCarNum+"");
            }
        });
    }

    @Override
    public void showLocationIcon(LocationInfo locationInfo) {
        if (isCanShowLocationIcon) {
            mLocation.setX(locationInfo.x);
            mLocation.setY(locationInfo.y);
            mLocationLatLng.setLatitude(locationInfo.latitude);
            mLocationLatLng.setLongitude(locationInfo.logtitude);
            Message message = Message.obtain();
            message.what = 2;
            if (bundle == null) {
                bundle = new Bundle();
            }
            bundle.putParcelable("mLocationLatLng",mLocationLatLng);
            message.setData(bundle);
            handler.sendMessage(message);
            if (isShown == false) {
                isShown = true;
            }
        }
    }

    Bundle bundle;
    /*@Override
    public void showLocationIcon(double var1, double var2, double var3, double var4) {
        if (isCanShowLocationIcon) {
            mLocation.setX(var3);
            mLocation.setY(var4);
            mLocationLatLng.setLatitude(var1);
            mLocationLatLng.setLongitude(var2);
            Message message = Message.obtain();
            message.what = 2;
            if (bundle == null) {
                bundle = new Bundle();
            }
            bundle.putParcelable("mLocationLatLng",mLocationLatLng);
            message.setData(bundle);
            handler.sendMessage(message);
            if (isShown == false) {
                isShown = true;
            }
        }
    }
*/
    @Override
    public void updateMapCamera(final CameraPosition position) {
        Message message = Message.obtain();
        message.what = 3;
        if (bundle == null) {
            bundle = new Bundle();
        }
        bundle.putParcelable("position",position);
        message.setData(bundle);
        handler.sendMessage(message);
    }

    @Override
    public void clearRoute() {
        Message message = Message.obtain();
        message.what = 4;
        handler.sendMessage(message);
        if (isStartMovcNavi) {
            isStartMovcNavi = false;
        }
        if (isStartNavi) {
            isStartNavi = false;
        }
    }

    @Override
    public void addLocationMark(LatLng location) {
        iMapViewController.addLocationMark(location.getLatitude(),location.getLongitude());
    }

    @Override
    public void resetBeforeNavi() {
        Message message = Message.obtain();
        message.what = 5;
        handler.sendMessage(message);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

            }
        });
    }

    @Override
    public void setLocationSuccess(final boolean isLocation) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLocationButton.setSelected(isLocation);
            }
        });
    }

    @Override
    public void rePlanRoute(final double x, final double y) {
        Message message = Message.obtain();
        message.what = 1;
        Bundle bundle = new Bundle();
        bundle.putDouble("x",x);
        bundle.putDouble("y",y);
        message.setData(bundle);
        handler.sendMessage(message);
    }
    SimpleDateFormat df;
    String parkTime;
    @Override
    public void saveParkingInfo() {
        if (df == null) {
            df = new SimpleDateFormat("MM-dd HH:mm");
        }
        parkTime = df.format(new Date());
        SharedPreferenceUtil.putValue(this,"parkinfo","parkName",carName);
        SharedPreferenceUtil.putValue(this,"parkinfo","parkTime",parkTime);
    }

    //选择起点
    public void selectStart(View view) {
        findCarEnsure.setVisibility(View.GONE);
        setStartNormal.setVisibility(View.VISIBLE);
        showSelectStart.setVisibility(View.GONE);
        if(findCar.getVisibility() == View.VISIBLE) {
            findCar.setVisibility(View.GONE);
        }
        if(stopCar.getVisibility() == View.VISIBLE) {
            stopCar.setVisibility(View.GONE);
        }
        if (showCarInfo.getVisibility() == View.GONE) {
            showCarInfo.setVisibility(View.VISIBLE);
        }
        if (findCarBack.getVisibility() == View.GONE) {
            findCarBack.setVisibility(View.VISIBLE);
        }
        isHaveSetEnd = true;
    }
    //取消
    public void cancleSelect(View view) {
        showSelectStart.setVisibility(View.GONE);
    }
    //设为起点
    public void setStartPoint(View view) {
        setStartSelect.setVisibility(View.GONE);
        findCarBack.setVisibility(View.GONE);
        navigateManager.navigation(
                start.x,
                start.y,
                iMapViewController.getFloorId(),
                end.x,
                end.y,
                iMapViewController.getFloorId()
        );
    }

    public void findCarBack(View view) {
        showCarInfo.setVisibility(View.GONE);
        findCarBack.setVisibility(View.GONE);
        findCarEnsure.setVisibility(View.GONE);
        findCar.setVisibility(View.VISIBLE);
        stopCar.setVisibility(View.VISIBLE);
        setStartNormal.setVisibility(View.GONE);
        setStartSelect.setVisibility(View.GONE);
        isHaveSetEnd = false;
        if (endMark != null) {
            ((MapBoxMapViewController) iMapViewController).getMapBox().removeMarker(endMark);
        }
        if (startMark != null) {
            ((MapBoxMapViewController) iMapViewController).getMapBox().removeMarker(startMark);
        }
    }
    //结束导航
    public void endNavi(View view) {
        if (isMovcNavi) {
            mFindCarNativePresenter.stopMovcNavi();
            iMapViewController.addLocationMark(0,0);
            if (isStartMovcNavi) {
                isStartMovcNavi = false;
            }
        }else {
            if (isStartNavi) {
                isStartNavi = false;
            }
            mFindCarNativePresenter.stopNavi();
        }
        if (endMark != null) {
            ((MapBoxMapViewController) iMapViewController).getMapBox().removeMarker(endMark);
        }
        if (startMark != null) {
            ((MapBoxMapViewController) iMapViewController).getMapBox().removeMarker(startMark);
        }
        mShowNaviInfo.setVisibility(View.GONE);
        ((MapBoxMapViewController) iMapViewController).showRoute(null);
        findCar.setVisibility(View.VISIBLE);
        stopCar.setVisibility(View.VISIBLE);
    }
    //定位
    public void location(View view) {
        Log.e(TAG,"我被点击了");
        ((MapBoxMapViewController) iMapViewController).getMapBox().animateCamera(CameraUpdateFactory.newLatLng(locationLatLng),2000);
        mFindCarNativePresenter.getLocation();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accelerometerValues = event.values;
            if(mFindCarNativePresenter != null) {
                mFindCarNativePresenter.setAcceData(accelerometerValues);
            }
        }else if(event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
        }else {
            magneticFieldValues = event.values;
        }
        ThreadManager.getNormalPool().execute(new Runnable() {
            @Override
            public void run() {
                calculateOrientation();
            }
        });
    }

    float degree;
    float oldDegree;
    long currentTime;
    long oldTime;
    private void calculateOrientation() {
        float[] values = new float[3];
        float[] R = new float[9];
        SensorManager.getRotationMatrix(R, null, accelerometerValues,
                magneticFieldValues);
        SensorManager.getOrientation(R, values);
        values[0] = (float) Math.toDegrees(values[0]);
        values[0] = values[0] < 0 ? 360+values[0]:values[0];
        currentTime = System.currentTimeMillis();
        degree =  values[0];
        if (isShown && !isStartMovcNavi) {
            if (Math.abs(degree - oldDegree) > 1 && currentTime - oldTime > 100)  {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        iMapViewController.updateLocationOrientation(degree);
                    }
                });
                oldTime = currentTime;
            }
            oldDegree = degree;
        }
    }
    double x;
    double y;
    CameraPosition position;
    LatLng locationLatLng;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                x = msg.getData().getDouble("x");
                y = msg.getData().getDouble("y");
                Toast.makeText(FindCarNativeActivity.this,"已偏离航线，正在为您重新规划路线",Toast.LENGTH_SHORT).show();
                navigateManager.navigation(
                        x,
                        y,
                        iMapViewController.getFloorId(),
                        end.x,
                        end.y,
                        iMapViewController.getFloorId()
                );
            }else if (msg.what == 2) {
                locationLatLng = msg.getData().getParcelable("mLocationLatLng");
                iMapViewController.addLocationMark(locationLatLng.getLatitude(),locationLatLng.getLongitude());
            }else if (msg.what == 3) {
                position = msg.getData().getParcelable("position");
                ((MapBoxMapViewController) iMapViewController).getMapBox().animateCamera(CameraUpdateFactory
                        .newCameraPosition(position), 500);
            }else if (msg.what == 4) {
                mHintEndNavi.setVisibility(View.VISIBLE);
                CameraPosition position = new CameraPosition.Builder()
                        .tilt(0)
                        .build();
                ((MapBoxMapViewController) iMapViewController).getMapBox().animateCamera(CameraUpdateFactory.newCameraPosition(position));
                ((MapBoxMapViewController) iMapViewController).showRoute(null);
            }else if (msg.what == 5) {
                findCar.setVisibility(View.VISIBLE);
                stopCar.setVisibility(View.VISIBLE);
                mShowNaviInfo.setVisibility(View.GONE);
                if (endMark != null) {
                    ((MapBoxMapViewController) iMapViewController).getMapBox().removeMarker(endMark);
                }
                if (startMark != null) {
                    ((MapBoxMapViewController) iMapViewController).getMapBox().removeMarker(startMark);
                }
                iMapViewController.addLocationMark(0,0);
            }else if (msg.what == 7) {
                dismissDialog();
                mFindCarNativePresenter = new FindCarNativePresenterImpl(parkData);
                initMapView(mSavedInstanceState);
                mFindCarNativePresenter.attachView(FindCarNativeActivity.this,FindCarNativeActivity.this);
                mFindCarNativePresenter.onCreate();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    //放大
    public void mapZoomInClick(View view) {
        ((MapBoxMapViewController) iMapViewController).getMapBox().animateCamera(CameraUpdateFactory.zoomIn());
    }
    //缩小
    public void mapZoomOutClick(View view) {
        ((MapBoxMapViewController) iMapViewController).getMapBox().animateCamera(CameraUpdateFactory.zoomOut());
    }
    //2d和3d模式切换
    private boolean is2DMode;
    public void changeMapMode(View view) {
        if (is2DMode) {
            is2DMode = false;
            mapStyleChange.setText("3D");
            CameraPosition position = new CameraPosition.Builder()
                    .tilt(60)
                    .build();
            ((MapBoxMapViewController) iMapViewController).getMapBox().animateCamera(CameraUpdateFactory.newCameraPosition(position));
        }else {
            is2DMode = true;
            mapStyleChange.setText("2D");
            CameraPosition position = new CameraPosition.Builder()
                    .tilt(0)
                    .build();
            ((MapBoxMapViewController) iMapViewController).getMapBox().animateCamera(CameraUpdateFactory.newCameraPosition(position));
        }
    }
    //确认导航结束
    public void ensureStopNavi(View view) {
        mHintEndNavi.setVisibility(View.GONE);
    }

    public void moveToH1(View view) {
        position = new CameraPosition.Builder()
                .target(selectMainParkingPos("H1大堂"))
                .zoom(18)
                .build();
        ((MapBoxMapViewController) iMapViewController).getMapBox().animateCamera(CameraUpdateFactory.newCameraPosition(position),1000);
        popWnd.dismiss();
        moveMapLocation.setText("H1");
    }

    public void moveToH2(View view) {
       position = new CameraPosition.Builder()
                .target(selectMainParkingPos("H2大堂"))
                .zoom(18)
                .build();
        ((MapBoxMapViewController) iMapViewController).getMapBox().animateCamera(CameraUpdateFactory.newCameraPosition(position),1000);
        popWnd.dismiss();
        moveMapLocation.setText("H2");
    }

    public void moveToH3(View view) {
        position = new CameraPosition.Builder()
                .target(selectMainParkingPos("H3大堂"))
                .zoom(18)
                .build();
        ((MapBoxMapViewController) iMapViewController).getMapBox().animateCamera(CameraUpdateFactory.newCameraPosition(position),1000);
        popWnd.dismiss();
        moveMapLocation.setText("H3");
    }
    View contentView;
    PopupWindow popWnd;
    public void moveMap(View view) {
        if (contentView == null) {
            contentView = LayoutInflater.from(FindCarNativeActivity.this).inflate(R.layout.popwindow, null);
        }
        if (popWnd == null) {
            popWnd = new PopupWindow(FindCarNativeActivity.this);
            popWnd.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_popwindow));
            popWnd.setContentView(contentView);
            popWnd.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
            popWnd.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        popWnd.showAsDropDown(moveMapLocation);
    }
}
