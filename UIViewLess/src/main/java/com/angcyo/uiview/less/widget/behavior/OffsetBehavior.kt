package com.angcyo.uiview.less.widget.behavior

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.core.view.ViewCompat
import com.angcyo.uiview.less.R
import com.angcyo.uiview.less.kotlin.coordinatorParams
import com.angcyo.uiview.less.kotlin.offsetTop
import com.angcyo.uiview.less.utils.UI
import kotlin.math.min

/**
 *  偏移[parent]第[offsetChildIndex]个[child]高度的距离
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/06/28
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
open class OffsetBehavior(context: Context, attrs: AttributeSet? = null) :
    LogBehavior<View>(context, attrs) {

    /**需要以哪个索引的视图当做参考*/
    var offsetChildIndex = 0

    //当前Top偏移量
    private var offsetTop = -1

    init {
        showLog = false

        val array = context.obtainStyledAttributes(attrs, R.styleable.OffsetBehavior_Layout)
        offsetChildIndex =
            array.getDimensionPixelOffset(
                R.styleable.OffsetBehavior_Layout_layout_offset_child_index,
                offsetChildIndex
            )
        array.recycle()
    }

    /**顶部最大偏移距离*/
    private fun getOffsetTopMax(
        parent: androidx.coordinatorlayout.widget.CoordinatorLayout,
        child: View
    ): Int {
        var offsetTop = 0
        if (offsetChildIndex in 0 until parent.childCount) {
            val targetChild = parent.getChildAt(offsetChildIndex)
            if (targetChild != child) {
                targetChild.layoutParams.coordinatorParams {
                    offsetTop = targetChild.measuredHeight + topMargin + bottomMargin
                }
            }
        }
        return offsetTop
    }

    /**
     * 关闭嵌套的内嵌滚动
     * [android:nestedScrollingEnabled="false"]
     * */
    override fun onStartNestedScroll(
        coordinatorLayout: androidx.coordinatorlayout.widget.CoordinatorLayout,
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

    override fun onLayoutChild(
        parent: androidx.coordinatorlayout.widget.CoordinatorLayout,
        child: View,
        layoutDirection: Int
    ): Boolean {
        val offsetTopMax = getOffsetTopMax(parent, child)
        if (offsetTopMax > 0) {
            if (offsetTop == -1) {
                offsetTop = offsetTopMax
            }
            val layoutParams: androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams =
                child.layoutParams as androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams

            val top = offsetTop + layoutParams.topMargin
            child.layout(
                layoutParams.leftMargin,
                top,
                parent.measuredWidth - parent.paddingRight - layoutParams.rightMargin,
                top + child.measuredHeight
            )
            return true
        } else {
            return super.onLayoutChild(parent, child, layoutDirection)
        }
    }

    override fun onNestedPreScroll(
        coordinatorLayout: androidx.coordinatorlayout.widget.CoordinatorLayout,
        child: View,
        target: View,
        dx: Int,
        dy: Int,
        consumed: IntArray,
        type: Int
    ) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)

        if (dy < 0) {
            //手指往下滑动
            if (!UI.canChildScrollUp(target)) {
                //RecyclerView的顶部没有滚动空间
                val offsetTopMax = getOffsetTopMax(coordinatorLayout, child)

                if (offsetTop < offsetTopMax) {
                    val consumedY = min(-dy, offsetTopMax - offsetTop)
                    consumed[1] = -consumedY

                    child.offsetTop(consumedY)
                    offsetTop = child.top
                }
            }
        } else if (dy > 0) {
            //手指往上滑动
            if (offsetTop > 0) {
                val consumedY = min(dy, offsetTop)
                consumed[1] = consumedY

                child.offsetTop(-consumedY)
                offsetTop = child.top
            }
        }
    }
}