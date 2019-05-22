package com.angcyo.uiview.less.kotlin.dialog

import android.app.Dialog
import android.graphics.drawable.Drawable
import com.angcyo.uiview.less.R
import com.angcyo.uiview.less.kotlin.dslAdapter
import com.angcyo.uiview.less.kotlin.renderItem
import com.angcyo.uiview.less.recycler.RBaseViewHolder
import com.angcyo.uiview.less.utils.UI

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/05/22
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */

open class GridDialogConfig : BaseDialogConfig() {
    override var dialogLayoutId: Int = R.layout.dialog_grid_layout

    var gridSpanCount = 4

    var gridItems = mutableListOf<GridItem>()

    /**
     * 布局资源
     * */
    var gridItemLayoutId = R.layout.item_grid_dialog_layout

    /**
     * 追加item
     * */
    fun appendItem(config: GridItem.() -> Unit) {
        val item = GridItem()
        item.config()

        gridItems.add(item)
    }

    override fun onDialogInit(dialog: Dialog, dialogViewHolder: RBaseViewHolder) {
        super.onDialogInit(dialog, dialogViewHolder)

        dialogViewHolder.click(R.id.cancel_button) {
            dialog.dismiss()
        }

        dialogViewHolder.rv(R.id.recycler_view).dslAdapter(gridSpanCount) {
            gridItems.forEachIndexed { index, gridItem ->
                renderItem {
                    itemLayoutId = gridItemLayoutId

                    itemBind = { itemHolder, itemPosition, adapterItem ->
                        itemHolder.imgV(R.id.image_view)?.apply {

                            //图标
                            if (gridItem.gridItemIconDrawable != null) {
                                setImageDrawable(gridItem.gridItemIconDrawable)
                            } else if (gridItem.gridItemIcon > 0) {
                                setImageResource(gridItem.gridItemIcon)
                            }

                            //图标背景
                            if (gridItem.gridItemBgDrawable != null) {
                                UI.setBackgroundDrawable(this, gridItem.gridItemBgDrawable)
                            } else if (gridItem.gridItemBg > 0) {
                                setBackgroundResource(gridItem.gridItemBg)
                            }
                        }

                        //文本
                        itemHolder.tv(R.id.text_view)?.text = gridItem.gridItemText

                        //事件
                        itemHolder.clickItem {
                            if (gridItem.gridItemListener(dialog, itemPosition)) {

                            } else {
                                dialog.dismiss()
                            }
                        }
                    }
                }
            }
        }
    }
}

class GridItem {
    /*图标资源*/
    var gridItemIcon: Int = -1
    var gridItemIconDrawable: Drawable? = null
    /*背景资源*/
    var gridItemBg: Int = -1
    var gridItemBgDrawable: Drawable? = null

    /*文本*/
    var gridItemText: CharSequence? = null
    /*点击事件, 返回true, 不会自动销毁dialog*/
    var gridItemListener: (dialog: Dialog, itemPosition: Int) -> Boolean = { _, _ ->
        false
    }
}