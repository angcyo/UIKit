package com.angcyo.uiview.less.widget.group;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.angcyo.uiview.less.R;

import static com.angcyo.uiview.less.widget.group.RawFragmentLayout.LayoutParams.NO_SET;

/**
 * Email:angcyo@126.com
 *
 * @author angcyo
 * @date 2019/03/08
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
public class RawFragmentLayout extends FrameLayout {
    public RawFragmentLayout(Context context) {
        this(context, null);
    }

    public RawFragmentLayout(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public RawFragmentLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
    }

    @Override
    public FrameLayout.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();

            if (layoutParams.rawWidth != NO_SET) {
                layoutParams.width = MeasureSpec.makeMeasureSpec(layoutParams.rawWidth, MeasureSpec.EXACTLY);
            }
            if (layoutParams.rawHeight != NO_SET) {
                layoutParams.height = MeasureSpec.makeMeasureSpec(layoutParams.rawHeight, MeasureSpec.EXACTLY);
            }
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
        }
        //在不支持 wrap_content
        setMeasuredDimension(resolveSize(0, widthMeasureSpec), resolveSize(0, heightMeasureSpec));
    }

    public static class LayoutParams extends FrameLayout.LayoutParams {

        public static final int NO_SET = -0xFF;

        private int rawWidth = NO_SET;
        private int rawHeight = NO_SET;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);

            final TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.RawFragmentLayout_Layout);
            rawWidth = a.getDimensionPixelOffset(R.styleable.RawFragmentLayout_Layout_r_layout_raw_width, rawWidth);
            rawHeight = a.getDimensionPixelOffset(R.styleable.RawFragmentLayout_Layout_r_layout_raw_height, rawHeight);
            a.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(int width, int height, int gravity) {
            super(width, height, gravity);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(ViewGroup.MarginLayoutParams source) {
            super(source);
        }
    }
}
