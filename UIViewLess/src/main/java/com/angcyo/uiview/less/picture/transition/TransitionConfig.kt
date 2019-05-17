package com.angcyo.uiview.less.picture.transition

import android.graphics.Color
import android.support.v7.widget.AppCompatImageView
import android.widget.ImageView
import com.angcyo.uiview.less.kotlin.getViewRect
import com.angcyo.uiview.less.picture.BaseTransitionFragment

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/04/27
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */

@Deprecated("")
class TransitionConfig {

    companion object {
        fun configPreviewFromImageView(previewImageView: AppCompatImageView, fromImageView: ImageView) {
            val viewRect = fromImageView.getViewRect()

            previewImageView.translationX = viewRect.left.toFloat()
            previewImageView.translationY = viewRect.top.toFloat()

            val params = previewImageView.layoutParams
            params.width = viewRect.width()
            params.height = viewRect.height()

            previewImageView.layoutParams = params

            previewImageView.setImageDrawable(fromImageView.drawable)
        }
    }

    /**
     * 背景动画开始的颜色
     * */
    var backgroundStartColor = Color.TRANSPARENT
    /**
     * 背景动画结束的颜色
     * */
    var backgroundEndColor = Color.BLACK

    /**
     * 配置预览的ImageView, 确定 x,y, w,h 和scaleType
     * */
    var configPreview: (BaseTransitionFragment, previewImageView: AppCompatImageView, index: Int) -> Unit =
        { _, _, _ -> }


}