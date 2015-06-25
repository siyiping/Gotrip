package com.siyiping.gotrip.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.siyiping.gotrip.R;

/**
 * Created by siyiping on 15/6/17.
 */
public class Strategy extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        inflater.inflate(R.layout.strategyfragment,container);

        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
