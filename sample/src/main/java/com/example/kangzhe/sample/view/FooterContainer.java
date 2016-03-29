package com.example.kangzhe.sample.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.zjutkz.powerfulrecyclerview.listener.IFooterView;


/**
 * Created by kangzhe on 16/1/10.
 */
public class FooterContainer extends FrameLayout implements IFooterView {

    public FooterContainer(Context context) {
        this(context, null);
    }

    public FooterContainer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FooterContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onShow() {
        setBackgroundColor(Color.RED);
    }

    @Override
    public void onLoadMore() {
        setBackgroundColor(Color.YELLOW);
    }
}
