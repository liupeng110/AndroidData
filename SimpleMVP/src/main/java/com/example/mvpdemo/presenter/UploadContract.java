package com.example.mvpdemo.presenter;

import com.example.mvpdemo.annotation.Implement;

import java.io.File;

/**
 * Created by LiuLei on 2017/11/27.
 */
@Implement(UploadPresenter.class)
public interface UploadContract {
    //上传
    void onUpload(File file, String token);
}
