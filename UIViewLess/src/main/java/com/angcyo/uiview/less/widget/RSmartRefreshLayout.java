package com.angcyo.uiview.less.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import com.angcyo.uiview.less.R;
import com.angcyo.uiview.less.smart.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;

/**
 * Email:angcyo@126.com
 *
 * @author angcyo
 * @date 2018/12/13
 */
public class RSmartRefreshLayout extends SmartRefreshLayout {

    private Drawable rBackgroundDrawable;
    private Drawable rBackgroundDrawableBottom;
    private float percent = 1f;
    private float percentBottom = 0.5f;

    public RSmartRefreshLayout(Context context) {
        this(context, null);
    }

    public RSmartRefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RSmartRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLayout(context);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.RSmartRefreshLayout);
        rBackgroundDrawable = array.getDrawable(R.styleable.RSmartRefreshLayout_r_background);
        percent = array.getFloat(R.styleable.RSmartRefreshLayout_r_background_percent, percent);

        rBackgroundDrawableBottom = array.getDrawable(R.styleable.RSmartRefreshLayout_r_background_bottom);
        percentBottom = array.getFloat(R.styleable.RSmartRefreshLayout_r_background_percent_bottom, percentBottom);

        array.recycle();
    }

    private void initLayout(Context context) {
        // √ 是否在列表不满一页时候开启上拉加载功能
        setEnableLoadMoreWhenContentNotFull(false);
        // √ 是否启用越界拖动（仿苹果效果）1.0.4
        setEnableOverScrollDrag(false);

        // √ 是否启用列表惯性滑动到底部时自动加载更多, 关闭之后, 需要释放手指, 才能加载更多
        setEnableAutoLoadMore(false);

        //是否启用嵌套滚动, 默认智能控制
        //setEnableNestedScroll(false);
        // √ 是否启用越界回弹, 关闭后, 快速下滑列表不会触发刷新事件回调
        setEnableOverScrollBounce(false);

        //是否在刷新完成时滚动列表显示新的内容 1.0.5,
        setEnableScrollContentWhenRefreshed(true);
        // √ 是否在加载完成时滚动列表显示新的内容, RecyclerView会自动滚动 Footer的高度
        setEnableScrollContentWhenLoaded(true);

        //是否在全部加载结束之后Footer跟随内容1.0.4
        setEnableFooterFollowWhenLoadFinished(true);

        // √ 是否下拉Header的时候向下平移列表或者内容, 内容是否跟手
        setEnableHeaderTranslationContent(true);
        // √ 是否上拉Footer的时候向上平移列表或者内容, 内容是否跟手
        setEnableFooterTranslationContent(true);

        /*
         * 重点：设置 srlEnableNestedScrolling 为 false 才可以兼容 BottomSheet
         * */
        //setEnableNestedScroll(false);

        //android 原生样式
        setRefreshHeader(new MaterialHeader(context));
        //关闭内容跟随移动, 更像原生样式
        setEnableHeaderTranslationContent(false);

        //ios的下拉刷新样式
        //setRefreshHeader(new ClassicsHeader(mAttachContext));
        setRefreshFooter(new ClassicsFooter(context));
    }

    public boolean isEnableLoadMore() {
        return mEnableLoadMore;
    }

    public boolean isEnableRefresh() {
        return mEnableRefresh;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (rBackgroundDrawableBottom != null) {
            rBackgroundDrawableBottom.setBounds(0, (int) (getMeasuredHeight() - getMeasuredHeight() * percentBottom),
                    getMeasuredWidth(), getMeasuredHeight());
            rBackgroundDrawableBottom.draw(canvas);
        }
        if (rBackgroundDrawable != null) {
            rBackgroundDrawable.setBounds(0, 0, getMeasuredWidth(), (int) (getMeasuredHeight() * percent));
            rBackgroundDrawable.draw(canvas);
        }
    }

    public void setRBackgroundDrawable(Drawable drawable) {
        this.rBackgroundDrawable = drawable;
        if (drawable != null) {
            setWillNotDraw(false);
        }
        postInvalidate();
    }

    public void setBackgroundPercent(float percent) {
        this.percent = percent;
        postInvalidate();
    }


    public void setRBackgroundDrawableBottom(Drawable drawable) {
        this.rBackgroundDrawableBottom = drawable;
        if (drawable != null) {
            setWillNotDraw(false);
        }
        postInvalidate();
    }

    public void setBackgroundPercentBottom(float percent) {
        this.percentBottom = percent;
        postInvalidate();
    }

    /**
     * 禁掉下拉刷新效果
     */
    public void disableRefreshAffect() {
        disableRefreshAffect(true);
    }

    /**
     * @param disable false 可以开启下拉刷新控件
     */
    public void disableRefreshAffect(boolean disable) {
        setEnableRefresh(!disable);
        setEnableLoadMore(false);
        setEnableOverScrollDrag(false);
        setEnablePureScrollMode(false);
    }

    /**
     * 启用纯下拉刷新效果
     */
    public void enableRefreshAffect() {
        //激活越界滚动
        setEnableOverScrollDrag(true);
        //纯滚动模式, 需要激活越界滚动才有效
        setEnablePureScrollMode(true);
        setEnableLoadMoreWhenContentNotFull(true);
        setEnableLoadMore(true);
        setEnableFooterTranslationContent(true);
        setEnableHeaderTranslationContent(true);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent e) {
        return super.dispatchTouchEvent(e);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!isEnabled()) {
            return false;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }
        return super.onTouchEvent(event);
    }
}
