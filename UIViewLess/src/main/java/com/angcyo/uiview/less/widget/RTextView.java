package com.angcyo.uiview.less.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.*;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import com.angcyo.uiview.less.R;
import com.angcyo.uiview.less.draw.RDrawNoRead;
import com.angcyo.uiview.less.draw.TextDraw;
import com.angcyo.uiview.less.kotlin.ExKt;
import com.angcyo.uiview.less.kotlin.ViewExKt;
import com.angcyo.uiview.less.skin.SkinHelper;
import com.angcyo.uiview.less.utils.RUtils;
import com.angcyo.uiview.less.utils.Reflect;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 通过Tag属性, 得到text格式化模板, 然后重写setText实现格式化
 * Created by angcyo on 2017-01-08.
 */

public class RTextView extends AppCompatTextView {

    public static final int LEFT_DRAWABLE_GRAVITY_TOP = 1;
    public static final int LEFT_DRAWABLE_GRAVITY_CENTER_VERTICAL = 2;

    /**
     * 滚动文本的方式, 从视图外, 缓缓的向左滚动
     */
    public static final int SCROLL_TYPE_DEFAULT = 1;
    /**
     * 滚动文本的方式, 从0开始, 缓缓的向左滚动
     */
    public static final int SCROLL_TYPE_START = 2;
    protected boolean useSkinStyle = false;
    /**
     * 宽高是否相等, 取其中最大值计算
     */
    protected boolean aeqWidth = false;
    /**
     * 左边垂直的矩形
     */
    RectF leftColorRect;
    /**
     * 左边垂直矩形的圆角大小
     */
    float leftRoundSize = 0;
    Paint colorPaint;
    int leftWidth = 0;
    @ColorInt
    int leftColor;
    int leftStringColor;
    int leftStringSize = 14;
    boolean hasUnderline = false;
    boolean isAttached = false;
    /**
     * 由于系统的drawableLeft, 并不会显示在居中文本的左边, 所以自定义此属性
     */
    Drawable textLeftDrawable;
    /**
     * textLeftDrawable filter color
     */
    int textLeftDrawableColor = -2;
    int leftDrawableGravity = LEFT_DRAWABLE_GRAVITY_CENTER_VERTICAL;
    int leftDrawableOffsetX = 0;
    int leftDrawableOffsetY = 0;
    /**
     * 将textLeftDrawable和TextView的文本居中都居中显示. (默认情况 文本会居中显示, 然后leftDrawable在文本的左边绘制)
     */
    boolean centerLeftDrawable = false;
    float scrollCurX = 0f;
    /**
     * 滚动步长
     */
    int scrollStep = 2;
    /**
     * 暂停滚动
     */
    boolean pauseScroll = false;
    RDrawNoRead mDrawNoRead;
    TextDraw textDraw;
    private Drawable mBackgroundDrawable;
    private CharSequence mRawText;
    private int mPaddingLeft;
    private TextPaint mTextPaint;
    private TextPaint mScrollTextPaint;
    private int mLeftOffset = 0, mTopOffset = 0, mBottomOffset = 0, textLeftOffset = (int) (2 * density());
    private String mLeftString;
    private boolean leftStringBold = false;
    private boolean autoFixTextSize = false;
    private boolean hideWithEmptyText = false;
    /**
     * 是否显示tip文本 (目前只支持右上角显示)
     */
    private boolean isShowTipText = false;
    private String tipText = "";
    private int tipTextColor = Color.WHITE;
    private int highlightWordColor = Color.RED;
    private int tipTextBgColor = Color.parseColor("#FF3333");
    private int tipTextSize = (int) (9 * density());
    //会根据Gravity, Left会有Right的作用
    private int tipTextTopOffset = 0;
    private int tipTextLeftOffset = 0;
    private RectF tipTextRectF = new RectF();
    /**
     * 是否滚动文本, 会自动设置以下属性
     * android:ellipsize="none"
     * android:singleLine="true"
     */
    private boolean isScrollText = false;
    /**
     * 是否循环绘制文本, 在文本后面连续绘制文本
     */
    private boolean isScrollTextCircle = false;
    /**
     * 循环绘制文本的间隙
     */
    private int scrollTextCircleOffset = -1;
    private int scrollType = SCROLL_TYPE_DEFAULT;
    /**
     * 使用英文字符数过滤, 一个汉字等于2个英文, 一个emoji表情等于2个汉字
     */
    private boolean useCharLengthFilter = false;
    /**
     * 滚动循环绘制文本的圈数, 会在没次setText的时候, 重置为0
     */
    private long scrollLoopCount = 0;
    private OnScrollTextLoopListener onScrollTextLoopListener;

    public RTextView(Context context) {
        this(context, null);
    }

    public RTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //绘制左边的提示竖线
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RTextView);
        leftColor = typedArray.getColor(R.styleable.RTextView_r_left_color,
                isInEditMode() ? getResources().getColor(R.color.colorPrimary) : SkinHelper.getSkin().getThemeColor());
        textLeftDrawableColor = typedArray.getColor(R.styleable.RTextView_r_left_drawable_color, textLeftDrawableColor);
        leftWidth = typedArray.getDimensionPixelOffset(R.styleable.RTextView_r_left_width, 0);
        leftRoundSize = typedArray.getDimensionPixelOffset(R.styleable.RTextView_r_left_round_size, (int) leftRoundSize);
        hasUnderline = typedArray.getBoolean(R.styleable.RTextView_r_has_underline, false);
        centerLeftDrawable = typedArray.getBoolean(R.styleable.RTextView_r_center_left_drawable, centerLeftDrawable);
        mBackgroundDrawable = typedArray.getDrawable(R.styleable.RTextView_r_background);

        //绘制左边的提示文本
        mPaddingLeft = getPaddingLeft();
        leftStringColor = typedArray.getColor(R.styleable.RTextView_r_left_text_color, getCurrentTextColor());
        leftStringSize = typedArray.getDimensionPixelOffset(R.styleable.RTextView_r_left_text_size, (int) (leftStringSize * density()));
        ensurePaint();

        mLeftOffset = typedArray.getDimensionPixelOffset(R.styleable.RTextView_r_left_margin, mLeftOffset);
        mBottomOffset = typedArray.getDimensionPixelOffset(R.styleable.RTextView_r_bottom_margin, mBottomOffset);
        mTopOffset = typedArray.getDimensionPixelOffset(R.styleable.RTextView_r_top_margin, mTopOffset);
        textLeftOffset = typedArray.getDimensionPixelOffset(R.styleable.RTextView_r_left_text_offset, textLeftOffset);
        leftStringBold = typedArray.getBoolean(R.styleable.RTextView_r_left_text_bold, leftStringBold);

        String string = typedArray.getString(R.styleable.RTextView_r_left_text);
        setLeftString(string);

        textLeftDrawable = typedArray.getDrawable(R.styleable.RTextView_r_left_drawable);
        if (textLeftDrawable != null) {
            textLeftDrawable.setBounds(0, 0, textLeftDrawable.getIntrinsicWidth(), textLeftDrawable.getIntrinsicHeight());
        }
        filterLeftDrawable();

        leftDrawableGravity = typedArray.getInt(R.styleable.RTextView_r_left_drawable_gravity, leftDrawableGravity);
        leftDrawableOffsetX = typedArray.getDimensionPixelOffset(R.styleable.RTextView_r_left_drawable_offset_x, leftDrawableOffsetX);
        leftDrawableOffsetY = typedArray.getDimensionPixelOffset(R.styleable.RTextView_r_left_drawable_offset_y, leftDrawableOffsetY);

        mDrawNoRead = new RDrawNoRead(this, attrs);
        mDrawNoRead.initAttribute(attrs);

        autoFixTextSize = typedArray.getBoolean(R.styleable.RTextView_r_auto_fix_text_size, autoFixTextSize);

        aeqWidth = typedArray.getBoolean(R.styleable.RTextView_r_is_aeq_width, aeqWidth);
        hideWithEmptyText = typedArray.getBoolean(R.styleable.RTextView_r_hide_with_empty_text, hideWithEmptyText);

        isShowTipText = typedArray.getBoolean(R.styleable.RTextView_r_is_show_tip_text, isShowTipText);
        tipText = typedArray.getString(R.styleable.RTextView_r_tip_text);
        if (tipText == null) {
            tipText = "";
        }
        tipTextBgColor = typedArray.getColor(R.styleable.RTextView_r_tip_text_bg, tipTextBgColor);
        tipTextColor = typedArray.getColor(R.styleable.RTextView_r_tip_text_color, tipTextColor);
        tipTextSize = typedArray.getDimensionPixelOffset(R.styleable.RTextView_r_tip_text_size, tipTextSize);
        tipTextTopOffset = typedArray.getDimensionPixelOffset(R.styleable.RTextView_r_tip_text_top_offset, tipTextTopOffset);
        tipTextLeftOffset = typedArray.getDimensionPixelOffset(R.styleable.RTextView_r_tip_text_left_offset, tipTextLeftOffset);

        isScrollText = typedArray.getBoolean(R.styleable.RTextView_r_is_scroll_text, isScrollText);
        if (isScrollText) {
            setSingleLine(true);
            setEllipsize(null);
        }
        isScrollTextCircle = typedArray.getBoolean(R.styleable.RTextView_r_is_scroll_text_circle, isScrollTextCircle);
        scrollStep = typedArray.getInt(R.styleable.RTextView_r_scroll_step, scrollStep);
        scrollType = typedArray.getInt(R.styleable.RTextView_r_scroll_type, scrollType);
        if (!isInEditMode()) {
            scrollTextCircleOffset = (int) (10 * density());
        }
        scrollTextCircleOffset = typedArray.getDimensionPixelOffset(R.styleable.RTextView_r_scroll_text_offset, scrollTextCircleOffset);

        if (scrollType == SCROLL_TYPE_START) {
            scrollCurX = scrollTextCircleOffset;
        }

        useSkinStyle = typedArray.getBoolean(R.styleable.RTextView_r_use_skin_style, useSkinStyle);

        useCharLengthFilter = typedArray.getBoolean(R.styleable.RTextView_r_use_chat_length_filter, useCharLengthFilter);

        if (isInEditMode()) {
            highlightWordColor = typedArray.getColor(R.styleable.RTextView_r_highlight_word_color, highlightWordColor);
        } else {
            highlightWordColor = typedArray.getColor(R.styleable.RTextView_r_highlight_word_color, SkinHelper.getSkin().getThemeSubColor());
        }

        int maxLength = -1;
//        int maxLength = typedArray.getInt(com.android.internal.R.styleable.TextView_maxLength, -1);
//        if (maxLength > 0) {
//            //暂时不替换
//        }
        maxLength = typedArray.getInt(R.styleable.RTextView_r_max_length, -1);
        if (maxLength > 0) {
            //暂时不替换
            setMaxLength(maxLength, true);
        }

        textDraw = new TextDraw(this);

        typedArray.recycle();

        initView();
    }

    public static void setLeftIco(TextView textView, @DrawableRes int leftIco) {
        Drawable[] compoundDrawables = textView.getCompoundDrawables();
        textView.setCompoundDrawablesWithIntrinsicBounds(ViewExKt.getDrawable(textView, leftIco),
                compoundDrawables[1], compoundDrawables[2], compoundDrawables[3]);
    }

    public static void setLeftIco(TextView textView, Drawable drawable) {
        Drawable[] compoundDrawables = textView.getCompoundDrawables();
        textView.setCompoundDrawablesWithIntrinsicBounds(drawable,
                compoundDrawables[1], compoundDrawables[2], compoundDrawables[3]);
    }

    public static void setRightIco(TextView textView, @DrawableRes int rightIco) {
        setRightIco(textView, ViewExKt.getDrawable(textView, rightIco));
    }

    public static void setRightIco(TextView textView, Drawable rightIco) {
        Drawable[] compoundDrawables = textView.getCompoundDrawables();
        textView.setCompoundDrawablesWithIntrinsicBounds(compoundDrawables[0],
                compoundDrawables[1], rightIco, compoundDrawables[3]);
    }

    public static void setTopIco(TextView textView, @DrawableRes int topIco) {
        setTopIco(textView, ViewExKt.getDrawable(textView, topIco));
    }

    public static void setTopIco(TextView textView, Drawable topIco) {
        Drawable[] compoundDrawables = textView.getCompoundDrawables();
        textView.setCompoundDrawablesWithIntrinsicBounds(compoundDrawables[0], topIco,
                compoundDrawables[2], compoundDrawables[3]);
    }

    public static void setBottomIco(TextView textView, @DrawableRes int bottomIco) {
        Drawable[] compoundDrawables = textView.getCompoundDrawables();
        textView.setCompoundDrawablesWithIntrinsicBounds(
                compoundDrawables[0], compoundDrawables[1],
                compoundDrawables[2], ViewExKt.getDrawable(textView, bottomIco));
    }

    public static void setBottomIco(TextView textView, Drawable drawable) {
        Drawable[] compoundDrawables = textView.getCompoundDrawables();
        textView.setCompoundDrawablesWithIntrinsicBounds(
                compoundDrawables[0], compoundDrawables[1],
                compoundDrawables[2], drawable);
    }

    public void setTextLeftDrawable(@DrawableRes int id) {
        Drawable drawable = ContextCompat.getDrawable(getContext(), id);
        if (drawable != null) {
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            textLeftDrawable = drawable;
            postInvalidate();
        }
    }

    public void setTextLeftDrawableColor(int textLeftDrawableColor) {
        this.textLeftDrawableColor = textLeftDrawableColor;
        if (textLeftDrawable != null) {
            filterLeftDrawable();
            postInvalidate();
        }
    }

    public void setLeftString(String leftString) {
        mLeftString = leftString;
        if (!TextUtils.isEmpty(mLeftString)) {
            float textWidth = ExKt.textWidth(mTextPaint, mLeftString);
            setPadding((int) (mPaddingLeft + textWidth + textLeftOffset), getPaddingTop(), getPaddingRight(), getPaddingBottom());
        } else {
            setPadding(mPaddingLeft, getPaddingTop(), getPaddingRight(), getPaddingBottom());
        }
    }

    private void ensurePaint() {
        if (mTextPaint == null) {
            mTextPaint = new TextPaint(getPaint());
            mTextPaint.setTextSize(leftStringSize);
        }
    }

    public float density() {
        return getResources().getDisplayMetrics().density;
    }

    @Override
    public void draw(Canvas canvas) {
        if (mBackgroundDrawable != null) {
            mBackgroundDrawable.setBounds(canvas.getClipBounds());
            mBackgroundDrawable.draw(canvas);
        }
        super.draw(canvas);
    }

    protected void initView() {
        initLeftRes();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initLeftRes();
    }

    @Deprecated
    protected void onDrawScrollTextOld(Canvas canvas) {
        String text = String.valueOf(getText());
        if (getText() == null) {
            if (isInEditMode()) {
                text = "会滚动的文本";
            } else {
                text = "";
            }
        }
        if (mScrollTextPaint == null) {
            mScrollTextPaint = new TextPaint(getPaint());
            //mScrollTextPaint.setTextAlign(Paint.Align.LEFT);
        }
        mScrollTextPaint.setColor(getCurrentTextColor());

        float textWidth = mScrollTextPaint.measureText(text);

        float drawTextY = getPaddingTop() - mScrollTextPaint.ascent();//getMeasuredHeight() - getPaddingBottom();

        float offset = scrollTextCircleOffset;
        if (scrollTextCircleOffset < 0) {
            offset = getMeasuredWidth() - textWidth;
        }

        if (scrollType == SCROLL_TYPE_DEFAULT) {
            if (isInEditMode()) {
                scrollCurX = 100;
            }

            canvas.drawText(text, getMeasuredWidth() - scrollCurX, drawTextY, mScrollTextPaint);

            if (isScrollTextCircle) {
                canvas.drawText(text, getMeasuredWidth() - scrollCurX + textWidth + offset, drawTextY, mScrollTextPaint);
            }
        } else if (scrollType == SCROLL_TYPE_START) {
            canvas.drawText(text, -scrollCurX, drawTextY, mScrollTextPaint);

            if (isScrollTextCircle) {
                canvas.drawText(text, -scrollCurX + textWidth + offset, drawTextY, mScrollTextPaint);
            }
        }

        scrollCurX += scrollStep;

        if (scrollType == SCROLL_TYPE_DEFAULT) {
            if (scrollCurX >= (textWidth + offset)) {
                if (isScrollTextCircle) {
                    scrollCurX = getMeasuredWidth();
                } else {
                    scrollCurX = 0;
                }
            }
        } else if (scrollType == SCROLL_TYPE_START) {
            if (scrollCurX >= textWidth + offset) {
                scrollCurX = 0;
            }
        }

        if (isInEditMode()) {
        } else {
            if (getVisibility() == View.VISIBLE && !pauseScroll) {
                postInvalidateOnAnimation();
            }
        }

        //canvas.drawText(text, );
        //canvas.translate(-100, 0);
        //super.onDraw(canvas);
    }

    private float getScrollTextWidth() {
        String text = String.valueOf(getText());
        if (getText() == null) {
            if (isInEditMode()) {
                text = "会滚动的文本";
            } else {
                text = "";
            }
        }

        if (TextUtils.isEmpty(text)) {
            return 0;
        }

        if (mScrollTextPaint == null) {
            mScrollTextPaint = new TextPaint(getPaint());
            //mScrollTextPaint.setTextAlign(Paint.Align.LEFT);
        }
        mScrollTextPaint.setColor(getCurrentTextColor());

        float textWidth = mScrollTextPaint.measureText(text);
        return textWidth;
    }

    private void translateTo(Canvas canvas, float translate) {
        canvas.save();
        canvas.translate(translate, 0);
        canvas.clipRect(0, 0, -translate + getMeasuredWidth(), getMeasuredHeight());
        super.onDraw(canvas);
        canvas.restore();
    }

    protected void onDrawScrollText(Canvas canvas) {
        float textWidth = getScrollTextWidth();

        float offset = scrollTextCircleOffset;
        if (scrollTextCircleOffset < 0) {
            offset = getMeasuredWidth() - textWidth;
        }

        float translateX = 0;
        if (scrollType == SCROLL_TYPE_DEFAULT) {
            if (isInEditMode()) {
                scrollCurX = 40 * ViewExKt.getDpi(canvas);
            }

            //canvas.drawText(text, getMeasuredWidth() - scrollCurX, drawTextY, mScrollTextPaint);

            translateX = getMeasuredWidth() - scrollCurX;
            translateTo(canvas, translateX);

            if (isScrollTextCircle) {
                //canvas.drawText(text, getMeasuredWidth() - scrollCurX + textWidth + offset, drawTextY, mScrollTextPaint);

                translateX = getMeasuredWidth() - scrollCurX + textWidth + offset;
                translateTo(canvas, translateX);
            }
        } else if (scrollType == SCROLL_TYPE_START) {
            //canvas.drawText(text, -scrollCurX, drawTextY, mScrollTextPaint);

            translateX = -scrollCurX;
            translateTo(canvas, translateX);

            if (isScrollTextCircle) {
                //canvas.drawText(text, -scrollCurX + textWidth + offset, drawTextY, mScrollTextPaint);

                translateX = -scrollCurX + textWidth + offset;
                translateTo(canvas, translateX);
            }
        }

        scrollCurX += scrollStep;

        if (scrollType == SCROLL_TYPE_DEFAULT) {
            if (scrollCurX >= (getMeasuredWidth() + textWidth + offset)) {
                if (isScrollTextCircle) {
                    scrollCurX = getMeasuredWidth();
                } else {
                    scrollCurX = 0;
                }
                scrollLoopCount++;

                if (onScrollTextLoopListener != null) {
                    onScrollTextLoopListener.onScrollLoop(this, scrollLoopCount);
                }
            }
        } else if (scrollType == SCROLL_TYPE_START) {
            if (scrollCurX >= textWidth + offset) {
                scrollCurX = 0;
                scrollLoopCount++;

                if (onScrollTextLoopListener != null) {
                    onScrollTextLoopListener.onScrollLoop(this, scrollLoopCount);
                }
            }
        }

        if (isInEditMode()) {
        } else {
            if (getVisibility() == View.VISIBLE && !pauseScroll) {
                postInvalidateOnAnimation();
            }
        }

        //canvas.drawText(text, );
        //canvas.translate(-100, 0);
        //super.onDraw(canvas);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isScrollText) {
            onDrawScrollText(canvas);
            return;
        }

        if (hasUnderline) {
            getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
            getPaint().setAntiAlias(true);
        }

        int centerSaveCount = -1;
        if (centerLeftDrawable && textLeftDrawable != null) {
            centerSaveCount = canvas.save();
            float dx = (textLeftDrawable.getIntrinsicWidth() + getCompoundDrawablePadding()) * 1f / 2;
            canvas.translate(dx, 0);
        } else {
            textDraw.onDraw(canvas);
        }
        super.onDraw(canvas);
        if (leftWidth > 0) {
            canvas.drawRoundRect(leftColorRect, leftRoundSize, leftRoundSize, colorPaint);
        }

        //左边绘制一些文本
        drawLeftString(canvas);
        //左边绘制drawable
        drawLeftDrawable(canvas);

        mDrawNoRead.onDraw(canvas);

        if (centerSaveCount != -1) {
            canvas.restoreToCount(centerSaveCount);
        }

        //提示文本, 可以用来提示 数字(未读数字)
        drawTipString(canvas);
    }

    private void drawTipString(Canvas canvas) {
        if (isShowTipText /*|| isInEditMode()*/) {
            TextPaint textPaint = mTextPaint;
            textPaint.setStyle(Paint.Style.FILL);

            float textW = ViewExKt.textWidth(this, String.valueOf(getText()));
            float textH = ViewExKt.textHeight(this);

            textPaint.setTextSize(tipTextSize);
            float tipW = ViewExKt.textWidth(this, textPaint, tipText);
            float tipH = ViewExKt.textHeight(this, textPaint);

            tipTextRectF.set(getMeasuredWidth() - tipW,
                    getMeasuredHeight() - getPaddingBottom() - textH - tipH,
                    getMeasuredWidth(),
                    getMeasuredHeight() - getPaddingBottom() - textH);

            float round = 6 * density();
            tipTextRectF.inset(-round / 2, 0);//让背景和文本有点距离
            tipTextRectF.offset(-round, round);//尽量和原有的文本相贴合

            tipTextRectF.offset(-tipTextLeftOffset, -tipTextTopOffset);

            textPaint.setColor(tipTextBgColor);
            canvas.drawRoundRect(tipTextRectF, round, round, textPaint);

            textPaint.setColor(tipTextColor);
            //默认位置在右上角
            canvas.drawText(tipText, tipTextRectF.centerX() - tipW / 2, tipTextRectF.bottom - textPaint.descent(), textPaint);
        }
    }

    private void filterLeftDrawable() {
        if (textLeftDrawable != null) {
            if (textLeftDrawableColor != -2) {
                textLeftDrawable.setColorFilter(textLeftDrawableColor, PorterDuff.Mode.SRC_ATOP);
            }
        }
    }

    private void drawLeftDrawable(Canvas canvas) {
        if (textLeftDrawable != null) {
            if (leftDrawableGravity == LEFT_DRAWABLE_GRAVITY_TOP) {
                canvas.save();
                canvas.translate(leftDrawableOffsetX, leftDrawableOffsetY);
                textLeftDrawable.draw(canvas);
                canvas.restore();
            } else if (leftDrawableGravity == LEFT_DRAWABLE_GRAVITY_CENTER_VERTICAL) {
                canvas.save();
//            Layout layout = getLayout();
                //layout.getLineStart()

//            canvas.translate(rawPaddingLeft + (viewDrawWith - drawWidth) / 2, paddingTop.toFloat() + (viewDrawHeight - drawHeight) / 2)

                canvas.translate(getMeasuredWidth() / 2 -
                                ViewExKt.textWidth(this, String.valueOf(getText())) / 2 -
                                textLeftDrawable.getIntrinsicWidth() -
                                getCompoundDrawablePadding() - leftDrawableOffsetX,
                        getMeasuredHeight() / 2 - textLeftDrawable.getIntrinsicHeight() / 2 + leftDrawableOffsetY);

                textLeftDrawable.draw(canvas);

                canvas.restore();
            }
        }
    }

    private void drawLeftString(Canvas canvas) {
        ensurePaint();

        if (!TextUtils.isEmpty(mLeftString)) {
            //底部绘制文本
            //mTextPaint.drawOriginText(canvas, mLeftString, getPaddingLeft(), getMeasuredHeight() - getPaddingBottom());
            //居中绘制文本
            canvas.save();
            canvas.translate(-getPaddingLeft() + getScrollX(), 0);
            mTextPaint.setColor(leftStringColor);
            mTextPaint.setTextSize(leftStringSize);
//            mTextPaint.drawOriginText(canvas, mLeftString, getPaddingLeft() + mPaddingLeft,
//                    (getMeasuredHeight() - getPaddingBottom() - getPaddingTop()) / 2 +
//                            getPaddingTop() + mTextPaint.getTextHeight() / 2
//            );
            TextPaint textPaint = mTextPaint;
            addPaintFlags(textPaint, leftStringBold, Paint.FAKE_BOLD_TEXT_FLAG, false);
            //textPaint.setFakeBoldText(leftStringBold);
            Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
//            canvas.drawText(mLeftString,
//                    getPaddingLeft() + mPaddingLeft,
//                    getPaddingTop() - fontMetrics.ascent + fontMetrics.descent /*fontMetrics.ascent + fontMetrics.descent*/,
//                    textPaint);
            float textY = 0;
            if (ExKt.have(getGravity(), Gravity.BOTTOM)) {
                if (isInEditMode()) {
                    textPaint.setColor(Color.RED);
                }
                textY = getMeasuredHeight() - getPaddingBottom() - fontMetrics.descent;
            } else if (ExKt.have(getGravity(), Gravity.TOP)) {
                if (isInEditMode()) {
                    textPaint.setColor(Color.GREEN);
                }
                textY = getPaddingTop() - fontMetrics.ascent;
            } else if (ExKt.have(getGravity(), Gravity.CENTER_VERTICAL)) {
                if (isInEditMode()) {
                    textPaint.setColor(Color.BLUE);
                }
                textY = getMeasuredHeight() - getPaddingBottom() - (fontMetrics.descent - fontMetrics.ascent) / 2;
            } else {
                if (isInEditMode()) {
                    textPaint.setColor(Color.YELLOW);
                }
                textY = getPaddingTop() - fontMetrics.ascent + fontMetrics.descent; /*fontMetrics.ascent + fontMetrics.descent*/
            }

            canvas.drawText(mLeftString,
                    getPaddingLeft() + mPaddingLeft,
                    textY,
                    textPaint);

            canvas.restore();
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (aeqWidth) {
            int size = Math.max(getMeasuredWidth(), getMeasuredHeight());
            if (ExKt.have(getGravity(), Gravity.CENTER) ||
                    ExKt.have(getGravity(), Gravity.CENTER_HORIZONTAL) ||
                    ExKt.have(getGravity(), Gravity.CENTER_VERTICAL)) {
                super.onMeasure(ViewExKt.exactlyMeasure(this, size), ViewExKt.exactlyMeasure(this, size));
            } else {
                setMeasuredDimension(size, size);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (isScrollText) {
            float textWidth = getScrollTextWidth();
            right = (int) (left + textWidth + getPaddingLeft() + getPaddingRight());
            setRight(right);
        }

        if (autoFixTextSize &&
                (!ExKt.have(getInputType(), EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE) || getMaxLines() == 1)) {
            while (getPaint().getTextSize() > 9 &&
                    ViewExKt.textWidth(this, getText().toString()) > ViewExKt.getViewDrawWith(this)) {
                setTextSize(TypedValue.COMPLEX_UNIT_PX, getTextSize() - 1 * density());
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isAttached = false;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        isAttached = true;
//        if (getContentDescription() != null) {
//            LinkifyCompat.addLinks(this, Linkify.ALL);
//        }
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        scrollCurX = 0;
        scrollLoopCount = 0;

        if (hideWithEmptyText) {
            setVisibility(TextUtils.isEmpty(text) ? View.GONE : View.VISIBLE);
        }

        if (getTag() != null &&
                getTag().toString().contains("%") &&
                !"title".equalsIgnoreCase(getTag().toString()) /**当出现在TitleBar中, 会有这个标志*/ &&
                !TextUtils.isEmpty(text)) {
            try {
                final String format = String.format(Locale.CHINA, getTag().toString(), text);
                setTextEx(format, type);
            } catch (Exception e) {
                setTextEx(text, type);
            }
        } else {
            setTextEx(text, type);
        }
    }

    /**
     * 系统的省略号, 有时不会显示, 采用Span手动处理
     */
    private void setTextEx(CharSequence text, BufferType type) {
        if (TextUtils.isEmpty(text)) {
            setSuperText(text, type);
            return;
        }

        SpannableStringBuilder spanBuilder;
        if (text instanceof SpannableStringBuilder) {
            spanBuilder = (SpannableStringBuilder) text;
        } else {
            spanBuilder = new SpannableStringBuilder(text);
        }

        if (getMaxLines() == 1 && getEllipsize() == TextUtils.TruncateAt.END) {

            int maxLength = Integer.MAX_VALUE;
            InputFilter[] filters = getFilters();
            for (InputFilter filter : filters) {
                if (filter instanceof InputFilter.LengthFilter) {
                    maxLength = (int) Reflect.getMember(InputFilter.LengthFilter.class, filter, "mMax");
                } else if (filter instanceof ExEditText.CharLengthFilter) {
                    maxLength = ((ExEditText.CharLengthFilter) filter).getMaxLen();
                }
            }

            /*请在设置 InputLengthFilter的时候, 预加上3个字符*/
            String more = getMoreString();
            int offset = more.length();
            int lastIndex = maxLength - offset;

            int textLength = text.length();
            if (lastIndex >= 0) {
                if (useCharLengthFilter) {
                    if (getCharLength(text) > lastIndex) {
//                    if (EmojiFilter.isEmojiCharacter(spanBuilder.charAt(lastIndex))) {
//                        offset = 4;/*兼容末尾是emoji表情*/
//                    }
                        int charLengthIndex = getCharLengthIndex(text, maxLength);
                        if (charLengthIndex > 0 && textLength > charLengthIndex - offset) {
                            spanBuilder.setSpan(new RExTextView.ImageTextSpan(getContext(), getTextSize(),
                                            getCurrentTextColor(), more),
                                    charLengthIndex - offset, textLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                    }
                } else {
                    if (textLength > lastIndex) {
//                    if (EmojiFilter.isEmojiCharacter(spanBuilder.charAt(lastIndex))) {
//                        offset = 4;/*兼容末尾是emoji表情*/
//                    }
                        spanBuilder.setSpan(new RExTextView.ImageTextSpan(getContext(), getTextSize(),
                                        getCurrentTextColor(), more),
                                lastIndex, textLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
            }
        }

        setSuperText(spanBuilder, type);
    }

    /**
     * 一个汉字等于2个英文, 一个emoji表情等于2个汉字
     */
    public int getCharLength() {
        return getCharLength(getText());
    }

    public int getCharLength(CharSequence text) {
        return RUtils.getCharLength(text.toString());
    }

    /**
     * 在Char过滤情况下, 返回真实对应的index, 用来设置span
     */
    public int getCharLengthIndex(CharSequence text, int maxCharLength) {
        if (TextUtils.isEmpty(text)) {
            return 0;
        }
        int count = 0;
        int index = -1;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) <= (char) ExEditText.CharLengthFilter.MAX_CHAR) {
                count = count + 1;
            } else {
                count = count + 2;
            }
            index++;
            if (count > maxCharLength) {
                break;
            }
        }
        return index;
    }


    @NonNull
    private String getMoreString() {
        return "...";
    }

    private void setSuperText(CharSequence text, BufferType type) {
        mRawText = text;
        super.setText(text, type);
    }

    /**
     * 没有被折叠的文本
     */
    public CharSequence getRawText() {
        return mRawText;
    }

    public void addFilter(InputFilter filter) {
        final InputFilter[] filters = getFilters();
        final InputFilter[] newFilters = new InputFilter[filters.length + 1];
        System.arraycopy(filters, 0, newFilters, 0, filters.length);
        newFilters[filters.length] = filter;
        setFilters(newFilters);
    }

    /**
     * 需要预留3个'...'字符的数量, <em></>已自动处理</em>
     */
    public void setMaxLength(int length) {
        setMaxLength(length, false);
    }

    public void setMaxLength(int length, boolean exclusiveMoreStringLength /*需要加上...的长度?*/) {
        InputFilter[] filters = getFilters();
        boolean have = false;

        InputFilter inputFilter;
        if (useCharLengthFilter) {
            inputFilter = new ExEditText.CharLengthFilter(length + (exclusiveMoreStringLength ? getMoreString().length() : 0));
        } else {
            inputFilter = new InputFilter.LengthFilter(length + (exclusiveMoreStringLength ? getMoreString().length() : 0));
        }

        for (int i = 0; i < filters.length; i++) {
            InputFilter filter = filters[i];
            if (filter instanceof InputFilter.LengthFilter ||
                    filter instanceof ExEditText.CharLengthFilter) {
                have = true;
                filters[i] = inputFilter;
                setFilters(filters);
                break;
            }
        }
        if (!have) {
            addFilter(inputFilter);
        }

        setMaxLines(1);
        //setSingleLine();//去掉系统的...处理
        setEllipsize(TextUtils.TruncateAt.END);
    }

    /**
     * 获取已经设置的最大长度
     */
    private int haveMaxLength() {
        int maxLength = -1;
        InputFilter[] filters = getFilters();
        for (int i = 0; i < filters.length; i++) {
            InputFilter filter = filters[i];
            if (filter instanceof InputFilter.LengthFilter) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    maxLength = ((InputFilter.LengthFilter) filters[i]).getMax();
                } else {
                    maxLength = (int) Reflect.getFieldValue(filters[i], "mMax");
                }
                break;
            }
        }
        return maxLength;
    }

    public void setText(Object... args) {
        if (getTag() != null && args != null && args.length > 0) {
            try {
                final String format = String.format(Locale.CHINA, getTag().toString(), args);
                super.setText(format);
            } catch (Exception e) {
                super.setText("");
            }
        } else {
            super.setText("");
        }
    }

    public Drawable getDrawable(@DrawableRes int icoRes) {
        if (icoRes == -1) {
            return null;
        }
        return ContextCompat.getDrawable(getContext(), icoRes);
    }

    public void setLeftIco(@DrawableRes int leftIco) {
        setLeftIco(this, leftIco);
    }

    public void setRightIco(@DrawableRes int rightIco) {
        setRightIco(this, rightIco);
    }

    public void setTopIco(@DrawableRes int topIco) {
        setTopIco(this, topIco);
    }

    public void setBottomIco(@DrawableRes int bottomIco) {
        setBottomIco(this, bottomIco);
    }

    public void setLeftOffset(int leftOffset) {
        mLeftOffset = leftOffset;
    }

    public void setTopOffset(int topOffset) {
        mTopOffset = topOffset;
    }

    public void setBottomOffset(int bottomOffset) {
        mBottomOffset = bottomOffset;
    }

    private void initLeftRes() {
        if (leftWidth <= 0) {
            return;
        }

        int viewHeight = getMeasuredHeight();
        if (leftColorRect == null) {
            leftColorRect = new RectF();
        }
        if (colorPaint == null) {
            colorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        }
        colorPaint.setColor(leftColor);
        leftColorRect.set(mLeftOffset, getPaddingTop() + mTopOffset,
                mLeftOffset + leftWidth, viewHeight - getPaddingBottom() - mBottomOffset);
    }

    public RTextView setLeftWidth(int leftWidth) {
        this.leftWidth = leftWidth;
        initLeftRes();
        return this;
    }

    public RTextView setLeftColor(int leftColor) {
        this.leftColor = leftColor;
        initLeftRes();
        return this;
    }

    public RTextView setLeftColor(int leftColor, int leftWidth) {
        this.leftColor = leftColor;
        this.leftWidth = leftWidth;
        initLeftRes();
        return this;
    }

    public void setDefaultSKin(int resId) {
        setDefaultSKin(getResources().getString(resId));
    }

    public void setDefaultSKin(String text) {
        setPadding(getResources().getDimensionPixelOffset(R.dimen.base_hdpi), getPaddingTop(),
                getPaddingRight(), getPaddingBottom());
        setLeftColor(SkinHelper.getSkin().getThemeSubColor(), getResources().getDimensionPixelOffset(R.dimen.base_mdpi));
        setText(text);
//        if (isAttached) {
//            postInvalidate();
//        }
    }

    public void setRBackgroundColor(int color) {
        mBackgroundDrawable = new ColorDrawable(color);
    }


    /**
     * 判断 需要检测的开始位置,结束位置, 是否已经在其他span中
     */
    protected boolean isInOtherSpan(SpannableStringBuilder builder, int length, int startPosition, int endPosition) {
        return isInOtherSpan(builder, CharacterStyle.class, length, startPosition, endPosition);
    }

    protected <T> boolean isInOtherSpan(SpannableStringBuilder builder, @Nullable Class<T> kind, int length, int startPosition, int endPosition) {
        T[] spans = builder.getSpans(0, length, kind);
        List<int[]> spanRange = new ArrayList<>();
        for (T span : spans) {
            int spanStart = builder.getSpanStart(span);
            int spanEnd = builder.getSpanEnd(span);

            spanRange.add(new int[]{spanStart, spanEnd});
        }

        boolean result = false;
        for (int[] range : spanRange) {
            if (startPosition >= range[0] && startPosition <= range[1]) {
                result = true;
                break;
            }
            if (endPosition >= range[0] && endPosition <= range[1]) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * 设置高亮的关键字
     */
    public void setHighlightWord(String word) {
        setHighlightWord(word, false);
    }

    public void setHighlightWord(String word, boolean first /*是否只显示找到的第一个*/) {
        CharSequence text = getText();
        if (TextUtils.isEmpty(text) || TextUtils.isEmpty(word)) {
            return;
        }

        SpannableStringBuilder builder;
        if (text instanceof SpannableStringBuilder) {
            builder = (SpannableStringBuilder) text;
        } else {
            builder = new SpannableStringBuilder(text);
        }

        Matcher matcher = Pattern.compile(word).matcher(text);

        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            //CharSequence text = matcher.group();//input.subSequence(start, end);

            builder.setSpan(new ForegroundColorSpan(highlightWordColor),
                    start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            if (first) {
                break;
            }
        }

        setTextEx(builder, BufferType.NORMAL);
    }

    /**
     * 设置是否显示删除线
     */
    public void setDeleteLine(boolean bool) {
        addFlags(bool, Paint.STRIKE_THRU_TEXT_FLAG);
    }

    /**
     * 设置是否显示下划线
     */
    public void setUnderLine(boolean bool) {
        addFlags(bool, Paint.UNDERLINE_TEXT_FLAG);
    }

    /**
     * 设置是否加粗文本
     */
    public void setBoldText(boolean bool) {
        addFlags(bool, Paint.FAKE_BOLD_TEXT_FLAG);
    }

    /**
     * 设置是否斜体
     */
    public void setItalic(boolean bool) {
        if (bool) {
            setTypeface(getTypeface(), Typeface.ITALIC);
        } else {
            setTypeface(getTypeface(), Typeface.NORMAL);
            //setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL), Typeface.NORMAL);
        }
    }

    public void addFlags(boolean add, int flat) {
        TextPaint paint = getPaint();
        addPaintFlags(paint, add, flat);
    }

    public void addPaintFlags(TextPaint paint, boolean add, int flat) {
        addPaintFlags(paint, add, flat, true);
    }

    public void addPaintFlags(TextPaint paint, boolean add, int flat, boolean invalidate) {
        if (add) {
            paint.setFlags(paint.getFlags() | flat);
        } else {
            paint.setFlags(paint.getFlags() & ~flat);
        }
        if (invalidate) {
            postInvalidate();
        }
    }

    /**
     * 是否显示未读小红点
     */
    public void setShowNoRead(boolean showNoRead) {
        mDrawNoRead.setShowNoRead(showNoRead);
    }

    public void setNoReadPaddingRight(float noReadPaddingRight) {
        mDrawNoRead.setNoReadPaddingRight(noReadPaddingRight);
    }

    public RDrawNoRead getDrawNoRead() {
        return mDrawNoRead;
    }

    public void setAutoFixTextSize(boolean autoFixTextSize) {
        this.autoFixTextSize = autoFixTextSize;
    }

    public void setShowTipText(boolean showTipText) {
        isShowTipText = showTipText;
    }

    public void setTipText(String tipText) {
        this.tipText = tipText;
    }

    public void setTipTextColor(int tipTextColor) {
        this.tipTextColor = tipTextColor;
    }

    public void setTipTextBgColor(int tipTextBgColor) {
        this.tipTextBgColor = tipTextBgColor;
    }

    public void setTipTextSize(int tipTextSize) {
        this.tipTextSize = tipTextSize;
    }

    public void setTipTextTopOffset(int tipTextTopOffset) {
        this.tipTextTopOffset = tipTextTopOffset;
    }

    public void setTipTextLeftOffset(int tipTextLeftOffset) {
        this.tipTextLeftOffset = tipTextLeftOffset;
    }

    public boolean isPauseScroll() {
        return pauseScroll;
    }

    public void setPauseScroll(boolean pauseScroll) {
        this.pauseScroll = pauseScroll;
    }

    public void setScrollText(boolean scrollText) {
        isScrollText = scrollText;
        postInvalidate();
    }

    public void setOnScrollTextLoopListener(OnScrollTextLoopListener onScrollTextLoopListener) {
        this.onScrollTextLoopListener = onScrollTextLoopListener;
    }

    public long getScrollLoopCount() {
        return scrollLoopCount;
    }

    @NonNull
    public TextDraw getTextDraw() {
        return textDraw;
    }

    public interface OnScrollTextLoopListener {
        public void onScrollLoop(@NonNull RTextView textView, long scrollLoopCount);
    }
}
