package com.zjutkz.powerfulrecyclerview.listener;

/**
 * Created by kangzhe on 16/1/27.
 */
public interface ItemTouchAdapter {

    void onMove(int fromPosition, int toPosition);

    void onDismiss(int position);
}
