package com.example.kangzhe.sample.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.example.kangzhe.powerfulrecyclerviewlib.listener.IFooterView;


/**
 * Created by kangzhe on 16/1/10.
 */
public class FooterTextView  extends TextView implements IFooterView {

    public FooterTextView(Context context) {
        this(context, null);
    }

    public FooterTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FooterTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onShow() {
        setText("完全定制onShow");
    }

    @Override
    public void onLoadMore() {
        setText("完全定制onLoadMore");
    }
}
