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

public class RemoteService extends Service {

    private static final String TAG = "RemoteService";

    RemoteServiceBinder remoteServiceBinder;
    RemoteServiceConnection remoteServiceConnection;

    private Timer timer;
    private TimerTask timerTask;
    private int counter = 0;


    @Override
    public void onCreate() {
        super.onCreate();
        if(remoteServiceBinder != null){
            remoteServiceBinder = new RemoteServiceBinder();
        }
        remoteServiceConnection = new RemoteServiceConnection();
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

        //将service process提高到前台通知优先级
        return START_STICKY;
    }

    public RemoteService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return remoteServiceBinder;
    }

    class RemoteServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected:");
            RemoteService.this.startService(new Intent(RemoteService.this,LocalService.class));
            RemoteService.this.bindService(new Intent(RemoteService.this,LocalService.class),remoteServiceConnection
                    , Context.BIND_IMPORTANT);
        }
    }

    class RemoteServiceBinder extends IServiceAidlInterface.Stub{

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
