package com.angcyo.uiview.less.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.angcyo.uiview.less.R;
import com.angcyo.uiview.less.base.helper.TitleItemHelper;
import com.angcyo.uiview.less.iview.AffectUI;
import com.angcyo.uiview.less.recycler.RBaseViewHolder;
import com.angcyo.uiview.less.recycler.adapter.RBaseAdapter;
import com.angcyo.uiview.less.recycler.widget.ItemLoadMoreLayout;
import com.angcyo.uiview.less.recycler.widget.ItemShowStateLayout;
import com.angcyo.uiview.less.skin.SkinHelper;
import com.angcyo.uiview.less.widget.ImageTextView;

/**
 * Email:angcyo@126.com
 *
 * @author angcyo
 * @date 2019/02/20
 * Copyright (c) 2019 Shenzhen O&M Cloud Co., Ltd. All rights reserved.
 */
public class BaseUI {
    public static UIFragment uiFragment;
    public static UIAdapterShowStatus uiAdapterShowStatus;
    public static UIAdapterLoadMore uiAdapterLoadMore;

    static {
        uiFragment = new DefaultUIFragment();
        uiAdapterShowStatus = new DefaultUIAdapterShowStatus();
        uiAdapterLoadMore = new DefaultUIAdapterLoadMore();
    }


    /**
     * UIFragment 界面定制
     */
    public interface UIFragment {
        void initBaseTitleLayout(@NonNull BaseTitleFragment titleFragment, @Nullable Bundle arguments);

        View createBackItem(@NonNull BaseTitleFragment titleFragment);

        AffectUI.Builder createAffectUI(@NonNull BaseTitleFragment titleFragment);

        AffectUI.Builder createAffectUI(@NonNull ViewGroup parent, AffectUI.OnAffectListener affectChangeListener);
    }

    /**
     * Adapter情感图界面定制
     */
    public interface UIAdapterShowStatus {
        View createShowState(@NonNull RBaseAdapter baseAdapter, @NonNull Context context, @NonNull ViewGroup parent);

        void onBindShowStateView(@NonNull RBaseAdapter baseAdapter, @NonNull RBaseViewHolder holder, int showState, int position);
    }

    /**
     * 加载更多界面定制
     */
    public interface UIAdapterLoadMore {
        View createLoadMore(@NonNull RBaseAdapter baseAdapter, @NonNull Context context, @NonNull ViewGroup parent);

        void onBindLoadMoreView(@NonNull RBaseAdapter baseAdapter, @NonNull RBaseViewHolder holder, int loadState, int position);
    }

    //默认实现
    public static class DefaultUIFragment implements UIFragment {

        @Override
        public void initBaseTitleLayout(@NonNull BaseTitleFragment titleFragment, @Nullable Bundle arguments) {
            titleFragment.titleControl()
                    .selector(R.id.base_title_bar_layout)
                    .setBackgroundColor(SkinHelper.getSkin().getThemeColor());
        }

        @Override
        public View createBackItem(@NonNull final BaseTitleFragment titleFragment) {
            ImageTextView backItem = TitleItemHelper
                    .createItem(
                            titleFragment.mAttachContext,
                            R.drawable.base_back,
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    titleFragment.onTitleBackClick(v);
                                }
                            }
                    );
            backItem.setId(R.id.base_title_back_view);
            return backItem;
        }

        @Override
        public AffectUI.Builder createAffectUI(@NonNull BaseTitleFragment titleFragment) {
            return createAffectUI(titleFragment.contentWrapperLayout, titleFragment)
                    .setContentAffect(AffectUI.CONTENT_AFFECT_INVISIBLE);
        }

        @Override
        public AffectUI.Builder createAffectUI(@NonNull ViewGroup parent, AffectUI.OnAffectListener affectChangeListener) {
            return AffectUI.build(parent)
                    .register(AffectUI.AFFECT_LOADING, R.layout.base_affect_loading)
                    .register(AffectUI.AFFECT_ERROR, R.layout.base_affect_error)
                    .register(AffectUI.AFFECT_OTHER, R.layout.base_affect_other)
                    .register(AffectUI.AFFECT_EMPTY, R.layout.base_empty_layout)
                    .setContentAffect(AffectUI.CONTENT_AFFECT_INVISIBLE)
                    .setAffectChangeListener(affectChangeListener);
        }
    }

    //默认实现
    public static class DefaultUIAdapterShowStatus implements UIAdapterShowStatus {

        @Override
        public View createShowState(@NonNull RBaseAdapter baseAdapter, @NonNull Context context, @NonNull ViewGroup parent) {
            View itemView = LayoutInflater.from(context)
                    .inflate(R.layout.base_item_show_state_layout, parent, false);
            return itemView;
        }

        @Override
        public void onBindShowStateView(@NonNull RBaseAdapter baseAdapter, @NonNull RBaseViewHolder holder, int showState, int position) {
            if (holder.itemView instanceof ItemShowStateLayout) {

            }
        }
    }

    //默认实现
    public static class DefaultUIAdapterLoadMore implements UIAdapterLoadMore {

        @Override
        public View createLoadMore(@NonNull RBaseAdapter baseAdapter, @NonNull Context context, @NonNull ViewGroup parent) {
            View itemView = LayoutInflater.from(context)
                    .inflate(R.layout.base_item_load_more_layout, parent, false);
            return itemView;
        }

        @Override
        public void onBindLoadMoreView(@NonNull final RBaseAdapter baseAdapter, @NonNull RBaseViewHolder holder, int loadState, int position) {
            //holder.tv(R.id.base_load_tip_view).setText();
            //holder.tv(R.id.base_error_tip_view).setText();
            //holder.tv(R.id.base_no_more_tip_view).setText("");

//        if (TextUtils.equals(RApplication.getApp().getPackageName(), "com.hn.d.valley")) {
//            holder.tv(R.id.base_no_more_tip_view).setTextSize(12f);
//            holder.tv(R.id.base_no_more_tip_view).setText("到底啦");
//        }

            if (holder.itemView instanceof ItemLoadMoreLayout) {
                //加载失败, 点击重试
                holder.click(R.id.base_error_layout, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        baseAdapter.setLoadMoreEnd();
                    }
                });
            }
        }
    }

}
