package com.example.mvpdemo.base.model;


import com.example.mvpdemo.api.BaseResponse;
import com.example.mvpdemo.base.BaseView;

import java.util.List;

import retrofit2.Response;

/**
 * Created by LiuLei on 2017/11/27.
 */
public interface LoadListDataLogic<T> {
    void onLoadComplete(BaseResponse<List<T>> response, boolean isMore);

    void onFailer(String msg);

    interface LoadListView<T> extends BaseView {
        void onLoadCompleteData(T body, boolean isMore);

        void onLoadComplete(boolean isMore);
    }
}
