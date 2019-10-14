package com.angcyo.uiview.less.kotlin

import android.animation.Animator
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.view.View
import android.view.animation.*
import com.angcyo.uiview.less.resources.RAnimatorListener
import com.angcyo.uiview.less.resources.RAnimtionListener

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/04/28
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */

fun View.animWidth(widthFrom: Int, widthTo: Int, end: () -> Unit = {}): Animator {
    val animator = ValueAnimator.ofInt(widthFrom, widthTo)
    animator.duration = 300
    animator.interpolator = DecelerateInterpolator()
    animator.addUpdateListener {
        val params = layoutParams
        params.width = it.animatedValue as Int
        layoutParams = params
    }
    animator.addListener(object : RAnimatorListener() {
        override fun onAnimationFinish(animation: Animator?, cancel: Boolean) {
            super.onAnimationFinish(animation, cancel)
            end.invoke()
        }
    })
    if (parent != null) {
        animator.start()
    }
    return animator
}

fun View.animScale(
    from: Float,
    to: Float,
    pivotXValue: Float = 0.5f,
    pivotYValue: Float = 0.5f,
    pivotXType: Int = Animation.RELATIVE_TO_SELF,
    pivotYTyp: Int = Animation.RELATIVE_TO_SELF,
    interpolator: Interpolator = DecelerateInterpolator(),
    duration: Long = 300,
    end: () -> Unit = {}
): Animation {
    val animation = ScaleAnimation(
        from, to, from, to,
        pivotXType, pivotXValue, pivotYTyp, pivotYValue
    )
    animation.duration = duration
    animation.interpolator = interpolator
    animation.setAnimationListener(object : RAnimtionListener() {
        override fun onAnimationEnd(animation: Animation?) {
            super.onAnimationEnd(animation)
            end.invoke()
        }
    })
    if (parent != null) {
        startAnimation(animation)
    }
    return animation
}

fun View.anim(
    from: Float = 0f,
    to: Float = 1f,
    duration: Long = 300L,
    interpolator: TimeInterpolator = LinearInterpolator(),
    end: (() -> Unit)? = null,
    update: (Float) -> Unit = {}
): Animator {
    val animator = ValueAnimator.ofFloat(from, to)
    animator.duration = duration
    animator.interpolator = interpolator
    animator.addUpdateListener {
        update(it.animatedValue as Float)
    }

    if (end != null) {
        animator.addListener(object : RAnimatorListener() {
            override fun onAnimationFinish(animation: Animator?, cancel: Boolean) {
                super.onAnimationFinish(animation, cancel)
                end.invoke()
            }
        })
    }

    if (parent != null && this.visibility == View.VISIBLE) {
        animator.start()
    }
    return animator
}