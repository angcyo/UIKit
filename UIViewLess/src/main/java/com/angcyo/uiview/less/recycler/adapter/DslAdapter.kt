package com.angcyo.uiview.less.recycler.adapter

import android.content.Context
import android.view.View
import com.angcyo.uiview.less.kotlin.findViewHolder
import com.angcyo.uiview.less.recycler.RBaseViewHolder

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/05/07
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
open class DslAdapter : RBaseAdapter<DslAdapterItem> {

    var dslDateFilter: DslDateFilter? = null
        set(value) {
            field = value
            updateFilterDataList()
        }

    /**
     * 缓存过滤后的数据源, 防止每次都计算
     * */
    val filterDataList = mutableListOf<DslAdapterItem>()

    constructor() : super()
    constructor(context: Context?) : super(context)
    constructor(context: Context?, datas: MutableList<DslAdapterItem>?) : super(context, datas) {
        updateFilterDataList()
    }

    /**
     * 没有过滤过的数据集合
     * */
    override fun getAllDatas(): MutableList<DslAdapterItem> {
        return super.getAllDatas()
    }

    /**获取有效过滤后的数据集合*/
    fun getValidFilterDataList(): MutableList<DslAdapterItem> {
        return if (dslDateFilter == null && filterDataList.isEmpty()) {
            allDatas
        } else {
            filterDataList
        }
    }

    /**
     * 过滤后的数据集合
     * */
    fun updateFilterDataList() {
        filterDataList.clear()
        dslDateFilter?.let {
            filterDataList.addAll(it.filterDataList)
        }
    }

    /**
     * 布局的type, 就是布局对应的 layout id
     * */
    override fun getItemLayoutId(viewType: Int): Int {
        return viewType
    }

    override fun getItemType(position: Int): Int {
        return getItemData(position)?.itemLayoutId ?: -1
    }

    override fun onBindView(holder: RBaseViewHolder, position: Int, bean: DslAdapterItem?) {
        getItemData(position)?.let {
            it.dslAdapter = this
            it.itemBind.invoke(holder, position, it)
        }
    }

    override fun getItemCount(): Int {
        if (dslDateFilter != null) {
            return filterDataList.size
        }
        return super.getItemCount()
    }

    override fun getItemData(position: Int): DslAdapterItem? {
        if (dslDateFilter != null) {
            if (position in 0 until filterDataList.size) {
                return filterDataList[position]
            }
            return null
        }
        return super.getItemData(position)
    }

    override fun appendData(datas: MutableList<DslAdapterItem>) {
        super.appendData(datas)
        updateFilterDataList()
    }

    override fun resetData(datas: MutableList<DslAdapterItem>) {
        super.resetData(datas)
        updateFilterDataList()
    }

    override fun addLastItem(bean: DslAdapterItem?) {
        super.addLastItem(bean)
        updateFilterDataList()
    }

    override fun addLastItemSafe(bean: DslAdapterItem?) {
        super.addLastItemSafe(bean)
        updateFilterDataList()
    }

    /**
     * 折叠这个分组
     * */
    fun foldItem(item: DslAdapterItem, folder: Boolean = true) {
        dslDateFilter?.filterItem(item, folder)
    }

    /**支持过滤数据源*/
    fun notifyItemChanged(item: DslAdapterItem?, useFilterList: Boolean = false) {
        if (item == null) {
            return
        }
        val indexOf = getDataList(useFilterList).indexOf(item)

        if (indexOf > -1) {
            notifyItemChanged(indexOf)
        }
    }

    /**获取数据列表*/
    fun getDataList(useFilterList: Boolean): MutableList<DslAdapterItem> {
        return if (useFilterList) getValidFilterDataList() else allDatas
    }

    override fun onViewAttachedToWindow(holder: RBaseViewHolder) {
        super.onViewAttachedToWindow(holder)
        if (holder.adapterPosition in 0 until itemCount) {
            getItemData(holder.adapterPosition)?.onItemViewAttachedToWindow?.invoke(holder)
        }
    }

    override fun onViewDetachedFromWindow(holder: RBaseViewHolder) {
        super.onViewDetachedFromWindow(holder)
        if (holder.adapterPosition in 0 until itemCount) {
            getItemData(holder.adapterPosition)?.onItemViewDetachedToWindow?.invoke(holder)
        }
    }

    override fun onChildViewAttachedToWindow(view: View, adapterPosition: Int, layoutPosition: Int) {
        super.onChildViewAttachedToWindow(view, adapterPosition, layoutPosition)
        if (adapterPosition in 0 until itemCount) {
            recyclerView?.findViewHolder(adapterPosition)?.let {
                getItemData(adapterPosition)?.onItemChildViewAttachedToWindow?.invoke(it, adapterPosition)
            }
        }
    }

    override fun onChildViewDetachedFromWindow(view: View, adapterPosition: Int, layoutPosition: Int) {
        super.onChildViewDetachedFromWindow(view, adapterPosition, layoutPosition)
        if (adapterPosition in 0 until itemCount) {
            recyclerView?.findViewHolder(adapterPosition)?.let {
                getItemData(adapterPosition)?.onItemChildViewDetachedFromWindow?.invoke(it, adapterPosition)
            }
        }
    }
}