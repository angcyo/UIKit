package com.angcyo.uiview.less.recycler

import android.graphics.Canvas
import android.graphics.Paint
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
class DslItemDecoration(
    val init: (that: DslItemDecoration) -> Unit = { _ -> },
    val onDrawOver: (that: DslItemDecoration, canvas: Canvas, parent: RecyclerView, state: RecyclerView.State, paint: Paint) -> Unit = { _, _, _, _, _ -> },
    val onDraw: (that: DslItemDecoration, canvas: Canvas, parent: RecyclerView, state: RecyclerView.State, paint: Paint) -> Unit = { _, _, _, _, _ -> },
    val getItemOffsets: (that: DslItemDecoration, outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) -> Unit = { _, _, _, _, _ -> }
) : RecyclerView.ItemDecoration() {

    val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val tempDrawRect = Rect()

    init {
        init.invoke(this)
    }

    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        onDrawOver.invoke(this, canvas, parent, state, paint)
    }

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        onDraw.invoke(this, canvas, parent, state, paint)
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        getItemOffsets.invoke(this, outRect, view, parent, state)
    }

    fun eachChildViewHolder(
        parent: RecyclerView,
        callback: (
            beforeViewHolder: RecyclerView.ViewHolder?,
            viewHolder: RecyclerView.ViewHolder,
            afterViewHolder: RecyclerView.ViewHolder?
        ) -> Unit
    ) {

        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val childViewHolder = parent.findContainingViewHolder(child)

            childViewHolder?.let {

                //前一个child
                var beforeViewHolder: RecyclerView.ViewHolder? = null
                //后一个child
                var afterViewHolder: RecyclerView.ViewHolder? = null

                if (i >= 1) {
                    beforeViewHolder = parent.findContainingViewHolder(parent.getChildAt(i - 1))
                }
                if (i < childCount - 1) {
                    afterViewHolder = parent.findContainingViewHolder(parent.getChildAt(i + 1))
                }

                callback.invoke(beforeViewHolder, it, afterViewHolder)
            }
        }
    }

    fun eachChildRViewHolder(
        parent: RecyclerView,
        callback: (
            beforeViewHolder: RBaseViewHolder?,
            viewHolder: RBaseViewHolder,
            afterViewHolder: RBaseViewHolder?
        ) -> Unit
    ) {

        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val childViewHolder = parent.findContainingViewHolder(child)

            childViewHolder?.let {

                //前一个child
                var beforeViewHolder: RBaseViewHolder? = null
                //后一个child
                var afterViewHolder: RBaseViewHolder? = null

                if (i >= 1) {
                    beforeViewHolder = parent.findContainingViewHolder(parent.getChildAt(i - 1)) as RBaseViewHolder?
                }
                if (i < childCount - 1) {
                    afterViewHolder = parent.findContainingViewHolder(parent.getChildAt(i + 1)) as RBaseViewHolder?
                }

                callback.invoke(beforeViewHolder, it as RBaseViewHolder, afterViewHolder)
            }
        }
    }
}