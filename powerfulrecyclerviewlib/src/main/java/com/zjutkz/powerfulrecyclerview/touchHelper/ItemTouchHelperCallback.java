package com.zjutkz.powerfulrecyclerview.touchHelper;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.zjutkz.powerfulrecyclerview.adapter.PowerfulRecyclerAdapter;
import com.zjutkz.powerfulrecyclerview.listener.ItemTouchAdapter;
import com.zjutkz.powerfulrecyclerview.ptr.PowerfulRecyclerView;


/**
 * Created by kangzhe on 16/1/27.
 */
public class ItemTouchHelperCallback  extends ItemTouchHelper.Callback {

    private final ItemTouchAdapter mAdapter;

    private boolean shouldDrag;

    private boolean shouldSwipe;

    public ItemTouchHelperCallback(ItemTouchAdapter adapter,boolean shouldDrag,boolean shouldSwipe) {
        mAdapter = adapter;
        this.shouldDrag = shouldDrag;
        this.shouldSwipe = shouldSwipe;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return shouldDrag;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return shouldSwipe;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP   | ItemTouchHelper.DOWN |
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                          RecyclerView.ViewHolder target) {
        //存在头部或底部，并且点击头部或底部
        if((((PowerfulRecyclerAdapter) mAdapter).hasHeaderView() && viewHolder.getAdapterPosition() == 0) || (((PowerfulRecyclerAdapter) mAdapter).hasFootView() && viewHolder.getAdapterPosition() == ((PowerfulRecyclerAdapter)mAdapter).getItemCount() - 1)){
            return false;
        }

        //存在头部或底部，并且点击头部或底部
        if((((PowerfulRecyclerAdapter) mAdapter).hasHeaderView() && target.getAdapterPosition() == 0) || (((PowerfulRecyclerAdapter) mAdapter).hasFootView() && target.getAdapterPosition() == ((PowerfulRecyclerAdapter)mAdapter).getItemCount() - 1)){
            return false;
        }

        if(((PowerfulRecyclerAdapter) mAdapter).getPlugAdapter().getItemViewType(viewHolder.getAdapterPosition() - ((PowerfulRecyclerAdapter) mAdapter).getHeaderViewCount()) == PowerfulRecyclerView.TYPE_RECYCLER_FOOTER){
            return false;
        }

        mAdapter.onMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        if(!(mAdapter instanceof PowerfulRecyclerAdapter)){
            return;
        }

        //存在头部，并且点击头部
        if(((PowerfulRecyclerAdapter) mAdapter).hasHeaderView() && viewHolder.getAdapterPosition() == 0){
            return;
        }

        //存在底部，并且点击底部
        if(((PowerfulRecyclerAdapter) mAdapter).hasFootView() && viewHolder.getAdapterPosition() == ((PowerfulRecyclerAdapter)mAdapter).getItemCount() - 1){
            return;
        }

        if(((PowerfulRecyclerAdapter) mAdapter).getPlugAdapter().getItemViewType(viewHolder.getAdapterPosition() - ((PowerfulRecyclerAdapter) mAdapter).getHeaderViewCount()) == PowerfulRecyclerView.TYPE_RECYCLER_FOOTER){
            return;
        }

        mAdapter.onDismiss(viewHolder.getAdapterPosition());
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        boolean clickHeader = ((PowerfulRecyclerAdapter) mAdapter).hasHeaderView() && viewHolder.getAdapterPosition() == 0;
        boolean clickFooter = ((PowerfulRecyclerAdapter) mAdapter).hasFootView() && viewHolder.getAdapterPosition() == ((PowerfulRecyclerAdapter)mAdapter).getItemCount() - 1;
        boolean clickRecyclerFooter = ((PowerfulRecyclerAdapter) mAdapter).getPlugAdapter().getItemViewType(viewHolder.getAdapterPosition() - ((PowerfulRecyclerAdapter) mAdapter).getHeaderViewCount()) == PowerfulRecyclerView.TYPE_RECYCLER_FOOTER;
        if(clickHeader || clickFooter || clickRecyclerFooter){
            return;
        }else{
            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                float width = (float) viewHolder.itemView.getWidth();
                float alpha = 1.0f - Math.abs(dX) / width;
                viewHolder.itemView.setAlpha(alpha);
                viewHolder.itemView.setTranslationX(dX);
            } else {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY,
                        actionState, isCurrentlyActive);
            }
        }
    }
}
