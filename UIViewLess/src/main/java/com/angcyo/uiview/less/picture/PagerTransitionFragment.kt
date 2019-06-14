package com.angcyo.uiview.less.picture

import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.View
import com.angcyo.uiview.less.R
import com.angcyo.uiview.less.recycler.RBaseViewHolder
import com.angcyo.uiview.less.widget.pager.RPagerAdapter
import com.angcyo.uiview.less.widget.pager.RViewPager
import com.angcyo.uiview.less.widget.pager.TextIndicator

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/05/16
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */

open class PagerTransitionFragment : ViewTransitionFragment() {

    init {
        transitionConfig.fragmentLayoutId = R.layout.base_pager_transition_layout
    }

    lateinit var viewPager: RViewPager

    override fun onInitBaseView(viewHolder: RBaseViewHolder, arguments: Bundle?, savedInstanceState: Bundle?) {
        super.onInitBaseView(viewHolder, arguments, savedInstanceState)

        viewPager = viewHolder.rpager(R.id.base_view_pager)
        viewPager.setPageTransformer(true, null)
        viewPager.adapter = object : RPagerAdapter() {
            override fun getCount(): Int = transitionConfig.getPagerCount(this@PagerTransitionFragment, this)

            override fun getItemType(position: Int): Int =
                transitionConfig.getPagerItemType(this@PagerTransitionFragment, this, position)

            override fun getLayoutId(position: Int, itemType: Int): Int =
                transitionConfig.getPagerLayoutId(this@PagerTransitionFragment, this, position, itemType)


            override fun initItemView(viewHolder: RBaseViewHolder, position: Int, itemType: Int) {
                super.initItemView(viewHolder, position, itemType)
                transitionConfig.bindPagerItemView(this@PagerTransitionFragment, this, viewHolder, position, itemType)
            }
        }

        if (transitionConfig.startPagerIndex > 0) {
            viewPager.setCurrentItem(transitionConfig.startPagerIndex, false)
        }

        //放在后面设置, 是不想让setCurrentItem, 后立马触发回调
        viewPager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                transitionConfig.onPageSelected(
                    this@PagerTransitionFragment,
                    viewPager.adapter as RPagerAdapter, position
                )
            }
        })

        viewPager.isEnabled = transitionConfig.enablePager

        if (transitionConfig.enablePager && transitionConfig.getPagerCount(this, viewPager.adapter as? RPagerAdapter) > 1) {
            viewHolder.v<TextIndicator>(R.id.base_text_indicator_view)?.setupViewPager(viewPager)
        }
    }

    private fun transitionAnimStart() {
        baseViewHolder.gone(R.id.base_text_indicator_view)
        if (transitionConfig.transitionAnimView(this) == previewView) {
            previewView?.visibility = View.VISIBLE
            //viewPager.visibility = View.GONE //用GONE属性, 在加载大一点的图片时会闪.
            viewPager.alpha = 0f
        } else {
            if (transitionConfig.getPagerCount(this, viewPager.adapter as? RPagerAdapter) > 0) {
                previewView?.visibility = View.GONE
                viewPager.visibility = View.VISIBLE
            }
        }
    }

    private fun transitionAnimEnd() {
        if (transitionConfig.enablePager && transitionConfig.getPagerCount(
                this,
                viewPager.adapter as? RPagerAdapter
            ) > 1
        ) {
            baseViewHolder.visible(R.id.base_text_indicator_view)
        }

        if (transitionConfig.getPagerCount(this, viewPager.adapter as? RPagerAdapter) > 0) {
            previewView?.visibility = View.GONE
            viewPager.visibility = View.VISIBLE
            viewPager.alpha = 1f
        }
    }

    override fun onTransitionShowBeforeValues() {
        super.onTransitionShowBeforeValues()
        transitionAnimStart()
    }

    override fun onTransitionHideBeforeValues() {
        super.onTransitionHideBeforeValues()
        transitionAnimStart()
    }

    override fun onTransitionShowEnd() {
        super.onTransitionShowEnd()
        transitionAnimEnd()
    }
}