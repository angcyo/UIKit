package com.angcyo.uiview.less.kotlin.dialog

import android.app.Dialog
import android.graphics.Color
import android.os.Build
import android.support.v4.app.Fragment
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

fun Fragment.buildBottomDialog(): RDialog.Builder {
    return RDialog.build(activity)
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

fun Fragment.normalDialog(config: NormalDialogConfig.() -> Unit): Dialog {
    val dialogConfig = NormalDialogConfig()
    dialogConfig.config()

    return configDialogBuilder(
        RDialog.build(activity)
            .setDialogWidth(-1),
        dialogConfig
    ).showCompatDialog()
}

fun Fragment.normalIosDialog(config: IosDialogConfig.() -> Unit): Dialog {
    val dialogConfig = IosDialogConfig()
    dialogConfig.config()

    return configDialogBuilder(
        RDialog.build(activity)
            .setDialogWidth(-1),
        dialogConfig
    ).showCompatDialog()
}

/**
 * 多选项, 选择对话框, 底部带 取消按钮, 标题栏不带取消
 * */
fun Fragment.itemsDialog(config: ItemDialogConfig.() -> Unit): Dialog {
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
fun Fragment.menuDialog(config: MenuDialogConfig.() -> Unit): Dialog {
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
fun Fragment.singleChoiceDialog(config: MenuDialogConfig.() -> Unit): Dialog {
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
fun Fragment.multiChoiceDialog(config: MenuDialogConfig.() -> Unit): Dialog {
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
fun Fragment.wheelDialog(config: WheelDialogConfig.() -> Unit): Dialog {
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
fun Fragment.inputDialog(config: InputDialogConfig.() -> Unit): Dialog {
    val dialogConfig = InputDialogConfig()
    dialogConfig.dialogCancel = false
    dialogConfig.dialogCanceledOnTouchOutside = false
    dialogConfig.config()

    return configDialogBuilder(
        buildBottomDialog(),
        dialogConfig
    ).showCompatDialog()
}

fun Fragment.multiInputDialog(config: InputDialogConfig.() -> Unit): Dialog {
    val dialogConfig = InputDialogConfig()
    dialogConfig.dialogCancel = false
    dialogConfig.dialogCanceledOnTouchOutside = false
    dialogConfig.maxInputLength = 2000
    dialogConfig.inputViewHeight = 100 * dpi
    dialogConfig.config()

    return configDialogBuilder(
        buildBottomDialog(),
        dialogConfig
    ).showCompatDialog()
}

/**
 * 展示一个popup window
 * */
fun Fragment.popupWindow(anchor: View? = null, config: PopupConfig.() -> Unit): PopupWindow {
    val popupConfig = PopupConfig()
    popupConfig.anchor = anchor
    popupConfig.config()

    val window = PopupWindow(context)

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
                LayoutInflater.from(context).inflate(popupConfig.layoutId, FrameLayout(context), false)
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