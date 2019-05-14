package com.angcyo.uiview.less.iview

import android.view.View
import android.view.ViewGroup
import com.angcyo.uiview.less.kotlin.childs

/**
 * Created by angcyo on 2019-2-27
 * ViewGroup中, 选中某个View
 */

open class ChoiceIView(val viewGroup: ViewGroup, val choiceMode: Int = CHOICE_MODE_SINGLE) {

    companion object {
        const val CHOICE_MODE_SINGLE = 1
        const val CHOICE_MODE_MULTI = 2

        fun get(viewGroup: ViewGroup, choiceMode: Int = CHOICE_MODE_SINGLE): ChoiceIView {
            return ChoiceIView(viewGroup, choiceMode)
        }
    }

    var onChoiceSelector: OnChoiceSelector? = null

    /**
     * 之前选中的index, 只在 单选中有效
     * */
    private var oldSelectedIndex = -1

    /**
     * 执行配置
     * */
    fun doIt() {

//        val size = viewGroup.childCount
        viewGroup.childs { index, childView ->

            onChoiceSelector?.onInitPosition(childView, index, choiceMode)

            if (onChoiceSelector?.onCanSelector(childView, index, choiceMode) == true) {
                childView.setOnClickListener {
                    if (it.isSelected) {
                        unSelector(index)
                    } else {
                        selector(index)
                    }
                }
            }
        }

//        if (size > 0) {
//            viewGroup.post {
//                selector(currentPosition)
//            }
//        }
    }

    /**
     * 选择, -1, 表示选择所有
     * */
    fun selector(index: Int) {
        choice(index, true)
    }

    /**
     * 取消选择, -1 表示取消所有
     * */
    fun unSelector(index: Int) {
        choice(index, false)
    }

    private fun choice(index: Int, selected: Boolean) {
        val size = viewGroup.childCount

        var childView: View? = null

        var oldSelectedChildView: View? = if (oldSelectedIndex in 0 until size) {
            viewGroup.getChildAt(oldSelectedIndex)
        } else {
            null
        }

        //之前的选中状态
        val oldSelected = if (index in 0 until size) {
            childView = viewGroup.getChildAt(index)
            childView.isSelected
        } else {
            false
        }

        childView?.let {
            if (choiceMode == CHOICE_MODE_SINGLE) {
                if (it == oldSelectedChildView && !selected) {
                    //单选模式下， 不允许取消同一个View的选中状态。
                    return
                }
            }

            if (selected) {
                selectedView(it, index)
            } else {
                unSelectedView(it, index)
            }
        }

        if (choiceMode == CHOICE_MODE_MULTI) {
            //多选
        } else {
            //单选
            if (oldSelected) {
                //
            } else {
                if (selected) {
                    //取消之前的选中状态
                    oldSelectedChildView?.apply {
                        if (isSelected) {
                            if (onChoiceSelector?.onCanUnSelector(this, oldSelectedIndex, choiceMode) == true) {
                                this.isSelected = false
                            }
                        }
                    }
                }
            }
        }

        if (selected) {
            oldSelectedIndex = index
        }

        if (index == -1) {
            viewGroup.childs { index, childView ->
                if (selected) {
                    selectedView(childView, index)
                } else {
                    unSelectedView(childView, index)
                }
            }
            onChoiceSelector?.onChoiceChange(getSelectedIndexs())
        } else if (childView != null) {
            onChoiceSelector?.onChoiceChange(getSelectedIndexs())
        }
    }

    /**
     * 选中/取消 一组
     * */
    fun choiceIndex(indexs: List<Int>, selected: Boolean = true) {
        var notify = false
        indexs.forEach {
            if (it in 0 until viewGroup.childCount) {
                notify = true
                if (selected) {
                    selectedView(viewGroup.getChildAt(it), it)
                } else {
                    unSelectedView(viewGroup.getChildAt(it), it)
                }
            }
        }
        if (notify) {
            onChoiceSelector?.onChoiceChange(getSelectedIndexs())
        }
    }

    fun getSelectedIndexs(): MutableList<Int> {
        val result = mutableListOf<Int>()
        viewGroup.childs { index, childView ->
            if (childView.isSelected) {
                result.add(index)
            }
        }
        return result
    }

    private fun selectedView(childView: View, index: Int) {
        val oldSelected = childView.isSelected
        if (oldSelected) {

        } else {
            if (onChoiceSelector?.onCanSelector(childView, index, choiceMode) == true) {
                childView.isSelected = true
                onChoiceSelector?.onChoiceSelector(childView, index)
            }
        }
    }

    private fun unSelectedView(childView: View, index: Int) {
        val oldSelected = childView.isSelected
        if (oldSelected) {
            if (onChoiceSelector?.onCanUnSelector(childView, index, choiceMode) == true) {
                childView.isSelected = false
                onChoiceSelector?.onChoiceUnSelector(childView, index)
            }
        } else {

        }
    }

    abstract class OnChoiceSelector {

        /**
         * 选中回调
         * */
        open fun onChoiceSelector(itemView: View, position: Int) {

        }

        /**
         * 取消选中回调
         * */
        open fun onChoiceUnSelector(itemView: View, position: Int) {

        }

        /**
         * 选中位置发生改变的回调
         * */
        open fun onChoiceChange(indexs: List<Int>) {

        }

        /**
         * 是否可以选中
         * */
        open fun onCanSelector(itemView: View, position: Int, choiceMode: Int): Boolean = true

        /**
         * 是否可以取消选中
         * */
        open fun onCanUnSelector(itemView: View, position: Int, choiceMode: Int): Boolean = true

        open fun onInitPosition(itemView: View, position: Int, choiceMode: Int) {

        }
    }
}