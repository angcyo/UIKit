package com.angcyo.uiview.less.widget.behavior

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.angcyo.uiview.less.kotlin.coordinatorParams

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/10/14
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
open class ContentCollapseBehavior(context: Context? = null, attributeSet: AttributeSet? = null) :
    ContentBehavior(context, attributeSet) {

    init {
        contentLayoutState =
            contentLayoutState or LAYOUT_FLAG_OFFSET or LAYOUT_FLAG_EXCLUDE
    }

    var titleBarCollapseCallback: TitleBarCollapseCallback? = null

    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {
        dependency.layoutParams.coordinatorParams {
            if (behavior is TitleBarCollapseBehavior) {
                (behavior as TitleBarCollapseBehavior).onTitleBarBehaviorCallback.let {
                    dependsLayout = dependency
                    titleBarCollapseCallback =
                        (it as OnTitleBarCollapseBehaviorCallback).titleBarCollapseCallback
                }
            }
        }
        return super.layoutDependsOn(parent, child, dependency)
    }

    override fun getFlagExcludeTop(default: Int): Int {
        return titleBarCollapseCallback?.getTitleBarMinHeight(dependsLayout!!) ?: 0
    }

    override fun getFlagOffsetTop(parent: CoordinatorLayout, child: View): Int {
        return titleBarCollapseCallback?.run {
            getTitleBarMaxHeight(dependsLayout!!) - getTitleBarMinHeight(dependsLayout!!)
        } ?: 0
    }
}