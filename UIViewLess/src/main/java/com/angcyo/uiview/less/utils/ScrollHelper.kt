package com.angcyo.uiview.less.utils

import android.view.View
import android.view.ViewTreeObserver
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.angcyo.lib.L

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/09/28
 */
class ScrollHelper {

    companion object {
        /**滚动类别: 默认不特殊处理. 滚动到item显示了就完事*/
        const val SCROLL_TYPE_NORMAL = 0
        /**滚动类别: 将item滚动到第一个位置*/
        const val SCROLL_TYPE_TOP = 1
        /**滚动类别: 将item滚动到最后一个位置*/
        const val SCROLL_TYPE_BOTTOM = 2
    }

    internal var recyclerView: RecyclerView? = null

    /**触发滚动是否伴随了adapter的addItem*/
    var isFromAddItem = false

    /**滚动是否需要动画*/
    var isScrollAnim = false

    /**滚动类别*/
    var scrollType = SCROLL_TYPE_NORMAL

    init {
        resetValue()
    }

    fun attach(recyclerView: RecyclerView) {
        if (this.recyclerView == recyclerView) {
            return
        }
        detach()
        this.recyclerView = recyclerView
    }

    fun detach() {
        recyclerView = null
    }

    fun resetValue() {
        isFromAddItem = false
        isScrollAnim = false

        scrollType = SCROLL_TYPE_NORMAL
    }

    fun lastItemPosition(): Int {
        return (recyclerView?.layoutManager?.itemCount ?: 0) - 1
    }

    fun scrollToLast() {
        scroll(lastItemPosition())
    }

    fun scroll(position: Int) {
        if (check(position)) {
            recyclerView?.stopScroll()

            if (isPositionVisible(position)) {
                scrollWithVisible(position, scrollType, isScrollAnim)
            } else {
                if (isScrollAnim) {
                    if (isFromAddItem) {
                        if (recyclerView?.itemAnimator is SimpleItemAnimator) {
                            //itemAnimator 自带动画
                            recyclerView?.scrollToPosition(position)
                        } else {
                            recyclerView?.smoothScrollToPosition(position)
                        }
                    } else {
                        recyclerView?.smoothScrollToPosition(position)
                    }
                } else {
                    if (isFromAddItem) {
                        val itemAnimator = recyclerView?.itemAnimator
                        if (itemAnimator != null) {
                            //有默认的动画
                            recyclerView?.itemAnimator = null
                            OnNoAnimScrollIdleListener(itemAnimator).attach(recyclerView!!)
                        }
                    }
                    recyclerView?.scrollToPosition(position)
                }
                if (scrollType != SCROLL_TYPE_NORMAL) {
                    //不可见时, 需要现滚动到可见位置, 再进行微调
                    OnScrollIdleListener(position, scrollType, isScrollAnim).attach(recyclerView!!)
                }
            }
            resetValue()
        }
    }

    private var lockScrollLayoutListener: LockScrollLayoutListener? = null

    /**
     * 当界面有变化时, 自动滚动到最后一个位置
     * [unlockLastPosition]
     * */
    fun lockLastPosition(config: LockScrollLayoutListener.() -> Unit = {}) {
        if (lockScrollLayoutListener == null && recyclerView != null) {
            lockScrollLayoutListener = LockScrollLayoutListener().apply {
                scrollAnim = isScrollAnim
                config()
                attach(recyclerView!!)
            }
        }
    }

    fun unlockLastPosition() {
        lockScrollLayoutListener?.detach()
        lockScrollLayoutListener = null
    }

    /**当需要滚动的目标位置已经在屏幕上可见*/
    internal fun scrollWithVisible(position: Int, scrollType: Int, anim: Boolean) {
        when (scrollType) {
            SCROLL_TYPE_NORMAL -> {
                //nothing
            }
            SCROLL_TYPE_TOP -> {
                viewByPosition(position)?.apply {
                    val dx = recyclerView!!.layoutManager!!.getDecoratedLeft(this) -
                            recyclerView!!.paddingLeft
                    val dy = recyclerView!!.layoutManager!!.getDecoratedTop(this) -
                            recyclerView!!.paddingTop

                    if (anim) {
                        recyclerView?.smoothScrollBy(dx, dy)
                    } else {
                        recyclerView?.scrollBy(dx, dy)
                    }
                }
            }
            SCROLL_TYPE_BOTTOM -> {
                viewByPosition(position)?.apply {
                    val dx = recyclerView!!.layoutManager!!.getDecoratedRight(this) -
                            recyclerView!!.measuredWidth + recyclerView!!.paddingRight
                    val dy = recyclerView!!.layoutManager!!.getDecoratedBottom(this) -
                            recyclerView!!.measuredHeight + recyclerView!!.paddingBottom

                    if (anim) {
                        recyclerView?.smoothScrollBy(dx, dy)
                    } else {
                        recyclerView?.scrollBy(dx, dy)
                    }
                }
            }
        }
    }

    /**位置是否可见*/
    private fun isPositionVisible(position: Int): Boolean {
        return recyclerView?.layoutManager.isPositionVisible(position)
    }

    private fun viewByPosition(position: Int): View? {
        return recyclerView?.layoutManager?.findViewByPosition(position)
    }

    private fun check(position: Int): Boolean {
        if (recyclerView == null) {
            L.e("请先调用[attach]方法.")
            return false
        }

        if (recyclerView?.adapter == null) {
            L.w("忽略, [adapter] is null")
            return false
        }

        if (recyclerView?.layoutManager == null) {
            L.w("忽略, [layoutManager] is null")
            return false
        }

        val itemCount = recyclerView?.layoutManager?.itemCount ?: 0
        if (position < 0 || position >= itemCount) {
            L.w("忽略, [position] 需要在 [0,$itemCount) 之间.")
            return false
        }

        return true
    }

    fun log(recyclerView: RecyclerView) {
        recyclerView.viewTreeObserver.apply {
            this.addOnDrawListener {
                L.i("onDraw")
            }
            this.addOnGlobalFocusChangeListener { oldFocus, newFocus ->
                L.i("on...$oldFocus ->$newFocus")
            }
            this.addOnGlobalLayoutListener {
                L.w("this....")
            }
            //此方法回调很频繁
            this.addOnPreDrawListener {
                //L.v("this....")
                true
            }
            this.addOnScrollChangedListener {
                L.i("this....${recyclerView.scrollState}")
            }
            this.addOnTouchModeChangeListener {
                L.i("this....")
            }
            this.addOnWindowFocusChangeListener {
                L.i("this....")
            }
        }
    }

    private inner abstract class OnScrollListener : ViewTreeObserver.OnScrollChangedListener,
        IAttachListener {
        var attachView: View? = null

        override fun attach(view: View) {
            detach()
            attachView = view
            view.viewTreeObserver.addOnScrollChangedListener(this)
        }

        override fun detach() {
            attachView?.viewTreeObserver?.removeOnScrollChangedListener(this)
        }

        override fun onScrollChanged() {
            onScrollChanged(recyclerView?.scrollState ?: RecyclerView.SCROLL_STATE_IDLE)
            detach()
        }

        abstract fun onScrollChanged(state: Int)
    }

    /**滚动结束之后, 根据类别, 继续滚动.*/
    private inner class OnScrollIdleListener(
        val position: Int,
        val scrollType: Int,
        val anim: Boolean
    ) :
        OnScrollListener() {

        override fun onScrollChanged(state: Int) {
            if (state == RecyclerView.SCROLL_STATE_IDLE) {
                scrollWithVisible(position, scrollType, anim)
            }
        }
    }

    private inner class OnNoAnimScrollIdleListener(val itemAnimator: RecyclerView.ItemAnimator?) :
        OnScrollListener() {

        override fun onScrollChanged(state: Int) {
            if (state == RecyclerView.SCROLL_STATE_IDLE) {
                recyclerView?.itemAnimator = itemAnimator
            }
        }
    }

    /**锁定滚动到最后一个位置*/
    inner class LockScrollLayoutListener : ViewTreeObserver.OnGlobalLayoutListener,
        IAttachListener, Runnable {

        /**激活滚动动画*/
        var scrollAnim: Boolean = true
        /**激活第一个滚动的动画*/
        var firstScrollAnim: Boolean = false

        /**不检查界面 情况, 强制滚动到最后的位置. 关闭后. 会智能判断*/
        var force: Boolean = false

        /**第一次时, 是否强制滚动*/
        var firstForce: Boolean = true

        /**滚动阈值, 倒数第几个可见时, 就允许滚动*/
        var scrollThreshold = 2

        /**锁定需要滚动的position, -1就是最后一个*/
        var lockPosition = RecyclerView.NO_POSITION

        /**是否激活功能*/
        var enableLock = true

        override fun run() {
            if (!enableLock || recyclerView?.layoutManager?.itemCount ?: 0 <= 0) {
                return
            }

            isScrollAnim = if (firstForce) firstScrollAnim else scrollAnim
            scrollType = SCROLL_TYPE_BOTTOM

            val position =
                if (lockPosition == RecyclerView.NO_POSITION) lastItemPosition() else lockPosition

            if (force || firstForce) {
                scroll(position)
            } else {
                val lastItemPosition = lastItemPosition()
                if (lastItemPosition != RecyclerView.NO_POSITION) {
                    val findLastVisibleItemPosition =
                        recyclerView?.layoutManager.findLastVisibleItemPosition()

                    //智能判断是否可以锁定
                    if (lastItemPosition - findLastVisibleItemPosition <= scrollThreshold) {
                        //最后第一个或者最后第2个可见, 智能判断为可以滚动到尾部
                        scroll(position)
                    }
                }
            }

            firstForce = false
        }

        var attachView: View? = null

        override fun attach(view: View) {
            detach()
            attachView = view
            view.viewTreeObserver.addOnGlobalLayoutListener(this)
        }

        override fun detach() {
            attachView?.removeCallbacks(this)
            attachView?.viewTreeObserver?.removeOnGlobalLayoutListener(this)
        }

        override fun onGlobalLayout() {
            attachView?.removeCallbacks(this)
            if (enableLock) {
                attachView?.post(this)
            }
        }
    }

    private interface IAttachListener {
        fun attach(view: View)

        fun detach()
    }
}

fun RecyclerView.LayoutManager?.findFirstVisibleItemPosition(): Int {
    var result = RecyclerView.NO_POSITION
    this?.also { layoutManager ->
        var firstItemPosition: Int = -1
        if (layoutManager is LinearLayoutManager) {
            firstItemPosition = layoutManager.findFirstVisibleItemPosition()
        } else if (layoutManager is StaggeredGridLayoutManager) {
            firstItemPosition =
                layoutManager.findFirstVisibleItemPositions(null).firstOrNull() ?: -1
        }
        result = firstItemPosition
    }
    return result
}

fun RecyclerView.LayoutManager?.findLastVisibleItemPosition(): Int {
    var result = RecyclerView.NO_POSITION
    this?.also { layoutManager ->
        var lastItemPosition: Int = -1
        if (layoutManager is LinearLayoutManager) {
            lastItemPosition = layoutManager.findLastVisibleItemPosition()
        } else if (layoutManager is StaggeredGridLayoutManager) {
            lastItemPosition =
                layoutManager.findLastVisibleItemPositions(null).lastOrNull() ?: -1
        }
        result = lastItemPosition
    }
    return result
}

fun RecyclerView.LayoutManager?.isPositionVisible(position: Int): Boolean {
    return position >= 0 && position in findFirstVisibleItemPosition()..findLastVisibleItemPosition()
}