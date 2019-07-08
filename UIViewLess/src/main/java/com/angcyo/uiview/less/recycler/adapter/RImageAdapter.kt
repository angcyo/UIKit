package com.angcyo.uiview.less.recycler.adapter

import android.content.Context
import androidx.fragment.app.FragmentManager
import android.util.SparseIntArray
import android.view.View
import com.angcyo.uiview.less.R
import com.angcyo.uiview.less.kotlin.dialog.menuDialog
import com.angcyo.uiview.less.picture.PagerTransitionFragment
import com.angcyo.uiview.less.picture.RPager
import com.angcyo.uiview.less.recycler.RBaseViewHolder
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.entity.LocalMedia
import kotlin.math.min

/**
 * 支持图片/视频 本地/在线 查看的Adapter
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/06/27
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */

open class RImageAdapter<T> : RBaseAdapter<T>() {

    /**允许最大添加的数量*/
    var maxItemCount = Int.MAX_VALUE

    /**是否显示删除按钮*/
    var showDeleteModel = false

    /**长按事件, 用来保存等操作*/
    var itemLongClickListener: ((
        fragment: PagerTransitionFragment, adapter: RImageAdapter<T>,
        viewHolder: RBaseViewHolder, position: Int, bean: LocalMedia?
    ) -> Boolean)? = null

    init {

    }

    override fun registerLayouts(layouts: SparseIntArray) {
        super.registerLayouts(layouts)
        layouts.put(getItemLayoutId(), getItemLayoutId())
    }

    override fun getItemCount(): Int {
        val itemCount = super.getItemCount()

        if (isStateLayout) {
            return itemCount
        }

        return if (isMax()) {
            maxItemCount
        } else {
            itemCount
        }
    }

    /**是否已达最大*/
    open fun isMax(): Boolean {
        val size = allDatas.size
        return size >= maxItemCount
    }

    override fun getItemType(position: Int, data: T?): Int {
        val size = allDatas.size
        if (position in 0 until size) {
            return getItemLayoutId()
        }
        return -1
    }

    /**正常布局*/
    open fun getItemLayoutId(): Int {
        return R.layout.item_adapter_show_image
    }

    override fun onBindView(holder: RBaseViewHolder, position: Int, bean: T?) {
        if (holder.itemViewType == getItemLayoutId()) {
            onBindShowItemView(holder, position, bean)
        }
    }

    /**绑定图片/视频按钮*/
    open fun onBindShowItemView(holder: RBaseViewHolder, position: Int, bean: T?) {

        //删除按钮
        holder.visible(R.id.delete_image_view, showDeleteModel)

        //播放按钮
        holder.gone(R.id.play_video_view)

        bean?.let {

            //显示图片or视频
            holder.giv(R.id.image_view).apply {
                reset()
                url = getMediaPath(position, bean)
            }

            //播放按钮
            holder.visible(R.id.play_video_view, isVideoType(position, bean))

            //删除按钮
            holder.click(R.id.delete_image_view) {
                onDeleteClick(holder, it, position, bean)
            }

            //启动图片浏览
            holder.clickItem {
                showLocalMediaPager(getFragmentManager(), position)
            }
        }
    }

    open fun onDeleteClick(holder: RBaseViewHolder, view: View, position: Int, bean: T) {
        deleteItem(bean)
    }

    override fun onDeleteItem(position: Int): Boolean {
        return super.onDeleteItem(position)
    }

    override fun onDeleteItemEnd(position: Int) {
        super.onDeleteItemEnd(position)
    }

    /**判断是否是视频*/
    open fun isVideoType(position: Int, bean: T): Boolean {
        if (bean is LocalMedia) {
            return bean.isVideoType
        }
        return false
    }

    /**获取图片路径, 支持本地or在线, 支持视频路径, 不需要特殊处理*/
    open fun getMediaPath(position: Int, bean: T): String? {
        if (bean is LocalMedia) {
            return bean.loadUrl
        }
        return null
    }

    open fun getFragmentManager(): androidx.fragment.app.FragmentManager? = null

    /**启动图片浏览*/
    open fun showLocalMediaPager(fragmentManager: androidx.fragment.app.FragmentManager?, startIndex: Int) {
        val startItem = allDatas[startIndex]

        val mediaList: List<LocalMedia>
        if (startItem is LocalMedia) {
            mediaList = allDatas as List<LocalMedia>
        } else {
            mediaList = MutableList(min(allDatas.size, maxItemCount)) {
                LocalMedia(
                    getMediaPath(it, allDatas[it]),
                    if (isVideoType(it, allDatas[it])) PictureConfig.TYPE_VIDEO else PictureConfig.TYPE_IMAGE
                )
            }
        }

        RPager.localMedia(fragmentManager) {
            localMediaList = mediaList as MutableList<LocalMedia>
            startPagerIndex = startIndex
            getPagerCount = { _, _ ->
                min(allDatas.size, maxItemCount)
            }
            onGetRecyclerView = {
                recyclerView
            }

            onItemLongClickListener = { fragment, _, viewHolder, position ->
                itemLongClickListener?.invoke(
                    fragment,
                    this@RImageAdapter,
                    viewHolder,
                    position,
                    if (position < mediaList.size) mediaList[position] else null
                ) == true
            }
        }
    }

    /**显示长按菜单*/
    open fun showLongMenu(context: Context, item: MutableList<Any>, callback: (index: Int, item: Any) -> Unit) {
        context.menuDialog {
            items = item
            showBottomCancelLayout = true

            onItemClick = { _, index, item ->
                callback(index, item)
                false
            }
        }
    }
}