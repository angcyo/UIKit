package com.angcyo.uiview.less.base.activity

import android.Manifest
import com.angcyo.uiview.less.R

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/06/03
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */

abstract class BasePermissionActivity : BaseActivity() {

    /**Activity布局id*/
    override fun getActivityLayoutId(): Int = R.layout.base_activity_layout

    /**Fragment需要显示在哪个布局id*/
    override fun getFragmentParentLayoutId(): Int {
        val parentLayoutId = super.getFragmentParentLayoutId()
        return if (parentLayoutId != -1) {
            parentLayoutId
        } else {
            R.id.frame_layout
        }
    }

    override fun onHavePermission() {
        toMain()
    }

    override fun onNoPermission() {
        PermissionFragment.show(supportFragmentManager, R.id.frame_layout) {
            getPermissionCount = this@BasePermissionActivity::getPermissionCount
            getPermissionIcon = this@BasePermissionActivity::getPermissionIcon
            getPermissionDes = this@BasePermissionActivity::getPermissionDes

            onEnablePermissionRequest = {
                checkPermissionsResult(needPermissions()) { string ->
                    if (string.contains("0")) {
                        //有权限被拒绝
                        //onPermissionDenied(string)
                    } else {
                        //所有权限通过
                        //only(supportFragmentManager).showFragment(LoginFragment::class.java).doIt()
                        onHavePermission()
                    }
                }
            }
        }
    }

    override fun needPermissions(): Array<String> {
        return arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE
//            Manifest.permission.ACCESS_FINE_LOCATION,
//            Manifest.permission.CAMERA
        )
    }

    open fun getPermissionCount(): Int {
        return needPermissions().size
    }

    open fun getPermissionIcon(index: Int): Int {
        return R.drawable.base_tip_ok
    }

    open fun getPermissionDes(index: Int): String {
        return "权限描述"
    }

    abstract fun toMain()
}
