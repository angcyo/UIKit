package com.angcyo.uiview.less.recycler.dslitem

import android.text.TextUtils
import android.view.Gravity
import androidx.annotation.LayoutRes
import com.angcyo.uiview.less.R
import com.angcyo.uiview.less.base.BaseFragment
import com.angcyo.uiview.less.component.FileSelectorFragment
import com.angcyo.uiview.less.kotlin.*
import com.angcyo.uiview.less.recycler.adapter.DslAdapter
import com.angcyo.uiview.less.recycler.adapter.DslAdapterItem
import com.angcyo.uiview.less.utils.RUtils
import com.angcyo.uiview.less.utils.Root
import com.angcyo.uiview.less.utils.Tip

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/08/09
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */

/**通过[itemTag]查找指定的[DslAdapterItem]*/
public fun DslAdapter.findItemByTag(itemTag: String?): DslAdapterItem? {
    if (TextUtils.isEmpty(itemTag)) {
        return null
    }

    var result: DslAdapterItem? = null
    getValidFilterDataList().forEach {
        if (it.itemTag == itemTag) {
            result = it
            return@forEach
        }
    }

    return result
}

/**通过[itemTag]查找指定的[DslAdapterItem], 并且更新对应的[DslAdapterItem]*/
public fun DslAdapter.updateItemByTag(
    itemTag: String?,
    config: DslAdapterItem.() -> Unit = {}
): DslAdapterItem? {
    if (TextUtils.isEmpty(itemTag)) {
        return null
    }

    var result: DslAdapterItem? = null
    getValidFilterDataList().forEachIndexed { index, dslAdapterItem ->
        if (dslAdapterItem.itemTag == itemTag) {
            result = dslAdapterItem
            dslAdapterItem.config()

            notifyItemChanged(index)
            return@forEachIndexed
        }
    }
    return result
}

public fun DslAdapter.dslItem(@LayoutRes layoutId: Int, config: DslAdapterItem.() -> Unit = {}) {
    val item = DslAdapterItem()
    item.itemLayoutId = layoutId
    addLastItem(item)
    item.config()
}

public fun <T : DslAdapterItem> DslAdapter.dslCustomItem(
    dslItem: T,
    config: T.() -> Unit = {}
) {
    addLastItem(dslItem)
    dslItem.config()
}

/**简单的单行文本*/
public fun DslAdapter.dslBaseInfoItem(config: DslBaseInfoItem.() -> Unit = {}) {
    dslCustomItem(DslBaseInfoItem(), config)
}

/**单行文本+右边文本*/
public fun DslAdapter.dslTextInfoItem(config: DslTextInfoItem.() -> Unit = {}) {
    dslCustomItem(DslTextInfoItem(), config)
}

/**单行文本+开关*/
public fun DslAdapter.dslSwitchInfoItem(config: DslSwitchInfoItem.() -> Unit = {}) {
    dslCustomItem(DslSwitchInfoItem(), config)
}

public fun DslAdapter.dslDeviceInfoItem(
    fragment: BaseFragment,
    config: DslAdapterItem.() -> Unit = {}
) {
    //设备信息
    dslItem(R.layout.base_single_text_layout) {
        itemBind = { itemHolder, _, _ ->
            itemHolder.tv(R.id.base_text_view).apply {
                text = span {
                    append(RUtils.getIP(fragment.requireContext()))
                    append(" ")
                    append(RUtils.getMobileIP())
                    appendln()
                    append(Root.device_info(fragment.requireContext()))
                }

                gravity = Gravity.CENTER
                setTextColor(getColor(R.color.base_text_color_dark))
                setTextSizeWithDp(getDimen(R.dimen.base_dark_text_size))

                clickIt {
                    RUtils.copyText(itemHolder.tv(R.id.base_text_view).text)
                    Tip.ok("已复制")

                    FileSelectorFragment.show(fragment.parentFragmentManager()) {
                        targetPath = Root.getAppExternalFolder()
                        showFileMd5 = true
                        showFileMenu = true
                        showHideFile = true
                    }
                }
            }
        }
        config()
    }
}