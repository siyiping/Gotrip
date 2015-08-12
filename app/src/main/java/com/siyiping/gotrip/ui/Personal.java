package com.siyiping.gotrip.ui;


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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.siyiping.gotrip.LoginActivity;
import com.example.siyiping.gotrip.OfflineMapManageActivity;
import com.example.siyiping.gotrip.R;
import com.siyiping.gotrip.control.UserInfo;


/**
 * Created by siyiping on 15/6/17.
 */
public class Personal extends Fragment implements OnClickListener{

	private Button mSignin;
	private View mRootView;
    private LinearLayout mPersonalPanel;
    private TextView mNickname;
    private TextView mTelephone;
    private View mOfflinemapManage;

    private UserInfo mUserInfo;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mUserInfo=new UserInfo();
        mUserInfo.setCurrentStatus(true);
        Log.i("siyiping","personal fragment oncreate");
    	mRootView=inflater.inflate(R.layout.personalfragment,container);
    	
        initView(mRootView);
        
        return super.onCreateView(inflater, container, savedInstanceState);
    }
    
    private void initView(View view){
    	mSignin=(Button)view.findViewById(R.id.clicktosignin);
    	mSignin.setOnClickListener(this);

        mPersonalPanel=(LinearLayout)view.findViewById(R.id.personalpanel);
        mNickname=(TextView)view.findViewById(R.id.nickname);
        mTelephone=(TextView)view.findViewById(R.id.telephone);

        mOfflinemapManage=view.findViewById(R.id.offlinemapmanage);
        mOfflinemapManage.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mUserInfo=null;
        mUserInfo=new UserInfo();
        if(mUserInfo.getCurrentStatus() && mNickname != null && mTelephone != null){
            Log.i("siyiping","mUserInfo  is  online");
            mSignin.setVisibility(View.GONE);
            mPersonalPanel.setVisibility(View.VISIBLE);
            mNickname.setText(mUserInfo.getCurrentUser().getUsername());
            mTelephone.setText(mUserInfo.getCurrentUser().getMobilePhoneNumber());
        }else{
            Log.i("siyiping","mUserInfo  is  offline");
            mSignin.setVisibility(View.VISIBLE);
            mPersonalPanel.setVisibility(View.GONE);
        }
    }

    @Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		    case R.id.clicktosignin:
                Intent intent=new Intent();
                intent.setClass(getActivity(), LoginActivity.class);
                startActivity(intent);
                break;

            case R.id.offlinemapmanage:
                intent=new Intent();
                intent.setClass(getActivity(), OfflineMapManageActivity.class);
                startActivity(intent);
                break;
		
		}
		
	}

    
}
