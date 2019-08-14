package com.angcyo.uiview.less.base.activity

import android.content.res.Configuration
import android.os.Bundle
import com.angcyo.uiview.less.base.BaseAppCompatActivity
import com.angcyo.uiview.less.kotlin.enterPictureInPictureModeEx
import com.angcyo.uiview.less.kotlin.supportPictureInPicture
import com.angcyo.uiview.less.kotlin.toast_tip

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/06/03
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
abstract class BaseActivity : BaseAppCompatActivity() {

    /**界面布局id*/
    abstract fun getActivityLayoutId(): Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getActivityLayoutId())

        initOnCreate()
    }

    open fun initOnCreate() {
        if (haveSelfPermission()) {
            onHavePermission()
        } else {
            onNoPermission()
        }
    }

    /**有权限时回调*/
    open fun onHavePermission() {

    }

    /**有=无权限时回调*/
    open fun onNoPermission() {

    }

    /**需要的权限*/
    override fun needPermissions(): Array<String> {
        return super.needPermissions()
    }

    override fun onPostResume() {
        super.onPostResume()
        //OfflineUI.checkOfflineTask(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        //OfflineUI.release()
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onBackPressedTime() {
        toast_tip("再按一次, 退出程序!")
    }

    open fun needPIP(): Boolean {
        return supportPictureInPicture()
    }

    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration?
    ) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
    }

    /**即将离开界面*/
    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        if (needPIP() && needMoreTaskToBack()) {
            moveTaskToBack()
        }
    }

    override fun needMoreTaskToBack(): Boolean {
        return super.needMoreTaskToBack() //supportPictureInPicture()
    }

    /**画中画*/
    override fun moveTaskToBack() {
        //进入画中画保活模式
        if (needPIP()) {
            enterPictureInPictureModeEx()
        } else {
            super.moveTaskToBack()
        }
    }
}
