package com.angcyo.uiview.less.kotlin.dsl

import com.angcyo.uiview.less.recycler.RBaseViewHolder
import com.angcyo.uiview.less.widget.pager.RPagerAdapter

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/05/13
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
open class DslViewPagerAdapter : RPagerAdapter() {

    val pagerItems = mutableListOf<DslPagerItem>()

    override fun getCount(): Int {
        return pagerItems.size
    }

    fun getItemPager(position: Int): DslPagerItem? {
        return pagerItems[position]
    }

    override fun getItemType(position: Int): Int {
        return getItemPager(position)?.pagerItemType ?: 0
    }

    override fun getLayoutId(position: Int, itemType: Int): Int {
        return getItemPager(position)?.pagerItemLayoutId ?: -1
    }

    override fun initItemView(viewHolder: RBaseViewHolder, position: Int, itemType: Int) {
        super.initItemView(viewHolder, position, itemType)
        getItemPager(position)?.pagerItemBind?.invoke(viewHolder, position, itemType)
    }

    override fun onItemDestroy(viewHolder: RBaseViewHolder, position: Int, itemType: Int) {
        super.onItemDestroy(viewHolder, position, itemType)

        getItemPager(position)?.pagerItemBind?.invoke(viewHolder, position, itemType)
    }
}