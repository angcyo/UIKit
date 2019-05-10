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

    /**
     * 将3个方法, 合一调用. 通过参数, 来区分是那一个方法.
     *
     * outRect 不为空时, 是 getItemOffsets 方法
     * canvas 不为空时, 是 onDrawOver onDraw
     * isOverDraw 控制是否是 onDrawOver
     * */
    var eachItemDoIt: (
        canvas: Canvas?, parent: RecyclerView, state: RecyclerView.State, outRect: Rect?,
        beforeViewHolder: RecyclerView.ViewHolder?,
        viewHolder: RecyclerView.ViewHolder,
        afterViewHolder: RecyclerView.ViewHolder?,
        isOverDraw: Boolean
    ) -> Unit =
        { _, _, _, _, _, _, _, _ -> }

    init {
        init.invoke(this)
    }

    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        onDrawOver.invoke(this, canvas, parent, state, paint)
        eachChildRViewHolder(parent) { beforeViewHolder, viewHolder, afterViewHolder ->
            eachItemDoIt.invoke(canvas, parent, state, null, beforeViewHolder, viewHolder, afterViewHolder, true)
        }
    }

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        onDraw.invoke(this, canvas, parent, state, paint)
        eachChildRViewHolder(parent) { beforeViewHolder, viewHolder, afterViewHolder ->
            eachItemDoIt.invoke(canvas, parent, state, null, beforeViewHolder, viewHolder, afterViewHolder, false)
        }
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        getItemOffsets.invoke(this, outRect, view, parent, state)
        eachChildRViewHolder(parent, view) { beforeViewHolder, viewHolder, afterViewHolder ->
            eachItemDoIt.invoke(null, parent, state, outRect, beforeViewHolder, viewHolder, afterViewHolder, false)
        }
    }

    fun eachChildViewHolder(
        parent: RecyclerView,
        targetView: View? = null,/*指定目标, 则只回调目标前后的ViewHolder*/
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

                if (targetView != null) {
                    if (targetView == child) {
                        callback.invoke(beforeViewHolder, it as RBaseViewHolder, afterViewHolder)
                        return
                    }
                } else {
                    callback.invoke(beforeViewHolder, it as RBaseViewHolder, afterViewHolder)
                }
            }
        }
    }

    fun eachChildRViewHolder(
        parent: RecyclerView,
        targetView: View? = null,/*指定目标, 则只回调目标前后的ViewHolder*/
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

                if (targetView != null) {
                    if (targetView == child) {
                        callback.invoke(beforeViewHolder, it as RBaseViewHolder, afterViewHolder)
                        return
                    }
                } else {
                    callback.invoke(beforeViewHolder, it as RBaseViewHolder, afterViewHolder)
                }
            }
        }
    }
}