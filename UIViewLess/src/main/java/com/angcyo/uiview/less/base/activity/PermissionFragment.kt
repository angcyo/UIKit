package com.angcyo.uiview.less.base.activity

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import com.angcyo.uiview.less.R
import com.angcyo.uiview.less.base.BaseFragment
import com.angcyo.uiview.less.base.helper.FragmentHelper
import com.angcyo.uiview.less.kotlin.dslAdapter
import com.angcyo.uiview.less.kotlin.renderItem
import com.angcyo.uiview.less.recycler.RBaseViewHolder
import com.angcyo.uiview.less.utils.RUtils

/**
 * 权限申请界面
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/05/29
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
class PermissionFragment : BaseFragment() {

    companion object {
        fun show(
            fragmentManager: FragmentManager,
            layoutId: Int,
            config: PermissionConfig.() -> Unit
        ) {
            FragmentHelper.build(fragmentManager)
                .parentLayoutId(layoutId)
                .showFragment(PermissionFragment().apply {
                    permissionConfig.config()
                })
                .noAnim()
                .doIt()
        }
    }

    var permissionConfig = PermissionConfig()

    override fun getLayoutId(): Int {
        return R.layout.fragment_permission_layout
    }

    override fun initBaseView(
        viewHolder: RBaseViewHolder,
        arguments: Bundle?,
        savedInstanceState: Bundle?
    ) {
        super.initBaseView(viewHolder, arguments, savedInstanceState)
        viewHolder.tv(R.id.permission_title).text =
            "${RUtils.getAppName(mAttachContext)}\n${permissionConfig.permissionTitle}"

        viewHolder.rv(R.id.recycler_view).apply {
            val count = permissionConfig.getPermissionCount()
            resetLayoutManager(
                mAttachContext, when {
                    count <= 2 -> "GV1"
                    count > 2 -> "GV2"
                    else -> "GV2"
                }
            )
            dslAdapter {
                for (i in 0 until count) {
                    renderItem {
                        itemLayoutId = R.layout.item_permission_layout

                        itemBind = { itemHolder, itemPosition, _ ->
                            itemHolder.imgV(R.id.image_view)
                                .setImageResource(permissionConfig.getPermissionIcon(itemPosition))
                            itemHolder.tv(R.id.text_view).text =
                                permissionConfig.getPermissionDes(itemPosition)
                        }
                    }
                }
            }
        }

        viewHolder.click(R.id.enable_button) {
            permissionConfig.onEnablePermissionRequest(this)
        }
    }

    override fun canSwipeBack(): Boolean {
        return false
    }

    override fun onBackPressed(activity: Activity): Boolean {
        return false
    }
}

class PermissionConfig {
    var permissionTitle = "为了更好的服务体验, 程序需要以下权限"

    var onEnablePermissionRequest: (fragment: PermissionFragment) -> Unit = {}

    var getPermissionCount: () -> Int = { 0 }
    var getPermissionIcon: (index: Int) -> Int = { 0 }
    var getPermissionDes: (index: Int) -> String = { "" }
}