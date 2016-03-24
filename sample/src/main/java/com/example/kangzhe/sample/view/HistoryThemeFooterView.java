package com.example.kangzhe.sample.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.kangzhe.powerfulrecyclerviewlib.listener.IFooterView;
import com.example.kangzhe.sample.R;


/**
 * Created by kangzhe on 16/2/29.
 */
public class HistoryThemeFooterView extends LinearLayout implements IFooterView {

    private static final int ANIMATION_DURATION = 180;

    public static final int ON_SHOW = 1;

    public static final int ON_LOAD_MORE = 2;

    public static final int IDLE = 3;

    private int state = IDLE;

    private ImageView iv;

    private TextView tv;

    private ProgressBar pb;

    private ImageView mImageView;

    private Animation mRotateUpAnim;

    private Animation mRotateDownAnim;

    public HistoryThemeFooterView(Context context) {
        this(context, null);
    }

    public HistoryThemeFooterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HistoryThemeFooterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

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
    }

    public void clearImage() {
        mImageView.setImageDrawable(null);
        mImageView.setVisibility(View.GONE);
    }


    @Override
    public void onShow() {
        if(state == ON_LOAD_MORE){
            iv.startAnimation(mRotateUpAnim);
        }

        state = ON_SHOW;

        pb.setVisibility(INVISIBLE);
        iv.setVisibility(VISIBLE);

        //tv.setText(activity.hasOldTheme() ? "松开加载下个主题" : "没有下个主题了");
        tv.setText("松开加载下个主题");
    }

    @Override
    public void onLoadMore() {
        if(iv == null){
            initView();
        }

        if(state != IDLE){
            iv.clearAnimation();
        }

        state = ON_LOAD_MORE;

        pb.setVisibility(VISIBLE);
        iv.setVisibility(INVISIBLE);

        tv.setText("正在加载...");
    }
}
