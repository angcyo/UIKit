package com.angcyo.uiview.less.draw.view;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import com.angcyo.uiview.less.draw.BaseDraw;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 实现 BaseDraw 的基类
 * Email:angcyo@126.com
 *
 * @author angcyo
 * @date 2019/01/11
 */
public class BaseDrawView<T extends BaseDraw> extends View {

    protected T baseDraw;

    protected List<BaseDraw> exDrawList = new ArrayList<>();

    public BaseDrawView(Context context) {
        this(context, null);
    }

    public BaseDrawView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseDrawView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initBaseDraw(context, attrs);
        initExDraw(context, attrs);
    }

    protected void initBaseDraw(Context context, @Nullable AttributeSet attrs) {
        try {
            Class cls = this.getClass();
            Type genericSuperclass = cls.getGenericSuperclass();

            while (!(genericSuperclass instanceof ParameterizedType)) {
                cls = cls.getSuperclass();
                if (cls == null || TextUtils.equals(cls.getSimpleName(), View.class.getSimpleName())) {
                    break;
                }
                genericSuperclass = cls.getGenericSuperclass();
            }

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

    protected void initExDraw(Context context, @Nullable AttributeSet attrs) {
        for (BaseDraw baseDraw : exDrawList) {
            baseDraw.initAttribute(attrs);
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
        for (BaseDraw baseDraw : exDrawList) {
            baseDraw.onLayout(changed, left, top, right, bottom);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        baseDraw.onSizeChanged(w, h, oldw, oldh);
        for (BaseDraw baseDraw : exDrawList) {
            baseDraw.onSizeChanged(w, h, oldw, oldh);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        baseDraw.onAttachedToWindow();
        for (BaseDraw baseDraw : exDrawList) {
            baseDraw.onAttachedToWindow();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        baseDraw.onDetachedFromWindow();
        for (BaseDraw baseDraw : exDrawList) {
            baseDraw.onDetachedFromWindow();
        }
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        baseDraw.onVisibilityChanged(changedView, visibility);
        for (BaseDraw baseDraw : exDrawList) {
            baseDraw.onVisibilityChanged(changedView, visibility);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        baseDraw.draw(canvas);
        for (BaseDraw baseDraw : exDrawList) {
            baseDraw.draw(canvas);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        baseDraw.onDraw(canvas);
        for (BaseDraw baseDraw : exDrawList) {
            baseDraw.onDraw(canvas);
        }
    }

    public T getBaseDraw() {
        return baseDraw;
    }
}
