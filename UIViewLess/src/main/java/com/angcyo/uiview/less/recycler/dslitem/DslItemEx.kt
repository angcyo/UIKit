package com.angcyo.uiview.less.recycler.dslitem

import androidx.annotation.LayoutRes
import com.angcyo.uiview.less.recycler.adapter.DslAdapter
import com.angcyo.uiview.less.recycler.adapter.DslAdapterItem

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/08/09
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
public fun DslAdapter.dslItem(@LayoutRes layoutId: Int, config: DslAdapterItem.() -> Unit = {}) {
    val item = DslAdapterItem()
    item.itemLayoutId = layoutId
    addLastItem(item)
    item.config()
}

public fun <T : DslAdapterItem> DslAdapter.dslCustomItem(
    dslItem: T,
    config: T.() -> Unit = {}
) {
    addLastItem(dslItem)
    dslItem.config()
}

/**简单的单行文本*/
public fun DslAdapter.dslBaseInfoItem(config: DslBaseInfoItem.() -> Unit = {}) {
    dslCustomItem(DslBaseInfoItem(), config)
}

/**单行文本+右边文本*/
public fun DslAdapter.dslTextInfoItem(config: DslTextInfoItem.() -> Unit = {}) {
    dslCustomItem(DslTextInfoItem(), config)
}