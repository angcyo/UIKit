package com.angcyo.uiview.less.widget.group;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import androidx.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.angcyo.lib.L;
import com.angcyo.uiview.less.R;
import com.angcyo.uiview.less.utils.RUtils;
import com.angcyo.uiview.less.widget.GlideImageView;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：第一张图片可以自定义大小, 之后自动根据图片数量, 自动排列图片位置
 * 创建人员：Robi
 * 创建时间：2017/02/10 10:52
 * 修改人员：Robi
 * 修改时间：2017/02/10 10:52
 * 修改备注：
 * Version: 1.0.0
 */
public class RNineImageLayout extends RelativeLayout implements View.OnClickListener {

    /**
     * 需要加载的图片列表
     */
    List<String> mImagesList = new ArrayList<>();
    List<INineItemData> mImagesItems = new ArrayList<>();
    /**
     * 用来显示图片的ImageView
     */
    List<GlideImageView> mImageViews = new ArrayList<>();

    NineImageConfig mNineImageConfig;

    int space = 3;//dp, 间隙

    boolean isContainVideo = false;

    boolean canItemClick = true;
    private boolean drawMask = false;
    private Paint mPaint;
    private float mDensity;

    private int mRPaddingLeft = 0;
    private int mRPaddingRight = 0;
    private int mRPaddingTop = 0;
    private int mRPaddingBottom = 0;

    /**
     * 每一行图片的数量, 如果大于零表示, 强制每一行显示多少个图片, 否则自动计算
     */
    private int lineImageCount = 0;

    public RNineImageLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        space *= getResources().getDisplayMetrics().density;

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RNineImageLayout);
        mRPaddingBottom = typedArray.getDimensionPixelOffset(R.styleable.RNineImageLayout_r_paddingBottom, 0);
        mRPaddingLeft = typedArray.getDimensionPixelOffset(R.styleable.RNineImageLayout_r_paddingLeft, 0);
        mRPaddingRight = typedArray.getDimensionPixelOffset(R.styleable.RNineImageLayout_r_paddingRight, 0);
        mRPaddingTop = typedArray.getDimensionPixelOffset(R.styleable.RNineImageLayout_r_paddingTop, 0);
        lineImageCount = typedArray.getInt(R.styleable.RNineImageLayout_r_line_image_count, lineImageCount);
        typedArray.recycle();
    }

    public RNineImageLayout(Context context) {
        this(context, null);
    }

    private int getSize(int width) {
        return MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
    }

    private int getSize2(int width) {
        return MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (isInEditMode()) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measureHeight = MeasureSpec.getSize(heightMeasureSpec);

        if (mImagesList.isEmpty()) {
            L.w("don't have image list , skip measure.");
            setMeasuredDimension(0, 0);//没有图片, 设置0大小
        } else if (mNineImageConfig == null) {
            L.w("need set nine image config.");
            getChildAt(0).measure(getSize(measureWidth), getSize(measureWidth));
            setMeasuredDimension(measureWidth, measureWidth);
        } else {
            final int size = getImageSize();
            int width = measureWidth;
            int height;//需要计算布局的高度
            if (size == 1 && lineImageCount <= 0) {
                //一张图片
                final int[] widthHeight = mNineImageConfig.getWidthHeight(1);
                if (widthHeight[0] == -1) {
                    if (widthHeight[1] > 0) {
                        getChildAt(0).measure(getSize(width), getSize(widthHeight[1]));
                        setMeasuredDimension(width, widthHeight[1]);
                    } else if (widthHeight[1] == -1) {
                        getChildAt(0).measure(getSize(measureWidth), getSize(measureHeight));
                        setMeasuredDimension(measureWidth, measureHeight);
                    } else {
                        getChildAt(0).measure(getSize(width), getSize(width));
                        setMeasuredDimension(width, width);
                    }
                } else if (widthHeight[0] == 0) {
                    int defaultSize = (int) (getResources().getDisplayMetrics().density * 150);//为0时, 采用默认大小
                    getChildAt(0).measure(getSize(defaultSize), getSize(defaultSize));
                    setMeasuredDimension(defaultSize, defaultSize);
                } else {
                    getChildAt(0).measure(isContainVideo ? getSize(widthHeight[0]) :
                                    getSize(Math.min(widthHeight[0], measureWidth - mRPaddingRight)),
                            getSize(widthHeight[1]));
                    setMeasuredDimension(isContainVideo ? widthHeight[0] : widthHeight[0] + mRPaddingRight, widthHeight[1]);

                    //L.e("width:" + widthHeight[0] + " height:" + widthHeight[1]);
                }
            } else {
                width = measureWidth - mRPaddingLeft - mRPaddingRight;

                final int columns = getColumns(size);
                final int rows = getRows(size);
                //每张图片的宽度
                int itemWidth = (width - space * Math.max(0, columns - 1)) / columns;
                height = rows * itemWidth + Math.max(0, rows - 1) * space;

                //L.e("call: onMeasure -> " + getImageSize() + " itemSize:" + itemWidth);
//                for (View view : mImageViews) {
//                    view.measure(getSize(itemWidth), getSize(itemWidth));
//                }

                for (int i = 0; i < getChildCount(); i++) {
                    getChildAt(i).measure(getSize(itemWidth), getSize(itemWidth));
                }
                setMeasuredDimension(measureWidth, height);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (isInEditMode()) {
            super.onLayout(changed, left, top, right, bottom);
            return;
        }

        if (mImageViews.isEmpty()) {
            L.w("don't have image list , skip layout.");
        } else if (mNineImageConfig == null) {
            L.w("need set nine image config.");
            final View firstView = getChildAt(0);
            firstView.layout(mRPaddingLeft, mRPaddingTop,
                    mRPaddingLeft + getMeasuredWidth(),
                    mRPaddingTop + getMeasuredHeight());

//            if (mNineImageConfig != null) {
//                mNineImageConfig.displayImage(firstView, mImagesList.get(0), getMeasuredWidth(), getMeasuredHeight());
//            }
        } else {
            final int size = getImageSize();
            final int columns = getColumns(size);
            final int rows = getRows(size);

            //L.e("call: onLayout([changed, left, top, right, bottom])-> 行:" + rows + " 列:" + columns);
            if (size == 1 && lineImageCount <= 0) {
                //一张图片
                final View firstView = getChildAt(0);
                firstView.layout(mRPaddingLeft, mRPaddingTop,
                        mRPaddingLeft + firstView.getMeasuredWidth(),
                        mRPaddingTop + firstView.getMeasuredHeight());

//                if (mNineImageConfig != null) {
//                    mNineImageConfig.displayImage(firstView, mImagesList.get(0), getMeasuredWidth(), getMeasuredHeight());
//                }
            } else {
                int l;
                int t;
                for (int i = 0; i < rows; i++) {
                    //行数
                    for (int j = 0; j < columns; j++) {
                        final int index = i * columns + j;
                        if (index >= size) {
                            break;
                        }
                        //列数
                        final View child = getChildAt(index);
                        final int width = child.getMeasuredWidth();
                        final int height = child.getMeasuredHeight();
                        l = mRPaddingLeft + j * width + j * space;
                        t = mRPaddingTop + i * height + i * space;
                        child.layout(l, t, l + width, t + height);

//                        if (mNineImageConfig != null) {
//                            mNineImageConfig.displayImage(child, mImagesList.get(index), child.getMeasuredWidth(), child.getMeasuredHeight());
//                        }
                    }
                }
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //displayImage();
    }

    private void displayImage() {
        //L.e("displayImage() -> " + this.getClass().getSimpleName());
        if (mNineImageConfig != null) {
            boolean delay = false;
            for (int i = 0; i < mImageViews.size(); i++) {
                GlideImageView imageView = mImageViews.get(i);
                imageView.setTag(R.id.tag_url, mImagesList.get(i));

                if (imageView.getMeasuredHeight() == 0 ||
                        imageView.getMeasuredWidth() == 0) {
                    imageView.setImageResource(imageView.getPlaceholderRes());
                    delay = true;
                }
            }

            if (delay) {
                post(new Runnable() {
                    @Override
                    public void run() {
                        displayImage();
                    }
                });
                return;
            }

            for (int i = 0; i < mImageViews.size(); i++) {
                GlideImageView imageView = mImageViews.get(i);
                //cancelRequest(imageView);

                String url = mImagesList.get(i);
                imageView.setTag(R.id.tag_url, url);


                mNineImageConfig.displayImage(imageView, url,
                        imageView.getMeasuredWidth(), imageView.getMeasuredHeight(), getImageSize());

                if (mNineImageConfig instanceof ExNineImageConfig) {
                    ((ExNineImageConfig) mNineImageConfig).displayItem(getChildAt(i), i, mImagesItems.get(i),
                            imageView.getMeasuredWidth(), imageView.getMeasuredHeight(), getImageSize());
                }
            }
        }
    }

    /**
     * 调用此方法, 开始显示图片,
     * 请先调用 {@link #setNineImageConfig(NineImageConfig)}
     */
    public void setImagesList(List<String> imagesList) {
        mImagesList.clear();
        if (imagesList != null) {
            mImagesList.addAll(imagesList);
        }
        notifyDataChanged();
    }

    public void setImagesItems(List<INineItemData> items) {
        mImagesList.clear();
        mImagesItems.clear();
        if (!RUtils.isListEmpty(items)) {
            for (int i = 0; i < items.size(); i++) {
                mImagesItems.add(i, items.get(i));
                mImagesList.add(i, items.get(i).getImageUrl());
            }
        }
        notifyDataChanged();
    }

    public void setImage(String image) {
        List<String> imagesList = new ArrayList<>();
        imagesList.add(image);
        setImagesList(imagesList);
    }

    /**
     * 设置图片加载方式, 和第一张图片的大小
     */
    public void setNineImageConfig(NineImageConfig nineImageConfig) {
        mNineImageConfig = nineImageConfig;
    }

    private void notifyDataChanged() {
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();

        int oldSize = mImageViews.size();
        int newSize = getImageSize();
        if (newSize > oldSize) {
            for (int i = oldSize; i < newSize; i++) {
                createImageView(i);
            }
//            if (measuredHeight != 0 && measuredWidth != 0) {
//                //新创建的ImageView, 还没有测量, 需要测量一次
//                measure(ViewExKt.exactlyMeasure(this, measuredWidth),
//                        ViewExKt.exactlyMeasure(this, measuredHeight));
//            }
        } else if (newSize < oldSize) {
            for (int i = oldSize - 1; i >= newSize; i--) {
                GlideImageView view = mImageViews.remove(i);

                if (getChildAt(i) != view) {
                    removeViewAt(i);
                } else {
                    removeView(view);
                }
            }
        }

        for (int i = 0; i < getChildCount(); i++) {
            View childAt = getChildAt(i);
            childAt.setTag(R.id.tag_position, i);
            if (canItemClick) {
                View imageView = childAt.findViewById(R.id.base_image_view);
                if (imageView == null) {
                    childAt.setOnClickListener(this);
                } else {
                    imageView.setTag(R.id.tag_position, i);
                    imageView.setOnClickListener(this);
                }
            } else {
                View imageView = childAt.findViewById(R.id.base_image_view);
                if (imageView == null) {

                } else {
                    childAt = imageView;
                }

                childAt.setOnClickListener(null);
                childAt.setClickable(false);

            }
        }

        displayImage();

//        if (newSize == oldSize &&
//                measuredHeight != 0 &&
//                measuredWidth != 0) {
//            displayImage();
//        } else {
//            getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                @Override
//                public void onGlobalLayout() {
//                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                    displayImage();
//                }
//            });
//        }

//        post(new Runnable() {
//            @Override
//            public void run() {
//                displayImage();
//            }
//        });

//        if (oldSize == newSize) {
//            displayImage();
//        } else {
//            requestLayout();
//        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        //cancelAllRequest();
    }

    private void cancelAllRequest() {
        for (View view : mImageViews) {
            cancelRequest(view);
        }
    }

    private void cancelRequest(View view) {
        if (view == null) {
            return;
        }
//        Object tag = view.getTag();
//        if (tag instanceof GenericRequest) {
//            ((GenericRequest) tag).clear();
//            L.d("onDetachedFromWindow() ->" + this.getClass().getSimpleName() + "  GenericRequest Clear");
//        }
        Glide.with(this).clear(this);
    }

    public int getImageSize() {
        if (mImagesList == null) {
            return 0;
        }
        return mImagesList.size();
    }

    private void createImageView(int i) {
        GlideImageView imageView;
        if (mNineImageConfig instanceof ExNineImageConfig) {
            imageView = ((ExNineImageConfig) mNineImageConfig).createImageView(this);
        } else {
            imageView = new GlideImageView(getContext(), null);
//            imageView.setBackgroundColor(Color.BLUE);
        }
        imageView.setTag(R.id.tag_position, i);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        mImageViews.add(imageView);

        if (imageView.getParent() == null) {
            addView(imageView, i, new LayoutParams(-2, -2));
        }
    }

    /**
     * 根据图片数量, 返回行数
     */
    private int getRows(int size) {
        if (lineImageCount > 0) {
            return size / (lineImageCount + 1) + 1;
        }

        if (size <= 3) {
            return 1;
        }
        if (size <= 6) {
            return 2;
        }
        if (size <= 9) {
            return 3;
        }
        return 0;
    }

    /**
     * 根据图片数量, 返回列数
     */
    private int getColumns(int size) {
        if (lineImageCount > 0) {
            return lineImageCount;
        }

        if (size == 4) {
            return 2;
        }
        return Math.min(3, size);
    }

    @Override
    public void onClick(View v) {
        if (mNineImageConfig != null) {
            final int position = (int) v.getTag(R.id.tag_position);
            mNineImageConfig.onImageItemClick((GlideImageView) v, mImagesList, mImageViews, position);

            if (mNineImageConfig instanceof ExNineImageConfig) {
                ((ExNineImageConfig) mNineImageConfig).onImageItemClick(position, mImagesItems.get(position));
            }
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (drawMask) {
            int height = getMeasuredHeight();
            LinearGradient linearGradient = new LinearGradient(0, height, 0,
                    height - 40 * mDensity,
                    new int[]{Color.parseColor("#80000000"), Color.TRANSPARENT /*Color.parseColor("#40000000")*/},
                    null, Shader.TileMode.CLAMP);
            mPaint.setShader(linearGradient);
            canvas.drawPaint(mPaint);
        }
    }

    /**
     * 是否绘制蒙层
     */
    public void setDrawMask(boolean drawMask) {
        this.drawMask = drawMask;
        if (drawMask) {
            ensurePaint();
            postInvalidate();
        }
    }

    private void ensurePaint() {
        if (mPaint == null) {
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mPaint.setFlags(Paint.FILTER_BITMAP_FLAG);
            mDensity = getResources().getDisplayMetrics().density;
        }
    }

    public int getRPaddingLeft() {
        return mRPaddingLeft;
    }

    public int getRPaddingRight() {
        return mRPaddingRight;
    }

    public int getRPaddingTop() {
        return mRPaddingTop;
    }

    public int getRPaddingBottom() {
        return mRPaddingBottom;
    }

    public void setRPaddingLeft(int RPaddingLeft) {
        mRPaddingLeft = RPaddingLeft;
    }

    public void setRPaddingRight(int RPaddingRight) {
        mRPaddingRight = RPaddingRight;
    }

    public void setRPaddingTop(int RPaddingTop) {
        mRPaddingTop = RPaddingTop;
    }

    public void setRPaddingBottom(int RPaddingBottom) {
        mRPaddingBottom = RPaddingBottom;
    }

    /**
     * 设置间隙大小
     */
    public void setSpace(int space) {
        this.space = space;
    }

    /**
     * 设置Item是否可点击
     */
    public void setCanItemClick(boolean canItemClick) {
        this.canItemClick = canItemClick;
    }

    /**
     * 包裹的内容是否是视频,如果是视频不受RPadding属性的影响
     */
    public void setContainVideo(boolean containVideo) {
        isContainVideo = containVideo;
    }

    public interface NineImageConfig {
        /**
         * 通过图片数量, 返回对应的宽度和高度, 目前只适用于1张图片的时候, 其他数量的会自动计算
         */
        int[] getWidthHeight(int imageSize);

        /**
         * @param imageSize 总共需要显示多少张图片, 根据图片数量的不同, 决定图片的 缩放类型
         */
        void displayImage(@NonNull GlideImageView imageView, @NonNull String url, int width, int height, int imageSize);

        void onImageItemClick(@NonNull GlideImageView imageView, @NonNull List<String> urlList, @NonNull List<GlideImageView> imageList, int index);
    }

    public interface INineItemData {
        String getImageUrl();
    }

    /**
     * 使用此类时, 请使用 {@link #setImagesItems(List)}
     */
    public static class ExNineImageConfig implements NineImageConfig {

        @Override
        public int[] getWidthHeight(int imageSize) {
            return new int[]{-1, -1};
        }

        public GlideImageView createImageView(@NonNull RNineImageLayout nineImageLayout) {
            return new GlideImageView(nineImageLayout.getContext(), null);
        }

        @Override
        public void displayImage(@NonNull GlideImageView imageView, @NonNull String url, int width, int height, int imageSize) {

        }

        public void displayItem(@NonNull View childAt, int position, @NonNull INineItemData iNineItemData, int measuredWidth, int measuredHeight, int imageSize) {

        }

        public void onImageItemClick(int position, INineItemData iNineItemData) {

        }

        @Override
        public void onImageItemClick(@NonNull GlideImageView imageView, @NonNull List<String> urlList, @NonNull List<GlideImageView> imageList, int index) {

        }
    }
}
