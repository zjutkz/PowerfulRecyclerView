package com.example.kangzhe.powerfulrecyclerviewlib.listener;

/**
 * Created by kangzhe on 16/1/2.
 */
public interface IHeaderView {

    void pullToRefresh();

    void releaseToRefresh();

    void onRefresh();

    void onReset(float distance, float fraction);

    void onPull(float distance, float fraction);
}
