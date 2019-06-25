package com.angcyo.uiview.less.draw

import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import com.angcyo.uiview.less.R
import com.angcyo.uiview.less.kotlin.alpha
import com.angcyo.uiview.less.kotlin.dp
import com.angcyo.uiview.less.kotlin.set
import kotlin.math.min

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/06/11
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
class HSProgressDraw(view: View) : RSectionDraw(view) {
    init {
        setSections(floatArrayOf(0.5f, 0.5f))

        setInterpolatorList(DecelerateInterpolator(), DecelerateInterpolator())
    }

    /**进度颜色*/
    var progressColor = Color.WHITE

    /**圆角大小*/
    var roundSize = 5 * dp

    override fun initAttribute(attr: AttributeSet?) {
        super.initAttribute(attr)
        val typedArray = obtainStyledAttributes(attr, R.styleable.HSProgressDraw)
        progressColor = typedArray.getColor(R.styleable.HSProgressDraw_r_progress_color, progressColor)
        roundSize = typedArray.getDimensionPixelOffset(
            R.styleable.HSProgressDraw_r_progress_round_size,
            roundSize.toInt()
        ).toFloat()
        typedArray.recycle()

        if (isInEditMode) {
            setProgress(50)
        }
    }

    override fun onDrawProgressSection(
        canvas: Canvas,
        index: Int,
        startProgress: Float,
        endProgress: Float,
        totalProgress: Float,
        sectionProgress: Float
    ) {
        mBasePaint.color = progressColor.alpha(255 * (1 - sectionProgress + 0.2f))

        val right = viewWidth - paddingRight

        var top = paddingTop
        val threshold = 0.9f

        if (sectionProgress > threshold) {
            top = (top + viewDrawHeight * (min((sectionProgress - threshold) / (1 - threshold), 0.5f))).toInt()
        }

        when {
            isInEditMode -> mDrawRectF.set(
                paddingLeft,
                top,
                (right * totalProgress).toInt(),
                viewHeight - paddingBottom
            )
            index == 0 -> mDrawRectF.set(
                paddingLeft,
                top,
                (right * sectionProgress).toInt(),
                viewHeight - paddingBottom
            )
            index == 1 -> mDrawRectF.set(
                (right - viewDrawWidth * sectionProgress).toInt(),
                top,
                right,
                viewHeight - paddingBottom
            )
        }
        canvas.drawRoundRect(mDrawRectF, roundSize, roundSize, mBasePaint)
    }

}