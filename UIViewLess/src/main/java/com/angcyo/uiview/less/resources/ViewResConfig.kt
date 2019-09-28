package com.angcyo.uiview.less.resources

import android.graphics.Color
import com.angcyo.uiview.less.R
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

    var fragmentBackgroundColor: Int = getColor(R.color.line_color)
    var titleBarBackgroundColor: Int = SkinHelper.getSkin().themeSubColor

    var defaultBehaviorBgViewHeight = -1
}