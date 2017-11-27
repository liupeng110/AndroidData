package com.example.mvpdemo.base.model;


import com.example.mvpdemo.api.BaseResponse;
import com.example.mvpdemo.base.BasePresenter;

import retrofit2.Response;

/**
 * Created by LiuLei on 2017/11/27.
 */
public class LoadEveryLogicImpl<T> extends BasePresenter<LoadEveryLogic.LoadEveryView> implements LoadEveryLogic<T> {

    @Override
    public void onLoadCompleteData(BaseResponse<T> response) {
        T body = response.data;
        if (body != null)
            getView().onLoadComplete(body);
    }

    @Override
    public void onFailer(String msg) {
        getView().hideProgress();
        getView().onLoadFailer(msg);
    }
}
