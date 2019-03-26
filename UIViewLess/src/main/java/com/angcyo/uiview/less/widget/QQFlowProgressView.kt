package com.angcyo.uiview.less.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.angcyo.uiview.less.R
import com.angcyo.uiview.less.kotlin.debugPaint
import com.angcyo.uiview.less.kotlin.density
import com.angcyo.uiview.less.kotlin.valueAnimator
import com.angcyo.uiview.less.kotlin.viewDrawWith
import com.angcyo.uiview.less.utils.RUtils

/**
 * 模仿QQ安全验证, 进度条
 * Created by angcyo on 2018/03/30 21:46
 */
class QQFlowProgressView(context: Context, attributeSet: AttributeSet? = null) : View(context, attributeSet) {
    private val drawRect = RectF()
    var roundRadius = 0f
    private var minDrawWidth = 0f
    var drawStep: Int = (0.6f * density).toInt()

    init {
        roundRadius = 2f * density

        debugPaint.color = Color.WHITE
        debugPaint.style = Paint.Style.FILL

        val array = context.obtainStyledAttributes(attributeSet, R.styleable.QQFlowProgressView)
        drawStep = array.getDimensionPixelOffset(R.styleable.QQFlowProgressView_r_flow_step, drawStep)
        roundRadius = array.getDimensionPixelOffset(
            R.styleable.QQFlowProgressView_r_flow_radius,
            roundRadius.toInt()
        ).toFloat()

        array.recycle()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (isInEditMode) {
            drawWidth = (w * 0.5).toFloat()
        } else {
            drawWidth = 0f
        }

        minDrawWidth = (Math.min(h, roundRadius.toInt() * 2) / 2).toFloat()
    }

    private var isDrawEnd = false
    private var drawWidth = 0f
        set(value) {
            if (field > viewDrawWith) {
                isDrawEnd = true
            }
            if (field <= 0 + minDrawWidth) {
                isDrawEnd = false
            }
            field = RUtils.clamp(value, 0f + minDrawWidth, viewDrawWith + minDrawWidth)
        }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.save()
        if (isDrawEnd) {
            drawRect.set(
                measuredWidth - drawWidth + paddingLeft, paddingTop.toFloat(),
                measuredWidth.toFloat() - paddingRight, measuredHeight.toFloat() - paddingBottom
            )
        } else {
            drawRect.set(
                0f + paddingLeft, paddingTop.toFloat(),
                drawWidth - paddingRight, measuredHeight.toFloat() - paddingBottom
            )
        }
        canvas.drawRoundRect(drawRect, roundRadius, roundRadius, debugPaint)
        canvas.restore()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        valueAnimator.apply {
            removeAllUpdateListeners()
            addUpdateListener {
                if (isDrawEnd) {
                    drawWidth -= drawStep
                } else {
                    drawWidth += drawStep
                }
                postInvalidateOnAnimation()
            }
            start()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        valueAnimator.cancel()
    }
}