package com.techwarriors.mavdriver;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;


public class Connectivity{
    public static final String DATABASE_URI="mongodb://192.168.43.71:27017";
    public static final String DATABASE_NAME = "mav";
    //public static final String COLLECTION_NAME1 = "rider";

    public static boolean isnetworkconnected(Context context) {
        ConnectivityManager connManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mWifi.isConnected();
    }

    public static void enableWifi(Context context){
        WifiManager manager=(WifiManager)context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        manager.setWifiEnabled(true);

    }
    public static boolean wifienabled(Application application){
        WifiManager manager = (WifiManager)application.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        return manager.isWifiEnabled();
    }


}