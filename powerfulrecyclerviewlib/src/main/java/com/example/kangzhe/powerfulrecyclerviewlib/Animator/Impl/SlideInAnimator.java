package com.example.kangzhe.powerfulrecyclerviewlib.Animator.Impl;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.kangzhe.powerfulrecyclerviewlib.Animator.base.BaseItemAnimator;


/**
 * Created by kangzhe on 16/1/29.
 */
public class SlideInAnimator extends BaseItemAnimator {
    @Override
    protected void onPreAnimateAdd(RecyclerView.ViewHolder holder) {
        ViewCompat.setTranslationX(holder.itemView, -holder.itemView.getMeasuredWidth());
    }

    @Override
    protected void onPreAnimateRemove(RecyclerView.ViewHolder holder) {

    }

    @Override
    protected AnimatorSet generateRemoveAnimator(RecyclerView.ViewHolder holder) {
        View target = holder.itemView;

        AnimatorSet animator = new AnimatorSet();

        animator.playTogether(
                ObjectAnimator.ofFloat(target, "translationX", 0, -holder.itemView.getMeasuredWidth())
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
                ObjectAnimator.ofFloat(target, "translationX", -holder.itemView.getMeasuredWidth(),0)
        );

        animator.setTarget(target);
        animator.setDuration(getAddDuration());

        return animator;
    }
}
