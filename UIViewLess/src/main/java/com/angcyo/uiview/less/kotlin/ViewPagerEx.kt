package com.angcyo.uiview.less.kotlin

import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.angcyo.uiview.less.kotlin.dsl.DslPagerItem
import com.angcyo.uiview.less.kotlin.dsl.DslViewPagerAdapter

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/05/13
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */

public fun ViewPager.dslPagerAdapter(init: DslViewPagerAdapter.() -> Unit) {
    adapter = DslViewPagerAdapter().apply {
        init()
    }
}

public fun DslViewPagerAdapter.renderPagerItem(
    count: Int = 1,
    init: DslPagerItem.(index: Int) -> Unit
) {
    for (i in 0 until count) {
        pagerItems.add(DslPagerItem().apply {
            init(i)
        })
    }
}