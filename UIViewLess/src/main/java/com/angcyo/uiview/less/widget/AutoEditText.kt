package com.angcyo.uiview.less.widget

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import com.angcyo.uiview.less.R
import com.angcyo.uiview.less.kotlin.onFocusChange
import com.angcyo.uiview.less.recycler.adapter.RArrayAdapter
import com.angcyo.uiview.less.utils.RUtils

/**
 * Email:angcyo@126.com
 *
 * @author angcyo
 * @date 2019/06/06
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
open class AutoEditText : REditText {

    var autoCompleteText: String? = null
    var autoCompleteTextSeparator = "\\|"
    var autoCompleteShowOnFocus = false
    var autoCompleteFocusDelay = 0

    constructor(context: Context) : super(context) {
        initAutoEditText(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initAutoEditText(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initAutoEditText(context, attrs)
    }

    override fun initEditText(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        super.initEditText(context, attrs, defStyleAttr)

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.AutoEditText)

        autoCompleteText = typedArray.getString(R.styleable.AutoEditText_r_auto_complete_text)

        typedArray.getString(R.styleable.AutoEditText_r_auto_complete_text_separator)?.also {
            if (!TextUtils.isEmpty(it)) {
                autoCompleteTextSeparator = it
            }
        }

        autoCompleteShowOnFocus =
            typedArray.getBoolean(R.styleable.AutoEditText_r_auto_complete_show_on_focus, autoCompleteShowOnFocus)
        autoCompleteFocusDelay =
            typedArray.getInt(R.styleable.AutoEditText_r_auto_complete_focus_delay, autoCompleteFocusDelay)

        typedArray.recycle()
    }

    /**单独开一个方法, 是因为 [initEditText] 是在 [super] 里面触发的, 这个时候, 此类的成员变量都还没有初始化...*/
    private fun initAutoEditText(context: Context, attrs: AttributeSet?) {
        resetAutoCompleteTextAdatper()
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

    fun resetAutoCompleteTextAdatper() {
        if (!TextUtils.isEmpty(autoCompleteText)) {
            setDataList(
                RUtils.split(autoCompleteText, autoCompleteTextSeparator, false),
                autoCompleteShowOnFocus, autoCompleteFocusDelay.toLong()
            )
        }
    }

    /**设置下拉数据源*/
    fun setDataList(list: List<CharSequence>, showOnFocus: Boolean = true, focusDelay: Long = 0L) {
        setAdapter(RArrayAdapter(context, list))

        if (showOnFocus) {
            onFocusChange {
                if (it) {
                    if (focusDelay > 0) {
                        postDelayed({
                            showDropDown()
                        }, focusDelay)
                    } else {
                        showDropDown()
                    }
                }
            }
        }
    }
}
