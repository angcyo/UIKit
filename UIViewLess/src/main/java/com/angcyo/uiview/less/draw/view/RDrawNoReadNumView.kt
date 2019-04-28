package com.angcyo.uiview.less.draw.view

import android.content.Context
import android.util.AttributeSet
import com.angcyo.uiview.less.draw.RDrawNoReadNum

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/04/27
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
class RDrawNoReadNumView(context: Context, attributeSet: AttributeSet? = null) :
    BaseDrawView<RDrawNoReadNum>(context, attributeSet) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val measureDraw = baseDraw.measureDraw(widthMeasureSpec, heightMeasureSpec)

        setMeasuredDimension(measureDraw[0], measureDraw[1])
    }

    fun getDrawReadNum(): RDrawNoReadNum = baseDraw
}