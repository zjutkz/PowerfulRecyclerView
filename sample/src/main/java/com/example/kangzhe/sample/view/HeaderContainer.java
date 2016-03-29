package com.example.kangzhe.sample.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.zjutkz.powerfulrecyclerview.listener.IHeaderView;


/**
 * Created by kangzhe on 16/1/4.
 */
public class HeaderContainer extends FrameLayout implements IHeaderView {

    public HeaderContainer(Context context) {
        this(context, null);
    }

    public HeaderContainer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HeaderContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void pullToRefresh() {
        setBackgroundColor(Color.RED);
    }

    @Override
    public void releaseToRefresh() {
        setBackgroundColor(Color.BLUE);
    }

    @Override
    public void onRefresh() {
        setBackgroundColor(Color.YELLOW);
    }

    @Override
    public void onReset(float distance, float fraction) {
    }

    @Override
    public void onPull(float distance, float fraction) {
    }
}
