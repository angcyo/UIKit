package com.angcyo.uiview.less.kotlin.dialog

import android.app.Dialog
import android.view.View
import com.angcyo.uiview.less.R
import com.angcyo.uiview.less.iview.ChoiceIView
import com.angcyo.uiview.less.recycler.RBaseViewHolder

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/05/11
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */

open class MenuDialogConfig : ItemDialogConfig() {
    init {
        showBottomCancelLayout = false

        positiveButtonListener = { dialog, _ ->
            if (choiceModel > 0) {
                if (onChoiceItemList.invoke(dialog, choiceIView!!.getSelectedIndexs())) {

                } else {
                    dialog.dismiss()
                }
            } else {
                dialog.dismiss()
            }
        }
    }

    /**
     * 选择模式 [ChoiceIView.CHOICE_MODE_SINGLE] [ChoiceIView.CHOICE_MODE_MULTI]
     * */
    var choiceModel = -1

    /**
     * 默认选中
     * */
    var defaultSelectorIndexList = mutableListOf<Int>()

    /**
     * 选中回调, 返回 true, 则不会自动 调用 dismiss
     * */
    var onChoiceItemList: (dialog: Dialog, indexList: MutableList<Int>) -> Boolean = { _, _ -> false }

    override var onItemClick: (dialog: Dialog, index: Int, item: Any) -> Boolean = { dialog, index, item ->
        if (choiceModel > 0) {
            true
        } else {
            super.onItemClick.invoke(dialog, index, item)
        }
    }

    override fun onDialogInit(dialog: Dialog, dialogViewHolder: RBaseViewHolder) {
        super.onDialogInit(dialog, dialogViewHolder)
    }

    override fun initControlLayout(dialog: Dialog, dialogViewHolder: RBaseViewHolder) {
        super.initControlLayout(dialog, dialogViewHolder)

        if (choiceModel > 0) {
            dialogViewHolder.visible(R.id.title_layout, true)
            dialogViewHolder.tv(R.id.positive_button)?.visibility = View.VISIBLE
            dialogViewHolder.tv(R.id.negative_button)?.visibility = View.VISIBLE
        }
    }

    private var choiceIView: ChoiceIView? = null
    override fun inflateItems(dialog: Dialog, dialogViewHolder: RBaseViewHolder) {
        super.inflateItems(dialog, dialogViewHolder)

        if (choiceModel > 0) {
            choiceIView = ChoiceIView.get(dialogViewHolder.group(R.id.item_wrap_layout), choiceModel).apply {

                onChoiceSelector = object : ChoiceIView.OnChoiceSelector() {
                    override fun onChoiceSelector(itemView: View, position: Int) {
                        super.onChoiceSelector(itemView, position)

                        itemView.findViewById<View>(R.id.item_text_view)?.isSelected = true
                    }

                    override fun onChoiceUnSelector(itemView: View, position: Int) {
                        super.onChoiceUnSelector(itemView, position)

                        itemView.findViewById<View>(R.id.item_text_view)?.isSelected = false
                    }
                }

                doIt()

                choiceIndex(defaultSelectorIndexList, true)
            }
        }
    }

}