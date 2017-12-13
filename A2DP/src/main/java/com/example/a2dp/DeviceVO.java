package com.example.a2dp;

/**
 * Created by admin on 2016/12/30.
 */

public class DeviceVO {

    private String name;
    private String alias;
    private String address;
    private int state = 0;//1代表成功   0代表未连接   2代表正在连接中

    public DeviceVO(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return name;
    }
}
