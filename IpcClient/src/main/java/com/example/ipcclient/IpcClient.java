package com.example.ipcclient;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by LiuLei on 2017/6/12.
 */

public class IpcClient extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int MSG_SUM = 0x110;

    private Button mBtnAdd;
    private LinearLayout mLyContainer;
    //显示连接状态
    private TextView mTvState;

    private Messenger mService;
    private boolean isConn;

    @SuppressLint("HandlerLeak")
    private Messenger mMessenger = new Messenger(new Handler(){
        @Override
        public void handleMessage(Message msgFromServer) {
            switch (msgFromServer.what){
                case MSG_SUM:
                    TextView tv = (TextView) mLyContainer.findViewById(msgFromServer.arg1);
                    tv.setText(tv.getText() + "=>" + msgFromServer.arg2);
                    break;
            }
        }
    });

    private ServiceConnection mConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = new Messenger(service);
            isConn = true;
            mTvState.setText("connected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            isConn = false;
            mTvState.setText("disConnected");
        }
    };

    private int mA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //开始绑定服务
        bindServiceInvoked();
        mTvState = (TextView) findViewById(R.id.id_tv_callback);
        mBtnAdd = (Button) findViewById(R.id.id_btn_add);
        mLyContainer = (LinearLayout) findViewById(R.id.id_ll_container);

        mBtnAdd.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    int a = mA++;
                    int b = (int) (Math.random() * 100);

                    //创建一个tv,添加到LinearLayout中
                    TextView tv = new TextView(IpcClient.this);
                    tv.setText(a + " + " + b + " = caculating ...");
                    tv.setId(a);
                    mLyContainer.addView(tv);

                    Message msgFromClient = Message.obtain(null, MSG_SUM, a, b);
                    msgFromClient.replyTo = mMessenger;
                    if (isConn)
                    {
                        //往服务端发送消息
                        mService.send(msgFromClient);
                    }
                } catch (RemoteException e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    private void bindServiceInvoked()
    {
        Intent intent = new Intent();
        intent.setAction("com.example.ipservice");
//        bindService(intent, mConn, Context.BIND_AUTO_CREATE);
//        Log.e(TAG, "bindService invoked !");
        intent.addCategory(Intent.CATEGORY_DEFAULT);

        PackageManager pm = getPackageManager();
        //我们先通过一个隐式的Intent获取可能会被启动的Service的信息
        ResolveInfo info = pm.resolveService(intent, 0);

        if(info != null){
            //如果ResolveInfo不为空，说明我们能通过上面隐式的Intent找到对应的Service
            //我们可以获取将要启动的Service的package信息以及类型
            String packageName = info.serviceInfo.packageName;
            String serviceNmae = info.serviceInfo.name;
            //然后我们需要将根据得到的Service的包名和类名，构建一个ComponentName
            //从而设置intent要启动的具体的组件信息，这样intent就从隐式变成了一个显式的intent
            //之所以大费周折将其从隐式转换为显式intent，是因为从Android 5.0 Lollipop开始，
            //Android不再支持通过通过隐式的intent启动Service，只能通过显式intent的方式启动Service
            //在Android 5.0 Lollipop之前的版本倒是可以通过隐式intent启动Service
            ComponentName componentName = new ComponentName(packageName, serviceNmae);
            intent.setComponent(componentName);
            try{
                Log.i("DemoLog", "客户端调用bindService方法");
                bindService(intent, mConn, BIND_AUTO_CREATE);
            }catch(Exception e){
                e.printStackTrace();
                Log.e("DemoLog", e.getMessage());
            }
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        unbindService(mConn);
    }



}
