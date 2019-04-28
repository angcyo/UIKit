package com.angcyo.uiview.less.draw

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import com.angcyo.uiview.less.R
import com.angcyo.uiview.less.kotlin.dp
import com.angcyo.uiview.less.kotlin.textHeight
import com.angcyo.uiview.less.kotlin.textWidth

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/04/27
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
class RDrawNoReadNum(view: View) : BaseDraw(view) {
    var textSize = 9f
    var textColor = Color.WHITE
    var backgroundColor = Color.parseColor("#FF3622")

    var drawBorder = true

    var borderColor = Color.WHITE

    var borderWidth = 1f

    /**
     * 当字符串太长时, 是否保持为圆形
     * */
    var keepCircle = false

    /**
     * 需要绘制的字符串数字, 为null时, 啥都不绘制, 为空时, 只绘制背景和边框
     * */
    var readNumString: String? = null

    /**
     * 圆角的大小
     * */
    var roundSize = 20f

    /**
     * 间隙大小
     * */
    var spaceSize = 2f

    override fun initAttribute(attr: AttributeSet?) {
        val typedArray = obtainStyledAttributes(attr, R.styleable.RDrawNoReadNum)
        textSize = typedArray.getDimensionPixelOffset(
            R.styleable.RDrawNoReadNum_r_draw_text_size,
            (textSize * dp).toInt()
        ).toFloat()
        borderWidth = typedArray.getDimensionPixelOffset(
            R.styleable.RDrawNoReadNum_r_draw_border_width,
            (borderWidth * dp).toInt()
        ).toFloat()
        roundSize = typedArray.getDimensionPixelOffset(
            R.styleable.RDrawNoReadNum_r_draw_round_size,
            (roundSize * dp).toInt()
        ).toFloat()
        spaceSize = typedArray.getDimensionPixelOffset(
            R.styleable.RDrawNoReadNum_r_draw_space_size,
            (spaceSize * dp).toInt()
        ).toFloat()

        textColor = typedArray.getColor(R.styleable.RDrawNoReadNum_r_draw_text_color, textColor)
        backgroundColor = typedArray.getColor(R.styleable.RDrawNoReadNum_r_draw_background_color, backgroundColor)
        borderColor = typedArray.getColor(R.styleable.RDrawNoReadNum_r_draw_border_color, borderColor)

        drawBorder = typedArray.getBoolean(R.styleable.RDrawNoReadNum_r_draw_border, drawBorder)
        keepCircle = typedArray.getBoolean(R.styleable.RDrawNoReadNum_r_draw_keep_circle, keepCircle)
        readNumString = typedArray.getString(R.styleable.RDrawNoReadNum_r_draw_read_num_string)

        typedArray.recycle()
        mBasePaint.textSize = textSize
    }

    override fun measureDraw(widthMeasureSpec: Int, heightMeasureSpec: Int): IntArray {
        var widthSize = View.MeasureSpec.getSize(widthMeasureSpec)
        val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)
        var heightSize = View.MeasureSpec.getSize(heightMeasureSpec)
        val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)

        val textWidth = mBasePaint.textWidth(readNumString ?: "")
        val textHeight = mBasePaint.textHeight()

        if (widthMode != View.MeasureSpec.EXACTLY) {
            //wrap_content unspecified
            //widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(measureDrawWidth(widthSize, widthMode), View.MeasureSpec.EXACTLY);
            widthSize = textWidth.toInt() + paddingLeft + paddingRight
        } else {
        }

        if (heightMode != View.MeasureSpec.EXACTLY) {
            //wrap_content unspecified
            //heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(measureDrawHeight(heightSize, heightMode), View.MeasureSpec.EXACTLY);
            heightSize = textHeight.toInt() + paddingTop + paddingBottom
        } else {
        }

        widthSize = (widthSize + borderWidth + spaceSize * 2).toInt()
        heightSize = (heightSize + borderWidth + spaceSize).toInt()

        //永远保证, 最小时的状态的圆
        if (widthSize < heightSize) {
            widthSize = heightSize
        }

        //强制显示为圆
        if (keepCircle) {
            if (heightSize < widthSize) {
                heightSize = widthSize
            }
        }

        measureTemp[0] = widthSize
        measureTemp[1] = heightSize
        return measureTemp
    }

    val drawPath: Path by lazy {
        Path()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        drawPath.reset()
        if (w == h) {
            drawPath.addCircle(
                drawCenterX().toFloat(),
                drawCenterY().toFloat(),
                Math.min(viewDrawWidth, viewDrawHeight) / 2 - borderWidth / 2,
                Path.Direction.CCW
            )
        } else {
            mDrawRectF.inset(borderWidth / 2, borderWidth / 2)
            drawPath.addRoundRect(mDrawRectF, roundSize, roundSize, Path.Direction.CCW)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        readNumString?.let {

            //绘制背景
            mBasePaint.style = Paint.Style.FILL
            mBasePaint.color = backgroundColor
            canvas.drawPath(drawPath, mBasePaint)

            if (drawBorder) {
                //绘制边框
                mBasePaint.style = Paint.Style.STROKE
                mBasePaint.strokeWidth = borderWidth
                mBasePaint.color = borderColor
                canvas.drawPath(drawPath, mBasePaint)
            }

            //绘制文本
            if (it.isNotBlank()) {
                mBasePaint.style = Paint.Style.FILL
                mBasePaint.strokeWidth = 0f
                mBasePaint.color = textColor
                val cx = (paddingLeft + viewDrawWidth / 2).toFloat()
                val cy = (paddingTop + viewDrawHeight / 2).toFloat()

                canvas.drawText(
                    it,
                    cx - mBasePaint.textWidth(it) / 2,
                    cy + mBasePaint.textHeight() / 2 - mBasePaint.descent(),
                    mBasePaint
                )
            }
        }
    }

}