package com.siyiping.gotrip.control;

import android.util.Log;

import com.avos.avoscloud.AVUser;

import java.util.HashMap;

/**
 * Created by siyiping on 15/7/25.
 */
public class UserInfo {

    public static final int NETWORKTYPE_INVALID=0;
    public static final int NETWORKTYPE_WIFI=1;
    public static final int NETWORKTYPE_MOBILE=2;
    public static final int NETWORKTYPE_4G=3;
    public static final int NETWORKTYPE_3G=4;
    public static final int NETWORKTYPE_2G=5;

    //用户对象
    private AVUser mCurrentUser=null;
    //用户登录状态
    private boolean mCurrentStatus=false;

    //保存当前网络类型
    private int netWorkType=NETWORKTYPE_INVALID;

    //当前城市
    private String currentCity;

    //目的地城市
    private HashMap<String,String> destinationCity;

    public int getNetWorkType() {
        return netWorkType;
    }

    public void setNetWorkType(int netWorkType) {
        this.netWorkType = netWorkType;
    }

    public String getCurrentCity() {
        return currentCity;
    }

    public void setCurrentCity(String currentCity) {
        this.currentCity = currentCity;
    }

    public AVUser getCurrentUser() {
        return mCurrentUser;
    }

    public void setCurrentUser(AVUser currentUser) {
        this.mCurrentUser = currentUser;
    }

    public boolean getCurrentStatus() {
        Log.i("siyiping","get   mCurrentStatus = "+mCurrentStatus);
        return mCurrentStatus;
    }

    public void setCurrentStatus(boolean currentStatus) {
        this.mCurrentStatus=currentStatus;
        Log.i("siyiping","this.mCurrentStatus = "+this.getCurrentStatus());
    }
}
