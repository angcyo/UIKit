package com.angcyo.uiview.less.draw.frame;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.view.View;
import com.angcyo.uiview.less.draw.BaseDraw;
import com.angcyo.uiview.less.resources.AnimUtil;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Email:angcyo@126.com
 *
 * @author angcyo
 * @date 2019/01/25
 * Copyright (c) 2019 Shenzhen O&M Cloud Co., Ltd. All rights reserved.
 */
public abstract class RDrawFrame extends BaseDraw implements ValueAnimator.AnimatorUpdateListener {

    /**
     * 保存每一帧 都需要绘制的元素
     */
    protected CopyOnWriteArrayList<IDrawFrame> frames = new CopyOnWriteArrayList<>();

    /**
     * 动画是60帧回调一次, 可以而外加一个值延迟. 延迟动画的回调
     */
    protected long animationDelayUpdateTime = 500L;

    public RDrawFrame(@NonNull View view) {
        super(view);
    }

    @Override
    public void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        for (IDrawFrame frame : frames) {
            frame.onDraw(canvas);
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stop();
    }

    /**
     * 添加元素
     */
    public RDrawFrame addFrame(IDrawFrame frame) {
        frames.add(frame);
        if (valueAnimator != null && valueAnimator.isStarted()) {
            long time = System.currentTimeMillis();
            frame.onReady(mView, this, time);
        }
        return this;
    }

    /**
     * 使用动画循环控制绘制. 动画帧率和手机刷新率一致.一般是 60帧
     */
    private ValueAnimator valueAnimator;

    protected long readyTime = 0L;
    protected long lastUpdateTime = 0L;

    public void start() {
        if (valueAnimator != null && valueAnimator.isStarted()) {
            return;
        }
        readyTime = System.currentTimeMillis();
        for (IDrawFrame frame : frames) {
            frame.onReady(mView, this, readyTime);
        }
        lastUpdateTime = readyTime;
        valueAnimator = AnimUtil.valueAnimator(this);
        valueAnimator.start();
    }

    /**
     * 停止绘制
     */
    public void stop() {
        if (valueAnimator != null) {
            valueAnimator.cancel();
            valueAnimator = null;
        }
        frames.clear();
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        long updateTime = System.currentTimeMillis();

        //判断是否需要移除
        for (int i = frames.size() - 1; i >= 0; i--) {
            IDrawFrame frame = frames.get(i);
            if (frame.isDrawEnd(updateTime)) {
                frames.remove(frame);

                long removeTime = System.currentTimeMillis();
                frame.onRemove(removeTime);

                onRemoveFrame(frame);
            }
        }

        //更新数据
        for (IDrawFrame frame : frames) {
            frame.onUpdate(updateTime);
        }

        //延迟
        if ((updateTime - lastUpdateTime) > animationDelayUpdateTime) {
            lastUpdateTime = updateTime;
            onAnimationDelayUpdate(animation);
        }

        postInvalidateOnAnimation();
    }

    protected void onRemoveFrame(IDrawFrame frame) {

    }

    /**
     * 动画延迟后的回调处理
     */
    protected void onAnimationDelayUpdate(ValueAnimator animation) {
    }
}
