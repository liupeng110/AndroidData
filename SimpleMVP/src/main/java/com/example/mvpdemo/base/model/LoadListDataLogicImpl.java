package com.example.mvpdemo.base.model;

import com.example.mvpdemo.api.BaseResponse;
import com.example.mvpdemo.base.BasePresenter;

import java.util.List;

import retrofit2.Response;

/**
 * Created by LiuLei on 2017/11/27.
 */
public class LoadListDataLogicImpl<T> extends BasePresenter<LoadListDataLogic.LoadListView> implements LoadListDataLogic<T> {
    @Override
    public void onLoadComplete(BaseResponse<List<T>> response, boolean isMore) {
        getView().onLoadComplete(isMore);
        List<T> body = response.data;
        if (body != null)
            getView().onLoadCompleteData(body, isMore);
    }

    @Override
    public void onFailer(String msg) {
        getView().showErrorMessage("网络错误", msg);
    }
}
