package com.angcyo.uiview.less

import android.content.Intent
import com.angcyo.uiview.less.base.activity.BasePermissionActivity
import com.angcyo.uiview.less.kotlin.handleTargetFragment

/**
 * 容器Activity, 用来当做Fragment的载体
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/10/24
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
open class ContainerActivity : BasePermissionActivity() {
    override fun toMain() {

    }

    override fun handleIntent(intent: Intent?) {
        handleTargetFragment(intent)
    }
}