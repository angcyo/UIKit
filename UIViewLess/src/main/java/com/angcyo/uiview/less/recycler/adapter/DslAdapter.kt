package com.angcyo.uiview.less.recycler.adapter

import android.content.Context
import android.text.TextUtils
import android.view.View
import com.angcyo.lib.L
import com.angcyo.uiview.less.kotlin.findViewHolder
import com.angcyo.uiview.less.recycler.RBaseViewHolder
import com.angcyo.uiview.less.recycler.adapter.RModelAdapter.*

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

    override fun getAllDataCount(): Int {
        return getValidFilterDataList().size
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
            it.itemDslAdapter = this
            it.itemBind.invoke(holder, position, it)
        }
    }

    override fun getItemCount(): Int {
        if (isStateLayout) {
            return 1
        }
        if (dslDateFilter != null) {
            return filterDataList.size + if (isEnableLoadMore) 1 else 0
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

    override fun notifyItemChanged(item: DslAdapterItem?) {
        if (dslDateFilter == null) {
            super.notifyItemChanged(item)
        } else {
            notifyItemChanged(item, filterDataList.size != allDatas.size)
        }
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

    /**支持过滤数据源*/
    fun deleteAdapterItem(item: DslAdapterItem?, useFilterList: Boolean = true) {
        if (item == null) {
            return
        }

        val dataList = getDataList(useFilterList)

        val indexOf = dataList.indexOf(item)

        val size = itemCount
        if (indexOf != -1 && size > indexOf) {
            if (onDeleteItem(indexOf)) {

                dataList.removeAt(indexOf)

                if (dataList != allDatas) {
                    //非全部数据源时, 总数据源的数据也要删除
                    allDatas.remove(item)
                }

                notifyItemRemoved(indexOf)
                notifyItemRangeChanged(indexOf, size - indexOf)

                onDeleteItemEnd(indexOf)
            }
        }
    }

    override fun deleteItem(position: Int) {
        super.deleteItem(position)
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

    override fun onChildViewAttachedToWindow(
        view: View,
        adapterPosition: Int,
        layoutPosition: Int
    ) {
        super.onChildViewAttachedToWindow(view, adapterPosition, layoutPosition)
        if (adapterPosition in 0 until itemCount) {
            recyclerView?.findViewHolder(adapterPosition)?.let {
                getItemData(adapterPosition)?.onItemChildViewAttachedToWindow?.invoke(
                    it,
                    adapterPosition
                )
            }
        }
    }

    override fun onChildViewDetachedFromWindow(
        view: View,
        adapterPosition: Int,
        layoutPosition: Int
    ) {
        super.onChildViewDetachedFromWindow(view, adapterPosition, layoutPosition)
        if (adapterPosition in 0 until itemCount) {
            recyclerView?.findViewHolder(adapterPosition)?.let {
                getItemData(adapterPosition)?.onItemChildViewDetachedFromWindow?.invoke(
                    it,
                    adapterPosition
                )
            }
        }
    }

    /**查找相邻相同类型的[item]*/
    fun findItemGroup(
        item: DslAdapterItem,
        callback: (items: MutableList<DslAdapterItem>, index: Int /*在分组当中的位置*/) -> Unit = { _, _ -> }
    ) {
        val groupItems = mutableListOf<DslAdapterItem>()
        var prevClassName: String? = null

        //目标的位置
        var targetIndex = -1

        val dataList = getDataList(true)
        for (index in 0 until dataList.size) {
            val dslAdapterItem = dataList[index]

            if (prevClassName == null) {
                prevClassName = dslAdapterItem.javaClass.simpleName
            }

            if (TextUtils.equals(dslAdapterItem.javaClass.simpleName, prevClassName)) {
                //相同类型
                if (item == dslAdapterItem) {
                    targetIndex = index
                }
            } else {
                if (targetIndex != -1) {
                    //找到了目标
                    break
                }
                if (item == dslAdapterItem) {
                    targetIndex = index
                }
                groupItems.clear()
            }
            groupItems.add(dslAdapterItem)

            prevClassName = dslAdapterItem.javaClass.simpleName
        }

        if (targetIndex != -1) {
            targetIndex = groupItems.indexOf(item)
        }

        callback.invoke(groupItems, targetIndex)
    }

    //<editor-fold desc="单选, 多选相关">

    @RModelAdapter.Model
    var selectorModel = MODEL_NORMAL

    /**最小选中数量*/
    var selectorMinLimit = 1

    private val selectorModelListeners = mutableSetOf<SelectModelListener>()

    fun addOnSelectorModelListener(listener: SelectModelListener) {
        selectorModelListeners.add(listener)
    }

    fun removeOnSelectorModelListener(listener: SelectModelListener) {
        selectorModelListeners.remove(listener)
    }

    /**更新选中状态*/
    fun updateSelector(dslAdapterItem: DslAdapterItem, select: Boolean) {
        if (dslAdapterItem.itemIsSelect == select) {
            return
        }

        if (selectorModel == MODEL_SINGLE || selectorModel == MODEL_MULTI) {

            val thisList = mutableListOf(dslAdapterItem)
            val selectItemList = getSelectItemList(true, thisList)

            val fromItem = selectItemList.firstOrNull()
            val toItem = dslAdapterItem

            if (select) {
                if (toItem.isItemCanSelect(toItem.itemIsSelectInner, true)) {
                    toItem.itemIsSelectInner = true
                } else {
                    return
                }

                if (selectorModel == MODEL_SINGLE) {
                    //单选, 互斥操作

                    getSelectItemList(false, thisList).forEach {
                        //取消所有选中状态
                        it.itemIsSelectInner = false
                    }

                    //先通知事件
                    selectorModelListeners.forEach {
                        it.onSingleSelectChange(fromItem, toItem)

                        it.onSelectChange(
                            toItem,
                            thisList,
                            mutableListOf(dslAdapterItem.itemIndexPosition)
                        )
                    }

                    //再更新UI
                    fromItem?.updateAdapterItem(true)
                } else {
                    //多选

                    selectItemList.add(toItem)

                    val indexList = mutableListOf<Int>()
                    selectItemList.forEach {
                        indexList.add(it.itemIndexPosition)
                    }

                    selectorModelListeners.forEach {
                        it.onSelectChange(
                            toItem,
                            selectItemList,
                            indexList
                        )
                    }
                }

                toItem.updateAdapterItem(true)
            } else {
                //取消选择
                if (selectorModel == MODEL_SINGLE && toItem.itemIsSelect) {
                    selectItemList.add(toItem)
                }

                if (selectItemList.size > selectorMinLimit) {

                    if (toItem.isItemCanSelect(toItem.itemIsSelectInner, false)) {
                        toItem.itemIsSelectInner = false
                    } else {
                        return
                    }

                    if (selectorModel == MODEL_SINGLE) {
                        selectorModelListeners.forEach {
                            it.onSingleSelectChange(toItem, toItem)

                            it.onSelectChange(
                                toItem,
                                thisList,
                                mutableListOf(dslAdapterItem.itemIndexPosition)
                            )
                        }
                    } else {
                        val indexList = mutableListOf<Int>()
                        selectItemList.forEach {
                            indexList.add(it.itemIndexPosition)
                        }

                        selectorModelListeners.forEach {
                            it.onSelectChange(
                                toItem,
                                selectItemList,
                                indexList
                            )
                        }
                    }

                    toItem.updateAdapterItem(true)
                } else {
                    selectorModelListeners.forEach {
                        it.onSelectMinLimitNotice(selectorMinLimit)
                    }
                }
            }
        } else {
            L.w("当前选择模式$selectorModel 不支持操作.")
        }
    }

    /**主动调用, 通知事件*/
    fun notifySelectListener(fromItem: DslAdapterItem? = null) {
        if (selectorModel == MODEL_SINGLE || selectorModel == MODEL_MULTI) {

            val selectItemList = getSelectItemList(true)

            val indexList = mutableListOf<Int>()
            selectItemList.forEach {
                indexList.add(it.itemIndexPosition)
            }

            selectorModelListeners.forEach {
                it.onSingleSelectChange(null, selectItemList.firstOrNull())

                it.onSelectChange(
                    fromItem,
                    selectItemList,
                    indexList
                )
            }

        } else {
            L.w("当前选择模式$selectorModel 不支持操作.")
        }
    }

    /**获取所有选中的[DslAdapterItem]*/
    fun getSelectItemList(
        useFilterList: Boolean = true,
        excludeItemList: List<DslAdapterItem> = listOf()
    ): MutableList<DslAdapterItem> {
        val result = mutableListOf<DslAdapterItem>()
        getDataList(useFilterList).filterTo(result) {
            if (excludeItemList.contains(it)) {
                false
            } else {
                it.itemIsSelect
            }
        }
        return result
    }

    //</editor-fold desc="单选, 多选相关">

}