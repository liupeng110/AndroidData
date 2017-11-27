package com.example.mvpdemo.presenter;

import android.widget.Toast;

import com.example.mvpdemo.api.BaseResponse;
import com.example.mvpdemo.api.model.UserVO;
import com.example.mvpdemo.api.util.RetrofitUtil;
import com.example.mvpdemo.base.model.LoadEveryLogicImpl;
import com.example.mvpdemo.utils.ToastUtil;

import java.util.HashMap;

import rx.Subscriber;

/**
 * Created by LiuLei on 2017/11/27.
 */

public class LoginPresenter extends LoadEveryLogicImpl<UserVO> implements LoginContract{
    @Override
    public void onLogin(String username, String password) {
        HashMap<String,String> map = new HashMap<>();
        map.put("mobile","18682176281");
        map.put("password","e10adc3949ba59abbe56e057f20f883e");
        RetrofitUtil.getInstance().getUserBean(map,new Subscriber<BaseResponse<UserVO>>() {
            @Override
            public void onCompleted() {
                ToastUtil.show("登录完成");
            }

            @Override
            public void onError(Throwable e) {
                ToastUtil.show("登录出错");
            }

            @Override
            public void onNext(BaseResponse<UserVO> userVOBaseResponse) {
//                String user = userVOBaseResponse.data.toString();
//                String token = userVOBaseResponse.data.getUser_token();
//                ToastUtil.show("登录成功");
//                ToastUtil.show(user);
                onLoadCompleteData(userVOBaseResponse);
            }
        });
    }
}
