package com.example.mvpdemo.presenter;

import com.example.mvpdemo.annotation.Implement;

/**
 * Created by LiuLei on 2017/11/27.
 */
@Implement(LoginPresenter.class)
public interface LoginContract {
    void onLogin(String username, String password);
}
