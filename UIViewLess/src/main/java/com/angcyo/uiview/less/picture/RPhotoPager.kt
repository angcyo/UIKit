package com.angcyo.uiview.less.picture

import android.app.Activity
import android.graphics.Rect
import android.graphics.drawable.Drawable
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.ImageView
import com.angcyo.uiview.less.base.helper.FragmentHelper
import com.angcyo.uiview.less.kotlin.childs
import com.angcyo.uiview.less.kotlin.getViewRect
import com.angcyo.uiview.less.utils.RUtils

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/03/12
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */

@Deprecated("")
object RPhotoPager {

    /**
     * 全部手动配置 PagerConfig
     * */
    fun start(
        fragmentManager: androidx.fragment.app.FragmentManager?,
        init: (RPhotoPagerConfig.() -> Unit)? = null
    ) {
        val config = RPhotoPagerConfig()
        init?.let { config.it() }

        FragmentHelper.build(fragmentManager)
            .noAnim()
            .showFragment(PhotoPagerFragment().apply {
                photoPagerConfig = config
            })
            .doIt()
    }

    /**
     * 通过RecyclerView简单启动pager
     * */
    fun start(
        fragmentManager: androidx.fragment.app.FragmentManager?,
        recyclerView: androidx.recyclerview.widget.RecyclerView,
        imageViewId: Int,
        photos: List<String>,
        startIndex: Int = 0,
        init: (RPhotoPagerConfig.() -> Unit)? = null
    ) {
        val config = RPhotoPagerConfig()
        config.currentIndex = startIndex
        val imageViews = getImageViews(recyclerView, imageViewId)
        config.originPhotoRect = getImageRects(imageViews)
        config.previewDrawables = getImageDrawables(imageViews)
        config.scaleType = imageViews[startIndex].scaleType

        config.dataSource = object : SinglePhotoDataSource<String>(photos) {
            override fun getPlaceholder(position: Int): Drawable? {
                return config.getPreviewDrawable(position)
            }
        }

        init?.let { config.it() }

        FragmentHelper.build(fragmentManager)
            .noAnim()
            .showFragment(PhotoPagerFragment().apply {
                photoPagerConfig = config
            })
            .doIt()
    }

    /**
     * 基础参数启动pager
     * */
    fun start(
        fragmentManager: androidx.fragment.app.FragmentManager?,
        images: List<ImageView>,
        photos: List<String>,
        startIndex: Int = 0,
        init: (RPhotoPagerConfig.() -> Unit)? = null
    ) {
        val config = RPhotoPagerConfig()
        config.currentIndex = startIndex
        config.scaleType = images[startIndex].scaleType

        //小图赋值
        config.previewDrawables = getImageDrawables(images)

        //位置赋值
        config.originPhotoRect = getImageRects(images)

        config.dataSource = object : SinglePhotoDataSource<String>(photos) {
            override fun getPlaceholder(position: Int): Drawable? {
                return config.getPreviewDrawable(position)
            }
        }

        init?.let { config.it() }

        FragmentHelper.build(fragmentManager)
            .noAnim()
            .showFragment(PhotoPagerFragment().apply {
                photoPagerConfig = config
            })
            .doIt()
    }

    /**
     * 拿到ImageView对应的Drawable
     * */
    fun getImageDrawables(images: List<ImageView>): MutableList<Drawable?> {
        val drawables = mutableListOf<Drawable?>()
        for (image in images) {
            drawables.add(image.drawable)
        }
        return drawables
    }

    /**
     * 从RecyclerView中, 获取到界面上所有的ImageView
     * */
    fun getImageViews(recyclerView: androidx.recyclerview.widget.RecyclerView, imageViewId: Int): MutableList<ImageView> {
        val results = mutableListOf<ImageView>()
        recyclerView.childs { index, child ->
            child.findViewById<ImageView>(imageViewId)?.let {
                results.add(it)
            }
        }
        return results
    }

    /**
     * 拿到ImageView对应的屏幕坐标位置
     * */
    fun getImageRects(images: List<ImageView>): MutableList<Rect> {
        val rects = mutableListOf<Rect>()
        var offsetX = 0
        var offsetY = 0
        for (image in images) {

            //横屏, 并且显示了虚拟导航栏的时候. 需要左边偏移
            if (rects.isEmpty()) {
                //只计算一次
                (image.context as? Activity)?.let {
                    val decorRect = Rect()
                    it.window.decorView.getGlobalVisibleRect(decorRect)
                    if (decorRect.width() > decorRect.height()) {
                        //横屏了
                        offsetX = -RUtils.navBarHeight(it)
                    }
                }
            }

            rects.add(image.getViewRect(offsetX, offsetY))
        }
        return rects
    }
}

@Deprecated("")
open class RPhotoPagerConfig {

    /**
     * 持久保存开始时的索引
     * */
    protected var startIndex = 0

    /**
     * 默认显示第几页
     * */
    var currentIndex = 0

    /**
     * 预览的小图
     * */
    var previewDrawables: MutableList<Drawable?>? = null

    /**
     * 图片显示的scale类型
     * */
    var scaleType: ImageView.ScaleType = ImageView.ScaleType.CENTER_CROP

    /**
     * 原始图片view, 所在的屏幕坐标位置
     * */
    var originPhotoRect: MutableList<Rect>? = null

    /**
     * 数据源和显示图片
     * */
    var dataSource: PhotoDataSource? = null

    open fun getPreviewDrawable(index: Int): Drawable? {
        return previewDrawables?.let {
            if (it.size > index) {
                it[index]
            } else {
                null
            }
        }
    }
}