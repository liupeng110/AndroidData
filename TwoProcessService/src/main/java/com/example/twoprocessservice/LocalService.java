package com.example.twoprocessservice;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class LocalService extends Service {
    private static final String TAG = "LocalService";

    LocalServiceBinder localServiceBinder;
    LocalServiceConnection localServiceConnection;
    private Timer timer;
    private TimerTask timerTask;
    private int counter = 0;



    @Override
    public void onCreate() {
        super.onCreate();
        if(localServiceBinder != null){
            localServiceBinder = new LocalServiceBinder();
        }
        localServiceConnection = new LocalServiceConnection();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startTimer();
        Notification.Builder builder = new Notification.Builder(this);
        builder.setDefaults(Notification.DEFAULT_SOUND);
        builder.setContentTitle("Dongnao.edu");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentInfo("info");
        builder.setWhen(System.currentTimeMillis());
        PendingIntent pi = PendingIntent.getActivity(this,0,intent,0);
        builder.setContentIntent(pi);

        startForeground(startId,builder.build());

        return START_STICKY;
    }

    public LocalService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return localServiceBinder;
    }

    class LocalServiceConnection implements ServiceConnection{

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected:");
            LocalService.this.startService(new Intent(LocalService.this,RemoteService.class));
            LocalService.this.bindService(new Intent(LocalService.this,RemoteService.class),localServiceConnection
            , Context.BIND_IMPORTANT);
        }
    }

    class LocalServiceBinder extends IServiceAidlInterface.Stub{

        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {
            Log.d(TAG, "basicTypes: ");
        }
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

}
