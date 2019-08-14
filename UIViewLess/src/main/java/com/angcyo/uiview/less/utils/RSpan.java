package com.angcyo.uiview.less.utils;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.text.style.ReplacementSpan;

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
         * 前景颜色, 文本颜色
         */
        protected int foregroundColor = -1;

        //缓存测量的大小
        private float spanSize = 0;

        protected float offsetY = 0;

        protected float offsetX = 0;

        protected float fontSize = 0;

        public TextSpan() {
        }

        public TextSpan(int minSize) {
            this.minSize = minSize;
        }

        public TextSpan(int minSize, int foregroundColor) {
            this.minSize = minSize;
            this.foregroundColor = foregroundColor;
            isSetColor = true;
        }

        public TextSpan(int minSize, int backgroundColor, int foregroundColor) {
            this.minSize = minSize;
            this.backgroundColor = backgroundColor;
            this.foregroundColor = foregroundColor;
            isSetColor = true;
        }

        public TextSpan(int minSize, int backgroundColor, int foregroundColor, CharSequence replaceText) {
            this.minSize = minSize;
            this.replaceText = replaceText;
            this.backgroundColor = backgroundColor;
            this.foregroundColor = foregroundColor;
            isSetColor = true;
        }

        private void configPaint(Paint paint) {
            if (paint != null) {
                if (fontSize > 0) {
                    paint.setTextSize(fontSize);
                }

                if (isSetColor) {
                    paint.setColor(foregroundColor);
                }
            }
        }

        @Override
        public int getSize(@NonNull Paint paint,
                           CharSequence text, int start, int end,
                           @Nullable Paint.FontMetricsInt fm) {
            if (isValid(text, start, end)) {
                configPaint(paint);

                spanSize = paint.measureText(String.valueOf(text.subSequence(start, end)));

                float replaceTextSize = 0f;
                if (replaceText != null) {
                    replaceTextSize = paint.measureText(replaceText, 0, replaceText.length());

                    spanSize = replaceTextSize;
                }

                if (minSize < 0) {
                    //未设置min size
                } else {
                    //设置了min size
                    spanSize = Math.max(spanSize, minSize);
                }

                if (maxSize > 0) {
                    spanSize = Math.min(spanSize, maxSize);
                }

                /**
                 * @see android.text.BoringLayout#init line:229 241~249
                 * */
                if (fm != null) {
                    int height1 = fm.bottom - fm.top;
                    int height2 = fm.descent - fm.ascent;
                    if (fontSize > 0 || (height1 <= 0 && height2 <= 0)) {
                        fm.ascent = Math.min((int) paint.ascent(), fm.ascent);
                        fm.descent = Math.max((int) paint.descent(), fm.descent);
                        fm.top = Math.min(fm.ascent, fm.top);
                        fm.bottom = Math.max(fm.descent, fm.bottom);
                    }
                }
            } else {
                spanSize = 0;
            }
            return (int) spanSize;
        }

        @Override
        public void draw(@NonNull Canvas canvas,
                         CharSequence text, int start, int end,
                         float x, int top, int y, int bottom,
                         @NonNull Paint paint) {
            if (isValid(text, start, end)) {

                if (backgroundColor != -1) {
                    paint.setColor(backgroundColor);
                    canvas.drawRect(x, top,
                            x + spanSize, bottom, paint);
                }

                configPaint(paint);

                x += offsetX;
                y += offsetY;

                if (replaceText != null) {
                    text = replaceText;
                    start = 0;
                    end = replaceText.length();
                }

                if (maxSize > 0) {
                    String textString = String.valueOf(text);
                    float textWidth = ExKt.textWidth(paint, textString);
                    if (textWidth > maxSize) {
                        //判断是否需要打省略号
                        text = ExKt.findTextWidth(paint, textString, maxSize, ellipsesText) + ellipsesText;

                        start = 0;
                        end = text.length();
                    }
                }
                canvas.drawText(text, start, end, x, y, paint);
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

        public void setSpanSize(float spanSize) {
            this.spanSize = spanSize;
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
    }
}
