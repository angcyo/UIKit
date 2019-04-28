package com.angcyo.uiview.less.picture.transition

import android.support.transition.*
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.view.ViewGroup
import com.angcyo.uiview.less.picture.BaseTransitionFragment

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/04/27
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
fun transition(
    sceneRoot: ViewGroup,
    onCaptureStartValues: () -> Unit,
    onCaptureEndValues: () -> Unit,
    transitionSet: TransitionSet = TransitionSet().apply {
        addTransition(ChangeTransform())
        addTransition(ChangeBounds())
        addTransition(ChangeImageTransform())
        addTransition(ChangeClipBounds())
        addTransition(ChangeScroll())

        addTarget(sceneRoot)
        duration = BaseTransitionFragment.ANIM_DURATION
        interpolator = FastOutSlowInInterpolator()
    }
) {
    onCaptureStartValues.invoke()
    sceneRoot.post {
        TransitionManager.beginDelayedTransition(sceneRoot, transitionSet)
        onCaptureEndValues.invoke()
    }
}