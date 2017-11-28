package com.example.socketdemo;

import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.logging.Handler;

/**
 * Created by LiuLei on 2017/6/16.
 */

public class SocketClient implements Runnable{

    private static final String TAG = "SocketClient";


    public static final int DISCONNECTED  = 0;
    public static final int CONNECTING    = 1;
    public static final int CONNECTED     = 2;
    public static final int DISCONNECTING = 3;

    private int mState = DISCONNECTED;

    private Socket mSocket;
    private Thread  mThread;
    private boolean running;
    private String  mAddress;
    private int     mPort;


    public boolean connect(String address, int port) {
        if (mState != DISCONNECTED) {
            return false;
        }
        mState = CONNECTING;

        if (running || mThread != null && mThread.isAlive()) {
//            shutdown();
        }
        mAddress = address;
        mPort = port;
        mThread = new Thread(this);
        mThread.start();
//		initHeartBeat();
        Log.e("connect","true");
        return true;
    }

    @Override
    public void run() {
        try {
            mSocket = new Socket(mAddress, mPort);
            mSocket.setKeepAlive(true);
            String s = "FUCK";
            byte[]data = s.getBytes();
            write(data);
        } catch (Exception e) {
            if (App.DEBUG) {
                e.printStackTrace();
            }
        }
        running = true;
        while (running && mSocket != null && mSocket.isConnected()) {
            try {
                InputStream inputStream = mSocket.getInputStream();
                byte[] bytes = new byte[1024];
                int len = inputStream.read(bytes);
                if (len > 0) {
                    StringBuilder sb = new StringBuilder();
                    if (App.DEBUG) {
                        sb.append("receive data=");
                        for (int i = 0; i < len; i++) {
                            byte b = bytes[i];
                            String hv = Integer.toHexString(b & 0xff);
                            if (hv.length() < 2) {
                                sb.append(0);
                            }
                            sb.append(hv);
                        }
//                        Toast.makeText(App.getInstance(),sb.toString(),Toast.LENGTH_SHORT).show();
                        Log.i(TAG, sb.toString());
                    }
                    while (bytes != null) {
                        int size = ((bytes[0] >> 8) & 0xff) | (bytes[1] & 0xff);
                        int dataLen = size - 2;
                        byte[] data = new byte[dataLen];
                        System.arraycopy(bytes, 2, data, 0, dataLen);

                    }
                }
            } catch (IOException e) {
                if (App.DEBUG) {
                    e.printStackTrace();
                }
                break;
            }
        }
//        shutdown();
    }


    private void write(byte[]data){
        if(mSocket == null){
            return;
        }
        try {
            mSocket.getOutputStream().write(data);
            mSocket.getOutputStream().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
