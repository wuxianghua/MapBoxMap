package com.org.mylibrary.yichemap.mode;

/**
 * Created by Administrator on 2017/11/10/010.
 */

public class BeaconInfo {
    public String mac;

    @Override
    public String toString() {
        return "BeaconInfo{" +
                "mac='" + mac + '\'' +
                ", rssi=" + rssi +
                ", accuracy=" + accuracy +
                ", txpower=" + txpower +
                '}';
    }

    public int rssi;
    public double accuracy;
    public int txpower;
}
