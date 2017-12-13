package com.example.a2dp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothProfile;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Button mScan;
    private ListView mListView;
    private List<DeviceVO> mLists = new ArrayList<>();

    private A2Manager mA2Manager;
    private ArrayAdapter mAdapter;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestPermission(new String[]{Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.ACCESS_COARSE_LOCATION}, "请求蓝牙相关权限", new GrantedResult() {
            @Override
            public void onResult(boolean granted) {
                if(granted){

                }else {
                    finish();
                }
            }
        });

        //检测蓝牙是否打开   是否支持蓝牙等操作自己去写  此处省略
        initA2dp();
        initView();
    }

    private void initView() {
        mScan = findViewById(R.id.scan);
        mListView = findViewById(R.id.lv_scan);
        mAdapter = new ArrayAdapter<DeviceVO>(this,android.R.layout.simple_expandable_list_item_1,mLists);
        mListView.setAdapter(mAdapter);
        mScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLists.clear();
                mA2Manager.startScan(true);
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                position = i;
                if(mLists.get(i).getState() == 1){//已连接
                    //断开
                    mA2Manager.disconnect(mA2Manager.getConnectedDevices().get(0));
                }else if(mLists.get(i).getState() == 0){
                    mA2Manager.connect(mLists.get(i).getAddress());
                }
            }
        });
    }

    private void initA2dp() {
        mA2Manager = A2Manager.getInstance();
        mA2Manager.init(getApplicationContext());
        mA2Manager.setA2dpLisenter(lisenter);
    }


    A2Lisenter lisenter = new A2Lisenter() {

        @Override
        public void onStart() {
        }

        @Override
        public void onStop() {
            super.onStop();
        }

        @Override
        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            super.onServiceConnected(profile, proxy);
            Log.w(TAG, "onServiceConnected: ");
        }

        @Override
        public void onLeScan(DeviceVO device) {
            if (null != device.getName()) {
                Log.w(TAG, "onLeScan: " + device.getName());
                mLists.add(device);
                mAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onConnectionChanged(DeviceVO device) {
            Log.w(TAG, "onConnectionChanged: " + device.getName());
            if (device.getState() == 1) {//连接成功
                mListView.getChildAt(position).setBackgroundColor(getResources().getColor(R.color.colorAccent));
            }else {
                mListView.getChildAt(position).setBackgroundColor(Color.WHITE);
            }
        }

        @Override
        public void onStatePlay(boolean isPlay) {
            Log.d(TAG, "播放状态: " + isPlay);
        }
    };







    private int mPermissionIdx = 0x10;//请求权限索引
    private SparseArray<GrantedResult> mPermissions = new SparseArray<>();//请求权限运行列表

    @SuppressLint("Override")
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        GrantedResult runnable = mPermissions.get(requestCode);
        if (runnable == null) {
            return;
        }
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            runnable.mGranted = true;
        }
        runOnUiThread(runnable);
    }

    public void requestPermission(String[] permissions, String reason, GrantedResult runnable) {
        if (runnable == null) {
            return;
        }
        runnable.mGranted = false;
        if (Build.VERSION.SDK_INT < 23 || permissions == null || permissions.length == 0) {
            runnable.mGranted = true;//新添加
            runOnUiThread(runnable);
            return;
        }
        final int requestCode = mPermissionIdx++;
        mPermissions.put(requestCode, runnable);

		/*
            是否需要请求权限
		 */
        boolean granted = true;
        for (String permission : permissions) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                granted = granted && checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
            }
        }

        if (granted) {
            runnable.mGranted = true;
            runOnUiThread(runnable);
            return;
        }

		/*
            是否需要请求弹出窗
		 */
        boolean request = true;
        for (String permission : permissions) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                request = request && !shouldShowRequestPermissionRationale(permission);
            }
        }

        if (!request) {
            final String[] permissionTemp = permissions;
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setMessage(reason)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermissions(permissionTemp, requestCode);
                            }
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            GrantedResult runnable = mPermissions.get(requestCode);
                            if (runnable == null) {
                                return;
                            }
                            runnable.mGranted = false;
                            runOnUiThread(runnable);
                        }
                    }).create();
            dialog.show();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(permissions, requestCode);
            }
        }
    }

    public static abstract class GrantedResult implements Runnable {
        public boolean mGranted;

        public abstract void onResult(boolean granted);

        @Override
        public void run() {
            onResult(mGranted);
        }
    }
}
