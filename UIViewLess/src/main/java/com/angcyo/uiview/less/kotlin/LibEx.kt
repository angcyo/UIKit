package com.angcyo.uiview.less.kotlin

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.view.View
import com.angcyo.uiview.less.R
import com.angcyo.uiview.less.base.BaseFragment
import com.angcyo.uiview.less.base.BaseTitleFragment
import com.angcyo.uiview.less.base.helper.FragmentHelper
import com.angcyo.uiview.less.base.helper.TitleItemHelper
import com.angcyo.uiview.less.utils.TopToast
import com.angcyo.uiview.less.widget.ImageTextView

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/05/06
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */

public fun get(fm: FragmentManager?): FragmentHelper.Builder {
    return FragmentHelper.build(fm)
        .defaultEnterAnim()
        .hideBeforeIndex(2)
}

public fun only(fm: FragmentManager?): FragmentHelper.Builder {
    return FragmentHelper.build(fm)
        .anim(R.anim.base_scale_alpha_enter, R.anim.base_no_alpha)
        .keepFragment(emptyList())
}

public fun BaseFragment.wtf(): FragmentHelper.Builder {
    return get(parentFragmentManager())
}

public fun BaseFragment.only(): FragmentHelper.Builder {
    return only(parentFragmentManager())
}

/**
 * 移除其他, 只显示 f
 * */
public fun BaseFragment.only(f: Class<out Fragment>, init: FragmentHelper.Builder.() -> Unit = {}): Fragment? {
    val builder = only().showFragment(f).apply {
        init()
    }
    return builder.doIt()
}

/**
 * 移除其他, 只显示 f
 * */
public fun BaseFragment.only(f: Fragment, init: FragmentHelper.Builder.() -> Unit = {}): Fragment? {
    val builder = only().showFragment(f).apply {
        init()
    }
    return builder.doIt()
}

public fun BaseFragment.show(f: Class<out Fragment>, init: FragmentHelper.Builder.() -> Unit = {}): Fragment? {
    val builder = wtf().showFragment(f).apply {
        init()
    }
    return builder.doIt()
}

public fun BaseFragment.show(f: Fragment, init: FragmentHelper.Builder.() -> Unit = {}): Fragment? {
    val builder = wtf().showFragment(f).apply {
        init()
    }
    return builder.doIt()
}

public fun toast_tip(tipText: CharSequence, imageResId: Int = -1) {
    TopToast.show(tipText, imageResId)
}

public fun BaseFragment.createItem(res: Int, click: (View) -> Unit): ImageTextView {
    val item = TitleItemHelper.createItem(requireContext(), res) {
        click(it)
    }
    return item
}

public fun BaseFragment.createItem(text: String, click: (View) -> Unit): ImageTextView {
    val item = TitleItemHelper.createItem(requireContext(), text) {
        click(it)
    }
    return item
}


public fun BaseTitleFragment.appendRightItem(res: Int, click: (View) -> Unit): ImageTextView {
    val item = createItem(res, click)
    rightControl().addView(item)
    return item
}

public fun BaseTitleFragment.appendRightItem(text: String, click: (View) -> Unit): ImageTextView {
    val item = createItem(text, click)
    rightControl().addView(item)
    return item
}


public fun BaseTitleFragment.appendLeftItem(res: Int, click: (View) -> Unit): ImageTextView {
    val item = createItem(res, click)
    leftControl().addView(item)
    return item
}

public fun BaseTitleFragment.appendLeftrItem(text: String, click: (View) -> Unit): ImageTextView {
    val item = createItem(text, click)
    leftControl().addView(item)
    return item
}
