package com.haibin.calendarview

import android.content.Context
import android.graphics.Color
import android.text.TextUtils
import android.util.AttributeSet
import com.angcyo.uiview.less.R
import com.angcyo.uiview.less.kotlin.getColor
import com.angcyo.uiview.less.skin.SkinHelper
import com.haibin.calendarview.custom.RMonthView

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/07/05
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
class RCalendarViewDelegate(context: Context, attributeSet: AttributeSet? = null) :
    CalendarViewDelegate(context, attributeSet) {

    var isInEditMode = false

    override fun init() {
        try {
            mWeekBarClass = if (TextUtils.isEmpty(mWeekBarClassPath))
                WeekBar::class.java
            else
                Class.forName(mWeekBarClassPath)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            mYearViewClass = if (TextUtils.isEmpty(mYearViewClassPath))
                DefaultYearView::class.java
            else
                Class.forName(mYearViewClassPath)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            mMonthViewClass = if (TextUtils.isEmpty(mMonthViewClassPath))
                RMonthView::class.java
            else
                Class.forName(mMonthViewClassPath)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            mWeekViewClass = if (TextUtils.isEmpty(mWeekViewClassPath))
                DefaultWeekView::class.java
            else
                Class.forName(mWeekViewClassPath)
        } catch (e: Exception) {
            e.printStackTrace()
        }


        super.init()

        monthViewClass
        weekBarClass
        weekViewClass
        yearViewClass

        //calendarItemHeight = 100 * dpi
        //monthViewShowMode = MODE_ALL_MONTH
        //selectMode = SELECT_MODE_DEFAULT

        mCurrentMonthTextColor = getColor(R.color.base_text_color)
        mCurMonthLunarTextColor = getColor(R.color.base_text_color_dark)
        mSelectedTextColor = Color.WHITE
        mSelectedLunarTextColor = Color.WHITE

        mWeekTextColor = Color.WHITE
        if (!isInEditMode) {
            mCurDayTextColor = SkinHelper.getSkin().themeColor
            mYearViewCurDayTextColor = SkinHelper.getSkin().themeColor
            mWeekBackground = SkinHelper.getSkin().themeColor
            mSelectedThemeColor = SkinHelper.getSkin().themeColor
        }
    }

    fun getSelectedStartRangeCalendar(): Calendar? {
        return mSelectedStartRangeCalendar
    }

    fun getSelectedEndRangeCalendar(): Calendar? {
        return mSelectedEndRangeCalendar
    }
}