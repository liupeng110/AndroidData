package com.example.ipcservice;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;

/**
 * Created by LiuLei on 2017/6/12.
 */

public class MessengerService extends Service {

    private static final int MSG_SUM = 0x110;

    //最好换成HandlerThread的形式
    @SuppressLint("HandlerLeak")
    private Messenger mMessenger = new Messenger(new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Message msgToClient = Message.obtain(msg);//返回给客户端的消息
            switch (msg.what){
                //msg  客户端传来的消息
                case MSG_SUM:
                    msgToClient.what = MSG_SUM;
                    //模拟耗时
                    try {
                        Thread.sleep(2000);
                        msgToClient.arg2 = msg.arg1 + msg.arg2;
                        try {
                            msg.replyTo.send(msgToClient);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    });
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }
}
