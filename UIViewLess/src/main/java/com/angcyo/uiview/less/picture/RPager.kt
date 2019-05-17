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
    fun start(fragmentManager: FragmentManager?, init: (ViewTransitionConfig.() -> Unit) = {}) {
        FragmentHelper.build(fragmentManager)
            .noAnim()
            .showFragment(ViewTransitionFragment().apply {
                transitionConfig.init()
            })
            .doIt()
    }

    fun pager(fragmentManager: FragmentManager?, init: (ViewTransitionConfig.() -> Unit) = {}) {
        FragmentHelper.build(fragmentManager)
            .noAnim()
            .showFragment(PagerTransitionFragment().apply {
                transitionConfig.init()
            })
            .doIt()
    }
}