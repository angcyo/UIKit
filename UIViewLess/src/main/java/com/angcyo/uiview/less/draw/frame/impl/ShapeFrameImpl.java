package com.angcyo.uiview.less.draw.frame.impl;

import android.graphics.Canvas;
import android.graphics.Color;
import com.angcyo.uiview.less.resources.AnimUtil;

/**
 * Email:angcyo@126.com
 *
 * @author angcyo
 * @date 2019/01/25
 * Copyright (c) 2019 Shenzhen O&M Cloud Co., Ltd. All rights reserved.
 */
public class ShapeFrameImpl extends BaseFrameImpl {

    /**
     * 圆的半径
     */
    float radius = 0L;

    int color;

    int alpha = 255;

    public ShapeFrameImpl(long delayTime, int color) {
        super(delayTime);
        this.color = color;
    }

    @Override
    protected void onDelayUpdate(long time) {
        super.onDelayUpdate(time);
        radius += 2;
//        if (radius > drawFrame.minViewSize() / 2) {
//            radius = 0;
//            //readyTime = time;
//        }
    }

    @Override
    public boolean isDrawEnd(long time) {
        return radius > drawFrame.minViewSize() / 2;
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (radius > 0) {
            drawFrame.mBasePaint.setColor(AnimUtil.evaluateColor((radius * 1f) / (drawFrame.minViewSize() / 2), color, Color.TRANSPARENT));
            canvas.drawCircle(drawFrame.drawCenterX(), drawFrame.drawCenterY(), radius, drawFrame.mBasePaint);
        }
    }
}
