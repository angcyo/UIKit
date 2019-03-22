package com.angcyo.uiview.less.picture

import android.graphics.Matrix
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.view.View
import com.angcyo.lib.L
import com.angcyo.uiview.less.R
import com.angcyo.uiview.less.kotlin.load
import com.angcyo.uiview.less.recycler.RBaseViewHolder
import com.angcyo.uiview.less.widget.group.MatrixLayout
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.github.chrisbanes.photoview.PhotoView

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/03/12
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
open class SinglePhotoDataSource<T>(val photos: List<T>) : PhotoDataSource() {

    var onItemPhotoClickListener: ((itemView: View, photos: List<T>, position: Int) -> Unit)? = null
    var onItemPhotoLongClickListener: ((itemView: View, photos: List<T>, position: Int) -> Unit)? = null
    var onMatrixTouchListener: MatrixLayout.OnMatrixTouchListener? = null

    override fun getLayoutId(position: Int, itemType: Int): Int {
        return R.layout.base_item_photo_pager_layout
    }

    override fun getCount(): Int {
        return photos.size
    }

    open fun getPlaceholder(position: Int): Drawable? {
        return null
    }

    override fun initItemView(viewHolder: RBaseViewHolder, position: Int, itemType: Int) {
        super.initItemView(viewHolder, position, itemType)

        //图片事件处理
        val photoView: PhotoView = viewHolder.v(R.id.base_photo_view)
        photoView.apply {
            setOnPhotoTapListener { view, x, y ->
                L.i("点击Photo")
            }

            setOnViewTapListener { view, x, y ->
                L.i("点击View")
                onItemPhotoClickListener?.invoke(view, photos, position)
            }

            setOnLongClickListener {
                L.i("长按1")
                onItemPhotoLongClickListener?.invoke(it, photos, position)
                true
            }
        }

        //拖拽返回回调
        viewHolder.v<MatrixLayout>(R.id.base_matrix_layout)
            .setOnMatrixTouchListener(object : MatrixLayout.OnMatrixTouchListener {
                override fun onMatrixChange(
                    matrixLayout: MatrixLayout,
                    matrix: Matrix,
                    fromRect: RectF,
                    toRect: RectF
                ) {
                    onMatrixTouchListener?.onMatrixChange(matrixLayout, matrix, fromRect, toRect)
                }

                override fun onTouchEnd(
                    matrixLayout: MatrixLayout,
                    matrix: Matrix,
                    fromRect: RectF,
                    toRect: RectF
                ): Boolean {
                    return onMatrixTouchListener?.onTouchEnd(matrixLayout, matrix, fromRect, toRect) ?: false
                }

                override fun checkTouchEvent(matrixLayout: MatrixLayout): Boolean {
                    onMatrixTouchListener?.checkTouchEvent(matrixLayout)
                    return photoView.scale <= 1f
                }
            })

        //加载图片
        val data = photos[position]
        if (data is String) {
            photoView.load(data) {
                dontAnimate()
                autoClone()
                diskCacheStrategy(DiskCacheStrategy.ALL)

                placeholder(getPlaceholder(position))

                addListener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        L.i("加载失败")
                        viewHolder.gone(R.id.base_loading_view)
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        L.i("加载成功 $model $resource $target $isFirstResource")
                        viewHolder.gone(R.id.base_loading_view)
                        return false
                    }

                })
            }
        }
    }
}