package com.angcyo.uiview.less.resources;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.*;
import android.os.Build;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Shape 动态创建类
 * Email:angcyo@126.com
 *
 * @author angcyo
 * @date 2019/01/23
 * Copyright (c) 2019 Shenzhen O&M Cloud Co., Ltd. All rights reserved.
 * @since 1.0
 */
public class RDrawable {
    public static final int NO_INT = -0xFFFFFF;

    //<editor-fold desc="内部成员">

    private Context context;

    /**
     * 保存状态对应的Drawable
     */
    private LinkedHashMap<Integer, Drawable> stateLinkedHashMap = new LinkedHashMap<>();
    /**
     * 保存layer对应的Drawable
     */
    private LinkedHashMap<Integer, Drawable> layerLinkedHashMap = new LinkedHashMap<>();

    private Drawable normalDrawable;

    /**
     * 没次调用doIt, 都将结果保存起来备用
     */
    private Drawable lastDrawable;

    //</editor-fold desc="内部成员">

    private RDrawable(Context context) {
        this.context = context;
        reset();
    }

    public static RDrawable get(@NonNull Context context) {
        return new RDrawable(context);
    }

    /**
     *
     * 默认使用 GradientDrawable 创建所需要的 Drawable
     *
     * 某些效果, 可能需要 LayerDrawable 配合使用
     *
     * 波纹效果, 必须使用 RippleDrawable 且 API>=21
     *
     * */

    //<editor-fold desc="GradientDrawable 相关属性">

    /**
     * 在设置TextView的leftDrawable等时, 大小位置属性, 就很重要.
     */
    private int width = NO_INT;
    private int height = NO_INT;
    private int left = NO_INT;
    private int top = NO_INT;

    /**
     * 四个角, 8个设置点的圆角信息
     * 从 左上y轴->左上x轴->右上x轴->右上y轴..., 开始设置.
     */
    private float[] radii = new float[8];

    /**
     * 蚂蚁线的宽度
     */
    private float dashWidth = 0f;
    /**
     * 蚂蚁线的间距
     */
    private float dashGap = 0f;

    /**
     * 形状 (GradientDrawable.RING 暂不属性)
     */
    @Shape
    private int shape = GradientDrawable.RECTANGLE;

    /**
     * 通常用来设置画笔的宽度
     */
    private int strokeWidth = NO_INT;

    /**
     * 边框颜色
     */
    private int strokeColor = Color.TRANSPARENT;

    /**
     * 填充颜色
     */
    private int solidColor = NO_INT;

    private Shader strokeShader = null;
    private Shader fillShader = null;

    //</editor-fold>

    //<editor-fold desc="GradientDrawable 渐变相关属性">

    /**
     * 渐变类型
     */
    @GradientType
    private int gradientType = GradientDrawable.LINEAR_GRADIENT;
    /**
     * 渐变中心点坐标
     */
    private float gradientCenterX = 0.5f;
    private float gradientCenterY = 0.5f;
    /**
     * 半径
     */
    private float gradientRadius = 0.5f;

    /**
     * 渐变颜色
     */
    private int[] gradientColors;

    /**
     * 渐变方向, 默认从左到右
     */
    private GradientDrawable.Orientation gradientOrientation = GradientDrawable.Orientation.LEFT_RIGHT;

    //</editor-fold desc="GradientDrawable 渐变相关属性">


    //<editor-fold desc="LayerDrawable 相关属性">

    /**
     * 重力属性, 通常需要配合 LayerDrawable 才能生效.
     * <p>
     * 此属性无法控制 shape为line时的位置 的位置
     * 请使用 inset的方式, 让 rectangle 偏移到视图外, 障眼法
     */
    @Deprecated
    private int gravity = NO_INT;

    private int layerInsetLeft = 0;
    private int layerInsetRight = 0;
    private int layerInsetBottom = 0;
    private int layerInsetTop = 0;

    //</editor-fold>

    //<editor-fold desc="操作属性">

    /**
     * 重置所有值到初始化状态
     */
    public RDrawable reset() {
        resetGradientValue();
        resetLayoutValue();
        resetRippleValue();

        //lastDrawable 为null时, 调用normal()方法, 会调用一次doIt()
        lastDrawable = null;
        return this;
    }

    private void resetGradientValue() {
        //重新分配内存, 否则会覆盖原来的值
        radii = new float[8];
        shape = GradientDrawable.RECTANGLE;
        width = NO_INT;
        height = NO_INT;
        left = NO_INT;
        top = NO_INT;
        strokeWidth = NO_INT;
        strokeColor = Color.TRANSPARENT;
        solidColor = NO_INT;
        dashGap = 0f;
        dashWidth = 0f;

        gradientType = GradientDrawable.LINEAR_GRADIENT;
        gradientCenterX = 0.5f;
        gradientCenterY = 0.5f;
        gradientRadius = 0.5f;
        gradientColors = null;
        gradientOrientation = GradientDrawable.Orientation.LEFT_RIGHT;

        strokeShader = null;
        fillShader = null;
    }

    private void resetLayoutValue() {
        gravity = NO_INT;
        layerInsetLeft = 0;
        layerInsetRight = 0;
        layerInsetBottom = 0;
        layerInsetTop = 0;
    }

    /**
     * 边框的宽度
     */
    public RDrawable strokeWidth(int width /*px*/) {
        strokeWidth = width;
        return this;
    }

    public RDrawable stroke(int width /*px*/, int color) {
        strokeWidth(width);
        strokeColor(color);
        return this;
    }

    public RDrawable stroke(int width /*px*/, int color, float dashWidth, float dashGap) {
        strokeWidth(width);
        strokeColor(color);
        dashWidth(dashWidth);
        dashGap(dashGap);
        return this;
    }

    public RDrawable strokeColor(int color) {
        strokeColor = color;
        return this;
    }

    public RDrawable dashWidth(float dashWidth) {
        this.dashWidth = dashWidth;
        return this;
    }

    public RDrawable dashGap(float dashGap) {
        this.dashGap = dashGap;
        return this;
    }

    public RDrawable shape(@Shape int shape) {
        this.shape = shape;
        return this;
    }

    public RDrawable gradientType(@GradientType int gradientType) {
        this.gradientType = gradientType;
        return this;
    }

    public RDrawable gradientCenter(float gradientCenterX, float gradientCenterY) {
        this.gradientCenterX = gradientCenterX;
        this.gradientCenterY = gradientCenterY;
        return this;
    }

    public RDrawable gradientRadius(float gradientRadius) {
        this.gradientRadius = gradientRadius;
        return this;
    }

    public RDrawable gradientColors(int[] gradientColors) {
        this.gradientColors = gradientColors;
        return this;
    }

    public RDrawable gradientColors(int startColor, int endColor) {
        gradientColors(new int[]{startColor, endColor});
        return this;
    }

    public RDrawable gradientOrientation(GradientDrawable.Orientation gradientOrientation) {
        this.gradientOrientation = gradientOrientation;
        return this;
    }

    /**
     * 填充颜色
     */
    public RDrawable solidColor(int color) {
        solidColor = color;
        return this;
    }

    public RDrawable circle() {
        shape(GradientDrawable.OVAL);
        return this;
    }

    public RDrawable circle(int color) {
        shape(GradientDrawable.OVAL);
        solidColor(color);
        return this;
    }

    public RDrawable strokeShader(Shader strokeShader) {
        this.strokeShader = strokeShader;
        return this;
    }

    public RDrawable fillShader(Shader fillShader) {
        this.fillShader = fillShader;
        return this;
    }

    //</editor-fold desc="操作属性">

    //<editor-fold desc="大小位置 操作">

    /**
     * 使用background设置drawable时, 大小属性无效
     * 但是在 TextView的leftDrawable时, 至关重要. 因为没有大小属性, drawable不会显示
     */
    public RDrawable width(int width) {
        this.width = width;
        this.height = -1;
        return this;
    }

    public RDrawable height(int height) {
        this.width = -1;
        this.height = height;
        return this;
    }

    public RDrawable size(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }

    public RDrawable size(int left, int top, int width, int height) {
        this.left = left;
        this.top = top;
        this.width = width;
        this.height = height;
        return this;
    }
    //</editor-fold desc="大小位置 操作">

    //<editor-fold desc="圆角相关配置">

    /**
     * 4个角, 8个点 圆角配置
     */
    public RDrawable cornerRadii(float[] radii) {
        this.radii = radii;
        return this;
    }

    public RDrawable cornerRadius(float radii) {
        Arrays.fill(this.radii, radii);
        return this;
    }

    /**
     * 只配置左边的圆角
     */
    public RDrawable cornerRadiiLeft(float radii) {
        this.radii[0] = radii;
        this.radii[1] = radii;
        this.radii[6] = radii;
        this.radii[7] = radii;
        return this;
    }

    public RDrawable cornerRadiiRight(float radii) {
        this.radii[2] = radii;
        this.radii[3] = radii;
        this.radii[4] = radii;
        this.radii[5] = radii;
        return this;
    }

    public RDrawable cornerRadiiTop(float radii) {
        this.radii[0] = radii;
        this.radii[1] = radii;
        this.radii[2] = radii;
        this.radii[3] = radii;
        return this;
    }

    public RDrawable cornerRadiiBottom(float radii) {
        this.radii[4] = radii;
        this.radii[5] = radii;
        this.radii[6] = radii;
        this.radii[7] = radii;
        return this;
    }

    //</editor-fold desc="圆角相关配置">


    //<editor-fold desc="Drawable状态配置">

    /**
     * 将当前的配置信息产生的Drawable, 当做 press状态
     */
    public RDrawable pressed(boolean enable) {
        stateSet(android.R.attr.state_pressed, enable, doIt());
        return this;
    }

    /**
     * 焦点状态
     */
    public RDrawable focused(boolean enable) {
        stateSet(android.R.attr.state_focused, enable, doIt());
        return this;
    }

    /**
     * 激活状态
     */
    public RDrawable enabled(boolean enable) {
        stateSet(android.R.attr.state_enabled, enable, doIt());
        return this;
    }

    /**
     * 选中状态
     */
    public RDrawable selected(boolean enable) {
        stateSet(android.R.attr.state_selected, enable, doIt());
        return this;
    }

    /**
     * 勾选状态
     */
    public RDrawable checked(boolean enable) {
        stateSet(android.R.attr.state_checked, enable, doIt());
        return this;
    }

    /**
     * 使用上一次的状态, 同时设置其他状态
     */
    public RDrawable andPressed(boolean enable) {
        stateSet(android.R.attr.state_pressed, enable, lastDrawable);
        return this;
    }


    public RDrawable andFocused(boolean enable) {
        stateSet(android.R.attr.state_focused, enable, lastDrawable);
        return this;
    }

    public RDrawable andEnabled(boolean enable) {
        stateSet(android.R.attr.state_enabled, enable, lastDrawable);
        return this;
    }


    public RDrawable andSelected(boolean enable) {
        stateSet(android.R.attr.state_selected, enable, lastDrawable);
        return this;
    }

    public RDrawable andChecked(boolean enable) {
        stateSet(android.R.attr.state_checked, enable, lastDrawable);
        return this;
    }

    /**
     * 开放对外设置方法
     */
    public RDrawable stateSet(int attr, boolean enable, Drawable drawable) {
        if (enable) {
            stateLinkedHashMap.put(attr, drawable);
        } else {
            stateLinkedHashMap.put(-attr, drawable);
        }
        return this;
    }

    /**
     * 设置正常状态的drawable
     */
    public RDrawable normal() {
        if (lastDrawable == null) {
            doIt();
        }
        normalDrawable = lastDrawable;
        return this;
    }


    //</editor-fold desc="Drawable状态配置">

    //<editor-fold desc="Ripple 相关方法和属性">

    private int rippleColor = Color.WHITE;

    private Drawable rippleMaskDrawable;
    private Drawable rippleContentDrawable;

    private void resetRippleValue() {
        rippleColor = Color.WHITE;
    }

    public RDrawable rippleColor(int color) {
        this.rippleColor = color;
        return this;
    }

    public RDrawable andRippleContent() {
        if (lastDrawable == null) {
            doIt();
        }
        rippleContentDrawable = lastDrawable;
        return this;
    }

    public RDrawable andRippleMask() {
        if (lastDrawable == null) {
            doIt();
        }
        rippleMaskDrawable = lastDrawable;
        return this;
    }

    /**
     * 打成RippleDrawable, 设置给lastDrawable
     */
    public RDrawable andRipple() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            lastDrawable = new RippleDrawable(ColorStateList.valueOf(rippleColor), rippleContentDrawable, rippleMaskDrawable);
        }
        return this;
    }

    /**
     * 创建具有Ripple效果的drawable.
     * <p>
     * 如果版本不支持时, 返回 state()
     */
    public Drawable ripple() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            lastDrawable = new RippleDrawable(ColorStateList.valueOf(rippleColor), rippleContentDrawable, rippleMaskDrawable);
        } else {
            if (stateLinkedHashMap.isEmpty()) {
                if (normalDrawable == null) {
                    if (rippleContentDrawable != null && rippleMaskDrawable != null) {
                        normalDrawable = rippleContentDrawable;
                    }
                }

                if (rippleContentDrawable != null && rippleMaskDrawable != null) {
                    lastDrawable = rippleMaskDrawable;
                    andPressed(true);
                } else {
                    if (rippleContentDrawable != null) {
                        lastDrawable = rippleContentDrawable;
                        andPressed(true);
                    } else if (rippleMaskDrawable != null) {
                        lastDrawable = rippleMaskDrawable;
                        andPressed(true);
                    }
                }
            }
            lastDrawable = state();
        }
        return lastDrawable;
    }

    //</editor-fold desc="Ripple 相关方法和属性">

    //<editor-fold desc="LayerDrawable 相关方法和属性">

    private LayerDrawable layerDrawable;

    /**
     * 是否需要使用 LayerDrawable 包裹 Drawable
     */
    private boolean useLayer() {
        return layerInsetLeft != 0 ||
                layerInsetRight != 0 ||
                layerInsetTop != 0 ||
                layerInsetBottom != 0;
    }

    /**
     * 设置线的颜色.
     * <p>
     * 必备属性: shape 为 GradientDrawable.RECTANGLE
     * 填充色 solidColor 为 Color.TRANSPARENT
     * 宽度
     */
    public RDrawable lineColor(int lineColor) {
        shape(GradientDrawable.RECTANGLE);
        solidColor(Color.TRANSPARENT);
        return strokeColor(lineColor);
    }


    /**
     * 创建一个左边竖线的Drawable
     * <p>
     * 颜色属性请使用 strokeColor 控制
     *
     * @param lineWidth 线宽, px单位
     */
    public RDrawable leftLine(int lineWidth) {
        return leftLine(lineWidth, strokeColor);
    }

    public RDrawable leftLine(int lineWidth, int lineColor) {
        shape(GradientDrawable.RECTANGLE);
        layerInsetLeft = 0;
        layerInsetRight = -lineWidth * 2;
        layerInsetTop = layerInsetBottom = layerInsetRight;
        stroke(lineWidth, lineColor);
        return this;
    }

    public RDrawable rightLine(int lineWidth) {
        return rightLine(lineWidth, strokeColor);
    }

    public RDrawable rightLine(int lineWidth, int lineColor) {
        shape(GradientDrawable.RECTANGLE);
        layerInsetRight = 0;
        layerInsetLeft = -lineWidth * 2;
        layerInsetTop = layerInsetBottom = layerInsetLeft;
        stroke(lineWidth, lineColor);
        return this;
    }

    public RDrawable topLine(int lineHeight) {
        return topLine(lineHeight, strokeColor);
    }

    public RDrawable topLine(int lineHeight, int lineColor) {
        shape(GradientDrawable.RECTANGLE);
        layerInsetTop = 0;
        layerInsetBottom = -lineHeight * 2;
        layerInsetLeft = layerInsetRight = layerInsetBottom;
        stroke(lineHeight, lineColor);
        return this;
    }

    public RDrawable bottomLine(int lineHeight) {
        return bottomLine(lineHeight, strokeColor);
    }

    public RDrawable bottomLine(int lineHeight, int lineColor) {
        shape(GradientDrawable.RECTANGLE);
        layerInsetBottom = 0;
        layerInsetTop = -lineHeight * 2;
        layerInsetLeft = layerInsetRight = layerInsetTop;
        stroke(lineHeight, lineColor);
        return this;
    }

    public RDrawable setLayerInset(int layerInsetLeft, int layerInsetTop,
                                   int layerInsetRight, int layerInsetBottom) {
        this.layerInsetLeft = layerInsetLeft;
        this.layerInsetRight = layerInsetRight;
        this.layerInsetBottom = layerInsetBottom;
        this.layerInsetTop = layerInsetTop;
        return this;
    }

    public RDrawable setLayerInsetLeft(int layerInsetLeft) {
        this.layerInsetLeft = layerInsetLeft;
        return this;
    }

    public RDrawable setLayerInsetRight(int layerInsetRight) {
        this.layerInsetRight = layerInsetRight;
        return this;
    }

    public RDrawable setLayerInsetBottom(int layerInsetBottom) {
        this.layerInsetBottom = layerInsetBottom;
        return this;
    }

    public RDrawable setLayerInsetTop(int layerInsetTop) {
        this.layerInsetTop = layerInsetTop;
        return this;
    }

    /**
     * 将最后一次的Drawable, 添加到 layer
     */
    public RDrawable andLayer() {
        ensureLayer();
        addLayer(lastDrawable);
        return this;
    }

    /**
     * 将当前Drawable, 添加到 layer
     */
    public RDrawable addLayer() {
        ensureLayer();
        addLayer(doIt());
        return this;
    }

    public RDrawable addLayer(Drawable drawable) {
        ensureLayer();
        int layer = layerDrawable.addLayer(drawable);
        layerDrawable.setLayerInset(layer, layerInsetLeft,
                layerInsetTop,
                layerInsetRight,
                layerInsetBottom);
        lastDrawable = layerDrawable;
        return this;
    }

    private void ensureLayer() {
        if (layerDrawable == null) {
            layerDrawable = new LayerDrawable(new Drawable[]{});
        }
    }

    //</editor-fold desc="LayerDrawable 相关方法和属性">

    //<editor-fold desc="生成可用的Drawable">

    /**
     * 返回具有选择状态的Drawable
     */
    public Drawable state() {
        StateListDrawable listDrawable = new StateListDrawable();
        for (Map.Entry<Integer, Drawable> entry : stateLinkedHashMap.entrySet()) {
            listDrawable.addState(new int[]{entry.getKey()}, entry.getValue());
        }
        listDrawable.addState(new int[]{}, normalDrawable);
        return listDrawable;
    }

    /**
     * 优先返回 layerDrawable, 其次返回 lastDrawable, 再返回 doIt()
     * 返回最后一次创建的有效Drawable, 作用类似于 doIt()
     */
    public Drawable get() {
        if (layerDrawable != null) {
            return layerDrawable;
        }
        if (lastDrawable == null) {
            doIt();
        }
        return lastDrawable;
    }

    /**
     * 使用此方法, 获取最终的Drawable
     */
    public Drawable doIt() {

        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(shape);
        if (strokeWidth != NO_INT) {
            gradientDrawable.setStroke(strokeWidth, strokeColor, dashWidth, dashGap);
        }
        if (solidColor != NO_INT) {
            gradientDrawable.setColor(solidColor);
        }
        if (width != NO_INT && height != NO_INT) {
            gradientDrawable.setSize(width, height);
            if (left != NO_INT || top != NO_INT) {
                gradientDrawable.setBounds(left, top, left + width, top + height);
            }
        } else {
            if (width != NO_INT) {
                gradientDrawable.setSize(width, -1);
            } else if (height != NO_INT) {
                gradientDrawable.setSize(-1, height);
            }
        }
        gradientDrawable.setCornerRadii(radii);

        if (gradientColors != null) {
            gradientDrawable.setGradientCenter(gradientCenterX, gradientCenterY);
            gradientDrawable.setGradientRadius(gradientRadius);
            gradientDrawable.setGradientType(gradientType);
            gradientDrawable.setColors(gradientColors);
            gradientDrawable.setOrientation(gradientOrientation);
        }

        /*反射修改 paint 的 shader*/
        if (strokeShader != null) {
            try {
                Field mStrokePaintField = GradientDrawable.class.getDeclaredField("mStrokePaint");
                mStrokePaintField.setAccessible(true);
                Paint mStrokePaint = (Paint) mStrokePaintField.get(gradientDrawable);
                mStrokePaint.setShader(strokeShader);
            } catch (Exception e) {

            }
        }

        /*反射修改 paint 的 shader*/
        if (fillShader != null) {
            try {
                Field mFillPaintField = GradientDrawable.class.getDeclaredField("mFillPaint");
                mFillPaintField.setAccessible(true);
                Paint mFillPaint = (Paint) mFillPaintField.get(gradientDrawable);
                mFillPaint.setShader(fillShader);
            } catch (Exception e) {

            }
        }

        lastDrawable = gradientDrawable;

        if (useLayer() && layerDrawable == null) {
            andLayer();
        }

        return lastDrawable;
    }

    //</editor-fold desc="生成可用的Drawable">

    //</editor-fold desc="注解">

    @IntDef({GradientDrawable.RECTANGLE, GradientDrawable.OVAL, GradientDrawable.LINE, GradientDrawable.RING})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Shape {
    }

    @IntDef({GradientDrawable.LINEAR_GRADIENT, GradientDrawable.RADIAL_GRADIENT, GradientDrawable.SWEEP_GRADIENT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface GradientType {
    }

    //</editor-fold desc="注解">
}
