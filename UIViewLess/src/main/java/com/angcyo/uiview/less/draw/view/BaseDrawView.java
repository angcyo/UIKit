package com.angcyo.uiview.less.draw.view;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import com.angcyo.uiview.less.draw.BaseDraw;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 实现 BaseDraw 的基类
 * Email:angcyo@126.com
 *
 * @author angcyo
 * @date 2019/01/11
 */
public class BaseDrawView<T extends BaseDraw> extends View {

    protected T baseDraw;

    public BaseDrawView(Context context) {
        this(context, null);
    }

    public BaseDrawView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseDrawView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initBaseDraw(context, attrs);
    }

    protected void initBaseDraw(Context context, @Nullable AttributeSet attrs) {
        try {
            Type genericSuperclass = this.getClass().getGenericSuperclass();
            if (genericSuperclass instanceof ParameterizedType) {
                Constructor constructor = ((Class) ((ParameterizedType) genericSuperclass)
                        .getActualTypeArguments()[0])
                        .getConstructor(View.class);
                baseDraw = (T) constructor.newInstance(this);
                baseDraw.initAttribute(attrs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //测量BaseDraw的大小
        //baseDraw.measureDraw(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        baseDraw.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        baseDraw.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        baseDraw.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        baseDraw.onDetachedFromWindow();
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        baseDraw.onVisibilityChanged(changedView, visibility);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        baseDraw.draw(canvas);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        baseDraw.onDraw(canvas);
    }
}
