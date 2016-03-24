package com.example.kangzhe.sample.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.example.kangzhe.powerfulrecyclerviewlib.listener.IHeaderView;
import com.example.kangzhe.sample.R;


/**
 * Created by kangzhe on 16/1/2.
 */
public class HeaderImageView extends ImageView implements IHeaderView {

    public HeaderImageView(Context context) {
        this(context, null);
    }

    public HeaderImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HeaderImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void pullToRefresh() {
        setImageDrawable(getContext().getResources().getDrawable(R.drawable.down));
    }

    @Override
    public void releaseToRefresh() {
        setImageDrawable(getContext().getResources().getDrawable(R.drawable.up));
    }

    @Override
    public void onRefresh() {
        setImageDrawable(getContext().getResources().getDrawable(R.mipmap.ic_launcher));
    }

    @Override
    public void onReset(float distance, float fraction) {

    }

    @Override
    public void onPull(float distance, float fraction) {

    }
}
