package com.zjutkz.powerfulrecyclerview.adapter;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import com.zjutkz.powerfulrecyclerview.listener.ItemTouchAdapter;
import com.zjutkz.powerfulrecyclerview.listener.OnViewClick;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kangZhe on 16/1/1.
 */
public class PowerfulRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemTouchAdapter {

    private static final String TAG = "PowerfulRecyclerAdapter";

    private static final int TYPE_HEADER = -2;
    private static final int TYPE_FOOT = -1;


    private List<View> mHeaderViews;
    private View mFootView;
    private RecyclerView.Adapter mPlugAdapter;
    private int spanCount;
    private int headerPosition = 0;

    public PowerfulRecyclerAdapter(final RecyclerView.Adapter mPlugAdapter){
        this.mPlugAdapter = mPlugAdapter;

        mHeaderViews = new ArrayList<View>();

        mPlugAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                PowerfulRecyclerAdapter.this.notifyDataSetChanged();
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                PowerfulRecyclerAdapter.this.notifyItemRangeChanged(positionStart + getHeaderViewCount(), itemCount);
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                int truePositionStart = positionStart + getHeaderViewCount();

                PowerfulRecyclerAdapter.this.notifyItemRangeInserted(truePositionStart, itemCount);
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                int truePositionStart = positionStart + getHeaderViewCount();

                PowerfulRecyclerAdapter.this.notifyItemRangeRemoved(truePositionStart, itemCount);
            }

            @Override
            public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                int truePositionStart = fromPosition + getHeaderViewCount();
                int truePositionEnd = toPosition + getHeaderViewCount();

                PowerfulRecyclerAdapter.this.notifyItemMoved(truePositionStart, truePositionEnd);
            }
        });
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_HEADER){
            if(headerPosition >= mHeaderViews.size()){
                headerPosition = 0;
            }
            return new ViewHolder(mHeaderViews.get(headerPosition++));
        }else if(viewType == TYPE_FOOT){
            return new ViewHolder(mFootView);
        }else{
            return mPlugAdapter.onCreateViewHolder(parent,viewType);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if(hasFootView() && hasHeaderView()){
            if(position < getHeaderViewCount() || position == getItemCount() - 1){
                //do nothing
            }else{

                mPlugAdapter.onBindViewHolder(holder, position - getHeaderViewCount());
            }
        }else if(hasFootView() && !hasHeaderView()){
            if(position == getItemCount() - 1){
                //do nothing
            }else{

                mPlugAdapter.onBindViewHolder(holder,position);
            }
        }else if(!hasFootView() && hasHeaderView()){
            if(position < getHeaderViewCount()){
                //do nothing
            }else{

                mPlugAdapter.onBindViewHolder(holder,position - getHeaderViewCount());
            }
        }else{

            mPlugAdapter.onBindViewHolder(holder, position);
        }
    }

    @Override
    public int getItemCount() {
        return mPlugAdapter.getItemCount() + getHeaderViewCount() + getFootViewCount();
    }

    @Override
    public int getItemViewType(int position) {
        int realCount = mPlugAdapter.getItemCount();

        if(position < getHeaderViewCount()){
            return TYPE_HEADER;
        }else if(position >= getHeaderViewCount() && position < realCount + getHeaderViewCount()){
            return mPlugAdapter.getItemViewType(position - getHeaderViewCount());
        }else{
            return TYPE_FOOT;
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if(manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return (getItemViewType(position) == TYPE_FOOT) || (getItemViewType(position) == TYPE_HEADER) ? spanCount : 1;
                }
            });
        }
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
        if (lp != null
                && lp instanceof StaggeredGridLayoutManager.LayoutParams
                && ((holder.getLayoutPosition() == this.getItemCount() - 1 && hasFootView())
                || (holder.getLayoutPosition() < getHeaderViewCount()) && hasHeaderView())) {
            StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
            p.setFullSpan(true);
        }
    }

    /**
     * 设置footrer
     * @param mFootView
     * @param spanCount
     */
    public void setFootView(View mFootView,int spanCount){
        if(mFootView == null){
            return;
        }
        this.mFootView = mFootView;
        this.spanCount = spanCount;
        this.notifyDataSetChanged();
    }

    /**
     * 设置header
     * @param mHeaderView
     * @param spanCount
     */
    public void setHeaderView(View mHeaderView,int spanCount){
        if(mHeaderView == null){
            return;
        }

        mHeaderViews.add(mHeaderView);

        this.spanCount = spanCount;
        this.notifyDataSetChanged();
    }

    /**
     * 去除footer
     * @param mFootView
     */
    public void removeFootView(View mFootView){
        if(mFootView == null){
            return;
        }
        this.mFootView = null;
        this.notifyDataSetChanged();
    }

    /**
     * 是否含有header
     */
    public boolean hasHeaderView(){
        return mHeaderViews != null && mHeaderViews.size() > 0;
    }

    /**
     * header个数
     */
    public int getHeaderViewCount(){
        return mHeaderViews.size();
    }

    /**
     * 是否含有footer
     */
    public boolean hasFootView(){
        return mFootView != null;
    }

    /**
     * footer个数
     */
    public int getFootViewCount(){
        return mFootView == null ? 0 : 1;
    }

    @Override
    public void onMove(int fromPosition, int toPosition) {
        if(mPlugAdapter instanceof ItemTouchAdapter){
            int realFromPos = fromPosition - getHeaderViewCount();
            int realToPos = toPosition - getHeaderViewCount();

            ((ItemTouchAdapter) mPlugAdapter).onMove(realFromPos, realToPos);
        }
    }

    @Override
    public void onDismiss(int position) {
        if(mPlugAdapter instanceof ItemTouchAdapter){
            int realPos = position - getHeaderViewCount();

            ((ItemTouchAdapter) mPlugAdapter).onDismiss(realPos);
        }
    }

    public RecyclerView.Adapter getPlugAdapter(){
        return mPlugAdapter;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
