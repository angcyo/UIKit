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
            .hideBeforeIndex(2)
            .showFragment(transitionFragment.apply {
                transitionConfig.init()
            })
            .doIt()
    }

    fun start(fragmentManager: FragmentManager?, init: (ViewTransitionConfig.() -> Unit) = {}) {
        start(fragmentManager, ViewTransitionFragment(), init)
    }

    /**
     * 简单使用:
     * <code>
     *     pagerCount = bean.attachments?.size ?: 0
     *     onGetPagerMediaUrl = { position ->
     *      bean.attachments[position].url ?: ""
     *    }
     * </code>
     *
     * 单图联动查看:
     * <pre>
     *   RPager.pager(parentFragmentManager()) {
     *      onGetTargetView = {
     *       view
     *     }
     *   }
     * </pre>
     *
     * 单图联动(在简单使用的基础上):
     * <pre>
     *     onGetTargetView = {
     *       holder.giv(R.id.task_work_order_pic)
     *     }
     *
     * </pre>
     * RecyclerView联动(在简单使用的基础上):
     * <pre>
     *     onGetRecyclerView = {
     *        recyclerView
     *     }
     * </pre>
     *
     * */
    fun pager(fragmentManager: FragmentManager?, init: (ViewTransitionConfig.() -> Unit) = {}) {
        start(fragmentManager, PagerTransitionFragment(), init)
    }

    /**
     * //全功能启动pager
     * <pre>
     * RPager.localMedia(fragmentManager) {
     *      localMediaList = allDatas
     *      startPagerIndex = startIndex
     *      enablePager = true
     *      onGetRecyclerView = {
     *       recyclerView
     *        }
     *      }
     *
     * </pre>
     *
     * //默认中间动画启动pager
     * <pre>
     *   RPager.localMedia(fragmentManger) {
     *      localMediaList = mutableListOf(LocalMedia(url))
     *   }
     * </pre>
     *
     * //单图简单联动启动pager
     * <pre>
     *   RPager.localMedia(fragmentManger) {
     *     localMediaList = mutableListOf(LocalMedia(url))
     *     onGetTargetView = { view }
     *  }
     * </pre>
     * */
    fun localMedia(
        fragmentManager: FragmentManager?,
        init: (LocalMediaTransitionFragment.LocalMediaTransitionConfig.() -> Unit) = {}
    ) {
        start(fragmentManager, LocalMediaTransitionFragment(), init as ViewTransitionConfig.() -> Unit)
    }
}