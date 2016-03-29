package com.zjutkz.powerfulrecyclerview.utils;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;


import com.zjutkz.powerfulrecyclerview.listener.IFooterView;
import com.zjutkz.powerfulrecyclerview.listener.OnLoadMoreListener;
import com.zjutkz.powerfulrecyclerview.ptr.PowerfulRecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kangzhe on 16/1/5.
 */
public class FooterUtils {
    private static final String TAG = "FooterUtils";

    private boolean isLoadMore = false;

    private List<IFooterView> mFooterViews;

    /**
     * 防止重复load
     */
    private boolean isFirst = true;

    /**
     * 由于setListener后会调用一次onScroll方法，屏蔽掉。
     */
    private boolean isFirstScroll = true;

    private OnLoadMoreListener loadMoreListener;

    private PowerfulRecyclerView container;

    public FooterUtils(PowerfulRecyclerView container){
        this.container = container;
    }

    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE && isLoadMore && isFirst && container.isLoadMoreEnable()) {
                if (loadMoreListener != null) {
                    isFirst = false;

                    loadMoreListener.onLoadMore();

                    for (IFooterView footerView : mFooterViews) {
                        footerView.onLoadMore();
                    }
                }
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (isFirstScroll) {
                isFirstScroll = false;
            } else {
                if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
                    int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                    if (lastVisibleItemPosition + 1 == recyclerView.getAdapter().getItemCount() && !isLoadMore) {

                        isLoadMore = true;
                    } else {
                        isLoadMore = false;
                    }
                } else {
                    int[] lastVisibleItemPosition = ((StaggeredGridLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPositions(null);
                    if (lastVisibleItemPosition[lastVisibleItemPosition.length - 1] + 1 == recyclerView.getAdapter().getItemCount() && !isLoadMore) {

                        isLoadMore = true;
                    } else {
                        isLoadMore = false;
                    }
                }
            }
        }
    };

    /**
     * 添加footer
     * @param parent
     * @param footerView
     * @param loadMoreListener
     */
    public void addFooterView(RecyclerView parent,View footerView,final OnLoadMoreListener loadMoreListener){
        this.loadMoreListener = loadMoreListener;

        if(mFooterViews != null){
            mFooterViews.clear();
        }

        if(mFooterViews == null){
            mFooterViews = new ArrayList<IFooterView>();
        }

        if(footerView instanceof IFooterView){
            mFooterViews.add((IFooterView)footerView);
        }

        if(footerView instanceof ViewGroup){
            for(int i = 0;i < ((ViewGroup) footerView).getChildCount();i++){
                View child = ((ViewGroup) footerView).getChildAt(i);
                if(child instanceof IFooterView){
                    mFooterViews.add((IFooterView)child);
                }
            }
        }

        RecyclerViewUtils.getInstance().setFootView(parent, footerView);

        parent.removeOnScrollListener(onScrollListener);
        parent.addOnScrollListener(onScrollListener);


        footerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loadMoreListener != null && isFirst) {
                    isFirst = false;

                    loadMoreListener.onLoadMore();
                    for (IFooterView footerView : mFooterViews) {
                        footerView.onLoadMore();
                    }
                }
            }
        });

        for (IFooterView footerView2 : mFooterViews) {
            footerView2.onLoadMore();
        }
    }

    public void resetFooterView(RecyclerView parent,View footerView,final OnLoadMoreListener loadMoreListener){
        isLoadMore = false;

        isFirst = true;

        addFooterView(parent, footerView, loadMoreListener);
    }

    public void stopLoadMore(){
        if(mFooterViews != null && mFooterViews.size() > 0) {
            for (IFooterView footerView : mFooterViews) {
                footerView.onShow();
            }
        }
        isLoadMore = false;

        isFirst = true;
    }

    /**
     * 取消footer
     * @param parent
     * @param footerView
     */
    public void removeFooterView(View parent,View footerView){
       if(parent instanceof RecyclerView){
            removeRecyclerViewFooterView(((RecyclerView) parent), footerView);
        }
    }

    public static void removeRecyclerViewFooterView(RecyclerView parent, View footerView){
        RecyclerViewUtils.getInstance().removeFootView(parent, footerView);

    }
}
