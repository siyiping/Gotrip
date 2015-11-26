package com.siyiping.gotrip.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.offline.MKOLSearchRecord;
import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.baidu.mapapi.map.offline.MKOfflineMapListener;
import com.siyiping.gotrip.R;
import com.siyiping.gotrip.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class OfflineMapManageActivity extends Activity implements MKOfflineMapListener {

    private MKOfflineMap mOffline = null;

    /**
     * 已下载的离线地图信息列表
     */
    private ArrayList<MKOLUpdateElement> localMapList = null;
    private LocalMapAdapter lAdapter = null;
    //需要更新的地图列表
    private ArrayList<MKOLUpdateElement> needUpdateCity;
    //当前正在更新的城市
    private MKOLUpdateElement currentDownloadCity;


    private ListView mOfflineMap;
    private LocalMapAdapter mAdapter;
    private AutoCompleteTextView citySearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_map_manage);

        mOffline = new MKOfflineMap();
        mOffline.init(this);

        initView();
        checkCurrentCityisDownload();
    }

    @Override
    protected void onPause() {

        if (getCurrentDownloadCity() != null) {
            int cityid = getCurrentDownloadCity().cityID;
            MKOLUpdateElement temp = mOffline.getUpdateInfo(cityid);
            if (temp != null && temp.status == MKOLUpdateElement.DOWNLOADING) {
                mOffline.pause(cityid);
            }
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mOffline.destroy();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.setClass(this,Personal.class);
        startActivity(intent);
        this.finish();
        super.onBackPressed();
    }

    public void checkCurrentCityisDownload(){
        String cityName = getSharedPreferences("current_city",MODE_PRIVATE).getString("currentCity","");
        int cityID = -1;
        // 获取热闹城市列表
        ArrayList<MKOLSearchRecord> records1 = mOffline.getHotCityList();
        for (MKOLSearchRecord r1 : records1){
            if (r1.cityName.equals(cityName)) {
                cityID = r1.cityID;
            }
        }
        // 获取所有支持离线地图的省
        ArrayList<MKOLSearchRecord> records2 = mOffline.getOfflineCityList();
        for (MKOLSearchRecord r2 : records2) {
            if (r2.cityName.equals(cityName)) {
                cityID = r2.cityID;
            }
        }
        Log.i(Utils.TAG, "HotCity count->"+records1.size()+" fflineCity"+records2.size());
        if (cityID == -1){
            return;
        }
        Log.i(Utils.TAG,"check current city ->"+cityName);
        if (localMapList.size() == 0){
            beginDownloadMap(cityID);
        }else {
            for (MKOLUpdateElement city : localMapList) {
                if (!city.cityName.equals(cityName)) {
                    beginDownloadMap(cityID);
                }

            }
        }
    }

    /**
     * 开始下载
     */
    public void beginDownloadMap(int cityID) {
        mOffline.start(cityID);
        Toast.makeText(this, "开始下载离线地图. cityid: " + cityID, Toast.LENGTH_SHORT).show();
        updateView();
    }

    @Override
    public void onGetOfflineMapState(int type, int state) {
        switch (type) {
            case MKOfflineMap.TYPE_DOWNLOAD_UPDATE: {
                MKOLUpdateElement update = mOffline.getUpdateInfo(state);
                // 处理下载进度更新提示
                updateView();

            }
            break;
            case MKOfflineMap.TYPE_NEW_OFFLINE:
                // 有新离线地图安装
                Log.d("OfflineDemo", String.format("add offlinemap num:%d", state));
                break;
            case MKOfflineMap.TYPE_VER_UPDATE:
                // 版本更新提示
                // MKOLUpdateElement e = mOffline.getUpdateInfo(state);

                break;
        }

    }

    public void initView(){

        // 获取已下过的离线地图信息
        localMapList = mOffline.getAllUpdateInfo();
        if (localMapList == null) {
            localMapList = new ArrayList<MKOLUpdateElement>();
        }

        mOfflineMap = (ListView)findViewById(R.id.offline_map);
        mAdapter = new LocalMapAdapter(this,localMapList);
        mOfflineMap.setAdapter(mAdapter);

        ArrayList<MKOLSearchRecord> allSupportCityList = new ArrayList<>();
        allSupportCityList.addAll(mOffline.getOfflineCityList());

        ArrayList<MKOLSearchRecord> temp = new ArrayList<>();
        for (Iterator iterator = allSupportCityList.iterator(); iterator.hasNext();){
            MKOLSearchRecord element = (MKOLSearchRecord)iterator.next();
            if (element.childCities != null) {
                temp.addAll(element.childCities);
            }
        }
        allSupportCityList.addAll(temp);
        final ArrayList<Map<String,String>> arrayList = new ArrayList<>();
        int i;
        Map<String,String> map;
        for (i = 0 ; i < allSupportCityList.size(); i++){
            map = new HashMap<>();
            map.put("cityname", allSupportCityList.get(i).cityName);
            map.put("citysize", formatDataSize(allSupportCityList.get(i).size));
            map.put("cityid", allSupportCityList.get(i).cityID+"");
            arrayList.add(map);
        }

        SimpleAdapter offlineMapSupportList =  new SimpleAdapter(this,arrayList,
                    R.layout.offline_map_support_city,
                    new String[]{"cityname","citysize"},
                    new int[]{R.id.support_city_name,R.id.support_city_size} );

        citySearch = (AutoCompleteTextView)findViewById(R.id.offlinemap_search);
        citySearch.setThreshold(1);
        citySearch.setAdapter(offlineMapSupportList);

        citySearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                beginDownloadMap(Integer.parseInt(arrayList.get(position).get("cityid")));
                //citySearch.setText("");
            }
        });
    }


    /**
     * 更新状态显示
     */
    public void updateView() {
        localMapList = mOffline.getAllUpdateInfo();
        if (localMapList == null) {
            localMapList = new ArrayList<MKOLUpdateElement>();
        }
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 离线地图管理列表适配器
     */
    public class LocalMapAdapter extends BaseAdapter {

        ArrayList<MKOLUpdateElement> localMapList;

        public LocalMapAdapter (Context context,ArrayList<MKOLUpdateElement> localMapList){
            this.localMapList = localMapList;
        }

        @Override
        public int getCount() {
            return localMapList.size();
        }

        @Override
        public Object getItem(int index) {
            return localMapList.get(index);
        }

        @Override
        public long getItemId(int index) {
            return index;
        }

        @Override
        public View getView(int index, View view, ViewGroup arg2) {
            MKOLUpdateElement e = (MKOLUpdateElement) getItem(index);
            view = View.inflate(OfflineMapManageActivity.this,
                    R.layout.offline_localmap_list, null);
            initViewItem(view, e, index);
            //startDownload(needUpdateCity);
            return view;
        }

        void initViewItem(View view, final MKOLUpdateElement e, int index) {
            TextView cityname = (TextView) view.findViewById(R.id.city_name);
            TextView citymapSize = (TextView) view.findViewById(R.id.citymap_size);
            Button mapStatus = (Button) view.findViewById(R.id.map_status);
            mapStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    needUpdateCity.add(e);
                }
            });
            mapStatus.setEnabled(true);
            cityname.setText(e.cityName);
            citymapSize.setText(formatDataSize(e.size));
            if(!e.update){
                mapStatus.setText(getResources().getString(R.string.offline_map_downloaded));
                mapStatus.setEnabled(false);
            }else {
                mapStatus.setText(getResources().getString(R.string.offline_map_update));
                mapStatus.setEnabled(true);
            }

        }

    }

    public String formatDataSize(int size) {
        String ret = "";
        if (size < (1024 * 1024)) {
            ret = String.format("%dK", size / 1024);
        } else {
            ret = String.format("%.1fM", size / (1024 * 1024.0));
        }
        return ret;
    }

    public MKOLUpdateElement getCurrentDownloadCity() {
        return currentDownloadCity;
    }

    public void setCurrentDownloadCity(MKOLUpdateElement currentDownloadCity) {
        this.currentDownloadCity = currentDownloadCity;
    }

}
