package com.org.mylibrary.yichemap.utils;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2017/11/13/013.
 */

public class GetDeviceUtil {
    private static final String marshmallowMacAddress = "02:00:00:00:00:00";
    private static final String fileAddressMac = "/sys/class/net/wlan0/address";
    public static String getAdressMAC(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo != null && marshmallowMacAddress.equals(wifiInfo.getMacAddress())) {
            String result = null;
            result = getAdressMacByInterface();
            if (result != null) {
                return result;
            }else {
                result = getAdressMacByFile(wifiManager);
                return result;
            }
        }else {
            if (wifiInfo != null && wifiInfo.getMacAddress() != null) {
                return wifiInfo.getMacAddress();
            }else {
                return "";
            }
        }
    }

    private static String getAdressMacByFile(WifiManager wifiMan) {
        String ret;
        int wifiState = wifiMan.getWifiState();

        wifiMan.setWifiEnabled(true);
        File fl = new File(fileAddressMac);
        FileInputStream fin = null;
        try {
            fin = new FileInputStream(fl);
            ret = crunchifyGetStringFromStream(fin);
            fin.close();
            boolean enabled = WifiManager.WIFI_STATE_ENABLED == wifiState;
            wifiMan.setWifiEnabled(enabled);
            return ret;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getAdressMacByInterface() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface networkInterface : all) {
                if (networkInterface.getName().equalsIgnoreCase("wlan0")) {
                    byte[] macBytes = networkInterface.getHardwareAddress();
                    if (macBytes == null) {
                        return "";
                    }
                    StringBuilder res1 = new StringBuilder();
                    for (byte macByte : macBytes) {
                        res1.append(String.format("%02X",macByte));
                    }
                    if (res1.length() > 0) {
                        res1.deleteCharAt(res1.length() - 1);
                    }
                    return res1.toString();
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String crunchifyGetStringFromStream(InputStream crunchifyStream) throws IOException {
        if (crunchifyStream != null) {
            Writer crunchifyWriter = new StringWriter();

            char[] crunchifyBuffer = new char[2048];
            try {
                Reader crunchifyReader = new BufferedReader(new InputStreamReader(crunchifyStream, "UTF-8"));
                int counter;
                while ((counter = crunchifyReader.read(crunchifyBuffer)) != -1) {
                    crunchifyWriter.write(crunchifyBuffer, 0, counter);
                }
            } finally {
                crunchifyStream.close();
            }
            return crunchifyWriter.toString();
        } else {
            return "No Contents";
        }
    }
}
