package com.angcyo.uiview.less.base

import android.os.Bundle
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.angcyo.uiview.less.R
import com.angcyo.uiview.less.base.helper.ViewGroupHelper
import com.angcyo.uiview.less.kotlin.coordinatorParams
import com.angcyo.uiview.less.kotlin.getDimen
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

    /**坍塌标题的一些配置参数*/
    var titleBarCollapseCallback: TitleBarCollapseCallback = TitleBarCollapseCallback()

    init {
        //Behavior界面, 默认关闭软键盘.否则会出事!
        contentNeedSoftInputLayout = false
    }

    override fun getLayoutId(): Int {
        return R.layout.base_behavior_fragment_layout
    }

    override fun getTitleBarLayoutId(): Int {
        if (isCollapseTitle) {
            return R.layout.base_fragment_collapse_title_layout
        }
        return super.getTitleBarLayoutId()
    }

    /**展开状态时的标题布局, 需要激活[isCollapseTitle]*/
    open fun getCollapseTitleLayoutId(): Int = R.layout.base_collapse_title_layout

    /**坍塌时, 背景需要被[offsetTop]的背景布局, 需要激活[isCollapseTitle]*/
    open fun getCollapseTitleBarBackgroundLayoutId(): Int =
        R.layout.base_collapse_title_background_layout

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

            //填充
            baseViewHolder.group(R.id.base_collapse_title_bar_wrap_layout)
                .inflate(getCollapseTitleLayoutId())
            baseViewHolder.group(R.id.base_collapse_title_bar_background_wrap_layout)
                .inflate(getCollapseTitleBarBackgroundLayoutId())

            //坍塌模式, 标题栏背景初始化
            titleBarLayout.isClickable = false
            titleControl()
                .selector(R.id.base_title_bar_layout)
                .setBackground(null)
                .selector(titleBarCollapseCallback.collapseBackgroundViewId)
                .setBackground(viewResConfig.titleBarBackgroundDrawable)
//                .setBackgroundColor(Color.TRANSPARENT)
                .selector(titleBarCollapseCallback.collapseViewId)
                .setTextColor(viewResConfig.titleTextColor)

            setTitleString(fragmentTitle)
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

                    titleBarCollapseCallback =
                        this@BaseBehaviorDslRecyclerFragment.titleBarCollapseCallback

                    //设置倒塌前的字体大小
                    gradientStartConfig.titleTextSize =
                        getDimen(R.dimen.base_collapse_text_size).toFloat()
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