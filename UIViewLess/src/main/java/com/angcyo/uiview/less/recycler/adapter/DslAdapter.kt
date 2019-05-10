package com.angcyo.uiview.less.recycler.adapter

import android.content.Context
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

    constructor() : super()
    constructor(context: Context?) : super(context)
    constructor(context: Context?, datas: MutableList<DslAdapterItem>?) : super(context, datas)

    /**
     * 没有过滤过的数据集合
     * */
    override fun getAllDatas(): MutableList<DslAdapterItem> {
        return super.getAllDatas()
    }

    /**
     * 过滤后的数据集合
     * */
    fun getFilterDataList(): MutableList<DslAdapterItem> = dslDateFilter!!.filterDataList

    override fun getItemLayoutId(viewType: Int): Int {
        return viewType
    }

    override fun getItemType(position: Int): Int {
        return getItemData(position).itemLayoutId
    }

    override fun onBindView(holder: RBaseViewHolder, position: Int, bean: DslAdapterItem?) {
        bean?.let {
            it.itemBind.invoke(holder, position, it)
        }
    }

    override fun getItemCount(): Int {
        if (dslDateFilter != null) {
            return getFilterDataList().size
        }
        return super.getItemCount()
    }

    override fun getItemData(position: Int): DslAdapterItem {
        if (dslDateFilter != null) {
            getFilterDataList()[position]
        }
        return super.getItemData(position)
    }

    override fun appendData(datas: MutableList<DslAdapterItem>) {
        super.appendData(datas)
    }

    override fun resetData(datas: MutableList<DslAdapterItem>) {
        super.resetData(datas)
    }

    override fun addLastItem(bean: DslAdapterItem?) {
        super.addLastItem(bean)
    }

    override fun addLastItemSafe(bean: DslAdapterItem?) {
        super.addLastItemSafe(bean)
    }

    /**
     * 折叠这个分组
     * */
    fun foldItem(item: DslAdapterItem, folder: Boolean = true) {
        dslDateFilter?.filterItem(item, folder)
    }
}