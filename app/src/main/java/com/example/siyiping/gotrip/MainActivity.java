package com.example.siyiping.gotrip;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
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
import com.avos.avoscloud.AVOSCloud;
import com.siyiping.gotrip.ui.Navigation;
import com.siyiping.gotrip.ui.Personal;
import com.siyiping.gotrip.ui.Strategy;


public class MainActivity extends FragmentActivity  {

    private String TAG="gotrip";

    private FragmentTabHost mTabhost=null;
    private View indicator=null;
    private Resources mRes;
    private Intent mIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);
        AVOSCloud.initialize(this, "dncec7gcbcmyxumaunavxokqb2ujh04xeagyj0t110xqgpa9", "krsmwsb0c00avt8xpqd60v8135vuq3mdyfjsysz0ppwddc45");
        AVOSCloud.setDebugLogEnabled(true);
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

    private View getIndicatorView(String name, int layoutId) {
        View v = getLayoutInflater().inflate(layoutId, null);
        TextView tv = (TextView) v.findViewById(R.id.tabText);
        tv.setText(name);
        return v;
    }


}
