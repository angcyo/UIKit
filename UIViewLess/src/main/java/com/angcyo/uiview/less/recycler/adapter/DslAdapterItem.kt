package com.angcyo.uiview.less.recycler.adapter

import com.angcyo.uiview.less.recycler.RBaseViewHolder

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/05/07
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
open class DslAdapterItem {

    //<editor-fold desc="Grid相关属性">

    /**
     * 在 GridLayoutManager 中, 需要占多少个 span
     * */
    var itemSpanCount = 1

    //</editor-fold>

    //<editor-fold desc="标准属性">

    /**布局的xml id, 必须设置.*/
    var itemLayoutId: Int = -1

    /**附加的数据*/
    var itemData: Any? = null

    /**界面绑定*/
    var itemBind: (itemHolder: RBaseViewHolder, itemPosition: Int, adapterItem: DslAdapterItem) -> Unit = { _, _, _ -> }

    //</editor-fold>


    //<editor-fold desc="分组相关属性">

    /**当前item, 是否是分组的头
     *
     * 如果为true, 哪里折叠此分组是, 会 伪删除 这个分组头, 到下一个分组头 中间的 data
     * */
    var itemIsGroupHead = false

    /**
     * 当前分组是否 展开
     * */
    var itemGroupExtend = true

    //</editor-fold>

    //<editor-fold desc="悬停相关属性">

    /**
     * 是否需要悬停, 在使用了 [HoverItemDecoration] 时, 有效
     * */
    var itemIsHover = false
        get() = itemIsGroupHead

    //</editor-fold>
}
