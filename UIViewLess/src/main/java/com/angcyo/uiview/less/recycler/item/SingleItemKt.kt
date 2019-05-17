package com.angcyo.uiview.less.recycler.item

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.text.TextPaint
import android.view.View
import android.view.ViewGroup
import com.angcyo.uiview.less.recycler.RBaseViewHolder

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/05/17
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
class SingleItemKt : SingleItem() {

    var singleItemLayoutId: Int = super.getItemLayoutId()

    var singleItemBind: (holder: RBaseViewHolder, posInData: Int, singItem: SingleItemKt) -> Unit = { _, _, _ ->

    }

    /**
     * 需要插入分割线的大小
     * */
    var itemTopInsert = 0
    var itemLeftInsert = 0
    var itemRightInsert = 0
    var itemBottomInsert = 0

    var itemDecorationColor = Color.WHITE

    /**
     * 仅绘制offset的区域
     * */
    var onlyDrawOffsetArea = true

    /**
     * 分割线绘制时的偏移
     * */
    var itemTopOffset = 0
    var itemLeftOffset = 0
    var itemRightOffset = 0
    var itemBottomOffset = 0

    override fun setItemOffsets2(rect: Rect, edge: Int) {
        //super.setItemOffsets2(rect, edge)
        rect.set(itemLeftInsert, itemTopInsert, itemRightInsert, itemBottomInsert)
    }

    override fun draw(
        canvas: Canvas,
        paint: TextPaint,
        itemView: View,
        offsetRect: Rect,
        itemCount: Int,
        position: Int
    ) {
        //super.draw(canvas, paint, itemView, offsetRect, itemCount, position)

        paint.color = itemDecorationColor
        if (itemTopInsert > 0) {
            if (onlyDrawOffsetArea) {
                //绘制左右区域
                if (itemLeftOffset > 0) {
                    mDrawRect.set(itemView.left, itemView.top - offsetRect.top, itemLeftOffset, itemView.top)
                    canvas.drawRect(mDrawRect, paint)
                }
                if (itemRightOffset > 0) {
                    mDrawRect.set(
                        itemView.right - itemRightOffset,
                        itemView.top - offsetRect.top,
                        itemView.right,
                        itemView.top
                    )
                    canvas.drawRect(mDrawRect, paint)
                }
            } else {
                mDrawRect.set(itemView.left, itemView.top - offsetRect.top, itemView.right, itemView.top)
                canvas.drawRect(mDrawRect, paint)
            }
        }
        if (itemBottomInsert > 0) {
            if (onlyDrawOffsetArea) {
                //绘制左右区域
                if (itemLeftOffset > 0) {
                    mDrawRect.set(itemView.left, itemView.bottom, itemLeftOffset, itemView.bottom + offsetRect.bottom)
                    canvas.drawRect(mDrawRect, paint)
                }
                if (itemRightOffset > 0) {
                    mDrawRect.set(
                        itemView.right - itemRightOffset,
                        itemView.bottom,
                        itemView.right,
                        itemView.bottom + offsetRect.bottom
                    )
                    canvas.drawRect(mDrawRect, paint)
                }
            } else {
                mDrawRect.set(itemView.left, itemView.bottom, itemView.right, itemView.bottom + offsetRect.bottom)
                canvas.drawRect(mDrawRect, paint)
            }
        }
        if (itemLeftInsert > 0) {
            if (onlyDrawOffsetArea) {
                //绘制上下区域
                if (itemTopOffset > 0) {
                    mDrawRect.set(itemView.left - offsetRect.left, itemView.top, itemView.left, itemTopOffset)
                    canvas.drawRect(mDrawRect, paint)
                }
                if (itemBottomOffset < 0) {
                    mDrawRect.set(
                        itemView.left - offsetRect.left,
                        itemView.bottom - itemBottomOffset,
                        itemView.left,
                        itemView.bottom
                    )
                    canvas.drawRect(mDrawRect, paint)
                }
            } else {
                mDrawRect.set(itemView.left - offsetRect.left, itemView.top, itemView.left, itemView.bottom)
                canvas.drawRect(mDrawRect, paint)
            }
        }
        if (itemRightInsert > 0) {
            if (onlyDrawOffsetArea) {
                //绘制上下区域
                if (itemTopOffset > 0) {
                    mDrawRect.set(itemView.right, itemView.top, itemView.right + offsetRect.right, itemTopOffset)
                    canvas.drawRect(mDrawRect, paint)
                }
                if (itemBottomOffset < 0) {
                    mDrawRect.set(
                        itemView.right,
                        itemView.bottom - itemBottomOffset,
                        itemView.right + offsetRect.right,
                        itemView.bottom
                    )
                    canvas.drawRect(mDrawRect, paint)
                }
            } else {
                mDrawRect.set(itemView.right, itemView.top, itemView.right + offsetRect.right, itemView.bottom)
                canvas.drawRect(mDrawRect, paint)
            }
        }

    }

    override fun getItemLayoutId(): Int {
        return singleItemLayoutId
    }

    override fun createItemView(parent: ViewGroup, viewType: Int): View? {
        return super.createItemView(parent, viewType)
    }

    override fun onBindView(holder: RBaseViewHolder, posInData: Int, itemDataBean: Item?) {
        super.onBindView(holder, posInData, itemDataBean)
        singleItemBind(holder, posInData, this)
    }
}