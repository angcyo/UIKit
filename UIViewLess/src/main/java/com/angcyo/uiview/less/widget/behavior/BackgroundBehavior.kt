package com.angcyo.uiview.less.widget.behavior

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import com.angcyo.uiview.less.R
import com.angcyo.uiview.less.kotlin.exactly
import com.angcyo.uiview.less.kotlin.offsetTop
import com.angcyo.uiview.less.kotlin.setHeight
import com.angcyo.uiview.less.widget.OnContentViewTranslationListener
import com.angcyo.uiview.less.widget.RSmartRefreshLayout
import kotlin.math.max

/**
 * 背景布局行为控制
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/09/27
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
open class BackgroundBehavior(context: Context? = null, attributeSet: AttributeSet? = null) :
    BaseDependsBehavior<View>(context, attributeSet) {

    /**强制指定child的高度*/
    var childHeight: Int = -1

    var onBackgroundBehaviorCallback: OnBackgroundBehaviorCallback = OnBackgroundBehaviorCallback()

    protected val contentViewTranslationListener = object : OnContentViewTranslationListener() {
        override fun onTranslation(contentView: View, translationY: Float) {
            if (childView == null) {
                return
            }

            //恢复默认的child高度
            if (translationY == 0f && childViewDefaultHeight > 0) {
                childViewDefaultHeight = childView!!.measuredHeight
            }

            //放大or缩小child的高度
            onBackgroundBehaviorCallback.onContentOverScroll(
                this@BackgroundBehavior,
                childView!!,
                translationY,
                translationY / contentView.measuredHeight
            )
        }
    }

    protected var childView: View? = null
    var childViewDefaultHeight: Int = -1

    init {
        showLog = true

        context?.let {
            val array =
                context.obtainStyledAttributes(attributeSet, R.styleable.BackgroundBehavior_Layout)
            childHeight = array.getDimensionPixelOffset(
                R.styleable.BackgroundBehavior_Layout_layout_child_height,
                childHeight
            )
            array.recycle()
        }
    }

    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {
        val result = super.layoutDependsOn(parent, child, dependency)
        if (dependency is RSmartRefreshLayout) {
            childView = child
            dependency.addContentTranslationListener(contentViewTranslationListener)
        }
        return result
    }

    override fun onLayoutChild(
        parent: CoordinatorLayout,
        child: View,
        layoutDirection: Int
    ): Boolean {
        if (childViewDefaultHeight <= 0) {
            childViewDefaultHeight = child.measuredHeight
        }
        return super.onLayoutChild(parent, child, layoutDirection)
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

        childView = child
        onBackgroundBehaviorCallback.onContentScroll(
            this,
            child,
            dxConsumed,
            dyConsumed,
            dxConsumedAll,
            dyConsumedAll
        )
    }

    override fun onNestedFling(
        coordinatorLayout: CoordinatorLayout,
        child: View,
        target: View,
        velocityX: Float,
        velocityY: Float,
        consumed: Boolean
    ): Boolean {
        return super.onNestedFling(coordinatorLayout, child, target, velocityX, velocityY, consumed)
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

    override fun onStopNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: View,
        target: View,
        type: Int
    ) {
        super.onStopNestedScroll(coordinatorLayout, child, target, type)
    }

    override fun onMeasureChild(
        parent: CoordinatorLayout,
        child: View,
        parentWidthMeasureSpec: Int,
        widthUsed: Int,
        parentHeightMeasureSpec: Int,
        heightUsed: Int
    ): Boolean {
        super.onMeasureChild(
            parent,
            child,
            parentWidthMeasureSpec,
            widthUsed,
            parentHeightMeasureSpec,
            heightUsed
        )
        if (childHeight >= 0) {
            parent.onMeasureChild(child, parentWidthMeasureSpec, 0, exactly(childHeight), 0)
            return true
        } else {
            return false
        }
    }
}

open class OnBackgroundBehaviorCallback {

    /**
     * 是否激活缩放, 视差放大,
     * ImageView 会自带缩放效果.不需要此开关, 但是开了此开关效果会更自然
     * */
    var enableOverScale = true

    /**激活平移, 当内容滚动的时候, 是否要平移*/
    var enableTranslation = true

    /**激活内容越界滚动时, 高度跟随变化*/
    var enableOverScroll = true

    /**
     * 当内容边界滚动时, 一同时间内改变view的高度和offset, 会出现BUG.
     * 因为修改高度时, offset会被系统重置.
     * 而如果异步修改offset, 就会出现更严重的问题.
     * */
    open fun onContentOverScroll(
        behavior: BackgroundBehavior,
        child: View,
        translationY: Float,
        ratio: Float
    ) {
        if (enableOverScroll) {
            if (translationY > 0) {
                if (child.top != 0) {
                    //修正一下child
                    child.offsetTopAndBottom(-child.top)
                }
            }
            child.setHeight(
                (behavior.childViewDefaultHeight + translationY).toInt(),
                child.top != 0
            )
            if (enableOverScale) {
                child.scaleX = max(1f, 1 + ratio)
            }
        }
    }

    /**当内容滚动时*/
    open fun onContentScroll(
        behavior: BackgroundBehavior,
        child: View,
        dx: Int,
        dy: Int,
        dxAll: Int,
        dyAll: Int
    ) {
        if (enableTranslation) {
            //限制一下允许偏移的top值, 和偏移时机
            if (-dy > 0) {
                //手指往下滚动
                if (child.top - dy >= 0) {
                    child.offsetTop(-child.top)
                } else {
                    child.offsetTop(-dy)
                }
            } else {
                child.offsetTop(-dy)
            }
        }
    }
}