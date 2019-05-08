package com.angcyo.uiview.less.kotlin

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.angcyo.uiview.less.kotlin.dsl.DslRecyclerScroll
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

public fun RecyclerView.onScroll(init: DslRecyclerScroll.() -> Unit) {
    val dslRecyclerView = DslRecyclerScroll()
    dslRecyclerView.init()
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager
            if (layoutManager is LinearLayoutManager) {
                dslRecyclerView.firstItemAdapterPosition = layoutManager.findFirstVisibleItemPosition()
                dslRecyclerView.firstItemCompletelyVisibleAdapterPosition =
                    layoutManager.findFirstCompletelyVisibleItemPosition()
            } else {

            }

            dslRecyclerView.onRecyclerScrolled.invoke(recyclerView, dx, dy)
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            dslRecyclerView.onRecyclerScrollStateChanged.invoke(recyclerView, newState)
        }
    })
}