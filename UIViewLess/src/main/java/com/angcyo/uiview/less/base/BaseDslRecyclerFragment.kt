package com.angcyo.uiview.less.base

import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.angcyo.uiview.less.kotlin.notifyItemChangedByTag
import com.angcyo.uiview.less.recycler.DslItemDecoration
import com.angcyo.uiview.less.recycler.HoverItemDecoration
import com.angcyo.uiview.less.recycler.RBaseViewHolder
import com.angcyo.uiview.less.recycler.RRecyclerView
import com.angcyo.uiview.less.recycler.adapter.DslAdapter
import com.angcyo.uiview.less.recycler.adapter.DslAdapterItem
import com.angcyo.uiview.less.recycler.adapter.DslDateFilter
import com.angcyo.uiview.less.recycler.adapter.RBaseAdapter
import java.lang.ref.WeakReference

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/06/11
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */

open class BaseDslRecyclerFragment : BaseRecyclerFragment<DslAdapterItem>() {
    companion object {

        /**共享缓存池*/
        var dslRecycledViewPool: WeakReference<RecyclerView.RecycledViewPool>? = null
            get() {
                if (field == null || field?.get() == null) {
                    field = WeakReference(RecyclerView.RecycledViewPool())
                }
                return field
            }
    }

    var hoverItemDecoration = HoverItemDecoration()
    var baseDslItemDecoration = DslItemDecoration()

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
            setRecycledViewPool(dslRecycledViewPool?.get())
            addItemDecoration(baseDslItemDecoration)
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

    /**通过[DslAdapter]渲染界面*/
    open fun renderDslAdapter(render: DslAdapter.() -> Unit) {
        baseDslAdapter?.render()
    }

    /**
     * 通过Tag, 刷新指定Item
     */
    open fun notifyItemChangedByTag(tag: String?) {
        baseDslAdapter?.notifyItemChangedByTag(tag)
    }
}