package com.example.siyiping.gotrip;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TabHost;
import android.widget.TextView;

import com.avos.avoscloud.AVAnalytics;
import com.siyiping.gotrip.service.NetWork;
import com.siyiping.gotrip.ui.Navigation;
import com.siyiping.gotrip.ui.Personal;
import com.siyiping.gotrip.ui.Strategy;


public class MainActivity extends FragmentActivity  {

    private String TAG="gotrip";

    private FragmentTabHost mTabhost=null;
    private View indicator=null;
    private Resources mRes;
    private Intent mIntent;
    private Intent mIntentNetWorkServiece;
    private NetWork netWorkService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        requestWindowFeature(Window.FEATURE_NO_TITLE);


        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mIntent=getIntent();
        String tabtag=mIntent.getStringExtra("tabtag");

        mRes=getResources();

        mTabhost=(FragmentTabHost)findViewById(android.R.id.tabhost);
        mTabhost.setup(getApplicationContext(),getSupportFragmentManager(),android.R.id.tabcontent);

        indicator=getIndicatorView(mRes.getString(R.string.navigation),R.layout.navigationtab);
        mTabhost.addTab(mTabhost.newTabSpec("navigation").setIndicator(indicator), Navigation.class, null);

        indicator=getIndicatorView(mRes.getString(R.string.strategy),R.layout.strategytab);
        mTabhost.addTab(mTabhost.newTabSpec("strategy").setIndicator(indicator),Strategy.class,null);

        indicator=getIndicatorView(mRes.getString(R.string.personal),R.layout.personaltab);
        mTabhost.addTab(mTabhost.newTabSpec("personal").setIndicator(indicator), Personal.class, null);

        if(tabtag != null){
            mTabhost.setCurrentTabByTag(tabtag);
        }else{
            mTabhost.setCurrentTabByTag("personal");
        }

        AVAnalytics.trackAppOpened(getIntent());

        mTabhost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                mTabhost.setCurrentTabByTag(tabId);
                Log.i(TAG, "   tab  changed  ");
            }
        });

        mIntentNetWorkServiece=new Intent("com.siyiping.gotrip.service.NetWork");
        startService(mIntentNetWorkServiece);
        bindService(mIntentNetWorkServiece,netWorkServiceCon, Context.BIND_AUTO_CREATE);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        stopService(mIntentNetWorkServiece);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //获取tabwidget图标
    private View getIndicatorView(String name, int layoutId) {
        View v = getLayoutInflater().inflate(layoutId, null);
        TextView tv = (TextView) v.findViewById(R.id.tabText);
        tv.setText(name);
        return v;
    }


    //连网的服务
    private ServiceConnection netWorkServiceCon= new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            netWorkService=((NetWork.LocalBinder)service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
}
