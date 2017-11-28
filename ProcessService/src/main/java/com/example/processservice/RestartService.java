package com.example.processservice;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class RestartService extends Service {
    private static final String TAG = "RestartService";
    private Timer timer;
    private TimerTask timerTask;
    private int counter = 0;
    long oldTime = 0;

    public RestartService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent,flags,startId);
        startTimer();
        //确保后台运行
        return START_STICKY;
    }

    private void startTimer() {
        timer = new Timer();
        initializeTimerTask();
        timer.schedule(timerTask, 1000, 1000);
    }

    private void stopTimerTask(){
        if(timer != null){
            timer.cancel();
            timer = null;
        }
    }

    private void initializeTimerTask() {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                Log.d(TAG, "int timer +++++ " + (counter++));
            }
        };
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"OnDestory");
        Intent broadcastIntent = new Intent("com.example.processservice.ServiceRestartBroadcastReceiver");
        sendBroadcast(broadcastIntent);
        stopTimerTask();
    }
}
