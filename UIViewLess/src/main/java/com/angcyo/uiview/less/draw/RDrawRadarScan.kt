package com.angcyo.uiview.less.draw

import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.angcyo.uiview.less.R
import com.angcyo.uiview.less.kotlin.dp
import com.angcyo.uiview.less.skin.SkinHelper

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/05/28
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
class RDrawRadarScan(view: View) : BaseDraw(view) {

    /**田字格线之间的距离*/
    var lineSpace = 10 * dp

    /**绘制背景线*/
    var drawBgLine = false
    var drawBgColor = false
    var backgroundColor = Color.WHITE

    var drawScanStartLine = true

    var enableDraw = false
    var enableAnim = false

    private val lineCirclePath: Path by lazy {
        Path()
    }
    private val drawLinePath: Path by lazy {
        Path()
    }
    private val linePaint: Paint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG)
    }

    val paint: Paint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG)
    }

    var circleWidth = 2 * dp

    var circleRadiusOffset = -circleWidth / 2

    var scanDrawRectF = RectF()

    val scanMatrix = Matrix()

    var sweepAngle = 0f
        set(value) {
            field = value % 360
            scanMatrix.reset()
            scanMatrix.postRotate(field, (centerX).toFloat(), (centerY).toFloat())
            postInvalidate()
        }

    val centerX: Int
        get() = viewDrawWidth / 2 + paddingLeft
    val centerY: Int
        get() = viewDrawHeight / 2 + paddingTop

    var bgLineWidth = 1f

    var radarShaderEndColor: Int = 0
    var radarShaderStartColor: Int = 0
    var radarLineColor: Int = 0

    init {
        if (isInEditMode) {
            paint.color = SkinHelper.getTranColor(getColor(R.color.colorPrimary), 0x40)
            radarLineColor = getColor(R.color.colorPrimary)
            radarShaderStartColor = SkinHelper.getTranColor(getColor(R.color.colorPrimary), 0x40)
            radarShaderEndColor = SkinHelper.getTranColor(getColor(R.color.colorPrimary), 0xFF)
        } else {
            paint.color = SkinHelper.getTranColor(SkinHelper.getSkin().themeSubColor, 0x40)
            radarLineColor = SkinHelper.getSkin().themeSubColor
            radarShaderStartColor = SkinHelper.getTranColor(SkinHelper.getSkin().themeSubColor, 0x40)
            radarShaderEndColor = SkinHelper.getTranColor(
                SkinHelper.getSkin().themeSubColor,
                0xAA
            ) //SkinHelper.getSkin().themeSubColor// SkinHelper.getTranColor(SkinHelper.getSkin().themeSubColor, 0xFF)
        }
    }

    override fun initAttribute(attr: AttributeSet?) {

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        //有效的绘制直径
        val size = Math.min(viewDrawWidth, viewDrawHeight)

        val circleRadius = size / 2f + circleRadiusOffset

        if (drawBgColor) {
            linePaint.style = Paint.Style.FILL
            linePaint.color = backgroundColor
            canvas.drawCircle(centerX.toFloat(), centerY.toFloat(), circleRadius, linePaint)
        }

        if (!enableDraw) {
            return
        }

        //绘制田字格
        if (drawBgLine) {
            canvas.save()
            lineCirclePath.reset()
            lineCirclePath.addCircle(centerX.toFloat(), centerY.toFloat(), circleRadius, Path.Direction.CW)
            canvas.clipPath(lineCirclePath)

            linePaint.color = getColor(R.color.default_base_line)
            linePaint.strokeWidth = bgLineWidth
            linePaint.style = Paint.Style.FILL_AND_STROKE
            val intervals = 1 * dp
            linePaint.pathEffect = DashPathEffect(floatArrayOf(intervals, intervals), 0f)

            //横向线
            var lineTop = lineSpace
            while (lineTop < viewHeight) {
                drawLinePath.reset()
                drawLinePath.moveTo(0.toFloat(), lineTop)
                drawLinePath.lineTo(viewWidth.toFloat(), lineTop)

                canvas.drawPath(drawLinePath, linePaint)
                lineTop += lineSpace
            }

            //纵向线
            var lineLeft = lineSpace
            while (lineLeft < viewWidth) {
                drawLinePath.reset()
                drawLinePath.moveTo(lineLeft, 0.toFloat())
                drawLinePath.lineTo(lineLeft, viewHeight.toFloat())

                canvas.drawPath(drawLinePath, linePaint)
                lineLeft += lineSpace
            }
            canvas.restore()
        }

        //绘制扫描弧度
        canvas.save()
        val shaderColor: Int = radarShaderEndColor
        val shaderStartColor: Int = radarShaderStartColor
        val lineColor: Int = radarLineColor

        paint.style = Paint.Style.FILL_AND_STROKE
        paint.strokeWidth = circleWidth //圈的厚度

        val arcOffset = circleWidth / 2
        scanDrawRectF.set(
            centerX - circleRadius + arcOffset, centerY - circleRadius + arcOffset,
            centerX + circleRadius - arcOffset, centerY + circleRadius - arcOffset
        )

        //雷达区域扫描arc
        paint.shader = SweepGradient(
            (centerX).toFloat(),
            (centerY).toFloat(),
            intArrayOf(Color.TRANSPARENT, shaderStartColor, shaderColor),
            floatArrayOf(0f, 0.6f, 1f)
        )

        if (!isInEditMode) {
            canvas.concat(scanMatrix)
        }
        canvas.drawArc(scanDrawRectF, 0f, 360f /*(36 * 6).toFloat()*/, true, paint)

        //外圈轮廓渐变arc
        paint.style = Paint.Style.STROKE
        paint.shader = SweepGradient(
            (centerX).toFloat(),
            (centerY).toFloat(),
            intArrayOf(Color.TRANSPARENT, shaderStartColor, shaderColor),
            floatArrayOf(0f, 0.1f, 1f)
        )

        canvas.drawArc(scanDrawRectF, 0f, 360f /*(36 * 6).toFloat()*/, false, paint)

        if (drawScanStartLine) {
            //开始扫描线
            paint.color = lineColor
            paint.style = Paint.Style.FILL_AND_STROKE
            paint.strokeWidth = circleWidth
            paint.shader = null
//        drawLinePath.reset()
//        drawLinePath.moveTo(0.toFloat(), lineTop)
//        drawLinePath.lineTo(measuredWidth.toFloat(), lineTop)

            canvas.drawLine(
                centerX.toFloat(),
                centerY.toFloat() + paint.strokeWidth / 2,
                (centerX + viewDrawWidth / 2).toFloat() + circleRadiusOffset,
                centerY.toFloat() + paint.strokeWidth / 2,
                paint
            )
        }

        canvas.restore()

//        //绘制圆圈
//        canvas.save()
//        canvas.translate((measuredWidth / 2).toFloat(), (measuredHeight / 2).toFloat())
//        paint.shader = object : LinearGradient(0f, 0f, (measuredWidth / 2).toFloat(), (measuredWidth / 2).toFloat(),
//                intArrayOf(Color.TRANSPARENT, Color.RED), floatArrayOf(0f, 1f), TileMode.CLAMP) {
//        }
//        paint.style = Paint.Style.FILL_AND_STROKE
//        paint.strokeWidth = circleWidth //圈的厚度
//        canvas.drawCircle(0f, 0f, circleRadius - circleWidth / 2, paint)
//
//        canvas.restore()

        if (enableAnim) {
            sweepAngle += 3 //控制扫描速度
            postInvalidate()
        }
    }
}