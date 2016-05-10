package com.siyiping.gotrip.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.IBinder;

import com.siyiping.gotrip.BaseApplication;

public class NetWork extends Service {

    Context mContext;
    ConnectivityManager connectivityManager;

    //Baseapplication
    BaseApplication mBaseApplication;
    @Override
    public void onCreate() {
        mContext = getApplicationContext();
        connectivityManager=(ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        //用application保存网络状态
        mBaseApplication=(BaseApplication)getApplication();

        super.onCreate();
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION); // 添加接收网络连接状态改变的Action
        registerReceiver(mReceiver, mFilter);
        if(isWIFIConnected(connectivityManager)){
            Intent mIntentDownloadOfflineMap= new Intent(mContext, downloadOfflineMapCon.getClass());
            startService(mIntentDownloadOfflineMap);
            bindService(mIntentDownloadOfflineMap,downloadOfflineMapCon,Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new LocalBinder();
    }


    public class LocalBinder extends Binder{
        public NetWork getService(){
            return NetWork.this;
        }
    }

    private BroadcastReceiver mReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)){
                if(isNetworkConnected(connectivityManager)){
                    if(isWIFIConnected(connectivityManager)){
                        mBaseApplication.setNetWorkType(mBaseApplication.NETWORKTYPE_WIFI);
                    }else{
                        mBaseApplication.setNetWorkType(mBaseApplication.NETWORKTYPE_MOBILE);
                    }

                }
            }
        }
    };

    public boolean isNetworkConnected(ConnectivityManager connectivityManager){
        if(connectivityManager!=null) {
            NetworkInfo mNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isConnected();
            }
        }
        return false;
    }

    public boolean isWIFIConnected(ConnectivityManager connectivityManager){
        if(connectivityManager!=null){
            NetworkInfo mWIFIInfo= connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if(mWIFIInfo!=null){
                return mWIFIInfo.isConnected();
            }
        }
        return false;
    }

    private ServiceConnection downloadOfflineMapCon = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
}
