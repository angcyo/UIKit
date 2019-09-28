package com.angcyo.uiview.less.skin

import com.angcyo.uiview.less.kotlin.toColor

/**
 * 用于在 preview 视图中预览的 skin
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/09/28
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
class PreviewSkin : BaseSkin(null) {
    override fun getThemeColor(): Int {
        return "#09C1CE".toColor()
    }

    override fun getThemeSubColor(): Int {
        return themeColor
    }

    override fun getThemeDarkColor(): Int {
        return "#00574B".toColor()
    }

    override fun getThemeDisableColor(): Int {
        return "#CBCBCB".toColor()
    }

    override fun getThemeColorAccent(): Int {
        return "#D81B60".toColor()
    }

    override fun getThemeColorPrimaryDark(): Int {
        return themeDarkColor
    }

    override fun getThemeColorPrimary(): Int {
        return themeColor
    }

    override fun getMainTextSize(): Float {
        return 16 * 3f
    }

    override fun getSubTextSize(): Float {
        return 14 * 3f
    }
}