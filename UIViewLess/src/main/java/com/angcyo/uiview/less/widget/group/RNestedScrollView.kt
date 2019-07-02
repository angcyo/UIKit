package com.angcyo.uiview.less.widget.group

import android.content.Context
import androidx.core.widget.NestedScrollView
import android.util.AttributeSet
import com.angcyo.uiview.less.R
import com.angcyo.uiview.less.kotlin.calcWidthHeightRatio

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/05/11
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */

class RNestedScrollView(context: Context, attributeSet: AttributeSet? = null) :
    NestedScrollView(context, attributeSet) {

    /**高度等于宽度*/
    protected var equWidth: Boolean = false
    var widthHeightRatio: String? = null

    /**
     * 允许的最大高度, 如果为-2px,那么就是屏幕高度的一半, 如果是-3px,那么就是屏幕高度的三分之, 以此内推, 0不处理
     * 如果是负数,就是屏幕的倍数.
     * 如果是正数,就是确确的值
     */
    private var maxHeight = 0


    init {
        val array = context.obtainStyledAttributes(attributeSet, R.styleable.RNestedScrollView)
        equWidth = array.getBoolean(R.styleable.RNestedScrollView_r_is_aeq_width, equWidth)
        widthHeightRatio = array.getString(R.styleable.RNestedScrollView_r_width_height_ratio)
        maxHeight = array.getDimension(R.styleable.RNestedScrollView_r_max_height, maxHeight.toFloat()).toInt()

        array.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (maxHeight > 0) {
            val heightMode = MeasureSpec.getMode(heightMeasureSpec)
            super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.AT_MOST))
            val measuredHeight = measuredHeight
            if (measuredHeight > maxHeight) {
                super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(maxHeight, heightMode))
            }
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }

        if (equWidth) {
            //setMeasuredDimension(measuredWidth, measuredWidth)
            super.onMeasure(
                MeasureSpec.makeMeasureSpec(measuredWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(measuredWidth, MeasureSpec.EXACTLY)
            )
        } else {
            calcWidthHeightRatio(widthHeightRatio)?.let {
                //setMeasuredDimension(it[0], it[1])
                super.onMeasure(
                    MeasureSpec.makeMeasureSpec(it[0], MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(it[1], MeasureSpec.EXACTLY)
                )
            }
        }
    }

}