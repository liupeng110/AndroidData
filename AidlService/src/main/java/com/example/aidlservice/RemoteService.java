package com.example.aidlservice;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by LiuLei on 2017/11/28.
 */

public class RemoteService extends Service {

    //用于表示service是否存活着
    public AtomicBoolean mIsServiceDestroyed = new AtomicBoolean(false);
    private ArrayList<IOnNewMsgArrivedLisenter> mLisenters = new ArrayList<>();

    private static final String TAG = "RemoteService";

    @Override
    public void onCreate() {
        super.onCreate();
        new Thread(new MessageRunnable()).start();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private final IMyAidlInterface.Stub mBinder = new IMyAidlInterface.Stub() {

        @Override
        public void ask(String ask) throws RemoteException {

        }

        @Override
        public void registerLisenter(IOnNewMsgArrivedLisenter lisenter) throws RemoteException {
            if (!mLisenters.contains(lisenter))
                mLisenters.add(lisenter);
            else
                System.out.println("此用户已经正在监听......");
        }

        @Override
        public void unRegisterLisenter(IOnNewMsgArrivedLisenter lisenter) throws RemoteException {
            if(mLisenters.contains(lisenter))
                mLisenters.remove(lisenter);
            else
                System.out.println("没找到用户,解除绑定失败......");
        }
    };


    //发送心跳包的线程，用于每隔1秒查看是否有用户给当前用户发送消息
    private class MessageRunnable implements Runnable{
        public void run() {
            while(!mIsServiceDestroyed.get())
            {
                //这里仅仅模拟了两个用户
                int senderID = (int)(Math.random()*10);
                int receiverID = (int)(Math.random()*2);
                if(senderID == receiverID)
                    continue;//自己不能给自己发消息
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String messageContent = getNowTime()+"** "+senderID+"向"+receiverID+"发送了一条消息";
                //service存活的话
                NewMessage message = new NewMessage(senderID+"", messageContent, receiverID+"");
                onNewMesssageArrived(message);
                System.out.println(messageContent);
            }
        }
    };

    //将消息分发给所有已经注册的用户
    public void onNewMesssageArrived(NewMessage message)
    {
        for(int i = 0;i < mLisenters.size();i++)
        {
            IOnNewMsgArrivedLisenter listener = mLisenters.get(i);
            try {
                if(listener != null)
                    listener.onNewMsgArrived(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public String getNowTime()
    {
        //将当前时间转换为HH:mm:ss的形式
        return new SimpleDateFormat("(HH:mm:ss)").format(new Date(System.currentTimeMillis()));
    }

    @Override
    public void onDestroy() {
        mIsServiceDestroyed.set(true);//设置取消服务
        super.onDestroy();
    }
}
