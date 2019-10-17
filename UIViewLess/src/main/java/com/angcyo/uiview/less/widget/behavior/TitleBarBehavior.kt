package com.angcyo.uiview.less.widget.behavior

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import com.angcyo.uiview.less.R
import com.angcyo.uiview.less.base.helper.ViewGroupHelper
import com.angcyo.uiview.less.kotlin.color
import com.angcyo.uiview.less.kotlin.find
import com.angcyo.uiview.less.kotlin.have
import com.angcyo.uiview.less.resources.AnimUtil
import com.angcyo.uiview.less.resources.ViewResConfig
import kotlin.math.min

/**
 * 标题栏行为控制
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/09/27
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
open class TitleBarBehavior(context: Context? = null, attributeSet: AttributeSet? = null) :
    BaseDependsBehavior<View>(context, attributeSet) {

    companion object {
        /**激活渐变改变背景颜色*/
        const val TITLE_BAR_GRADIENT = 0x00000001
    }

    /**依赖view, 滚动时, 标题栏的行为*/
    var titleBarScrollBehavior = TITLE_BAR_GRADIENT

    var gradientStartConfig: ViewResConfig = ViewResConfig().apply {
        titleBarBackgroundColor = Color.TRANSPARENT
        titleItemTextColor = Color.WHITE
        titleTextColor = Color.WHITE
    }

    var gradientEndConfig: ViewResConfig = ViewResConfig()

    //标题栏的高度
    protected var titleBarHeight = 0
    //内容滚动的y值
    protected var contentScrollY = 0

    var onTitleBarBehaviorCallback: OnTitleBarBehaviorCallback = OnTitleBarBehaviorCallback()

    init {
        showLog = false

        context?.let {
            val array =
                context.obtainStyledAttributes(attributeSet, R.styleable.TitleBarBehavior_Layout)

            array.recycle()
        }
    }

    /**渐变改变背景颜色*/
    fun dispatchGradient(child: View) {
        if (titleBarScrollBehavior.have(TITLE_BAR_GRADIENT)) {
            var currentScrollY = 0
            var maxScrollY = titleBarHeight

            if (titleBarHeight > 0) {
                currentScrollY = contentScrollY
            } else {
                maxScrollY = 1
            }

            dispatchGradient(child, currentScrollY, maxScrollY)
        }
    }

    fun dispatchGradient(child: View, currentScrollY: Int, maxScrollY: Int) {
        val ratio =
            onTitleBarBehaviorCallback.onTitleBarGradientValue(
                this,
                child,
                currentScrollY,
                maxScrollY
            )
        onTitleBarBehaviorCallback.onTitleBarGradient(
            this,
            child,
            currentScrollY,
            maxScrollY,
            ratio
        )
    }

    override fun onLayoutChild(
        parent: CoordinatorLayout,
        child: View,
        layoutDirection: Int
    ): Boolean {
        super.onLayoutChild(parent, child, layoutDirection)
        titleBarHeight = child.measuredHeight
        return false
    }

    override fun onLayoutChildAfter(parent: CoordinatorLayout, child: View, layoutDirection: Int) {
        super.onLayoutChildAfter(parent, child, layoutDirection)
        onTitleBarBehaviorCallback.onChildLayout(this, child)
        dispatchGradient(child)
    }

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: View,
        directTargetChild: View,
        target: View,
        axes: Int,
        type: Int
    ): Boolean {
        super.onStartNestedScroll(
            coordinatorLayout,
            child,
            directTargetChild,
            target,
            axes,
            type
        )
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL
    }

    override fun onNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: View,
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int
    ) {
        super.onNestedScroll(
            coordinatorLayout,
            child,
            target,
            dxConsumed,
            dyConsumed,
            dxUnconsumed,
            dyUnconsumed,
            type
        )
        contentScrollY = dyConsumedAllSum + currentDyConsumedAll
        dispatchGradient(child)
    }

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {
        return super.onDependentViewChanged(parent, child, dependency)
    }
}

/**具体的行为控制回调*/
open class OnTitleBarBehaviorCallback {

    /**是否保持标题显示, 否则只会在背景颜色显示到底的时候才会显示标题*/
    var alwaysShowTitle = false

    /**标题栏上的item,icon是否渐变颜色*/
    var titleBarItemGradient = false

    /**标题栏背景是否渐变颜色*/
    var titleBackgroundGradient = true

    /**标题是否需要颜色渐变*/
    var titleTextGradient = true

    /**渐变因子, 值越小, 颜色渐变越慢*/
    var titleGradientFactor = 0.8f

    /**滑动到多少比例时, 显示标题配合[alwaysShowTitle]使用*/
    var titleShowThreshold = 0.8f

    open fun onChildLayout(behavior: TitleBarBehavior, child: View) {

    }

    /**控制渐变阈值*/
    open fun onTitleBarGradientValue(
        behavior: TitleBarBehavior,
        child: View,
        currentScrollY: Int, maxScrollY: Int
    ): Float {
        return min(1f, currentScrollY * titleGradientFactor / maxScrollY)
    }

    open fun titleBarBackgroundView(child: View): View? {
        return child
    }

    /**控制背景颜色*/
    open fun onTitleBarGradient(
        behavior: TitleBarBehavior,
        child: View,
        currentScrollY: Int,
        maxScrollY: Int,
        ratio: Float
    ) {

        //背景颜色控制
        if (titleBackgroundGradient) {
            titleBarBackgroundView(child)?.apply {
                val evaluateColor = AnimUtil.evaluateColor(
                    ratio,
                    behavior.gradientStartConfig.titleBarBackgroundColor,
                    behavior.gradientEndConfig.titleBarBackgroundColor
                )
                if (background == null || background is ColorDrawable) {
                    setBackgroundColor(evaluateColor)
                } else {
                    background.color(evaluateColor)
                }
            }
        }

        //标题显示控制
        if (alwaysShowTitle) {
            ViewGroupHelper.build(child)
                .selector(R.id.base_title_view)
                .setAlpha(1f)
        } else {
            ViewGroupHelper.build(child)
                .selector(R.id.base_title_view)
                .setAlpha(
                    if (ratio >= titleShowThreshold) {
                        1f
                    } else {
                        0f
                    }
                )
        }

        if (titleTextGradient) {
            val titleEvaluateColor = AnimUtil.evaluateColor(
                ratio,
                behavior.gradientStartConfig.titleTextColor,
                behavior.gradientEndConfig.titleTextColor
            )
            evaluateTextColor(child.find(R.id.base_title_view), titleEvaluateColor)
        }

        //item icon控制
        if (titleBarItemGradient) {

            //图标颜色控制
            val iconEvaluateColor = AnimUtil.evaluateColor(
                ratio,
                behavior.gradientStartConfig.titleItemIconColor,
                behavior.gradientEndConfig.titleItemIconColor
            )
            evaluateIconColor(child.find(R.id.base_title_left_layout), iconEvaluateColor)
            evaluateIconColor(child.find(R.id.base_title_right_layout), iconEvaluateColor)

            //文本颜色控制
            val textEvaluateColor = AnimUtil.evaluateColor(
                ratio,
                behavior.gradientStartConfig.titleItemTextColor,
                behavior.gradientEndConfig.titleItemTextColor
            )
            evaluateTextColor(child.find(R.id.base_title_left_layout), textEvaluateColor)
            evaluateTextColor(child.find(R.id.base_title_right_layout), textEvaluateColor)
        }
    }

    private fun evaluateTextColor(view: View?, color: Int) {
        ViewGroupHelper.build(view)
            .selector()
            .textColorFilter(color)
    }

    private fun evaluateIconColor(view: View?, color: Int) {
        ViewGroupHelper.build(view)
            .selector()
            .colorFilter(color)
    }
}