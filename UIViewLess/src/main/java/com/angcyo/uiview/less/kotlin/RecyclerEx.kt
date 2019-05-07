package com.angcyo.uiview.less.kotlin

import android.support.v7.widget.RecyclerView
import com.angcyo.uiview.less.recycler.adapter.DslAdapter
import com.angcyo.uiview.less.recycler.adapter.DslAdapterItem

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/05/07
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */

public fun RecyclerView.dslAdapter(init: DslAdapter.() -> Unit) {
    val dslAdapter = DslAdapter()
    dslAdapter.init()
    adapter = dslAdapter
}

public fun DslAdapter.renderItem(init: DslAdapterItem.() -> Unit) {
    val adapterItem = DslAdapterItem()
    adapterItem.init()
    addLastItem(adapterItem)
}