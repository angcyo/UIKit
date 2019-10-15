package com.angcyo.uiview.less.widget.behavior

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.angcyo.uiview.less.R
import com.angcyo.uiview.less.base.helper.ViewGroupHelper
import com.angcyo.uiview.less.kotlin.*
import com.angcyo.uiview.less.widget.behavior.ContentBehavior.Companion.NO_INIT

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/10/12
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
open class TitleBarCollapseBehavior(context: Context? = null, attributeSet: AttributeSet? = null) :
    TitleBarBehavior(context, attributeSet) {

    var titleBarCollapseCallback: TitleBarCollapseCallback = TitleBarCollapseCallback()

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

    var _lastOffsetTop = NO_INIT

    override fun onLayoutChild(
        parent: CoordinatorLayout,
        child: View,
        layoutDirection: Int
    ): Boolean {

        _lastOffsetTop =
            child.find<View>(titleBarCollapseCallback.collapseBackgroundWrapViewId)?.top ?: NO_INIT

        return super.onLayoutChild(parent, child, layoutDirection)
    }

    override fun onLayoutChildAfter(parent: CoordinatorLayout, child: View, layoutDirection: Int) {
        super.onLayoutChildAfter(parent, child, layoutDirection)
        //背景布局偏移恢复操作
        if (_lastOffsetTop != NO_INIT) {
            titleBarCollapseCallback.let {
                child.find<View>(titleBarCollapseCallback.collapseBackgroundWrapViewId)
                    ?.offsetTopTo(_lastOffsetTop)
            }
        }
    }

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {
        super.onDependentViewChanged(parent, child, dependency)

        (onTitleBarBehaviorCallback as OnTitleBarCollapseBehaviorCallback).also { onTitleBarCollapseBehaviorCallback ->

            if (onTitleBarCollapseBehaviorCallback.isInit()) {
                //初始化过, 有 有效的Rect数据
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
            } else {
                //未初始化, 回归开始的位置
                dispatchGradient(child, 0, 1)
            }
        }

        return false
    }
}

open class OnTitleBarCollapseBehaviorCallback : OnTitleBarBehaviorCallback() {

    var startRect: Rect? = null
    var endRect: Rect? = null

    /**top 改变时, 额外追加的增量*/
    var topIncrement = 0f * dp

    /**left 改变时, 额外追加的增量*/
    var leftIncrement = 0f * dp

    init {
        alwaysShowTitle = false
        titleBackgroundGradient = false
        titleGradientFactor = 1f
        titleShowThreshold = 1f
    }

    fun isInit(): Boolean {
        return startRect?.isEmpty == false && endRect?.isEmpty == false
    }

    override fun onChildLayout(behavior: TitleBarBehavior, child: View) {
        super.onChildLayout(behavior, child)

        val titleBarCollapseCallback =
            (behavior as TitleBarCollapseBehavior).titleBarCollapseCallback

        //获取[View]在[child]中的[Rect]位置
        val startRectTemp = child.find<View>(titleBarCollapseCallback.collapseViewId)
            ?.getLocationInParent(child)?.run {
                if (this.isEmpty) {
                    null
                } else {
                    this
                }
            }

        val endRectTemp = child.find<View>(titleBarCollapseCallback.targetViewId)
            ?.getLocationInParent(child)?.run {
                if (this.isEmpty) {
                    null
                } else {
                    this
                }
            }

        if (!isInit()) {
            startRect = startRectTemp
        } else {
            if (endRect?.width() != endRectTemp?.width()) {
                startRect = startRectTemp
            }
        }

        endRect = endRectTemp
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
        return null
    }

    override fun onTitleBarGradient(
        behavior: TitleBarBehavior,
        child: View,
        currentScrollY: Int,
        maxScrollY: Int,
        ratio: Float
    ) {

        super.onTitleBarGradient(behavior, child, currentScrollY, maxScrollY, ratio)

        val titleBarCollapseCallback =
            (behavior as TitleBarCollapseBehavior).titleBarCollapseCallback

        if (ratio >= titleShowThreshold) {
            ViewGroupHelper.build(child)
                .selector(titleBarCollapseCallback.targetViewId)
                .setAlpha(1f)
                .selector(titleBarCollapseCallback.collapseViewId)
                .setAlpha(0f)
        } else {
            ViewGroupHelper.build(child)
                .selector(titleBarCollapseCallback.targetViewId)
                .setAlpha(0f)
                .selector(titleBarCollapseCallback.collapseViewId)
                .setAlpha(1f)
        }

        //背景布局偏移操作
        titleBarCollapseCallback.let {
            child.find<View>(titleBarCollapseCallback.collapseBackgroundWrapViewId)?.offsetTopTo(
                (0..-(it.getTitleBarMaxHeight(child) - it.getTitleBarMinHeight(child)))
                    .evaluate(ratio).toInt()
            )
        }

        if (startRect != null && endRect != null) {
            val startRect = startRect!!
            val endRect = endRect!!

            child.find<View>(titleBarCollapseCallback.collapseViewId)?.apply {
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

    /**需要移动到的目标view*/
    var targetViewId: Int = R.id.base_title_view

    /**需要移动的view*/
    var collapseViewId: Int = R.id.base_collapse_title_view

    /**背景颜色view*/
    var collapseBackgroundViewId: Int = R.id.base_collapse_title_bar_background_layout

    /**背景需要偏移的view*/
    var collapseBackgroundWrapViewId: Int = R.id.base_collapse_title_bar_background_wrap_layout

    /**真实标题栏布局id*/
    var titleBarContentLayoutId: Int = R.id.base_title_bar_content_layout

    open fun getTitleBarMaxHeight(titleBarLayout: View): Int {
        return titleBarLayout.measuredHeight
    }

    open fun getTitleBarMinHeight(titleBarLayout: View): Int {
        return titleBarLayout.v<View>(titleBarContentLayoutId)?.measuredHeight ?: 0
    }

    open fun isTitleBarHeightValid(titleBarLayout: View): Boolean {
        return titleBarLayout.v<View>(targetViewId)?.run {
            measuredWidth > 0 && measuredHeight > 0
        } == true && titleBarLayout.v<View>(collapseViewId)?.run {
            measuredWidth > 0 && measuredHeight > 0
        } == true
    }
}