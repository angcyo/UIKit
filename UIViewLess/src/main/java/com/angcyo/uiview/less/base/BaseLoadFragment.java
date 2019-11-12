package com.angcyo.uiview.less.base;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
import com.angcyo.uiview.less.R;
import com.angcyo.uiview.less.iview.AffectUI;
import com.angcyo.uiview.less.recycler.RBaseViewHolder;

/**
 * Email:angcyo@126.com
 *
 * @author angcyo
 * @date 2019/05/23
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
public class BaseLoadFragment extends BaseTitleFragment {

    public static int DELAY_TIME = 360;

    protected Runnable delayRunnable = new Runnable() {
        @Override
        public void run() {
            if (isAdded() && !isDetached()) {
                onUIDelayLoadData();
            }
        }
    };

    @Override
    protected void onInitBaseView(@NonNull RBaseViewHolder viewHolder, @Nullable Bundle arguments,
                                  @Nullable Bundle savedInstanceState) {
        super.onInitBaseView(viewHolder, arguments, savedInstanceState);
        if (isFirstNeedLoadData()) {
            switchToLoading();
        }
    }

    /**
     * 如果没有数据展示, 才切换到错误情感图
     */
    public void switchToError() {
        switchToError(null);
    }

    public void switchToError(Object extraObj) {
        switchAffectUI(AffectUI.AFFECT_ERROR, extraObj);
    }

    /**
     * 显示内容
     */
    public void switchToContent() {
        switchAffectUI(AffectUI.AFFECT_CONTENT);
    }

    /**
     * 显示空数据
     */
    public void switchToEmpty() {
        switchAffectUI(AffectUI.AFFECT_EMPTY);
    }

    /**
     * 显示加载中
     */
    public void switchToLoading() {
        switchAffectUI(AffectUI.AFFECT_LOADING);
    }

    @Override
    public void onAffectChange(@NonNull AffectUI affectUI, int fromAffect, int toAffect, @Nullable View fromView, @Nullable View toView) {
        super.onAffectChange(affectUI, fromAffect, toAffect, fromView, toView);
        if (toAffect == AffectUI.AFFECT_ERROR) {
            onAffectToError(affectUI, fromAffect, toAffect, fromView, toView);
        } else if (toAffect == AffectUI.AFFECT_LOADING) {
            onAffectToLoading(affectUI, fromAffect, toAffect, fromView, toView);
        }
    }

    /**
     * 情感图 显示异常时
     */
    protected void onAffectToError(@NonNull AffectUI affectUI, int fromAffect, int toAffect, @Nullable View fromView, @Nullable View toView) {
        //显示额外的错误信息
        Object extraObj = affectUI.getExtraObj();
        if (extraObj != null) {
            if (extraObj instanceof String) {
                baseViewHolder.tv(R.id.base_error_tip_view).setText((CharSequence) extraObj);
            } else if (extraObj instanceof Number) {

            } else {
                baseViewHolder.tv(R.id.base_error_tip_view).setText(extraObj.toString());
            }
        }

        baseViewHolder.click(R.id.base_retry_button, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchAffectUI(AffectUI.AFFECT_LOADING);
            }
        });
    }

    /**
     * 情感图 显示加载时
     */
    protected void onAffectToLoading(@NonNull AffectUI affectUI,
                                     int fromAffect, int toAffect,
                                     @Nullable View fromView, @Nullable View toView) {
        boolean needRefresh = false;

        if (firstShowEnd) {
            if (!isFragmentHide()) {
                needRefresh = true;
            }
        } else {
            if (isFirstNeedLoadData()) {
                if (isFragmentInViewPager()) {
                    //不在此处触发
                } else {
                    needRefresh = true;
                }
            }
        }

        if (needRefresh) {
            int delay = firstShowEnd ? 0 : DELAY_TIME;
            //切换到加载情感图, 调用刷新数据接口
            baseViewHolder.postDelay(delay, delayRunnable);
        }
    }

    @Override
    public void onFragmentFirstShow(@Nullable Bundle bundle) {
        super.onFragmentFirstShow(bundle);

        boolean needRefresh = false;

        if (isFragmentInViewPager()) {
            needRefresh = true;
        } else {
            if (affectUI == null || affectUI.getAffectStatus() != AffectUI.AFFECT_LOADING) {
                needRefresh = true;
            }
        }

        if (needRefresh && isFirstNeedLoadData()) {
            baseViewHolder.postDelay(DELAY_TIME, delayRunnable);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (baseViewHolder != null) {
            baseViewHolder.removeCallbacks(delayRunnable);
        }
    }

    /**
     * 界面首次显示, 是否需要触发加载数据
     */
    protected boolean isFirstNeedLoadData() {
        return true;
    }

    /**
     * 重写此方法, 加载界面数据
     */
    public void onUIDelayLoadData() {

    }
}
