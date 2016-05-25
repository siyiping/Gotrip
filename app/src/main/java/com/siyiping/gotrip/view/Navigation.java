package com.siyiping.gotrip.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.baidu.mapapi.map.FileTileProvider;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Tile;
import com.baidu.mapapi.map.TileOverlay;
import com.baidu.mapapi.map.TileOverlayOptions;
import com.baidu.mapapi.map.TileProvider;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.siyiping.gotrip.BaseApplication;
import com.siyiping.gotrip.R;
import com.siyiping.gotrip.utils.Utils;
import com.tencent.tauth.bean.UserInfo;

import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * Created by siyiping on 15/6/17.
 */
public class Navigation extends Fragment implements BaiduMap.OnMapLoadedCallback, BaiduMap.OnMapStatusChangeListener{

    public Context context;
    // 设置瓦片图的在线缓存大小，默认为20 M
    private static final int TILE_TMP = 20 * 1024 * 1024;
    private static final int MAX_LEVEL = 21;
    private static final int MIN_LEVEL = 3;
    // 定位相关
    LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    private LocationMode mCurrentMode;
    private double currentLongitude=0;//经度
    private double currentLatitude=0;//纬度
    // 是否首次定位
    boolean isFirstLoc = true;
    //搜索相关
    public GeoCoder mSearch;
    private boolean getCitySuccess=false;

    MapStatusUpdate mMapStatusUpdate;
    TileProvider tileProvider;
    TileOverlay tileOverlay;
    Tile offlineTile;

    View view;
    MapView mMapView;
    UserInfo user;

    BaiduMap mBaiduMap;
    private boolean mapLoaded = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=this.getActivity().getApplicationContext();
        //初始化百度地图sdk
        SDKInitializer.initialize(getActivity().getApplication().getApplicationContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.navigationfragment, null);
        }

        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }

        return view;
    }

    @Override
    public void onStart() {
        if (mMapView == null || mBaiduMap == null){
            mMapView = (MapView) view.findViewById(R.id.bmapView);
            mBaiduMap = mMapView.getMap();
            mBaiduMap.setOnMapLoadedCallback(this);
            mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, null));

            //定义地图状态
            MapStatus mMapStatus = new MapStatus.Builder()
                    .zoom(18)
                    .build();
            //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
            mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
            //改变地图状态
            mBaiduMap.setMapStatus(mMapStatusUpdate);
        }
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);

        Double mLastLongitude=((BaseApplication)getActivity().getApplication()).mLastLongitude;
        Double mLastLatitude=((BaseApplication) getActivity().getApplication()).mLastLatitude;
        MyLocationData locData = new MyLocationData.Builder()
                // 此处设置开发者获取到的方向信息，顺时针0-360
                .direction(100).latitude(mLastLongitude)
                .longitude(mLastLatitude).build();
        mBaiduMap.setMyLocationData(locData);

        mBaiduMap.setOnMapStatusChangeListener(this);
        // 定位初始化
        mLocClient= new LocationClient(context);
        mLocClient.registerLocationListener(myListener);

        LocationClientOption locationClientOption=new LocationClientOption();
        locationClientOption.setOpenGps(true);
        locationClientOption.setCoorType("bd09ll");
        locationClientOption.setIsNeedAddress(true);//不开无法获得城市街道等信息
        locationClientOption.setScanSpan(5000);
        locationClientOption.disableCache(true);
        locationClientOption.setIsNeedLocationPoiList(true);
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

            //反geo搜索结果获取具体地址信息
            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult erseGeoCodeResult) {
                ReverseGeoCodeResult.AddressComponent addressDetail=erseGeoCodeResult.getAddressDetail();
                if(addressDetail==null)
                    return;
                getCitySuccess=true;
                SharedPreferences writeCityName = context.getSharedPreferences("current_city",Context.MODE_PRIVATE);
                writeCityName.edit().putString("currentCity",addressDetail.city).apply();
            }
        });

        super.onStart();
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        mLocClient.stop();
        super.onPause();
    }

    @Override
    public void onResume() {
        //offlineTile();
        mMapView.onResume();

        //请求定位
        mLocClient.start();
        if (mLocClient != null && mLocClient.isStarted()) {
            mLocClient.requestLocation();
        }
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        //mMapView.onDestroy();
        SharedPreferences lastlocation= context.getSharedPreferences("lastlocation",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=lastlocation.edit();
        if(currentLongitude != 0 &&  currentLatitude != 0){
            editor.putString("Longitude",String.valueOf(currentLongitude));
            editor.putString("Latitude",String.valueOf(currentLatitude));
            editor.apply();
        }
        Log.i("siyiping","commit  currentLongitude"+currentLongitude+" currentLatitude"+currentLatitude);
        mLocClient.stop();
        mLocClient.unRegisterLocationListener(myListener);

        mBaiduMap.setMyLocationEnabled(false);
        super.onDestroyView();
    }

    @Override
    public void onMapLoaded() {
        mapLoaded = true;
    }

    @Override
    public void onMapStatusChangeStart(MapStatus mapStatus) {

    }

    @Override
    public void onMapStatusChange(MapStatus mapStatus) {

    }

    @Override
    public void onMapStatusChangeFinish(MapStatus mapStatus) {

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

            currentLongitude=locData.longitude;//经度
            currentLatitude=locData.latitude;//纬度
            Log.i(Utils.TAG,"currentLongitude->"+currentLongitude+"  currentLatitude"+currentLatitude);
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

    /**
     * 瓦片图的离线添加
     */
    private void offlineTile() {
        if (tileOverlay != null && mBaiduMap != null) {
            tileOverlay.removeTileOverlay();
        }

        /**
         * 定义瓦片图的离线Provider，并实现相关接口
         * MAX_LEVEL、MIN_LEVEL 表示地图显示瓦片图的最大、最小级别
         * Tile 对象表示地图每个x、y、z状态下的瓦片对象
         */
        tileProvider = new FileTileProvider() {
            @Override
            public Tile getTile(int x, int y, int z) {
                // 根据地图某一状态下x、y、z加载指定的瓦片图
                Log.i(Utils.TAG,"x->"+x+" y->"+y+" z->"+z);
                String filedir = "LocalTileImage/" + z + "/" + z + "_" + x + "_" + y + ".png";
                Bitmap bm = getFromAssets(filedir);
                if (bm == null) {
                    return null;
                }
                // 瓦片图尺寸必须满足256 * 256
                offlineTile = new Tile(bm.getWidth(), bm.getHeight(), toRawData(bm));
                bm.recycle();
                return offlineTile;
            }

            @Override
            public int getMaxDisLevel() {
                return MAX_LEVEL;
            }

            @Override
            public int getMinDisLevel() {
                return MIN_LEVEL;
            }

        };
        TileOverlayOptions options = new TileOverlayOptions();
        // 构造显示瓦片图范围，当前为世界范围
        LatLng northeast = new LatLng(80, 180);
        LatLng southwest = new LatLng(-80, -180);
        // 设置离线瓦片图属性option
        options.tileProvider(tileProvider)
                .setPositionFromBounds(new LatLngBounds.Builder().include(northeast).include(southwest).build());
        // 通过option指定相关属性，向地图添加离线瓦片图对象
        tileOverlay = mBaiduMap.addTileLayer(options);

        if (mapLoaded) {
            setMapStatusLimits();
        }
    }

    private void setMapStatusLimits() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(new LatLng(39.94001804746338, 116.41224644234747))
                .include(new LatLng(39.90299859954822, 116.38359947963427));
        mBaiduMap.setMapStatusLimits(builder.build());
        mBaiduMap.setMaxAndMinZoomLevel(17.0f, 16.0f);
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NONE);
    }

    /**
     * 瓦片文件解析为Bitmap
     * @param fileName
     * @return 瓦片文件的Bitmap
     */
    public Bitmap getFromAssets(String fileName) {
        AssetManager am = context.getAssets();
        InputStream is = null;
        Bitmap bm;

        try {
            is = am.open(fileName);
            bm = BitmapFactory.decodeStream(is);
            return bm;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 解析Bitmap
     * @param bitmap
     * @return
     */
    byte[] toRawData(Bitmap bitmap) {
        ByteBuffer buffer = ByteBuffer.allocate(bitmap.getWidth()
                * bitmap.getHeight() * 4);
        bitmap.copyPixelsToBuffer(buffer);
        byte[] data = buffer.array();
        buffer.clear();
        return data;
    }
}



