package com.angcyo.uiview.less.resources

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import com.angcyo.uiview.less.R
import com.angcyo.uiview.less.kotlin.copy
import com.angcyo.uiview.less.kotlin.getColor
import com.angcyo.uiview.less.skin.SkinHelper

/**
 *
 * Email:angcyo@126.com
 *
 * 存储一些res资源
 *
 * @author angcyo
 * @date 2019/09/27
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
class ViewResConfig {
    var titleTextSize: Float = SkinHelper.getSkin().mainTextSize
    var titleTextColor: Int = getColor(R.color.base_text_color)

    var titleItemIconColor: Int = Color.WHITE
    var titleItemTextColor: Int = getColor(R.color.base_text_color)

    /**[com.angcyo.uiview.less.widget.behavior.OnTitleBarBehaviorCallback.onTitleBarGradient]*/
    var fragmentBackgroundColor: Int = getColor(R.color.line_color)
    var titleBarBackgroundColor: Int = SkinHelper.getSkin().themeSubColor

    var fragmentBackgroundDrawable: Drawable? = null
        get() {
            if (field == null) {
                return ColorDrawable(fragmentBackgroundColor)
            }
            return field
        }

    var titleBarBackgroundDrawable: Drawable? = null
        get() {
            if (field == null) {
                return ColorDrawable(titleBarBackgroundColor)
            }
            return field
        }

    /**
     * [com.angcyo.uiview.less.widget.behavior.BackgroundBehavior.childHeight]
     * */
    var defaultBehaviorBgViewHeight = -1
}

public fun ViewResConfig.copy(config: ViewResConfig? = null): ViewResConfig {
    val newConfig = config ?: ViewResConfig()
    newConfig.also {
        it.titleTextSize = titleTextSize
        it.titleTextColor = titleTextColor
        it.titleItemIconColor = titleItemIconColor
        it.titleItemTextColor = titleItemTextColor
        it.fragmentBackgroundColor = fragmentBackgroundColor
        it.titleBarBackgroundColor = titleBarBackgroundColor
        it.defaultBehaviorBgViewHeight = defaultBehaviorBgViewHeight

        it.fragmentBackgroundDrawable = fragmentBackgroundDrawable.copy()
        it.titleBarBackgroundDrawable = titleBarBackgroundDrawable.copy()
    }
    return newConfig
}

public fun ViewResConfig.copyFrom(config: ViewResConfig): ViewResConfig {
    return config.copy(this)
}

public fun ViewResConfig.copyTo(config: ViewResConfig): ViewResConfig {
    return this.copy(config)
}

public fun ViewResConfig.setTitleBarBgColor(color: Int): ViewResConfig {
    this.titleBarBackgroundColor = color
    this.titleBarBackgroundDrawable = ColorDrawable(color)
    return this
}