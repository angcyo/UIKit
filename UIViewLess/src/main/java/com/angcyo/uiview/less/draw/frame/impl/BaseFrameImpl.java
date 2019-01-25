package com.angcyo.uiview.less.draw.frame.impl;

import android.graphics.Canvas;
import android.view.View;
import com.angcyo.uiview.less.draw.frame.IDrawFrame;
import com.angcyo.uiview.less.draw.frame.RDrawFrame;

/**
 * Email:angcyo@126.com
 *
 * @author angcyo
 * @date 2019/01/25
 * Copyright (c) 2019 Shenzhen O&M Cloud Co., Ltd. All rights reserved.
 */
public abstract class BaseFrameImpl implements IDrawFrame {

    /**
     * 延迟多久开始更新数据
     */
    protected long delayTime = 0L;

    protected long readyTime;
    protected View view;
    protected RDrawFrame drawFrame;

    protected Runnable onRemoveRunnable;

    public BaseFrameImpl() {
    }

    public BaseFrameImpl(long delayTime) {
        this.delayTime = delayTime;
    }

    @Override
    public void onReady(View view, RDrawFrame drawFrame, long readyTime) {
        this.view = view;
        this.readyTime = readyTime;
        this.drawFrame = drawFrame;
    }

    @Override
    public void onUpdate(long time) {
        if (delayTime > 0 && time - readyTime <= delayTime) {

        } else {
            onDelayUpdate(time);
        }
    }

    protected void onDelayUpdate(long time) {

    }

    @Override
    public void onDraw(Canvas canvas) {

    }

    @Override
    public boolean isDrawEnd(long time) {
        return false;
    }

    @Override
    public void onRemove(long time) {
        if (onRemoveRunnable != null) {
            onRemoveRunnable.run();
        }
    }

    public BaseFrameImpl setOnRemoveRunnable(Runnable onRemoveRunnable) {
        this.onRemoveRunnable = onRemoveRunnable;
        return this;
    }
}
