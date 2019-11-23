package com.angcyo.uiview.less.draw

import android.animation.Animator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import com.angcyo.uiview.less.R
import com.angcyo.uiview.less.kotlin.abs
import com.angcyo.uiview.less.kotlin.dpi
import com.angcyo.uiview.less.resources.RAnimatorListener
import com.angcyo.uiview.less.skin.SkinHelper

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2018/06/21 09:17
 * 修改人员：Robi
 * 修改时间：2018/06/21 09:17
 * 修改备注：
 * Version: 1.0.0
 */
class RTabIndicator(view: View, attributeSet: AttributeSet? = null) : BaseDraw(view, attributeSet) {
    companion object {
        //无样式
        const val INDICATOR_TYPE_NONE = 0
        //底部一根线, 会在childView的前面绘制
        const val INDICATOR_TYPE_BOTTOM_LINE = 1
        //圆角矩形块状, 会在childView的后面绘制
        const val INDICATOR_TYPE_ROUND_RECT_BLOCK = 2
        //其他等同 INDICATOR_TYPE_BOTTOM_LINE, 但是在滑动过程中,会放大到目标位置,再缩小的本身大小
        const val INDICATOR_TYPE_BOTTOM_FLOW_LINE = 3
        //[INDICATOR_TYPE_BOTTOM_LINE] 渐变
        const val INDICATOR_TYPE_BOTTOM_GRADIENT_LINE = 4
    }

    var indicatorDrawable: Drawable? = null

    /**指示器的样式*/
    var indicatorType = INDICATOR_TYPE_BOTTOM_LINE

    /**指示器的颜色*/
    var indicatorColor = -1

    /**如果未指定指示器的宽度, 那么就用对应child的宽度*/
    var indicatorWidth = 0
    var indicatorHeight: Int = (2 * density()).toInt()

    /**偏移距离, 不能用paddingBottom*/
    var indicatorOffsetY: Int = (2 * density()).toInt()
    /**宽度 修正量*/
    var indicatorWidthOffset: Int = 0
    var indicatorHeightOffset: Int = 0

    /**圆角大小*/
    var indicatorRoundSize: Int = (10 * density()).toInt()

    /**激活指示器滚动动画*/
    var enableIndicatorAnim = true

    /**激活动画的差值器*/
    var enableOvershoot = true


    /**使用Drawable的大小, 设置r_indicator_width, r_indicator_height*/
    var useIndicatorDrawableSize = false

    /**使用tab Item 中的哪一个child index, 作为定位的靶子, -1使用顶级item*/
    var useChildViewIndex = -1

    var gradientStartColor: Int = 0
    var gradientEndColor: Int = 0

    init {
        initAttribute(attributeSet)
    }

    override fun initAttribute(attr: AttributeSet?) {
        val typedArray = obtainStyledAttributes(attr, R.styleable.RTabIndicator)
        indicatorType = typedArray.getInt(R.styleable.RTabIndicator_r_indicator_type, indicatorType)
        indicatorColor = baseColor
        indicatorColor = typedArray.getColor(R.styleable.RTabIndicator_r_indicator_color, indicatorColor)
        indicatorWidth = typedArray.getDimensionPixelOffset(R.styleable.RTabIndicator_r_indicator_width, indicatorWidth)
        indicatorHeight =
            typedArray.getDimensionPixelOffset(R.styleable.RTabIndicator_r_indicator_height, indicatorHeight)
        indicatorOffsetY =
            typedArray.getDimensionPixelOffset(R.styleable.RTabIndicator_r_indicator_offset_y, indicatorOffsetY)
        indicatorWidthOffset = typedArray.getDimensionPixelOffset(
            R.styleable.RTabIndicator_r_indicator_offset_width,
            indicatorWidthOffset
        )
        indicatorHeightOffset = typedArray.getDimensionPixelOffset(
            R.styleable.RTabIndicator_r_indicator_offset_height,
            indicatorHeightOffset
        )

        if (isInEditMode) {
            indicatorRoundSize = 4 * dpi
        }

        indicatorRoundSize =
            typedArray.getDimensionPixelOffset(R.styleable.RTabIndicator_r_indicator_round_size, indicatorRoundSize)

        enableIndicatorAnim =
            typedArray.getBoolean(R.styleable.RTabIndicator_r_indicator_enable_anim, enableIndicatorAnim)
        enableOvershoot =
            typedArray.getBoolean(R.styleable.RTabIndicator_r_indicator_enable_anim_overshoot, enableOvershoot)

        useIndicatorDrawableSize =
            typedArray.getBoolean(R.styleable.RTabIndicator_r_use_indicator_drawable_size, useIndicatorDrawableSize)

        useChildViewIndex = typedArray.getInt(R.styleable.RTabIndicator_r_use_child_view_index, useChildViewIndex)

        indicatorDrawable = typedArray.getDrawable(R.styleable.RTabIndicator_r_indicator_drawable)
        if (useIndicatorDrawableSize) {
            indicatorDrawable?.let {
                indicatorWidth = it.intrinsicWidth
                indicatorHeight = it.intrinsicHeight
            }
        }

        if (isInEditMode) {
            gradientStartColor = this.getColor(R.color.theme_color_primary)
            gradientEndColor = this.getColor(R.color.theme_color_primary_dark)
        } else {
            gradientStartColor = SkinHelper.getSkin().themeSubColor
            gradientEndColor = SkinHelper.getSkin().themeDarkColor
        }

        gradientStartColor = typedArray.getColor(R.styleable.RTabIndicator_r_gradient_start_color, gradientStartColor)
        gradientEndColor = typedArray.getColor(R.styleable.RTabIndicator_r_gradient_end_color, gradientEndColor)

        typedArray.recycle()
    }


    private var animStartCenterX = -1
    private var animEndCenterX = -1

    private var animStartWidth = -1
    private var animEndWidth = -1

    //之前的索引
    private var oldIndex = 0

    /**当前指示那个位置*/
    var curIndex = 0
        set(value) {
            if (isNoIndicator()) {
                field = value
            } else if (viewWidth == 0 || viewHeight == 0) {
                field = value
            } else if (field == value || value == -1 || isInEditMode) {
                field = value
                scrollTabLayoutToCenter()
            } else if (pagerPositionOffset == 0f) {
                oldIndex = field

                resetAnimValue(field, value)

                field = value

                if (enableIndicatorAnim) {
                    animatorValueInterpolator = 0f
                    indicatorAnimator.start()
                } else {
                    indicatorAnimator.cancel()
                    scrollTabLayoutToCenter()
                }
            } else {
                field = value
                //scrollTabLayoutToCenter()
                //postInvalidate()
            }
        }

    /**INDICATOR_TYPE_BOTTOM_FLOW_LINE 下, 指示器左右的坐标*/
    private var typeFlowIndicatorLeft = 0
    private var typeFlowIndicatorRight = 0

    /**ViewPager滚动相关*/
    var pagerPositionOffset = 0f
        set(value) {
            field = value

            if (isNoIndicator()) {
                return
            }

            if (field > 0f) {
                if (curIndex == pagerPosition) {
                    //view pager 往下一页滚
                    resetAnimValue(curIndex, curIndex + 1)

                    animatorValueInterpolator = value
                    animatorValue = value

                    resetNextFlowValue()
                } else {
                    //往上一页滚
                    resetAnimValue(curIndex, pagerPosition)

                    animatorValueInterpolator = 1f - value
                    animatorValue = 1f - value

                    resetPrevFlowValue()
                }
                postInvalidate()
            }
        }
    var pagerPosition = 0

    private val indicatorDrawRect: RectF by lazy {
        RectF()
    }

    //准备滚动到下一页需要的数据
    private fun resetAnimValue(startIndex: Int, endIndex: Int) {
        animStartCenterX = getChildCenter(startIndex)
        animEndCenterX = getChildCenter(endIndex)

        animStartWidth = getIndicatorWidth(startIndex)
        animEndWidth = getIndicatorWidth(endIndex)
    }

    private fun resetFlowValue() {
        //指示器有效的宽度
        val validWidth = animStartWidth + indicatorWidthOffset
        typeFlowIndicatorLeft = animStartCenterX - validWidth / 2
        typeFlowIndicatorRight = animStartCenterX + validWidth / 2
    }

    private fun resetNormalFlowValue() {
        val validWidth = animStartWidth + indicatorWidthOffset

        //child横向中心x坐标
        val childCenter: Int = if (isAnimStart()) {
            (animStartCenterX + (animEndCenterX - animStartCenterX) * animatorValueInterpolator).toInt()
        } else {
            getChildCenter(curIndex)
        }

        //L.e("RTabIndicator: draw ->$viewWidth $childCenter $indicatorDrawWidth $curIndex $animatorValueInterpolator")

        typeFlowIndicatorLeft = childCenter - validWidth / 2
        typeFlowIndicatorRight = childCenter + validWidth / 2
    }

    private fun resetNextFlowValue() {
        //flow允许移动的最大距离
        val maxDistance = (animEndCenterX - animStartCenterX).abs()

        resetFlowValue()

        if (animatorValueInterpolator <= 0.5f) {
            //变长
            typeFlowIndicatorRight += (maxDistance * animatorValueInterpolator / 0.5f).toInt()
        } else if (animatorValueInterpolator <= 1f) {
            //变短
            typeFlowIndicatorRight += maxDistance
            typeFlowIndicatorLeft += (maxDistance * (animatorValueInterpolator - 0.5f) / 0.5f).toInt()
        }
    }

    private fun resetPrevFlowValue() {
        //flow允许移动的最大距离
        val maxDistance = (animEndCenterX - animStartCenterX).abs()

        resetFlowValue()

        if (animatorValueInterpolator <= 0.5f) {
            //变长
            typeFlowIndicatorLeft -= (maxDistance * animatorValueInterpolator / 0.5f).toInt()
        } else if (animatorValueInterpolator <= 1f) {
            //变短
            typeFlowIndicatorLeft -= maxDistance
            typeFlowIndicatorRight -= (maxDistance * (animatorValueInterpolator - 0.5f) / 0.5f).toInt()
        }
    }

    private fun getChildCenter(index: Int): Int {
        if (index in 0 until childCount) {
            var curChildView = getChildAt(index)

            var pLeft = 0
            if (useChildViewIndex >= 0) {
                if (curChildView is ViewGroup) {
                    pLeft = curChildView.left
                    curChildView = curChildView.getChildAt(useChildViewIndex)
                }
            }

            //child横向中心x坐标
            return pLeft + curChildView.left + curChildView.paddingLeft +
                    (curChildView.measuredWidth - curChildView.paddingLeft - curChildView.paddingRight) / 2
        }
        //返回上一次结束的x坐标
        return animEndCenterX
    }

    private fun getIndicatorWidth(index: Int): Int {
        return if (indicatorWidth == 0) {
            if (index in 0 until childCount) {
                val curChildView = getChildAt(index)
                //child横向中心x坐标
                return curChildView.measuredWidth - curChildView.paddingLeft - curChildView.paddingRight
            }
            //返回上一次结束的x坐标
            return animEndWidth
        } else {
            indicatorWidth
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (isNoIndicator()) {
            return
        }

        if (isInEditMode && curIndex < 0) {
            curIndex = 0
        }

        if (curIndex in 0 until childCount) {
            //安全的index

            val childView = getChildAt(curIndex)

            //指示器的宽度
            val indicatorDrawWidth = if (isAnimStart()) {
                (animStartWidth + (animEndWidth - animStartWidth) * animatorValueInterpolator + indicatorWidthOffset).toInt()
            } else {
                getIndicatorWidth(curIndex) + indicatorWidthOffset
            }

            //child横向中心x坐标
            val childCenter: Int = if (isAnimStart()) {
                (animStartCenterX + (animEndCenterX - animStartCenterX) * animatorValueInterpolator).toInt()
            } else {
                getChildCenter(curIndex)
            }

            //L.e("RTabIndicator: draw ->$viewWidth $childCenter $indicatorDrawWidth $curIndex $animatorValueInterpolator")

            val left = if (isAnimStart() && indicatorType == INDICATOR_TYPE_BOTTOM_FLOW_LINE) {
                typeFlowIndicatorLeft.toFloat()
            } else {
                (childCenter - indicatorDrawWidth / 2).toFloat()
            }

            val right = if (isAnimStart() && indicatorType == INDICATOR_TYPE_BOTTOM_FLOW_LINE) {
                typeFlowIndicatorRight.toFloat()
            } else {
                (childCenter + indicatorDrawWidth / 2).toFloat()
            }

            val top = when (indicatorType) {
                INDICATOR_TYPE_BOTTOM_LINE, INDICATOR_TYPE_BOTTOM_GRADIENT_LINE -> (viewHeight - indicatorOffsetY - indicatorHeight).toFloat()
                INDICATOR_TYPE_ROUND_RECT_BLOCK -> (childView.top - indicatorHeightOffset / 2).toFloat()
                INDICATOR_TYPE_BOTTOM_FLOW_LINE -> (viewHeight - indicatorOffsetY - indicatorHeight).toFloat()
                else -> 0f
            }
            val bottom = when (indicatorType) {
                INDICATOR_TYPE_BOTTOM_LINE, INDICATOR_TYPE_BOTTOM_GRADIENT_LINE -> (viewHeight - indicatorOffsetY).toFloat()
                INDICATOR_TYPE_ROUND_RECT_BLOCK -> (childView.bottom + indicatorHeightOffset / 2).toFloat()
                INDICATOR_TYPE_BOTTOM_FLOW_LINE -> (viewHeight - indicatorOffsetY).toFloat()
                else -> 0f
            }
            indicatorDrawRect.set(left, top, right, bottom)

            if (indicatorDrawable == null) {
                mBasePaint.shader = null
                when (indicatorType) {
                    INDICATOR_TYPE_NONE -> {
                    }
                    INDICATOR_TYPE_BOTTOM_LINE, INDICATOR_TYPE_BOTTOM_GRADIENT_LINE -> {
                        if (indicatorType == INDICATOR_TYPE_BOTTOM_GRADIENT_LINE) {
                            @SuppressLint("DrawAllocation")
                            mBasePaint.shader = LinearGradient(
                                indicatorDrawRect.left,
                                indicatorDrawRect.top,
                                indicatorDrawRect.right,
                                indicatorDrawRect.top,
                                intArrayOf(gradientStartColor, gradientEndColor),
                                null,
                                Shader.TileMode.CLAMP
                            )
                        }

                        mBasePaint.color = indicatorColor
                        canvas.drawRoundRect(
                            indicatorDrawRect,
                            indicatorRoundSize.toFloat(),
                            indicatorRoundSize.toFloat(),
                            mBasePaint
                        )
                    }
                    INDICATOR_TYPE_ROUND_RECT_BLOCK -> {
                        mBasePaint.color = indicatorColor
                        canvas.drawRoundRect(
                            indicatorDrawRect,
                            indicatorRoundSize.toFloat(),
                            indicatorRoundSize.toFloat(),
                            mBasePaint
                        )
                    }
                    INDICATOR_TYPE_BOTTOM_FLOW_LINE -> {
                        mBasePaint.color = indicatorColor
                        canvas.drawRoundRect(
                            indicatorDrawRect,
                            indicatorRoundSize.toFloat(),
                            indicatorRoundSize.toFloat(),
                            mBasePaint
                        )
                    }
                }
            } else {
                indicatorDrawable?.let {
                    it.setBounds(
                        indicatorDrawRect.left.toInt(),
                        indicatorDrawRect.top.toInt(),
                        indicatorDrawRect.right.toInt(),
                        indicatorDrawRect.bottom.toInt()
                    )
                    it.draw(canvas)
                }
            }
        }
    }

    /**确保当前的centerX , 在TabLayout 显示区域的中心*/
    private fun scrollTabLayoutToCenter() {
        if (curIndex in 0..(childCount - 1)) {

            //child横向中心x坐标
            val childCenter: Int = if (isAnimStart()) {
                (animStartCenterX + (animEndCenterX - animStartCenterX) * animatorValue).toInt()
            } else {
                getChildCenter(curIndex)
            }

            val viewCenterX = viewWidth / 2
            if (childCenter > viewCenterX) {
                scrollTo(childCenter - viewCenterX, 0)
            } else {
                scrollTo(0, 0)
            }
        }
    }

    /**没有指示器*/
    fun isNoIndicator() = indicatorType == INDICATOR_TYPE_NONE

    private var animatorValue = -1f
        set(value) {
            field = value
            if (field != -1f) {
                scrollTabLayoutToCenter()
            }
        }

    /**用此成员变量判断动画开始和结束*/
    private var animatorValueInterpolator = -1f

    private fun isAnimStart() = animatorValueInterpolator != -1f

    private val overshootInterpolator: OvershootInterpolator by lazy { OvershootInterpolator() }

    private val indicatorAnimator by lazy {
        ObjectAnimator.ofFloat(0f, 1f).apply {
            duration = 300
            addUpdateListener {
                animatorValue = it.animatedValue as Float
                if (enableOvershoot) {
                    animatorValueInterpolator = overshootInterpolator.getInterpolation(animatorValue)
                } else {
                    animatorValueInterpolator = animatorValue
                }
//                if (oldIndex + 1 == curIndex) {
//                    resetNextFlowValue()
//                } else if (curIndex + 1 == oldIndex) {
//                    resetPrevFlowValue()
//                } else {
//                    resetNormalFlowValue()
//                }
                resetNormalFlowValue()
                //L.e("call: $animatorValue -> ")
                postInvalidateOnAnimation()
            }
            addListener(object : RAnimatorListener() {
                override fun onAnimationFinish(animation: Animator?, cancel: Boolean) {
                    super.onAnimationFinish(animation, cancel)
                    animatorValueInterpolator = -1f
                }
            })
        }
    }
}