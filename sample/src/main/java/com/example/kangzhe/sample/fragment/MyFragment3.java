package com.example.kangzhe.sample.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kangzhe.sample.R;


/**
 * Created by kangzhe on 16/1/17.
 */
public class MyFragment3 extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_layout,container,false);
        TextView tv = (TextView)view.findViewById(R.id.fragment_tv);
        tv.setBackgroundColor(Color.MAGENTA);
        tv.setText("fragment3");
        return view;
    }
}
