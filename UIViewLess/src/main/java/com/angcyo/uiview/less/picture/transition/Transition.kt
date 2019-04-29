package com.angcyo.uiview.less.picture.transition

import android.graphics.Rect
import android.support.transition.*
import android.support.v4.view.animation.LinearOutSlowInInterpolator
import android.view.View
import android.view.ViewGroup
import com.angcyo.uiview.less.kotlin.setWidthHeight
import com.angcyo.uiview.less.picture.BaseTransitionFragment

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/04/27
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
fun defaultTransitionSet(listener: Transition.TransitionListener): TransitionSet {
    return TransitionSet().apply {
        addTransition(ChangeTransform())
        addTransition(ChangeBounds())
        addTransition(ChangeImageTransform())
        addTransition(ChangeClipBounds())
        addTransition(ChangeScroll())

        duration = BaseTransitionFragment.ANIM_DURATION
        interpolator = LinearOutSlowInInterpolator()

        addListener(listener)
    }
}

fun transition(
    sceneRoot: ViewGroup,
    onCaptureStartValues: () -> Unit,
    onCaptureEndValues: () -> Unit,
    listener: Transition.TransitionListener = TransitionListenerAdapter(),
    transitionSet: TransitionSet = defaultTransitionSet(listener)
) {
    transition(sceneRoot, sceneRoot, onCaptureStartValues, onCaptureEndValues, listener, transitionSet)
}

fun transition(
    sceneRoot: ViewGroup,
    targetView: View,
    onCaptureStartValues: () -> Unit,
    onCaptureEndValues: () -> Unit,
    listener: Transition.TransitionListener = TransitionListenerAdapter(),
    transitionSet: TransitionSet = defaultTransitionSet(listener)
) {
    transitionSet.addTarget(targetView)
    onCaptureStartValues.invoke()
    sceneRoot.post {
        TransitionManager.beginDelayedTransition(sceneRoot, transitionSet)
        onCaptureEndValues.invoke()
    }
}

/**
 * 从一个指定的矩形展开的过渡动画
 * */
fun transitionFromRect(
    rect: Rect,
    sceneRoot: ViewGroup, targetView: View = sceneRoot,
    onCaptureStartValues: () -> Unit = {}, onCaptureEndValues: () -> Unit = {},
    listener: Transition.TransitionListener = TransitionListenerAdapter(),
    transitionSet: TransitionSet = defaultTransitionSet(listener)
) {
    targetView.setWidthHeight(rect.width(), rect.height())
    targetView.translationX = rect.left.toFloat()
    targetView.translationY = rect.top.toFloat()

    transitionSet.addTarget(targetView)
    onCaptureStartValues.invoke()
    sceneRoot.post {
        TransitionManager.beginDelayedTransition(sceneRoot, transitionSet)

        targetView.setWidthHeight(-1, -1)
        targetView.translationX = 0f
        targetView.translationY = 0f

        onCaptureEndValues.invoke()
    }
}

/**
 * 缩小平移, 到指定的矩形位置
 * */
fun transitionToRect(
    rect: Rect,
    sceneRoot: ViewGroup, targetView: View = sceneRoot,
    onCaptureStartValues: () -> Unit = {}, onCaptureEndValues: () -> Unit = {},
    listener: Transition.TransitionListener = TransitionListenerAdapter(),
    transitionSet: TransitionSet = defaultTransitionSet(listener)
) {
    transitionSet.addTarget(targetView)
    onCaptureStartValues.invoke()
    sceneRoot.post {
        TransitionManager.beginDelayedTransition(sceneRoot, transitionSet)

        targetView.setWidthHeight(rect.width(), rect.height())
        targetView.translationX = rect.left.toFloat()
        targetView.translationY = rect.top.toFloat()

        onCaptureEndValues.invoke()
    }
}