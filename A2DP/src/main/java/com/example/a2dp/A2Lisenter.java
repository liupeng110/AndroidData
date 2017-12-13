package com.example.a2dp;

import android.bluetooth.BluetoothProfile;

/**
 * Created by LiuLei on 2017/7/14.
 */

public abstract class A2Lisenter {

    public void onStart(){};

    public void onStop(){};

    public void onServiceConnected(int profile, BluetoothProfile proxy){}

    public abstract void onLeScan(DeviceVO device);

    public abstract void onConnectionChanged(DeviceVO device);

    public abstract void onStatePlay(boolean isPlay);

    public void onBondChanged(int state){};

}
