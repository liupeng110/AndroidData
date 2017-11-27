package com.example.mvpdemo.view;

import android.content.Intent;
import android.widget.EditText;

import com.example.mvpdemo.R;
import com.example.mvpdemo.api.model.UserVO;
import com.example.mvpdemo.base.BaseActivity;
import com.example.mvpdemo.base.model.LoadEveryLogic;
import com.example.mvpdemo.presenter.LoginContract;

import butterknife.Bind;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity implements LoadEveryLogic.LoadEveryView<UserVO>{

    @Bind(R.id.et_name)
    EditText mName;
    @Bind(R.id.et_pwd)
    EditText mPwd;
    @Override
    protected int getLayoutResource() {
        return R.layout.activity_login;
    }

    @Override
    protected void onInitView() {
        mPresenter = getBaseImpl(LoginContract.class, this);
    }

    @OnClick(R.id.btn_login)
    void login(){
//        showProgress("正在登陆");
        ((LoginContract)mPresenter).onLogin(mName.getText().toString(),mPwd.getText().toString());
    }

    @Override
    public void onLoadComplete(UserVO body) {
//        hideProgress();
        startActivity(new Intent(LoginActivity.this, MainActivity.class).putExtra("token",body.getUser_token()));
    }

    @Override
    public void onLoadFailer(String msg) {
        showErrorMessage("网络错误", msg);
    }
}
