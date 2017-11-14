package com.org.mylibrary.yichemap.mode;

import java.util.List;

/**
 * Created by Administrator on 2017/11/10/010.
 */

public class PositionInfo {
    public List<BeaconInfo> ble;
    public String userid;
    public int move;
    public int rsp;

    @Override
    public String toString() {
        return "PositionInfo{" +
                "ble=" + ble +
                ", userid='" + userid + '\'' +
                ", move=" + move +
                ", rsp=" + rsp +
                ", sys=" + sys +
                '}';
    }

    public int sys;
}
