package com.example.mvpdemo.presenter;

import com.example.mvpdemo.annotation.Implement;

/**
 * Created by LiuLei on 2017/11/27.
 */
@Implement(MainPresenter.class)
public interface MainContract {
    void onLoadDeviceList(boolean isMore, String token);
}
