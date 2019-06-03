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

open class RAddAdapter<T> : RBaseAdapter<T>() {

    /**是否需要显示添加按钮*/
    var showAddItem = true

    /**是否显示删除按钮*/
    var showDeleteModel = true

    /**允许最大添加的数量*/
    var maxItemCount = 9

    init {

    }

    override fun registerLayouts(layouts: SparseIntArray) {
        super.registerLayouts(layouts)
        layouts.put(getItemLayoutId(), getItemLayoutId())
        layouts.put(getAddItemLayoutId(), getAddItemLayoutId())
    }

    override fun getItemCount(): Int {
        val itemCount = super.getItemCount()

        if (isStateLayout) {
            return itemCount
        }

        return if (isMax()) {
            maxItemCount
        } else {
            itemCount + 1
        }
    }

    /**是否已达最大*/
    open fun isMax(): Boolean {
        val size = allDatas.size
        return size >= maxItemCount
    }

    final override fun getItemType(position: Int, data: T?): Int {
        if (showAddItem && !isMax() && position == itemCount - 1) {
            //最后一个item
            return getAddItemLayoutId()
        }

        val size = allDatas.size
        if (position in 0 until size) {
            return getItemLayoutId()
        }
        return getAddItemLayoutId()
    }

    /**正常布局*/
    open fun getItemLayoutId(): Int {
        return R.layout.item_adapter_show_image
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

    open fun onBindShowItemView(holder: RBaseViewHolder, position: Int, bean: T?) {
        holder.visible(R.id.delete_image_view, showDeleteModel)
        holder.click(R.id.delete_image_view) {
            deleteItem(bean)
        }
    }

    override fun onDeleteItem(position: Int): Boolean {
        return super.onDeleteItem(position)
    }
}