package com.angcyo.uiview.less.draw

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.text.TextUtils
import android.widget.TextView
import com.angcyo.uiview.less.R
import com.angcyo.uiview.less.kotlin.*
import kotlin.math.max

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/06/24
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
class TextDraw(val textView: TextView) {

    /**是否激活文本背景绘制*/
    var drawTextBg = false

    /**当文本为空时, 是否绘制一个点*/
    var drawDotBgOnTextEmpty = false
    /**一个点时的绘制半径*/
    var drawDotBgOnTextEmptyRadius = 2 * dp
    /**圆角矩形时, 绘制的圆角大小*/
    var drawTextBgRound = 6 * dp

    /**padding大小*/
    var drawTextBgPadding = 1 * dp

    var drawTextBgOffsetX = 0 * dp
    var drawTextBgOffsetY = 0 * dp

    var drawTextBgColor = if (textView.isInEditMode) "#FC3C38".toColor() else getColor(R.color.base_red)

    val paint: Paint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG)
    }

    val textRectF: RectF by lazy {
        RectF()
    }

    fun onDraw(canvas: Canvas) {
        if (drawTextBg) {
            val text = textView.text
            val compoundDrawables = textView.compoundDrawables
            var leftOffset = 0
            var topOffset = 0

            compoundDrawables[0]?.let {
                leftOffset = it.intrinsicWidth + textView.compoundDrawablePadding
            }
            compoundDrawables[1]?.let {
                topOffset = it.intrinsicWidth + textView.compoundDrawablePadding
            }

            paint.color = drawTextBgColor
            paint.textSize = textView.textSize

            val cx: Float
            val cy: Float
            val cr: Float

            val textWidth = paint.textWidth("$text")
            val textHeight = paint.textHeight()

            //文本绘制中心点坐标
            cx = when {
                textView.isGravityLeft() || textView.isGravityCenterVertical() -> textView.paddingLeft + textWidth / 2
                else -> textView.drawCenterX().toFloat()
            }
            cy = when {
                textView.isGravityTop() || textView.isGravityCenterHorizontal() -> textView.paddingTop + textHeight / 2
                else -> textView.drawCenterY().toFloat()
            }

            if (TextUtils.isEmpty(text)) {
                if (drawDotBgOnTextEmpty) {
                    //小红点
                    cr = drawDotBgOnTextEmptyRadius
                    canvas.drawCircle(cx + leftOffset, cy + topOffset, cr, paint)
                }
            } else {
                cr = max(textWidth, textHeight) / 2
                if (text.length <= 2) {
                    //圆
                    canvas.drawCircle(
                        cx + drawTextBgOffsetX + leftOffset,
                        cy + drawTextBgOffsetY + topOffset,
                        cr + drawTextBgPadding,
                        paint
                    )
                } else {
                    textRectF.set(
                        cx - textWidth / 2 - drawTextBgPadding,
                        cy - textHeight / 2 - drawTextBgPadding,
                        cx + textWidth / 2 + drawTextBgPadding,
                        cy + textHeight / 2 + drawTextBgPadding
                    )
                    textRectF.offset(drawTextBgOffsetX + leftOffset, drawTextBgOffsetY + topOffset)
                    //圆角矩形
                    canvas.drawRoundRect(textRectF, drawTextBgRound, drawTextBgRound, paint)
                }
            }
        }
    }
}