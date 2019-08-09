package com.angcyo.uiview.less.recycler.dslitem

import androidx.annotation.DrawableRes
import com.angcyo.uiview.less.R
import com.angcyo.uiview.less.draw.view.RDrawNoReadNumView
import com.angcyo.uiview.less.kotlin.color
import com.angcyo.uiview.less.kotlin.setRightIco
import com.angcyo.uiview.less.recycler.RBaseViewHolder
import com.angcyo.uiview.less.recycler.adapter.DslAdapterItem

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/08/09
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
open class DslTextInfoItem : DslBaseInfoItem() {
    init {
        itemExtendLayoutId = R.layout.dsl_extent_text_item
    }

    /**显示未读小红点*/
    var itemShowNoRead: Boolean = false

    /**描述文本*/
    var itemDarkText: CharSequence? = null

    @DrawableRes
    var itemDarkIcon: Int = -1
    var itemDarkIconColor: Int = -2

    /**未读数*/
    var itemNoReadNumString: String? = null

    override fun onItemBind(
        itemHolder: RBaseViewHolder,
        itemPosition: Int,
        adapterItem: DslAdapterItem
    ) {
        super.onItemBind(itemHolder, itemPosition, adapterItem)

        //文本
        itemHolder.rtv(R.id.dark_view)?.apply {
            text = itemDarkText

            if (itemDarkIconColor == -2) {
                setRightIco(itemDarkIcon)
            } else {
                setRightIco(getDrawable(itemDarkIcon).color(itemDarkIconColor))
            }

            setShowNoRead(itemShowNoRead)
        }

        //未读数
        itemHolder.v<RDrawNoReadNumView>(R.id.read_num_view)?.getDrawReadNum()?.readNumString =
            itemNoReadNumString
    }
}