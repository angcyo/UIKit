package com.angcyo.uiview.less.base

import android.os.Bundle
import com.angcyo.uiview.less.recycler.DslItemDecoration
import com.angcyo.uiview.less.recycler.HoverItemDecoration
import com.angcyo.uiview.less.recycler.RBaseViewHolder
import com.angcyo.uiview.less.recycler.RRecyclerView
import com.angcyo.uiview.less.recycler.adapter.DslAdapter
import com.angcyo.uiview.less.recycler.adapter.DslAdapterItem
import com.angcyo.uiview.less.recycler.adapter.DslDateFilter
import com.angcyo.uiview.less.recycler.adapter.RBaseAdapter

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/06/11
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */

open class BaseDslRecyclerFragment : BaseRecyclerFragment<DslAdapterItem>() {
    val hoverItemDecoration = HoverItemDecoration()
    val dslItemDslAdapterItem = DslItemDecoration()

    override fun onCreateAdapter(datas: MutableList<DslAdapterItem>?): RBaseAdapter<DslAdapterItem> {
        return DslAdapter(mAttachContext, datas).apply {
            dslDateFilter = DslDateFilter(this)
        }
    }

    override fun onInitBaseView(viewHolder: RBaseViewHolder, arguments: Bundle?, savedInstanceState: Bundle?) {
        super.onInitBaseView(viewHolder, arguments, savedInstanceState)
    }

    override fun initRecyclerView(recyclerView: RRecyclerView?) {
        super.initRecyclerView(recyclerView)

        initDslRecyclerView(recyclerView)
    }

    open fun initDslRecyclerView(recyclerView: RRecyclerView?) {
        recyclerView?.apply {
            addItemDecoration(dslItemDslAdapterItem)
            hoverItemDecoration.attachToRecyclerView(this)
        }
    }

    override fun onBaseLoadData() {
        super.onBaseLoadData()
    }

    override fun onBaseLoadEnd(datas: MutableList<DslAdapterItem>?, pageSize: Int) {
        super.onBaseLoadEnd(datas, pageSize)
    }

    override fun isFirstNeedLoadData(): Boolean {
        return super.isFirstNeedLoadData()
    }

    override fun onUIDelayLoadData() {
        super.onUIDelayLoadData()
    }
}