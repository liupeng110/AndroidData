package com.example.mvpdemo.view;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.example.mvpdemo.R;
import com.example.mvpdemo.base.BaseActivity;
import com.example.mvpdemo.base.model.LoadEveryLogic;
import com.example.mvpdemo.presenter.LoginContract;
import com.example.mvpdemo.presenter.UploadContract;
import com.example.mvpdemo.presenter.UploadPresenter;
import com.example.mvpdemo.utils.ToastUtil;

import java.io.File;

import butterknife.Bind;
import butterknife.OnClick;

public class UploadActivity extends BaseActivity implements LoadEveryLogic.LoadEveryView{

    @Bind(R.id.imageView)
    ImageView mImg;
    private String mToken;
    private File mFile;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_upload;
    }

    @Override
    protected void onInitView() {
        mToken = getIntent().getStringExtra("token");
        mPresenter = getBaseImpl(UploadContract.class, this);
    }

    @OnClick(R.id.upload_picture)
    void upload(){
        mFile=new File("/storage/emulated/0/11.jpg");

        ((UploadPresenter)mPresenter).onUpload(mFile,mToken);
//        Album.album(this)
//                .requestCode(999) // Request code.
//                .toolBarColor(getResources().getColor(R.color.albumColorPrimary)) // Toolbar color.
//                .statusBarColor(getResources().getColor(R.color.albumColorPrimary)) // StatusBar color.
//                .navigationBarColor(getResources().getColor(R.color.albumColorPrimary)) // NavigationBar color.
//                .title("Album") // Title.
//
//                .selectCount(9) // Choose up to a few pictures.
//                .columnCount(2) // Number of albums.
//                .camera(true) // Have a camera function.
//                .checkedList(mImageList) // Has selected the picture, automatically select.
//                .start();
    }

    @Override
    public void onLoadComplete(Object body) {
        Bitmap bitmap = BitmapFactory.decodeFile(mFile.getAbsolutePath());
        mImg.setImageBitmap(bitmap);
    }

    @Override
    public void onLoadFailer(String msg) {
        ToastUtil.show("上传头像失败");
    }
}
