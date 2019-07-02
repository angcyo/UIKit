package com.angcyo.uiview.less.picture

import android.os.Bundle
import androidx.transition.*
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.appcompat.widget.AppCompatImageView
import android.widget.ImageView
import com.angcyo.uiview.less.R
import com.angcyo.uiview.less.picture.transition.ColorTransition
import com.angcyo.uiview.less.picture.transition.TransitionConfig
import com.angcyo.uiview.less.recycler.RBaseViewHolder

/**
 * 只提供预览图的动画效果, 图片切换的效果需要继承实现
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/04/27
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */

@Deprecated("")
open class BasePhotoTransitionFragment : BaseTransitionFragment() {
    protected lateinit var previewImageView: AppCompatImageView

    protected var transitionConfig: TransitionConfig? = null

    override fun getContentLayoutId(): Int {
        return R.layout.base_photo_transition_fragment
    }

    override fun onInitBaseView(viewHolder: RBaseViewHolder, arguments: Bundle?, savedInstanceState: Bundle?) {
        super.onInitBaseView(viewHolder, arguments, savedInstanceState)
        previewImageView = viewHolder.v(R.id.base_preview_image_view)
    }

    /**
     * 界面参数配置
     * */
    open fun config(config: TransitionConfig.() -> Unit) {
        transitionConfig = TransitionConfig().apply {
            config()
        }
    }

    override fun onTransitionShowBeforeValues() {
        if (transitionConfig == null) {
            super.onTransitionShowBeforeValues()
        }
        transitionConfig?.apply {
            configPreview.invoke(this@BasePhotoTransitionFragment, previewImageView, 0)
            rootLayout.setBackgroundColor(backgroundStartColor)
        }
    }

    override fun onTransitionShowAfterValues() {
        super.onTransitionShowAfterValues()
    }

    override fun onTransitionShowEnd() {
        super.onTransitionShowEnd()
    }

    override fun onTransitionHideBeforeValues() {
        if (transitionConfig == null) {
            super.onTransitionHideBeforeValues()
            return
        }
        transitionConfig?.apply {
            rootLayout.setBackgroundColor(backgroundEndColor)
        }

        val params = previewImageView.layoutParams

        previewImageView.translationX = 0f
        previewImageView.translationY = 0f

        params.width = -1
        params.height = -1

        previewImageView.scaleType = ImageView.ScaleType.FIT_CENTER
        previewImageView.layoutParams = params
    }

    override fun onTransitionHideAfterValues() {
        super.onTransitionHideAfterValues()
    }

    override fun onTransitionHideEnd() {
        super.onTransitionHideEnd()
    }

    override fun createShowTransitionSet(): TransitionSet {
        if (transitionConfig == null) {
            return super.createShowTransitionSet()
        }

        val transitionSet = TransitionSet()
        transitionSet.addTransition(ChangeTransform())
        transitionSet.addTransition(ChangeScroll())
        transitionSet.addTransition(ChangeClipBounds())
        transitionSet.addTransition(ChangeImageTransform())
        transitionSet.addTransition(ChangeBounds())
        transitionSet.addTransition(ColorTransition().addTarget(rootLayout))

        transitionSet.duration = ANIM_DURATION
        transitionSet.interpolator = FastOutSlowInInterpolator()
        return transitionSet
    }
}