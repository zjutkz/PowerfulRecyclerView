package com.zjutkz.powerfulrecyclerview.utils;

import android.graphics.Rect;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MotionEvent;
import android.view.View;

import com.zjutkz.powerfulrecyclerview.adapter.PowerfulRecyclerAdapter;
import com.zjutkz.powerfulrecyclerview.touchHelper.ItemTouchHelperFactory;


/**
 * Created by Administrator on 2015/11/26.
 */
public class RecyclerViewUtils {

    private final String TAG = "RecyclerViewUtils";

    private static RecyclerViewUtils mInstance;

    private static final int MIN_MOVE_COUNT = 4;

    private static final int ACTIVE_POINTER_ID_NONE = -1;

    private float mInitialTouchX;

    private float mInitialTouchY;

    private int mActivePointerId = ACTIVE_POINTER_ID_NONE;

    private boolean shouldIntercept = false;

    private int moveCount = 0;

    public static RecyclerViewUtils getInstance(){
        if(mInstance == null){
            synchronized (RecyclerViewUtils.class){
                if(mInstance == null){
                    mInstance = new RecyclerViewUtils();
                }
            }
        }

        return mInstance;
    }

    private final class MyOnItemTouchListener implements RecyclerView.OnItemTouchListener {

        private View recyclerViewHeader;

        public MyOnItemTouchListener(View recyclerViewHeader){
            this.recyclerViewHeader = recyclerViewHeader;
        }

        //如果是左右滑动，那么只会进第一个判断，因为onIntercept返回true,后面不会拦截了。
        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent event) {
            mActivePointerId = MotionEventCompat.getPointerId(event, 0);
            final int action = MotionEventCompat.getActionMasked(event);
            final int index = MotionEventCompat.findPointerIndex(event, mActivePointerId);

            if (action == MotionEvent.ACTION_DOWN) {
                mInitialTouchX = event.getX();
                mInitialTouchY = event.getY();

                checkHitArea(recyclerViewHeader,event,index);
                //下拉的时候由于不会intercept，所以会进这些判断。所以要判断moveCount，不然会出发点击事件。
            } else if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
                if(moveCount < MIN_MOVE_COUNT){
                    recyclerViewHeader.dispatchTouchEvent(event);
                }

                moveCount = 0;

                mActivePointerId = ACTIVE_POINTER_ID_NONE;
                shouldIntercept = false;

            } else if (mActivePointerId != ACTIVE_POINTER_ID_NONE) {
                if (index >= 0) {
                    moveCount++;
                    checkOrientation(recyclerViewHeader, event, index);
                }
            }
            return shouldIntercept;
        }

        //左右滑动onIntercept返回true，会进这个函数。
        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent event) {
            mActivePointerId = MotionEventCompat.getPointerId(event, 0);

            if (mActivePointerId == ACTIVE_POINTER_ID_NONE) {
                return;
            }

            final int action = MotionEventCompat.getActionMasked(event);
            final int activePointerIndex = MotionEventCompat
                    .findPointerIndex(event, mActivePointerId);
            switch (action) {
                case MotionEvent.ACTION_MOVE: {

                    if (activePointerIndex >= 0) {
                        recyclerViewHeader.dispatchTouchEvent(event);
                    }
                    break;
                }
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    if (activePointerIndex >= 0) {

                        recyclerViewHeader.dispatchTouchEvent(event);
                    }

                    mActivePointerId = ACTIVE_POINTER_ID_NONE;
                    shouldIntercept = false;
                    break;
                case MotionEvent.ACTION_POINTER_UP: {
                    final int pointerIndex = MotionEventCompat.getActionIndex(event);
                    final int pointerId = MotionEventCompat.getPointerId(event, pointerIndex);
                    if (pointerId == mActivePointerId) {

                        final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                        mActivePointerId = MotionEventCompat.getPointerId(event, newPointerIndex);
                    }
                    break;
                }
            }
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    };

    private void checkHitArea(View recyclerViewHeader, MotionEvent motionEvent, int pointerIndex) {
        final float x = MotionEventCompat.getX(motionEvent, pointerIndex);
        final float y = MotionEventCompat.getY(motionEvent, pointerIndex);

        Rect rect = new Rect();
        recyclerViewHeader.getHitRect(rect);
        if((rect.contains((int)x,(int)y))){
            recyclerViewHeader.dispatchTouchEvent(motionEvent);

            shouldIntercept = true;

            return;
        }

        shouldIntercept = false;
    }

    private  void checkOrientation(View recyclerViewHeader, MotionEvent motionEvent, int pointerIndex) {
        final float x = MotionEventCompat.getX(motionEvent, pointerIndex);
        final float y = MotionEventCompat.getY(motionEvent, pointerIndex);

        final float dx = x - mInitialTouchX;
        final float dy = y - mInitialTouchY;

        final float absDx = Math.abs(dx);
        final float absDy = Math.abs(dy);

        Rect rect = new Rect();
        recyclerViewHeader.getHitRect(rect);
        if((absDx > absDy) && (rect.contains((int)x,(int)y))){

            shouldIntercept = true;
            return;
        }

        shouldIntercept = false;
    }

    /**
     * 设置recyclerView的header
     *
     * @param mRecycler
     * @param header
     */
    public void setHeaderView(RecyclerView mRecycler,View header){
        RecyclerView.Adapter mAdapter = mRecycler.getAdapter();

        if(mAdapter == null || !(mAdapter instanceof PowerfulRecyclerAdapter)){
            return ;
        }

        PowerfulRecyclerAdapter realAdapter = (PowerfulRecyclerAdapter)mAdapter;

        RecyclerView.LayoutManager manager = mRecycler.getLayoutManager();

        int spanCount = 1;

        if(manager instanceof GridLayoutManager){
            spanCount = ((GridLayoutManager)manager).getSpanCount();
        }else if(manager instanceof StaggeredGridLayoutManager) {
            spanCount = ((StaggeredGridLayoutManager) manager).getSpanCount();
        }

        realAdapter.setHeaderView(header, spanCount);
    }

    /**
     * 设置加载更多的footer
     *
     * @param mRecycler
     * @param foot
     */
    public void setFootView(RecyclerView mRecycler,View foot){
        RecyclerView.Adapter mAdapter = mRecycler.getAdapter();

        if(mAdapter == null || !(mAdapter instanceof PowerfulRecyclerAdapter)){
            return ;
        }

        PowerfulRecyclerAdapter realAdapter = (PowerfulRecyclerAdapter)mAdapter;
        if(realAdapter.hasFootView()){
            return;
        }

        RecyclerView.LayoutManager manager = mRecycler.getLayoutManager();

        int spanCount = 1;

        if(manager instanceof GridLayoutManager){
            spanCount = ((GridLayoutManager)manager).getSpanCount();
        }else if(manager instanceof StaggeredGridLayoutManager) {
            spanCount = ((StaggeredGridLayoutManager) manager).getSpanCount();
        }

        realAdapter.setFootView(foot, spanCount);
    }

    public  void removeFootView(RecyclerView mRecycler,View foot){
        RecyclerView.Adapter mAdapter = mRecycler.getAdapter();

        if(mAdapter == null || !(mAdapter instanceof PowerfulRecyclerAdapter)){
            return ;
        }

        PowerfulRecyclerAdapter realAdapter = (PowerfulRecyclerAdapter)mAdapter;

        realAdapter.removeFootView(foot);
    }

    public void addOnItemTouchListener(RecyclerView recyclerView,final View recyclerViewHeader){
        recyclerView.addOnItemTouchListener(new MyOnItemTouchListener(recyclerViewHeader));
    }

    public  void prepareForDragAndSwipe(RecyclerView mRecycler,boolean shouldDrag,boolean shouldSwipe){
        if(mRecycler != null){
            if(!(mRecycler.getAdapter() instanceof PowerfulRecyclerAdapter)){
                return;
            }

            ItemTouchHelper helper;
            ItemTouchHelper.Callback callback;

            callback = ItemTouchHelperFactory.createCallback(mRecycler, mRecycler.getLayoutManager(), shouldDrag, shouldSwipe);
            helper = new ItemTouchHelper(callback);
            helper.attachToRecyclerView(mRecycler);
        }
    }
}