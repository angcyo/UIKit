package com.angcyo.uiview.less.draw.frame;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import com.angcyo.uiview.less.draw.view.BaseDrawView;

/**
 * Email:angcyo@126.com
 *
 * @author angcyo
 * @date 2019/01/25
 * Copyright (c) 2019 Shenzhen O&M Cloud Co., Ltd. All rights reserved.
 */
public class FrameView extends BaseDrawView<RDrawFrame> {

    RDrawFrame drawFrame;

    public FrameView(Context context) {
        super(context);
    }

    public FrameView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FrameView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void initBaseDraw(Context context, @Nullable AttributeSet attrs) {
        super.initBaseDraw(context, attrs);
        drawFrame = baseDraw;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
