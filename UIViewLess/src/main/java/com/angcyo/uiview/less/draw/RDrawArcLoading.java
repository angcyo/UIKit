package com.angcyo.uiview.less.draw;

import android.animation.ValueAnimator;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;

import com.angcyo.uiview.less.R;
import com.angcyo.uiview.less.resources.AnimUtil;
import com.angcyo.uiview.less.skin.SkinHelper;

import static android.view.View.VISIBLE;

/**
 * 2个扇形转圈, 然后变小的loading动画
 * <p>
 * Email:angcyo@126.com
 *
 * @author angcyo
 * @date 2019/03/05
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
public class RDrawArcLoading extends RSectionDraw {

    /**
     * 线宽
     */
    float strokeWidth;

    /**
     * 2个扇形, 间隙角度
     */
    float spaceAngle = 25;

    /**
     * 开始的绘制角度
     */
    float startAngle = 0f;

    //动画控制的角度
    float animAngle = 0f;

    int arcColor = Color.RED;

    /**
     * 动画时长
     */
    long duration = 2000;
    ValueAnimator valueAnimator;

    public RDrawArcLoading(@NonNull View view) {
        super(view);
    }

    @Override
    public void initAttribute(AttributeSet attr) {
        super.initAttribute(attr);
        setSections(new float[]{0.2f, 0.3f, 0.3f, 0.2f});
        strokeWidth = 3 * density();

        TypedArray array = obtainStyledAttributes(attr, R.styleable.RDrawArcLoading);

        if (isInEditMode()) {
            arcColor = Color.RED;
            progress = 50;
        } else {
            arcColor = SkinHelper.getSkin().getThemeColorAccent();
            arcColor = array.getColor(R.styleable.RDrawArcLoading_r_arc_color, arcColor);

            strokeWidth = array.getDimensionPixelOffset(R.styleable.RDrawArcLoading_r_arc_width, (int) strokeWidth);
        }

        duration = array.getInt(R.styleable.RDrawArcLoading_r_arc_duration, (int) duration);

        array.recycle();
    }

    @Override
    public void onDraw(@NonNull Canvas canvas) {
        //canvas.drawColor(Color.DKGRAY);
        super.onDraw(canvas);
    }

    @Override
    protected void onDrawSectionBefore(@NonNull Canvas canvas, int maxSection, float totalProgress) {
        super.onDrawSectionBefore(canvas, maxSection, totalProgress);
        mBasePaint.setAlpha(255);
    }

    @Override
    protected void onDrawProgressSection(@NonNull Canvas canvas, int index,
                                         float startProgress, float endProgress,
                                         float totalProgress, float sectionProgress) {
        super.onDrawProgressSection(canvas, index, startProgress, endProgress, totalProgress, sectionProgress);

        mDrawRectF.inset(strokeWidth / 2, strokeWidth / 2);

        //两个点, 分散效果
        if (index == 0) {
            mBasePaint.setColor(arcColor);
            mBasePaint.setStyle(Paint.Style.FILL);
            mBasePaint.setStrokeWidth(0f);
            mBasePaint.setStrokeCap(Paint.Cap.ROUND);

            float offset = mDrawRectF.width() / 2 * sectionProgress;

            canvas.drawCircle(mDrawRectF.centerX() - offset, mDrawRectF.centerY(), strokeWidth / 2, mBasePaint);
            canvas.drawCircle(mDrawRectF.centerX() + offset, mDrawRectF.centerY(), strokeWidth / 2, mBasePaint);
        } else if (index == 3) {
            //往里缩的效果

//            mBasePaint.setColor(Color.RED);
//            mBasePaint.setStyle(Paint.Style.FILL);
//            mBasePaint.setStrokeWidth(0f);
//            mBasePaint.setStrokeCap(Paint.Cap.ROUND);
//            mBasePaint.setAlpha((int) (255 * (1 - sectionProgress)));
//
//            float offset = mDrawRectF.width() / 2 * sectionProgress;
//            float offsetX = offset / 4;
//            float offsetY = offset / 2;
//
//            float xOffset = 10 * density();
//            float yOffset = 10 * density();
//            canvas.drawCircle(mDrawRectF.left + xOffset + offsetX, mDrawRectF.centerY() + yOffset - offsetY, strokeWidth / 2, mBasePaint);
//            canvas.drawCircle(mDrawRectF.right - xOffset - offsetX, mDrawRectF.centerY() - yOffset + offsetY, strokeWidth / 2, mBasePaint);

            mBasePaint.setColor(arcColor);
            mBasePaint.setStyle(Paint.Style.STROKE);
            mBasePaint.setStrokeWidth(strokeWidth * (1 - sectionProgress));
            mBasePaint.setStrokeCap(Paint.Cap.ROUND);
            mBasePaint.setAlpha((int) (255 * (1 - sectionProgress)));

            float inset = getViewDrawWidth() * sectionProgress;
            mDrawRectF.inset(inset, inset);

            animAngle = sectionProgress * 360;

            float startDrawAngle = animAngle + 130;
            float sweepAngle = 20;

            float ratio = 1 - sectionProgress;

            canvas.drawArc(mDrawRectF,
                    startDrawAngle, sweepAngle * ratio, false, mBasePaint);

            canvas.drawArc(mDrawRectF,
                    startDrawAngle + 180, sweepAngle * ratio, false, mBasePaint);
        } else {

            animAngle = sectionProgress * 180;

            mBasePaint.setColor(arcColor);
            mBasePaint.setStyle(Paint.Style.STROKE);
            mBasePaint.setStrokeWidth(strokeWidth);
            mBasePaint.setStrokeCap(Paint.Cap.ROUND);

            float startDrawAngle = animAngle - startAngle;
            float sweepAngle = (360 - 2 * spaceAngle) / 2;

            float ratio = 1f;

            if (index == 1) {
                //由小变大
                ratio = sectionProgress;
            } else if (index == 2) {
                //由大到小
                startDrawAngle += sweepAngle * sectionProgress;
                ratio = 1 - sectionProgress;
            }

            canvas.drawArc(mDrawRectF,
                    startDrawAngle, sweepAngle * ratio, false, mBasePaint);

            canvas.drawArc(mDrawRectF,
                    startDrawAngle + spaceAngle + sweepAngle, sweepAngle * ratio, false, mBasePaint);
        }


    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mView != null && mView.getVisibility() == VISIBLE) {
            onVisibilityChanged(mView, VISIBLE);
        }
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        endLoad();
    }

    @Override
    public void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == VISIBLE) {
            if (valueAnimator != null && valueAnimator.isStarted()) {
                return;
            }
            startLoad();
        } else {
            endLoad();
        }
    }

    private void startLoad() {
        valueAnimator = AnimUtil.valueAnimator(duration, new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setProgress((int) (animation.getAnimatedFraction() * 100));
            }
        });

        valueAnimator.start();
    }

    private void endLoad() {
        if (valueAnimator != null) {
            valueAnimator.cancel();
            valueAnimator = null;
        }
    }
}
