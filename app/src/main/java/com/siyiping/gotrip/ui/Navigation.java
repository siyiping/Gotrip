package com.siyiping.gotrip.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.example.siyiping.gotrip.R;
import com.siyiping.gotrip.control.UserInfo;

/**
 * Created by siyiping on 15/6/17.
 */
public class Navigation extends Fragment {

    public Context context;

    // 定位相关
    LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    private LocationMode mCurrentMode;
    // 是否首次定位
    boolean isFirstLoc = true;
    //搜索相关
    public GeoCoder mSearch;
    private boolean getCitySuccess=false;

    View view;
    MapView mMapView = null;
    UserInfo user;

    BaiduMap mBaiduMap;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        context=this.getActivity().getApplicationContext();

        SDKInitializer.initialize(context);
        view = inflater.inflate(R.layout.navigationfragment, container);


        mMapView = (MapView) view.findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, null));

        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);

        user=new UserInfo();

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onStart() {

        // 定位初始化
        mLocClient= new LocationClient(context);
        mLocClient.registerLocationListener(myListener);

        LocationClientOption locationClientOption=new LocationClientOption();
        locationClientOption.setOpenGps(true);
        locationClientOption.setCoorType("bd09ll");
        locationClientOption.setIsNeedAddress(true);//不开无法获得城市街道等信息
        locationClientOption.setScanSpan(5000);
        locationClientOption.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);
        mLocClient.setLocOption(locationClientOption);
        mLocClient.start();
        if (mLocClient != null && mLocClient.isStarted()) {
            mLocClient.requestLocation();
        }

        mSearch=GeoCoder.newInstance();
        mSearch.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult erseGeoCodeResult) {
                ReverseGeoCodeResult.AddressComponent addressDetail=erseGeoCodeResult.getAddressDetail();
                if(addressDetail==null)
                    return;
                getCitySuccess=true;
                user.setCurrentCity(addressDetail.city);
                SharedPreferences writeCityName = context.getSharedPreferences("current_city",Context.MODE_PRIVATE);
                writeCityName.edit().putString("currentCity",addressDetail.city).commit();
            }
        });

        super.onStart();
    }

    @Override
    public void onDestroyView() {
        //mMapView.onDestroy();
        mLocClient.stop();
        mLocClient.unRegisterLocationListener(myListener);

        mBaiduMap.setMyLocationEnabled(false);

        super.onDestroyView();
    }

    @Override
    public void onPause() {
        //mMapView.onPause();
        mLocClient.stop();
        super.onPause();
    }

    @Override
    public void onResume() {
        //mMapView.onResume();
        //地图显示最后一次定位的位置
        BDLocation lasttimeLocation=mLocClient.getLastKnownLocation();
        if(lasttimeLocation!=null) {
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(lasttimeLocation.getRadius())
                    .latitude(lasttimeLocation.getLatitude())
                    .longitude(lasttimeLocation.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
        }
        //重新定位
        mLocClient.start();
        if (mLocClient != null && mLocClient.isStarted()) {
            mLocClient.requestLocation();
        }
        super.onResume();
    }


    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            Log.i("siyiping","receive location listener");
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null)
                return;
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                            // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();

            double currentLongitude=locData.longitude;//经度
            double currentLatitude=locData.latitude;//纬度

            LatLng ll = new LatLng(currentLatitude,
                    currentLongitude);//反地理编码

            //反geo搜索
            if(!getCitySuccess)
                mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(ll));

            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;

                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
                mBaiduMap.animateMapStatus(u);
            }
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }

}



