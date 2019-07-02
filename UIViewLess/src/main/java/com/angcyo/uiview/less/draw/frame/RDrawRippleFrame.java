package com.angcyo.uiview.less.draw.frame;

import android.animation.ValueAnimator;
import android.graphics.Color;
import androidx.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import com.angcyo.uiview.less.draw.frame.impl.RippleFrameImpl;
import com.angcyo.uiview.less.resources.ResUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Email:angcyo@126.com
 *
 * @author angcyo
 * @date 2019/01/25
 * Copyright (c) 2019 Shenzhen O&M Cloud Co., Ltd. All rights reserved.
 */
public class RDrawRippleFrame extends RDrawFrame {

    /**
     * 波纹颜色
     */
    int rippleColor = Color.YELLOW;

    /**
     * 每个多少毫秒, 添加一个波纹
     */
    int frequency = 800;

    List<IDrawFrame> cacheList = new ArrayList<>();

    public RDrawRippleFrame(@NonNull View view) {
        super(view);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        addFrame();
        start();
    }

    @Override
    public void initAttribute(AttributeSet attr) {
        rippleColor = Color.parseColor("#52B2E0");//SkinHelper.getSkin().getThemeSubColor();
        animationDelayUpdateTime = frequency;
    }

    private void addFrame() {
        if (cacheList.isEmpty() || cacheList.size() < 6) {
            addFrame(new RippleFrameImpl(0, rippleColor, (int) ResUtil.dpToPx(1)));
        } else {
            addFrame(cacheList.remove(0));
        }
    }

    @Override
    protected void onAnimationDelayUpdate(ValueAnimator animation) {
        super.onAnimationDelayUpdate(animation);
        addFrame();
    }

    @Override
    protected void onRemoveFrame(IDrawFrame frame) {
        super.onRemoveFrame(frame);
        cacheList.add(frame);
    }
}
