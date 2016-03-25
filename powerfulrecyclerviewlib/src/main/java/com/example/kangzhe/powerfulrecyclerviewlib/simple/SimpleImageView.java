package com.example.kangzhe.powerfulrecyclerviewlib.simple;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.example.kangzhe.powerfulrecyclerviewlib.listener.IHeaderView;


/**
 * Created by kangzhe on 16/1/5.
 */
public class SimpleImageView extends ImageView implements IHeaderView {

    private Drawable pullToRefreshDrawable;
    private Drawable releaseToRefreshDrawable;
    private Drawable onRefreshDrawable;

    public void setPullToRefreshDrawable(Drawable pullToRefreshDrawable) {
        this.pullToRefreshDrawable = pullToRefreshDrawable;
    }

    public void setReleaseToRefreshDrawable(Drawable releaseToRefreshDrawable) {
        this.releaseToRefreshDrawable = releaseToRefreshDrawable;
    }

    public void setOnRefreshDrawable(Drawable onRefreshDrawable) {
        this.onRefreshDrawable = onRefreshDrawable;
    }

    public SimpleImageView(Context context) {
        this(context, null);
    }

    public SimpleImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SimpleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void pullToRefresh() {
        if(pullToRefreshDrawable != null){
            setImageDrawable(pullToRefreshDrawable);
        }
    }

    @Override
    public void releaseToRefresh() {
        if(releaseToRefreshDrawable != null){
            setImageDrawable(releaseToRefreshDrawable);
        }
    }

    @Override
    public void onRefresh() {
        if(onRefreshDrawable != null){
            setImageDrawable(onRefreshDrawable);
        }
    }

    @Override
    public void onReset(float distance, float fraction) {

    }

    @Override
    public void onPull(float distance, float fraction) {

    }
}
