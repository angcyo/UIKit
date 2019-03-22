package com.angcyo.uiview.less.kotlin

import android.graphics.Matrix
import android.graphics.Path
import android.graphics.PathMeasure
import android.graphics.RectF

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/03/19
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */

/**
 *
 * https://blog.csdn.net/cquwentao/article/details/51436852
 * @param progress [0,1]
 * */
public fun Path.getProgressPosition(progress: Float): FloatArray {
    val pathMeasure = PathMeasure(this, false)

    val floatArray = floatArrayOf()
    pathMeasure.getPosTan(progress, floatArray, null)

    return floatArray
}

/**
 * @param progress [0,1]
 * */
public fun Path.getProgressPath(progress: Float): Path {
    val dst = Path()
    dst.reset()
    // 硬件加速的BUG
    dst.lineTo(0f, 0f)
    val pathMeasure = PathMeasure(this, false)

    //参数startWithMoveTo表示起始点是否使用moveTo方法，通常为True，保证每次截取的Path片段都是正常的、完整的。
    pathMeasure.getSegment(0f, progress, dst, true)

    return dst
}

/**
 * 简单的缩放一个矩形
 * */
public fun RectF.scale(scaleX: Float, scaleY: Float): RectF {
    val matrix = Matrix()
    matrix.setScale(scaleX, scaleY)
    matrix.mapRect(this)
    return this
}

/**
 * 在矩形中间点, 缩放
 * */
public fun RectF.scaleFromCenter(scaleX: Float, scaleY: Float): RectF {
    val matrix = Matrix()
    matrix.setScale(scaleX, scaleY)
    val tempRectF = RectF()
    matrix.mapRect(tempRectF, this)
    matrix.postTranslate(
        this.width() / 2 - tempRectF.width() / 2,
        this.height() / 2 - tempRectF.height() / 2
    )
    matrix.mapRect(this)
    return this
}