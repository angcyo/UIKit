package com.angcyo.uiview.less.kotlin.dialog

import android.app.Dialog
import com.angcyo.uiview.less.R
import com.angcyo.uiview.less.recycler.RBaseViewHolder
import com.bigkoo.pickerview.adapter.ArrayWheelAdapter
import com.contrarywind.view.WheelView

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/05/11
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */

open class WheelDialogConfig : BaseDialogConfig() {

    /**
     * 选项集合
     * */
    var wheelItems = mutableListOf<Any>()

    /**
     * 无限循环样式
     * */
    var wheelCyclic = false

    /**
     * 默认显示的位置索引
     * */
    var defaultIndex = 0

    override var dialogLayoutId: Int = R.layout.dialog_wheel_single_layout

    /**
     * 选择回调, 返回 true, 则不会自动 调用 dismiss
     * */
    var onWheelItemSelector: (dialog: Dialog, index: Int, item: Any) -> Boolean = { _, _, _ -> false }

    var convertItemToString: (item: Any) -> CharSequence =

        {
            if (it is CharSequence) {
                it
            } else {
                it.toString()
            }
        }

    private var selectorIndex = -1

    init {
        positiveButtonListener = { dialog, _ ->
            if (onWheelItemSelector.invoke(dialog, selectorIndex, wheelItems[selectorIndex])) {
            } else {
                dialog.dismiss()
            }
        }
    }

    override fun onDialogInit(dialog: Dialog, dialogViewHolder: RBaseViewHolder) {
        super.onDialogInit(dialog, dialogViewHolder)

        dialogViewHolder.enable(R.id.positive_button, wheelItems.isNotEmpty())

        dialogViewHolder.v<WheelView>(R.id.wheel_view).apply {

            val stringList = mutableListOf<CharSequence>()

            for (item in wheelItems) {
                stringList.add(convertItemToString.invoke(item))
            }

            setOnItemSelectedListener {
                selectorIndex = it
            }

            adapter = ArrayWheelAdapter<CharSequence>(stringList)

            setCyclic(wheelCyclic)

            currentItem = defaultIndex
            selectorIndex = defaultIndex
        }
    }
}