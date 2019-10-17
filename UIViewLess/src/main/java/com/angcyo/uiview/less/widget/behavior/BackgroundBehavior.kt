package com.angcyo.uiview.less.widget.behavior

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import com.angcyo.uiview.less.R
import com.angcyo.uiview.less.kotlin.*
import com.angcyo.uiview.less.utils.UI
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
        set(value) {
            field = value
            childViewDefaultHeight = value
        }

    /**背景效果处理回调*/
    var onBackgroundBehaviorCallback: OnBackgroundBehaviorCallback = OnBackgroundBehaviorCallback()

    internal var _lastContentTranslationY: Float = 0f
    private val contentViewTranslationListener = object : OnContentViewTranslationListener() {
        override fun onTranslation(contentView: View, translationY: Float) {
            if (_childView == null) {
                return
            }
            _contentView = contentView
            if (translationY > 0) {
                //内容往下偏移
                _notifyScrollTopTo(_childView!!, translationY.toInt())
            } else {
                /*min(_lastChildTop, translationY.toInt())*/
                _notifyScrollTopTo(_childView!!, _lastScrollTop + translationY.toInt())
            }

            _lastContentTranslationY = translationY
        }
    }

    //背景布局
    var _childView: View? = null
    //内容布局, 用来判断是否可以滚动
    var _contentView: View? = null
    internal var childViewDefaultHeight: Int = -1

    init {
        showLog = false

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
            _childView = child
            _contentView = dependency
            dependency.addContentTranslationListener(contentViewTranslationListener)
        } else {
            dependency.find<RSmartRefreshLayout>(R.id.base_refresh_layout)?.apply {
                _childView = child
                _contentView = dependency
                addContentTranslationListener(contentViewTranslationListener)
            }
        }
        return result
    }

    var _lastScrollTop = 0
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
        _lastScrollTop = -(dyConsumedAllSum + currentDyConsumedAll)
        _notifyScrollTopTo(child, _lastScrollTop + _lastContentTranslationY.toInt())
    }

    fun _notifyScrollTopTo(child: View, top: Int) {
        onBackgroundBehaviorCallback.onScrollTopTo(this, child, top)
    }

    var _lastChildTop = 0
    override fun onLayoutChild(
        parent: CoordinatorLayout,
        child: View,
        layoutDirection: Int
    ): Boolean {
        _lastChildTop = child.top
        return super.onLayoutChild(parent, child, layoutDirection)
    }

    override fun onLayoutChildAfter(parent: CoordinatorLayout, child: View, layoutDirection: Int) {
        super.onLayoutChildAfter(parent, child, layoutDirection)
        if (childViewDefaultHeight <= 0) {
            childViewDefaultHeight = child.measuredHeight
        }

        _contentView?.apply {
            val view = findRecyclerView() ?: this
            if (UI.canChildScrollDown(view) || UI.canChildScrollUp(view)) {
                //可以滚动
            } else {
                //不可用滚动
                dyConsumedAllSum = 0
                if (_lastContentTranslationY < 0) {
                    _lastChildTop = _lastContentTranslationY.toInt()
                } else {
                    _lastContentTranslationY = 0f
                    _lastChildTop = 0
                }
            }
        }

        val lp = child.layoutParams.coordinatorParams()!!
        val left = lp.leftMargin
        child.offsetLeftTo(left)
        child.offsetTopTo(_lastChildTop)
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
        return if (childHeight >= 0) {
            parent.onMeasureChild(child, parentWidthMeasureSpec, 0, exactly(childHeight), 0)
            true
        } else {
            false
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

    /**当内容边界滚动时, 改变高度*/
    var enableOverScrollHeightChange = true

    /**
     * 当内容边界滚动时, 一同时间内改变view的高度和offset, 会出现BUG.
     * 因为修改高度时, offset会被系统重置.
     * 而如果异步修改offset, 就会出现更严重的问题.
     * 尽量避免这种情况的存在.
     * */
    open fun onScrollTopTo(behavior: BackgroundBehavior, child: View, scrollTop: Int) {
        if (enableTranslation) {
            child.offsetTopTo(scrollTop, -child.measuredHeight, 0)
        }
        if (enableOverScroll) {

            if (enableOverScrollHeightChange) {
                if (scrollTop > 0) {
                    //只有往下覆盖滚动,才改变高度
                    child.setHeight((behavior.childViewDefaultHeight + scrollTop))
                }
            }

            val ratio = scrollTop * 1f / child.measuredHeight
            if (enableOverScale) {
                child.scaleX = max(1f, 1 + ratio)
            }
        }
    }
}