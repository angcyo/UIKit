package com.angcyo.uiview.less.picture.transition

import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.os.Bundle
import android.support.transition.*
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import com.angcyo.lib.L
import com.angcyo.okdownload.FDown
import com.angcyo.okdownload.FDownListener
import com.angcyo.uiview.less.BuildConfig
import com.angcyo.uiview.less.R
import com.angcyo.uiview.less.draw.view.HSProgressView
import com.angcyo.uiview.less.kotlin.*
import com.angcyo.uiview.less.media.play.TextureVideoView
import com.angcyo.uiview.less.picture.BaseTransitionFragment
import com.angcyo.uiview.less.picture.PagerTransitionFragment
import com.angcyo.uiview.less.picture.ViewTransitionFragment
import com.angcyo.uiview.less.recycler.RBaseViewHolder
import com.angcyo.uiview.less.recycler.adapter.RBaseAdapter
import com.angcyo.uiview.less.utils.RNetwork
import com.angcyo.uiview.less.utils.RUtils
import com.angcyo.uiview.less.widget.group.MatrixLayout
import com.angcyo.uiview.less.widget.pager.RPagerAdapter
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.github.chrisbanes.photoview.PhotoView
import com.liulishuo.okdownload.DownloadTask
import java.io.File
import java.lang.ref.WeakReference

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/05/16
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */

open class ViewTransitionConfig {

    var fragmentLayoutId = R.layout.base_pager_transition_layout

    /**
     * 背景动画开始的颜色
     * */
    var backgroundStartColor = Color.TRANSPARENT
    /**
     * 背景动画结束的颜色
     * */
    var backgroundEndColor = Color.BLACK

    /**
     * 背景当前的颜色, 拖拽时 改变的颜色
     * */
    var backgroundCurrentColor: Int? = null

    /**
     * fragment 中 onInitBaseView 触发回调
     * */
    var initFragmentView: (
        fragment: ViewTransitionFragment,
        viewHolder: RBaseViewHolder,
        arguments: Bundle?,
        savedInstanceState: Bundle?
    ) -> Unit = { fragment, viewHolder, arguments, savedInstanceState ->
        onInitFragmentView(fragment, viewHolder, arguments, savedInstanceState)
    }

    /**默认实现*/
    var onInitFragmentView: (
        fragment: ViewTransitionFragment,
        viewHolder: RBaseViewHolder,
        arguments: Bundle?,
        savedInstanceState: Bundle?
    ) -> Unit =
        { fragment, _, _, _ ->
            if (BuildConfig.DEBUG && fragment !is PagerTransitionFragment) {
                fragment.previewView?.setBackgroundColor(fragment.getColor(R.color.base_orange))
            }

            val targetView = onGetTargetView(startPagerIndex)
            if (targetView == null) {
                fromViewWeak?.clear()
                fromViewWeak = null
            } else {
                fromViewWeak = WeakReference(targetView)
            }

            fromViewWeak?.get()?.let { fView ->
                if (fView is ImageView && fragment.previewView is ImageView) {
                    (fragment.previewView as? ImageView)?.let { pView ->
                        pView.setImageDrawable(fView.drawable?.mutate()?.constantState?.newDrawable())
                    }
                }
            }
        }

    //<editor-fold desc="转场动画相关处理">

    //当参数配置, 无法执行转场动画时, 使用默认的动画矩形转场
    val defaultFromRect = Rect(
        RUtils.getScreenWidth() / 2 - 10 * dpi, RUtils.getScreenHeight() / 2 - 10 * dpi,
        RUtils.getScreenWidth() / 2 + 10 * dpi, RUtils.getScreenHeight() / 2 + 10 * dpi
    )

    //默认的最终 坐标/宽高
    val defaultToRect = Rect(0, 0, -1, -1)

    /**
     * 动画结束时的坐标和宽高
     * */
    val toRect = Rect(defaultToRect)

    var fromViewWeak: WeakReference<View>? = null

    val getImagePlaceholder: (position: Int) -> Drawable? = {
        var targetView: View?

        if (it == currentPagerIndex) {
            targetView = fromViewWeak?.get()

        } else {
            targetView = onGetTargetView(it)
        }

        if (targetView is ImageView) {
            targetView.drawable?.mutate()?.constantState?.newDrawable()
        } else {
            null
        }
    }

    val getTargetViewRect: () -> Rect? = {
        val view = fromViewWeak?.get()
        view?.getViewRect()
    }

    /**
     * 背景动画作用的View
     * */
    var backgroundColorAnimView: (fragment: ViewTransitionFragment) -> View? = {
        it.rootLayout
    }

    /**
     * 转场动画作用的view
     * */
    var transitionAnimView: (fragment: ViewTransitionFragment) -> View? = { fragment ->
        if (fragment is PagerTransitionFragment && pagerCount > 0) {
            if (fromViewWeak?.get() == null) {
                fragment.viewPager
            } else {
                fragment.previewView
            }
        } else {
            fragment.previewView
        }
    }

    /**
     * 转场动画 界面显示时, 需要捕捉的值.
     * */
    var transitionShowBeforeSetValues: (
        fragment: ViewTransitionFragment
    ) -> Unit = { fragment ->

        backgroundColorAnimView(fragment)?.setBackgroundColor(backgroundStartColor)

        val animView = transitionAnimView(fragment)

        if (animView == fragment.previewView) {
            fromViewWeak?.get()?.let { fView ->
                if (fView is ImageView && animView is ImageView) {
                    (animView as? ImageView)?.let { pView ->
                        pView.scaleType = fView.scaleType
                    }
                }
            }
        }

        animView?.apply {
            val fromRect = getTargetViewRect() ?: defaultFromRect

            translationX = fromRect.left.toFloat()
            translationY = fromRect.top.toFloat()
            setWidthHeight(fromRect.width(), fromRect.height())

            if (fromViewWeak == null && animView != fragment.previewView) {
                //预览图的转场动画, 不需要透明度动画的支持
                alpha = 0.1f
            }
        }
    }

    var transitionShowAfterSetValues: (
        fragment: ViewTransitionFragment
    ) -> Unit = { fragment ->
        transitionHideBeforeSetValues(fragment)
    }

    /**
     * 转场动画 界面移除时, 需要捕捉的值
     * */
    var transitionHideBeforeSetValues: (
        fragment: ViewTransitionFragment
    ) -> Unit = { fragment ->

        backgroundColorAnimView(fragment)?.setBackgroundColor(backgroundCurrentColor ?: backgroundEndColor)

        val animView = transitionAnimView(fragment)

        animView?.apply {
            (this as? ImageView)?.apply {
                scaleType = ImageView.ScaleType.FIT_CENTER
            }

            alpha = 1f

            translationX = toRect.left.toFloat()
            translationY = toRect.top.toFloat()
            setWidthHeight(toRect.width(), toRect.height())
        }
    }

    var transitionHideAfterSetValues: (
        fragment: ViewTransitionFragment
    ) -> Unit = { fragment ->
        transitionShowBeforeSetValues(fragment)
    }

    var createShowTransitionSet: (
        fragment: ViewTransitionFragment
    ) -> TransitionSet = { fragment ->

        val transitionSet = TransitionSet()
        transitionSet.addTransition(ChangeBounds())
        transitionSet.addTransition(ChangeTransform())
        //transitionSet.addTransition(ChangeScroll()) //图片过渡效果, 请勿设置此项
        transitionSet.addTransition(ChangeClipBounds())
        transitionSet.addTransition(ChangeImageTransform())

        //背景颜色过渡动画
        backgroundColorAnimView(fragment)?.let {
            transitionSet.addTransition(ColorTransition().addTarget(it))
        }

        //透明度动画
        if (fromViewWeak == null) {
            transitionAnimView(fragment)?.let {
                transitionSet.addTransition(AlphaTransition().addTarget(it))
            }
        }

        transitionSet.duration = BaseTransitionFragment.ANIM_DURATION
        transitionSet.interpolator = FastOutSlowInInterpolator()
        transitionSet
    }

    var createHideTransitionSet: (
        fragment: ViewTransitionFragment
    ) -> TransitionSet = { fragment ->
        createShowTransitionSet(fragment)
    }

    //</editor-fold desc="转场动画相关处理">

    //<editor-fold desc="Matrix拖拽返回处理">

    /**
     * 返回true, 激活 拖拽返回
     * */
    var checkMatrixTouchEvent: (fragment: ViewTransitionFragment, matrixLayout: MatrixLayout) -> Boolean =
        { fragment, _ ->
            var result = true
            if (fragment.isTransitionAnimEnd) {
                if (fragment is PagerTransitionFragment) {
                    result = pagerCount <= 0

                    for (i in 0 until fragment.viewPager.childCount) {
                        val childView = fragment.viewPager.getChildAt(i)

                        if (childView != null) {
                            if (fragment.viewPager.isViewIn(childView)) {
                                val photoView: PhotoView? = childView.v(R.id.base_photo_view)

                                result = (photoView != null && photoView.scale <= 1)

                                break
                            }
                        }
                    }
                }
            } else {
                result = false
            }
            result
        }

    /**
     * 拖拽中
     * */
    var onMatrixChange: (fragment: ViewTransitionFragment, matrixLayout: MatrixLayout, matrix: Matrix, fromRect: RectF, toRect: RectF) -> Unit =
        { fragment, _, _, fromRect, toRect ->
            backgroundCurrentColor =
                fragment.getEvaluatorColor(toRect.top / fromRect.bottom, backgroundEndColor, backgroundStartColor)
            backgroundColorAnimView(fragment)?.setBackgroundColor(backgroundCurrentColor!!)
        }

    /**
     * 拖拽结束, 返回true, 自行处理. 否则会 回滚到默认位置
     * */
    var onMatrixTouchEnd: (fragment: ViewTransitionFragment, matrixLayout: MatrixLayout, matrix: Matrix, fromRect: RectF, toRect: RectF) -> Boolean =
        { fragment, matrixLayout, _, fromRect, toRect ->
            if (toRect.top / fromRect.bottom > 0.3f) {
                this.toRect.set(toRect)

                matrixLayout.resetMatrix()
                fragment.doTransitionHide()
                true
            } else {
                false
            }
        }

    //</editor-fold desc="Matrix拖拽返回处理">

    //<editor-fold desc="ViewPager相关处理">

    companion object {
        /**图片*/
        const val MEDIA_TYPE_IMAGE = 1
        /**视频*/
        const val MEDIA_TYPE_VIDEO = 2
    }

    /**
     * 默认显示第几页
     * viewPager.setCurrentItem()
     * */
    var startPagerIndex = 0
        set(value) {
            field = value
            currentPagerIndex = value
        }

    /**
     * 当前第几页
     * */
    var currentPagerIndex = startPagerIndex

    /**是否要激活翻页*/
    var enablePager = true

    /**自动关联RecyclerView*/
    var pagerCount = 0
        get() {
            if (field > 0) {
                return field
            }
            val getRecyclerView = onGetRecyclerView()
            if (getRecyclerView != null) {
                return if (getRecyclerView.adapter is RBaseAdapter<*>) {
                    (getRecyclerView.adapter as? RBaseAdapter<*>)?.allDataCount ?: 0
                } else {
                    getRecyclerView.adapter?.itemCount ?: 0
                }
            }
            return field
        }

    /**
     * 获取指定位置的图片url地址
     * */
    @Deprecated("使用[onGetPagerMediaUrl]")
    var onGetPagerImageUrl: (position: Int) -> String? = { null }

    var onGetPagerMediaUrl: (position: Int) -> String? = { onGetPagerImageUrl(it) }

    var onGetMediaType: (position: Int) -> Int = { MEDIA_TYPE_IMAGE }

    /**
     * 获取指定位置对应的View, 用于设置 ImagePlaceholder , 和 fromRect
     * @see [onGetRecyclerView]
     * */
    var onGetTargetView: (position: Int) -> View? = {
        val getRecyclerView = onGetRecyclerView()
        if (getRecyclerView != null) {
            getRecyclerView.scrollToPosition(it)
            (getRecyclerView.findViewHolderForAdapterPosition(it) as? RBaseViewHolder)?.view(onGetImageViewId())
        } else if (pagerCount > 0) {
            if (currentPagerIndex == startPagerIndex) {
                fromViewWeak?.get()
            } else {
                null
            }
        } else {
            fromViewWeak?.get()
        }
    }

    /**@see [onGetImageViewId]]*/
    var onGetRecyclerView: () -> RecyclerView? = {
        null
    }
    /**@see [onGetRecyclerView]]*/
    var onGetImageViewId: () -> Int = {
        R.id.image_view
    }

    /**
     * 图片点击回调
     * */
    var onItemPhotoClickListener: (
        fragment: PagerTransitionFragment, adapter: RPagerAdapter,
        viewHolder: RBaseViewHolder, itemView: PhotoView, position: Int
    ) -> Unit =
        { fragment, _, _, _, _ -> fragment.doTransitionHide() }

    /**
     * 图片长安回调
     * */
    var onItemPhotoLongClickListener: (
        fragment: PagerTransitionFragment, adapter: RPagerAdapter,
        viewHolder: RBaseViewHolder, itemView: PhotoView, position: Int
    ) -> Boolean =
        { _, _, _, _, _ -> true }

    /**
     * 页面切换回调
     * */
    var onPageSelected: (fragment: PagerTransitionFragment, adapter: RPagerAdapter, position: Int) -> Unit =
        { fragment, _, position ->
            currentPagerIndex = position

            if (fragment.isTransitionAnimEnd) {
                val targetView = onGetTargetView(position)
                if (targetView == null) {
                    fromViewWeak?.clear()
                    fromViewWeak = null
                } else {
                    fromViewWeak = WeakReference(targetView)
                }
            }

            (fragment.previewView as? ImageView)?.setImageDrawable(getImagePlaceholder(position))

            lastVideoView?.stop()
            FDown.cancel(lastVideoDownTaskIt)
        }

    var getPagerCount: (fragment: PagerTransitionFragment, adapter: RPagerAdapter) -> Int = { _, _ ->
        pagerCount
    }

    var getPagerItemType: (fragment: PagerTransitionFragment, adapter: RPagerAdapter, position: Int) -> Int =
        { _, _, position ->
            onGetMediaType(position)
        }

    var getPagerLayoutId: (fragment: PagerTransitionFragment, adapter: RPagerAdapter, position: Int, itemType: Int) -> Int =
        { _, _, _, itemType ->
            if (itemType == MEDIA_TYPE_VIDEO) {
                R.layout.base_item_single_video_pager_layout
            } else {
                R.layout.base_item_single_photo_pager_layout
            }
        }

    /**
     * 绑定页面
     * */
    var bindPagerItemView: (
        fragment: PagerTransitionFragment, adapter: RPagerAdapter,
        viewHolder: RBaseViewHolder, position: Int, itemType: Int
    ) -> Unit = { fragment, adapter, viewHolder, position, itemType ->
        onBindPagerItemView(fragment, adapter, viewHolder, position, itemType)
    }

    private var lastVideoView: TextureVideoView? = null
    private var lastVideoDownTaskIt = 0

    /**默认实现*/
    var onBindPagerItemView: (
        fragment: PagerTransitionFragment, adapter: RPagerAdapter,
        viewHolder: RBaseViewHolder, position: Int, itemType: Int
    ) -> Unit = { fragment, adapter, viewHolder, position, itemType ->
        //图片事件处理
        val photoView: PhotoView = viewHolder.v(R.id.base_photo_view)
        photoView.apply {
            setOnPhotoTapListener { view, x, y ->
                L.i("点击Photo")
            }

            setOnViewTapListener { view, x, y ->
                L.i("点击View")
                onItemPhotoClickListener(fragment, adapter, viewHolder, photoView, position)
            }

            setOnLongClickListener {
                L.i("长按1")
                onItemPhotoLongClickListener(fragment, adapter, viewHolder, photoView, position)
            }
        }

        //加载图片
        val urlData = onGetPagerMediaUrl(position)
        if (urlData is String) {
            photoView.load(urlData) {
                dontAnimate()
                autoClone()
                diskCacheStrategy(DiskCacheStrategy.ALL)

                var placeholderDrawable: Drawable? = null
                getImagePlaceholder(position)?.let {
                    placeholderDrawable = it.mutate().constantState?.newDrawable()
                    placeholder(placeholderDrawable)
                    error(placeholderDrawable)
                }

                addListener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        L.i("加载失败")
                        viewHolder.gone(R.id.base_loading_view)
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        L.i("加载成功 $model $resource $target $isFirstResource")
                        viewHolder.gone(R.id.base_loading_view)
                        return false
                    }

                })
            }

            //视频加载
            if (itemType == MEDIA_TYPE_VIDEO) {
                viewHolder.visible(R.id.base_photo_view)
                val videoView: TextureVideoView = viewHolder.v(R.id.video_view)
                videoView.setRepeatPlay(false)

                if (urlData.isFileExists()) {
                    videoView.setVideoURI(RUtils.getFileUri(RUtils.getApp(), File(urlData)))
                } else {
                    videoView.setVideoPath(urlData)

                    if (!FDown.isCompleted(urlData) && RNetwork.isMobile(viewHolder.itemView.context)) {
                        toast_tip("正在使用移动数据")
                    }
                }

                videoView.setMediaPlayerCallback(object : TextureVideoView.SimpleMediaPlayerCallback() {
                    override fun onPrepared(mp: MediaPlayer?) {
                        super.onPrepared(mp)
                        onPlayStateChanged(mp, TextureVideoView.STATE_PLAYING)
                    }

                    override fun onCompletion(mp: MediaPlayer?) {
                        super.onCompletion(mp)
                        onPlayStateChanged(mp, TextureVideoView.STATE_PLAYBACK_COMPLETED)
                    }

                    override fun onPlayStateChanged(mp: MediaPlayer?, newState: Int) {
                        super.onPlayStateChanged(mp, newState)
                        if (newState == TextureVideoView.STATE_PLAYING) {
                            viewHolder.gone(R.id.hs_progress_view)
                            viewHolder.gone(R.id.base_photo_view)
                            viewHolder.gone(R.id.play_video_view)
                        } else {
                            viewHolder.visible(R.id.play_video_view)
                        }
                    }
                })

                //点击视频, 暂停or恢复
                viewHolder.click(R.id.video_view) {
                    if (videoView.isPlaying) {
                        videoView.pause()
                    } else {
                        videoView.resume()
                    }
                }

                //播放视频
                viewHolder.click(R.id.base_photo_view) {
                    viewHolder.clickView(R.id.play_video_view)
                }

                viewHolder.click(R.id.play_video_view) {
                    viewHolder.gone(R.id.play_video_view)

                    if (urlData.isFileExists()) {
                        //本地视频
                        playVideo(viewHolder, urlData)
                    } else if (FDown.isCompleted(urlData)) {
                        //视频已经下载好了
                        playVideo(viewHolder, getVideoLocalPath(urlData))
                    } else {
                        //开始下载视频

                        viewHolder.v<HSProgressView>(R.id.hs_progress_view).apply {
                            visibility = View.VISIBLE
                            startAnimator()
                        }

                        downVideo(urlData) {
                            playVideo(viewHolder, it)
                        }
                    }
                }
            }
        }
    }

    /**获取本地缓存的视频路径*/
    open fun getVideoLocalPath(url: String): String {
        return FDown.defaultDownloadPath(url)
    }

    /**下载视频*/
    open fun downVideo(url: String, callback: (path: String) -> Unit) {
        lastVideoDownTaskIt = FDown.down(url, object : FDownListener() {
            override fun onTaskEnd(task: DownloadTask, isCompleted: Boolean, realCause: Exception?) {
                super.onTaskEnd(task, isCompleted, realCause)
                if (isCompleted) {
                    callback.invoke(task.file!!.absolutePath)
                }
            }
        }).id
    }

    /**播放视频*/
    open fun playVideo(viewHolder: RBaseViewHolder, path: String) {
        val videoView: TextureVideoView = viewHolder.v(R.id.video_view)
        viewHolder.gone(R.id.play_video_view)

        videoView.setVideoURI(RUtils.getFileUri(RUtils.getApp(), File(path)))

        if (videoView.targetState == TextureVideoView.STATE_PAUSED) {
            videoView.resume()
        } else {
            videoView.start()
        }

        lastVideoView = videoView
    }

    //</editor-fold desc="ViewPager相关处理">
}