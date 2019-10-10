package com.angcyo.uiview.less.base

import android.os.Bundle
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.angcyo.uiview.less.R
import com.angcyo.uiview.less.kotlin.coordinatorParams
import com.angcyo.uiview.less.kotlin.inflate
import com.angcyo.uiview.less.recycler.RBaseViewHolder
import com.angcyo.uiview.less.widget.behavior.BackgroundBehavior
import com.angcyo.uiview.less.widget.behavior.ContentBehavior
import com.angcyo.uiview.less.widget.behavior.TitleBarBehavior

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

    override fun getContentLayoutId(): Int {
        return super.getContentLayoutId()
    }

    override fun onInitBaseView(
        viewHolder: RBaseViewHolder,
        arguments: Bundle?,
        savedInstanceState: Bundle?
    ) {
        super.onInitBaseView(viewHolder, arguments, savedInstanceState)

        if (getBehaviorBgLayoutId() > 0) {
            viewHolder.vg(R.id.base_behavior_bg_layout)?.inflate(getBehaviorBgLayoutId())
        }

        initBehavior {
            onInitBehavior(this)
        }
    }

    open fun initBehavior(config: CoordinatorLayout.Behavior<*>.() -> Unit = {}) {
        baseViewHolder.view(R.id.base_behavior_bg_layout)?.coordinatorParams {
            behavior = BackgroundBehavior().apply {
                childHeight = viewResConfig.defaultBehaviorBgViewHeight

                config()
            }
        }

        baseViewHolder.view(R.id.base_content_wrapper_layout)?.coordinatorParams {
            behavior = ContentBehavior().apply {
                config()
            }
        }

        baseViewHolder.view(R.id.base_title_bar_layout)?.coordinatorParams {
            behavior = TitleBarBehavior().apply {
                //gradientStartConfig = viewResConfig
                gradientEndConfig = viewResConfig

                config()
            }
        }
    }

    open fun getBehaviorBgLayoutId(): Int = -1

    open fun onInitBehavior(behavior: CoordinatorLayout.Behavior<*>) {

    }
}