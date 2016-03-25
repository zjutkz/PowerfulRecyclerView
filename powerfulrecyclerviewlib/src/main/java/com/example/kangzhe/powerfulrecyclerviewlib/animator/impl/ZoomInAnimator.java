package com.example.kangzhe.powerfulrecyclerviewlib.animator.impl;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.kangzhe.powerfulrecyclerviewlib.animator.base.BaseItemAnimator;


/**
 * Created by kangzhe on 16/1/29.
 */
public class ZoomInAnimator extends BaseItemAnimator {
    private static final String TAG = "ZoomInAnimator";

    @Override
    protected void onPreAnimateAdd(RecyclerView.ViewHolder holder) {
        ViewCompat.setScaleX(holder.itemView, 0);
        ViewCompat.setScaleY(holder.itemView, 0);
        //setAddDuration(1000);
    }

    @Override
    protected void onPreAnimateRemove(RecyclerView.ViewHolder holder) {
        //setRemoveDuration(1000);
    }

    @Override
    protected AnimatorSet generateRemoveAnimator(RecyclerView.ViewHolder holder) {
        View target = holder.itemView;

        AnimatorSet animator = new AnimatorSet();

        animator.playTogether(
                ObjectAnimator.ofFloat(target, "scaleX", 1.0f, 0.0f),
                ObjectAnimator.ofFloat(target, "scaleY", 1.0f, 0.0f)
        );

        animator.setTarget(target);
        animator.setDuration(getRemoveDuration());

        return animator;
    }

    @Override
    protected AnimatorSet generateAddAnimator(RecyclerView.ViewHolder holder) {
        View target = holder.itemView;

        AnimatorSet animator = new AnimatorSet();

        animator.playTogether(
                ObjectAnimator.ofFloat(target, "scaleX", 0.0f, 1.0f),
                ObjectAnimator.ofFloat(target, "scaleY", 0.0f, 1.0f)
        );

        animator.setTarget(target);
        animator.setDuration(getAddDuration());

        return animator;
    }
}
