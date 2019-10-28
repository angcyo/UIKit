package com.angcyo.uiview.less.recycler.dslitem

import com.angcyo.lib.L
import com.angcyo.uiview.less.R
import com.angcyo.uiview.less.base.BaseUI
import com.angcyo.uiview.less.kotlin.setWidthHeight
import com.angcyo.uiview.less.recycler.RBaseViewHolder
import com.angcyo.uiview.less.recycler.adapter.DslAdapterItem

/**
 * [DslAdapter] 中, 控制情感图显示状态的 [Item]
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/08/09
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
open class DslAdapterStatusItem : BaseDslStateItem() {

    init {
        BaseUI.uiDslAdapterStatus.initStateLayoutMap(this, itemStateLayoutMap)
        itemState = ADAPTER_STATUS_NONE
        itemSpanCount = -1
    }

    companion object {
        /**正常状态, 切换到内容*/
        const val ADAPTER_STATUS_NONE = -1
        /**空数据*/
        const val ADAPTER_STATUS_EMPTY = 0
        /**加载中*/
        const val ADAPTER_STATUS_LOADING = 1
        /**错误*/
        const val ADAPTER_STATUS_ERROR = 2
    }

    /**刷新回调*/
    var onRefresh: (RBaseViewHolder) -> Unit = {
        L.i("[DslAdapterStatusItem] 触发刷新")
    }

    override fun onItemBind(
        itemHolder: RBaseViewHolder,
        itemPosition: Int,
        adapterItem: DslAdapterItem
    ) {
        itemHolder.itemView.setWidthHeight(-1, -1)
        super.onItemBind(itemHolder, itemPosition, adapterItem)
    }

    override fun _onBindStateLayout(itemHolder: RBaseViewHolder, state: Int) {
        super._onBindStateLayout(itemHolder, state)
        if (itemState == ADAPTER_STATUS_ERROR) {
            //出现错误后, 触击刷新
            itemHolder.clickItem {
                if (itemState == ADAPTER_STATUS_ERROR) {
                    _notifyRefresh(itemHolder)
                    itemDslAdapter?.setAdapterStatus(ADAPTER_STATUS_LOADING)
                }
            }
            itemHolder.click(R.id.base_retry_button) {
                itemHolder.clickView(itemHolder.itemView)
            }
        } else if (itemState == ADAPTER_STATUS_LOADING) {
            _notifyRefresh(itemHolder)
        } else {
            itemHolder.itemView.isClickable = false
        }
    }

    /**返回[true] 表示不需要显示情感图, 即显示[Adapter]原本的内容*/
    open fun isInAdapterStatus() = itemState != ADAPTER_STATUS_NONE

    open fun _notifyRefresh(itemHolder: RBaseViewHolder) {
        if (!_isRefresh) {
            _isRefresh = true
            itemHolder.post { onRefresh(itemHolder) }
        }
    }

    //是否已经在刷新
    var _isRefresh = false

    override fun _onItemStateChange(old: Int, value: Int) {
        if (old != value && value != ADAPTER_STATUS_LOADING) {
            _isRefresh = false
        }
        super._onItemStateChange(old, value)
    }
}