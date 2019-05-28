package com.angcyo.uiview.less.draw.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import com.angcyo.uiview.less.R
import com.angcyo.uiview.less.draw.RDrawRadarScan
import com.angcyo.uiview.less.kotlin.getColor
import com.angcyo.uiview.less.skin.SkinHelper

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/05/28
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
class RRadarNumDrawView(context: Context, attributeSet: AttributeSet? = null) :
    RDrawNoReadNumView(context, attributeSet) {

    override fun initExDraw(context: Context?, attrs: AttributeSet?) {
        val radarScan = RDrawRadarScan(this)
        radarScan.apply {
            enableDraw = false
            enableAnim = false
            drawBgColor = true
            backgroundColor = getColor(R.color.colorPrimaryDark)
            radarShaderStartColor = SkinHelper.getTranColor(Color.WHITE, 0x10)
            radarShaderEndColor = SkinHelper.getTranColor(Color.WHITE, 0xFF)
            drawScanStartLine = false
        }
        exDrawList.add(radarScan)
        super.initExDraw(context, attrs)
    }

    override fun onDraw(canvas: Canvas) {
        //super.onDraw(canvas)
        for (baseDraw in exDrawList) {
            baseDraw.onDraw(canvas)
        }
        baseDraw.onDraw(canvas)
    }

    fun getDrawRadar(): RDrawRadarScan = exDrawList[0] as RDrawRadarScan
}