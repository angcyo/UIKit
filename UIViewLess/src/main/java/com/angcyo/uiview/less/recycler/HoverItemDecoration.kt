package com.angcyo.uiview.less.recycler

import android.graphics.*
import android.support.v7.widget.RecyclerView
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import com.angcyo.lib.L
import com.angcyo.uiview.less.kotlin.dp

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/05/08
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */

open class HoverItemDecoration : RecyclerView.ItemDecoration() {
    internal var recyclerView: RecyclerView? = null
    internal var hoverCallback: HoverCallback? = null
    internal var isDownInHoverItem = false

    internal val paint: Paint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG)
    }

    private val itemTouchListener = object : RecyclerView.SimpleOnItemTouchListener() {
        override fun onInterceptTouchEvent(recyclerView: RecyclerView, event: MotionEvent): Boolean {
            val action = event.actionMasked
            if (action == MotionEvent.ACTION_DOWN) {
                isDownInHoverItem = overDecorationRect.contains(event.x.toInt(), event.y.toInt())
            }

            var result = false
            if (isDownInHoverItem) {
                overViewHolder?.apply {
                    val textView: TextView = itemView as TextView

                    textView.dispatchTouchEvent(event)
                    result = textView.onTouchEvent(event)
                }
            }

            return result
        }

        override fun onTouchEvent(recyclerView: RecyclerView, event: MotionEvent) {
            if (isDownInHoverItem) {
                overViewHolder?.apply {
                    itemView.onTouchEvent(event)
                }
            }
        }
    }

    /**
     * 调用此方法, 安装悬浮分割线
     * */
    fun attachToRecyclerView(recyclerView: RecyclerView?, init: HoverCallback.() -> Unit) {
        hoverCallback = HoverCallback()
        hoverCallback?.init()

        if (this.recyclerView !== recyclerView) {
            if (this.recyclerView != null) {
                this.destroyCallbacks()
            }

            this.recyclerView = recyclerView
            if (recyclerView != null) {
                this.setupCallbacks()
            }
        }
    }

    private fun setupCallbacks() {
        this.recyclerView?.apply {
            addItemDecoration(this@HoverItemDecoration)
            addOnItemTouchListener(itemTouchListener)
        }
    }

    private fun destroyCallbacks() {
        this.recyclerView?.apply {
            removeItemDecoration(this@HoverItemDecoration)
            removeOnItemTouchListener(itemTouchListener)
        }
    }

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        //L.i("onDraw..1 $state")
//        childItems(parent) {
//
//        }
    }

    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        //L.i("onDraw..2 $state")
//        childItems(parent) { viewHolder ->
//            if (viewHolder.adapterPosition != RecyclerView.NO_POSITION) {
//                parent.adapter?.let {
//                    hoverCallback?.drawOverDecoration?.invoke(canvas, parent, it, viewHolder)
//                }
//            }
//        }

//        hoverCallback?.let { hoverCallback ->
//            childViewHolder(parent, 0)?.let { viewHolder ->
//                if (viewHolder.adapterPosition != RecyclerView.NO_POSITION) {
//
//                    //相同类型的分割线只绘制1个
//
//                    //不同类型的分割线, 上推效果
//
//                    parent.adapter?.let {
//                        //如果需要分割线
//                        if (hoverCallback.decorationOverLayoutType.invoke(viewHolder) > 0) {
//                            //回调, 绘制分割线的方法
//                            hoverCallback.drawOverDecoration.invoke(canvas, parent, it, viewHolder)
//                        }
//                    }
//                }
//            }
//        }

        checkOverDecoration(parent)

        overViewHolder?.let {
            if (!overDecorationRect.isEmpty) {
                hoverCallback?.drawOverDecoration?.invoke(canvas, paint, it, overDecorationRect)
            }
        }
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        L.i("onDraw..0 $state")
    }

    private fun childViewHolder(parent: RecyclerView, childIndex: Int): RecyclerView.ViewHolder? {
        if (parent.childCount > childIndex) {
            return parent.findContainingViewHolder(parent.getChildAt(childIndex))
        }
        return null
    }

    private fun childItems(parent: RecyclerView, op: (viewHolder: RecyclerView.ViewHolder, childIndex: Int) -> Unit) {
        for (i in 0 until parent.childCount) {
            //val params: RecyclerView.LayoutParams = childView.layoutParams as RecyclerView.LayoutParams
            //viewHolder?.adapterPosition
            //viewHolder?.layoutPosition

            childViewHolder(parent, i)?.let {
                op.invoke(it, i)
            }
        }
    }

    /**当前悬浮的分割线, 如果有.*/
    internal var overViewHolder: RecyclerView.ViewHolder? = null
//    /**接下来的分割线, 如果有*/
//    internal var nextViewHolder: RecyclerView.ViewHolder? = null

    /**当前悬浮分割线的坐标.*/
    internal val overDecorationRect = Rect()
    /**下一个悬浮分割线的坐标.*/
    internal val nextDecorationRect = Rect()

    /**
     * 核心方法, 用来实时监测界面上 需要浮动的 分割线.
     * */
    internal fun checkOverDecoration(parent: RecyclerView) {
        overDecorationRect.clear()
        nextDecorationRect.clear()
        overViewHolder = null

        childViewHolder(parent, 0)?.let { viewHolder ->
            val firstChildAdapterPosition = viewHolder.adapterPosition

            if (firstChildAdapterPosition != RecyclerView.NO_POSITION) {

                parent.adapter?.let { adapter ->
                    hoverCallback?.let { callback ->
                        if (callback.haveOverDecoration.invoke(firstChildAdapterPosition)) {

                            val overStartPosition = findOverStartPosition(adapter, firstChildAdapterPosition)

                            //第一个位置的child 需要分割线
                            overViewHolder =
                                callback.createDecorationOverView.invoke(
                                    parent,
                                    adapter,
                                    overStartPosition
                                )

                            val overView = overViewHolder!!.itemView

                            if (overStartPosition == firstChildAdapterPosition) {
                                //第一个child, 正好是 分割线的开始位置
                                if (viewHolder.itemView.top < 0) {
                                    // 此时才需要悬停
                                    overDecorationRect.set(overView.left, overView.top, overView.right, overView.bottom)
                                }
                            } else {
                                overDecorationRect.set(overView.left, overView.top, overView.right, overView.bottom)
                            }

                            val nextViewHolder = childViewHolder(parent, 1)
                            if (nextViewHolder == null) {
                                //已经到了最后一个item了
                            } else {
                                //下一个也有分割线, 监测是否需要上推

                                if (callback.isOverDecorationSame.invoke(
                                        adapter,
                                        firstChildAdapterPosition,
                                        nextViewHolder.adapterPosition
                                    )
                                ) {
                                    //
                                } else {
                                    //不同的分割线, 实现上推效果
                                    if (nextViewHolder.itemView.top < overDecorationRect.height()) {
                                        overDecorationRect.offsetTo(
                                            0,
                                            nextViewHolder.itemView.top - overDecorationRect.height()
                                        )
                                    }
                                }
                            }

                            //overDecorationRect.set(overViewHolder.itemView)

                        } else {
                            //第一个位置的child 不需要需要分割线
                        }
                    }
                }
            }
        }

//        childItems(parent) { viewHolder, childIndex ->
//            if (childIndex == 0) {
//                hoverCallback?.let {
//                    if (it.haveOverDecoration.invoke(viewHolder)) {
//                        //第一个位置的child 需要分割线
//                        if (viewHolder.itemView.top < 0) {
//                            //这个时候, 才绘制悬浮分割线
//
//                        } else {
//
//                        }
//                    } else {
//                        //第一个位置的child 不需要需要分割线
//                    }
//                }
//            }
//        }
    }

    /**
     * 查找指定位置的分割线, 最开始的adapterPosition
     * */
    internal fun findOverStartPosition(
        adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>,
        adapterPosition: Int
    ): Int {
        var result = adapterPosition
        for (i in adapterPosition - 1 downTo 0) {
            if (i == 0) {
                result = i
                break
            } else if (!hoverCallback!!.isOverDecorationSame(adapter, adapterPosition, i)) {
                result = i + 1
                break
            }
        }
        return result
    }

    class HoverCallback {

        /**
         * 当前的 位置 是否有 悬浮分割线
         * */
        var haveOverDecoration: (adapterPosition: Int) -> Boolean =
            { adapterPosition -> decorationOverLayoutType.invoke(adapterPosition) > 0 }

        /**
         * 根据 位置, 返回对应分割线的布局类型, 小于0, 不绘制
         *
         * @see RecyclerView.Adapter.getItemViewType
         * */
        var decorationOverLayoutType: (adapterPosition: Int) -> Int =
            { _ -> -1 }

        /**
         * 判断2个分割线是否相同, 不同的分割线, 才会悬停, 相同的分割线只会绘制一条.
         * */
        var isOverDecorationSame: (
            adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>,
            nowAdapterPosition: Int, nextAdapterPosition: Int
        ) -> Boolean =
            { adapter, nowAdapterPosition, nextAdapterPosition ->
                adapter.getItemViewType(nowAdapterPosition) == adapter.getItemViewType(
                    nextAdapterPosition
                )
            }

        /**
         * 创建 分割线 视图
         * */
        var createDecorationOverView: (
            recyclerView: RecyclerView,
            adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>,
            overAdapterPosition: Int
        ) -> RecyclerView.ViewHolder = { recyclerView, adapter, overAdapterPosition ->

            //拿到分割线对应的itemType
            val layoutType = decorationOverLayoutType.invoke(overAdapterPosition)

            //复用adapter的机制, 创建View
            val holder = adapter.createViewHolder(recyclerView, layoutType)

            //注意这里的position
            adapter.bindViewHolder(holder, overAdapterPosition)

            //测量view
            holder.itemView.apply {
                //                layoutParams = RecyclerView.LayoutParams(viewHolder.itemView.layoutParams)
                val params = layoutParams

                val widthSize: Int
                val widthMode: Int
                when (params.width) {
                    -1 -> {
                        widthSize = recyclerView.measuredWidth
                        widthMode = View.MeasureSpec.EXACTLY
                    }
                    else -> {
                        widthSize = recyclerView.measuredWidth
                        widthMode = View.MeasureSpec.AT_MOST
                    }
                }

                val heightSize: Int
                val heightMode: Int
                when (params.height) {
                    -1 -> {
                        heightSize = recyclerView.measuredWidth
                        heightMode = View.MeasureSpec.EXACTLY
                    }
                    else -> {
                        heightSize = recyclerView.measuredWidth
                        heightMode = View.MeasureSpec.AT_MOST
                    }
                }

                //标准方法1
                measure(
                    View.MeasureSpec.makeMeasureSpec(widthSize, widthMode),
                    View.MeasureSpec.makeMeasureSpec(heightSize, heightMode)
                )
                //标准方法2
                layout(0, 0, measuredWidth, measuredHeight)

                //标准方法3
                //draw(canvas)
            }

            holder
        }

        /**
         * 绘制分割线
         * */
        var drawOverDecoration: (
            canvas: Canvas,
            paint: Paint,
            viewHolder: RecyclerView.ViewHolder,
            overRect: Rect
        ) -> Unit =
            { canvas, paint, viewHolder, overRect ->

                canvas.save()
                canvas.translate(overRect.left.toFloat(), overRect.top.toFloat())
                viewHolder.itemView.draw(canvas)

                if (overRect.top == 0) {
                    //分割线完全显示的情况下, 才绘制阴影
                    val shadowTop = overRect.bottom.toFloat()
                    val shadowHeight = 4 * dp

                    paint.shader = LinearGradient(
                        0f, shadowTop, 0f,
                        shadowTop + shadowHeight,
                        intArrayOf(
                            Color.parseColor("#40000000"),
                            Color.TRANSPARENT /*Color.parseColor("#40000000")*/
                        ),
                        null, Shader.TileMode.CLAMP
                    )

                    //绘制阴影
                    canvas.drawRect(
                        overRect.left.toFloat(),
                        shadowTop,
                        overRect.right.toFloat(),
                        shadowTop + shadowHeight,
                        paint
                    )
                }

                canvas.restore()

            }
    }
}

fun Rect.clear() {
    set(0, 0, 0, 0)
}