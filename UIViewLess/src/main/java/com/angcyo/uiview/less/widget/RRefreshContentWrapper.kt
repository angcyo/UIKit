package com.angcyo.uiview.less.widget

import android.view.View
import com.scwang.smartrefresh.layout.impl.RefreshContentWrapper

/**
 * [RSmartRefreshLayout] 纯滚动模式下, 内容布局 [translationY] 监听
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/08/28
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
class RRefreshContentWrapper(view: View) : RefreshContentWrapper(view) {

    var translationListenerSet = mutableSetOf<OnContentViewTranslationListener>()

    override fun moveSpinner(
        spinner: Int,
        headerTranslationViewId: Int,
        footerTranslationViewId: Int
    ) {
        super.moveSpinner(spinner, headerTranslationViewId, footerTranslationViewId)

        var translated = false
        if (headerTranslationViewId != View.NO_ID) {
            val headerTranslationView =
                mOriginalContentView.findViewById<View>(headerTranslationViewId)
            if (headerTranslationView != null) {
                if (spinner > 0) {
                    translated = true
                }
            }
        }
        if (footerTranslationViewId != View.NO_ID) {
            val footerTranslationView =
                mOriginalContentView.findViewById<View>(footerTranslationViewId)
            if (footerTranslationView != null) {
                if (spinner < 0) {
                    translated = true
                }
            }
        }

        //L.i("->$translated $spinner")

        if (!translated) {
            translationListenerSet.forEach {
                it.onTranslation(view, spinner.toFloat())
            }
        } else {
            translationListenerSet.forEach {
                it.onTranslation(view, 0f)
            }
        }
    }
}

abstract class OnContentViewTranslationListener {

    /**
     * 纯滚动模式下, 内容偏移距离的回调
     * @param contentView 被移动的布局
     * @param translationY 被移动的目标位置. (非差值)
     * */
    open fun onTranslation(contentView: View, translationY: Float) {

    }
}