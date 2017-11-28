package com.example.twoprocessservice;

import android.app.ActivityManager;
import android.app.Service;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;

/**
 * Created by LiuLei on 2017/6/15.
 */

public class JobHandlerService extends JobService {
    private static final String TAG = "JobHandlerService";

    private int jobId = 0x0008;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ");
        scheduleJob(getJobInfo());
        return START_NOT_STICKY;
    }

    private void scheduleJob(JobInfo job){
        JobScheduler js = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);

        js.schedule(job);
    }

    private JobInfo getJobInfo(){
        JobInfo.Builder builder = new JobInfo.Builder(jobId,new ComponentName(this,JobHandlerService.class));

        //发送之后  会一直存在
        builder.setPersisted(true);
        builder.setPeriodic(5000);
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        builder.setRequiresCharging(false);//充电
        builder.setRequiresDeviceIdle(false);
        return builder.build();
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        boolean isLocalServiceWorking = isServiceWork(this,"com.example.twoprocessservice.LocalService");
        boolean isRemoteServiceWorking = isServiceWork(this,"com.example.twoprocessservice.RemoteService");
        if(!isLocalServiceWorking || !isRemoteServiceWorking){
            startService(new Intent(this,LocalService.class));
            startService(new Intent(this,RemoteService.class));
        }
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        scheduleJob(getJobInfo());
        return true;
    }

    public boolean isServiceWork(Context context, String serviceName){
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> list = manager.getRunningServices(128);
        if(list.size() < 0 ){
            return false;
        }
        for (int i = 0; i < list.size(); i++){
            String name = list.get(i).service.getClassName().toString();
            if(serviceName.equals(name)){
                return true;
            }
        }
        return false;

    }

    private boolean isServiceRuning(Class<? extends Service> aClass) {
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
