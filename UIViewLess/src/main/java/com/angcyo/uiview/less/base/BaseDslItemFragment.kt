package com.angcyo.uiview.less.base

import com.angcyo.uiview.less.recycler.item.SingleItem
import com.angcyo.uiview.less.recycler.item.SingleItemKt


/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/05/17
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
@Deprecated("使用[BaseDslRecyclerFragment]")
abstract class BaseDslItemFragment : BaseItemFragment() {

    open fun dslCreateItem(items: ArrayList<SingleItem> = singleItems, init: SingleItemKt.() -> Unit) {
        val item = SingleItemKt()
        item.init()
        items.add(item)
    }
}