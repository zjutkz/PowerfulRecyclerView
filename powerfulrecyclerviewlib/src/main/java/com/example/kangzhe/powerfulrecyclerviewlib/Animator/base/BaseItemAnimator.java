package com.example.kangzhe.powerfulrecyclerviewlib.Animator.base;

import android.animation.AnimatorSet;
import android.os.Handler;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.example.kangzhe.powerfulrecyclerviewlib.Utils.AnimateViewUtils;

import java.util.ArrayList;


/**
 * Created by kangzhe on 16/1/29.
 */
public abstract class BaseItemAnimator extends SimpleItemAnimator{

    private static final String TAG = "BaseItemAnimator";

    private ArrayList<RecyclerView.ViewHolder> mPendingAdditions = new ArrayList<>();
    private ArrayList<RecyclerView.ViewHolder> mPendingRemoves = new ArrayList<>();
    private ArrayList<moveInfo> mPendingChanges = new ArrayList<>();

    private long totalTime = 0;

    private boolean shouldNotify = false;

    private RecyclerView.Adapter mAdapter;

    protected Interpolator mInterpolator = new LinearInterpolator();

    private static class moveInfo {
        public RecyclerView.ViewHolder holder;
        public int fromX, fromY, toX, toY;

        private moveInfo(RecyclerView.ViewHolder holder, int fromX, int fromY, int toX, int toY) {
            this.holder = holder;
            this.fromX = fromX;
            this.fromY = fromY;
            this.toX = toX;
            this.toY = toY;
        }
    }

    // fix bug
    // 由于recyclerView源码中的逻辑为invisible的item不论怎么样都不会notify(就算notifyItemRangeChanged(0,count))
    // 所以这里要强制执行notifyDataSetChanged()方法
    private Runnable mNotifyRunnable = new Runnable() {
        @Override
        public void run() {
            mAdapter.notifyDataSetChanged();
            totalTime = 0;
        }
    };

    public static Handler postHandler = new Handler();

    public BaseItemAnimator() {
        super();
        setSupportsChangeAnimations(false);
    }

    public void setInterpolator(Interpolator mInterpolator) {
        this.mInterpolator = mInterpolator;
    }

    /**
     * 逻辑应该是先判断remove，再判断move，最后判断add。
     */
    @Override
    public void runPendingAnimations() {
        boolean shouldRemove = !mPendingRemoves.isEmpty();
        boolean shouldChange = !mPendingChanges.isEmpty();
        boolean shouldAdd = !mPendingAdditions.isEmpty();

        //判断remove的逻辑
        if(shouldRemove){
            shouldNotify = true;
            doRemoveAnimate();
            totalTime = getRemoveDuration();
        }

        //判断change的逻辑
        if(shouldChange){
            Runnable changePadding = new Runnable() {
                @Override
                public void run() {
                    doChangeAnimate();
                }
            };
            /**
             * 如果在判断change逻辑的时候，还在进行remove，就等remove逻辑执行完了再执行move，同下。
             * 这个delay的时间应该和动画的时间相等。关于时间不应该自己直接写数字，而应该调用getXXXDuration()。
             * 怎么改变这个时间具体见
             * @see com.vdian.UI.animator.impl.ZoomInAnimator
             */
            if(shouldRemove){
                postHandler.postDelayed(changePadding, totalTime);
            }else{
                changePadding.run();
            }
            totalTime += getChangeDuration();
        }

        //判断add的逻辑
        if(shouldAdd){
            Runnable addPadding = new Runnable() {
                @Override
                public void run() {
                    doAddAnimate();
                }
            };

            if(shouldRemove || shouldChange){
                postHandler.postDelayed(addPadding,totalTime);
            }else{
                addPadding.run();
            }
            totalTime += getAddDuration();
        }

        if(shouldNotify){
            Handler h = new Handler();
            h.postDelayed(mNotifyRunnable,totalTime);
        }
    }

    /**
     * adapter调用notifyItemRemove会回调到这个方法
     */
    @Override
    public boolean animateRemove(RecyclerView.ViewHolder holder) {
        endAnimation(holder);
        preAnimateRemove(holder);
        mPendingRemoves.add(holder);
        return true;
    }

    /**
     * adapter调用notifyItemAdd会回调到这个方法
     */
    @Override
    public boolean animateAdd(RecyclerView.ViewHolder holder) {
        endAnimation(holder);
        preAnimateAdd(holder);
        mPendingAdditions.add(holder);
        return true;
    }

    /**
     * adapter调用notifyItemMove会回调到这个方法
     */
    @Override
    public boolean animateMove(RecyclerView.ViewHolder holder, int fromX, int fromY, int toX, int toY) {
        return true;
    }

    /**
     * adapter调用notifyItemChanged会回调到这个方法
     */
    @Override
    public boolean animateChange(RecyclerView.ViewHolder oldHolder, RecyclerView.ViewHolder newHolder, int fromX, int fromY, int toX, int toY) {
        final View view = oldHolder.itemView;
        fromX += ViewCompat.getTranslationX(oldHolder.itemView);
        fromY += ViewCompat.getTranslationY(oldHolder.itemView);
        endAnimation(oldHolder);
        int deltaX = toX - fromX;
        int deltaY = toY - fromY;
        if (deltaX == 0 && deltaY == 0) {
            dispatchMoveFinished(oldHolder);
            return false;
        }
        if (deltaX != 0) {
            ViewCompat.setTranslationX(view, -deltaX);
        }
        if (deltaY != 0) {
            ViewCompat.setTranslationY(view, -deltaY);
        }
        mPendingChanges.add(new moveInfo(oldHolder, fromX, fromY, toX, toY));

        return true;
    }

    @Override
    public void endAnimation(RecyclerView.ViewHolder item) {
        final View view = item.itemView;

        ViewCompat.animate(view).cancel();

        totalTime = 0;

        if(mPendingRemoves.remove(item)){
            AnimateViewUtils.clear(item.itemView);
            dispatchRemoveFinished(item);
        }

        if(mPendingChanges.remove(item)){
            AnimateViewUtils.clear(item.itemView);
            dispatchMoveFinished(item);
        }

        if(mPendingAdditions.remove(item)){
            AnimateViewUtils.clear(item.itemView);
            dispatchAddFinished(item);
        }

        dispatchFinishedWhenDone();
    }

    @Override
    public void endAnimations() {
        int count = mPendingChanges.size();

        for (int i = count - 1; i >= 0; i--) {
            moveInfo item = mPendingChanges.get(i);
            View view = item.holder.itemView;
            ViewCompat.setTranslationY(view, 0);
            ViewCompat.setTranslationX(view, 0);
            dispatchMoveFinished(item.holder);
            mPendingChanges.remove(i);
        }

        count = mPendingRemoves.size();
        for (int i = count - 1; i >= 0; i--) {
            RecyclerView.ViewHolder item = mPendingRemoves.get(i);
            dispatchRemoveFinished(item);
            mPendingRemoves.remove(i);
        }

        count = mPendingAdditions.size();
        for (int i = count - 1; i >= 0; i--) {
            RecyclerView.ViewHolder item = mPendingAdditions.get(i);
            AnimateViewUtils.clear(item.itemView);
            dispatchAddFinished(item);
            mPendingAdditions.remove(i);
        }
    }

    @Override
    public boolean isRunning() {
        return !mPendingAdditions.isEmpty() || !mPendingRemoves.isEmpty() || !mPendingChanges.isEmpty();
    }

    private void preAnimateRemove(RecyclerView.ViewHolder holder) {
        AnimateViewUtils.clear(holder.itemView);
        onPreAnimateRemove(holder);
    }

    private void preAnimateAdd(RecyclerView.ViewHolder holder) {
        AnimateViewUtils.clear(holder.itemView);
        onPreAnimateAdd(holder);
    }

    private void doRemoveAnimate() {
        AnimatorSet animator;

        for(final RecyclerView.ViewHolder holder : mPendingRemoves) {
            animator = generateRemoveAnimator(holder);

            animator.start();
        }

        mPendingRemoves.clear();
    }

    private void doChangeAnimate() {
        for(final moveInfo info : mPendingChanges){
            final RecyclerView.ViewHolder holder = info.holder;
            int fromX = info.fromX;
            int fromY = info.fromY;
            int toX = info.toX;
            int toY = info.toY;

            final View view = holder.itemView;
            final int deltaX = toX - fromX;
            final int deltaY = toY - fromY;
            if (deltaX != 0) {
                ViewCompat.animate(view).translationX(0);
            }
            if (deltaY != 0) {
                ViewCompat.animate(view).translationY(0);
            }

            final ViewPropertyAnimatorCompat animation = ViewCompat.animate(view);
            animation.setDuration(getChangeDuration()).setListener(new ViewPropertyAnimatorListener() {
                @Override
                public void onAnimationStart(View view) {
                    dispatchMoveStarting(holder);
                }

                @Override
                public void onAnimationCancel(View view) {
                    if (deltaX != 0) {
                        ViewCompat.setTranslationX(view, 0);
                    }
                    if (deltaY != 0) {
                        ViewCompat.setTranslationY(view, 0);
                    }
                    dispatchFinishedWhenDone();
                }

                @Override
                public void onAnimationEnd(View view) {
                    animation.setListener(null);
                    //根据官方要求，调用了animateMove之后要调用dispatchMoveFinished
                    dispatchMoveFinished(holder);
                    dispatchFinishedWhenDone();
                }
            }).start();
        }

        mPendingChanges.clear();
    }

    private void doAddAnimate() {
        AnimatorSet animator;

        for(final RecyclerView.ViewHolder viewHolder : mPendingAdditions) {

            animator = generateAddAnimator(viewHolder);

            animator.start();
        }

        mPendingAdditions.clear();
    }

    private void dispatchFinishedWhenDone() {
        shouldNotify = false;

        if (!isRunning()) {
            dispatchAnimationsFinished();
        }
    }

    public void setAdapter(RecyclerView.Adapter mAdapter){
        this.mAdapter = mAdapter;
    }

    /**
     * 这个函数会在在add动画执行之前执行，可以用来做一些准备设置
     */
    protected abstract void onPreAnimateAdd(RecyclerView.ViewHolder holder);

    /**
     * 这个函数会在在remove动画执行之前执行，可以用来做一些准备设置
     */
    protected abstract void onPreAnimateRemove(RecyclerView.ViewHolder holder);

    /**
     * 生成remove动画
     */
    protected abstract AnimatorSet generateRemoveAnimator(RecyclerView.ViewHolder holder);

    /**
     * 生成add动画
     */
    protected abstract AnimatorSet generateAddAnimator(RecyclerView.ViewHolder holder);
}