package com.angcyo.uiview.less.recycler;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.text.TextPaint;
import android.view.View;
import com.angcyo.uiview.less.R;
import com.angcyo.uiview.less.RApplication;

/**
 * 自定制超强的分割线
 * Created by angcyo on 星期三 2017-2-15
 */

public class RExItemDecoration extends RecyclerView.ItemDecoration {

    /**
     * 视图靠左边缘
     */
    public static final int EDGE_LEFT = 0b0001;
    /**
     * 视图靠右边缘
     */
    public static final int EDGE_RIGHT = 0b0010;
    /**
     * 视图靠顶边缘
     */
    public static final int EDGE_TOP = 0b0100;
    /**
     * 视图靠底边缘
     */
    public static final int EDGE_BOTTOM = 0b1000;
    public TextPaint mTextPaint;
    ItemDecorationCallback mItemDecorationCallback;

    public RExItemDecoration(ItemDecorationCallback itemDecorationCallback) {
        mItemDecorationCallback = itemDecorationCallback;
        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    }

    public RExItemDecoration() {
        this(null);
    }

    public static RExItemDecoration build(ItemDecorationCallback itemDecorationCallback) {
        return new RExItemDecoration(itemDecorationCallback);
    }

    /**
     * 判断 viewLayoutPosition 是否是一排的结束位置 (垂直水平通用)
     */
    public static boolean isEndOfGrid(int viewLayoutPosition, int spanCount) {
        return viewLayoutPosition % spanCount == spanCount - 1;
    }

    //------------------------------------------公共方法---------------------------------

    /**
     * 判断 viewLayoutPosition 所在的位置,是否是最后一排(垂直水平通用)
     */
    public static boolean isLastOfGrid(int itemCount, int viewLayoutPosition, int spanCount) {
        boolean result = false;
        final double ceil = Math.ceil(((float) itemCount) / spanCount);
        if (viewLayoutPosition >= ceil * spanCount - spanCount) {
            result = true;
        }
        return result;
    }

    public static boolean isLeftEdge(int edge) {
        return (edge & EDGE_LEFT) == EDGE_LEFT;
    }

    public static boolean isRightEdge(int edge) {
        return (edge & EDGE_RIGHT) == EDGE_RIGHT;
    }

    public static boolean isTopEdge(int edge) {
        return (edge & EDGE_TOP) == EDGE_TOP;
    }

    public static boolean isBottomEdge(int edge) {
        return (edge & EDGE_BOTTOM) == EDGE_BOTTOM;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (state.isPreLayout() || state.willRunSimpleAnimations()) {
            return;
        }

        final RecyclerView.LayoutManager manager = parent.getLayoutManager();
        final int firstItem;
        if (manager instanceof StaggeredGridLayoutManager) {
            final StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) manager;
            firstItem = layoutManager.findFirstVisibleItemPositions(null)[0];
            draw(c, parent, firstItem, layoutManager);
        } else if (manager instanceof LinearLayoutManager) {
            //线性布局
            final LinearLayoutManager layoutManager = (LinearLayoutManager) manager;
            firstItem = layoutManager.findFirstVisibleItemPosition();
            draw(c, parent, firstItem, layoutManager);
        }
    }

    //------------------------------------------私有方法---------------------------------

    private void draw(Canvas c, RecyclerView parent, int firstItem, LayoutManager layoutManager) {
        for (int i = 0; i < layoutManager.getChildCount(); i++) {
            final int viewAdapterPosition = firstItem + i;
            final View view = layoutManager.findViewByPosition(viewAdapterPosition);
            if (view != null) {
                mItemDecorationCallback.draw(c, mTextPaint, view,
                        mItemDecorationCallback.getItemOffsets(layoutManager, viewAdapterPosition,
                                getEdge(viewAdapterPosition, layoutManager)),
                        layoutManager.getItemCount(), viewAdapterPosition);
            }
        }
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        //do nothing here
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        final RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();//布局管理器
        final RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) view.getLayoutParams();
        final int viewLayoutPosition = layoutParams.getViewLayoutPosition();//布局时当前View的位置
        final int viewAdapterPosition = layoutParams.getViewAdapterPosition();

        getItemOffsets(outRect, layoutManager, viewAdapterPosition, getEdge(viewAdapterPosition, layoutManager));
    }

    private int getEdge(int position, LayoutManager layoutManager) {
        int edge = 0;
        if (layoutManager instanceof GridLayoutManager) {
            return getEdge(((GridLayoutManager) layoutManager).getSpanCount(), position,
                    layoutManager.getItemCount(), ((GridLayoutManager) layoutManager).getOrientation());
        } else if (layoutManager instanceof LinearLayoutManager) {
            if (((LinearLayoutManager) layoutManager).getOrientation() == LinearLayoutManager.VERTICAL) {
                edge |= EDGE_LEFT;
                edge |= EDGE_RIGHT;
                if (position == 0) {
                    edge |= EDGE_TOP;
                }
                if (position == layoutManager.getItemCount() - 1) {
                    edge |= EDGE_BOTTOM;
                }
            } else {
                edge |= EDGE_TOP;
                edge |= EDGE_BOTTOM;
                if (position == 0) {
                    edge |= EDGE_LEFT;
                }
                if (position == layoutManager.getItemCount() - 1) {
                    edge |= EDGE_RIGHT;
                }
            }
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            //瀑布布局,分割线不好加
        }

        return edge;
    }

    private int getEdge(int spanCount, int position, int itemCount, int orientation) {
        int edge = 0;

        if (orientation == LinearLayoutManager.VERTICAL) {
            if (position % spanCount == 0) {
                edge |= EDGE_LEFT;
            }
            if ((position + 1) % spanCount == 0) {
                edge |= EDGE_RIGHT;
            }
            if (position < spanCount) {
                edge |= EDGE_TOP;
            }
            final double ceil = Math.ceil(itemCount * 1f / spanCount);
            if (position >= ceil * spanCount - spanCount) {
                edge |= EDGE_BOTTOM;
            }
        } else {
            if (position % spanCount == 0) {
                edge |= EDGE_TOP;
            }
            if ((position + 1) % spanCount == 0) {
                edge |= EDGE_BOTTOM;
            }
            if (position < spanCount) {
                edge |= EDGE_LEFT;
            }
            final double ceil = Math.ceil(itemCount * 1f / spanCount);
            if (position >= ceil * spanCount - spanCount) {
                edge |= EDGE_RIGHT;
            }
        }
        return edge;
    }

    private void getItemOffsets(Rect outRect, LayoutManager layoutManager, int viewAdapterPosition, int edge) {
        final Rect offsets = mItemDecorationCallback.getItemOffsets(layoutManager, viewAdapterPosition, edge);
        outRect.set(offsets.left, offsets.top, offsets.right, offsets.bottom);
    }

    public ItemDecorationCallback getItemDecorationCallback() {
        return mItemDecorationCallback;
    }

    public void setItemDecorationCallback(ItemDecorationCallback itemDecorationCallback) {
        mItemDecorationCallback = itemDecorationCallback;
    }

    public SingleItemCallback getSingleItemCallback() {
        return (SingleItemCallback) mItemDecorationCallback;
    }

    public interface ItemDecorationCallback {
        /**
         * 返回需要腾出的空间大小
         */
        Rect getItemOffsets(LayoutManager layoutManager, int position, int edge);

        /**
         * 绘制分割线
         */
        void draw(Canvas canvas, TextPaint paint, View itemView, Rect offsetRect, int itemCount, int position);
    }

    public static abstract class SingleItemCallback implements ItemDecorationCallback {

        Rect mRect;
        int lineColor;
        int offsetLeftColor;

        public SingleItemCallback() {
            mRect = new Rect();
            lineColor = ContextCompat.getColor(RApplication.getApp(), R.color.base_chat_bg_color);
            offsetLeftColor = Color.WHITE;
        }

        @Override
        final public Rect getItemOffsets(LayoutManager layoutManager, int position, int edge) {
            mRect.set(0, 0, 0, 0);
            getItemOffsets2(mRect, position, edge);
            return mRect;
        }

        @Deprecated
        public void getItemOffsets(@NonNull Rect outRect, int position) {

        }

        public void getItemOffsets2(@NonNull Rect outRect, int position, int edge) {
            getItemOffsets(outRect, position);
        }

        @Override
        public void draw(@NonNull Canvas canvas, @NonNull TextPaint paint,
                         @NonNull View itemView, @NonNull Rect offsetRect, int itemCount, int position) {
            drawTopLine(canvas, paint, itemView, offsetRect, itemCount, position);
        }

        /**
         * 简单的在顶部绘制一根线
         */
        public void drawTopLine(@NonNull Canvas canvas, @NonNull TextPaint paint,
                                @NonNull View itemView, @NonNull Rect offsetRect, int itemCount, int position) {
            if (offsetRect.top > 0) {
                paint.setColor(getPaintColor(itemView.getContext(), position));
                offsetRect.set(0, itemView.getTop() - offsetRect.top, itemView.getRight(), itemView.getTop());
                canvas.drawRect(offsetRect, paint);
            }
        }

        /**
         * 简单的在底部绘制一根线
         */
        public void drawBottomLine(@NonNull Canvas canvas, @NonNull TextPaint paint,
                                   @NonNull View itemView, @NonNull Rect offsetRect, int itemCount, int position) {
            if (offsetRect.bottom > 0) {
                paint.setColor(getPaintColor(itemView.getContext(), position));
                offsetRect.set(0, itemView.getBottom(), itemView.getRight(), itemView.getBottom() + offsetRect.bottom);
                canvas.drawRect(offsetRect, paint);
            }
        }

        /**
         * 绘制右边竖线
         */
        public void drawRightLine(@NonNull Canvas canvas, @NonNull TextPaint paint,
                                  @NonNull View itemView, @NonNull Rect offsetRect, int itemCount, int position) {
            paint.setColor(getPaintColor(itemView.getContext(), position));
            offsetRect.set(itemView.getRight(), 0, itemView.getRight() + offsetRect.right, itemView.getBottom());
            canvas.drawRect(offsetRect, paint);
        }

        /**
         * 绘制左边竖线
         */
        public void drawLeftLine(@NonNull Canvas canvas, @NonNull TextPaint paint,
                                 @NonNull View itemView, @NonNull Rect offsetRect, int itemCount, int position) {
            paint.setColor(getPaintColor(itemView.getContext(), position));
            offsetRect.set(itemView.getLeft() - offsetRect.left, 0, itemView.getLeft(), itemView.getBottom());
            canvas.drawRect(offsetRect, paint);
        }

        /**
         * 在底部绘制一根线, 可以控制左右偏移的线
         */
        public void drawBottomMarginLine(@NonNull Canvas canvas, @NonNull TextPaint paint,
                                         @NonNull View itemView, @NonNull Rect offsetRect, int itemCount, int position, int leftMargin, int rightMargin) {
            if (offsetRect.bottom > 0) {
                paint.setColor(getPaintColor(itemView.getContext(), position));
                offsetRect.set(leftMargin, itemView.getBottom(), itemView.getRight() - rightMargin, itemView.getBottom() + offsetRect.bottom);
                canvas.drawRect(offsetRect, paint);
            }
        }

        /**
         * 在顶部左边绘制一个偏移距离的线
         */
        public void drawLeftTopLine(@NonNull Canvas canvas, @NonNull TextPaint paint,
                                    @NonNull View itemView, @NonNull Rect offsetRect, int itemCount, int position) {
            paint.setColor(getOffsetPaintColor(itemView.getContext(), position));
            offsetRect.set(0, itemView.getTop() - offsetRect.top, getLeftOffset(itemView.getContext()), itemView.getTop());
            canvas.drawRect(offsetRect, paint);
        }

        /**
         * 在底部左边绘制一个偏移距离的线
         */
        public void drawLeftBottomLine(@NonNull Canvas canvas, @NonNull TextPaint paint,
                                       @NonNull View itemView, @NonNull Rect offsetRect, int itemCount, int position) {
            if (offsetRect.bottom > 0) {
                paint.setColor(getOffsetPaintColor(itemView.getContext(), position));
                offsetRect.set(0, itemView.getBottom(), getLeftOffset(itemView.getContext()), itemView.getBottom() + offsetRect.bottom);
                canvas.drawRect(offsetRect, paint);
            }
        }

        public void setLineColor(int lineColor) {
            this.lineColor = lineColor;
        }

        public void setOffsetLeftColor(int offsetLeftColor) {
            this.offsetLeftColor = offsetLeftColor;
        }

        @Deprecated
        protected int getPaintColor(Context context) {
            return lineColor;
        }

        @Deprecated
        protected int getOffsetPaintColor(Context context) {
            return offsetLeftColor;
        }

        protected int getPaintColor(Context context, int position) {
            return getPaintColor(context);
        }

        protected int getOffsetPaintColor(Context context, int position) {
            return getOffsetPaintColor(context);
        }

        protected int getLeftOffset(Context context) {
            return context.getResources().getDimensionPixelOffset(R.dimen.base_xhdpi);
        }
    }
}
