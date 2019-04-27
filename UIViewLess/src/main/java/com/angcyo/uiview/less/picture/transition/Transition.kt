package com.angcyo.uiview.less.picture.transition

import android.support.transition.TransitionManager
import android.support.transition.TransitionSet
import android.view.ViewGroup

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
    transitionSet: TransitionSet
) {
    onCaptureStartValues.invoke()
    sceneRoot.post {
        TransitionManager.beginDelayedTransition(sceneRoot, transitionSet)
        onCaptureEndValues.invoke()
    }
}