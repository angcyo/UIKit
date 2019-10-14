package com.angcyo.uiview.less.base

import android.os.Bundle
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.angcyo.uiview.less.R
import com.angcyo.uiview.less.base.helper.ViewGroupHelper
import com.angcyo.uiview.less.kotlin.coordinatorParams
import com.angcyo.uiview.less.kotlin.dp
import com.angcyo.uiview.less.kotlin.inflate
import com.angcyo.uiview.less.recycler.RBaseViewHolder
import com.angcyo.uiview.less.widget.behavior.*

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/09/27
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */

open class BaseBehaviorDslRecyclerFragment : BaseDslRecyclerFragment() {

    /**开启折叠标题后, 标题栏的背景颜色请不要用 [titleBarLayout] 设置*/
    var isCollapseTitle = false

    override fun getLayoutId(): Int {
        return R.layout.base_behavior_fragment_layout
    }

    override fun getTitleBarLayoutId(): Int {
        if (isCollapseTitle) {
            return R.layout.base_fragment_collapse_title_layout
        }
        return super.getTitleBarLayoutId()
    }

    override fun getContentLayoutId(): Int {
        return super.getContentLayoutId()
    }

    override fun setTitleString(title: CharSequence) {
        super.setTitleString(title)
        if (isCollapseTitle) {
            //坍塌标题文本设置
            titleControl().selector(R.id.base_collapse_title_view).setText(title)
        }
    }

    override fun initBaseTitleLayout(arguments: Bundle?) {
        super.initBaseTitleLayout(arguments)

        initCollapseTitleLayout()
    }

    open fun initCollapseTitleLayout() {
        if (isCollapseTitle) {
            //坍塌模式, 标题栏背景初始化
            titleBarLayout.isClickable = false
            titleControl()
                .selector(R.id.base_title_bar_layout)
                .setBackground(null)
                .selector(R.id.base_collapse_title_bar_background_layout)
                .setBackground(viewResConfig.titleBarBackgroundDrawable)
//                .setBackgroundColor(Color.TRANSPARENT)
        }
    }

    override fun onInitBaseView(
        viewHolder: RBaseViewHolder,
        arguments: Bundle?,
        savedInstanceState: Bundle?
    ) {
        super.onInitBaseView(viewHolder, arguments, savedInstanceState)

        //联动背景布局
        if (getBehaviorBgLayoutId() > 0) {
            viewHolder.vg(R.id.base_behavior_bg_layout)?.inflate(getBehaviorBgLayoutId())
        }

        //初始化默认的行为behavior
        initBehavior {
            onInitBehavior(this)
        }
    }

    open fun initBehavior(config: CoordinatorLayout.Behavior<*>.() -> Unit = {}) {
        baseViewHolder.view(R.id.base_behavior_bg_layout)?.coordinatorParams {
            behavior = BackgroundBehavior().apply {
                //背景行为控制
                //固定背景布局的高度
                childHeight = viewResConfig.defaultBehaviorBgViewHeight
                config()
            }
        }

        baseViewHolder.view(R.id.base_title_bar_layout)?.coordinatorParams {
            //标题行为控制
            behavior = if (isCollapseTitle) {
                TitleBarCollapseBehavior().apply {
                    //设置倒塌前的字体大小
                    gradientStartConfig.titleTextSize = 22 * dp
                    gradientStartConfig.titleTextColor = viewResConfig.titleTextColor
                    ViewGroupHelper.build(baseViewHolder.view(R.id.base_collapse_title_view))
                        .selector()
                        .setTextSize(gradientStartConfig.titleTextSize)
                        .setTextColor(gradientStartConfig.titleTextColor)
                }
            } else {
                TitleBarBehavior()
            }.apply {
                //渐变结束信息配置
                //gradientStartConfig = viewResConfig
                gradientEndConfig = viewResConfig
                config()
            }
        }

        //[ContentCollapseBehavior]需要依赖[TitleBarCollapseBehavior], 所在放在后面初始化. 其实也可以不用. 安全第一
        baseViewHolder.view(R.id.base_content_wrapper_layout)?.coordinatorParams {
            behavior = if (isCollapseTitle) {
                ContentCollapseBehavior()
            } else {
                ContentBehavior()
            }.apply {
                //内容行为控制
                config()
            }
        }
    }

    open fun getBehaviorBgLayoutId(): Int = -1

    open fun onInitBehavior(behavior: CoordinatorLayout.Behavior<*>) {

    }
}