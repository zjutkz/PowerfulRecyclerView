package com.zjutkz.powerfulrecyclerview.listener;

import android.support.v7.widget.RecyclerView;

/**
 * Created by kangzhe on 16/1/20.
 */
public interface OnViewClick {

    void onClick(RecyclerView.ViewHolder holder, int position);

    boolean onLongClick(RecyclerView.ViewHolder holder, int position);
}
