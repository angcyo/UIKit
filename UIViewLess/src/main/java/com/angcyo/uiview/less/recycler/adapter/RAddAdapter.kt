package com.angcyo.uiview.less.recycler.adapter

import android.util.SparseIntArray
import com.angcyo.uiview.less.R
import com.angcyo.uiview.less.recycler.RBaseViewHolder

/**
 * 支持设置最大图片数量的添加图片适配器
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/06/03
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */

open class RAddAdapter<T> : RImageAdapter<T>() {

    /**是否需要显示添加按钮*/
    var showAddItem = true

    init {
        showDeleteModel = true
        maxItemCount = 9
    }

    override fun registerLayouts(layouts: SparseIntArray) {
        super.registerLayouts(layouts)
        layouts.put(getAddItemLayoutId(), getAddItemLayoutId())
    }

    override fun getItemCount(): Int {
        val itemCount = super.getItemCount()

        if (isStateLayout) {
            return itemCount
        }

        return if (isMax()) {
            maxItemCount
        } else if (showAddItem) {
            itemCount + 1
        } else {
            itemCount
        }
    }

    final override fun getItemType(position: Int, data: T?): Int {
        if (showAddItem && !isMax() && position == itemCount - 1) {
            //最后一个item
            return getAddItemLayoutId()
        }

        return super.getItemType(position, data)
    }

    /**add按钮布局*/
    open fun getAddItemLayoutId(): Int {
        return R.layout.item_adapter_add_image
    }

    override fun onBindView(holder: RBaseViewHolder, position: Int, bean: T?) {
        if (holder.itemViewType == getAddItemLayoutId()) {
            onBindAddItemView(holder, position, bean)
        } else {
            onBindShowItemView(holder, position, bean)
        }
    }

    open fun onBindAddItemView(holder: RBaseViewHolder, position: Int, bean: T?) {

    }
}