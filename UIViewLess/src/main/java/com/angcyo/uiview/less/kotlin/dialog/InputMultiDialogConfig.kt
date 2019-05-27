package com.angcyo.uiview.less.kotlin.dialog

import android.app.Dialog
import android.text.InputType
import android.view.Gravity
import android.view.View
import com.angcyo.uiview.less.R
import com.angcyo.uiview.less.kotlin.append
import com.angcyo.uiview.less.kotlin.childs
import com.angcyo.uiview.less.kotlin.find
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
open class InputMultiDialogConfig : BaseDialogConfig() {
    override var dialogLayoutId: Int = R.layout.dialog_input_multi_layout

    /**
     * 最大输入字符限制
     * */
    var maxInputLength = mutableListOf(0, 0)

    /**
     * 强制指定输入框的高度
     * */
    var inputViewHeight = mutableListOf(-1, -1)

    /**
     * 文本框hint文本
     */
    var hintInputString = mutableListOf<CharSequence>("请输入...", "请输入...")

    /**
     * 缺省的文本框内容, 同时也决定输入框的个数
     */
    var defaultInputString = mutableListOf<CharSequence>("", "")

    /**
     * 默认是否显示键盘
     * */
    var showSoftInput = true

    /**
     * 使用英文字符数过滤, 一个汉字等于2个英文, 一个emoji表情等于2个汉字
     */
    var useCharLengthFilter = mutableListOf(false, false)

    /**
     * 输入框内容回调, 返回 true, 则不会自动 调用 dismiss
     * */
    var onInputResult: (dialog: Dialog, inputTextList: MutableList<String>) -> Boolean = { _, _ ->
        false
    }

    /**文本输入类型*/
    var inputType = mutableListOf(InputType.TYPE_CLASS_TEXT, InputType.TYPE_CLASS_TEXT)

    init {
        positiveButtonListener = { dialog, dialogViewHolder ->
            val result = mutableListOf<String>()
            dialogViewHolder.group(R.id.input_wrapper_layout)?.childs { _, child ->
                result.add(child.find<ExEditText>(R.id.edit_text_view)?.string() ?: "")
            }

            if (onInputResult.invoke(dialog, result)) {

            } else {
                dialog.dismiss()
            }
        }
    }

    override fun onDialogInit(dialog: Dialog, dialogViewHolder: RBaseViewHolder) {
        super.onDialogInit(dialog, dialogViewHolder)

        dialogViewHolder.group(R.id.input_wrapper_layout)?.apply {
            defaultInputString.forEachIndexed { index, _ ->
                append(R.layout.dialog_input_multi_item) {
                    val editView = it.find<ExEditText>(R.id.edit_text_view)
                    val indicatorView = it.find<TextIndicator>(R.id.single_text_indicator_view)

                    if (useCharLengthFilter[index]) {
                        editView?.isUseCharLengthFilter = useCharLengthFilter[index]
                    }

                    if (maxInputLength[index] > 0) {
                        editView?.setMaxLength(maxInputLength[index])
                        indicatorView?.visibility = View.VISIBLE
                        indicatorView?.initIndicator(maxInputLength[index], editView)
                    }

                    if (inputViewHeight[index] > 0) {
                        UI.setViewHeight(editView, inputViewHeight[index])
                        editView?.gravity = Gravity.TOP
                    } else {
                        editView?.gravity = Gravity.CENTER_VERTICAL
                        editView?.setSingleLine(true)
                        editView?.maxLines = 1
                    }

                    editView?.inputType = inputType[index]
                    editView?.hint = hintInputString[index]
                    editView?.setInputText(defaultInputString[index])
                }
            }
        }

        if (showSoftInput) {
            dialogViewHolder.post { showSoftInput(dialogViewHolder.v(R.id.edit_text_view)) }
        }
    }

    private fun showSoftInput(view: View) {
        RSoftInputLayout.showSoftInput(view)
    }
}