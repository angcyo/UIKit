package com.angcyo.uiview.less.widget

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import com.angcyo.uiview.less.R
import com.angcyo.uiview.less.widget.ExEditText.hideSoftInputRunnable

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/06/06
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
open class REditText : ClearEditText {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    /**
     * 是否是不可编辑模式
     */
    var isNoEditMode = false

    /**
     * 是否只有在 touch 事件的时候, 才可以请求焦点. 防止在列表中,自动获取焦点的情况
     */
    var requestFocusOnTouch = false

    /**
     * 当失去焦点时, 是否隐藏键盘
     */
    var autoHideSoftInputOnLostFocus = false
    /**
     * 当onDetachedFromWindow时, 是否隐藏键盘
     */
    var autoHideSoftInputOnDetached = false

    override fun initEditText(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        super.initEditText(context, attrs, defStyleAttr)

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.REditText)

        isNoEditMode = typedArray.getBoolean(R.styleable.REditText_r_is_no_edit_mode, isNoEditMode)
        requestFocusOnTouch =
            typedArray.getBoolean(R.styleable.REditText_r_request_focus_on_touch, requestFocusOnTouch)

        autoHideSoftInputOnLostFocus = typedArray.getBoolean(
            R.styleable.REditText_r_auto_hide_soft_input_on_lost_focus,
            autoHideSoftInputOnLostFocus
        )
        autoHideSoftInputOnDetached = typedArray.getBoolean(
            R.styleable.REditText_r_auto_hide_soft_input_on_detached,
            autoHideSoftInputOnDetached
        )

        typedArray.recycle()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isNoEditMode || !isEnabled) {
            return false
        }
        return super.onTouchEvent(event)
    }

    override fun requestFocus(direction: Int, previouslyFocusedRect: Rect?): Boolean {
        if (isNoEditMode) {
            return false
        }
        if (requestFocusOnTouch) {
            if (System.currentTimeMillis() - downTime > 160) {
                return false
            }
        }
        return super.requestFocus(direction, previouslyFocusedRect)
    }

    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect)

        if (focused) {
            hideSoftInputRunnable?.remove()
        } else {

            if (autoHideSoftInputOnLostFocus) {
                hideSoftInputRunnable?.remove()
                hideSoftInputRunnable = ExEditText.HideSoftInputRunnable(this)
                postDelayed(hideSoftInputRunnable, 60)
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        isAttached = false
        if (autoHideSoftInputOnDetached) {
            ExEditText.HideSoftInputRunnable(this).run()
        }
    }
}