package com.angcyo.uiview.less.base

import com.angcyo.uiview.less.R

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/09/27
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */

open class BaseBehaviorDslRecyclerFragment : BaseDslRecyclerFragment() {
    override fun getLayoutId(): Int {
        return R.layout.base_behavior_fragment_layout
    }
}