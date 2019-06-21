package com.angcyo.uiview.less.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import com.angcyo.uiview.less.R;
import com.angcyo.uiview.less.kotlin.ViewExKt;
import com.angcyo.uiview.less.resources.ResUtil;
import com.angcyo.uiview.less.skin.SkinHelper;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2017/05/09 17:44
 * 修改人员：Robi
 * 修改时间：2017/05/09 17:44
 * 修改备注：
 * Version: 1.0.0
 */
public class Button extends RTextView {

    /**
     * 默认就是很小的圆角矩形填充样式
     */
    public static final int DEFAULT = 1;
    /**
     * 圆角矩形填充样式, 通过 roundRadii 修改圆角大小
     */
    public static final int ROUND = 2;
    /**
     * 可自定义的圆角边框样式
     */
    public static final int ROUND_BORDER = 3;
    /**
     * 正常边框不填充, 按下主题颜色填充
     */
    public static final int ROUND_BORDER_FILL = 4;

    /**
     * 透明渐变, 很小的圆角矩形填充样式
     */
    public static final int ROUND_GRADIENT_RECT = 5;
    public static final int RADII = 300;

    int mButtonStyle = DEFAULT;

    int themeColor;
    int rippleColor;
    int themeDarkColor;
    int disableColor;

    int borderWidth;
    int roundRadii;

    int gradientStartColor;
    int gradientEndColor;

    public Button(Context context) {
        this(context, null);
    }

    public Button(Context context, AttributeSet attrs) {
        super(context, attrs, R.attr.defaultButtonStyle);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.Button, R.attr.defaultButtonStyle, 0);
        mButtonStyle = typedArray.getInt(R.styleable.Button_r_button_style, DEFAULT);

        //typedArray.getDimensionPixelOffset(com.android.in)

        int defaultValue = (int) (3 * density());
        if (mButtonStyle == ROUND) {
            defaultValue = RADII;
        }

        if (isInEditMode()) {
            themeColor = ViewExKt.getColor(this, R.color.theme_color_accent);
            themeDarkColor = ViewExKt.getColor(this, R.color.theme_color_primary_dark);
            disableColor = Color.GRAY;
        } else {
            themeColor = SkinHelper.getSkin().getThemeSubColor();
            themeDarkColor = SkinHelper.getSkin().getThemeDarkColor();
            disableColor = ContextCompat.getColor(getContext(), R.color.base_color_disable);
        }

        rippleColor = typedArray.getColor(R.styleable.Button_r_button_ripple_color, Color.WHITE);

        themeColor = typedArray.getColor(R.styleable.Button_r_button_theme_color, themeColor);
        themeDarkColor = typedArray.getColor(R.styleable.Button_r_button_theme_dark_color, themeDarkColor);
        disableColor = typedArray.getColor(R.styleable.Button_r_button_disable_color, disableColor);

        borderWidth = typedArray.getDimensionPixelOffset(R.styleable.Button_r_button_border_width, (int) (1 * density()));
        roundRadii = typedArray.getDimensionPixelOffset(R.styleable.Button_r_button_round_radii, defaultValue);

        gradientStartColor = themeColor;
        gradientEndColor = themeDarkColor;

        gradientStartColor = typedArray.getColor(R.styleable.Button_r_gradient_start_color, gradientStartColor);
        gradientEndColor = typedArray.getColor(R.styleable.Button_r_gradient_end_color, gradientEndColor);

        typedArray.recycle();

        /*不覆盖系统的background属性*/
        initButton(getBackground() == null);
    }

    private void initButton(boolean refreshBg) {
        setTextColor(getTextColors());

        if (refreshBg) {
            switch (mButtonStyle) {
                case ROUND:
                    setBackground(ResUtil.ripple(rippleColor,
                            ResUtil.selector(
                                    ResUtil.createDrawable(themeColor, roundRadii),
                                    ResUtil.createDrawable(themeDarkColor, roundRadii),
                                    ResUtil.createDrawable(disableColor, roundRadii)
                            )));
                    break;
                case ROUND_BORDER:
                    setBackground(ResUtil.ripple(rippleColor,
                            ResUtil.selector(
                                    ResUtil.createDrawable(themeColor, Color.TRANSPARENT, borderWidth, roundRadii),
                                    ResUtil.createDrawable(themeDarkColor, Color.TRANSPARENT, borderWidth, roundRadii),
                                    ResUtil.createDrawable(disableColor, Color.TRANSPARENT, borderWidth, roundRadii)
                            )));

                    if (useSkinStyle) {
                        int subColor = themeColor;
                        setTextColor(ResUtil.generateTextColor(subColor, subColor, disableColor, subColor));
                    }
                    break;
                case ROUND_BORDER_FILL:
                    int subColor = themeColor;
                    int lineColor = ViewExKt.getColor(this, R.color.default_base_line);
                    if (useSkinStyle) {
                        lineColor = subColor;
                        setTextColor(ResUtil.generateTextColor(Color.WHITE, subColor));
                    } else {
                        setTextColor(ResUtil.generateTextColor(getCurrentTextColor(), ViewExKt.getColor(this, R.color.base_text_color)));
                    }

                    setBackground(ResUtil.ripple(rippleColor,
                            ResUtil.selector(
                                    ResUtil.createDrawable(lineColor,
                                            Color.TRANSPARENT, borderWidth,
                                            roundRadii),
                                    ResUtil.createDrawable(themeColor,
                                            roundRadii),
                                    ResUtil.createDrawable(disableColor,
                                            roundRadii)
                            )));

                    break;
                case ROUND_GRADIENT_RECT:
                    setBackground(ResUtil.ripple(rippleColor,
                            ResUtil.selector(
                                    ResUtil.createGradientDrawable(gradientStartColor, gradientEndColor, roundRadii),
                                    ResUtil.createGradientDrawable(gradientStartColor, gradientEndColor, roundRadii),
                                    ResUtil.createGradientDrawable(gradientStartColor, gradientEndColor, roundRadii))
                    ));
                    break;
                default:
                    setBackground(
                            ResUtil.generateRippleRoundMaskDrawable(roundRadii,
                                    rippleColor, themeDarkColor,
                                    disableColor,
                                    themeColor
                            ));
                    break;
            }
        }
    }

    @Override
    protected void initView() {
        super.initView();

        setGravity(Gravity.CENTER);
        setClickable(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
//        if ((heightMode == MeasureSpec.AT_MOST || heightMode == MeasureSpec.UNSPECIFIED) &&
//                getPaddingTop() == 0 && getPaddingBottom() == 0 && !aeqWidth) {
//            setMeasuredDimension(getMeasuredWidth(), getResources().getDimensionPixelOffset(R.dimen.base_title_bar_item_size));
//        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public void updateStyle() {
        themeColor = SkinHelper.getSkin().getThemeSubColor();
        themeDarkColor = SkinHelper.getSkin().getThemeDarkColor();
        disableColor = SkinHelper.getSkin().getThemeDisableColor();

        setBackground(null);
        initButton(true);
    }
}
