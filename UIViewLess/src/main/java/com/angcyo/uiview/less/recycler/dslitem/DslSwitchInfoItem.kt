package com.angcyo.uiview.less.recycler.dslitem

import com.angcyo.github.SwitchButton
import com.angcyo.uiview.less.R
import com.angcyo.uiview.less.recycler.RBaseViewHolder
import com.angcyo.uiview.less.recycler.adapter.DslAdapterItem

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/08/09
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
open class DslSwitchInfoItem : DslBaseInfoItem() {
    init {
        itemExtendLayoutId = R.layout.dsl_extent_switch_item
    }

    /**是否选中*/
    var itemSwitchChecked = false

    /**状态回调*/
    var onItemSwitchChanged: (checked: Boolean) -> Unit = {

    }

    override fun onItemBind(
        itemHolder: RBaseViewHolder,
        itemPosition: Int,
        adapterItem: DslAdapterItem
    ) {
        super.onItemBind(itemHolder, itemPosition, adapterItem)

        itemHolder.v<SwitchButton>(R.id.switch_view)?.apply {

            setOnCheckedChangeListener { _, isChecked ->
                val old = itemSwitchChecked
                itemSwitchChecked = isChecked
                if (old != itemSwitchChecked) {
                    onItemSwitchChanged(itemSwitchChecked)
                }
            }

            //刷新界面的时候, 不执行动画
            val old = isEnableEffect
            isEnableEffect = false
            isChecked = itemSwitchChecked
            isEnableEffect = old
        }
    }
}