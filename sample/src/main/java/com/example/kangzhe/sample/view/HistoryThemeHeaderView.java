package com.example.kangzhe.sample.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zjutkz.powerfulrecyclerview.listener.IHeaderView;
import com.example.kangzhe.sample.R;

/**
 * Created by kangzhe on 16/2/29.
 */
public class HistoryThemeHeaderView extends LinearLayout implements IHeaderView {

    private static final int ANIMATION_DURATION = 180;

    public static final int PULL_TO_REFRESH = 0;

    public static final int RELEASE_TO_REFRESH = 1;

    public static final int ON_REFRESH = 2;

    public static final int IDLE = 3;

    private int state = IDLE;

    private ImageView iv;

    private TextView tv;

    private ProgressBar pb;

    private ImageView mImageView;

    private Animation mRotateUpAnim;

    private Animation mRotateDownAnim;

    public HistoryThemeHeaderView(Context context) {
        this(context, null);
    }

    public HistoryThemeHeaderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HistoryThemeHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.post(new Runnable() {
            @Override
            public void run() {
                initView();
            }
        });

        initAnim();
    }

    private void initAnim() {
        mRotateUpAnim = new RotateAnimation(0.0f, -180.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        mRotateUpAnim.setDuration(ANIMATION_DURATION);
        mRotateUpAnim.setFillAfter(true);

        mRotateDownAnim = new RotateAnimation(-180.0f, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        mRotateDownAnim.setDuration(ANIMATION_DURATION);
        mRotateDownAnim.setFillAfter(true);
    }

    private void initView() {
        iv = (ImageView)findViewById(R.id.arrow);

        pb = (ProgressBar)findViewById(R.id.progress);

        tv = (TextView)findViewById(R.id.message);

        mImageView = (ImageView)findViewById(R.id.image);
        iv.setImageDrawable(getResources().getDrawable(R.drawable.up));
    }

    @Override
    public void pullToRefresh() {
        if(state == RELEASE_TO_REFRESH){
            iv.startAnimation(mRotateDownAnim);
        }

        state = PULL_TO_REFRESH;

        pb.setVisibility(INVISIBLE);
        iv.setVisibility(VISIBLE);

        tv.setText("下拉查看上个主题");
    }

    @Override
    public void releaseToRefresh() {
        if(state == PULL_TO_REFRESH){
            iv.startAnimation(mRotateUpAnim);
        }

        state = RELEASE_TO_REFRESH;

        pb.setVisibility(INVISIBLE);
        iv.setVisibility(VISIBLE);

        tv.setText("松开加载上个主题");
    }

    @Override
    public void onRefresh() {
        state = ON_REFRESH;

        iv.clearAnimation();

        pb.setVisibility(VISIBLE);
        iv.setVisibility(INVISIBLE);

        tv.setText("正在加载...");
    }

    @Override
    public void onReset(float distance, float fraction) {

    }

    @Override
    public void onPull(float distance, float fraction) {

    }
}
