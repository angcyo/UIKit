package com.angcyo.uiview.less.widget

import android.content.Context
import android.util.AttributeSet
import com.angcyo.uiview.less.kotlin.onFocusChange
import com.angcyo.uiview.less.recycler.adapter.RArrayAdapter

/**
 * Email:angcyo@126.com
 *
 * @author angcyo
 * @date 2019/06/06
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
open class AutoEditText : REditText {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun initEditText(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        super.initEditText(context, attrs, defStyleAttr)
    }

    /**
     * 输入过滤阈值
     */
    override fun getThreshold(): Int {
        return super.getThreshold()
    }

    override fun setThreshold(threshold: Int) {
        super.setThreshold(threshold)
    }

    override fun enoughToFilter(): Boolean {
        return super.enoughToFilter()
    }

    /**设置下拉数据源*/
    fun setDataList(list: List<CharSequence>, showOnFocus: Boolean = true) {
        setAdapter(RArrayAdapter(context, list))

        if (showOnFocus) {
            onFocusChange {
                if (it) {
                    showDropDown()
                }
            }
        }
    }
}
