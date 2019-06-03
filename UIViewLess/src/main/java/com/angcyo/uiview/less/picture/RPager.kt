package com.angcyo.uiview.less.picture

import android.support.v4.app.FragmentManager
import com.angcyo.uiview.less.base.helper.FragmentHelper
import com.angcyo.uiview.less.picture.transition.ViewTransitionConfig

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/05/16
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */

object RPager {
    fun start(
        fragmentManager: FragmentManager?,
        transitionFragment: ViewTransitionFragment,
        init: (ViewTransitionConfig.() -> Unit) = {}
    ) {
        FragmentHelper.build(fragmentManager)
            .noAnim()
            .showFragment(transitionFragment.apply {
                transitionConfig.init()
            })
            .doIt()
    }

    fun start(fragmentManager: FragmentManager?, init: (ViewTransitionConfig.() -> Unit) = {}) {
        start(fragmentManager, ViewTransitionFragment(), init)
    }

    fun pager(fragmentManager: FragmentManager?, init: (ViewTransitionConfig.() -> Unit) = {}) {
        start(fragmentManager, PagerTransitionFragment(), init)
    }

    fun localMedia(
        fragmentManager: FragmentManager?,
        init: (LocalMediaTransitionFragment.LocalMediaTransitionConfig.() -> Unit) = {}
    ) {
        start(fragmentManager, LocalMediaTransitionFragment(), init as ViewTransitionConfig.() -> Unit)
    }
}