package com.angcyo.uiview.less.widget.group

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/09/10
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
open class RCoordinatorLayout : CoordinatorLayout {

    constructor(context: Context) : super(context) {
        initLayout(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initLayout(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initLayout(context, attrs)
    }

    protected open fun initLayout(context: Context, attrs: AttributeSet?) {

    }

    override fun onStartNestedScroll(child: View, target: View, axes: Int, type: Int): Boolean {
        return super.onStartNestedScroll(child, target, axes, type)
    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int
    ) {
        super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type)
    }
}