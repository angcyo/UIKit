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

    constructor() : super()
    constructor(context: Context?) : super(context)
    constructor(context: Context?, datas: MutableList<DslAdapterItem>?) : super(context, datas)

    override fun getItemData(position: Int): DslAdapterItem {
        return super.getItemData(position)
    }

    override fun getItemCount(): Int {
        return super.getItemCount()
    }

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

}