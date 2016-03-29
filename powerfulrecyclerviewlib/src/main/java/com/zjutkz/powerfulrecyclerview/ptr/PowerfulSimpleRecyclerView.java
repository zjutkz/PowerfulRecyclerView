package com.zjutkz.powerfulrecyclerview.ptr;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;

import com.example.kangzhe.powerfulrecyclerviewlib.R;
import com.zjutkz.powerfulrecyclerview.listener.IHeaderView;
import com.zjutkz.powerfulrecyclerview.listener.OnViewClick;
import com.zjutkz.powerfulrecyclerview.simple.SimpleImageView;
import com.zjutkz.powerfulrecyclerview.simple.SimpleTextView;


/**
 * Created by kangzhe on 16/1/5.
 */
public class PowerfulSimpleRecyclerView extends PowerfulRecyclerView implements OnViewClick,SwipeRefreshLayout.OnRefreshListener{

    private static final String TAG = "WdSimplePtrContainer";
    
    public PowerfulSimpleRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PowerfulSimpleRecyclerView(Context context) {
        this(context, null);
    }

    public PowerfulSimpleRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if(!isSwipeToRefresh && mRefreshListener != null){
            setHeaderView(LayoutInflater.from(getContext()).inflate(R.layout.simple_header, PowerfulSimpleRecyclerView.this, false));
        }
    }

    public PowerfulSimpleRecyclerView setHeaderStrings(final String[] strArray){
        this.post(new Runnable() {
            @Override
            public void run() {
                for (IHeaderView headerView : mHeaderViews) {
                    if (headerView instanceof SimpleTextView) {
                        ((SimpleTextView) headerView).setPullToRefreshText(strArray[0]);
                        ((SimpleTextView) headerView).setReleaseToRefreshText(strArray[1]);
                        ((SimpleTextView) headerView).setOnRefreshText(strArray[2]);
                    }
                }
            }
        });

        return this;
    }

    public PowerfulSimpleRecyclerView setHeaderDrawables(final int[] drawable){
        this.post(new Runnable() {
            @Override
            public void run() {
                for (IHeaderView headerView : mHeaderViews) {
                    if (headerView instanceof SimpleImageView) {
                        ((SimpleImageView) headerView).setPullToRefreshDrawable(getContext().getResources().getDrawable(drawable[0]));
                        ((SimpleImageView) headerView).setReleaseToRefreshDrawable(getContext().getResources().getDrawable(drawable[1]));
                    }
                }
            }
        });

        return this;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }
}
