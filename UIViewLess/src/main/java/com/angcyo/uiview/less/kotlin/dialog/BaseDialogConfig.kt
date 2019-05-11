package com.angcyo.uiview.less.kotlin.dialog

import android.app.Dialog
import android.view.View
import com.angcyo.lib.L
import com.angcyo.uiview.less.R
import com.angcyo.uiview.less.kotlin.clickIt
import com.angcyo.uiview.less.recycler.RBaseViewHolder

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/05/11
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
abstract class BaseDialogConfig {

    open var dialogLayoutId = R.layout.dialog_normal_layout

    var dialogCancel = true

    var dialogCanceledOnTouchOutside = true

    /**
     * 对话框的标题, 为null时, 标题栏会被 GONE
     * */
    var dialogTitle: CharSequence? = null

    /**
     * 对话框的消息内容, 为null时, 会被 GONE
     * */
    var dialogMessage: CharSequence? = null

    /**
     * 中立按钮文本, 为null时, 会被 GONE
     * */
    var neutralButtonText: CharSequence? = null
    var neutralButtonListener: (dialog: Dialog, dialogViewHolder: RBaseViewHolder) -> Unit = { _, _ -> }

    open fun neutralButton(
        text: CharSequence? = neutralButtonText,
        listener: (dialog: Dialog, dialogViewHolder: RBaseViewHolder) -> Unit
    ) {
        neutralButtonText = text
        neutralButtonListener = listener
    }

    /**
     * 取消按钮文本, 为null时, 会被 GONE
     * */
    var negativeButtonText: CharSequence? = "取消"
    var negativeButtonListener: (dialog: Dialog, dialogViewHolder: RBaseViewHolder) -> Unit =
        { dialog, _ -> dialog.cancel() }

    open fun negativeButton(
        text: CharSequence? = negativeButtonText,
        listener: (dialog: Dialog, dialogViewHolder: RBaseViewHolder) -> Unit
    ) {
        negativeButtonText = text
        negativeButtonListener = listener
    }

    /**
     * 确定按钮文本, 为null时, 会被 GONE
     * */
    var positiveButtonText: CharSequence? = "确定"
    var positiveButtonListener: (dialog: Dialog, dialogViewHolder: RBaseViewHolder) -> Unit =
        { dialog, _ -> dialog.dismiss() }

    open fun positiveButton(
        text: CharSequence? = positiveButtonText,
        listener: (dialog: Dialog, dialogViewHolder: RBaseViewHolder) -> Unit
    ) {
        positiveButtonText = text
        positiveButtonListener = listener
    }

    /**
     * 初始化回调方法
     * */
    var dialogInit: (dialog: Dialog, dialogViewHolder: RBaseViewHolder) -> Unit = { _, _ -> }

    /**
     * 对话框初始化方法
     * */
    open fun onDialogInit(dialog: Dialog, dialogViewHolder: RBaseViewHolder) {

        //标题
        dialogViewHolder.tv(R.id.title_view)?.apply {
            visibility = if (dialogTitle == null) View.GONE else View.VISIBLE
            text = dialogTitle
        }

        //消息体
        dialogViewHolder.tv(R.id.message_view)?.apply {
            visibility = if (dialogMessage == null) View.GONE else View.VISIBLE
            text = dialogMessage
        }

        initControlLayout(dialog, dialogViewHolder)
    }

    open fun initControlLayout(dialog: Dialog, dialogViewHolder: RBaseViewHolder) {
        //确定按钮
        dialogViewHolder.tv(R.id.positive_button)?.apply {
            visibility = if (positiveButtonText == null) View.GONE else View.VISIBLE
            text = positiveButtonText

            clickIt {
                positiveButtonListener.invoke(dialog, dialogViewHolder)
            }
        }

        //取消按钮
        dialogViewHolder.tv(R.id.negative_button)?.apply {
            visibility = if (negativeButtonText == null) View.GONE else View.VISIBLE
            text = negativeButtonText

            clickIt {
                negativeButtonListener.invoke(dialog, dialogViewHolder)
            }
        }

        //中立按钮
        dialogViewHolder.tv(R.id.neutral_button)?.apply {
            visibility = if (neutralButtonText == null) View.GONE else View.VISIBLE
            text = neutralButtonText

            clickIt {
                neutralButtonListener.invoke(dialog, dialogViewHolder)
            }
        }

        //3个按钮都没有文本, 隐藏底部控制栏
        if (positiveButtonText == null &&
            negativeButtonText == null &&
            neutralButtonText == null
        ) {
            dialogViewHolder.tv(R.id.control_layout)?.visibility = View.GONE
        }
    }


    /**
     * 可以设置的监听回调
     * */
    var onDialogCancel: (dialog: Dialog) -> Unit = {}
    var onDialogDismiss: (dialog: Dialog) -> Unit = {}

    /**
     * 当调用dialog.cancel时, 此方法会回调, 并且 onDialogDismiss 也会回调
     * */
    open fun onDialogCancel(dialog: Dialog) {
        L.d("onDialogCancel")
    }

    /**
     * 当调用dialog.dismiss时, 此方法会回调, 并且 onDialogCancel 不会回调
     * */
    open fun onDialogDismiss(dialog: Dialog) {
        L.d("onDialogDismiss")
    }

}