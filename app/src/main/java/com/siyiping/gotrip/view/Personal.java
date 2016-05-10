package com.siyiping.gotrip.view;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVUser;
import com.siyiping.gotrip.BaseApplication;
import com.siyiping.gotrip.R;
import com.siyiping.gotrip.utils.Utils;


/**
 * Created by siyiping on 15/6/17.
 */
public class Personal extends Fragment implements OnClickListener{

	private Button mSignin;
	private View mRootView;
    private RelativeLayout mPersonalPanel;
    private TextView mNickname;
    private TextView mTelephone;
    private View mOfflinemapManage;

    //baseapplication
    BaseApplication mBaseApplication;

    //用户对象
    private AVUser mUser;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        mBaseApplication=(BaseApplication)getActivity().getApplication();
        //当前用户
        mUser=mBaseApplication.getCurrentUser();

    	mRootView=inflater.inflate(R.layout.personalfragment,container);
    	
        initView(mRootView);
        
        return super.onCreateView(inflater, container, savedInstanceState);
    }
    
    private void initView(View view){
    	mSignin=(Button)view.findViewById(R.id.clicktosignin);
    	mSignin.setOnClickListener(this);

        mPersonalPanel=(RelativeLayout)view.findViewById(R.id.personalpanel);
        mNickname=(TextView)view.findViewById(R.id.nickname);

        mOfflinemapManage=view.findViewById(R.id.offlinemapmanage);
        mOfflinemapManage.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mUser=((BaseApplication)getActivity().getApplication()).getCurrentUser();
        if(mBaseApplication.getCurrentStatus() && mNickname != null && mTelephone != null){
            mSignin.setVisibility(View.GONE);
            mPersonalPanel.setVisibility(View.VISIBLE);
            mNickname.setText(mUser.getUsername());
        }else{
            mSignin.setVisibility(View.VISIBLE);
            mPersonalPanel.setVisibility(View.GONE);
        }
    }

    @Override
	public void onClick(View v) {
        Log.i(Utils.TAG,"personal  on click");
		// TODO Auto-generated method stub
		switch(v.getId()){
		    case R.id.clicktosignin:
                Intent intent=new Intent();
                intent.setClass(getActivity(), LoginActivity.class);
                startActivity(intent);
                break;

            case R.id.offlinemapmanage:
                Log.i(Utils.TAG,"off line map click");
                intent=new Intent();
                intent.setClass(getActivity(), OfflineMapManageActivity.class);
                startActivity(intent);
                break;
		
		}
		
	}

    
}
