package com.angcyo.uiview.less.utils;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.text.style.ReplacementSpan;
import com.angcyo.uiview.less.resources.ResUtil;

/**
 * Email:angcyo@126.com
 *
 * @author angcyo
 * @date 2019/02/18
 * Copyright (c) 2019 Shenzhen O&M Cloud Co., Ltd. All rights reserved.
 */
public class RSpan extends SpanUtils {

    private final int mTypeMinSize = 10;

    private int minSize = -1;
    private boolean isSetColor = false;

    public RSpan() {
    }

    public static RSpan get() {
        return new RSpan();
    }

    public static RSpan get(String text) {
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
        isSetColor = false;
    }

    @Override
    public RSpan setForegroundColor(int color) {
        isSetColor = true;
        super.setForegroundColor(color);
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
        if (mType == mTypeMinSize) {
            updateMinSize();
        } else {
            super.applyLast();
        }
    }

    /**
     * 设置 span 占用的最小值
     */
    public RSpan setMinSize(int size) {
        minSize = size;
        mType = mTypeMinSize;
        return this;
    }

    private void updateMinSize() {
        if (mText.length() == 0) return;
        int start = mBuilder.length();
        mBuilder.append(mText);
        int end = mBuilder.length();

        TextSpan textSpan = new TextSpan(minSize);
        if (isSetColor) {
            textSpan.setForegroundColor(foregroundColor);
        }

        if (backgroundColor != COLOR_DEFAULT) {
            textSpan.setBackgroundColor(backgroundColor);
        }

        mBuilder.setSpan(textSpan, start, end, flag);

        mType = -1;
    }


    public static class TextSpan extends ReplacementSpan {

        /**
         * 当`文本`大小小于minSize时, 使用 minSize
         * 负数 表示使用原始大小
         */
        protected int minSize = -1;

        /**
         * 需要替代显示的文本
         */
        protected CharSequence replaceText = null;

        protected boolean isSetColor = false;
        /**
         * 背景颜色
         */
        protected int backgroundColor = -1;
        /**
         * 前景颜色, 文本颜色
         */
        protected int foregroundColor = -1;

        //缓存测量的大小
        protected float spanSize = 0;

        protected float offsetY = 0;

        protected float offsetX = 0;

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

        @Override
        public int getSize(@NonNull Paint paint,
                           CharSequence text, int start, int end,
                           @Nullable Paint.FontMetricsInt fm) {
            if (isValid(text, start, end)) {
                float originTextSize = paint.measureText(String.valueOf(text.subSequence(start, end)));
                spanSize = originTextSize;

                float replaceTextSize = 0f;
                if (replaceText != null) {
                    replaceTextSize = paint.measureText(replaceText, 0, replaceText.length());

                    spanSize = replaceTextSize;
                }

                //spanSize = (int) Math.max(originTextSize, replaceTextSize);

                if (minSize < 0) {
                    //未设置min size
                } else {
                    //设置了min size
                    spanSize = Math.max(spanSize, minSize);
                }

                if (fm != null) {
                    fm.ascent = 0;
                    fm.descent = 0;

                    fm.top = 0;
                    fm.bottom = 0;
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

                if (isSetColor) {
                    paint.setColor(foregroundColor);
                }

                x += offsetX;
                y += offsetY;

                if (replaceText != null) {
                    text = replaceText;
                    start = 0;
                    end = replaceText.length();
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
    }
}
