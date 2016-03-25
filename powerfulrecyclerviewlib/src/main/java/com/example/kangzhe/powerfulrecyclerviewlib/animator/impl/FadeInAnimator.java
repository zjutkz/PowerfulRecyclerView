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
public class FadeInAnimator extends BaseItemAnimator {

    @Override
    protected void onPreAnimateAdd(RecyclerView.ViewHolder holder) {
        ViewCompat.setAlpha(holder.itemView, 0);
    }

    @Override
    protected void onPreAnimateRemove(RecyclerView.ViewHolder holder) {
    }

    @Override
    protected AnimatorSet generateRemoveAnimator(RecyclerView.ViewHolder holder) {
        View target = holder.itemView;

        AnimatorSet animator = new AnimatorSet();

        animator.playTogether(
                ObjectAnimator.ofFloat(target, "alpha", 1.0f, 0.0f)
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
                ObjectAnimator.ofFloat(target, "alpha", 0.0f, 1.0f)
        );

        animator.setTarget(target);
        animator.setDuration(getAddDuration());

        return animator;
    }
}
