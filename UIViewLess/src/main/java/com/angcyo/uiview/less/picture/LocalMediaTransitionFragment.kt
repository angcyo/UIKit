package com.angcyo.uiview.less.picture

import com.angcyo.uiview.less.picture.transition.ViewTransitionConfig
import com.luck.picture.lib.entity.LocalMedia

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/06/03
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
open class LocalMediaTransitionFragment : PagerTransitionFragment() {

    init {
        transitionConfig = LocalMediaTransitionConfig()
    }

    inner class LocalMediaTransitionConfig : ViewTransitionConfig() {
        var localMediaList = mutableListOf<LocalMedia>()

        init {
            getPagerCount = { _, _ ->
                Math.max(pagerCount, localMediaList.size)
            }

            onGetPagerMediaUrl = {
                localMediaList[it].loadUrl
            }

            onGetMediaType = {
                if (localMediaList[it].isVideoType) {
                    MEDIA_TYPE_VIDEO
                } else {
                    MEDIA_TYPE_IMAGE
                }
            }
        }
    }
}