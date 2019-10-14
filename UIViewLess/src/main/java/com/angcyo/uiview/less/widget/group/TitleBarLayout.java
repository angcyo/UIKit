package com.angcyo.uiview.less.widget.group;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.angcyo.uiview.less.R;
import com.angcyo.uiview.less.base.helper.ActivityHelper;
import com.angcyo.uiview.less.utils.RUtils;
import com.angcyo.uiview.less.utils.ScreenUtil;

/**
 * 用来控制状态栏的padding
 * Created by angcyo on 2016-11-05.
 */

public class TitleBarLayout extends FrameLayout {

    boolean enablePadding = true;
    boolean fitActionBar = false;
    boolean isScreenHeight = false;
    int statusBarHeight;
    int actionBarHeight;
    /**
     * 允许的最大高度, 如果为-2px,那么就是屏幕高度的一半, 如果是-3px,那么就是屏幕高度的三分之, 以此内推
     */
    private int maxHeight = -1;

    public TitleBarLayout(Context context) {
        this(context, null);
    }

    public TitleBarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TitleBarLayout);
            maxHeight = typedArray.getDimensionPixelOffset(R.styleable.TitleBarLayout_r_max_height, -1);
            enablePadding = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
            if (enablePadding && context instanceof Activity) {
                enablePadding = ActivityHelper.isLayoutFullScreen((Activity) context);
            }
            fitActionBar = typedArray.getBoolean(R.styleable.TitleBarLayout_r_fit_action_bar_height, fitActionBar);
            enablePadding = typedArray.getBoolean(R.styleable.TitleBarLayout_r_fit_status_bar_height, enablePadding);
            isScreenHeight = typedArray.getBoolean(R.styleable.TitleBarLayout_r_is_screen_height, isScreenHeight);

            resetMaxHeight();
            typedArray.recycle();

            statusBarHeight = RUtils.getStatusBarHeight(getContext());//getResources().getDimensionPixelSize(R.dimen.status_bar_height);
            actionBarHeight = getResources().getDimensionPixelSize(R.dimen.action_bar_height);
        }
        initLayout();
    }

    private void initLayout() {

    }

    private void resetMaxHeight() {
        if (maxHeight < -1) {
            int num = Math.abs(maxHeight);
            maxHeight = ScreenUtil.screenHeight / num;
        }
    }

    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
        resetMaxHeight();
        requestLayout();
    }

    public void setEnablePadding(boolean enablePadding) {
        this.enablePadding = enablePadding;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int topHeight = topHeight();
        int viewHeight = 0;

        int screenHeight = getResources().getDisplayMetrics().heightPixels;

        screenHeight = Math.max(screenHeight, heightSize);

        if (maxHeight > 0) {
            maxHeight += topHeight;
        }

        for (int i = 0; i < getChildCount(); i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                if (lp.fixStatusBar == LayoutParams.FIX_STATUS_BAR_MARGIN_TOP) {
                    lp.topMargin = topHeight;
                } else if (lp.fixStatusBar == LayoutParams.FIX_STATUS_BAR_PADDING_TOP) {
                    child.setPadding(child.getPaddingLeft(), topHeight, child.getPaddingRight(), child.getPaddingBottom());
                    int height = lp.height;
                    if (height > 0) {
                        lp.height = lp.getOldHeight() + topHeight;
                        //如果手动设置过height
                        if (height != lp.height) {
                            lp.setOldHeight(height);
                            lp.height = lp.getOldHeight() + topHeight;
                        }
                    }
                }
            }
        }

        if (maxHeight > 0) {
            super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(Math.min(maxHeight, heightSize + topHeight), heightMode));
        } else {
            viewHeight = Math.min(heightSize + topHeight, screenHeight);
            if (heightMode == MeasureSpec.EXACTLY) {
                int childWidth = widthSize;
                if (getChildCount() > 0) {
                    if (heightSize == screenHeight || isScreenHeight) {
                        heightSize -= topHeight;
                    }
                    getChildAt(0).measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(heightSize - getPaddingBottom(), heightMode));
                    childWidth = getChildAt(0).getMeasuredWidth();
                }
                if (widthMode == MeasureSpec.EXACTLY) {
                    setMeasuredDimension(widthSize, viewHeight);
                } else {
                    setMeasuredDimension(childWidth, viewHeight);
                }
            } else {
                super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(viewHeight, heightMode));
            }
        }

        for (int i = 0; i < getChildCount(); i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                if (lp.fixStatusBar == LayoutParams.FIX_STATUS_BAR_MATCH_PARENT) {
                    //重新测量child的高度
                    if (child.getMeasuredHeight() != getMeasuredHeight()) {
                        child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(getMeasuredHeight(), MeasureSpec.EXACTLY));
                    }
                }
            }
        }
    }

    private int topHeight() {
        int topHeight = 0;
        if (fitActionBar && enablePadding) {
            topHeight = statusBarHeight + actionBarHeight;
        } else if (enablePadding) {
            topHeight = statusBarHeight;
        } else if (fitActionBar) {
            topHeight = actionBarHeight;
        } else {
            topHeight = 0;
        }
        return topHeight;
    }

    @Override
    protected FrameLayout.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public FrameLayout.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    public static class LayoutParams extends FrameLayout.LayoutParams {

        public static final int FIX_STATUS_BAR_MARGIN_TOP = 1;
        public static final int FIX_STATUS_BAR_PADDING_TOP = 2;
        /**
         * 与TitleBarLayout同高度
         */
        public static final int FIX_STATUS_BAR_MATCH_PARENT = 3;

        public int fixStatusBar = FIX_STATUS_BAR_MARGIN_TOP;

        public LayoutParams(@NonNull Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);

            final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TitleBarLayout_Layout);
            fixStatusBar = a.getInt(R.styleable.TitleBarLayout_Layout_layout_r_fix_status_bar, fixStatusBar);
            a.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public int getHeight() {
            return height;
        }

        public int oldHeight = -1;

        public int getOldHeight() {
            if (oldHeight == -1) {
                oldHeight = getHeight();
            }
            return oldHeight;
        }

        public void setOldHeight(int oldHeight) {
            this.oldHeight = oldHeight;
        }
    }
}
