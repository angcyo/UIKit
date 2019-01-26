package com.angcyo.uiview.less.draw.frame.impl;

import android.graphics.Canvas;
import android.graphics.Color;
import android.view.View;
import com.angcyo.uiview.less.draw.frame.RDrawFrame;
import com.angcyo.uiview.less.resources.AnimUtil;

/**
 * 模拟效果 https://github.com/angcyo/android-ripple-background
 * <p>
 * Email:angcyo@126.com
 *
 * @author angcyo
 * @date 2019/01/25
 * Copyright (c) 2019 Shenzhen O&M Cloud Co., Ltd. All rights reserved.
 */
public class RippleFrameImpl extends BaseFrameImpl {

    /**
     * 圆的半径
     */
    float radius = 0L;

    int color;

    int step = 2;

    boolean isCyclic = false;

    public RippleFrameImpl(long delayTime, int color, int step) {
        super(delayTime);
        this.color = color;
        this.step = step;
    }

    @Override
    public void onReady(View view, RDrawFrame drawFrame, long readyTime) {
        super.onReady(view, drawFrame, readyTime);
        radius = 0L;
    }

    @Override
    protected void onDelayUpdate(long time) {
        super.onDelayUpdate(time);
        radius += step;
        if (isCyclic) {
            if (radius > drawFrame.minViewSize() / 2) {
                radius = 0;
                //readyTime = time;
            }
        }
    }

    @Override
    public boolean isDrawEnd(long time) {
        if (isCyclic) {
            return false;
        }
        return radius > drawFrame.minViewSize() / 2;
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (radius > 0) {
            int viewSize = drawFrame.minViewSize();
            drawFrame.mBasePaint.setColor(AnimUtil.evaluateColor((radius * 1f) / (viewSize * 1f / 2), color, Color.TRANSPARENT));
            canvas.drawCircle(drawFrame.drawCenterX(), drawFrame.drawCenterY(), radius, drawFrame.mBasePaint);
        }
    }

    public void setCyclic(boolean cyclic) {
        isCyclic = cyclic;
    }
}
