package com.angcyo.uiview.less.widget.behavior

import android.content.Context
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
import kotlin.math.min

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
    }

    /**多个flag可以组合*/
    var contentLayoutState = 0b00

    //当前Top偏移量
    protected var offsetTop = -1

    init {
        showLog = false

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

    //<editor-fold desc="辅助方法">

    protected fun needDependsLayout(): Boolean = contentLayoutState != 0 && dependsLayout != null

    //</editor-fold desc="辅助方法">

    //<editor-fold desc="布局方法">

    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {
        super.layoutDependsOn(parent, child, dependency)
        //取消默认的依赖关系建立, 某种会出现闭环依赖关系
        return false
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

                dependsLayout?.apply {
                    val usedHeight = measuredHeight + clp().topMargin + clp().bottomMargin
                    parent.onMeasureChild(
                        child,
                        parentWidthMeasureSpec,
                        widthUsed,
                        parentHeightMeasureSpec,
                        heightUsed + usedHeight
                    )
                    handle = true
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
                    top = dependsLayout?.measuredHeight ?: top
                }
            }

            var handle = false

            val left = parent.paddingLeft + child.clp().leftMargin
            var top = child.clp().topMargin

            if (contentLayoutState.have(LAYOUT_FLAG_EXCLUDE)) {

                dependsLayout?.apply {
                    val usedHeight = measuredHeight + clp().topMargin + clp().bottomMargin
                    top += usedHeight
                    handle = true
                }
            }

            if (contentLayoutState.have(LAYOUT_FLAG_OFFSET)) {
                val offsetTopMax = getOffsetTopMax(parent, child)
                if (offsetTopMax > 0) {
                    if (offsetTop == -1) {
                        offsetTop = offsetTopMax
                    }

                    top += offsetTop

                    handle = true
                }
            }

            if (handle) {
                child.layout(
                    left,
                    top,
                    left + child.measuredWidth,
                    top + child.measuredHeight
                )
            }
            handle
        } else {
            false
        }
    }

    //</editor-fold desc="布局方法">

    //<editor-fold desc="偏移滚动相关">

    /**顶部最大偏移距离*/
    protected fun getOffsetTopMax(
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

    //</editor-fold desc="偏移滚动相关">
}