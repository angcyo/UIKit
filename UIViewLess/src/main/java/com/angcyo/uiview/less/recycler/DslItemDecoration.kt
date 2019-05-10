package com.angcyo.uiview.less.recycler

import android.graphics.Canvas
import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/05/10
 * Copyright (canvas) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
open class DslItemDecoration : RecyclerView.ItemDecoration() {
    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
    }

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
    }

    fun eachChildViewHolder(parent: RecyclerView) {
        for (i in 0 until parent.childCount) {

        }
    }
} 