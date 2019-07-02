package com.angcyo.uiview.less.recycler.adapter

import android.app.Activity
import androidx.fragment.app.FragmentManager
import com.angcyo.uiview.less.R
import com.angcyo.uiview.less.picture.RPager
import com.angcyo.uiview.less.recycler.RBaseViewHolder
import com.luck.picture.lib.entity.LocalMedia

/**图片适配*/
open class LocalMediaAdapter : RAddAdapter<LocalMedia>() {

    /**重写此方法, 选择媒体*/
    override fun onBindAddItemView(holder: RBaseViewHolder, position: Int, bean: LocalMedia?) {
        super.onBindAddItemView(holder, position, bean)

        //nothing
        holder.clickItem {
            (it.context as? Activity)?.let {

            }
        }
    }
}