package com.angcyo.uiview.less.kotlin

import android.app.Activity
import android.app.PictureInPictureParams
import android.content.pm.PackageManager
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Rational
import android.view.ViewGroup
import android.view.Window
import androidx.annotation.ColorInt
import com.angcyo.lib.L
import com.angcyo.uiview.less.base.BaseAppCompatActivity
import com.angcyo.uiview.less.base.helper.ActivityHelper

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2018/07/17 08:47
 * 修改人员：Robi
 * 修改时间：2018/07/17 08:47
 * 修改备注：
 * Version: 1.0.0
 */
/**
 * @see com.angcyo.uiview.view.UIIViewImpl.lightStatusBar
 */
public fun Activity.lightStatusBar(light: Boolean = true) {
    ActivityHelper.lightStatusBar(this, light)
}

public fun Activity.setStatusBarColor(@ColorInt color: Int) {
    ActivityHelper.setStatusBarColor(this, color)
}

public fun Activity.setStatusBarDrawable(drawable: Drawable) {
    ActivityHelper.setStatusBarDrawable(this, drawable)
}

/**
 * 激活布局全屏, View 可以布局在 StatusBar 下面
 */
public fun Activity.enableLayoutFullScreen() {
    ActivityHelper.enableLayoutFullScreen(this, true)
}

public fun Activity.contentView(): ViewGroup {
    return this.window.findViewById(Window.ID_ANDROID_CONTENT)
}

public fun Activity.fullscreen(enable: Boolean = true, checkSdk: Boolean = true) {
    ActivityHelper.fullscreen(this, enable, checkSdk)
}

/**
 * 进入画中画模式. 可以指定宽高的比例
 * https://developer.android.google.cn/guide/topics/ui/picture-in-picture
 * @param numerator 分子
 * @param denominator 分母
 * */
public fun BaseAppCompatActivity.enterPictureInPictureModeEx(numerator: Int = 3, denominator: Int = 4) {
    if (isActivityResume && packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val builder = PictureInPictureParams.Builder()
            builder.setAspectRatio(Rational(numerator, denominator))
            enterPictureInPictureMode(builder.build())
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            enterPictureInPictureMode()
        }
    } else {
        L.w("设备不支持画中画.")
    }
}