package com.angcyo.uiview.less.picture;

import android.support.annotation.NonNull;
import android.view.View;
import com.angcyo.uiview.less.recycler.RBaseViewHolder;
import com.angcyo.uiview.less.widget.pager.RPagerAdapter;

/**
 * Email:angcyo@126.com
 *
 * @author angcyo
 * @date 2019/03/12
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
public class PhotoPagerAdapter extends RPagerAdapter {

    PhotoDataSource photoDataSource;

    public PhotoPagerAdapter(PhotoDataSource photoDataSource) {
        this.photoDataSource = photoDataSource;
    }

    @Override
    protected int getLayoutId(int position, int itemType) {
        return photoDataSource.getLayoutId(position, itemType);
    }

    @Override
    public int getCount() {
        if (photoDataSource == null) {
            return 0;
        }
        return photoDataSource.getCount();
    }

    @Override
    protected void initItemView(@NonNull View rootView, int position, int itemType) {
        //photoDataSource.initItemView(rootView, position, itemType);
    }

    @Override
    protected void initItemView(@NonNull RBaseViewHolder viewHolder, int position, int itemType) {
        photoDataSource.initItemView(viewHolder, position, itemType);
    }
}
