package com.angcyo.uiview.less.widget.behavior

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.angcyo.uiview.less.R
import com.angcyo.uiview.less.base.helper.ViewGroupHelper
import com.angcyo.uiview.less.kotlin.*

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/10/12
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
open class TitleBarCollapseBehavior(context: Context? = null, attributeSet: AttributeSet? = null) :
    TitleBarBehavior(context, attributeSet) {

    init {
        onTitleBarBehaviorCallback = OnTitleBarCollapseBehaviorCallback()
        //关闭默认的scroll回调触发, 采用onDependentViewChanged触发
        titleBarScrollBehavior = 0
    }

    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {
        return super.layoutDependsOn(
            parent,
            child,
            dependency
        ) || dependency.layoutParams.coordinatorParams {
            if (behavior is ContentCollapseBehavior) {
                dependsLayout = dependency
            }
        }.run {
            dependsLayout != null && dependsLayout == dependency
        }
    }

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {
        super.onDependentViewChanged(parent, child, dependency)

        val titleBarMaxHeight =
            (onTitleBarBehaviorCallback as OnTitleBarCollapseBehaviorCallback).run {
                titleBarCollapseCallback.getTitleBarMaxHeight(child)
            }

        val maxScroll =
            (onTitleBarBehaviorCallback as OnTitleBarCollapseBehaviorCallback).run {
                titleBarCollapseCallback.getTitleBarMaxHeight(child) - titleBarCollapseCallback.getTitleBarMinHeight(
                    child
                )
            }

        dispatchGradient(child, titleBarMaxHeight - dependency.top, maxScroll)

        return false
    }
}

open class OnTitleBarCollapseBehaviorCallback : OnTitleBarBehaviorCallback() {

    /**需要移动到的目标view*/
    var targetViewId: Int = R.id.base_title_view

    /**需要移动的view*/
    var collapseViewId: Int = R.id.base_collapse_title_view

    /**背景view*/
    var collapseBackgroundViewId: Int = R.id.base_collapse_title_bar_background_layout

    var startRect: Rect? = null
    var endRect: Rect? = null

    /**top 改变时, 额外追加的增量*/
    var topIncrement = 0f * dp

    /**left 改变时, 额外追加的增量*/
    var leftIncrement = 0f * dp

    var titleBarCollapseCallback: TitleBarCollapseCallback = TitleBarCollapseCallback()

    init {
        alwaysShowTitle = false
        titleBackgroundGradient = false
        titleGradientFactor = 1f
        titleShowThreshold = 1f
    }

    override fun onChildLayout(behavior: TitleBarBehavior, child: View) {
        super.onChildLayout(behavior, child)

        if (startRect == null || endRect == null) {
            //延迟获取[View]在[child]中的[Rect]位置
            child.post {
                if (startRect == null) {
                    startRect = child.find<View>(collapseViewId)?.run {
                        getLocationInParent(child)
                    }
                }
                if (endRect == null) {
                    endRect = child.find<View>(targetViewId)?.run {
                        getLocationInParent(child)
                    }
                }
            }
        }
    }

    override fun onTitleBarGradientValue(
        behavior: TitleBarBehavior,
        child: View,
        currentScrollY: Int,
        maxScrollY: Int
    ): Float {
        return super.onTitleBarGradientValue(behavior, child, currentScrollY, maxScrollY)
    }

    override fun titleBarBackgroundView(child: View): View? {
        return child.find(collapseBackgroundViewId)
    }

    override fun onTitleBarGradient(
        behavior: TitleBarBehavior,
        child: View,
        currentScrollY: Int,
        maxScrollY: Int,
        ratio: Float
    ) {
        super.onTitleBarGradient(behavior, child, currentScrollY, maxScrollY, ratio)

        if (ratio >= titleShowThreshold) {
            ViewGroupHelper.build(child)
                .selector(targetViewId)
                .setAlpha(1f)
                .selector(collapseViewId)
                .setAlpha(0f)
        } else {
            ViewGroupHelper.build(child)
                .selector(targetViewId)
                .setAlpha(0f)
                .selector(collapseViewId)
                .setAlpha(1f)
        }

        //背景颜色的偏移
        titleBarCollapseCallback.let {
            titleBarBackgroundView(child)?.offsetTopTo(
                (0..-(it.getTitleBarMaxHeight(child) - it.getTitleBarMinHeight(child)))
                    .evaluate(ratio).toInt()
            )
        }

        if (startRect != null && endRect != null) {
            val startRect = startRect!!
            val endRect = endRect!!

            child.find<View>(collapseViewId)?.apply {
                val left = (startRect.left..endRect.left).evaluate(ratio) + leftIncrement
                val top = (startRect.top..endRect.top).evaluate(ratio) + topIncrement

                val startWidth = startRect.width()
                val startHeight = startRect.height()
                val currentWidth: Float = (startWidth..endRect.width()).evaluate(ratio)
                val scale = currentWidth / startWidth
                val currentHeight: Float = startHeight * scale

                this.scaleX = scale
                this.scaleY = scale

                //因为用了scale, 所以偏移量需要修正
                offsetLeftTo((left - (startWidth - currentWidth) / 2).toInt())
                offsetTopTo((top - (startHeight - currentHeight) / 2).toInt())
            }
        }
    }
}

/**标题栏高度提供回调*/
open class TitleBarCollapseCallback {
    open fun getTitleBarMaxHeight(titleBarLayout: View): Int {
        return titleBarLayout.measuredHeight
    }

    open fun getTitleBarMinHeight(titleBarLayout: View): Int {
        return titleBarLayout.v<View>(R.id.base_title_bar_content_layout)?.measuredHeight ?: 0
    }
}