package com.angcyo.uiview.less.widget.behavior

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.angcyo.uiview.less.R
import com.angcyo.uiview.less.kotlin.coordinatorParams
import com.angcyo.uiview.less.kotlin.have
import com.angcyo.uiview.less.kotlin.offsetTop
import com.angcyo.uiview.less.kotlin.padding
import com.angcyo.uiview.less.utils.UI

/**
 * 用来控制 内容布局 和 标题栏布局 之间的布局关系
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/09/10
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */

open class ContentBehavior(context: Context? = null, attributeSet: AttributeSet? = null) :
    BaseDependsBehavior<View>(context, attributeSet) {

    companion object {
        /**内容布局的高度, 需要排除标题栏的高度*/
        const val LAYOUT_FLAG_EXCLUDE = 0x00000001
        /**内容布局需要使用标题栏的高度当做paddingTop*/
        const val LAYOUT_FLAG_PADDING_TOP = 0x00000002
        /**具有偏移特性, 当内容滚动的时候, 会移动[dependsLayout]的高度*/
        const val LAYOUT_FLAG_OFFSET = 0x00000004

        const val NO_INIT = -0xffffff
    }

    /**多个flag可以组合*/
    var contentLayoutState = 0b00

    /**配合[LAYOUT_FLAG_EXCLUDE]使用, 内容布局的高度需要排除多少, -1表示标题栏的高度*/
    var contentLayoutExcludeHeight = -1

    //当前Top偏移量
    var _offsetTop = NO_INIT

    //是否发生过内嵌滚动, 保持 child 的 top 属性
    var _isNestedScroll = false

    init {
        showLog = false
        enableDependsOn = false

        context?.let {
            val array =
                context.obtainStyledAttributes(attributeSet, R.styleable.ContentBehavior_Layout)

            //行为控制
            contentLayoutState =
                array.getInt(
                    R.styleable.ContentBehavior_Layout_layout_content_layout_state,
                    contentLayoutState
                )

            array.recycle()
        }
    }

    //<editor-fold desc="方法区">

    /**改变布局结构状态*/
    fun changeLayoutState(parent: CoordinatorLayout, newState: Int) {
        val refresh = newState != contentLayoutState
        contentLayoutState = newState
        if (refresh) {
            parent.requestLayout()
        }
    }

    //</editor-fold desc="方法区">

    //<editor-fold desc="布局方法">

    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {
        //注意出现闭环依赖关系的情况
        return super.layoutDependsOn(parent, child, dependency)
    }

    override fun onMeasureChild(
        parent: CoordinatorLayout,
        child: View,
        parentWidthMeasureSpec: Int,
        widthUsed: Int,
        parentHeightMeasureSpec: Int,
        heightUsed: Int
    ): Boolean {
        return if (needDependsLayout()) {
            var handle = false
            if (contentLayoutState.have(LAYOUT_FLAG_EXCLUDE)) {

                var usedHeight = 0

                val flagExcludeTop = getFlagExcludeTop(-1)
                if (flagExcludeTop > 0) {
                    usedHeight = flagExcludeTop
                    handle = true
                }

                if (handle) {
                    parent.onMeasureChild(
                        child,
                        parentWidthMeasureSpec,
                        widthUsed,
                        parentHeightMeasureSpec,
                        heightUsed + usedHeight
                    )
                }
            }
            handle
        } else {
            false
        }
    }

    override fun onLayoutChild(
        parent: CoordinatorLayout,
        child: View,
        layoutDirection: Int
    ): Boolean {
        return if (needDependsLayout()) {
            if (contentLayoutState.have(LAYOUT_FLAG_PADDING_TOP)) {
                child.padding {
                    top = getFlagPaddingTop(top)
                }
            }

            var handle = false

            val left = parent.paddingLeft + child.clp().leftMargin
            var top = child.clp().topMargin

            if (contentLayoutState.have(LAYOUT_FLAG_EXCLUDE)) {

                val flagExcludeTop = getFlagExcludeTop(-1)
                if (flagExcludeTop > 0) {
                    top += flagExcludeTop
                    handle = true
                }
            }

            if (contentLayoutState.have(LAYOUT_FLAG_OFFSET)) {
                val offsetTopMax = getFlagOffsetTop(parent, child)
                if (offsetTopMax > 0) {
                    top += offsetTopMax
                    handle = true
                }
            }

            if (handle) {
                if (_isNestedScroll) {
                    //所有通过[Offset]的操作, 都需要在[onLayout]的时候, 恢复之前的值. 恢复top值
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        if (child.isLaidOut) {
                            //已经布局过
                            top = _offsetTop
                        }
                    } else {
                        if (_offsetTop != NO_INIT) {
                            top = _offsetTop
                        }
                    }
                }
                child.layout(
                    left,
                    top,
                    left + child.measuredWidth,
                    top + child.measuredHeight
                )
                _offsetTop = child.top
            }
            handle
        } else {
            false
        }
    }

    //</editor-fold desc="布局方法">

    //<editor-fold desc="辅助方法">

    open fun needDependsLayout(): Boolean = contentLayoutState != 0 && dependsLayout != null

    open fun getFlagPaddingTop(default: Int = 0): Int {
        return dependsLayout?.measuredHeight ?: default
    }

    open fun getFlagExcludeTop(default: Int = 0): Int {
        return if (contentLayoutState.have(LAYOUT_FLAG_EXCLUDE)) {
            if (contentLayoutExcludeHeight > 0) {
                contentLayoutExcludeHeight
            } else {
                dependsLayout?.run {
                    measuredHeight + clp().topMargin + clp().bottomMargin
                } ?: default
            }
        } else {
            0
        }
    }

    /**顶部布局偏移的最大距离*/
    open fun getFlagOffsetTop(
        parent: CoordinatorLayout,
        child: View
    ): Int {
        var offsetTop = 0
        if (dependsLayout != null && dependsLayout != child) {
            dependsLayout!!.layoutParams.coordinatorParams {
                offsetTop = dependsLayout!!.measuredHeight + topMargin + bottomMargin
            }
        }
        return offsetTop
    }

    /**顶部最大偏移的[Top]值*/
    open fun getOffsetTopMax(
        parent: CoordinatorLayout,
        child: View
    ): Int {
        return getFlagExcludeTop(0) + getFlagOffsetTop(parent, child)
    }

    /**顶部滚动最小的[Top]值*/
    open fun getOffsetTopMin(
        parent: CoordinatorLayout,
        child: View
    ): Int {
        return getFlagExcludeTop(0)
    }
    //</editor-fold desc="辅助方法">

    //<editor-fold desc="偏移滚动相关">

    /**
     * 关闭嵌套的内嵌滚动
     * [android:nestedScrollingEnabled="false"]
     * */
    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: View,
        directTargetChild: View,
        target: View,
        axes: Int,
        type: Int
    ): Boolean {
//        if (target != directTargetChild) {
//            //关闭嵌套的内嵌滚动
//            target.isNestedScrollingEnabled = false
//        }
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL
    }

    override fun onNestedPreScroll(
        coordinatorLayout: CoordinatorLayout,
        child: View,
        target: View,
        dx: Int,
        dy: Int,
        consumed: IntArray,
        type: Int
    ) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)

        //滚动到边界时, 终止越界滚动
        if (dy > 0) {
            //手指向上滑动
            if (!UI.canChildScrollDown(target)) {
                (target as? RecyclerView)?.stopScroll()
            }
        } else if (dy < 0) {
            //手指向下滑动
            if (!UI.canChildScrollUp(target)) {
                (target as? RecyclerView)?.stopScroll()
            }
        }

        if (contentLayoutState.have(LAYOUT_FLAG_OFFSET)) {

            _isNestedScroll = _isNestedScroll || dy != 0

            val offsetTopMin = getOffsetTopMin(coordinatorLayout, child)
            val offsetTopMax = getOffsetTopMax(coordinatorLayout, child)

            if (dy < 0) {
                //手指往下滑动
                if (!UI.canChildScrollUp(target)) {
                    //RecyclerView的顶部没有滚动空间
                    consumed[1] = child.offsetTop(
                        -dy,
                        offsetTopMin,
                        offsetTopMax
                    )
                    _offsetTop = child.top
                }
            } else if (dy > 0) {
                //手指往上滑动
                consumed[1] = child.offsetTop(
                    -dy,
                    offsetTopMin,
                    offsetTopMax
                )
                _offsetTop = child.top
            }
        }
    }

    //</editor-fold desc="偏移滚动相关">
}