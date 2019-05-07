package com.angcyo.uiview.less.picture

import android.app.Activity
import android.graphics.Rect
import android.os.Bundle
import android.support.transition.*
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.view.ViewGroup
import com.angcyo.uiview.less.R
import com.angcyo.uiview.less.base.BaseFragment
import com.angcyo.uiview.less.base.helper.FragmentHelper
import com.angcyo.uiview.less.kotlin.inflate
import com.angcyo.uiview.less.kotlin.setWidthHeight
import com.angcyo.uiview.less.picture.transition.ColorTransition
import com.angcyo.uiview.less.recycler.RBaseViewHolder
import com.angcyo.uiview.less.utils.RUtils

/**
 * 转场动画基类Fragment, 请关闭默认的Fragment Transition动画
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/04/27
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */

open abstract class BaseTransitionFragment : BaseFragment() {

    companion object {
        const val ANIM_DURATION = 300L
        const val KEY_TRANSITION_FROM_RECT = "key_transition_from_rect"
    }

    //<editor-fold desc="界面基本的初始化">

    lateinit var rootLayout: ViewGroup
    override fun getLayoutId(): Int {
        return R.layout.base_transition_fragment
    }

    override fun initBaseView(viewHolder: RBaseViewHolder, arguments: Bundle?, savedInstanceState: Bundle?) {
        super.initBaseView(viewHolder, arguments, savedInstanceState)
        onInitBaseView(viewHolder, arguments, savedInstanceState)
        doTransitionShow()
    }

    open fun onInitBaseView(viewHolder: RBaseViewHolder, arguments: Bundle?, savedInstanceState: Bundle?) {
        rootLayout = viewHolder.v(R.id.base_root_content_layout)
        rootLayout.inflate(getContentLayoutId())
    }

    abstract fun getContentLayoutId(): Int

    override fun onBackPressed(activity: Activity): Boolean {
        doTransitionHide()
        return false
    }

    /**
     * 无动画移除Fragment
     * */
    open fun noAnimRemoveFragment() {
        FragmentHelper.build(parentFragmentManager())
            .noAnim()
            .remove(this)
            .doIt()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showFromRect = arguments?.getParcelable(KEY_TRANSITION_FROM_RECT)
    }

    //</editor-fold desc="界面基本的初始化">

    //<editor-fold desc="转场动画相关处理">

    /**
     * 界面从哪个矩形坐标开始显示, 如果为null, 默认动画是从底部往上平移
     * */
    var showFromRect: Rect? = null

    var showTransitionSet: TransitionSet? = null
    var hideTransitionSet: TransitionSet? = null

    val showTransitionListener = object : TransitionListenerAdapter() {
        override fun onTransitionEnd(transition: Transition) {
            super.onTransitionEnd(transition)
            showTransitionSet?.removeListener(this)

            onTransitionShowEnd()
        }
    }

    val hideTransitionListener = object : TransitionListenerAdapter() {
        override fun onTransitionEnd(transition: Transition) {
            super.onTransitionEnd(transition)
            hideTransitionSet?.removeListener(this)

            onTransitionHideEnd()
        }
    }

    /**
     * 开始显示的转场动画
     * */
    open fun doTransitionShow() {
        onTransitionShowBeforeValues()
        baseViewHolder.post {
            onTransitionShowAfterValues()
        }
    }

    /**
     * 开始隐藏的转场动画
     * */
    open fun doTransitionHide() {
        onTransitionHideBeforeValues()
        baseViewHolder.post {
            onTransitionHideAfterValues()
        }
    }

    /**
     * 显示的转场动画, 第一步. captureValues
     *
     * 捕捉动画开始时, 需要的values
     * */
    open fun onTransitionShowBeforeValues() {
        if (showFromRect != null) {
            rootLayout.setWidthHeight(showFromRect!!.width(), showFromRect!!.height())
            rootLayout.translationX = showFromRect!!.left.toFloat()
            rootLayout.translationY = showFromRect!!.top.toFloat()
        } else {
            rootLayout.translationY = RUtils.getScreenHeight().toFloat()
        }
    }

    /**
     * 动画结束后的values
     * */
    open fun onTransitionShowAfterValues() {
        createShowTransitionSet().apply {
            showTransitionSet = this
            addListener(showTransitionListener)

            //真正开始转场动画
            //流程 captureStartValues->(OnPreDraw回调后)->captureEndValues->(playTransition)createAnimator->runAnimators
            TransitionManager.beginDelayedTransition(rootLayout, this)
        }
        onTransitionHideBeforeValues()
    }

    open fun onTransitionHideBeforeValues() {
        rootLayout.setWidthHeight(-1, -1)
        rootLayout.translationY = 0f
        rootLayout.translationX = 0f
    }

    open fun onTransitionHideAfterValues() {
        createHideTransitionSet().apply {
            hideTransitionSet = this
            addListener(hideTransitionListener)

            TransitionManager.beginDelayedTransition(rootLayout, this)
        }
        onTransitionShowBeforeValues()
    }

    /**
     * 显示的转场动画结束
     * */
    open fun onTransitionShowEnd() {
        //L.e("...动画结束:\n$showTransitionSet")
    }

    open fun onTransitionHideEnd() {
        //L.e("...动画结束:\n$hideTransitionSet")
        noAnimRemoveFragment()
    }

    open fun createShowTransitionSet(): TransitionSet {
        val transitionSet = TransitionSet()
        transitionSet.addTransition(ChangeTransform())
        transitionSet.addTransition(ChangeScroll())
        transitionSet.addTransition(ChangeClipBounds())
        transitionSet.addTransition(ChangeImageTransform())
        transitionSet.addTransition(ChangeBounds())
        transitionSet.addTransition(ColorTransition())

        transitionSet.duration = ANIM_DURATION
        transitionSet.interpolator = FastOutSlowInInterpolator()
        transitionSet.addTarget(rootLayout)
        return transitionSet
    }

    open fun createHideTransitionSet(): TransitionSet {
        return createShowTransitionSet()
    }

    //</editor-fold desc="转场动画相关处理">
}