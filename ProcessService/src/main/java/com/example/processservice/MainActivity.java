package com.example.processservice;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    //进程  优先级划分
    //1.Foreground Process  在手机休眠状态的时候会停掉
    //2.visible Process
    //3.servcle process
    //4.background process
    //5.empty process

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RestartService restartService = new RestartService();
        Intent serviceIntent = new Intent(this,restartService.getClass());
        if(!isServiceRuning(restartService.getClass())){
            startService(serviceIntent);
        }
    }

    private boolean isServiceRuning(Class<? extends RestartService> aClass) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(40);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName().toString();
            if (mName.equals(aClass.getName())) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }
}
