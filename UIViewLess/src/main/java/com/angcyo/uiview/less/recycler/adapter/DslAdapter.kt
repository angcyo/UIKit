package com.angcyo.uiview.less.recycler.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.angcyo.uiview.less.recycler.RBaseViewHolder
import com.angcyo.uiview.less.recycler.dslitem.DslAdapterStatusItem
import com.angcyo.uiview.less.recycler.dslitem.DslLoadMoreItem
import kotlin.math.min

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/05/07
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
open class DslAdapter : RBaseAdapter<DslAdapterItem> {

    /*为了简单起见, 这里写死套路, 理论上应该用状态器管理的.*/
    var dslAdapterStatusItem = DslAdapterStatusItem()
    var dslLoadMoreItem = DslLoadMoreItem()

    /**包含所有[DslAdapterItem], 包括 [headerItems] [dataItems] [footerItems]的数据源*/
    val adapterItems = mutableListOf<DslAdapterItem>()

    /**底部数据, 用来存放 [DslLoadMoreItem] */
    val footerItems = mutableListOf<DslAdapterItem>()
    /**头部数据*/
    val headerItems = mutableListOf<DslAdapterItem>()
    /**列表数据*/
    val dataItems = mutableListOf<DslAdapterItem>()

    /**数据过滤规则*/
    var dslDateFilter: DslDateFilter? = DslDateFilter(this)
        set(value) {
            field = value
            updateItemDepend()
        }

    /**单/多选助手*/
    val itemSelectorHelper = ItemSelectorHelper(this)

    constructor() : super()

    constructor(dataItems: List<DslAdapterItem>?) : this(null, dataItems)

    constructor(context: Context?, dataItems: List<DslAdapterItem>?) : super(context, dataItems) {
        dataItems?.let {
            this.dataItems.clear()
            this.dataItems.addAll(dataItems)
            _updateAdapterItems()
            updateItemDepend(FilterParams(async = false, just = true))
        }
    }

    init {
        if (dslLoadMoreItem.itemEnableLoadMore) {
            setLoadMoreEnable(true)
        }
    }

    //<editor-fold desc="生命周期方法">

    var _recyclerView: RecyclerView? = null

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        _recyclerView = recyclerView
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        _recyclerView = null
    }

    override fun getItemType(position: Int): Int {
        return if (isAdapterStatus()) {
            dslAdapterStatusItem.itemLayoutId
        } else {
            getItemData(position)?.itemLayoutId ?: 0
        }
    }

    override fun getItemLayoutId(viewType: Int): Int = viewType

    override fun getAllDatas(): MutableList<DslAdapterItem> {
        return adapterItems
    }

    override fun getItemCount(): Int {
        return if (isAdapterStatus()) {
            1
        } else {
            //兼容旧的加载更多
            getValidFilterDataList().size + if (mEnableLoadMore) 1 else 0
        }
    }

    //兼容[RBaseAdapter]
    override fun onBindView(holder: RBaseViewHolder, position: Int, bean: DslAdapterItem?) {
        val dslItem = getAdapterItem(position)
        dslItem.itemDslAdapter = this
        dslItem.itemBind.invoke(holder, position, dslItem)
    }

    override fun onViewAttachedToWindow(holder: RBaseViewHolder) {
        super.onViewAttachedToWindow(holder)
        if (isAdapterStatus()) {
            dslAdapterStatusItem.onItemViewAttachedToWindow.invoke(holder)
        } else {
            if (holder.adapterPosition in getValidFilterDataList().indices) {
                getAdapterItem(holder.adapterPosition).onItemViewAttachedToWindow.invoke(holder)
            }
        }
    }

    override fun onViewDetachedFromWindow(holder: RBaseViewHolder) {
        super.onViewDetachedFromWindow(holder)
        if (isAdapterStatus()) {
            dslAdapterStatusItem.onItemViewDetachedToWindow.invoke(holder)
        } else {
            if (holder.adapterPosition in getValidFilterDataList().indices) {
                getAdapterItem(holder.adapterPosition).onItemViewDetachedToWindow.invoke(holder)
            }
        }
    }

    //</editor-fold desc="生命周期方法">

    //<editor-fold desc="辅助方法">

    /**
     * 适配器当前是情感图状态
     * */
    fun isAdapterStatus(): Boolean {
        return !dslAdapterStatusItem.isNoStatus() || isStateLayout
    }

    fun getAdapterItem(position: Int): DslAdapterItem {
        return if (isAdapterStatus()) {
            dslAdapterStatusItem
        } else {
            getItemData(position)!!
        }
    }

    fun _updateAdapterItems() {
        //整理数据
        adapterItems.clear()
        adapterItems.addAll(headerItems)
        adapterItems.addAll(dataItems)
        adapterItems.addAll(footerItems)
    }

    //</editor-fold desc="辅助方法">

    //<editor-fold desc="操作方法">

    /**设置[Adapter]需要显示情感图的状态*/
    fun setAdapterStatus(status: Int) {
        if (dslAdapterStatusItem.itemState == status) {
            return
        }
        dslAdapterStatusItem.itemState = status
        notifyDataSetChanged()
    }

    fun setLoadMoreEnable(enable: Boolean = true) {
        if (dslLoadMoreItem.itemEnableLoadMore == enable &&
            getValidFilterDataList().indexOf(dslLoadMoreItem) != -1
        ) {
            return
        }
        dslLoadMoreItem.itemEnableLoadMore = enable

        changeFooterItems {
            if (enable) {
                it.add(dslLoadMoreItem)
            } else {
                it.remove(dslLoadMoreItem)
            }
        }
    }

    fun setLoadMore(status: Int) {
        if (dslLoadMoreItem.itemState == status) {
            return
        }
        dslLoadMoreItem.itemState = status
        if (dslLoadMoreItem.itemEnableLoadMore) {
            notifyItemChanged(dslLoadMoreItem)
        }
    }

    /**
     * 在最后的位置插入数据
     */
    override fun addLastItem(bean: DslAdapterItem) {
        insertItem(-1, bean)
    }

    fun addLastItem(list: List<DslAdapterItem>) {
        insertItem(-1, list)
    }

    //修正index
    fun _validIndex(list: List<*>, index: Int): Int {
        return if (index < 0) {
            list.size
        } else {
            min(index, list.size)
        }
    }

    /**插入数据列表*/
    fun insertItem(index: Int, bean: List<DslAdapterItem>) {
        dataItems.addAll(_validIndex(dataItems, index), bean)
        _updateAdapterItems()
        updateItemDepend()
    }

    /**插入数据列表*/
    override fun insertItem(index: Int, bean: DslAdapterItem) {
        dataItems.add(_validIndex(dataItems, index), bean)
        _updateAdapterItems()
        updateItemDepend()
    }

    /**重置数据列表*/
    fun resetItem(list: List<DslAdapterItem>) {
        dataItems.clear()
        dataItems.addAll(list)
        _updateAdapterItems()
        updateItemDepend()
    }

    /**清理数据列表, 但不刷新界面*/
    fun clearItems() {
        dataItems.clear()
        _updateAdapterItems()
    }

    /**可以在回调中改变数据, 并且会自动刷新界面*/
    fun changeItems(change: () -> Unit) {
        change()
        _updateAdapterItems()
        updateItemDepend()
    }

    fun changeDataItems(change: (dataItems: MutableList<DslAdapterItem>) -> Unit) {
        changeItems {
            change(dataItems)
        }
    }

    fun changeHeaderItems(change: (headerItems: MutableList<DslAdapterItem>) -> Unit) {
        changeItems {
            change(headerItems)
        }
    }

    fun changeFooterItems(change: (footerItems: MutableList<DslAdapterItem>) -> Unit) {
        changeItems {
            change(footerItems)
        }
    }

    /**
     * 刷新某一个item
     */
    override fun notifyItemChanged(item: DslAdapterItem?) {
        notifyItemChanged(item, true)
    }

    /**支持过滤数据源*/
    fun notifyItemChanged(item: DslAdapterItem?, useFilterList: Boolean = true) {
        if (item == null) {
            return
        }
        val indexOf = getDataList(useFilterList).indexOf(item)

        if (indexOf > -1) {
            notifyItemChanged(indexOf)
        }
    }

    override fun getItemData(position: Int): DslAdapterItem? {
        val list = getDataList(true)
        return if (position in list.indices) {
            list[position]
        } else {
            null
        }
    }

    /**获取数据列表*/
    fun getDataList(useFilterList: Boolean = true): List<DslAdapterItem> {
        return if (useFilterList) getValidFilterDataList() else adapterItems
    }

    /**调用[DiffUtil]更新界面*/
    fun updateItemDepend(
        filterParams: FilterParams = FilterParams(
            just = dataItems.isEmpty(),
            async = getDataList().isNotEmpty()
        )
    ) {
        dslDateFilter?.let {
            it.updateFilterItemDepend(filterParams.apply {
                justFilter = isAdapterStatus()
            })
        }
    }

    /**获取有效过滤后的数据集合*/
    fun getValidFilterDataList(): List<DslAdapterItem> {
        return dslDateFilter?.filterDataList ?: adapterItems
    }

    //</editor-fold desc="操作方法">

    //<editor-fold desc="兼容的操作">

    override fun getAllDataCount(): Int {
        return itemCount
    }

    override fun appendData(datas: MutableList<DslAdapterItem>?) {
        val list: List<DslAdapterItem> = datas ?: emptyList()
        if (list.isNotEmpty()) {
            dataItems.addAll(_validIndex(dataItems, -1), list)
            _updateAdapterItems()
            updateItemDepend(FilterParams(async = false, just = true))
        }
    }

    override fun resetData(datas: MutableList<DslAdapterItem>?) {
        val list: List<DslAdapterItem> = datas ?: emptyList()
        dataItems.clear()
        dataItems.addAll(list)
        _updateAdapterItems()
        updateItemDepend(FilterParams(async = false, just = true))
    }

    override fun addFirstItem(bean: DslAdapterItem) {
        dataItems.add(_validIndex(dataItems, 0), bean)
        _updateAdapterItems()
        updateItemDepend(FilterParams(async = false, just = true))
    }

    override fun setNoMore(refresh: Boolean) {
        super.setNoMore(refresh)
        //由于diff, 只会更新布局item, 所以这里兼容一下, 强制更新最后一项.
        updateLoadMoreView()
    }
    //</editor-fold desc="兼容的操作">

    //<editor-fold desc="不支持的操作">

    override fun addLastItemSafe(bean: DslAdapterItem?) {
        //no op
    }

    //</editor-fold desc="不支持的操作">

}