package com.angcyo.uiview.less.kotlin.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.support.v4.app.Fragment
import android.text.InputType
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.PopupWindow
import com.angcyo.uiview.less.iview.ChoiceIView
import com.angcyo.uiview.less.kotlin.dpi
import com.angcyo.uiview.less.recycler.RBaseViewHolder
import com.angcyo.uiview.less.utils.RDialog

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/05/11
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
fun Context.buildBottomDialog(): RDialog.Builder {
    return RDialog.build(this)
        .setCanceledOnTouchOutside(false)
        .setDialogWidth(-1)
        .setDialogHeight(-2)
        .setDialogBgColor(Color.TRANSPARENT)
        .setDialogGravity(Gravity.BOTTOM)
}

private fun configDialogBuilder(builder: RDialog.Builder, dialogConfig: BaseDialogConfig): RDialog.Builder {
    builder.setCancelable(dialogConfig.dialogCancel)
        .setCanceledOnTouchOutside(dialogConfig.dialogCanceledOnTouchOutside)
        .setOnCancelListener {
            dialogConfig.onDialogCancel(it as Dialog)
            dialogConfig.onDialogCancel.invoke(it)
        }
        .setOnDismissListener {
            dialogConfig.onDialogDismiss(it as Dialog)
            dialogConfig.onDialogDismiss.invoke(it)
        }
        .setContentLayoutId(dialogConfig.dialogLayoutId)
        .setInitListener(object : RDialog.OnInitListener() {
            override fun onInitDialog(dialog: Dialog, dialogViewHolder: RBaseViewHolder) {
                dialogConfig.onDialogInit(dialog, dialogViewHolder)

                dialogConfig.dialogInit.invoke(dialog, dialogViewHolder)
            }
        })
    return builder
}

fun Context.normalDialog(config: NormalDialogConfig.() -> Unit): Dialog {
    val dialogConfig = NormalDialogConfig()
    dialogConfig.config()

    return configDialogBuilder(
        RDialog.build(this)
            .setDialogWidth(-1),
        dialogConfig
    ).showCompatDialog()
}

fun Context.normalIosDialog(config: IosDialogConfig.() -> Unit): Dialog {
    val dialogConfig = IosDialogConfig()
    dialogConfig.config()

    return configDialogBuilder(
        RDialog.build(this)
            .setDialogWidth(-1),
        dialogConfig
    ).showCompatDialog()
}

/**
 * 多选项, 选择对话框, 底部带 取消按钮, 标题栏不带取消
 * */
fun Context.itemsDialog(config: ItemDialogConfig.() -> Unit): Dialog {
    val dialogConfig = ItemDialogConfig()
    dialogConfig.config()

    return configDialogBuilder(
        buildBottomDialog(),
        dialogConfig
    ).showCompatDialog()
}

/**
 * 多选项, 菜单对话框, 底部不带取消按钮, 标题栏不带取消
 * */
fun Context.menuDialog(config: MenuDialogConfig.() -> Unit): Dialog {
    val dialogConfig = MenuDialogConfig()
    dialogConfig.config()

    return configDialogBuilder(
        buildBottomDialog(),
        dialogConfig
    ).showCompatDialog()
}

/**
 * 单选对话框, 底部不带取消按钮, 标题栏带取消和确定
 * */
fun Context.singleChoiceDialog(config: MenuDialogConfig.() -> Unit): Dialog {
    val dialogConfig = MenuDialogConfig()
    dialogConfig.choiceModel = ChoiceIView.CHOICE_MODE_SINGLE
    dialogConfig.dialogCancel = false
    dialogConfig.dialogCanceledOnTouchOutside = false
    dialogConfig.config()

    return configDialogBuilder(
        buildBottomDialog(),
        dialogConfig
    ).showCompatDialog()
}

/**
 * 多选对话框, 底部不带取消按钮, 标题栏带取消和确定
 * */
fun Context.multiChoiceDialog(config: MenuDialogConfig.() -> Unit): Dialog {
    val dialogConfig = MenuDialogConfig()
    dialogConfig.choiceModel = ChoiceIView.CHOICE_MODE_MULTI
    dialogConfig.dialogCancel = false
    dialogConfig.dialogCanceledOnTouchOutside = false
    dialogConfig.config()

    return configDialogBuilder(
        buildBottomDialog(),
        dialogConfig
    ).showCompatDialog()
}


/**
 * 3D滚轮选择对话框, 标题栏带取消和确定
 * */
fun Context.wheelDialog(config: WheelDialogConfig.() -> Unit): Dialog {
    val dialogConfig = WheelDialogConfig()
    dialogConfig.config()

    return configDialogBuilder(
        buildBottomDialog(),
        dialogConfig
    ).showCompatDialog()
}


/**
 * 文本输入对话框, 默认是单行, 无限制
 * */
fun Context.inputDialog(config: InputDialogConfig.() -> Unit): Dialog {
    val dialogConfig = InputDialogConfig()
    dialogConfig.dialogCancel = false
    dialogConfig.dialogCanceledOnTouchOutside = false
    dialogConfig.config()

    return configDialogBuilder(
        buildBottomDialog(),
        dialogConfig
    ).showCompatDialog()
}

/**多输入框*/
fun Context.inputMultiDialog(config: InputMultiDialogConfig.() -> Unit): Dialog {
    val dialogConfig = InputMultiDialogConfig()
    dialogConfig.dialogCancel = false
    dialogConfig.dialogCanceledOnTouchOutside = false
    dialogConfig.config()

    return configDialogBuilder(
        buildBottomDialog(),
        dialogConfig
    ).showCompatDialog()
}


/**
 * 多行文本输入框
 * */
fun Context.multiInputDialog(config: InputDialogConfig.() -> Unit): Dialog {
    val dialogConfig = InputDialogConfig()
    dialogConfig.dialogCancel = false
    dialogConfig.dialogCanceledOnTouchOutside = false
    dialogConfig.maxInputLength = 2000
    dialogConfig.inputViewHeight = 100 * dpi
    /**多行输入时, 需要 [InputType.TYPE_TEXT_FLAG_MULTI_LINE] 否则输入框, 不能输入 回车 */
    dialogConfig.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
    dialogConfig.config()

    return configDialogBuilder(
        buildBottomDialog(),
        dialogConfig
    ).showCompatDialog()
}

/**
 * 底部网格对话框
 * */
fun Context.gridDialog(config: GridDialogConfig.() -> Unit): Dialog {
    val dialogConfig = GridDialogConfig()
    dialogConfig.config()

    return configDialogBuilder(
        RDialog.build(this)
            .setDialogWidth(-1)
            .setDialogBgColor(Color.TRANSPARENT)
            .setDialogGravity(Gravity.BOTTOM),
        dialogConfig
    ).showCompatDialog()
}

/**
 * 展示一个popup window
 * */
fun Context.popupWindow(anchor: View? = null, config: PopupConfig.() -> Unit): PopupWindow {
    val popupConfig = PopupConfig()
    popupConfig.anchor = anchor
    popupConfig.config()

    val window = PopupWindow(this)

    window.apply {

        width = popupConfig.width
        height = popupConfig.height

        isFocusable = popupConfig.focusable
        isTouchable = popupConfig.touchable
        isOutsideTouchable = popupConfig.outsideTouchable
        setBackgroundDrawable(popupConfig.background)

        animationStyle = popupConfig.animationStyle

        setOnDismissListener {
            popupConfig.onDismiss(window)
        }

        if (popupConfig.layoutId != -1) {
            popupConfig.contentView =
                LayoutInflater.from(this@popupWindow)
                    .inflate(popupConfig.layoutId, FrameLayout(this@popupWindow), false)
        }
        val view = popupConfig.contentView

        popupConfig.popupViewHolder = RBaseViewHolder(view)

        popupConfig.onPopupInit(window, popupConfig.popupViewHolder!!)
        popupConfig.popupInit(window, popupConfig.popupViewHolder!!)

        contentView = view
    }

    if (popupConfig.parent != null) {
        window.showAtLocation(popupConfig.parent, popupConfig.gravity, popupConfig.xoff, popupConfig.yoff)
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        window.showAsDropDown(popupConfig.anchor, popupConfig.xoff, popupConfig.yoff, popupConfig.gravity)
    } else {
        window.showAsDropDown(popupConfig.anchor, popupConfig.xoff, popupConfig.yoff)
    }

    return window
}

/**
 * 日期选择
 * */
fun Context.dateDialog(config: DateDialogConfig.() -> Unit): Dialog {
    val dialogConfig = DateDialogConfig()
    dialogConfig.config()

    return configDialogBuilder(
        RDialog.build(this)
            .setDialogWidth(-1)
            .setDialogBgColor(Color.TRANSPARENT)
            .setDialogGravity(Gravity.BOTTOM),
        dialogConfig
    ).showCompatDialog()
}

// Fragment

fun Fragment.normalDialog(config: NormalDialogConfig.() -> Unit): Dialog {
    return context!!.normalDialog(config)
}

fun Fragment.normalIosDialog(config: IosDialogConfig.() -> Unit): Dialog {
    return context!!.normalIosDialog(config)
}

fun Fragment.itemsDialog(config: ItemDialogConfig.() -> Unit): Dialog {
    return context!!.itemsDialog(config)
}

fun Fragment.menuDialog(config: MenuDialogConfig.() -> Unit): Dialog {
    return context!!.menuDialog(config)
}

fun Fragment.singleChoiceDialog(config: MenuDialogConfig.() -> Unit): Dialog {
    return context!!.singleChoiceDialog(config)
}

fun Fragment.multiChoiceDialog(config: MenuDialogConfig.() -> Unit): Dialog {
    return context!!.multiChoiceDialog(config)
}

fun Fragment.wheelDialog(config: WheelDialogConfig.() -> Unit): Dialog {
    return context!!.wheelDialog(config)
}

fun Fragment.inputDialog(config: InputDialogConfig.() -> Unit): Dialog {
    return context!!.inputDialog(config)
}

fun Fragment.inputMultiDialog(config: InputMultiDialogConfig.() -> Unit): Dialog {
    return context!!.inputMultiDialog(config)
}

fun Fragment.multiInputDialog(config: InputDialogConfig.() -> Unit): Dialog {
    return context!!.multiInputDialog(config)
}

fun Fragment.gridDialog(config: GridDialogConfig.() -> Unit): Dialog {
    return context!!.gridDialog(config)
}

fun Fragment.popupWindow(anchor: View? = null, config: PopupConfig.() -> Unit): PopupWindow {
    return context!!.popupWindow(anchor, config)
}

fun Fragment.dateDialog(config: DateDialogConfig.() -> Unit): Dialog {
    return context!!.dateDialog(config)
}

// RBaseViewHolder

fun RBaseViewHolder.normalDialog(config: NormalDialogConfig.() -> Unit): Dialog {
    return context!!.normalDialog(config)
}

fun RBaseViewHolder.normalIosDialog(config: IosDialogConfig.() -> Unit): Dialog {
    return context!!.normalIosDialog(config)
}

fun RBaseViewHolder.itemsDialog(config: ItemDialogConfig.() -> Unit): Dialog {
    return context!!.itemsDialog(config)
}

fun RBaseViewHolder.menuDialog(config: MenuDialogConfig.() -> Unit): Dialog {
    return context!!.menuDialog(config)
}

fun RBaseViewHolder.singleChoiceDialog(config: MenuDialogConfig.() -> Unit): Dialog {
    return context!!.singleChoiceDialog(config)
}

fun RBaseViewHolder.multiChoiceDialog(config: MenuDialogConfig.() -> Unit): Dialog {
    return context!!.multiChoiceDialog(config)
}

fun RBaseViewHolder.wheelDialog(config: WheelDialogConfig.() -> Unit): Dialog {
    return context!!.wheelDialog(config)
}

fun RBaseViewHolder.inputDialog(config: InputDialogConfig.() -> Unit): Dialog {
    return context!!.inputDialog(config)
}

fun RBaseViewHolder.inputMultiDialog(config: InputMultiDialogConfig.() -> Unit): Dialog {
    return context!!.inputMultiDialog(config)
}

fun RBaseViewHolder.multiInputDialog(config: InputDialogConfig.() -> Unit): Dialog {
    return context!!.multiInputDialog(config)
}

fun RBaseViewHolder.gridDialog(config: GridDialogConfig.() -> Unit): Dialog {
    return context!!.gridDialog(config)
}

fun RBaseViewHolder.popupWindow(anchor: View? = null, config: PopupConfig.() -> Unit): PopupWindow {
    return context!!.popupWindow(anchor, config)
}

fun RBaseViewHolder.dateDialog(config: DateDialogConfig.() -> Unit): Dialog {
    return context!!.dateDialog(config)
}