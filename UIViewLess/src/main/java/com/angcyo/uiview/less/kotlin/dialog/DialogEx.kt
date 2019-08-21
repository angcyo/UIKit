package com.angcyo.uiview.less.kotlin.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.fragment.app.Fragment
import android.text.InputType
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.PopupWindow
import com.angcyo.uiview.less.base.helper.TitleItemHelper.NO_NUM
import com.angcyo.uiview.less.iview.ChoiceIView
import com.angcyo.uiview.less.kotlin.dpi
import com.angcyo.uiview.less.kotlin.getViewRect
import com.angcyo.uiview.less.recycler.RBaseViewHolder
import com.angcyo.uiview.less.utils.RDialog
import com.angcyo.uiview.less.utils.RUtils
import kotlin.math.max

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

public fun configDialogBuilder(builder: RDialog.Builder, dialogConfig: BaseDialogConfig): RDialog.Builder {
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
        .setWindowFeature(dialogConfig.windowFeature)
        .setWindowFlags(dialogConfig.windowFlags)

    dialogConfig.dialogBgDrawable?.let {
        builder.setDialogBgDrawable(it)
    }

    if (dialogConfig.dialogWidth != NO_NUM) {
        builder.setDialogWidth(dialogConfig.dialogWidth)
    }
    if (dialogConfig.dialogHeight != NO_NUM) {
        builder.setDialogHeight(dialogConfig.dialogHeight)
    }
    return builder
}

public fun RDialog.Builder.show(dialogConfig: BaseDialogConfig): Dialog {
    val builder = configDialogBuilder(this, dialogConfig)

    return when (dialogConfig.dialogType) {
        BaseDialogConfig.DIALOG_TYPE_ALERT_DIALOG -> builder.showAlertDialog()
        BaseDialogConfig.DIALOG_TYPE_BOTTOM_SHEET_DIALOG -> builder.showSheetDialog()
        else -> builder.showCompatDialog()
    }
}

fun Context.normalDialog(config: NormalDialogConfig.() -> Unit): Dialog {
    val dialogConfig = NormalDialogConfig()
    dialogConfig.config()

    return RDialog.build(this)
        .setDialogWidth(-1).show(dialogConfig)
}

fun Context.normalIosDialog(config: IosDialogConfig.() -> Unit): Dialog {
    val dialogConfig = IosDialogConfig()
    dialogConfig.config()

    return RDialog.build(this)
        .setDialogWidth(-1).show(dialogConfig)
}

/**
 * 多选项, 选择对话框, 底部带 取消按钮, 标题栏不带取消
 * */
fun Context.itemsDialog(config: ItemDialogConfig.() -> Unit): Dialog {
    val dialogConfig = ItemDialogConfig()
    dialogConfig.config()

    return buildBottomDialog().show(dialogConfig)
}

/**
 * 多选项, 菜单对话框, 底部不带取消按钮, 标题栏不带取消
 * */
fun Context.menuDialog(config: MenuDialogConfig.() -> Unit): Dialog {
    val dialogConfig = MenuDialogConfig()
    dialogConfig.config()

    return buildBottomDialog().show(dialogConfig)
}

/**
 * 单选对话框, 底部不带取消按钮, 标题栏带取消和确定
 * */
fun Context.singleChoiceDialog(config: MenuDialogConfig.() -> Unit): Dialog {
    val dialogConfig = MenuDialogConfig()
    dialogConfig.choiceModel = ChoiceIView.CHOICE_MODE_SINGLE
    dialogConfig.dialogCanceledOnTouchOutside = false
    dialogConfig.config()

    return buildBottomDialog().show(dialogConfig)
}

/**
 * 多选对话框, 底部不带取消按钮, 标题栏带取消和确定
 * */
fun Context.multiChoiceDialog(config: MenuDialogConfig.() -> Unit): Dialog {
    val dialogConfig = MenuDialogConfig()
    dialogConfig.choiceModel = ChoiceIView.CHOICE_MODE_MULTI
    dialogConfig.dialogCanceledOnTouchOutside = false
    dialogConfig.config()

    return buildBottomDialog().show(dialogConfig)
}


/**
 * 3D滚轮选择对话框, 标题栏带取消和确定
 * */
fun Context.wheelDialog(config: WheelDialogConfig.() -> Unit): Dialog {
    val dialogConfig = WheelDialogConfig()
    dialogConfig.config()

    return buildBottomDialog().show(dialogConfig)
}


/**
 * 文本输入对话框, 默认是单行, 无限制
 * */
fun Context.inputDialog(config: InputDialogConfig.() -> Unit): Dialog {
    val dialogConfig = InputDialogConfig()
    dialogConfig.dialogCanceledOnTouchOutside = false
    dialogConfig.config()

    return buildBottomDialog().show(dialogConfig)
}

/**多输入框*/
fun Context.inputMultiDialog(config: InputMultiDialogConfig.() -> Unit): Dialog {
    val dialogConfig = InputMultiDialogConfig()
    dialogConfig.dialogCanceledOnTouchOutside = false
    dialogConfig.config()

    return buildBottomDialog().show(dialogConfig)
}


/**
 * 多行文本输入框
 * */
fun Context.multiInputDialog(config: InputDialogConfig.() -> Unit): Dialog {
    val dialogConfig = InputDialogConfig()
    dialogConfig.dialogCanceledOnTouchOutside = false
    dialogConfig.maxInputLength = 2000
    dialogConfig.inputViewHeight = 100 * dpi
    /**多行输入时, 需要 [InputType.TYPE_TEXT_FLAG_MULTI_LINE] 否则输入框, 不能输入 回车 */
    dialogConfig.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
    dialogConfig.config()

    return buildBottomDialog().show(dialogConfig)
}

/**
 * 底部网格对话框
 * */
fun Context.gridDialog(config: GridDialogConfig.() -> Unit): Dialog {
    val dialogConfig = GridDialogConfig()
    dialogConfig.config()

    return buildBottomDialog().show(dialogConfig)
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

        popupConfig.anchor?.let {
            val viewRect = it.getViewRect()
            if (popupConfig.exactlyHeight) {
                height = max(RUtils.getContentViewHeight(it.context), RUtils.getScreenHeight()) - viewRect.bottom
            }

            if (viewRect.bottom >= RUtils.getScreenHeight()) {
                //接近屏幕底部
                if (popupConfig.gravity == Gravity.NO_GRAVITY) {
                    //手动控制无效
                    //popupConfig.gravity = Gravity.TOP

                    if (popupConfig.exactlyHeight) {
                        height = viewRect.top
                    }
                }
            }
        }

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

    return buildBottomDialog().show(dialogConfig)
}

/**
 * 多级选项对话框
 * */
fun Context.optionDialog(config: OptionDialogConfig.() -> Unit): Dialog {
    val dialogConfig = OptionDialogConfig()
    dialogConfig.config()

    return buildBottomDialog().show(dialogConfig)
}

/**
 * 日历选择对话框
 * */
fun Context.calendarDialog(config: CalendarDialogConfig.() -> Unit): Dialog {
    val dialogConfig = CalendarDialogConfig()
    dialogConfig.config()

    return buildBottomDialog().show(dialogConfig)
}

fun <T : BaseDialogConfig> Context.customBottomDialog(customConfig: T, config: T.() -> Unit): Dialog {
    customConfig.config()

    return buildBottomDialog().show(customConfig)
}

// Fragment

fun androidx.fragment.app.Fragment.normalDialog(config: NormalDialogConfig.() -> Unit): Dialog {
    return context!!.normalDialog(config)
}

fun androidx.fragment.app.Fragment.normalIosDialog(config: IosDialogConfig.() -> Unit): Dialog {
    return context!!.normalIosDialog(config)
}

fun androidx.fragment.app.Fragment.itemsDialog(config: ItemDialogConfig.() -> Unit): Dialog {
    return context!!.itemsDialog(config)
}

fun androidx.fragment.app.Fragment.menuDialog(config: MenuDialogConfig.() -> Unit): Dialog {
    return context!!.menuDialog(config)
}

fun androidx.fragment.app.Fragment.singleChoiceDialog(config: MenuDialogConfig.() -> Unit): Dialog {
    return context!!.singleChoiceDialog(config)
}

fun androidx.fragment.app.Fragment.multiChoiceDialog(config: MenuDialogConfig.() -> Unit): Dialog {
    return context!!.multiChoiceDialog(config)
}

fun androidx.fragment.app.Fragment.wheelDialog(config: WheelDialogConfig.() -> Unit): Dialog {
    return context!!.wheelDialog(config)
}

fun androidx.fragment.app.Fragment.inputDialog(config: InputDialogConfig.() -> Unit): Dialog {
    return context!!.inputDialog(config)
}

fun androidx.fragment.app.Fragment.inputMultiDialog(config: InputMultiDialogConfig.() -> Unit): Dialog {
    return context!!.inputMultiDialog(config)
}

fun androidx.fragment.app.Fragment.multiInputDialog(config: InputDialogConfig.() -> Unit): Dialog {
    return context!!.multiInputDialog(config)
}

fun androidx.fragment.app.Fragment.gridDialog(config: GridDialogConfig.() -> Unit): Dialog {
    return context!!.gridDialog(config)
}

fun androidx.fragment.app.Fragment.popupWindow(anchor: View? = null, config: PopupConfig.() -> Unit): PopupWindow {
    return context!!.popupWindow(anchor, config)
}

fun androidx.fragment.app.Fragment.dateDialog(config: DateDialogConfig.() -> Unit): Dialog {
    return context!!.dateDialog(config)
}

fun androidx.fragment.app.Fragment.optionDialog(config: OptionDialogConfig.() -> Unit): Dialog {
    return context!!.optionDialog(config)
}

fun <T : BaseDialogConfig> androidx.fragment.app.Fragment.customBottomDialog(customConfig: T, config: T.() -> Unit): Dialog {
    return context!!.customBottomDialog(customConfig, config)
}

fun androidx.fragment.app.Fragment.calendarDialog(config: CalendarDialogConfig.() -> Unit): Dialog {
    return context!!.calendarDialog(config)
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

fun RBaseViewHolder.optionDialog(config: OptionDialogConfig.() -> Unit): Dialog {
    return context!!.optionDialog(config)
}

fun <T : BaseDialogConfig> RBaseViewHolder.customBottomDialog(customConfig: T, config: T.() -> Unit): Dialog {
    return context!!.customBottomDialog(customConfig, config)
}

fun RBaseViewHolder.calendarDialog(config: CalendarDialogConfig.() -> Unit): Dialog {
    return context!!.calendarDialog(config)
}

