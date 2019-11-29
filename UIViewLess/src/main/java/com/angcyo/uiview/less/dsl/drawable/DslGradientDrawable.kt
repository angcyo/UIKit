package com.angcyo.uiview.less.dsl.drawable

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import androidx.annotation.IntDef
import java.util.*

/**
 * 用来构建GradientDrawable
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/11/27
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
open class DslGradientDrawable : DslDrawable() {

    /**形状*/
    @Shape
    var gradientShape = GradientDrawable.RECTANGLE

    /**填充的颜色*/
    var gradientSolidColor = Color.TRANSPARENT

    /**边框的颜色*/
    var gradientStrokeColor = Color.TRANSPARENT
    /**边框的宽度*/
    var gradientStrokeWidth = 0
    /**蚂蚁线的宽度*/
    var gradientDashWidth = 0f
    /**蚂蚁线之间的间距*/
    var gradientDashGap = 0f

    /**
     * 四个角, 8个设置点的圆角信息
     * 从 左上y轴->左上x轴->右上x轴->右上y轴..., 开始设置.
     */
    var gradientRadii = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)

    /**颜色渐变*/
    var gradientColors: IntArray? = null
    var gradientColorsOffsets: FloatArray? = null
    /**渐变中心点坐标*/
    var gradientCenterX = 0.5f
    var gradientCenterY = 0.5f
    /**半径*/
    var gradientRadius = 0.5f
    /** 渐变方向, 默认从左到右 */
    var gradientOrientation = GradientDrawable.Orientation.LEFT_RIGHT
    /** 渐变类型 */
    @GradientType
    var gradientType = GradientDrawable.LINEAR_GRADIENT

    var originDrawable: Drawable? = null

    /**宽度补偿*/
    var gradientWidthOffset: Int = 0

    /**高度补偿*/
    var gradientHeightOffset: Int = 0

    /**当前的配置, 是否能生成有效的[GradientDrawable]*/
    open fun isValidConfig(): Boolean {
        return gradientSolidColor != Color.TRANSPARENT ||
                gradientStrokeColor != Color.TRANSPARENT ||
                gradientColors != null
    }

    fun _fillRadii(array: FloatArray, radii: String?) {
        if (radii.isNullOrEmpty()) {
            return
        }
        val split = radii.split(",")
        if (split.size != 8) {
            throw IllegalArgumentException("radii 需要8个值.")
        } else {
            val dp = Resources.getSystem().displayMetrics.density
            for (i in split.indices) {
                array[i] = split[i].toFloat() * dp
            }
        }
    }

    fun _fillColor(colors: String?): IntArray? {
        if (colors.isNullOrEmpty()) {
            return null
        }
        val split = colors.split(",")

        return IntArray(split.size) { Color.parseColor(split[it]) }
    }

    open fun updateDrawable() {
        originDrawable = generateDrawable()
        invalidateSelf()
    }

    open fun configDrawable(config: DslGradientDrawable.() -> Unit): DslGradientDrawable {
        this.config()
        updateDrawable()
        return this
    }

    /**构建[GradientDrawable]*/
    open fun generateDrawable(): GradientDrawable {
        val drawable = GradientDrawable()

        with(drawable) {
            shape = gradientShape
            setStroke(
                gradientStrokeWidth,
                gradientStrokeColor,
                gradientDashWidth,
                gradientDashGap
            )
            setColor(gradientSolidColor)
            cornerRadii = gradientRadii

            if (gradientColors != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    setGradientCenter(
                        this@DslGradientDrawable.gradientCenterX,
                        this@DslGradientDrawable.gradientCenterY
                    )
                }
                gradientRadius = this@DslGradientDrawable.gradientRadius
                gradientType = this@DslGradientDrawable.gradientType
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    orientation = gradientOrientation
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    setColors(gradientColors, gradientColorsOffsets)
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    colors = gradientColors
                }
            }
        }

        return drawable
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        originDrawable?.apply {
            setBounds(
                this@DslGradientDrawable.bounds.left - gradientWidthOffset / 2,
                this@DslGradientDrawable.bounds.top - gradientHeightOffset / 2,
                this@DslGradientDrawable.bounds.right + gradientWidthOffset / 2,
                this@DslGradientDrawable.bounds.bottom + gradientHeightOffset / 2
            )
            draw(canvas)
        }
    }

    //<editor-fold desc="圆角相关配置">

    /**
     * 4个角, 8个点 圆角配置
     */
    open fun cornerRadii(radii: FloatArray) {
        gradientRadii = radii
    }

    open fun cornerRadius(radii: Float) {
        Arrays.fill(gradientRadii, radii)
    }

    open fun cornerRadius(
        leftTop: Float = 0f,
        rightTop: Float = 0f,
        rightBottom: Float = 0f,
        leftBottom: Float = 0f
    ) {
        gradientRadii[0] = leftTop
        gradientRadii[1] = leftTop
        gradientRadii[2] = rightTop
        gradientRadii[3] = rightTop
        gradientRadii[4] = rightBottom
        gradientRadii[5] = rightBottom
        gradientRadii[6] = leftBottom
        gradientRadii[7] = leftBottom
    }

    /**
     * 只配置左边的圆角
     */
    open fun cornerRadiiLeft(radii: Float) {
        gradientRadii[0] = radii
        gradientRadii[1] = radii
        gradientRadii[6] = radii
        gradientRadii[7] = radii
    }

    open fun cornerRadiiRight(radii: Float) {
        gradientRadii[2] = radii
        gradientRadii[3] = radii
        gradientRadii[4] = radii
        gradientRadii[5] = radii
    }

    open fun cornerRadiiTop(radii: Float) {
        gradientRadii[0] = radii
        gradientRadii[1] = radii
        gradientRadii[2] = radii
        gradientRadii[3] = radii
    }

    open fun cornerRadiiBottom(radii: Float) {
        gradientRadii[4] = radii
        gradientRadii[5] = radii
        gradientRadii[6] = radii
        gradientRadii[7] = radii
    }

    //</editor-fold desc="圆角相关配置">
}

@IntDef(
    GradientDrawable.RECTANGLE,
    GradientDrawable.OVAL,
    GradientDrawable.LINE,
    GradientDrawable.RING
)
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
annotation class Shape

@IntDef(
    GradientDrawable.LINEAR_GRADIENT,
    GradientDrawable.RADIAL_GRADIENT,
    GradientDrawable.SWEEP_GRADIENT
)
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
annotation class GradientType