package com.angcyo.uiview.less.kotlin

import android.animation.Animator
import android.animation.ValueAnimator
import android.view.View
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import android.view.animation.ScaleAnimation
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