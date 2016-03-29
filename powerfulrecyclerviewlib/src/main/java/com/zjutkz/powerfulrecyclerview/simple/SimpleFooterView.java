package com.zjutkz.powerfulrecyclerview.simple;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.zjutkz.powerfulrecyclerview.listener.IFooterView;


/**
 * Created by kangzhe on 16/1/10.
 */
public class SimpleFooterView extends TextView implements IFooterView {

    private String onShowText;
    private String onLoadMoreText;

    public SimpleFooterView(Context context) {
        this(context, null);
    }

    public SimpleFooterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SimpleFooterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onShow() {
        setText(onShowText);
    }

    @Override
    public void onLoadMore() {
        setText(onLoadMoreText);
    }

    public void setOnShowText(String onShowText){
        this.onShowText = onShowText;
    }

    public void setOnLoadMoreText(String onLoadMoreText){
        this.onLoadMoreText = onLoadMoreText;
    }
}
