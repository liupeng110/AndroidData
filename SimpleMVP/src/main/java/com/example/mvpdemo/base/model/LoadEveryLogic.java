package com.example.mvpdemo.base.model;


import com.example.mvpdemo.api.BaseResponse;
import com.example.mvpdemo.base.BaseView;

import retrofit2.Response;

/**
 * Created by LiuLei on 2017/11/27.
 */
public interface LoadEveryLogic<T> {

    void onLoadCompleteData(BaseResponse<T> response);

    void onFailer(String msg);

    interface LoadEveryView<T> extends BaseView {
        void onLoadComplete(T body);

        void onLoadFailer(String msg);
    }
}
