package com.siyiping.gotrip.ui;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.siyiping.gotrip.LoginActivity;
import com.example.siyiping.gotrip.R;


/**
 * Created by siyiping on 15/6/17.
 */
public class Personal extends Fragment implements OnClickListener{

	private Button mSignin;
	
	private View mRootView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    	mRootView=inflater.inflate(R.layout.personalfragment,container);
    	
        initView(mRootView);
        
        return super.onCreateView(inflater, container, savedInstanceState);
    }
    
    private void initView(View view){
    	mSignin=(Button)view.findViewById(R.id.clicktosignin);
    	mSignin.setOnClickListener(this);
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
		
		}
		
	}
    
    
    
}
