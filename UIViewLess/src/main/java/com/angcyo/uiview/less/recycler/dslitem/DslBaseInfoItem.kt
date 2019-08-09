package com.angcyo.uiview.less.recycler.dslitem

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import com.angcyo.uiview.less.R
import com.angcyo.uiview.less.kotlin.color
import com.angcyo.uiview.less.kotlin.getDrawable
import com.angcyo.uiview.less.kotlin.inflate
import com.angcyo.uiview.less.kotlin.setLeftIco
import com.angcyo.uiview.less.recycler.RBaseViewHolder
import com.angcyo.uiview.less.recycler.adapter.DslAdapterItem
import com.angcyo.uiview.less.widget.group.RLinearLayout


/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/08/09
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
open class DslBaseInfoItem : DslAdapterItem() {
    init {
        itemLayoutId = R.layout.dsl_info_item
    }

    override var itemBind: (itemHolder: RBaseViewHolder, itemPosition: Int, adapterItem: DslAdapterItem) -> Unit =
        { itemHolder, itemPosition, adapterItem ->
            onItemBind(itemHolder, itemPosition, adapterItem)
        }

    /**背景*/
    var itemBackgroundDrawable: Drawable? = ColorDrawable(Color.WHITE)

    /**条目文本*/
    var itemInfoText: CharSequence? = null

    @DrawableRes
    var itemInfoIcon: Int = -1
    var itemInfoIconColor: Int = -2

    /**扩展布局信息*/
    @LayoutRes
    var itemExtendLayoutId: Int = -1

    var itemClickListener: ((View) -> Unit)? = null

    open fun onItemBind(
        itemHolder: RBaseViewHolder,
        itemPosition: Int,
        adapterItem: DslAdapterItem
    ) {

        (itemHolder.itemView as? RLinearLayout)?.setRBackgroundDrawable(itemBackgroundDrawable)

        //文本信息
        itemHolder.tv(R.id.text_view)?.apply {
            text = itemInfoText

            if (itemInfoIconColor == -2) {
                setLeftIco(itemInfoIcon)
            } else {
                setLeftIco(getDrawable(itemInfoIcon).color(itemInfoIconColor))
            }
        }

        //扩展布局
        if (itemExtendLayoutId > 0) {
            itemHolder.group(R.id.wrap_layout)?.inflate(itemExtendLayoutId)
        } else {
            itemHolder.group(R.id.wrap_layout)?.removeAllViews()
        }

        //事件
        if (itemClickListener == null) {
            itemHolder.itemView.isClickable = false
        } else {
            itemHolder.clickItem {
                itemClickListener?.invoke(it)
            }
        }
    }
}