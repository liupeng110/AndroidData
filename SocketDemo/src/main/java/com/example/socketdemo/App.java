package com.example.socketdemo;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.compat.BuildConfig;
import android.text.TextUtils;
import android.util.Log;

import java.net.URISyntaxException;

/**
 * 程序对象
 * Created by LiuLei on 2016/12/5.
 */

public class App extends Application {
	private static App     instance = null;
	public static  boolean DEBUG    = BuildConfig.DEBUG;
	private long       mPhoneId;

	public final static String APP_SOCKET = "devices.e-toys.cn";
	public final static int    APP_SOCKET_PORT = 6007;

	@Override
	public void onCreate() {
		instance = this;
		super.onCreate();

		connectServer();
//		SocketIO socketIO = new SocketIO();
//		try {
//			socketIO.init(APP_SOCKET);
//		} catch (URISyntaxException e) {
//			e.printStackTrace();
//		}
//		socketIO.connect();

	}

	public static App getInstance() {
		return instance;
	}

	public void connectServer() {
		Log.e("App","connectServer");

		SocketClient client = new SocketClient();
		client.connect(APP_SOCKET,APP_SOCKET_PORT);
	}
}
