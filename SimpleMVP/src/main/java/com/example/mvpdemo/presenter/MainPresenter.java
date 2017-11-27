package com.example.mvpdemo.presenter;

import android.widget.Toast;

import com.example.mvpdemo.api.BaseResponse;
import com.example.mvpdemo.api.model.DeviceVO;
import com.example.mvpdemo.api.util.RetrofitUtil;
import com.example.mvpdemo.base.model.LoadListDataLogicImpl;
import com.example.mvpdemo.utils.ToastUtil;

import java.util.HashMap;
import java.util.List;

import rx.Subscriber;

/**
 * Created by LiuLei on 2017/11/27.
 */

public class MainPresenter extends LoadListDataLogicImpl<DeviceVO> implements MainContract{
    @Override
    public void onLoadDeviceList(boolean isMore, String token) {
        HashMap<String,String> map = new HashMap<>();
        if(token!=null){
            map.put("token",token);
        }else {
            ToastUtil.show("token不能为空");
            return;
        }
        RetrofitUtil.getInstance().MyDevices(map,new Subscriber<BaseResponse<List<DeviceVO>>>() {
            @Override
            public void onCompleted() {
                ToastUtil.show("获取设备列表完成");
            }

            @Override
            public void onError(Throwable e) {
                ToastUtil.show("获取设备列表出错");
            }

            @Override
            public void onNext(BaseResponse<List<DeviceVO>> deviceVOBaseResponse) {
                onLoadComplete(deviceVOBaseResponse, false);
            }
        });
    }
}
