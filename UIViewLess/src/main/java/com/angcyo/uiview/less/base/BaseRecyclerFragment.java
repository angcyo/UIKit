package com.angcyo.uiview.less.base;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.angcyo.uiview.less.R;
import com.angcyo.uiview.less.iview.AffectUI;
import com.angcyo.uiview.less.recycler.RBaseViewHolder;
import com.angcyo.uiview.less.recycler.RRecyclerView;
import com.angcyo.uiview.less.recycler.adapter.DslAdapter;
import com.angcyo.uiview.less.recycler.adapter.RBaseAdapter;
import com.angcyo.uiview.less.recycler.dslitem.DslAdapterStatusItem;
import com.angcyo.uiview.less.recycler.widget.IShowState;
import com.angcyo.uiview.less.smart.MaterialHeader;
import com.angcyo.uiview.less.widget.RSmartRefreshLayout;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView打底的Fragment
 * <p>
 * Email:angcyo@126.com
 *
 * @author angcyo
 * @date 2018/12/08
 */
public abstract class BaseRecyclerFragment<T> extends BaseLoadFragment
        implements OnRefreshListener, OnLoadMoreListener, RBaseAdapter.OnAdapterLoadMoreListener<T> {

    public static int FIRST_PAGE_INDEX = 1;
    protected RSmartRefreshLayout smartRefreshLayout;
    protected RRecyclerView recyclerView;
    protected RBaseAdapter<T> baseAdapter;
    protected DslAdapter baseDslAdapter;
    /**
     * 当前请求完成的页
     */
    protected int currentPageIndex = FIRST_PAGE_INDEX;
    /**
     * 正在请求的页
     */
    protected int requestPageIndex = FIRST_PAGE_INDEX;

    //<editor-fold desc="初始化部分">

    @Override
    protected int getContentLayoutId() {
        return R.layout.base_recycler_fragment_layout;
    }

    @Override
    protected void onInitBaseView(@NonNull RBaseViewHolder viewHolder, @Nullable Bundle arguments, @Nullable Bundle savedInstanceState) {
        super.onInitBaseView(viewHolder, arguments, savedInstanceState);

        smartRefreshLayout = baseViewHolder.v(R.id.base_refresh_layout);
        recyclerView = baseViewHolder.v(R.id.base_recycler_view);

        initRefreshRecyclerView(smartRefreshLayout, recyclerView);
    }

    public void initRefreshRecyclerView(@Nullable SmartRefreshLayout smartRefreshLayout, @Nullable RRecyclerView recyclerView) {
        initRefreshView(smartRefreshLayout);
        initRecyclerView(recyclerView);
    }

    public void initRefreshView(@Nullable SmartRefreshLayout smartRefreshLayout) {
        if (smartRefreshLayout != null) {
            smartRefreshLayout.setOnRefreshListener(this);
            /*设置加载更多监听之后, 会自动开启加载更多*/
            smartRefreshLayout.setOnLoadMoreListener(this);
            // √ 激活加载更多, 关闭加载更多时, 尽量也关闭不满一页时候开启上拉加载功能
            smartRefreshLayout.setEnableLoadMore(false);
            // √ 是否在列表不满一页时候开启上拉加载功能
            smartRefreshLayout.setEnableLoadMoreWhenContentNotFull(false);
            // √ 是否启用越界拖动（仿苹果效果）1.0.4
            smartRefreshLayout.setEnableOverScrollDrag(false);

            // √ 是否启用下拉刷新功能
            smartRefreshLayout.setEnableRefresh(true);

            // √ 是否启用列表惯性滑动到底部时自动加载更多, 关闭之后, 需要释放手指, 才能加载更多
            smartRefreshLayout.setEnableAutoLoadMore(false);

            //是否启用嵌套滚动, 默认智能控制
            //smartRefreshLayout.setEnableNestedScroll(false);
            // √ 是否启用越界回弹, 关闭后, 快速下滑列表不会触发刷新事件回调
            smartRefreshLayout.setEnableOverScrollBounce(false);

            //是否在刷新完成时滚动列表显示新的内容 1.0.5,
            smartRefreshLayout.setEnableScrollContentWhenRefreshed(true);
            // √ 是否在加载完成时滚动列表显示新的内容, RecyclerView会自动滚动 Footer的高度
            smartRefreshLayout.setEnableScrollContentWhenLoaded(true);
            // √ 是否下拉Header的时候向下平移列表或者内容, 内容是否跟手
            smartRefreshLayout.setEnableHeaderTranslationContent(true);
            // √ 是否上拉Footer的时候向上平移列表或者内容, 内容是否跟手
            smartRefreshLayout.setEnableFooterTranslationContent(true);

            //是否在全部加载结束之后Footer跟随内容1.0.4
            smartRefreshLayout.setEnableFooterFollowWhenLoadFinished(true);

            //android 原生样式
            smartRefreshLayout.setRefreshHeader(new MaterialHeader(mAttachContext)
                    .setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark));
            //关闭内容跟随移动, 更像原生样式
            smartRefreshLayout.setEnableHeaderTranslationContent(false);

            //加载更多使用 adapter 中的回调

            //ios的下拉刷新样式
            //smartRefreshLayout.setRefreshHeader(new ClassicsHeader(mAttachContext));
            //smartRefreshLayout.setRefreshFooter(new ClassicsFooter(mAttachContext));
        }
    }

    public void initRecyclerView(@Nullable RRecyclerView recyclerView) {
        if (recyclerView != null) {
            baseAdapter = onCreateAdapter(new ArrayList<T>());
            if (baseAdapter instanceof DslAdapter) {
                baseDslAdapter = (DslAdapter) baseAdapter;
            }
            recyclerView.setAdapter(baseAdapter);

            if (baseAdapter != null) {
                baseAdapter.setOnLoadMoreListener(this);
            }
            //recyclerView.setBackgroundColor(Color.GREEN);
        }
    }

    //</editor-fold desc="初始化部分">

    //<editor-fold desc="属性和方法">

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
        if (smartRefreshLayout != null) {
            smartRefreshLayout.disableRefreshAffect(disable);
        }
    }

    /**
     * 启用纯下拉刷新效果
     */
    public void enableRefreshAffect() {
        if (smartRefreshLayout != null) {
            smartRefreshLayout.enableRefreshAffect();
        }
    }

    /**
     * 重置刷新控件和Adapter状态
     */
    public void resetUIStatus() {
        resetRefreshStatus();
        resetAdapterStatus();
    }

    public void resetRefreshStatus() {
        if (smartRefreshLayout != null) {
            if (smartRefreshLayout.isEnableRefresh()) {
                smartRefreshLayout.finishRefresh();
            }
            if (smartRefreshLayout.isEnableLoadMore()) {
                smartRefreshLayout.finishLoadMore();
            }
        }
    }

    public void resetAdapterStatus() {
        if (baseAdapter != null) {
            if (baseAdapter.isEnableLoadMore()) {
                baseAdapter.setLoadMoreEnd();
            }
        }
    }

    @Override
    public void switchToError(Object extraObj) {
        if (baseAdapter != null) {
            if (baseAdapter.getAllDataCount() <= 0) {
                super.switchToError(extraObj);
            }
        } else {
            super.switchToError(extraObj);
        }
    }

    //</editor-fold desc="属性和方法">

    //<editor-fold desc="Adapter相关">

    /**
     * 创建适配器
     */
    protected RBaseAdapter<T> onCreateAdapter(@Nullable List<T> datas) {
        return new RBaseAdapter<T>(mAttachContext, datas) {
            @Override
            protected int getItemLayoutId(int viewType) {
                return android.R.layout.simple_list_item_1;
            }

            @Override
            protected void onBindView(@NonNull RBaseViewHolder holder, int position, T bean) {
                if (holder.itemView instanceof TextView && bean instanceof String) {
                    ((TextView) holder.itemView).setText((String) bean);
                }
            }

            @Override
            protected void onLoadMore() {
                super.onLoadMore();
            }

            @Override
            public void onScrollStateChanged(@NonNull RRecyclerView recyclerView, int newState) {
                BaseRecyclerFragment.this.onRecyclerScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RRecyclerView recyclerView, int dx, int dy) {
                BaseRecyclerFragment.this.onRecyclerScrolled(recyclerView, dx, dy);
            }
        };
    }

    /**
     * @see RRecyclerView#mScrollListener
     */
    public void onRecyclerScrolled(@NonNull RRecyclerView recyclerView, int dx, int dy) {

    }

    /**
     * @see RRecyclerView#mScrollListener
     */
    public void onRecyclerScrollStateChanged(@NonNull RRecyclerView recyclerView, int newState) {

    }

    //</editor-fold desc="Adapter相关">

    //<editor-fold desc="事件回调">

    /**
     * 刷新控件, 刷新事件
     */
    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        onBaseRefresh(refreshLayout);
    }

    /**
     * 刷新控件, 加载更多事件
     */
    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        onBaseLoadMore(refreshLayout, null);
    }

    /**
     * 适配器, 加载更多事件
     */
    @Override
    public void onAdapterLodeMore(@NonNull final RBaseAdapter<T> adapter) {
        onBaseLoadMore(null, adapter);
    }

    @Override
    public boolean onAdapterRefresh(@NonNull RBaseAdapter<T> baseAdapter) {
        if (affectUI != null) {
            switchToLoading();
            return true;
        }
        onBaseRefresh(null);
        return false;
    }

    /**
     * 分出来的刷新回调
     */
    public void onBaseRefresh(@Nullable RefreshLayout refreshLayout) {
        currentPageIndex = FIRST_PAGE_INDEX;
        requestPageIndex = FIRST_PAGE_INDEX;

        onBaseLoadData();
    }

    /**
     * 分出来的加载更多回调
     */
    public void onBaseLoadMore(@Nullable RefreshLayout refreshLayout,
                               @Nullable final RBaseAdapter<T> adapter) {
        requestPageIndex = currentPageIndex + 1;

        onBaseLoadData();
    }

    @Override
    public void onUIDelayLoadData() {
        onBaseRefresh(null);
    }

    /**
     * 重写此方法, 加载数据
     */
    public void onBaseLoadData() {
        if (requestPageIndex <= 1) {
            if (smartRefreshLayout != null) {
                smartRefreshLayout.finishRefresh(2_000);
            }
        } else {
            if (smartRefreshLayout != null) {
                smartRefreshLayout.finishLoadMore(2_000);
            }

            if (baseAdapter != null) {
                baseViewHolder.postDelay(2_000, new Runnable() {
                    @Override
                    public void run() {
                        baseAdapter.setNoMore(true);
                    }
                });
            }
        }
    }

    /**
     * 调用此方法, 设置数据
     */
    public void onBaseLoadEnd(@Nullable List<T> datas, int pageSize) {
        onBaseLoadEnd(datas, pageSize, null);
    }

    public void onBaseLoadEnd(@Nullable List<T> datas, int pageSize, @Nullable Throwable error) {
        if (error != null) {
            onBaseLoadError(error);
        } else {
            currentPageIndex = requestPageIndex;
            if (affectUI != null) {
                affectUI.showAffect(AffectUI.AFFECT_CONTENT);
            }
            resetRefreshStatus();
            if (baseAdapter != null) {
                baseAdapter.setShowState(IShowState.NORMAL);
                baseAdapter.loadMoreEnd(datas, requestPageIndex, pageSize);
            }
        }
    }

    /**
     * 加载失败, 默认使用 adapter 的错误情感图提示
     */
    public void onBaseLoadError(@Nullable Throwable error) {
        resetRefreshStatus();
        if (baseAdapter != null) {
            if (affectUI != null) {
                affectUI.showAffect(AffectUI.AFFECT_CONTENT);
            }

            if (baseAdapter instanceof DslAdapter &&
                    ((((DslAdapter) baseAdapter).isAdapterStatus()) ||
                            baseAdapter.getAllDataCount() <= 0)) {
                ((DslAdapter) baseAdapter).setAdapterStatus(DslAdapterStatusItem.ADAPTER_STATUS_ERROR, error);
            } else if (baseAdapter.isStateLayout() || baseAdapter.getAllDataCount() <= 0) {
                baseAdapter.setShowState(IShowState.ERROR);
            } else {
                if (baseAdapter.isEnableLoadMore()) {
                    baseAdapter.setLoadError();
                }
            }
        } else if (affectUI != null) {
            switchToError();
        }
    }

    //</editor-fold desc="事件回调">

    /**
     * 获取缓存池
     */
    @Nullable
    public RecyclerView.RecycledViewPool getRecyclerViewPool() {
        if (mAttachContext instanceof BaseAppCompatActivity) {
            return ((BaseAppCompatActivity) mAttachContext).getRecycledViewPool();
        } else {
            return null;
        }
    }
}
