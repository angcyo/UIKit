package com.angcyo.uiview.less.kotlin.dsl

import com.angcyo.uiview.less.recycler.RBaseViewHolder

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/05/13
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */

class DslPagerItem {
    var pagerItemType: Int = 1
    var pagerItemLayoutId: Int = 1

    var pagerItemBind: (viewHolder: RBaseViewHolder, position: Int, itemType: Int) -> Unit = { _, _, _ -> }

    var pagerItemDestroy: (viewHolder: RBaseViewHolder, position: Int, itemType: Int) -> Unit = { _, _, _ -> }

}