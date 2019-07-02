package com.angcyo.uiview.less.kotlin.dsl

import androidx.recyclerview.widget.RecyclerView

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/05/08
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
class DslRecyclerScroll {

    var firstItemAdapterPosition = androidx.recyclerview.widget.RecyclerView.NO_POSITION

    var firstItemCompletelyVisibleAdapterPosition = androidx.recyclerview.widget.RecyclerView.NO_POSITION

    /**
     * @see RecyclerView.OnScrollListener.onScrolled
     * */
    var onRecyclerScrolled: (recyclerView: androidx.recyclerview.widget.RecyclerView, dx: Int, dy: Int) -> Unit = { _, _, _ -> }

    /**
     * @see RecyclerView.OnScrollListener.onScrollStateChanged
     * */
    var onRecyclerScrollStateChanged: (recyclerView: androidx.recyclerview.widget.RecyclerView, newState: Int) -> Unit = { _, _ -> }

}