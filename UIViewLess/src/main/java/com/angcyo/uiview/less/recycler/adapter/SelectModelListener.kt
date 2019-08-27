package com.angcyo.uiview.less.recycler.adapter

import com.angcyo.lib.L

/**
 * [DslAdapter.addOnSelectorModelListener]
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/08/27
 */
open class SelectModelListener {

    /**
     * 单选模式下, 从[from]切换到[to], 仅在单选模式下会回调
     * 当主动调用 [DslAdapter.notifySelectListener] 时 [to] 可能为空
     * */
    var onSingleSelectChange: (from: DslAdapterItem?, to: DslAdapterItem?) -> Unit = { from, to ->
        L.i("选中: ${from?.itemIndexPosition} -> ${to?.itemIndexPosition}")
    }

    /**
     * 选中状态改变, 单选多选都会触发
     * @param from 触发事件的[item] 和 上面的[from] 不一样.
     * */
    var onSelectChange: (from: DslAdapterItem?, itemList: MutableList<DslAdapterItem>, indexList: MutableList<Int>) -> Unit =
        { from, itemList, indexList ->
            val builder = buildString {
                indexList.forEachIndexed { index, i ->
                    append(i)
                    append(":")
                    append(itemList[index].javaClass.simpleName)
                    append(":")
                    append(itemList[index].hashCode())
                    appendln()
                }
            }
            L.i("选中:\n$builder")
        }

    var onSelectMinLimitNotice: (minLimit: Int) -> Unit = {
        L.w("至少需要选中 $it 个.")
    }
}