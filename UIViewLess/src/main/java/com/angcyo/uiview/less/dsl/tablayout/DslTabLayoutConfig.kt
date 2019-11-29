package com.angcyo.uiview.less.dsl.tablayout

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import com.angcyo.uiview.less.R
import com.angcyo.uiview.less.dsl.DslSelectorConfig
import com.angcyo.uiview.less.dsl.tablayout.DslTabIndicator.Companion.NO_COLOR
import com.angcyo.uiview.less.kotlin.tintDrawableColor
import com.angcyo.uiview.less.resources.AnimUtil.evaluateColor

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/11/26
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
open class DslTabLayoutConfig(val tabLayout: DslTabLayout) : DslSelectorConfig() {

    /**是否开启文本颜色*/
    var tabEnableTextColor = true
        set(value) {
            field = value
            if (field) {
                tabEnableIcoColor = true
            }
        }
    /**是否开启颜色渐变效果*/
    var tabEnableGradientColor = false
        set(value) {
            field = value
            if (field) {
                tabEnableIcoGradientColor = true
            }
        }
    /**选中的文本颜色*/
    var tabSelectColor: Int = Color.WHITE //Color.parseColor("#333333")
    /**未选中的文本颜色*/
    var tabDeselectColor: Int = Color.parseColor("#999999")
    /**是否开启Bold, 文本加粗*/
    var tabEnableTextBold = false

    /**是否开启图标颜色*/
    var tabEnableIcoColor = true
    /**是否开启图标颜色渐变效果*/
    var tabEnableIcoGradientColor = false
    /**选中的图标颜色*/
    var tabIcoSelectColor: Int = NO_COLOR
        get() {
            return if (field == NO_COLOR) tabSelectColor else field
        }
    /**未选中的图标颜色*/
    var tabIcoDeselectColor: Int = NO_COLOR
        get() {
            return if (field == NO_COLOR) tabDeselectColor else field
        }

    /**是否开启scale渐变效果*/
    var tabEnableGradientScale = false

    /**最小缩放的比例*/
    var tabMinScale = 0.8f
    /**大嘴缩放的比例*/
    var tabMaxScale = 1.2f

    var tabGradientCallback = TabGradientCallback()

    init {
        onStyleItemView = { itemView, index, select ->
            onUpdateItemStyle(itemView, index, select)
        }
    }

    /**xml属性读取*/
    open fun initAttribute(context: Context, attributeSet: AttributeSet? = null) {
        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.DslTabLayout)

        tabSelectColor =
            typedArray.getColor(R.styleable.DslTabLayout_tab_select_color, tabSelectColor)
        tabDeselectColor =
            typedArray.getColor(
                R.styleable.DslTabLayout_tab_deselect_color,
                tabDeselectColor
            )
        tabIcoSelectColor =
            typedArray.getColor(R.styleable.DslTabLayout_tab_ico_select_color, NO_COLOR)
        tabIcoDeselectColor =
            typedArray.getColor(R.styleable.DslTabLayout_tab_ico_deselect_color, NO_COLOR)

        tabEnableTextColor = typedArray.getBoolean(
            R.styleable.DslTabLayout_tab_enable_text_color,
            tabEnableTextColor
        )
        tabEnableGradientColor = typedArray.getBoolean(
            R.styleable.DslTabLayout_tab_enable_gradient_color,
            tabEnableGradientColor
        )
        tabEnableIcoColor = typedArray.getBoolean(
            R.styleable.DslTabLayout_tab_enable_ico_color,
            tabEnableIcoColor
        )
        tabEnableIcoGradientColor = typedArray.getBoolean(
            R.styleable.DslTabLayout_tab_enable_ico_gradient_color,
            tabEnableIcoGradientColor
        )

        tabEnableTextBold = typedArray.getBoolean(
            R.styleable.DslTabLayout_tab_enable_text_bold,
            tabEnableTextBold
        )

        tabEnableGradientScale = typedArray.getBoolean(
            R.styleable.DslTabLayout_tab_enable_gradient_scale,
            tabEnableGradientScale
        )
        tabMinScale = typedArray.getFloat(R.styleable.DslTabLayout_tab_min_scale, tabMinScale)
        tabMaxScale = typedArray.getFloat(R.styleable.DslTabLayout_tab_max_scale, tabMaxScale)
        typedArray.recycle()
    }

    /**更新item的样式*/
    open fun onUpdateItemStyle(itemView: View, index: Int, select: Boolean) {
        //"$itemView\n$index\n$select".logw()

        (itemView as? TextView)?.apply {
            //文本加粗
            paint?.apply {
                flags = if (tabEnableTextBold && select) {
                    paint.flags or Paint.FAKE_BOLD_TEXT_FLAG
                } else {
                    paint.flags and Paint.FAKE_BOLD_TEXT_FLAG.inv()
                }
            }

            if (tabEnableTextColor) {
                //文本颜色
                setTextColor(if (select) tabSelectColor else tabDeselectColor)
            }
        }

        if (tabEnableIcoColor) {
            _updateIcoColor(itemView, if (select) tabIcoSelectColor else tabIcoDeselectColor)
        }

        if (tabEnableGradientScale) {
            itemView.scaleX = if (select) tabMaxScale else tabMinScale
            itemView.scaleY = if (select) tabMaxScale else tabMinScale
        }

        if (tabLayout.drawBorder) {
            tabLayout.tabBorder?.updateItemBackground(tabLayout, itemView, index, select)
        }
    }

    /**
     * [DslTabLayout]滚动时回调.
     * */
    open fun onPageIndexScrolled(fromIndex: Int, toIndex: Int, positionOffset: Float) {

    }

    /**
     * [onPageIndexScrolled]
     * */
    open fun onPageViewScrolled(fromView: View?, toView: View, positionOffset: Float) {
        //"$fromView\n$toView\n$positionOffset".logi()

        if (fromView != toView) {
            if (tabEnableGradientColor) {
                //文本渐变
                _gradientColor(fromView, tabSelectColor, tabDeselectColor, positionOffset)
                _gradientColor(toView, tabDeselectColor, tabSelectColor, positionOffset)
            }

            if (tabEnableIcoGradientColor) {
                //图标渐变
                _gradientIcoColor(fromView, tabIcoSelectColor, tabIcoDeselectColor, positionOffset)
                _gradientIcoColor(toView, tabIcoDeselectColor, tabIcoSelectColor, positionOffset)
            }

            if (tabEnableGradientScale) {
                //scale渐变
                _gradientScale(fromView, tabMaxScale, tabMinScale, positionOffset)
                _gradientScale(toView, tabMinScale, tabMaxScale, positionOffset)
            }
        }
    }

    open fun _gradientColor(view: View?, startColor: Int, endColor: Int, percent: Float) {
        tabGradientCallback.onGradientColor(view, startColor, endColor, percent)
    }

    open fun _gradientIcoColor(view: View?, startColor: Int, endColor: Int, percent: Float) {
        tabGradientCallback.onGradientIcoColor(view, startColor, endColor, percent)
    }

    open fun _gradientScale(view: View?, startScale: Float, endScale: Float, percent: Float) {
        tabGradientCallback.onGradientScale(view, startScale, endScale, percent)
    }

    open fun _updateIcoColor(view: View?, color: Int) {
        tabGradientCallback.onUpdateIcoColor(view, color)
    }
}

open class TabGradientCallback {
    open fun onGradientColor(view: View?, startColor: Int, endColor: Int, percent: Float) {
        (view as? TextView)?.apply {
            setTextColor(evaluateColor(percent, startColor, endColor))
        }
    }

    open fun onGradientIcoColor(view: View?, startColor: Int, endColor: Int, percent: Float) {
        onUpdateIcoColor(view, evaluateColor(percent, startColor, endColor))
    }

    open fun onUpdateIcoColor(view: View?, color: Int) {
        view?.tintDrawableColor(color)
    }

    open fun onGradientScale(view: View?, startScale: Float, endScale: Float, percent: Float) {
        view?.apply {
            (startScale + (endScale - startScale) * percent).let {
                scaleX = it
                scaleY = it
            }
        }
    }
}