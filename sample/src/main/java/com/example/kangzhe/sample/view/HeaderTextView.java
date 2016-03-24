package com.example.kangzhe.sample.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.example.kangzhe.powerfulrecyclerviewlib.listener.IHeaderView;


/**
 * Created by kangzhe on 16/1/2.
 */
public class HeaderTextView extends TextView implements IHeaderView {

    public HeaderTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public HeaderTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HeaderTextView(Context context) {
        this(context, null);
    }

    @Override
    public void pullToRefresh() {
        setText("pull to refresh");
    }

    @Override
    public void releaseToRefresh() {
        setText("release to refresh");
    }

    @Override
    public void onRefresh() {
        setText("on refresh");
    }

    @Override
    public void onReset(float distance, float fraction) {

    }

    @Override
    public void onPull(float distance, float fraction) {

    }
}
