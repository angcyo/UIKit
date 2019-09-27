package com.angcyo.uiview.less.utils;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ReplacementSpan;
import android.view.Gravity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.angcyo.uiview.less.R;
import com.angcyo.uiview.less.kotlin.ExKt;
import com.angcyo.uiview.less.kotlin.ViewExKt;
import com.angcyo.uiview.less.resources.ResUtil;

import kotlin.jvm.functions.Function1;

/**
 * Email:angcyo@126.com
 *
 * @author angcyo
 * @date 2019/02/18
 * Copyright (c) 2019 Shenzhen O&M Cloud Co., Ltd. All rights reserved.
 */
public class RSpan extends SpanUtils {

    private static final int TYPE_MIN_SIZE = 10;
    private static final int TYPE_CUSTOM = 11;

    private int minSize = -1;
    private int maxSize = -1;
    private String replaceText = null;
    private boolean isSetColor = false;
    private Function1<TextSpan, Object> configTextSpan;
    private Function1<RSpan, Object> customSpan;

    public RSpan() {
    }

    public static RSpan get() {
        return new RSpan();
    }

    public static RSpan get(CharSequence text) {
        return (RSpan) new RSpan().append(text);
    }

    public static RSpan build(CharSequence text) {
        return (RSpan) new RSpan().append(text);
    }

    public RSpan appendImage2(@NonNull Drawable drawable, int tintColor) {
        appendImage(ResUtil.filterDrawable(drawable, tintColor), SpanUtils.ALIGN_CENTER);
        return this;
    }

    public RSpan appendImage2(int resourceId, int tintColor) {
        appendImage(ResUtil.filterDrawable(ResUtil.getDrawable(resourceId), tintColor), SpanUtils.ALIGN_CENTER);
        return this;
    }

    @Override
    public RSpan append(@NonNull CharSequence text) {
        super.append(text);
        return this;
    }

    @Override
    protected void setDefault() {
        super.setDefault();
        minSize = -1;
        maxSize = -1;
        isSetColor = false;
        replaceText = null;
        configTextSpan = null;
        customSpan = null;
    }

    @Override
    public RSpan setForegroundColor(int color) {
        isSetColor = true;
        super.setForegroundColor(color);
        return this;
    }

    @Override
    public RSpan appendSpace(int size) {
        super.appendSpace(size);
        return this;
    }

    @Override
    public RSpan setBackgroundColor(int color) {
        super.setBackgroundColor(color);
        return this;
    }

    @Override
    public RSpan appendImage(int resourceId) {
        super.appendImage(resourceId);
        return this;
    }

    @Override
    public RSpan appendImage(int resourceId, int align) {
        super.appendImage(resourceId, align);
        return this;
    }

    @Override
    protected void applyLast() {
        if (mType == TYPE_MIN_SIZE) {
            updateMinSize();
        } else if (mType == TYPE_CUSTOM) {
            customSpan();
        }
        super.applyLast();
    }

    /**
     * 设置 span 占用的最小值
     */
    public RSpan setMinSize(int size) {
        minSize = size;
        mType = TYPE_MIN_SIZE;
        return this;
    }

    public RSpan setMaxSize(int size) {
        maxSize = size;
        mType = TYPE_MIN_SIZE;
        return this;
    }

    /**
     * 使用 [TextSpan] 手动绘制
     */
    public RSpan replaceText(String text) {
        replaceText = text;
        mType = TYPE_MIN_SIZE;
        return this;
    }

    public RSpan setConfigTextSpan(Function1<TextSpan, Object> configTextSpan) {
        this.configTextSpan = configTextSpan;
        mType = TYPE_MIN_SIZE;
        return this;
    }

    /**
     * 自定义span
     */
    public RSpan customSpan(Function1<RSpan, Object> customSpan) {
        this.customSpan = customSpan;
        mType = TYPE_CUSTOM;
        return this;
    }

    private void customSpan() {
        if (mText.length() == 0 || customSpan == null) return;
        Object span = customSpan.invoke(this);

        int start = mBuilder.length();
        mBuilder.append(mText);
        int end = mBuilder.length();

        if (span == null) {
            return;
        }

        mBuilder.setSpan(span, start, end, flag);

        mType = -1;
    }

    private void updateMinSize() {
        if (mText.length() == 0) return;
        int start = mBuilder.length();
        mBuilder.append(mText);
        int end = mBuilder.length();

        TextSpan textSpan = new TextSpan(minSize);
        textSpan.replaceText = replaceText;
        textSpan.maxSize = maxSize;

        if (isSetColor) {
            textSpan.setForegroundColor(foregroundColor);
        }

        if (backgroundColor != COLOR_DEFAULT) {
            textSpan.setBackgroundColor(backgroundColor);
        }

        if (fontSize != -1) {
            if (fontSizeIsDp) {
                textSpan.fontSize = fontSize * ViewExKt.getDp(fontSize);
            } else {
                textSpan.fontSize = fontSize;
            }
        }

        if (configTextSpan != null) {
            configTextSpan.invoke(textSpan);
        }

        mBuilder.setSpan(textSpan, start, end, flag);

        mType = -1;
    }

    public RSpan appendDarkText(@NonNull CharSequence text) {
        append(text);
        setForegroundColor(ResUtil.getColor(R.color.base_text_color));
        setFontSize(ResUtil.getDimen(R.dimen.default_text_size), false);
        return this;
    }

    public RSpan appendMainText(@NonNull CharSequence text) {
        append(text);
        setForegroundColor(ResUtil.getColor(R.color.base_text_color_dark));
        setFontSize(ResUtil.getDimen(R.dimen.default_text_little_size), false);
        return this;
    }

    @Override
    public RSpan setBold() {
        super.setBold();
        return this;
    }

    @Override
    public RSpan setSpans(@NonNull Object... spans) {
        super.setSpans(spans);
        return this;
    }

    public static class TextSpan extends ReplacementSpan {

        /**
         * 当`文本`大小小于minSize时, 使用 minSize
         * 负数 表示使用原始大小
         */
        protected int minSize = -1;

        /**
         * 最大的 宽度, 负数不限制
         */
        protected int maxSize = -1;

        /**
         * 省略号
         */
        protected String ellipsesText = "...";

        /**
         * 需要替代显示的文本
         */
        protected CharSequence replaceText = null;

        private boolean isSetColor = false;
        /**
         * 背景颜色
         */
        protected int backgroundColor = -1;
        /**
         * 背景圆角大小, 只在设置了背景颜色时有效
         */
        protected float backgroundRadius = 0;

        /**
         * 当keepCircle为true时, 此drawable不会绘制
         */
        protected Drawable backgroundDrawable = null;

        /**
         * 前景颜色, 文本颜色
         */
        protected int foregroundColor = -1;

        //缓存测量的大小, 如果文本当中包含 换行符, 系统会拆分成2次测量和绘制(同一个对象), 所以不能使用缓存
        @Deprecated
        private float spanSize = 0;
        private float spanValidHeight = -1f;
        /**
         * 文本绘制偏移距离
         */
        protected float offsetY = 0;
        protected float offsetX = 0;

        protected float fontSize = 0;

        /**
         * 只有marginLeft 会影响文本绘制的x坐标
         * <p>
         * margin属性 不影响keepCircle模式下的背景
         */
        protected float marginLeft = 0;
        protected float marginRight = 0;
        protected float marginTop = 0;
        protected float marginBottom = 0;

        /**
         * 默认垂直方向的margin不影响span的height, 只影响background的绘制
         * 实验性属性
         */
        protected boolean marginHeight = false;

        /**
         * 只有paddingLeft 会影响文本绘制的x坐标
         */
        protected float paddingLeft = 0;
        protected float paddingRight = 0;
        protected float paddingTop = 0;
        protected float paddingBottom = 0;

        /**
         * 为了不污染 TextView 中的paint
         */
        protected TextPaint textPaint;

        /**
         * 文本绘制重力
         */
        protected int textGravity = Gravity.LEFT | Gravity.BOTTOM;

        /**
         * 设置了此属性,背景颜色绘制范围只会受到padding的影响
         */
        protected int keepCircleOnTextLength = -1;

        private RectF drawRect = new RectF();

        /**
         * 当[RTextView]onMeasure时, 会赋值.
         *
         * @see com.angcyo.uiview.less.widget.RTextView#onMeasure(int, int)
         */
        public int textViewMeasureWidth = -1;

        /**
         * 宽度是[textViewMeasureWidth]多少倍.
         * 需要[RTextView]的支持, 并且非[wrap_content]测量模式
         */
        protected float spanWeight = -1f;

        /**
         * 最大宽度weight限制
         */
        protected float spanMaxWeight = -1f;

        public TextSpan() {

        }

        public TextSpan(int minSize) {
            this();
            this.minSize = minSize;
        }

        public TextSpan(int minSize, int foregroundColor) {
            this(minSize);
            this.foregroundColor = foregroundColor;
            isSetColor = true;
        }

        public TextSpan(int minSize, int backgroundColor, int foregroundColor) {
            this(minSize, foregroundColor);
            this.backgroundColor = backgroundColor;
            isSetColor = true;
        }

        public TextSpan(int minSize, int backgroundColor, int foregroundColor, CharSequence replaceText) {
            this(minSize, backgroundColor, foregroundColor);
            this.replaceText = replaceText;
            isSetColor = true;
        }

        private void configPaint(Paint paint) {
            if (textPaint == null) {
                textPaint = new TextPaint();
            }

            if (paint != null) {
                if (paint != textPaint) {
                    textPaint.set(paint);
                }

                if (fontSize > 0) {
                    textPaint.setTextSize(fontSize);
                }

                if (isSetColor) {
                    textPaint.setColor(foregroundColor);
                }
            }
        }

        protected float measureTextWidthMargin(@NonNull Paint paint, CharSequence text, int start, int end) {
            return chooseSize(measureTextWidth(paint, text, start, end) + marginLeft + marginRight + paddingLeft + paddingRight);
        }

        protected float measureTextWidth(@NonNull Paint paint, CharSequence text, int start, int end) {
            float size = 0f;
            if (isValid(text, start, end)) {
                configPaint(paint);

                size = textPaint.measureText(String.valueOf(text.subSequence(start, end)));

                float replaceTextSize = 0f;
                if (replaceText != null) {
                    replaceTextSize = textPaint.measureText(replaceText, 0, replaceText.length());

                    size = replaceTextSize;
                }

                if (minSize < 0) {
                    //未设置min size
                } else {
                    //设置了min size
                    size = Math.max(size, minSize);
                }

                if (maxSize > 0) {
                    size = Math.min(size, maxSize);
                }
            }
            return size;
        }

        protected float chooseSize(float textSize) {
            float mxSize = chooseMaxSize();

            float tSize = textSize;
            if (textViewMeasureWidth > 0) {
                if (spanWeight > 0) {
                    tSize = textViewMeasureWidth * spanWeight;
                }
            }

            if (minSize < 0) {
                //未设置min size
            } else {
                //设置了min size
                tSize = Math.max(tSize, minSize);
            }

            if (mxSize > 0) {
                tSize = Math.min(tSize, mxSize);
            }

            return tSize;
        }

        protected float chooseMaxSize() {
            float mxSize = maxSize;
            if (textViewMeasureWidth > 0) {
                if (spanMaxWeight > 0) {
                    mxSize = textViewMeasureWidth * spanMaxWeight;
                }
            }
            return mxSize;
        }

        @Override
        public int getSize(@NonNull Paint paint,
                           CharSequence text, int start, int end,
                           @Nullable Paint.FontMetricsInt fm) {
            CharSequence validText = validText(text, start, end);

            spanSize = 0f;
            if (isValid(text, start, end)) {
                spanSize = measureTextWidthMargin(paint, validText, 0, validText.length());
                float textHeight = ExKt.textHeight(textPaint);

                float spanWidth = spanSize;
                float spanHeight = textHeight + paddingTop + paddingBottom;

                if (marginHeight) {
                    spanHeight += (marginTop + marginBottom);
                }

                /**
                 * @see android.text.BoringLayout#init line:229 241~249
                 * */
                if (fm != null) {
                    int height1 = fm.bottom - fm.top;
                    int height2 = fm.descent - fm.ascent;

                    if (fontSize > 0 || (height1 <= 0 && height2 <= 0)) {
                        fm.ascent = Math.min((int) textPaint.ascent(), fm.ascent);
                        fm.descent = Math.max((int) textPaint.descent(), fm.descent);
                        fm.top = Math.min((int) textPaint.ascent(), fm.top);
                        fm.bottom = Math.max((int) textPaint.descent(), fm.bottom);
                    }

                    if (marginHeight) {
                        fm.ascent = (int) (fm.ascent - marginTop - paddingTop);
                        fm.descent = (int) (fm.descent + marginBottom + paddingBottom);
                        fm.top = (int) (fm.top - marginTop - paddingTop);
                        fm.bottom = (int) (fm.bottom + marginBottom + paddingBottom);
                    }

                    spanValidHeight = fm.bottom - fm.top;
                }

                if (keepCircle(validText)) {
                    float size = Math.max(spanWidth, spanHeight);
                    spanSize = size;

                    if (fm != null && size > spanValidHeight) {
                        float offsetHeight = size - spanValidHeight;

                        fm.ascent -= offsetHeight / 2;
                        fm.descent += offsetHeight / 2;
                        fm.top -= offsetHeight / 2;
                        fm.bottom += offsetHeight / 2;
                    }
                }
            }
            return (int) spanSize;
        }

        private boolean keepCircle(CharSequence validText) {
            return validText.length() <= keepCircleOnTextLength;
        }

        private CharSequence validText(CharSequence text, int start, int end) {
            if (replaceText != null) {
                return replaceText;
            }
            return text.subSequence(start, end);
        }

        private void drawBackgroundColor(@NonNull Canvas canvas,
                                         @NonNull Paint paint,
                                         @NonNull RectF drawRect,
                                         float circleRadius,
                                         boolean keepCircle) {
            if (backgroundColor != -1) {
                int paintColor = paint.getColor();
                paint.setColor(backgroundColor);

                if (keepCircle) {
                    canvas.drawCircle(drawRect.centerX(), drawRect.centerY(), circleRadius, paint);
                } else {
                    canvas.drawRoundRect(drawRect, backgroundRadius, backgroundRadius, paint);
                }

                if (isSetColor) {
                    paint.setColor(foregroundColor);
                } else {
                    paint.setColor(paintColor);
                }
            }
        }

        private void drawBackgroundDrawable(@NonNull Canvas canvas, @NonNull RectF drawRect) {
            if (backgroundDrawable != null) {
                backgroundDrawable.setBounds((int) drawRect.left, (int) drawRect.top, (int) drawRect.right, (int) drawRect.bottom);
                backgroundDrawable.draw(canvas);
            }
        }

        @Override
        public void draw(@NonNull Canvas canvas,
                         CharSequence text, int start, int end,
                         float x, int top, int y, int bottom,
                         @NonNull Paint paint) {
            CharSequence validText = validText(text, start, end);

            if (isValid(text, start, end)) {
                float measureTextWidth = measureTextWidth(paint, validText, 0, validText.length());
                float textHeight = ExKt.textHeight(textPaint);

                float drawLeft = x + marginLeft;
                float drawTop = top + marginTop;
                float drawY = y /*- marginBottom*/;
                float drawBottom = bottom - marginBottom;

                float spanRight = drawLeft + measureTextWidth + paddingLeft + paddingRight;
                float spanWidth = chooseSize(spanRight - drawLeft - +paddingLeft - paddingRight) + paddingLeft + paddingRight;
                float spanHeight = drawBottom - drawTop;

                boolean keepCircle = keepCircle(validText);

                float circleSize = Math.max(spanWidth, spanHeight);
                if (keepCircle) {
                    float circleWidth = measureTextWidth + paddingLeft + paddingRight;
                    float circleHeight = textHeight + paddingTop + paddingBottom;
                    circleSize = Math.max(circleWidth, circleHeight);
                }

                drawRect.set(drawLeft, drawTop, spanRight, drawBottom);

                if (keepCircle) {
                    //drawRect.set();
                    drawBackgroundColor(canvas, textPaint, drawRect, circleSize / 2, true);
                    if (backgroundColor == -1) {
                        float dTop = drawRect.top;
                        float dBottom = drawRect.bottom;
                        drawRect.top = (spanHeight - textHeight) / 2 - paddingTop;
                        drawRect.bottom = (spanHeight + textHeight) / 2 + paddingBottom;
                        drawBackgroundDrawable(canvas, drawRect);
                        drawRect.top = dTop;
                        drawRect.bottom = dBottom;
                    }
                } else {
                    if (keepCircleOnTextLength < 0) {
                        //属性未设置
                        drawBackgroundColor(canvas, textPaint, drawRect, circleSize / 2, false);
                        drawBackgroundDrawable(canvas, drawRect);
                    } else {
                        //属性已设置
                        //优先绘制Drawable
                        if (backgroundDrawable != null) {
                            drawBackgroundDrawable(canvas, drawRect);
                        } else {
                            float dTop = drawRect.top;
                            float dBottom = drawRect.bottom;
                            drawRect.top = (spanHeight - textHeight) / 2 - paddingTop;
                            drawRect.bottom = (spanHeight + textHeight) / 2 + paddingBottom;
                            drawBackgroundColor(canvas, textPaint, drawRect, circleSize / 2, false);
                            drawRect.top = dTop;
                            drawRect.bottom = dBottom;
                        }
                    }
                }

                drawLeft += offsetX + paddingLeft;
                drawY += offsetY - paddingBottom;

                start = 0;
                end = validText.length();

                float maxSize = chooseMaxSize();

                if (maxSize > 0) {
                    String textString = String.valueOf(validText);
                    float textWidth = ExKt.textWidth(textPaint, textString);
                    if (textWidth > maxSize) {
                        //判断是否需要打省略号
                        validText = ExKt.findTextWidth(textPaint, textString, maxSize, ellipsesText) + ellipsesText;

                        start = 0;
                        end = validText.length();
                    }
                }

                if (ViewExKt.isGravityCenter(textGravity)) {
                    drawLeft = x + marginLeft + (spanWidth - measureTextWidth) / 2;
                    if (keepCircle) {
                        drawY = drawRect.centerY() + textHeight / 2 - textPaint.descent();
                    } else {
                        drawY = drawTop + (spanHeight - textHeight) / 2 - textPaint.ascent();
                    }
                } else if (ViewExKt.isGravityCenterVertical(textGravity)) {
                    if (keepCircle) {
                        drawY = drawRect.centerY() + textHeight / 2 - textPaint.descent();
                    } else {
                        drawY = drawTop + (spanHeight - textHeight) / 2 - textPaint.ascent();
                    }
                } else if (ViewExKt.isGravityCenterHorizontal(textGravity)) {
                    drawLeft = x + marginLeft + (spanWidth - measureTextWidth) / 2;
                }

                canvas.drawText(validText, start, end, drawLeft, drawY, textPaint);
            }
        }

        /**
         * 验证值 是否有效
         */
        protected boolean isValid(CharSequence text, int start, int end) {
            if (end <= start) {
                return false;
            }

            if (TextUtils.isEmpty(text) && TextUtils.isEmpty(replaceText)) {
                return false;
            }

            return true;
        }

        public void setMinSize(int minSize) {
            this.minSize = minSize;
        }

        public void setReplaceText(CharSequence replaceText) {
            this.replaceText = replaceText;
        }

        public void setBackgroundColor(int backgroundColor) {
            this.backgroundColor = backgroundColor;
        }

        public void setForegroundColor(int foregroundColor) {
            this.foregroundColor = foregroundColor;
            isSetColor = true;
        }

        public void setOffsetY(float offsetY) {
            this.offsetY = offsetY;
        }

        public void setOffsetX(float offsetX) {
            this.offsetX = offsetX;
        }

        public void setMaxSize(int maxSize) {
            this.maxSize = maxSize;
        }

        public void setEllipsesText(String ellipsesText) {
            this.ellipsesText = ellipsesText;
        }

        public void setFontSize(float fontSize) {
            this.fontSize = fontSize;
        }

        public void setBackgroundDrawable(Drawable backgroundDrawable) {
            this.backgroundDrawable = backgroundDrawable;
        }

        public void setMarginLeft(float marginLeft) {
            this.marginLeft = marginLeft;
        }

        public void setMarginRight(float marginRight) {
            this.marginRight = marginRight;
        }

        public void setMarginTop(float marginTop) {
            this.marginTop = marginTop;
        }

        public void setMarginBottom(float marginBottom) {
            this.marginBottom = marginBottom;
        }

        public void setMarginVertical(float margin) {
            setMarginTop(margin);
            setMarginBottom(margin);
        }

        public void setMarginHorizontal(float margin) {
            setMarginLeft(margin);
            setMarginRight(margin);
        }

        public void setMargin(float margin) {
            setMarginVertical(margin);
            setMarginHorizontal(margin);
        }

        public void setTextGravity(int textGravity) {
            this.textGravity = textGravity;
        }

        public void setPaddingLeft(float paddingLeft) {
            this.paddingLeft = paddingLeft;
        }

        public void setPaddingRight(float paddingRight) {
            this.paddingRight = paddingRight;
        }

        public void setPaddingTop(float paddingTop) {
            this.paddingTop = paddingTop;
        }

        public void setPaddingBottom(float paddingBottom) {
            this.paddingBottom = paddingBottom;
        }

        public void setPaddingVertical(float padding) {
            setPaddingTop(padding);
            setPaddingBottom(padding);
        }

        public void setPaddingHorizontal(float padding) {
            setPaddingLeft(padding);
            setPaddingRight(padding);
        }

        public void setPadding(float padding) {
            setPaddingVertical(padding);
            setPaddingHorizontal(padding);
        }

        /**
         * keepCircle 在span, 中无法完美的实现. 因为能拿到的测量数据太少了. 所以size 会对不上.
         * 适当的加一些 margin 可以解决.
         */
        public void setKeepCircleOnTextLength(int keepCircleOnTextLength) {
            this.keepCircleOnTextLength = keepCircleOnTextLength;
            setTextGravity(Gravity.CENTER);
        }

        public void setBackgroundRadius(float backgroundRadius) {
            this.backgroundRadius = backgroundRadius;
        }

        public void setBackground(int backgroundColor, float backgroundRadius) {
            this.backgroundColor = backgroundColor;
            this.backgroundRadius = backgroundRadius;
        }

        public void setSpanWeight(float spanWeight) {
            this.spanWeight = spanWeight;
        }

        public void setSpanMaxWeight(float spanMaxWeight) {
            this.spanMaxWeight = spanMaxWeight;
        }
    }
}
