package com.siyiping.gotrip.control;

/**
 * Created by siyiping on 15/7/25.
 */
public class DeviceInfo {

    public static final int NETWORKTYPE_INVALID=0;
    public static final int NETWORKTYPE_WIFI=1;
    public static final int NETWORKTYPE_MOBILE=2;
    public static final int NETWORKTYPE_4G=3;
    public static final int NETWORKTYPE_3G=4;
    public static final int NETWORKTYPE_2G=5;

    private int netWorkType=NETWORKTYPE_INVALID;

    public int getNetWorkType() {
        return netWorkType;
    }

    public void setNetWorkType(int netWorkType) {
        this.netWorkType = netWorkType;
    }






}
