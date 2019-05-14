package com.angcyo.uiview.less.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.animation.LinearInterpolator;
import android.widget.OverScroller;
import com.angcyo.uiview.less.utils.RUtils;
import org.jetbrains.annotations.Nullable;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：让图片在当前可视区域平滑移动显示, 类似QQ资料页背景图片效果
 * 创建人员：Robi
 * 创建时间：2018/05/03 15:40
 * 修改人员：Robi
 * 修改时间：2018/05/03 15:40
 * 修改备注：
 * Version: 1.0.0
 */
public class FlatImageView extends GlideImageView {

    private static final int STATE_BACKWARD = 2;
    private static final int STATE_FORWARD = 1;
    private OverScroller mOverScroller = new OverScroller(getContext(), new LinearInterpolator());
    //绘制时的值
    private float drawScrollX = 0f;
    private float drawScrollY = 0f;
    //目标滚动值
    private int targetScrollX = 0;
    private int targetScrollY = 0;
    private boolean startFlat = false;
    private int scrollState = STATE_FORWARD;

    private int scrollDuration = 5000;

    public FlatImageView(Context context) {
        super(context, null);
    }

    public FlatImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();

        if (startFlat && isReady()) {
            if (mOverScroller.computeScrollOffset() && startFlat) {

                postInvalidate();

//            if (targetScrollX > 0 || targetScrollY > 0) {
//                if (mOverScroller.getCurrX() >= 0 ||
//                        mOverScroller.getCurrY() >= 0) {
//                    forward();
//                }
//            } else if (targetScrollX < 0 || targetScrollY < 0) {
//                if (mOverScroller.getCurrX() <= targetScrollX ||
//                        mOverScroller.getCurrY() <= targetScrollY) {
//                    //反向滚动
//                    backward();
//                }
//            }
            } else {
                if (scrollState == STATE_FORWARD) {
                    backward();
                } else {
                    forward();
                }
            }
        }


    }

    @Override
    public void setImageDrawable(@Nullable Drawable drawable) {
        super.setImageDrawable(drawable);
        if (startFlat && isReady() && !mOverScroller.computeScrollOffset()) {
            forward();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        setRight(drawableWidth());
        setBottom(drawableHeight());
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setScaleType(ScaleType.FIT_XY);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (startFlat && isReady()) {
            forward();
        }
    }

    private boolean isVerticalScroller() {
        float wf = drawableWidth() * 1f / getMeasuredWidth();
        float hf = drawableHeight() * 1f / getMeasuredHeight();

        return hf >= wf;
    }

    /**
     * 图片前进滚动, 那么canvas, 就要负向 translate
     */
    public void forward() {
        scrollState = STATE_FORWARD;

        if (isVerticalScroller()) {
            targetScrollX = 0;
            targetScrollY = -drawableHeight() + getMeasuredHeight();
        } else {
            targetScrollY = 0;
            targetScrollX = -drawableWidth() + getMeasuredWidth();
        }
        startScroller();
    }

    public void backward() {
        scrollState = STATE_BACKWARD;

        if (isVerticalScroller()) {
            targetScrollX = 0;
            targetScrollY = drawableHeight() - getMeasuredHeight();
        } else {
            targetScrollY = 0;
            targetScrollX = drawableWidth() - getMeasuredWidth();
        }
        startScroller();
    }

    private void startScroller() {
        mOverScroller.startScroll(((int) drawScrollX), (int) drawScrollY, targetScrollX, targetScrollY, scrollDuration);
        postInvalidate();
    }

    private int drawableHeight() {
        Drawable drawable = getDrawable();
        if (drawable == null) {
            return 0;
        }
        return (int) (drawable.getIntrinsicHeight() * RUtils.density());
    }

    private int drawableWidth() {
        Drawable drawable = getDrawable();
        if (drawable == null) {
            return 0;
        }
        return (int) (drawable.getIntrinsicWidth() * RUtils.density());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (startFlat && isReady()) {
            Drawable drawable = getDrawable();
            if (drawable == null || drawable == getPlaceholderDrawable()) {
                mOverScroller.abortAnimation();
                super.onDraw(canvas);
                return;
            }

            canvas.save();
            canvas.translate(mOverScroller.getCurrX(), mOverScroller.getCurrY());
            drawable.setBounds(0, 0,
                    drawableWidth(), drawableHeight());
//        canvas.translate(200, 200);
            super.onDraw(canvas);
//            drawable.draw(canvas);
            canvas.restore();

            drawScrollX = mOverScroller.getCurrX();
            drawScrollY = mOverScroller.getCurrY();
        } else {
            super.onDraw(canvas);
        }
    }

    public void startFlat(boolean startFlat) {
        this.startFlat = startFlat;
        if (isReady()) {
            forward();
        }
    }

    private boolean isReady() {
        return getDrawable() != null && getMeasuredHeight() > 0 && getMeasuredWidth() > 0;
    }

    @Override
    public void onLoadSuccess() {
        super.onLoadSuccess();
        startFlat(true);
    }

    @Override
    public void onLoadFailed() {
        //super.onLoadFailed();
        showPlaceholderDrawable();
        setLoadSuccessUrl(getUrl());
    }
}
