package com.angcyo.uiview.less.utils

import androidx.recyclerview.widget.RecyclerView
import com.angcyo.lib.L
import com.angcyo.uiview.less.recycler.RRecyclerView

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/09/28
 */
class ScrollLockHelper {

    var lockListener = true

    /**当目标布局发生变化之后, 触发锁定滚动*/
    fun lock(recyclerView: RecyclerView) {
        recyclerView.viewTreeObserver.apply {
            this.addOnDrawListener {
                L.i("onDraw")
            }
            this.addOnGlobalFocusChangeListener { oldFocus, newFocus ->
                L.i("on...$oldFocus $newFocus")
            }
            this.addOnGlobalLayoutListener {
                L.i("this....")
                if (lockListener) {
                    if (recyclerView is RRecyclerView) {
                        recyclerView.scrollToLastBottom(false, false)

                    }
                    lockListener = false
                    recyclerView.postDelayed({
                        lockListener = true
                    }, 1000)
                }
            }
//            this.addOnPreDrawListener {
//                L.i("this....")
//                true
//            }
            this.addOnScrollChangedListener {
                L.i("this....")
            }
            this.addOnTouchModeChangeListener {
                L.i("this....")
            }
            this.addOnWindowFocusChangeListener {
                L.i("this....")
            }
        }
    }
}