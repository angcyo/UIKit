package com.angcyo.uiview.less.draw;

import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import androidx.annotation.NonNull;

import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.angcyo.uiview.less.R;
import com.angcyo.uiview.less.kotlin.ExKt;
import com.angcyo.uiview.less.kotlin.ViewExKt;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：未读红点绘制
 * 创建人员：Robi
 * 创建时间：2018/04/19 09:58
 * 修改人员：Robi
 * 修改时间：2018/04/19 09:58
 * 修改备注：
 * Version: 1.0.0
 */
public class RDrawNoRead extends BaseDraw {

    public static final int LEFT = 0x01;
    public static final int TOP = 0x02;
    public static final int RIGHT = 0x04;
    public static final int BOTTOM = 0x08;
    public static final int CENTER_VERTICAL = 0x10;
    public static final int CENTER_HORIZONTAL = 0x20;
    public static final int CENTER = 0x30;

    /**
     * 是否显示 未读小红点
     */
    private boolean showNoRead = false;
    /**
     * 绘制的时候, 是否要排除 l t r b 的drawable宽高
     */
    private boolean offsetDrawable = true;
    /**
     * 绘制的时候, 是否要排除 padding
     */
    private boolean offsetPadding = false;
    /**
     * 小红点半径
     */
    private float noReadRadius = 4 * density();
    private float noReadPaddingTop = 0 * density();
    private float noReadPaddingRight = 0 * density();

    private int noReadColor = Color.RED;

    private Paint mPaint;

    private int noreadGravity = TOP | RIGHT;

    public RDrawNoRead(@NonNull View view) {
        super(view);
    }

    public RDrawNoRead(View view, AttributeSet attr) {
        super(view, attr);
        initAttribute(attr);
    }

    @Override
    public void initAttribute(AttributeSet attr) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attr, R.styleable.RDrawNoRead);
        showNoRead = typedArray.getBoolean(R.styleable.RDrawNoRead_r_show_noread, showNoRead);
        noReadRadius = typedArray.getDimensionPixelOffset(R.styleable.RDrawNoRead_r_noread_radius, (int) noReadRadius);
        noreadGravity = typedArray.getInt(R.styleable.RDrawNoRead_r_noread_gravity, noreadGravity);
        noReadPaddingRight = typedArray.getDimensionPixelOffset(R.styleable.RDrawNoRead_r_noread_padding_right, (int) noReadPaddingRight);
        noReadPaddingTop = typedArray.getDimensionPixelOffset(R.styleable.RDrawNoRead_r_noread_padding_top, (int) noReadPaddingTop);
        noReadColor = typedArray.getColor(R.styleable.RDrawNoRead_r_noread_color, noReadColor);
        offsetDrawable = typedArray.getBoolean(R.styleable.RDrawNoRead_r_offset_drawable, offsetDrawable);
        offsetPadding = typedArray.getBoolean(R.styleable.RDrawNoRead_r_offset_padding, offsetPadding);

        typedArray.recycle();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (showNoRead /*|| isInEditMode()*/) {
            //未读小红点
            if (mPaint == null) {
                mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            }
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(noReadColor);

            float cx = noReadRadius;
            float cy = noReadRadius;

            if (noreadGravity == CENTER) {
                cx = ViewExKt.getDrawCenterCx(mView) + noReadPaddingRight;
                cy = ViewExKt.getDrawCenterCy(mView) + noReadPaddingTop;
            } else {
                if (ExKt.have(noreadGravity, CENTER_HORIZONTAL)) {
                    cx = ViewExKt.getDrawCenterCx(mView) + noReadPaddingRight;
                }
                if (ExKt.have(noreadGravity, CENTER_VERTICAL)) {
                    cy = ViewExKt.getDrawCenterCy(mView) + noReadPaddingTop;
                }
                if (ExKt.have(noreadGravity, LEFT)) {
                    cx = noReadRadius + noReadPaddingRight + getDrawableOffset(0) + getPaddingOffset(0);
                }
                if (ExKt.have(noreadGravity, RIGHT)) {
                    cx = getViewWidth() - noReadRadius - noReadPaddingRight - getDrawableOffset(2) - getPaddingOffset(2);
                }
                if (ExKt.have(noreadGravity, TOP)) {
                    cy = noReadRadius + noReadPaddingTop + getDrawableOffset(1) + getPaddingOffset(1);
                }
                if (ExKt.have(noreadGravity, BOTTOM)) {
                    cy = getViewHeight() - noReadRadius - noReadPaddingTop - getDrawableOffset(3) - getPaddingOffset(3);
                }
            }
            canvas.drawCircle(cx, cy, noReadRadius, mPaint);
        }
    }

    private int getDrawableOffset(int pos) {
        if (offsetDrawable && mView instanceof TextView) {
            Drawable[] compoundDrawables = ((TextView) mView).getCompoundDrawables();
            Drawable drawable = compoundDrawables[pos];
            if (drawable != null) {
                if (pos == 0 || pos == 2) {
                    int intrinsicWidth = drawable.getIntrinsicWidth();
                    return intrinsicWidth + ((TextView) mView).getCompoundDrawablePadding();
                } else {
                    int intrinsicHeight = drawable.getIntrinsicHeight();
                    return intrinsicHeight + ((TextView) mView).getCompoundDrawablePadding();
                }
            }
        }
        return 0;
    }

    private int getPaddingOffset(int pos) {
        if (offsetPadding) {
            switch (pos) {
                case 1:
                    return getPaddingTop();
                case 2:
                    return getPaddingRight();
                case 3:
                    return getPaddingBottom();
                default:
                    return getPaddingLeft();
            }
        }
        return 0;
    }

    /**
     * 默认显示在右上角
     */
    public void setShowNoRead(boolean showNoRead) {
        this.showNoRead = showNoRead;
        postInvalidate();
    }

    /**
     * 半径大小
     */
    public void setNoReadRadius(float noReadRadius) {
        this.noReadRadius = noReadRadius;
        postInvalidate();
    }

    /**
     * 右上角的padding
     */
    public void setNoReadPaddingTop(float noReadPaddingTop) {
        this.noReadPaddingTop = noReadPaddingTop;
        postInvalidate();
    }

    /**
     * 右上角的padding
     */
    public void setNoReadPaddingRight(float noReadPaddingRight) {
        this.noReadPaddingRight = noReadPaddingRight;
        postInvalidate();
    }

    public void setNoReadColor(int noReadColor) {
        this.noReadColor = noReadColor;
        postInvalidate();
    }

    public void setNoreadGravity(int noreadGravity) {
        this.noreadGravity = noreadGravity;
        postInvalidate();
    }

    public void setOffsetDrawable(boolean offsetDrawable) {
        this.offsetDrawable = offsetDrawable;
        postInvalidate();
    }
}
