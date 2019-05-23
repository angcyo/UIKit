package com.angcyo.uiview.less.kotlin.dialog

import android.app.Dialog
import android.view.Gravity
import android.view.View
import com.angcyo.uiview.less.R
import com.angcyo.uiview.less.recycler.RBaseViewHolder
import com.angcyo.uiview.less.utils.RDialog.getCalendar
import com.bigkoo.pickerview.view.WheelTime
import com.contrarywind.view.WheelView
import java.text.ParseException
import java.util.*

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/05/22
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */

open class DateDialogConfig : BaseDialogConfig() {
    override var dialogLayoutId: Int = R.layout.dialog_date_picker_layout

    //time picker 年月日 时分秒
    var type = booleanArrayOf(true, true, true, false, false, false)//显示类型，默认显示： 年月日

    var textGravity = Gravity.CENTER

    var textSizeContent = 18//内容文字大小 dp

    var isLunarCalendar = false//是否显示农历

    var startYear: Int = 0//开始年份
    var endYear: Int = 0//结尾年份

    var date: Calendar? = null//当前选中时间
    var startDate: Calendar? = null//开始时间
    var endDate: Calendar? = null//终止时间

    var label_year: String? = null
    var label_month: String? = null
    var label_day: String? = null
    var label_hours: String? = null
    var label_minutes: String? = null
    var label_seconds: String? = null

    var x_offset_year: Int = 0
    var x_offset_month: Int = 0
    var x_offset_day: Int = 0
    var x_offset_hours: Int = 0
    var x_offset_minutes: Int = 0
    var x_offset_seconds: Int = 0

    var cyclic = false//是否循环

    var dividerColor = -0x2a2a2b //分割线的颜色
    var textColorOut = -0x575758 //分割线以外的文字颜色
    var textColorCenter = -0xd5d5d6 //分割线之间的文字颜色
    var isCenterLabel = true//是否只显示中间的label,默认每个item都显示

    var dividerType: WheelView.DividerType = WheelView.DividerType.FILL//分隔线类型

    var lineSpacingMultiplier = 1.6f // 条目间距倍数 默认1.6

    /**点击确定后回调*/
    var onDateSelectListener: (dialog: Dialog, date: Date) -> Boolean = { _, _ ->
        false
    }

    /**
     * 滚动的时候回调
     * */
    var onDateChangedListener: (dialog: Dialog, date: Date) -> Unit = { _, _ ->

    }

    init {
        positiveButtonListener = { dialog, _ ->
            if (onDateSelectListener.invoke(dialog, WheelTime.dateFormat.parse(wheelTime!!.time))) {
            } else {
                dialog.dismiss()
            }
        }
    }

    override fun onDialogInit(dialog: Dialog, dialogViewHolder: RBaseViewHolder) {
        super.onDialogInit(dialog, dialogViewHolder)

        initWheelTime(dialog, dialogViewHolder.view(R.id.time_picker))
    }

    fun setDate(time: String, pattern: String) {
        date = getCalendar(time, pattern)
    }

    private var wheelTime: WheelTime? = null //自定义控件
    private fun initWheelTime(dialog: Dialog, timePickerView: View) {
        wheelTime =
            WheelTime(timePickerView, type, textGravity, textSizeContent)

        wheelTime?.let { wheelTime ->

            wheelTime.setSelectChangeCallback {
                try {
                    val date = WheelTime.dateFormat.parse(wheelTime.time)
                    onDateChangedListener(dialog, date)
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
            }

            wheelTime.isLunarMode = isLunarCalendar

            if (startYear != 0 && endYear != 0
                && startYear <= endYear
            ) {
                setRange()
            }

            //若手动设置了时间范围限制
            if (startDate != null && endDate != null) {
                if (startDate!!.timeInMillis > endDate!!.timeInMillis) {
                    throw IllegalArgumentException("startDate can't be later than endDate")
                } else {
                    setRangDate()
                }
            } else if (startDate != null) {
                if (startDate!!.get(Calendar.YEAR) < 1900) {
                    throw IllegalArgumentException("The startDate can not as early as 1900")
                } else {
                    setRangDate()
                }
            } else if (endDate != null) {
                if (endDate!!.get(Calendar.YEAR) > 2100) {
                    throw IllegalArgumentException("The endDate should not be later than 2100")
                } else {
                    setRangDate()
                }
            } else {//没有设置时间范围限制，则会使用默认范围。
                setRangDate()
            }

            setTime()
            wheelTime.setLabels(
                label_year,
                label_month,
                label_day,
                label_hours,
                label_minutes,
                label_seconds
            )
            wheelTime.setTextXOffset(
                x_offset_year, x_offset_month, x_offset_day,
                x_offset_hours, x_offset_minutes, x_offset_seconds
            )

            wheelTime.setCyclic(cyclic)
            wheelTime.setDividerColor(dividerColor)
            wheelTime.setDividerType(dividerType)
            wheelTime.setLineSpacingMultiplier(lineSpacingMultiplier)
            wheelTime.setTextColorOut(textColorOut)
            wheelTime.setTextColorCenter(textColorCenter)
            wheelTime.isCenterLabel(isCenterLabel)

        }
    }

    /**
     * 设置可以选择的时间范围, 要在setTime之前调用才有效果
     */
    private fun setRange() {
        wheelTime?.let {
            it.startYear = startYear
            it.endYear = endYear
        }
    }

    /**
     * 设置可以选择的时间范围, 要在setTime之前调用才有效果
     */
    private fun setRangDate() {
        wheelTime!!.setRangDate(startDate, endDate)
        initDefaultSelectedDate()
    }

    private fun initDefaultSelectedDate() {
        //如果手动设置了时间范围
        if (startDate != null && endDate != null) {
            //若默认时间未设置，或者设置的默认时间越界了，则设置默认选中时间为开始时间。
            if (date == null || date!!.timeInMillis < startDate!!.timeInMillis
                || date!!.timeInMillis > endDate!!.timeInMillis
            ) {
                date = startDate
            }
        } else if (startDate != null) {
            //没有设置默认选中时间,那就拿开始时间当默认时间
            date = startDate
        } else if (endDate != null) {
            date = endDate
        }
    }

    /**
     * 设置选中时间,默认选中当前时间
     */
    private fun setTime() {
        val year: Int
        val month: Int
        val day: Int
        val hours: Int
        val minute: Int
        val seconds: Int
        val calendar = Calendar.getInstance()

        if (date == null) {
            calendar.timeInMillis = System.currentTimeMillis()
            year = calendar.get(Calendar.YEAR)
            month = calendar.get(Calendar.MONTH)
            day = calendar.get(Calendar.DAY_OF_MONTH)
            hours = calendar.get(Calendar.HOUR_OF_DAY)
            minute = calendar.get(Calendar.MINUTE)
            seconds = calendar.get(Calendar.SECOND)
        } else {
            year = date!!.get(Calendar.YEAR)
            month = date!!.get(Calendar.MONTH)
            day = date!!.get(Calendar.DAY_OF_MONTH)
            hours = date!!.get(Calendar.HOUR_OF_DAY)
            minute = date!!.get(Calendar.MINUTE)
            seconds = date!!.get(Calendar.SECOND)
        }

        wheelTime?.setPicker(year, month, day, hours, minute, seconds)
    }
}
