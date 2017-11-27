package com.example.mvpdemo.presenter;

import android.widget.Toast;

import com.example.mvpdemo.api.BaseResponse;
import com.example.mvpdemo.api.upload.ProgressRequestBody;
import com.example.mvpdemo.api.upload.UploadProgressListener;
import com.example.mvpdemo.api.util.RetrofitUtil;
import com.example.mvpdemo.base.model.LoadEveryLogic;
import com.example.mvpdemo.base.model.LoadEveryLogicImpl;
import com.example.mvpdemo.utils.ToastUtil;

import java.io.File;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Subscriber;

/**
 * Created by LiuLei on 2017/11/27.
 */

public class UploadPresenter extends LoadEveryLogicImpl implements UploadContract{
    @Override
    public void onUpload(File file, String token) {

        HashMap<String,RequestBody> map = new HashMap<>();
        map.put("token",RequestBody.create(MediaType.parse("text/plain"),token));
        map.put("client_id",RequestBody.create(MediaType.parse("text/plain"),"0c313613013040acae32f088b069932a"));
        map.put("device_name",RequestBody.create(MediaType.parse("text/plain"),"傻了"));
        map.put("device_header",RequestBody.create(MediaType.parse("text/plain"),"device_header"));

        RequestBody requestBody=RequestBody.create(MediaType.parse("image/jpeg"),file);
        MultipartBody.Part part= MultipartBody.Part.createFormData("device_header", file.getName(), new ProgressRequestBody(requestBody,
                new UploadProgressListener() {
                    @Override
                    public void onProgress(long currentBytesCount, long totalBytesCount) {
//                        tvMsg.setText("提示:上传中");
//                        progressBar.setMax((int) totalBytesCount);
//                        progressBar.setProgress((int) currentBytesCount);
                    }
                }));
        RetrofitUtil.getInstance().uploadImg(part,map, new Subscriber<BaseResponse>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                ToastUtil.show("上传头像出错");
            }

            @Override
            public void onNext(BaseResponse baseResponse) {
                onLoadCompleteData(baseResponse);
            }
        });

    }
}
