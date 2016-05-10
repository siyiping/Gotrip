package com.siyiping.gotrip;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Vibrator;
import android.util.Log;
import android.widget.TextView;

import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVUser;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.Poi;
import com.baidu.mapapi.SDKInitializer;

import java.util.List;

/**
 * 主Application，所有百度定位SDK的接口说明请参考线上文档：http://developer.baidu.com/map/loc_refer/index.html
 *
 * 百度定位SDK官方网站：http://developer.baidu.com/map/index.php?title=android-locsdk
 */
public class BaseApplication extends Application {

    //联网状态
    public static final int NETWORKTYPE_INVALID=0;
    public static final int NETWORKTYPE_WIFI=1;
    public static final int NETWORKTYPE_MOBILE=2;
    public static final int NETWORKTYPE_4G=3;
    public static final int NETWORKTYPE_3G=4;
    public static final int NETWORKTYPE_2G=5;

    public LocationClient mLocationClient;
    public MyLocationListener mMyLocationListener;

    public TextView mLocationResult,logMsg;
    public Vibrator mVibrator;
    public double mLastLongitude,mLastLatitude;

    public int mSystemVersion;

    //用户对象
    private AVUser mCurrentUser;
    //用户登录状态
    private boolean mCurrentStatus;
    //保存当前网络类型
    private int netWorkType=NETWORKTYPE_INVALID;

    @Override
    public void onCreate() {
        super.onCreate();

        //LeanCloud 云存储等云服务初始化
        AVOSCloud.initialize(this, "dncec7gcbcmyxumaunavxokqb2ujh04xeagyj0t110xqgpa9", "krsmwsb0c00avt8xpqd60v8135vuq3mdyfjsysz0ppwddc45");
        AVOSCloud.setDebugLogEnabled(true);

        //初始化百度地图sdk
        SDKInitializer.initialize(getApplicationContext());

        //android系统版本
        mSystemVersion= Build.VERSION.SDK_INT;

        mCurrentUser=null;

        mLocationClient = new LocationClient(this.getApplicationContext());
        mMyLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mMyLocationListener);
        mVibrator =(Vibrator)getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);

        //获取上一次定位的地址
        SharedPreferences getLastLocation=getSharedPreferences("lastlocation", Activity.MODE_PRIVATE);
        mLastLongitude=Double.valueOf(getLastLocation.getString("Longitude",String.valueOf("116.232922")));
        mLastLatitude=Double.valueOf(getLastLocation.getString("Latitude",String.valueOf("39.264103")));
    }

    public AVUser getCurrentUser() {
        return mCurrentUser;
    }

    public void setCurrentUser(AVUser currentUser) {
        this.mCurrentUser = currentUser;
    }

    public boolean getCurrentStatus() {
        return mCurrentStatus;
    }

    public void setCurrentStatus(boolean currentStatus) {
        this.mCurrentStatus=currentStatus;
    }

    public int getNetWorkType() {
        return netWorkType;
    }

    public void setNetWorkType(int netWorkType) {
        this.netWorkType = netWorkType;
    }

    /**
     * 实现实时位置回调监听
     */
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            //Receive Location
            StringBuffer sb = new StringBuffer(256);
            sb.append("time : ");
            sb.append(location.getTime());
            sb.append("\nerror code : ");
            sb.append(location.getLocType());
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());
            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());
            sb.append("\nradius : ");
            sb.append(location.getRadius());
            if (location.getLocType() == BDLocation.TypeGpsLocation){// GPS定位结果
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());// 单位：公里每小时
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());
                sb.append("\nheight : ");
                sb.append(location.getAltitude());// 单位：米
                sb.append("\ndirection : ");
                sb.append(location.getDirection());
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                sb.append("\ndescribe : ");
                sb.append("gps定位成功");

            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation){// 网络定位结果
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                //运营商信息
                sb.append("\noperationers : ");
                sb.append(location.getOperators());
                sb.append("\ndescribe : ");
                sb.append("网络定位成功");
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                sb.append("\ndescribe : ");
                sb.append("离线定位成功，离线定位结果也是有效的");
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                sb.append("\ndescribe : ");
                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                sb.append("\ndescribe : ");
                sb.append("网络不同导致定位失败，请检查网络是否通畅");
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                sb.append("\ndescribe : ");
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
            }
            sb.append("\nlocationdescribe : ");// 位置语义化信息
            sb.append(location.getLocationDescribe());
            List<Poi> list = location.getPoiList();// POI信息
            if (list != null) {
                sb.append("\npoilist size = : ");
                sb.append(list.size());
                for (Poi p : list) {
                    sb.append("\npoi= : ");
                    sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
                }
            }
            logMsg(sb.toString());
            Log.i("BaiduLocationApiDem", sb.toString());
        }


    }


    /**
     * 显示请求字符串
     * @param str
     */
    public void logMsg(String str) {
        try {
            if (mLocationResult != null)
                mLocationResult.setText(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
