package com.org.mylibrary.yichemap.utils;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;
import com.google.gson.Gson;
import com.org.mylibrary.yichemap.http.CarServiceFactory;
import com.org.mylibrary.yichemap.http.GetPositionInfoService;
import com.org.mylibrary.yichemap.mode.BeaconInfo;
import com.org.mylibrary.yichemap.mode.PositionInfo;
import com.org.mylibrary.yichemap.mode.PositionResult;
import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2017/11/10/010.
 */

public class PositionUtil implements BeaconConsumer, RangeNotifier {
    private final String UUID_NUM = "FDA50693-A4E2-4FB1-AFCF-C6EB07647825";
    private final int MAJOR_NUM = 10169;
    private BeaconManager beaconManager;
    private Handler handler;
    private Context mContext;
    private List<Region> regions = new ArrayList<>();
    private List<BeaconInfo> beacons;
    private BeaconInfo beaconInfo;
    private PositionInfo positionInfo;
    private String devicesMac;
    private Gson gson;
    private OnUpdateLocationData mOnUpdateLocationData;
    //初始化
    public PositionUtil(Context context) {
        mContext = context;
        handler = new Handler();
        initBeaconManager(context);
        initView();
    }

    public void start() {
        registerBeaconInfo();
    }

    private void initView() {
        beacons = new ArrayList<>();
        positionInfo = new PositionInfo();
        devicesMac = GetDeviceUtil.getAdressMAC(mContext);
        gson = new Gson();
        tempBeacons = new ArrayList<>();
    }

    private void initBeaconManager(Context context) {
        beaconManager = BeaconManager.getInstanceForApplication(context.getApplicationContext());
        beaconManager.getBeaconParsers().add(new BeaconParser()
                .setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.setForegroundScanPeriod(1000);
        beaconManager.bind(this);
    }

    private void registerBeaconInfo() {
        Region r = new Region(
                "",
                Identifier.parse(UUID_NUM),
                Identifier.fromInt(MAJOR_NUM),
                null
        );
        regions.add(r);

            try {
                if (beaconManager.isBound(this)) {
                    beaconManager.startRangingBeaconsInRegion(r);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
    }
    private float[] acce;
    public void setAccelerotate(float[] accelerometerValues) {
        acce = accelerometerValues;
    }

    @Override
    public void onBeaconServiceConnect() {
        try {
            beaconManager.addRangeNotifier(this);
            for (Region r : this.regions) {
                beaconManager.startRangingBeaconsInRegion(r);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Context getApplicationContext() {
        return mContext.getApplicationContext();
    }

    @Override
    public void unbindService(ServiceConnection serviceConnection) {
        mContext.unbindService(serviceConnection);
    }

    @Override
    public boolean bindService(Intent intent, ServiceConnection serviceConnection, int i) {
        return mContext.bindService(intent, serviceConnection, i);
    }

    private List<BeaconInfo> tempBeacons;
    @Override
    public void didRangeBeaconsInRegion(final Collection<Beacon> collection, Region region) {
        ThreadManager.getDownloadPool().execute(new Runnable() {
            @Override
            public void run() {
                beacons.clear();
                tempBeacons.clear();
                if (collection.size() == 0) {
                    return;
                }
                for (Beacon beacon : collection) {
                    beaconInfo = new BeaconInfo();
                    beaconInfo.mac = beacon.getId2()+":"+beacon.getId3();
                    beaconInfo.accuracy = beacon.getDistance();
                    beaconInfo.rssi = beacon.getRssi();
                    beaconInfo.txpower = beacon.getTxPower();
                    beacons.add(beaconInfo);
                }
                Collections.sort(beacons, new Comparator<BeaconInfo>() {
                    @Override
                    public int compare(BeaconInfo beaconInfo, BeaconInfo beaconInfo1) {
                        return beaconInfo1.rssi - beaconInfo.rssi;
                    }
                });
                if (beacons.size() > 10) {
                    for (int i = 0; i < 10; i++) {
                        tempBeacons.add(beacons.get(i));
                    }
                    positionInfo.ble = tempBeacons;
                    positionInfo.userid = devicesMac;
                    positionInfo.sys = 0;
                    positionInfo.rsp = 1;
                    positionInfo.move = getAcceResult() < 0.48 ? 0 : 1;
                }else {
                    positionInfo.ble = beacons;
                    positionInfo.userid = devicesMac;
                    positionInfo.sys = 0;
                    positionInfo.rsp = 1;
                    positionInfo.move = getAcceResult() < 0.48 ? 0 : 1;
                }
                uploadAllBeaconsInfo(positionInfo);
            }
        });
    }

    private double getAcceResult() {
        return Math.sqrt(acce[0]*acce[0]+acce[1]*acce[1]+acce[2]*acce[2]);
    }
    private GetPositionInfoService getPositionInfoService;
    private void uploadAllBeaconsInfo(final PositionInfo positionInfo) {
        if (getPositionInfoService == null) {
            getPositionInfoService = CarServiceFactory.getInstance().createService(GetPositionInfoService.class);
        }
        final RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), gson.toJson(positionInfo));
        Call<PositionResult> httpResultCall = getPositionInfoService.uploadAllBeaconsInfo(requestBody);
        httpResultCall.enqueue(new Callback<PositionResult>() {
            @Override
            public void onResponse(Call<PositionResult> call, Response<PositionResult> response) {
                mOnUpdateLocationData.updateLocation("OK",response.body());
            }

            @Override
            public void onFailure(Call<PositionResult> call, Throwable t) {
                mOnUpdateLocationData.updateLocation("ERROR",null);
            }
        });
    }

    public void setOnUpdateLocationData(OnUpdateLocationData onUpdateLocationData) {
        mOnUpdateLocationData = onUpdateLocationData;
    }

    public interface OnUpdateLocationData{
        void updateLocation(String state,PositionResult positionResult);
    }

    public void unPwebViewRegistertation() {
        try {
            if (beaconManager.isBound(this)) {
                Region r = new Region(
                        "",
                        Identifier.parse(UUID_NUM),
                        Identifier.fromInt(MAJOR_NUM),
                         null
                );
                regions.remove(r);
                beaconManager.stopRangingBeaconsInRegion(r);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void exitBeaconScan() {
        try {
            if (beaconManager.isBound(this)) {
                for (Region r : regions) {
                    beaconManager.stopRangingBeaconsInRegion(r);
                }
                beaconManager.removeRangeNotifier(this);
                beaconManager.unbind(this);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
