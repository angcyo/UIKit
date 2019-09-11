package com.angcyo.uiview.less.widget.behavior

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.angcyo.uiview.less.kotlin.offsetTop

/**
 * 跟随目标, 一起向上/向下移动
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/09/10
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
class FollowOffsetBehavior(context: Context, attrs: AttributeSet? = null) :
    BaseDependsBehavior<View>(context, attrs) {

    init {
        showLog = false
    }

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {
        super.onDependentViewChanged(parent, child, dependency)
        child.offsetTop(constraintChildOffset(child, dependsOffsetRect.top))
        updateDependsRect(dependency)
        return dependsOffsetRect.top != 0
    }

    //约束一下偏移量
    protected fun constraintChildOffset(
        child: View,
        dy: Int
    ): Int {
        return dy
    }

}