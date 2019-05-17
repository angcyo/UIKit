package com.angcyo.uiview.less.picture;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.animation.ArgbEvaluatorCompat;
import android.support.transition.*;
import android.support.v4.view.ViewPager;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.angcyo.uiview.less.R;
import com.angcyo.uiview.less.base.BaseFragment;
import com.angcyo.uiview.less.base.helper.FragmentHelper;
import com.angcyo.uiview.less.recycler.RBaseViewHolder;
import com.angcyo.uiview.less.resources.AnimUtil;
import com.angcyo.uiview.less.resources.RAnimtionListener;
import com.angcyo.uiview.less.widget.group.MatrixLayout;
import com.angcyo.uiview.less.widget.pager.RViewPager;
import kotlin.Unit;
import kotlin.jvm.functions.Function3;

import java.util.List;

/**
 * 图片左右翻页预览
 * Email:angcyo@126.com
 *
 * @author angcyo
 * @date 2019/03/12
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */

@Deprecated
public class PhotoPagerFragment extends BaseFragment {

    public static final int ANIM_DURATION = 300;

    protected ViewGroup rootLayout;
    protected RViewPager viewPager;
    protected RPhotoPagerConfig photoPagerConfig;
    protected ImageView previewImageView;

    @Override
    protected int getLayoutId() {
        return R.layout.base_photo_pager_fragment;
    }

    @Override
    protected void initBaseView(@NonNull RBaseViewHolder viewHolder, @Nullable Bundle arguments, @Nullable Bundle savedInstanceState) {
        super.initBaseView(viewHolder, arguments, savedInstanceState);

        rootLayout = viewHolder.v(R.id.base_root_content_layout);
        previewImageView = viewHolder.v(R.id.base_preview_image_view);
        viewPager = viewHolder.v(R.id.base_view_pager);
        viewPager.setPageTransformer(false, null);
        photoPagerConfig.setStartIndex(photoPagerConfig.getCurrentIndex());
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setPagePosition(position);
            }
        });
        PhotoDataSource dataSource = photoPagerConfig.getDataSource();
        viewPager.setAdapter(new PhotoPagerAdapter(dataSource));

        if (dataSource != null) {
            if (dataSource instanceof SinglePhotoDataSource) {
                if (((SinglePhotoDataSource) dataSource).getOnItemPhotoClickListener() == null) {
                    ((SinglePhotoDataSource) dataSource).setOnItemPhotoClickListener(new Function3<View, List, Integer, Unit>() {
                        @Override
                        public Unit invoke(View view, List list, Integer integer) {
                            toHide();
                            return null;
                        }
                    });
                }
                if (((SinglePhotoDataSource) dataSource).getOnMatrixTouchListener() == null) {
                    ((SinglePhotoDataSource) dataSource).setOnMatrixTouchListener(new MatrixLayout.OnMatrixTouchListener() {
                        @Override
                        public boolean checkTouchEvent(@NonNull MatrixLayout matrixLayout) {
                            return true;
                        }

                        @Override
                        public void onMatrixChange(@NonNull MatrixLayout matrixLayout,
                                                   @NonNull Matrix matrix,
                                                   @NonNull RectF fromRect,
                                                   @NonNull RectF toRect) {
                            bgHideDragColor = getColor(toRect.top / fromRect.bottom, bgHideStartColor, bgHideEndColor);
                            getBgAnimView().setBackgroundColor(bgHideDragColor);
                        }

                        @Override
                        public boolean onTouchEnd(@NonNull MatrixLayout matrixLayout, @NonNull Matrix matrix,
                                                  @NonNull RectF fromRect,
                                                  @NonNull RectF toRect) {
                            if (toRect.top / fromRect.bottom > 0.3f) {
                                if (dragRectF == null) {
                                    dragRectF = new RectF();
                                }
                                dragRectF.set(toRect);
                                toHide();
                                return true;
                            }
                            return false;
                        }
                    });
                }
            }
        }

        toShow();
    }

    /**
     * 设置当前到第几页了
     */
    public void setPagePosition(int position) {
        dragRectF = null;
        bgHideDragColor = bgHideStartColor;
        photoPagerConfig.setCurrentIndex(position);
        showPreviewDrawable(position);
    }

    //<editor-fold desc="背景控制">

    protected int bgShowStartColor = Color.TRANSPARENT;
    protected int bgShowEndColor = Color.BLACK;

    protected int bgHideStartColor = Color.BLACK;
    protected int bgHideDragColor = Color.BLACK;
    protected int bgHideEndColor = Color.TRANSPARENT;

    protected RectF dragRectF;

    protected View getBgAnimView() {
        return rootLayout;
    }

    /**
     * 背景显示时的动画
     */
    protected void startBgShowAnim() {
        AnimUtil.startArgb(getBgAnimView(), bgShowStartColor, bgShowEndColor, ANIM_DURATION);
    }

    protected void startBgHideAnim() {
        AnimUtil.startArgb(getBgAnimView(), bgHideDragColor, bgHideEndColor, ANIM_DURATION);
    }

    //</editor-fold desc="背景控制">

    //<editor-fold desc="预览视图控制">

    protected void startShowPreviewAnim() {
        startShowPreviewAnim(getOriginPhotoRect());
    }

    /**
     * 预览图片显示的动画
     */
    protected void startShowPreviewAnim(Rect targetRect) {
        if (targetRect == null) {
            final FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) previewImageView.getLayoutParams();
            params.width = -1;
            params.height = -1;
            params.gravity = Gravity.CENTER;
            previewImageView.setLayoutParams(params);

            AnimationSet animationSet = new AnimationSet(true);
            animationSet.addAnimation(new ScaleAnimation(0.8f, 1f, 0.8f, 1f,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f));
            animationSet.addAnimation(new AlphaAnimation(0.2f, 1f));
            animationSet.setDuration(ANIM_DURATION);
            animationSet.setInterpolator(new FastOutSlowInInterpolator());
            animationSet.setAnimationListener(new RAnimtionListener() {
                @Override
                public void onAnimationEnd(@org.jetbrains.annotations.Nullable Animation animation) {
                    super.onAnimationEnd(animation);
                    onShowAnimEnd();
                }
            });
            viewPager.startAnimation(animationSet);
        } else {
            setPreviewTargetParam(targetRect);

            previewImageView.post(new Runnable() {
                @Override
                public void run() {
                    TransitionSet transitionSet = defaultTransitionSet();
                    transitionSet.addListener(new TransitionListenerAdapter() {
                        @Override
                        public void onTransitionEnd(@NonNull Transition transition) {
                            super.onTransitionEnd(transition);
                            previewImageView.post(new Runnable() {
                                @Override
                                public void run() {
                                    onShowAnimEnd();
                                }
                            });
                        }
                    });
                    TransitionManager.beginDelayedTransition(rootLayout, transitionSet);

                    final FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) previewImageView.getLayoutParams();

                    previewImageView.setTranslationX(0);
                    previewImageView.setTranslationY(0);

                    params.width = -1;
                    params.height = -1;

                    previewImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    previewImageView.setLayoutParams(params);
                }
            });
        }
    }

    protected void startHidePreviewAnim() {
        startHidePreviewAnim(getOriginPhotoRect());
    }

    protected Rect getOriginPhotoRect() {
        Rect rect = null;
        List<Rect> rectList = photoPagerConfig.getOriginPhotoRect();
        if (rectList != null) {
            if (rectList.size() > photoPagerConfig.getCurrentIndex()) {
                rect = rectList.get(photoPagerConfig.getCurrentIndex());
            }
        }
        return rect;
    }

    /**
     * 预览图片隐藏的动画
     */
    protected void startHidePreviewAnim(final Rect targetRect) {
        onHideAnimStart();
        if (targetRect == null || noPreviewDrawable()) {
            float from = 0.9f;
            float to = 0.2f;
            AnimationSet animationSet = new AnimationSet(true);
            animationSet.addAnimation(new ScaleAnimation(from, to, from, to,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f));
            animationSet.addAnimation(new AlphaAnimation(0.8f, 0f));
            animationSet.setDuration(ANIM_DURATION);
            animationSet.setInterpolator(new FastOutSlowInInterpolator());
            viewPager.startAnimation(animationSet);
        } else {
            final TransitionSet transitionSet = defaultTransitionSet();
            transitionSet.addListener(new TransitionListenerAdapter() {
                @Override
                public void onTransitionEnd(@NonNull Transition transition) {
                    super.onTransitionEnd(transition);
                }
            });

            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    TransitionManager.beginDelayedTransition(rootLayout, transitionSet);
                    setPreviewTargetParam(targetRect);
                }
            };

            if (dragRectF != null) {
                setPreviewTargetParam(new Rect(((int) dragRectF.left), ((int) dragRectF.top),
                        (int) dragRectF.right, ((int) dragRectF.bottom)));
                previewImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                rootLayout.post(runnable);
            } else {
                runnable.run();
            }
        }
    }

    /**
     * 目标状态
     */
    protected void setPreviewTargetParam(Rect targetRect) {
        setTargetParam(previewImageView, targetRect);
        previewImageView.setScaleType(photoPagerConfig.getScaleType());
    }

    protected void setTargetParam(View view, Rect targetRect) {
        view.setTranslationX(targetRect.left);
        view.setTranslationY(targetRect.top);

        final ViewGroup.LayoutParams params = view.getLayoutParams();
        params.width = targetRect.width();
        params.height = targetRect.height();

        view.setLayoutParams(params);
    }

    protected TransitionSet defaultTransitionSet() {
        TransitionSet transitionSet = new TransitionSet();
        //transitionSet.addTarget(previewView);
        transitionSet.setDuration(ANIM_DURATION);
        transitionSet.addTransition(new ChangeBounds());
        transitionSet.addTransition(new ChangeTransform());
        transitionSet.addTransition(new ChangeImageTransform());
        transitionSet.setInterpolator(new FastOutSlowInInterpolator());
        return transitionSet;
    }

    protected void onShowAnimEnd() {
        previewImageView.setVisibility(View.GONE);
        viewPager.setVisibility(View.VISIBLE);
        viewPager.setAlpha(1f);
        isAnimStart = false;
    }

    protected void onHideAnimStart() {
        if (noPreviewDrawable()) {

        } else {
            previewImageView.setVisibility(View.VISIBLE);
            viewPager.setVisibility(View.GONE);
        }
    }

    protected boolean noPreviewDrawable() {
        return previewImageView == null || previewImageView.getDrawable() == null;
    }
    //</editor-fold desc="预览视图控制">

    //<editor-fold desc="进出动画控制">

    /**
     * 动画正在执行, 拦截其他操作
     */
    protected boolean isAnimStart = false;

    /**
     * 显示界面
     */
    protected void toShow() {
        if (isAnimStart) {
            return;
        }
        isAnimStart = true;
        viewPager.setVisibility(View.VISIBLE);
        viewPager.setAlpha(0f);
        viewPager.setCurrentItem(photoPagerConfig.getCurrentIndex(), false);

        previewImageView.setScaleType(photoPagerConfig.getScaleType());
        showPreviewDrawable(photoPagerConfig.getCurrentIndex());

        startBgShowAnim();
        startShowPreviewAnim();
    }

    /**
     * 隐藏界面
     */
    protected void toHide() {
        if (isAnimStart) {
            return;
        }
        isAnimStart = true;
        startBgHideAnim();

        startHidePreviewAnim();

        baseViewHolder.postDelay(ANIM_DURATION, new Runnable() {
            @Override
            public void run() {
                FragmentHelper.build(parentFragmentManager())
                        .noAnim()
                        .remove(PhotoPagerFragment.this)
                        .doIt();
            }
        });
    }

    protected void showPreviewDrawable(int index) {

        Drawable drawable = null;
        if (photoPagerConfig != null) {
            List<Drawable> drawables = photoPagerConfig.getPreviewDrawables();
            if (drawables != null) {
                if (drawables.size() > index) {
                    drawable = drawables.get(index);
                }
            }
        }

        previewImageView.setImageDrawable(drawable);
    }

    //</editor-fold desc="进出动画控制">

    private ArgbEvaluatorCompat argbEvaluator;

    private int getColor(float fraction, int startValue, int endValue) {
        if (argbEvaluator == null) {
            argbEvaluator = new ArgbEvaluatorCompat();
        }
        return argbEvaluator.evaluate(fraction, startValue, endValue);
    }

    @Override
    public boolean onBackPressed(@NonNull Activity activity) {
        toHide();
        return false;
        //return super.onBackPressed(activity);
    }
}
