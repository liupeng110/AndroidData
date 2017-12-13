package com.example.a2dp;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * A2DP蓝牙工具类
 * Created by LiuLei on 2017/7/14.
 */

public class A2Manager {
    private static final String TAG = "A2Manager";
    private static A2Manager sA2Manager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothA2dp mBluetoothA2dp;
//    private BluetoothDevice mBluetoothDevice;
    private ArrayList<BluetoothDevice> mScanBluetoothDevices = new ArrayList<>();
    private boolean mScanning = false;
//    public final ArrayList<DeviceVO> mConnetedDevices = new ArrayList<>();
    public final ArrayList<DeviceVO> mDevices = new ArrayList<>();


    private List<A2Lisenter> mA2Lisenters = new ArrayList<>();

    private Handler mHandler = new Handler(Looper.myLooper()){
        @Override
        public void handleMessage(Message msg) {

        }
    };

    protected A2Manager() {
    }

//    public void setBluetooth(BluetoothDevice device){
//        if(mBluetoothDevice == null){
//            mBluetoothDevice = device;
//        }
//    }
    public static A2Manager getInstance(){
        if(sA2Manager == null){
            synchronized (A2Manager.class){
                if(sA2Manager == null){
                    sA2Manager = new A2Manager();
                }
            }
        }
        return sA2Manager;
    }

    public void setA2dpLisenter(A2Lisenter lisenter){
        if(!mA2Lisenters.contains(lisenter)){
            mA2Lisenters.add(lisenter);
        }
    }

    public boolean init(Context context){
        //注册服务
        registerReceiver(context);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null){
            Log.e(TAG,"have no bluetooth adapter.");
            return false;
        }
        //getA2dp
        mBluetoothAdapter.getProfileProxy(context, new BluetoothProfile.ServiceListener() {
            @Override
            public void onServiceConnected(int profile, BluetoothProfile proxy) {
                if(profile == BluetoothProfile.A2DP){
                    //Service连接成功，获得BluetoothA2DP
                    if(mBluetoothA2dp == null){
                        Log.e("onServiceConnected","a2dp服务发现成功");
                        mBluetoothA2dp = (BluetoothA2dp)proxy;
                        for(A2Lisenter lisenter : mA2Lisenters){
                            lisenter.onServiceConnected(profile,proxy);
                        }
                    }
                }
            }

            @Override
            public void onServiceDisconnected(int profile) {
                Log.e("onServiceDisconnected",profile+"");
                mBluetoothA2dp = null;
            }
        },BluetoothProfile.A2DP);
        return true;

    }

    private void registerReceiver(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED);
        filter.addAction(BluetoothA2dp.ACTION_PLAYING_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        context.registerReceiver(mBroadcastReceiver, filter);
    }

    public void unRegisterReceiver(Context context) {
        if(mBroadcastReceiver != null){
            context.unregisterReceiver(mBroadcastReceiver);
        }
    }

    public void startScan(final boolean enable){
        mScanBluetoothDevices.clear();
        if(enable){
            Log.i(TAG,"mBluetoothAdapter startDiscovery.");
            if(mBluetoothAdapter!=null && mBluetoothAdapter.isEnabled() && !mBluetoothAdapter.isDiscovering()){

                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mScanning = false;
                        mBluetoothAdapter.cancelDiscovery();
                        for(A2Lisenter lisenter : mA2Lisenters){
                            lisenter.onStop();
                        }
                    }
                },12000);
                mScanning = true;
                mBluetoothAdapter.startDiscovery();
                for(A2Lisenter lisenter : mA2Lisenters){
                    lisenter.onStart();
                }
            }
        }else {
            mScanning = false;
            mBluetoothAdapter.cancelDiscovery();
            for(A2Lisenter lisenter : mA2Lisenters){
                lisenter.onStop();
            }
        }
    }

    public boolean isScanning() {
        return mScanning;
    }

    //connect和disconnect都是hide方法，普通应用只能通过反射机制来调用该方法
    public void connect(String address){
        BluetoothDevice device = getBluetoothDevice(address);
        if(mBluetoothA2dp == null){
            return;
        }
        if(device == null){
            return;
        }
        setPriority(device, 100); //设置priority

        try {
            @SuppressLint("PrivateApi")
            Method connect = mBluetoothA2dp.getClass().getDeclaredMethod("connect", BluetoothDevice.class);
            connect.setAccessible(true);
            connect.invoke(mBluetoothA2dp,device);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            Log.e(TAG,"connect exception:"+e);
            e.printStackTrace();
        }
    }

    public void disconnect(BluetoothDevice device){
        if(mBluetoothA2dp == null){
            return;
        }
        if(device == null){
            return;
        }
        Log.i(TAG,"disconnect"+device.getName());
        setPriority(device, 0);

        try {
            @SuppressLint("PrivateApi")
            Method disconnect = mBluetoothA2dp.getClass().getDeclaredMethod("disconnect", BluetoothDevice.class);
            disconnect.setAccessible(true);
            disconnect.invoke(mBluetoothA2dp,device);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            Log.e(TAG,"connect exception:"+e);
            e.printStackTrace();
        }

    }

    /*设置优先级是必要的，否则可能导致连接或断开连接失败等问题。*/
    private void setPriority(BluetoothDevice device, int priority) {
        if (mBluetoothA2dp == null) return;
        try {//通过反射获取BluetoothA2dp中setPriority方法（hide的），设置优先级
            Method connectMethod =BluetoothA2dp.class.getMethod("setPriority",
                    BluetoothDevice.class,int.class);
            connectMethod.invoke(mBluetoothA2dp, device, priority);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private BluetoothDevice getBluetoothDevice(String address){
        for (BluetoothDevice device : mScanBluetoothDevices){
            if(device.getAddress().equals(address)){
                return device;
            }
        }
        return null;
    }

    //创建绑定
    private void createBond(BluetoothDevice device){
        Log.i(TAG, "createBond");
        if(device != null){
            device.createBond();
        }
    }

    //取消配对
    public void unPairAllDevices(BluetoothDevice device){
        Log.i(TAG,"unPairAllDevices");
//        for(BluetoothDevice device:mBluetoothAdapter.getBondedDevices()){
//            try {
//                Method removeBond = device.getClass().getDeclaredMethod("removeBond");
//                removeBond.invoke(device);
//            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
//                e.printStackTrace();
//            }
//        }
        if(device == null){
            return;
        }
        try {
            Method removeBond = device.getClass().getDeclaredMethod("removeBond");
            removeBond.invoke(device);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    //注意，在程序退出之前（OnDestroy），需要断开蓝牙相关的Service
    //否则，程序会报异常：service leaks
    public void disableAdapter(Context context){
        Log.i(TAG,"disableAdapter");
        //取消注册服务
        unRegisterReceiver(context);

        if(mBluetoothAdapter == null){
            return;
        }

        if(mBluetoothAdapter.isDiscovering()){
            mBluetoothAdapter.cancelDiscovery();
        }

        //关闭ProfileProxy，也就是断开service连接
        mBluetoothAdapter.closeProfileProxy(BluetoothProfile.A2DP,mBluetoothA2dp);
        if(mBluetoothAdapter.isEnabled()){
            boolean ret = mBluetoothAdapter.disable();
            Log.i(TAG,"disable adapter:"+ret);
        }
    }

    //监听广播
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            DeviceVO deviceVO;
            BluetoothDevice device;
            switch (intent.getAction()) {
                case BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED:
                    //<editor-fold>
                    switch (intent.getIntExtra(BluetoothA2dp.EXTRA_STATE, -1)) {
                        case BluetoothA2dp.STATE_CONNECTING:
                            device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                            deviceVO = getBleDevice(device);
                            Log.i(TAG, "device: " + device.getName() +" connecting");
                            deviceVO.setState(2);
                            for(A2Lisenter lisenter : mA2Lisenters){
                                lisenter.onConnectionChanged(deviceVO);
                            }
                            break;
                        case BluetoothA2dp.STATE_CONNECTED:
                            device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                            deviceVO = getBleDevice(device);
                            Log.i(TAG, "device: " + device.getName() +" connected");
                            deviceVO.setState(1);
                            for(A2Lisenter lisenter : mA2Lisenters){
                                lisenter.onConnectionChanged(deviceVO);
                            }
                            mDevices.add(deviceVO);
                            //连接成功，开始播放
//                                startPlay();
                            break;
                        case BluetoothA2dp.STATE_DISCONNECTING:
                            device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                            deviceVO = getBleDevice(device);
                            Log.i(TAG, "device: " + device.getName() +" disconnecting");
                            break;
                        case BluetoothA2dp.STATE_DISCONNECTED:
                            device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                            deviceVO = getBleDevice(device);
                            Log.i(TAG, "device: " + device.getName() +" disconnected");
                            deviceVO.setState(0);
                            for(A2Lisenter lisenter : mA2Lisenters){
                                lisenter.onConnectionChanged(deviceVO);
                            }
//                            mDevices.remove(deviceVO);
//                                setResultPASS();
                            break;
                        default:
                            break;
                    }
                    //</editor-fold>
                    break;
                case BluetoothA2dp.ACTION_PLAYING_STATE_CHANGED:
                    //<editor-fold>
                    int state = intent.getIntExtra(BluetoothA2dp.EXTRA_STATE, -1);
                    switch (state) {
                        case BluetoothA2dp.STATE_PLAYING:
                            Log.i(TAG, "state: playing.");
                            for(A2Lisenter lisenter : mA2Lisenters){
                                lisenter.onStatePlay(true);
                            }
                            break;
                        case BluetoothA2dp.STATE_NOT_PLAYING:
                            Log.i(TAG, "state: not playing");
                            for(A2Lisenter lisenter : mA2Lisenters){
                                lisenter.onStatePlay(false);
                            }
                            break;
                        default:
                            Log.i(TAG, "state: unkown");
                            for(A2Lisenter lisenter : mA2Lisenters){
                                lisenter.onStatePlay(false);
                            }
                            break;
                    }
                    //</editor-fold>
                    break;
                case BluetoothDevice.ACTION_FOUND:
                    //<editor-fold>
                    device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    Log.e(TAG, "ACTION_FOUND: "+device.getName());
                    deviceVO = getBleDevice(device);
//                    int deviceClassType = device.getBluetoothClass().getDeviceClass();
                    for(A2Lisenter lisenter : mA2Lisenters){
                        lisenter.onLeScan(deviceVO);
                    }
                    //找到指定的蓝牙设备
//                        if ((deviceClassType == BluetoothClass.Device.AUDIO_VIDEO_WEARABLE_HEADSET
//                                || deviceClassType == BluetoothClass.Device.AUDIO_VIDEO_HEADPHONES)
//                                && device.getName().equals(DEVICE_NAME)) {
                    if(deviceVO.getName()!=null && !deviceVO.getName().equals("")){
                        if(!mScanBluetoothDevices.contains(device)){
                            mScanBluetoothDevices.add(device);
                        }
                        Log.e(TAG, "mScanBluetoothDevices.size: "+mScanBluetoothDevices.size());
//                        if (device.getName().equals("BK8002")) {
//                            Log.e(TAG, "Found device:" + device.getName());
//                            mBluetoothDevice = device;
//                            //start bond，开始配对
////                            createBond();
//                        }
                    }
                    //</editor-fold>
                    break;
                case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                    //<editor-fold>
                    int bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE,BluetoothDevice.BOND_NONE);
                    device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                    deviceVO = getBleDevice(device);
                    switch (bondState){
                        case BluetoothDevice.BOND_BONDED:  //配对成功
                            Log.i(TAG,"Device:"+device.getName()+" bonded.");
                            mBluetoothAdapter.cancelDiscovery();  //取消搜索
//                            connect();  //连接蓝牙设备
                            break;
                        case BluetoothDevice.BOND_BONDING:
                            Log.i(TAG,"Device:"+device.getName()+" bonding.");
                            break;
                        case BluetoothDevice.BOND_NONE:
                            Log.i(TAG,"Device:"+device.getName()+" not bonded.");
                            //不知道是蓝牙耳机的关系还是什么原因，经常配对不成功
                            //配对不成功的话，重新尝试配对
//                            createBond();
                            break;
                        default:
                            break;

                    }

                    //</editor-fold>
                    break;
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    //<editor-fold>
                    state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
                    switch (state) {
                        case BluetoothAdapter.STATE_TURNING_ON:
                            Log.i(TAG, "BluetoothAdapter is turning on.");
                            break;
                        case BluetoothAdapter.STATE_ON:
                            Log.i(TAG, "BluetoothAdapter is on.");
                            //蓝牙已打开，开始搜索并连接service
//                            startDiscovery();
//                            getBluetoothA2DP();
                            break;
                        case BluetoothAdapter.STATE_TURNING_OFF:
                            Log.i(TAG, "BluetoothAdapter is turning off.");
                            break;
                        case BluetoothAdapter.STATE_OFF:
                            Log.i(TAG, "BluetoothAdapter is off.");
                            break;
                    }
                    //</editor-fold>
                    break;
                default:
                    break;
            }
        }
    };

    public DeviceVO getBleDevice(BluetoothDevice device) {
//        if (device == null) {
//            return null;
//        }
        synchronized (mDevices){
            if(mDevices.size() > 0){
                for (DeviceVO bleDevice : mDevices) {
                    if (bleDevice.getAddress().equals(device.getAddress())) {
                        return bleDevice;
                    }
                }
            }
            return new DeviceVO(device.getName(),device.getAddress());
        }
    }

    public boolean deleteDevice(DeviceVO device){
        for(DeviceVO deviceVo : mDevices){
            if(device.getAddress().equals(deviceVo.getAddress())){
                return mDevices.remove(deviceVo);
            }
        }
        return false;
    }



    //查找当前是否有设备连接
    public List<BluetoothDevice> getConnectedDevices(){
        if (mBluetoothA2dp != null) {
            return mBluetoothA2dp.getConnectedDevices();
        }
        return null;
    }

    //获取当前已连接设备的数量
    public int getConnectedDeviceSize(){
        if(mBluetoothA2dp != null){
            return mBluetoothA2dp.getConnectedDevices().size();
        }
        return 0;
    }

}
