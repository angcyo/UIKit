package com.angcyo.uiview.less.draw.frame;

import android.graphics.Canvas;
import android.view.View;

/**
 * Email:angcyo@126.com
 *
 * @author angcyo
 * @date 2019/01/25
 * Copyright (c) 2019 Shenzhen O&M Cloud Co., Ltd. All rights reserved.
 */
public interface IDrawFrame {
    /**
     * 准备工作回调, 在即将开始时触发
     *
     * @param view      所在的View
     * @param drawFrame 所在的BaseDraw
     * @param readyTime 时间
     */
    void onReady(View view, RDrawFrame drawFrame, long readyTime);

    /**
     * 每一帧都会触发回调, 用来更新绘制
     *
     * @param time 当前时间
     */
    void onUpdate(long time);

    /**
     * 用来绘制
     */
    void onDraw(Canvas canvas);

    /**
     * 是否绘制结束,
     *
     * @return true 会被移除绘制列表
     */
    boolean isDrawEnd(long time);

    void onRemove(long time);
}
