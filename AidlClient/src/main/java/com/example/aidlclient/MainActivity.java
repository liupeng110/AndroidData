package com.example.aidlclient;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.aidlservice.IMyAidlInterface;
import com.example.aidlservice.IOnNewMsgArrivedLisenter;
import com.example.aidlservice.NewMessage;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public static final int NEW_MESSAGE = 1;
    private IMyAidlInterface mService;
    public boolean isRegister = false;//表示客户端是否注册监听到服务端
    public boolean isRepeat = false;//表示是否是重新开启的service连接

    @SuppressLint("HandlerLeak")
    public Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case NEW_MESSAGE:
                    NewMessage message = (NewMessage) msg.obj;
                    Log.e(TAG, "客户端收到消息: "+message.messageContent);
                    break;
                default:
                    break;
            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        findViewById(R.id.bind).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent("com.example.aidlservice");
////                intent.setAction("com.example.aidlservice.RemoteService");
//                //从 Android 5.0开始 隐式Intent绑定服务的方式已不能使用,所以这里需要设置Service所在服务端的包名
//                intent.setPackage("com.example.aidlservice.RemoteService");
//                bindService(intent, connection, Context.BIND_AUTO_CREATE);
                bindServiceInvoked();
            }
        });

        findViewById(R.id.unBind).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isRegister)
                {
                    //判断如果已经注册过的话，则将其解注册
                    if(mService != null && mService.asBinder().isBinderAlive())
                    {
                        try {
                            mService.unRegisterLisenter(listener);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    isRegister = false;
                }
            }
        });
    }

    private void bindServiceInvoked()
    {
        Intent intent = new Intent();
        intent.setAction("com.example.aidlservice");
//        bindService(intent, mConn, Context.BIND_AUTO_CREATE);
//        Log.e(TAG, "bindService invoked !");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        ComponentName componentName = new ComponentName("com.example.aidlservice", "com.example.aidlservice.RemoteService");
        intent.setComponent(componentName);
        Log.e(TAG, "bindServiceInvoked: 开始绑定远程服务" );
        bindService(intent, connection, BIND_AUTO_CREATE);

//        PackageManager pm = getPackageManager();
//        //我们先通过一个隐式的Intent获取可能会被启动的Service的信息
//        ResolveInfo info = pm.resolveService(intent, 0);
//
//        if(info != null){
//            //如果ResolveInfo不为空，说明我们能通过上面隐式的Intent找到对应的Service
//            //我们可以获取将要启动的Service的package信息以及类型
//            String packageName = info.serviceInfo.packageName;
//            String serviceNmae = info.serviceInfo.name;
//            //然后我们需要将根据得到的Service的包名和类名，构建一个ComponentName
//            //从而设置intent要启动的具体的组件信息，这样intent就从隐式变成了一个显式的intent
//            //之所以大费周折将其从隐式转换为显式intent，是因为从Android 5.0 Lollipop开始，
//            //Android不再支持通过通过隐式的intent启动Service，只能通过显式intent的方式启动Service
//            //在Android 5.0 Lollipop之前的版本倒是可以通过隐式intent启动Service
//            ComponentName componentName = new ComponentName(packageName, serviceNmae);
//            intent.setComponent(componentName);
//            try{
//                Log.i("DemoLog", "客户端调用bindService方法");
//                bindService(intent, connection, BIND_AUTO_CREATE);
//            }catch(Exception e){
//                e.printStackTrace();
//                Log.e("DemoLog", e.getMessage());
//            }
//        }
    }

    public IOnNewMsgArrivedLisenter listener = new IOnNewMsgArrivedLisenter.Stub(){
        @Override
        public void onNewMsgArrived(NewMessage msg) throws RemoteException {
//            if(msg.receiverID.equals("1"))
//            {
//                //表示是发给自己的消息,需要显示在界面上
//                mHandler.obtainMessage(NEW_MESSAGE,msg).sendToTarget();
//            }
            mHandler.obtainMessage(NEW_MESSAGE,msg).sendToTarget();
        }
    };

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            try {
                iBinder.linkToDeath(mDeathRecipient, 0);
            } catch (RemoteException e1) {
                e1.printStackTrace();
            }
            Log.e(TAG, "onServiceConnected: 绑定远程服务成功");
            mService = IMyAidlInterface.Stub.asInterface(iBinder);

            if(isRepeat)
                System.out.println("Binder死亡之后重新建立的连接");
            else
                System.out.println("Binder没有死亡");
            isRepeat = false;

            try {
                mService.registerLisenter(listener);
                isRegister = true;//客户端注册监听到服务端
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mService = null;
            Toast.makeText(MainActivity.this,"断开绑定了",Toast.LENGTH_SHORT).show();
        }
    };

    public IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            System.out.println("binderDied: "+Thread.currentThread().getName());
            if(mService == null)
                return;//表示服务端返回的binder是null
            //解除binder
            mService.asBinder().unlinkToDeath(mDeathRecipient,0);
            mService = null;
            //重新与服务端建立连接
            bindServiceInvoked();
            isRepeat = true;
        }
    };

    @Override
    protected void onDestroy() {
        if(isRegister)
        {
            //判断如果Binder服务端没有断开的话，则解除客户端消息监听绑定
            if(mService != null && mService.asBinder().isBinderAlive())
            {
                try {
                    mService.unRegisterLisenter(listener);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
        //解除客户端绑定
        unbindService(connection);
        super.onDestroy();
    }
}
