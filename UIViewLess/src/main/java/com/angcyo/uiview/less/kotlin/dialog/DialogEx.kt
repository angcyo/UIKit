package com.angcyo.uiview.less.kotlin.dialog

import android.app.Dialog
import android.graphics.Color
import android.view.Gravity
import com.angcyo.uiview.less.base.BaseFragment
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

public fun BaseFragment.buildBottomDialog(): RDialog.Builder {
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

public fun BaseFragment.normalDialog(config: NormalDialogConfig.() -> Unit) {
    val dialogConfig = NormalDialogConfig()
    dialogConfig.config()

    configDialogBuilder(
        RDialog.build(activity)
            .setDialogWidth(-1),
        dialogConfig
    ).showCompatDialog()
}

public fun BaseFragment.normalIosDialog(config: IosDialogConfig.() -> Unit) {
    val dialogConfig = IosDialogConfig()
    dialogConfig.config()

    configDialogBuilder(
        RDialog.build(activity)
            .setDialogWidth(-1),
        dialogConfig
    ).showCompatDialog()
}

/**
 * 多选项, 选择对话框, 底部带 取消按钮, 标题栏不带取消
 * */
public fun BaseFragment.itemsDialog(config: ItemDialogConfig.() -> Unit) {
    val dialogConfig = ItemDialogConfig()
    dialogConfig.config()

    configDialogBuilder(
        buildBottomDialog(),
        dialogConfig
    ).showCompatDialog()
}

/**
 * 多选项, 菜单对话框, 底部不带取消按钮, 标题栏不带取消
 * */
public fun BaseFragment.menuDialog(config: MenuDialogConfig.() -> Unit) {
    val dialogConfig = MenuDialogConfig()
    dialogConfig.config()

    configDialogBuilder(
        buildBottomDialog(),
        dialogConfig
    ).showCompatDialog()
}

/**
 * 单选对话框, 底部不带取消按钮, 标题栏带取消和确定
 * */
public fun BaseFragment.singleChoiceDialog(config: MenuDialogConfig.() -> Unit) {
    val dialogConfig = MenuDialogConfig()
    dialogConfig.choiceModel = ChoiceIView.CHOICE_MODE_SINGLE
    dialogConfig.dialogCancel = false
    dialogConfig.dialogCanceledOnTouchOutside = false
    dialogConfig.config()

    configDialogBuilder(
        buildBottomDialog(),
        dialogConfig
    ).showCompatDialog()
}

/**
 * 多选对话框, 底部不带取消按钮, 标题栏带取消和确定
 * */
public fun BaseFragment.multiChoiceDialog(config: MenuDialogConfig.() -> Unit) {
    val dialogConfig = MenuDialogConfig()
    dialogConfig.choiceModel = ChoiceIView.CHOICE_MODE_MULTI
    dialogConfig.dialogCancel = false
    dialogConfig.dialogCanceledOnTouchOutside = false
    dialogConfig.config()

    configDialogBuilder(
        buildBottomDialog(),
        dialogConfig
    ).showCompatDialog()
}


/**
 * 3D滚轮选择对话框, 标题栏带取消和确定
 * */
public fun BaseFragment.wheelDialog(config: WheelDialogConfig.() -> Unit) {
    val dialogConfig = WheelDialogConfig()
    dialogConfig.config()

    configDialogBuilder(
        buildBottomDialog(),
        dialogConfig
    ).showCompatDialog()
}


/**
 * 文本输入对话框, 默认是单行, 无限制
 * */
public fun BaseFragment.inputDialog(config: InputDialogConfig.() -> Unit) {
    val dialogConfig = InputDialogConfig()
    dialogConfig.dialogCancel = false
    dialogConfig.dialogCanceledOnTouchOutside = false
    dialogConfig.config()

    configDialogBuilder(
        buildBottomDialog(),
        dialogConfig
    ).showCompatDialog()
}

public fun BaseFragment.multiInputDialog(config: InputDialogConfig.() -> Unit) {
    val dialogConfig = InputDialogConfig()
    dialogConfig.dialogCancel = false
    dialogConfig.dialogCanceledOnTouchOutside = false
    dialogConfig.maxInputLength = 2000
    dialogConfig.inputViewHeight = 100 * dpi
    dialogConfig.config()

    configDialogBuilder(
        buildBottomDialog(),
        dialogConfig
    ).showCompatDialog()
}