package com.angcyo.uiview.less.recycler.adapter

import android.app.Activity
import android.support.v4.app.FragmentManager
import com.angcyo.uiview.less.R
import com.angcyo.uiview.less.picture.RPager
import com.angcyo.uiview.less.recycler.RBaseViewHolder
import com.luck.picture.lib.entity.LocalMedia

/**图片适配*/
open class LocalMediaAdapter : RAddAdapter<LocalMedia>() {

    /**重写此方法, 选择媒体*/
    override fun onBindAddItemView(holder: RBaseViewHolder, position: Int, bean: LocalMedia?) {
        super.onBindAddItemView(holder, position, bean)

        holder.clickItem {
            (it.context as? Activity)?.let {

            }
        }
    }

    override fun onBindShowItemView(holder: RBaseViewHolder, position: Int, bean: LocalMedia?) {
        super.onBindShowItemView(holder, position, bean)
        bean?.let {
            holder.giv(R.id.image_view).apply {
                reset()
                url = bean.loadUrl
            }

            holder.visible(R.id.play_video_view, bean.isVideoType)
        }
    }

    open fun showLocalMediaPager(fragmentManager: FragmentManager?, startIndex: Int) {
        RPager.localMedia(fragmentManager) {
            localMediaList = allDatas
            startPagerIndex = startIndex
            onGetRecyclerView = {
                recyclerView
            }
        }
    }
}