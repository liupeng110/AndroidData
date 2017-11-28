//package com.example.socketdemo;
//
//import android.support.compat.BuildConfig;
//import android.util.Log;
//
//import java.net.URISyntaxException;
//
//import io.socket.client.IO;
//import io.socket.client.Socket;
//import io.socket.emitter.Emitter;
//
///**
// * Created by LiuLei on 2017/6/19.
// */
//
//public class SocketIO {
//
//    public static final int STATE_IDLE       = 0x601;
//    public static final int STATE_CONNECTING = 0x602;
//    public static final int STATE_CONNECTED  = 0x603;
//
//    private static final String TAG = "SocketIO";
//
//    private int mState = STATE_IDLE;
//
//    private Socket mClient;
//
//
//    public void init(String url) throws URISyntaxException {
//        if(mClient != null){
//            return;
//        }
//        IO.Options options = new IO.Options();
//        options.forceNew = true;
////        options.query =
//        options.timeout = 5000;
//        options.reconnection = true;
//        options.reconnectionAttempts = Integer.MAX_VALUE;
//        options.reconnectionDelay = 3000;
//        mClient = IO.socket(url,options);
//        mClient.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
//            @Override
//            public void call(Object... args) {
//                if(BuildConfig.DEBUG){
//                    Log.d(TAG,"connect");
//                }
//            }
//        }).on("disconnect", new Emitter.Listener() {
//            public void call(Object... args) {
//                if (BuildConfig.DEBUG) {
//                    Log.d(TAG, "disconnect");
//                }
//                mState = STATE_IDLE;
//            }
//        }).on("error", new Emitter.Listener() {
//            public void call(Object... args) {
//                if (BuildConfig.DEBUG) {
//                    Log.d(TAG, "error");
//                }
//                mState = STATE_IDLE;
//            }
//        }).on("connect_timeout", new Emitter.Listener() {
//            public void call(Object... args) {
//                if (BuildConfig.DEBUG) {
//                    Log.d(TAG, "connect_timeout");
//                }
//                mState = STATE_IDLE;
//            }
//        }).on("connect_error", new Emitter.Listener() {
//            public void call(Object... args) {
//                if (BuildConfig.DEBUG) {
//                    Log.d(TAG, "connect_error");
//                }
//                mState = STATE_IDLE;
//            }
//        }).on("message", new Emitter.Listener() {
//            public void call(Object... args) {
//                if (BuildConfig.DEBUG) {
//                    Log.d(TAG, "message");
//                }
//            }
//        });
//    }
//
//    public void connect(){
//        if(mClient != null && mState == STATE_IDLE){
//            mState = STATE_CONNECTING;
//            mClient.connect();
//        }
//    }
//}
