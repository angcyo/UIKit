package com.angcyo.uiview.less.iview

import android.view.View
import android.view.ViewGroup
import com.angcyo.uiview.less.kotlin.childs

/**
 * Created by angcyo on 2018/12/02 17:08
 * 导航栏控制
 */

@Deprecated("请使用[DslSelector]")
class NavIView(val viewGroup: ViewGroup) {

    companion object {
        fun get(viewGroup: ViewGroup): NavIView {
            return NavIView(viewGroup)
        }
    }

    //默认选中第一个
    var currentPosition = 0

    var onNavSelector: OnNavSelector? = null

    private val navViews = mutableListOf<View>()

    /**
     * 执行配置
     * */
    fun doIt() {

        val size = viewGroup.childCount
        viewGroup.childs { index, childView ->
            navViews.add(childView)

            onNavSelector?.onInitPosition(childView, index)

            if (onNavSelector?.onCanSelector(index) == true) {
                childView.setOnClickListener {
                    selector(index)
                }
            }
        }

        if (size > 0) {
            viewGroup.post {
                selector(currentPosition)
            }
        }
    }

    /**
     * 选中某一项
     * */
    fun selector(index: Int) {
        val size = viewGroup.childCount

        if (index < size && onNavSelector?.onCanSelector(index) == true) {
            val itemView = navViews[index]
            if (itemView.isSelected) {
                //重复选择
                onNavSelector?.onNavReSelector(index)
            } else {
                val oldPosition = currentPosition
                if (size > currentPosition) {
                    navViews[currentPosition].isSelected = false
                }
                itemView.isSelected = true
                currentPosition = index

                onNavSelector?.onNavSelector(oldPosition, index)
            }
        }
    }

    interface OnNavSelector {

        fun onNavSelector(fromPosition: Int, toPosition: Int)
        fun onNavReSelector(position: Int) {

        }

        fun onCanSelector(position: Int): Boolean = true

        fun onInitPosition(childView: View, position: Int) {

        }
    }
}