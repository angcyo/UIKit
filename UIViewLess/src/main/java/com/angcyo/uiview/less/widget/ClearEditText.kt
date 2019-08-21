package com.angcyo.uiview.less.widget

import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import android.text.TextUtils
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.util.StateSet
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.angcyo.uiview.less.R
import com.angcyo.uiview.less.kotlin.dpi

/**
 * 带删除按钮的输入框
 * Email:angcyo@126.com
 *
 * @author angcyo
 * @date 2019/06/06
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
open class ClearEditText : AppCompatAutoCompleteTextView {

    private val STATE_NONE = StateSet.WILD_CARD
    private val STATE_CHECKED =
        intArrayOf(android.R.attr.state_checked, android.R.attr.state_pressed, android.R.attr.state_selected)

    /**
     * @see .onAttachedToWindow
     */
    var isAttached = false

    /**
     * 删除按钮区域
     */
    var clearRect = Rect()
    /**
     * 是否在 一键清空 按钮区域按下
     */
    var isDownIn = false

    /**
     * 是否显示删除按钮
     */
    var showClear = true

    /**
     * clear 按钮功能切换成, 显示/隐藏 密码.
     */
    var isPasswordDrawable = false

    /**
     * 隐藏显示密码, 在touch down一段时候后
     */
    var showPasswordOnTouch = false

    var clearDrawable: Drawable? = null
        get() {
            if (showClear && field == null) {
                field = ContextCompat.getDrawable(context, R.drawable.base_edit_delete_selector)

                if (compoundDrawablePadding == 0) {
                    compoundDrawablePadding = 4 * dpi
                }
            }
            return field
        }

    /**
     * 按下的时间
     */
    var downTime: Long = 0

    var updateStateRunnable: Runnable = Runnable { updateState(false, isDownIn) }

    /**
     * 当前密码, 是否可见
     */
    val isPasswordShow: Boolean
        get() = transformationMethod !is PasswordTransformationMethod

    constructor(context: Context) : super(context) {
        initEditText(context, null, 0)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initEditText(context, attrs, 0)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initEditText(context, attrs, defStyleAttr)
    }

    open fun initEditText(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        val tag = tag
        if (tag != null) {
            val tagString = tag.toString()

            if (tagString.contains("password")) {
                //隐藏显示密码
                showPasswordOnTouch = true
            }

            if (tagString.contains("hide")) {
                //隐藏删除按钮
                showClear = false
            } else if (tagString.contains("show")) {
                //显示删除按钮
                showClear = true
            }
        }

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ClearEditText)

        showClear = typedArray.getBoolean(R.styleable.ClearEditText_r_show_clear, showClear)
        clearDrawable = typedArray.getDrawable(R.styleable.ClearEditText_r_clear_drawable)
        isPasswordDrawable = typedArray.getBoolean(R.styleable.ClearEditText_r_is_password_drawable, isPasswordDrawable)
        showPasswordOnTouch =
            typedArray.getBoolean(R.styleable.ClearEditText_r_show_password_on_touch, showPasswordOnTouch)

        typedArray.recycle()

        clearDrawable
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        isAttached = false
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        isAttached = true
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        super.setText(text, type)
        //checkEdit(isFocused());
    }

    override fun onTextChanged(text: CharSequence?, start: Int, lengthBefore: Int, lengthAfter: Int) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
        checkEdit(isFocused)
    }

    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect)
        checkEdit(focused)
        if (!focused) {
            lastKeyCode = -1
        }
    }

    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val drawableState = super.onCreateDrawableState(extraSpace)
        if (isPasswordDrawable) {
            post(updateStateRunnable)
        }
        return drawableState
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (showClear) {
            val offset = (4 * resources.displayMetrics.density).toInt()
            clearRect.set(
                w - paddingRight - clearDrawable!!.intrinsicWidth - offset,
                paddingTop, w - paddingRight + offset, Math.min(w, h) - paddingBottom
            )
        }
    }

    fun addClearDrawable() {
        /*是否要显示删除按钮*/
        val clearDrawable = clearDrawable

        val compoundDrawables = compoundDrawables
        if (compoundDrawables[2] === clearDrawable) {

        } else {
            setCompoundDrawablesWithIntrinsicBounds(
                compoundDrawables[0],
                compoundDrawables[1],
                clearDrawable,
                compoundDrawables[3]
            )
        }
    }

    fun removeClearDrawable() {
        /*移除显示的删除按钮*/
        val compoundDrawables = compoundDrawables
        error = null
        setCompoundDrawablesWithIntrinsicBounds(compoundDrawables[0], compoundDrawables[1], null, compoundDrawables[3])

        if (isPasswordDrawable) {
            updateState(false, true)
        }
    }

    private fun updateState(fromTouch: Boolean, isDownIn: Boolean) {
        val clearDrawable = compoundDrawables[2] ?: return

        if (isPasswordDrawable) {
            if (fromTouch) {
            } else {
                if (isPasswordShow) {
                    clearDrawable.state = STATE_CHECKED
                } else {
                    clearDrawable.state = STATE_NONE
                }
            }
        } else {
            if (isDownIn) {
                clearDrawable.state = STATE_CHECKED
            } else {
                clearDrawable.state = STATE_NONE
            }
        }
    }

    protected fun updateState(isDownIn: Boolean) {
        updateState(true, isDownIn)
    }

    fun checkEdit(focused: Boolean) {
        /*是否要显示删除按钮*/
        if (showClear) {
            if (TextUtils.isEmpty(text) || !focused) {
                //文本为空, 或者 无焦点
                removeClearDrawable()
            } else {
                addClearDrawable()
            }
        }
    }

    protected fun checkClear(x: Float, y: Float): Boolean {
        return clearRect.contains(x.toInt(), y.toInt())
    }

    protected fun onClickClearDrawable(): Boolean {
        if (isPasswordDrawable) {
            if (isPasswordShow) {
                hidePassword()
            } else {
                showPassword()
            }
            updateState(false, true)
            postInvalidate()
            return true
        }

        if (!TextUtils.isEmpty(text)) {
            setText("")
            setSelection(0)
            return true
        }
        return false
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isEnabled) {
            return false
        }
        val action = event.action

        if (action == MotionEvent.ACTION_DOWN) {
            downTime = System.currentTimeMillis()
        }

        if (showClear && isFocused) {
            if (action == MotionEvent.ACTION_DOWN) {
                isDownIn = checkClear(event.x, event.y)
                updateState(isDownIn)
            } else if (action == MotionEvent.ACTION_MOVE) {
                updateState(checkClear(event.x, event.y))
            } else if (action == MotionEvent.ACTION_UP) {
                updateState(false)
                if (isDownIn && checkClear(event.x, event.y)) {
                    isDownIn = false

                    if (onClickClearDrawable()) {
                        return true
                    }
                }
                isDownIn = false
            } else if (action == MotionEvent.ACTION_CANCEL) {
                updateState(false)
                isDownIn = false
            }
        }

        if (showPasswordOnTouch) {
            if (action == MotionEvent.ACTION_DOWN) {
            } else if (action == MotionEvent.ACTION_MOVE) {
                if (System.currentTimeMillis() - downTime > 100) {
                    if (isDownIn) {
                        hidePassword()
                    } else {
                        showPassword()
                    }
                }
            } else if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
                hidePassword()
            }
        }

        return super.onTouchEvent(event)
    }

    private fun hasPasswordTransformation(): Boolean {
        return this.transformationMethod is PasswordTransformationMethod
    }

    fun showPassword() {
        val selection = selectionEnd
        transformationMethod = null
        setSelection(selection)
    }

    fun hidePassword() {
        val selection = selectionEnd
        transformationMethod = PasswordTransformationMethod.getInstance()
        setSelection(selection)
    }

    fun passwordVisibilityToggleRequested() {
        val selection = selectionEnd

        if (hasPasswordTransformation()) {
            transformationMethod = null
        } else {
            transformationMethod = PasswordTransformationMethod.getInstance()
        }

        // And restore the cursor position
        setSelection(selection)
    }

    private var lastKeyCode = -1
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        lastKeyCode = keyCode

        //L.i("${isEditSingleLine()} $maxLines $minLines")

        return super.onKeyDown(keyCode, event)
    }

    override fun hasOnClickListeners(): Boolean {
        //使用AutoCompleteTextView时, 会被默认设置onClickListener
        //这个时候, 输入法中的"下一步"触发的onKeyUp事件, 就会根据这个方法的返回值,
        //将焦点切换到下一个`EditText`.
        if (isEditSingleLine() && lastKeyCode == KeyEvent.KEYCODE_ENTER) {
            return false
        }
        return super.hasOnClickListeners()
    }

    fun isEditSingleLine(): Boolean {
        return inputType and EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE != EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE &&
                maxLines == 1 &&
                minLines == 1
    }
}
