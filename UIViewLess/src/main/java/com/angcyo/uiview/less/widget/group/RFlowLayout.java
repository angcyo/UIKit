package com.angcyo.uiview.less.widget.group;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.angcyo.uiview.less.R;
import com.angcyo.uiview.less.kotlin.ViewGroupExKt;
import com.angcyo.uiview.less.resources.ResUtil;
import com.angcyo.uiview.less.skin.SkinHelper;
import com.angcyo.uiview.less.widget.RTextCheckView;
import com.angcyo.uiview.less.widget.RTextView;
import kotlin.jvm.functions.Function1;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by angcyo on 15-10-22-022.
 */
public class RFlowLayout extends LinearLayout {
    /**
     * The M all views.
     */
    List<List<View>> mAllViews = new ArrayList<>();//保存所有行的所有View
    /**
     * The M line height.
     */
    List<Integer> mLineHeight = new ArrayList<>();//保存每一行的行高

    List<View> lineViews = new ArrayList<>();

    /**
     * 每一行最多多少个, 强制限制. -1, 不限制. 大于0生效
     */
    int maxCountLine = -1;

    /**
     * 每一行的Item等宽
     */
    boolean itemEquWidth = false;

    /**
     * item之间, 横竖向间隔.
     */
    int itemHorizontalSpace = 0;
    int itemVerticalSpace = 0;

    /**
     * Instantiates a new Flow radio group.
     *
     * @param context the context
     */
    public RFlowLayout(Context context) {
        this(context, null);
    }

    /**
     * Instantiates a new Flow radio group.
     *
     * @param context the context
     * @param attrs   the attrs
     */
    public RFlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.RFlowLayout);
        maxCountLine = array.getInt(R.styleable.RFlowLayout_r_flow_max_line_child_count, maxCountLine);
        itemEquWidth = array.getBoolean(R.styleable.RFlowLayout_r_flow_equ_width, itemEquWidth);
        itemHorizontalSpace = array.getDimensionPixelOffset(R.styleable.RFlowLayout_r_flow_item_horizontal_space, itemHorizontalSpace);
        itemVerticalSpace = array.getDimensionPixelOffset(R.styleable.RFlowLayout_r_flow_item_vertical_space, itemVerticalSpace);
        array.recycle();

        init();
    }

    private void init() {
        setOrientation(LinearLayout.HORIZONTAL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (getChildCount() == 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int measureHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

        int width = 0, height = 0;
        int lineWidth = 0, lineHeight = 0;
        int childWidth = 0, childHeight = 0;

        mAllViews.clear();
        mLineHeight.clear();
        lineViews.clear();

        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == View.GONE) {
                continue;
            }

            if (itemEquWidth) {
                measureChild(child, MeasureSpec.makeMeasureSpec(measureWidth, MeasureSpec.AT_MOST), heightMeasureSpec);
            } else {
                measureChild(child, widthMeasureSpec, heightMeasureSpec);
            }

            LayoutParams params = (LayoutParams) child.getLayoutParams();

            childWidth = child.getMeasuredWidth() + params.leftMargin + params.rightMargin;
            childHeight = child.getMeasuredHeight() + params.topMargin + params.bottomMargin;

            int lineViewSize = lineViews.size();
            if (lineWidth + childWidth > measureWidth - getPaddingLeft() - getPaddingRight() ||
                    (maxCountLine > 0 && lineViewSize == maxCountLine)) {
                //需要换新行
                if (itemEquWidth) {
                    //margin,padding 消耗的宽度
                    childWidth = measureLineEquWidth(lineViews, measureWidth, heightMeasureSpec) + params.leftMargin + params.rightMargin;
                }

                width = Math.max(width, lineWidth);
                height += lineHeight + itemVerticalSpace;
                mLineHeight.add(lineHeight);
                mAllViews.add(lineViews);

                lineWidth = childWidth;
                lineHeight = childHeight;
                lineViews = new ArrayList<>();
            } else {
                lineWidth += childWidth + itemHorizontalSpace;
                lineHeight = Math.max(childHeight, lineHeight);
            }
            lineViews.add(child);

            if (i == (count - 1)) {
                width = Math.max(width, lineWidth);
                height += lineHeight;
            }
        }
        mLineHeight.add(lineHeight);
        mAllViews.add(lineViews);
        if (itemEquWidth) {
            measureLineEquWidth(lineViews, measureWidth, heightMeasureSpec);
        }
        width += getPaddingLeft() + getPaddingRight();
        height += getPaddingTop() + getPaddingBottom();
        setMeasuredDimension(
                Math.max((modeWidth == MeasureSpec.AT_MOST || modeWidth == MeasureSpec.UNSPECIFIED) ? width : measureWidth,
                        getMinimumWidth()),
                Math.max((modeHeight == MeasureSpec.AT_MOST || modeHeight == MeasureSpec.UNSPECIFIED) ? height : measureHeight,
                        getMinimumHeight()));
    }

    /**
     * 等宽并且maxCountLine>0 的时候, 计算 每个child的需要的宽度, margin 属性, 将使用每一行的第一个child
     */
    private int measureEquChildWidth(List<View> lineViews, int viewWidth) {
        int consumeWidth = getPaddingLeft() + getPaddingRight() + itemHorizontalSpace * Math.max(maxCountLine - 1, 0);
        View firstChild = lineViews.get(0);
        LayoutParams lineViewParams = (LayoutParams) firstChild.getLayoutParams();

        for (int j = 0; j < maxCountLine; j++) {
            consumeWidth += lineViewParams.leftMargin + lineViewParams.rightMargin;
        }

        int lineChildWidth;
        if (maxCountLine > 0) {
            lineChildWidth = (viewWidth - consumeWidth) / maxCountLine;
        } else {
            lineChildWidth = (viewWidth - consumeWidth);
        }

        return lineChildWidth;
    }

    private int measureLineEquWidth(List<View> lineViews, int viewWidth, int heightMeasureSpec) {
        int lineViewSize = lineViews.size();

        int lineChildWidth;

        if (maxCountLine > 0) {
            //等宽并且平分, 当lineViewSize没有达到maxCountLine数量时, 需要考虑计算方式.
            lineChildWidth = measureEquChildWidth(lineViews, viewWidth);
        } else {
            int consumeWidth = getPaddingLeft() + getPaddingRight() + itemHorizontalSpace * Math.max(maxCountLine - 1, 0);
            for (int j = 0; j < lineViewSize; j++) {
                View lineView = lineViews.get(j);
                LayoutParams lineViewParams = (LayoutParams) lineView.getLayoutParams();
                consumeWidth += lineViewParams.leftMargin + lineViewParams.rightMargin;
            }
            lineChildWidth = (viewWidth - consumeWidth) / lineViewSize;
        }

        for (int j = 0; j < lineViewSize; j++) {
            View lineView = lineViews.get(j);
            lineView.measure(MeasureSpec.makeMeasureSpec(lineChildWidth, MeasureSpec.EXACTLY), heightMeasureSpec);
            lineView.getMeasuredWidth();
        }
        return lineChildWidth;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (getChildCount() == 0) {
            super.onLayout(changed, l, t, r, b);
            return;
        }

        int top = getPaddingTop();//开始布局子view的 top距离
        int left = getPaddingLeft();//开始布局子view的 left距离

        int lineNum = mAllViews.size();//行数
        List<View> lineView;
        int lineHeight;
        for (int i = 0; i < lineNum; i++) {
            lineView = mAllViews.get(i);
            lineHeight = mLineHeight.get(i);

            for (int j = 0; j < lineView.size(); j++) {
                View child = lineView.get(j);
                if (child.getVisibility() == View.GONE) {
                    continue;
                }

                LayoutParams params = (LayoutParams) child.getLayoutParams();
                int ld = left + params.leftMargin;
                int td = top + params.topMargin;
                int rd = ld + child.getMeasuredWidth();//不需要加上 params.rightMargin,
                int bd = td + child.getMeasuredHeight();//不需要加上 params.bottomMargin, 因为在 onMeasure , 中已经加在了 lineHeight 中
                child.layout(ld, td, rd, bd);

                left += child.getMeasuredWidth() + params.leftMargin + params.rightMargin + itemHorizontalSpace;//因为在 这里添加了;
            }

            left = getPaddingLeft();
            top += lineHeight + itemVerticalSpace;
        }
    }

    private int getDimensionPixelOffset(@DimenRes int id) {
        return getResources().getDimensionPixelOffset(id);
    }

    private int getColor(@ColorRes int id) {
        return ContextCompat.getColor(getContext(), id);
    }

    public RTextCheckView addCheckTextView(String text) {
        return addCheckTextView(text, SkinHelper.getSkin().getThemeTranColor(0x80), getColor(R.color.base_text_color));
    }

    public RTextCheckView addCheckTextView(String text, int pressBgColor, int pressTextColor) {
        RTextCheckView textView = new RTextCheckView(getContext());
        textView.setText(text);
        textView.setGravity(Gravity.CENTER);

        int lineSize = getDimensionPixelOffset(R.dimen.base_line);
        int radius = getDimensionPixelOffset(R.dimen.base_round_little_radius);
        int offset = getDimensionPixelOffset(R.dimen.base_xxhdpi);
        textView.setPadding(offset, offset / 4, offset, offset / 4);

        LayoutParams params = new LayoutParams(-2, -2);
        params.rightMargin = offset / 2;
        params.topMargin = radius;
        params.bottomMargin = radius;
        textView.setLayoutParams(params);

        textView.setBackground(ResUtil.selectorChecked(
                ResUtil.createDrawable(getColor(R.color.default_base_line), Color.TRANSPARENT, lineSize, radius),
                ResUtil.createDrawable(pressBgColor, radius)
        ));

        textView.setTextColor(ResUtil.generateTextColor(pressTextColor, pressTextColor, getColor(R.color.base_text_color)));

        addView(textView);
        return textView;
    }

    public List<String> getCheckedTextList() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < getChildCount(); i++) {
            View childAt = getChildAt(i);
            if (childAt instanceof RTextCheckView) {
                if (((RTextCheckView) childAt).isChecked()) {
                    list.add(String.valueOf(((RTextCheckView) childAt).getText()));
                }
            }
        }
        return list;
    }

    public List<String> getTextList() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < getChildCount(); i++) {
            View childAt = getChildAt(i);
            if (childAt instanceof RTextView) {
                list.add(String.valueOf(((RTextView) childAt).getText()));
            }
        }
        return list;
    }

    public RTextCheckView addTagTextView(String text) {
        RTextCheckView textView = new RTextCheckView(getContext());
        textView.setText(text);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(getColor(R.color.base_text_color_dark));
        textView.setEnabled(false);//不允许点击

        int radius = getDimensionPixelOffset(R.dimen.base_round_little_radius);
        int offset = getDimensionPixelOffset(R.dimen.base_xxhdpi);
        textView.setPadding(offset, offset / 4, offset, offset / 4);

        LayoutParams params = new LayoutParams(-2, -2);
        params.rightMargin = offset / 2;
        params.topMargin = radius;
        params.bottomMargin = radius;
        textView.setLayoutParams(params);

        textView.setBackground(ResUtil.selectorChecked(
                ResUtil.createDrawable(getColor(R.color.base_chat_bg_color), radius),
                ResUtil.createDrawable(SkinHelper.getSkin().getThemeTranColor(0x80), radius)
        ));

        addView(textView);
        return textView;
    }

    public void addTextView(List<String> texts, final OnAddViewListener onAddViewListener) {
        final int offset = getDimensionPixelOffset(R.dimen.base_ldpi);

        ViewGroupExKt.resetChildCount(this, texts.size(), new Function1<Integer, View>() {
            @Override
            public View invoke(Integer integer) {
                RTextView textView = new RTextView(getContext());
                textView.setTextColor(getColor(R.color.base_text_color));
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getDimensionPixelOffset(R.dimen.default_text_size9));
                textView.setGravity(Gravity.CENTER);

                textView.setPadding(offset, offset / 2, offset, offset / 2);

                LayoutParams layoutParams = new LayoutParams(-2, -2);
                layoutParams.setMarginEnd(offset * 2);
                textView.setLayoutParams(layoutParams);
                return textView;
            }
        });

        for (int i = 0; i < texts.size(); i++) {
            LayoutParams layoutParams = new LayoutParams(-2, -2);
            layoutParams.setMarginEnd(offset * 2);

            TextView textView = (TextView) getChildAt(i);
            textView.setLayoutParams(layoutParams);
            textView.setText(texts.get(i));

            if (onAddViewListener != null) {
                onAddViewListener.onInitView(textView);
            }
        }
    }

    /**
     * 清除除checkView以外的其他View 的check状态
     */
    public void clearCheck(RTextCheckView checkView) {
        for (int i = 0; i < getChildCount(); i++) {
            View childAt = getChildAt(i);
            if (childAt instanceof RCheckGroup.ICheckView) {
                if (childAt != checkView && ((RCheckGroup.ICheckView) childAt).isChecked()) {
                    ((RCheckGroup.ICheckView) childAt).setChecked(false);
                }
            }
        }
    }

    public void setMaxCountLine(int maxCountLine) {
        this.maxCountLine = maxCountLine;
        requestLayout();
    }

    public interface OnAddViewListener {
        void onInitView(View view);
    }
}
