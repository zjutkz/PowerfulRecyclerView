package com.example.kangzhe.sample.view;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.example.kangzhe.powerfulrecyclerviewlib.listener.IHeaderView;
import com.example.kangzhe.sample.R;


/**
 * Created by kangzhe on 16/1/2.
 */
public class HeaderScaleImageView extends ImageView implements IHeaderView {

    public HeaderScaleImageView(Context context) {
        this(context, null);
    }

    public HeaderScaleImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HeaderScaleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setImageDrawable(getContext().getResources().getDrawable(R.mipmap.ic_launcher));
    }

    @Override
    public void pullToRefresh() {

    }

    @Override
    public void releaseToRefresh() {

    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onReset(float distance, float fraction) {
        ViewCompat.setScaleX(this, fraction);
        ViewCompat.setScaleY(this, fraction);
    }

    @Override
    public void onPull(float distance, float fraction) {
        ViewCompat.setScaleX(this, fraction);
        ViewCompat.setScaleY(this, fraction);
    }
}