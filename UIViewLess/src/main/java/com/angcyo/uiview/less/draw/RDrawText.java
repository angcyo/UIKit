package com.angcyo.uiview.less.draw;

import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;

import com.angcyo.uiview.less.R;


/**
 * Email:angcyo@126.com
 *
 * @author angcyo
 * @date 2018/10/20
 */
public class RDrawText extends BaseDraw {
    public static final int GRAVITY_TOP = 1;
    public static final int GRAVITY_LEFT = 2;
    public static final int GRAVITY_RIGHT = 4;
    public static final int GRAVITY_BOTTOM = 8;
    public static final int GRAVITY_CENTER = 16;
    public static final int GRAVITY_CENTER_H = 32;
    public static final int GRAVITY_CENTER_V = 64;

    protected ColorStateList textColor;
    protected CharSequence drawText;

    /**
     * 文本大小, 用来控制测量
     */
    protected float textSize;
    /**
     * 文本大小 , 用来控制绘制
     */
    protected float drawTextSize;

    protected int textGravity = GRAVITY_TOP | GRAVITY_LEFT;

    protected int textOffsetX, textOffsetY;

    protected Layout textLayout;

    /**
     * 行间距
     */
    protected float lineSpacingExtra;

    public RDrawText(View view, AttributeSet attr) {
        super(view, attr);
        initAttribute(attr);
    }

    public static boolean haveInt(int src, int i) {
        int maskSrc = src & 0xff;
        int maskI = i & 0xff;
        return (maskSrc & maskI) == maskI;
    }

    /**
     * @return 返回文本 绘制 在 top 和 bottom 中间的 y坐标
     */
    public static int textDrawCenterY(Paint paint, int top, int bottom) {
        int height = bottom - top;
        int textHeight = (int) (paint.descent() - paint.ascent());
        return (int) (top + height / 2 + textHeight / 2 - paint.descent());
    }

    @Override
    public void initAttribute(AttributeSet attr) {
        TypedArray array = obtainStyledAttributes(attr, R.styleable.RDrawText);
        textColor = array.getColorStateList(R.styleable.RDrawText_r_draw_text_color);
        drawText = array.getString(R.styleable.RDrawText_r_draw_text_string);
        textSize = array.getDimensionPixelOffset(R.styleable.RDrawText_r_draw_text_size, getResources().getDimensionPixelOffset(R.dimen.base_xhdpi_15));
        textOffsetX = array.getDimensionPixelOffset(R.styleable.RDrawText_r_draw_text_offset_x, textOffsetX);
        textOffsetY = array.getDimensionPixelOffset(R.styleable.RDrawText_r_draw_text_offset_y, textOffsetY);
        drawTextSize = array.getDimensionPixelOffset(R.styleable.RDrawText_r_draw_text_draw_size, ((int) textSize));
        textGravity = array.getInt(R.styleable.RDrawText_r_draw_text_gravity, textGravity);

        if (textColor == null) {
            textColor = ColorStateList.valueOf(getBaseColor());
        }

        array.recycle();

        lineSpacingExtra = getResources().getDimensionPixelOffset(R.dimen.base_ldpi);

        mBasePaint.setTextSize(textSize);
    }

    private int[] getCurState() {
        return new int[]{};
    }

    private void makeLayout(int widthSize) {
        if (drawText == null) {
            drawText = "";
        }
        mBasePaint.setTextSize(textSize);
        /**
         * CharSequence source : 需要分行的字符串
         * int bufstart : 需要分行的字符串从第几的位置开始
         * int bufend : 需要分行的字符串到哪里结束
         * TextPaint paint : 画笔对象
         * int outerwidth : layout的宽度，超出时换行
         * Alignment align : layout的对其方式，有ALIGN_CENTER， ALIGN_NORMAL， ALIGN_OPPOSITE 三种
         * float spacingmult : 相对行间距，相对字体大小，1.5f表示行间距为1.5倍的字体高度。
         * float spacingadd : 在基础行距上添加多少
         * boolean includepad,
         * TextUtils.TruncateAt ellipsize : 从什么位置开始省略
         * int ellipsizedWidth : 超过多少开始省略
         * */
        textLayout = new StaticLayout(
                drawText, mBasePaint, widthSize - getPaddingHorizontal(),
                Layout.Alignment.ALIGN_NORMAL,
                1.0f, lineSpacingExtra, false);
    }

    @Override
    public int[] measureDraw(int widthMeasureSpec, int heightMeasureSpec) {
        if (textLayout == null) {
            int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
            makeLayout(widthSize);
        }
        return super.measureDraw(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public int measureDrawWidth() {
        if (TextUtils.isEmpty(drawText) || textLayout.getLineCount() < 1) {
            return 0;
        }
        return Math.min(textLayout.getWidth(), ((int) textLayout.getLineRight(0)));
    }

    @Override
    public int measureDrawHeight() {
        if (TextUtils.isEmpty(drawText)) {
            return 0;
        }
        return textLayout.getHeight();
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        if (this.textSize == textSize) {
            return;
        }
        this.textSize = textSize;
        this.drawTextSize = textSize;
        textLayout = null;
        requestLayout();
    }

    protected boolean isCenter() {
        return haveInt(textGravity, GRAVITY_CENTER);
    }

    protected int getTextDrawX() {
        if (isCenter()) {
            return getPaddingLeft() + getViewDrawWidth() / 2 - measureDrawWidth() / 2 + textOffsetX;
        }
        if (haveInt(textGravity, GRAVITY_RIGHT)) {
            return getViewWidth() - getPaddingRight() - measureDrawWidth() - textOffsetX;
        }
        return getPaddingLeft() + textOffsetX;
    }

    protected int getTextDrawY() {
        if (isCenter()) {
            return (int) (getPaddingTop() + getViewDrawHeight() / 2 + measureDrawHeight() / 2 - mBasePaint.descent()) + textOffsetY;
        }
        if (haveInt(textGravity, GRAVITY_BOTTOM)) {
            return (int) (getViewHeight() - getPaddingBottom() - mBasePaint.descent() - textOffsetY);
        }
        return (int) (getPaddingTop() - mBasePaint.ascent()) + textOffsetY;
    }

    protected int getDrawTextColor() {
        return textColor.getColorForState(getCurState(), getBaseColor());
    }

    @Override
    public void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();

        canvas.translate(getPaddingLeft(), getPaddingRight());
        if (!TextUtils.isEmpty(drawText)) {
            mBasePaint.setTextSize(drawTextSize);
            mBasePaint.setColor(getDrawTextColor());
            //canvas.drawText(drawText, getTextDrawX(), getTextDrawY(), mBasePaint);
            textLayout.draw(canvas);
        }
        canvas.restore();
    }

    public float getDrawTextSize() {
        return drawTextSize;
    }

    public void setDrawTextSize(float drawTextSize) {
        if (this.drawTextSize == drawTextSize) {
            return;
        }
        this.drawTextSize = drawTextSize;
        invalidate();
    }

    public void setTextColor(int textColor) {
        setTextColor(ColorStateList.valueOf(textColor));
    }

    public void setTextColor(ColorStateList textColor) {
        this.textColor = textColor;
        postInvalidate();
    }

    public void setDrawText(CharSequence drawText) {
        if (TextUtils.equals(this.drawText, drawText)) {
            return;
        }
        this.drawText = drawText;
        textLayout = null;
        requestLayout();
    }

    public void setTextGravity(int textGravity) {
        if (this.textGravity == textGravity) {
            return;
        }
        this.textGravity = textGravity;
        postInvalidate();
    }

    public void setTextOffsetX(int textOffsetX) {
        if (this.textOffsetX == textOffsetX) {
            return;
        }
        this.textOffsetX = textOffsetX;
        postInvalidate();
    }

    public void setTextOffsetY(int textOffsetY) {
        if (this.textOffsetY == textOffsetY) {
            return;
        }
        this.textOffsetY = textOffsetY;
        postInvalidate();
    }
}
