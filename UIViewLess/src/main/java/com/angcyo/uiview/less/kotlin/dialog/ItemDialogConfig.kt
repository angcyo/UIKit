package com.angcyo.uiview.less.kotlin.dialog

import android.app.Dialog
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.angcyo.uiview.less.R
import com.angcyo.uiview.less.kotlin.clickIt
import com.angcyo.uiview.less.kotlin.getColor
import com.angcyo.uiview.less.kotlin.getDrawable
import com.angcyo.uiview.less.recycler.RBaseViewHolder
import com.angcyo.uiview.less.widget.RTextView

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/05/11
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */

open class ItemDialogConfig : BaseDialogConfig() {

    /**
     * 需要填充的item数据集合
     * */
    var items = mutableListOf<Any>()

    /**对应的图标*/
    var itemIcons = mutableListOf<Int>()

    /**文本的重力*/
    var itemTextGravity = Gravity.CENTER

    override var dialogLayoutId: Int = R.layout.dialog_items_layout

    /**
     * 填充的item 布局资源
     * */
    var dialogItemLayoutId = R.layout.dialog_item_text_layout

    /**
     * 返回 true, 不会自动调用 dismiss
     * */
    open var onItemClick: (dialog: Dialog, index: Int, item: Any) -> Boolean = { _, _, _ ->
        false
    }

    /**
     * 创建item布局
     * */
    var createDialogItemView: (dialog: Dialog, parent: ViewGroup, inflater: LayoutInflater, index: Int, item: Any) -> View =
        { dialog, parent, inflater, index, item ->
            val view = inflater.inflate(dialogItemLayoutId, parent, false)

            view.findViewById<RTextView>(R.id.item_text_view)?.apply {
                gravity = itemTextGravity

                if (index in 0 until itemIcons.size) {
                    if (gravity and Gravity.HORIZONTAL_GRAVITY_MASK == Gravity.CENTER_HORIZONTAL) {
                        setTextLeftDrawable(itemIcons[index])
                    } else {
                        setLeftIco(itemIcons[index])
                    }
                }

                if (item is CharSequence) {
                    text = item
                }
            }

            view.clickIt {
                if (onItemClick.invoke(dialog, index, item)) {

                } else {
                    dialog.dismiss()
                }
            }

            view
        }

    /**
     * 是否显示底部的取消布局
     * */
    var showBottomCancelLayout = true

    var initBottomCancelLayout: (dialog: Dialog, dialogViewHolder: RBaseViewHolder) -> Unit =
        { dialog, dialogViewHolder ->

            if (showBottomCancelLayout) {
                dialogViewHolder.view(R.id.cancel_layout)?.apply {
                    findViewById<TextView>(R.id.item_text_view).text = "取消"
                    clickIt {
                        dialog.cancel()
                    }
                }
            } else {
                dialogViewHolder.gone(R.id.cancel_layout_line)
                dialogViewHolder.gone(R.id.cancel_layout)
            }

        }

    override fun onDialogInit(dialog: Dialog, dialogViewHolder: RBaseViewHolder) {
        super.onDialogInit(dialog, dialogViewHolder)

        inflateItems(dialog, dialogViewHolder)

        initBottomCancelLayout.invoke(dialog, dialogViewHolder)
    }

    override fun initControlLayout(dialog: Dialog, dialogViewHolder: RBaseViewHolder) {
        super.initControlLayout(dialog, dialogViewHolder)

        dialogViewHolder.enable(R.id.positive_button, items.isNotEmpty())

        //默认item dialog 不显示标题栏上的 确定/取消 按钮
        dialogViewHolder.tv(R.id.positive_button)?.visibility = View.GONE
        dialogViewHolder.tv(R.id.negative_button)?.visibility = View.GONE

        //此时如果标题为空, 隐藏title layout
        dialogViewHolder.visible(R.id.title_layout, dialogTitle != null)
    }

    /**item之间的分割线控制*/
    var showItemDividers = LinearLayout.SHOW_DIVIDER_BEGINNING or LinearLayout.SHOW_DIVIDER_MIDDLE
    var showDividerDrawable = getDrawable(R.drawable.base_shape_line_px)

    /**
     * 填充items
     * */
    open fun inflateItems(dialog: Dialog, dialogViewHolder: RBaseViewHolder) {
        dialogViewHolder.group(R.id.item_wrap_layout).apply {
            (this as? LinearLayout)?.apply {

                //标题栏隐藏时, 去掉顶部的分割线
                showDividers =
                    if (dialogViewHolder.view(R.id.title_layout).visibility == View.GONE)
                        (showItemDividers and LinearLayout.SHOW_DIVIDER_BEGINNING.inv())
                    else showItemDividers

                dividerDrawable = showDividerDrawable
            }

            val layoutInflater = LayoutInflater.from(context)

            for (i in 0 until items.size) {
                addView(createDialogItemView.invoke(dialog, this, layoutInflater, i, items[i]))
            }
        }
    }
}