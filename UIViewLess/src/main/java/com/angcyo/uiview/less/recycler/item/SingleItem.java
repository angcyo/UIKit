package com.angcyo.uiview.less.recycler.item;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import androidx.annotation.NonNull;
import android.text.TextPaint;
import android.view.View;
import android.view.ViewGroup;
import com.angcyo.uiview.less.R;
import com.angcyo.uiview.less.RApplication;
import com.angcyo.uiview.less.recycler.RBaseViewHolder;

/**
 * 可以设置top分割线, 和中间分割线样式的{@link Item}
 * Created by angcyo on 2017-03-12.
 */

public class SingleItem implements Item {

    protected final Rect mDrawRect = new Rect();
    /**
     * 左边绘制距离
     */
    protected int leftOffset = 0;
    protected int rightOffset = 0;
    /**
     * 上边留出距离
     */
    protected int topOffset = 0;
    protected int bottomOffset = 1;
    protected int lineColor = Color.parseColor("#EDF1F7");//-1是白色 0是透明
    protected int leftLineColor = Color.WHITE;
    protected Type mType = Type.TOP;

    String mTag = "";

    public SingleItem() {

    }

    public SingleItem(Type type, String tag) {
        this(type);
        mTag = tag;
    }

    public SingleItem(String tag) {
        mTag = tag;
    }

    public SingleItem(Type type) {
        setType(type);
    }

    public SingleItem(Type type, int leftOffset, int rightOffset) {
        this.leftOffset = leftOffset;
        this.rightOffset = rightOffset;
        setType(type);
    }

    public SingleItem(Type type, int lineColor) {
        this(type);
        this.lineColor = lineColor;
    }

    public SingleItem(Context context) {
        this.leftOffset = context.getResources().getDimensionPixelSize(R.dimen.base_xhdpi);
        this.topOffset = context.getResources().getDimensionPixelSize(R.dimen.base_line);
    }

    public SingleItem(int topOffset) {
        this.topOffset = topOffset;
    }

    public SingleItem(int leftOffset, int topOffset) {
        this.leftOffset = leftOffset;
        this.topOffset = topOffset;
    }

    public SingleItem(int leftOffset, int topOffset, int lineColor) {
        this.leftOffset = leftOffset;
        this.topOffset = topOffset;
        this.lineColor = lineColor;
    }

    public SingleItem setLeftOffset(int leftOffset) {
        this.leftOffset = leftOffset;
        return this;
    }

    public SingleItem setRightOffset(int rightOffset) {
        this.rightOffset = rightOffset;
        return this;
    }

    public SingleItem setTopOffset(int topOffset) {
        this.topOffset = topOffset;
        return this;
    }

    public SingleItem setLineColor(int lineColor) {
        this.lineColor = lineColor;
        return this;
    }

    public SingleItem setLeftLineColor(int leftLineColor) {
        this.leftLineColor = leftLineColor;
        return this;
    }

    public SingleItem setBottomOffset(int bottomOffset) {
        this.bottomOffset = bottomOffset;
        return this;
    }

    public void setType(Type type) {
        this.mType = type;
        Resources resources = RApplication.getApp().getResources();
        switch (type) {
            case TOP:
                this.topOffset = resources.getDimensionPixelSize(R.dimen.base_xhdpi);
                break;
            case LINE:
                this.topOffset = resources.getDimensionPixelSize(R.dimen.base_line);
                break;
            case TOP_LINE:
                this.leftOffset = resources.getDimensionPixelSize(R.dimen.base_xhdpi);
                this.topOffset = resources.getDimensionPixelSize(R.dimen.base_line);
                break;
            case BOTTOM:
                this.leftOffset = resources.getDimensionPixelSize(R.dimen.base_xhdpi);
                this.topOffset = 0;
                this.rightOffset = leftOffset;
                break;
            default:
                break;
        }
    }

    @Override
    public void setItemOffsets(Rect rect) {
        if (mType == Type.BOTTOM) {
            rect.bottom = bottomOffset;
        } else {
            rect.top = topOffset;
        }
        //rect.left = leftOffset;
    }

    @Override
    public void setItemOffsets2(Rect rect, int edge) {
        setItemOffsets(rect);
    }

    @Override
    public void draw(Canvas canvas, TextPaint paint, View itemView, Rect offsetRect, int itemCount, int position) {
        if (mType == Type.LINE || mType == Type.TOP) {
            paint.setColor(lineColor);
            mDrawRect.set(itemView.getLeft(), itemView.getTop() - offsetRect.top, itemView.getRight(), itemView.getTop());
            canvas.drawRect(mDrawRect, paint);
        } else if (mType == Type.BOTTOM) {
            paint.setColor(lineColor);
            mDrawRect.set(itemView.getLeft() + leftOffset,
                    itemView.getBottom(),
                    itemView.getRight() - rightOffset,
                    itemView.getBottom() + offsetRect.bottom);
            canvas.drawRect(mDrawRect, paint);
        } else {
            paint.setColor(leftLineColor);
            mDrawRect.set(itemView.getLeft(), itemView.getTop() - offsetRect.top,
                    itemView.getLeft() + leftOffset, itemView.getTop());
            canvas.drawRect(mDrawRect, paint);
        }
    }

    @Override
    public int getItemLayoutId() {
        return R.layout.base_item_info_layout;
    }

    @Override
    public View createItemView(ViewGroup parent, int viewType) {
        return null;
    }

    public SingleItem getThisItem() {
        return this;
    }

    @Override
    public String getTag() {
        return mTag;
    }

    public SingleItem setTag(String mTag) {
        this.mTag = mTag;
        return this;
    }

    @Override
    public void onBindView(@NonNull RBaseViewHolder holder, int posInData, Item itemDataBean) {

    }

    public enum Type {
        /**
         * 距离很大的Line
         */
        TOP,
        /**
         * Line
         */
        LINE,
        /**
         * 左边偏移TOP距离的Line
         */
        TOP_LINE,

        BOTTOM,
    }
}
