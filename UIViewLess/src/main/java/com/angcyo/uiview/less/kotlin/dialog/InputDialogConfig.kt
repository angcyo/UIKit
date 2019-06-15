package com.angcyo.uiview.less.kotlin.dialog

import android.app.Dialog
import android.text.InputFilter
import android.text.InputType
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import com.angcyo.uiview.less.R
import com.angcyo.uiview.less.kotlin.onEmptyText
import com.angcyo.uiview.less.recycler.RBaseViewHolder
import com.angcyo.uiview.less.utils.UI
import com.angcyo.uiview.less.widget.ExEditText
import com.angcyo.uiview.less.widget.group.RSoftInputLayout
import com.angcyo.uiview.less.widget.pager.TextIndicator

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/05/11
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
open class InputDialogConfig : BaseDialogConfig() {
    override var dialogLayoutId: Int = R.layout.dialog_input_layout

    /**
     * 最大输入字符限制
     * */
    var maxInputLength = 0

    /**
     * 强制指定输入框的高度
     * */
    var inputViewHeight = -1

    /**
     * 文本框hint文本
     */
    var hintInputString = "请输入..."

    /**
     * 缺省的文本框内容
     */
    var defaultInputString = ""

    /**
     * 默认是否显示键盘
     * */
    var showSoftInput = true

    /**
     * 是否允许输入为空
     */
    var canInputEmpty = true

    /**
     * 使用英文字符数过滤, 一个汉字等于2个英文, 一个emoji表情等于2个汉字
     */
    var useCharLengthFilter = false

    /**
     * 输入框内容回调, 返回 true, 则不会自动 调用 dismiss
     * */
    var onInputResult: (dialog: Dialog, inputText: CharSequence) -> Boolean = { _, _ ->
        false
    }

    /**文本输入类型*/
    var inputType = InputType.TYPE_CLASS_TEXT

    /**输入框过滤器*/
    var inputFilterList = mutableListOf<InputFilter>()

    init {
        positiveButtonListener = { dialog, dialogViewHolder ->
            if (onInputResult.invoke(dialog, dialogViewHolder.exV(R.id.edit_text_view).string())) {

            } else {
                dialog.dismiss()
            }
        }
    }

    override fun onDialogInit(dialog: Dialog, dialogViewHolder: RBaseViewHolder) {
        super.onDialogInit(dialog, dialogViewHolder)

        val editView = dialogViewHolder.v<ExEditText>(R.id.edit_text_view)
        val indicatorView = dialogViewHolder.v<TextIndicator>(R.id.single_text_indicator_view)

        if (!canInputEmpty) {
            editView.onEmptyText { aBoolean ->
                dialogViewHolder.enable(R.id.positive_button, (!aBoolean))
                null
            }
            dialogViewHolder.enable(R.id.base_save_button, !TextUtils.isEmpty(defaultInputString))
        }

        //过滤器
        inputFilterList.forEach {
            editView.addFilter(it)
        }

        if (useCharLengthFilter) {
            editView.isUseCharLengthFilter = useCharLengthFilter
        }

        if (maxInputLength > 0) {
            editView.setMaxLength(maxInputLength)
            indicatorView.visibility = View.VISIBLE
            indicatorView.initIndicator(maxInputLength, editView)
        }

        if (inputViewHeight > 0) {
            UI.setViewHeight(editView, inputViewHeight)
            editView.gravity = Gravity.TOP
        } else {
            editView.gravity = Gravity.CENTER_VERTICAL
            editView.setSingleLine(true)
            editView.maxLines = 1
        }

        editView.inputType = inputType
        editView.hint = hintInputString
        editView.setInputText(defaultInputString)

        if (showSoftInput) {
            dialogViewHolder.post { showSoftInput(editView) }
        }
    }

    private fun showSoftInput(view: View) {
        RSoftInputLayout.showSoftInput(view)
    }
}