package com.siyiping.gotrip.view;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.avos.avoscloud.AVAnalytics;
import com.siyiping.gotrip.R;
import com.siyiping.gotrip.service.NetWork;


public class MainActivity extends AppCompatActivity {

    private FragmentTabHost mTabhost=null;
    private View indicator=null;
    private Resources mRes;
    private Intent mIntent;
    private Intent mIntentNetWorkServiece;
    private NetWork netWorkService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRes=getResources();

        mIntent=getIntent();
        String tabtag=mIntent.getStringExtra("tabtag");

        mTabhost=(FragmentTabHost)findViewById(android.R.id.tabhost);
        mTabhost.setup(this,getSupportFragmentManager(),R.id.realtabcontent);

        indicator=getIndicatorView(mRes.getString(R.string.navigation),R.drawable.guide);
        mTabhost.addTab(mTabhost.newTabSpec("guide").setIndicator(indicator), Navigation.class, null);

        indicator=getIndicatorView(mRes.getString(R.string.strategy),R.drawable.strategy);
        mTabhost.addTab(mTabhost.newTabSpec("strategy").setIndicator(indicator),Strategy.class,null);

        indicator=getIndicatorView(mRes.getString(R.string.personal),R.drawable.personal);
        mTabhost.addTab(mTabhost.newTabSpec("personal").setIndicator(indicator), Personal.class, null);

        if(TextUtils.isEmpty(tabtag)){
            mTabhost.setCurrentTabByTag(tabtag);
        }else{
            mTabhost.setCurrentTabByTag("guide");
        }

        AVAnalytics.trackAppOpened(getIntent());

        mTabhost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                mTabhost.setCurrentTabByTag(tabId);
            }
        });

        mIntentNetWorkServiece=new Intent();
        mIntentNetWorkServiece.setClass(this, NetWork.class);
        startService(mIntentNetWorkServiece);
        bindService(mIntentNetWorkServiece,netWorkServiceCon, Context.BIND_AUTO_CREATE);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        unbindService(netWorkServiceCon);
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
    private View getIndicatorView(String name, int drawableId) {
        View v = getLayoutInflater().inflate(R.layout.tab_widget, null);
        TextView tv = (TextView) v.findViewById(R.id.tab_indicator_text);
        tv.setText(name);
        ImageView iv = (ImageView) v.findViewById(R.id.tab_indicator_image);
        iv.setImageResource(drawableId);
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
