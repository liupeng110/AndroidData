package com.example.mvpdemo.view;

import android.content.Intent;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.mvpdemo.R;
import com.example.mvpdemo.api.model.DeviceVO;
import com.example.mvpdemo.base.BaseActivity;
import com.example.mvpdemo.base.model.LoadListDataLogic;
import com.example.mvpdemo.presenter.MainContract;
import com.example.mvpdemo.presenter.MainPresenter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

public class MainActivity extends BaseActivity implements LoadListDataLogic.LoadListView<List<DeviceVO>>{

    private static final String TAG = "MainActivity";
    @Bind(R.id.listView)
    ListView mListView;

    private ArrayAdapter<DeviceVO> mAdapter;
    private List<DeviceVO> mLists = new ArrayList<>();
    private String mToken;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_main;
    }

    @Override
    protected void onInitView() {
        mPresenter = getBaseImpl(MainContract.class, this);
        mToken = getIntent().getStringExtra("token");
        initData();
    }

    private void initData() {
        mAdapter = new ArrayAdapter<DeviceVO>(this,android.R.layout.simple_list_item_1,mLists);
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void onLoadCompleteData(List<DeviceVO> body, boolean isMore) {
        mLists.clear();
        mLists.addAll(body);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoadComplete(boolean isMore) {

    }

    @OnClick(R.id.load)
    void load(){
        if(!TextUtils.isEmpty(mToken)){
            ((MainPresenter) mPresenter).onLoadDeviceList(false, mToken);
        }
    }

    @OnClick(R.id.next)
    void next(){
        if(!TextUtils.isEmpty(mToken)){
            startActivity(new Intent(MainActivity.this,UploadActivity.class).putExtra("token",mToken));
        }
    }
}
