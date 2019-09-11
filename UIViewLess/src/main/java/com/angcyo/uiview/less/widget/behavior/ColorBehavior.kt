package com.angcyo.uiview.less.widget.behavior

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.math.MathUtils
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.angcyo.uiview.less.R
import com.angcyo.uiview.less.kotlin.abs
import com.angcyo.uiview.less.kotlin.getColor
import com.angcyo.uiview.less.kotlin.minValue
import com.angcyo.uiview.less.widget.RSmartRefreshLayout
import com.google.android.material.animation.ArgbEvaluatorCompat

/**
 * 颜色变化行为
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/09/11
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */

class ColorBehavior(
    context: Context? = null,
    attrs: AttributeSet? = null
) : BaseDependsBehavior<View>(context, attrs) {

    var colorFrom = Color.TRANSPARENT
    var colorTo = getColor(R.color.theme_color_primary)

    protected val argbEvaluator: ArgbEvaluatorCompat by lazy {
        ArgbEvaluatorCompat()
    }

    protected var fraction = 0f

    init {
        showLog = false

        context?.let {
            val array = it.obtainStyledAttributes(attrs, R.styleable.ColorBehavior_Layout)

            colorFrom = array.getColor(
                R.styleable.ColorBehavior_Layout_layout_color_from,
                colorFrom
            )

            colorTo = array.getColor(
                R.styleable.ColorBehavior_Layout_layout_color_to,
                colorTo
            )

            array.recycle()
        }

    }

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {
        val changed = super.onDependentViewChanged(parent, child, dependency)
        if (dependsOffsetRect.top <= 0) {
            changeChildView(child, dependsOffsetRect.top.abs() * 1f / child.measuredHeight)
        }

        //updateDependsRect(dependency)
        return changed
    }

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: View,
        directTargetChild: View,
        target: View,
        axes: Int,
        type: Int
    ): Boolean {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL &&
                (dependsLayout == target ||
                        target is RecyclerView ||
                        target is RSmartRefreshLayout)
    }

    override fun onNestedScrollAccepted(
        coordinatorLayout: CoordinatorLayout,
        child: View,
        directTargetChild: View,
        target: View,
        axes: Int,
        type: Int
    ) {
        super.onNestedScrollAccepted(
            coordinatorLayout,
            child,
            directTargetChild,
            target,
            axes,
            type
        )
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

        if (dyConsumedAll >= 0) {
            val topFraction = dependsOffsetRect.top.abs() * 1f / child.measuredHeight
            val scrollFraction = dyConsumedAll * 1f / child.measuredHeight
            changeChildView(child, scrollFraction.minValue(topFraction))
        }
    }

    protected fun changeChildView(child: View, fraction: Float) {
        this.fraction = fraction
        child.setBackgroundColor(getEvaluatorColor(fraction, colorFrom, colorTo))
    }

    protected fun getEvaluatorColor(fraction: Float /*[0-1]*/, startValue: Int, endValue: Int): Int {
        return argbEvaluator.evaluate(MathUtils.clamp(fraction, 0f, 1f), startValue, endValue)!!
    }
}