package com.example.admin.queue;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * 请求队列demo   BlockingQueue
 * Created by LiuLei on 2017/11/24.
 */

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        execute();
    }

    private void execute() {
        RequestQueue queue = new RequestQueue(3);
        Request request = new Request(new Request.RequestLisenter() {
            @Override
            public void onComplete(Result result) {
                System.out.print(result.getName());
                Log.e(TAG, "onComplete: "+result.getName());
            }
        });
        queue.addRequest(request);
        queue.start();
    }
}
