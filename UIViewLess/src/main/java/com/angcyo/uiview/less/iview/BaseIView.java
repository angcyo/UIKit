package com.angcyo.uiview.less.iview;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.*;
import android.view.animation.Animation;
import com.angcyo.uiview.less.R;
import com.angcyo.uiview.less.recycler.RBaseViewHolder;
import com.angcyo.uiview.less.resources.AnimUtil;


/**
 * Email:angcyo@126.com
 *
 * @author angcyo
 * @date 2018/10/16
 */
public abstract class BaseIView implements AffectUI.OnAffectListener {
    public static final int STATUS_INIT = 0x01;
    public static final int STATUS_CREATE = 0x02;
    public static final int STATUS_LOAD = 0x04;
    public static final int STATUS_SHOW = 0x08;
    public static final int STATUS_RESHOW = 0x40;
    public static final int STATUS_HIDE = 0x10;
    public static final int STATUS_UNLOAD = 0x20;

    protected View rootView;
    protected RBaseViewHolder iViewHolder;
    protected Context context;
    protected ViewGroup parent;
    protected int iViewStatus = STATUS_INIT;
    protected AffectUI affectUI;

    /**
     * 从View对象的tag中, 获取BaseIView对象
     */
    public static BaseIView from(@Nullable View view) {
        if (view == null) {
            return null;
        }
        Object tag = view.getTag(R.id.tag_base_iview);
        if (tag instanceof BaseIView) {
            return (BaseIView) tag;
        }
        return null;
    }

    /**
     * @return 根布局id
     */
    protected int getLayoutId() {
        return -1;
    }

    /**
     * @return 用代码的方式, 创建布局
     */
    protected View createRootView() {
        return new View(context);
    }

    public Context getContext() {
        return context;
    }

    public Activity getActivity() {
        if (context instanceof Activity) {
            return (Activity) context;
        }
        return null;
    }

    //<editor-fold desc="情感图方法">

    protected void setViewStatus(int status) {
        iViewStatus = status;
    }

    protected void initAffectUI() {
        initAffectUI(parent);
    }

    protected void initAffectUI(ViewGroup viewGroup) {
        affectUI = AffectUI.build(viewGroup)
                .register(AffectUI.AFFECT_LOADING, R.layout.base_affect_loading)
                .register(AffectUI.AFFECT_ERROR, R.layout.base_affect_error)
                .register(AffectUI.AFFECT_OTHER, R.layout.base_affect_other)
                .setContentAffect(AffectUI.CONTENT_AFFECT_NONE)
                .setAffectChangeListener(this)
                .create();
    }

    @Override
    public void onAffectChangeBefore(@NonNull AffectUI affectUI, int fromAffect, int toAffect) {

    }

    @Override
    public void onAffectChange(@NonNull AffectUI affectUI, int fromAffect, int toAffect, @Nullable View fromView, @Nullable View toView) {

    }

    @Override
    public void onAffectInitLayout(@NonNull AffectUI affectUI, int affect, @NonNull View rootView) {

    }

    //<editor-fold desc="情感图方法">

    /**
     * 1.创建根布局
     *
     * @param context 上下文
     * @param parent  是否需要attach到parent
     * @param state   初始化的状态参数
     */
    public View createView(@NonNull Context context, @Nullable ViewGroup parent, @Nullable Bundle state, boolean attachToRoot) {
        if (isIViewLoad()) {
            return rootView;
        }

        this.context = context;
        this.parent = parent;
        setViewStatus(STATUS_CREATE);

        int layoutId = getLayoutId();
        if (layoutId == -1) {
            rootView = createRootView();
        } else {
            rootView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        }

        iViewHolder = new RBaseViewHolder(rootView);

        //将 BaseIView 对象和 View 关联.
        rootView.setTag(R.id.tag_base_iview, this);

        if (rootView.getLayoutParams() == null) {
            rootView.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        }

        if (attachToRoot && parent != null) {
            parent.addView(rootView);
        }

        initIView(state);

        if (attachToRoot && parent != null) {
            onIViewLoad(state);
            onIViewShow(state);
        }
        return rootView;
    }

    /**
     * 1.1 初始化IView
     *
     * @param state {@link #createView(Context, ViewGroup, Bundle, boolean)}
     */
    protected void initIView(@Nullable Bundle state) {

    }

    /**
     * 2. 生命周期
     */
    public void onIViewLoad(@Nullable Bundle state) {
        setViewStatus(STATUS_LOAD);
    }

    /**
     * 3. 生命周期
     */
    public void onIViewShow(@Nullable Bundle state) {
        setViewStatus(STATUS_SHOW);
    }

    public void onIViewReShow(@Nullable Bundle state) {
        setViewStatus(STATUS_RESHOW);
    }

    /**
     * 4. 生命周期
     */
    public void onIViewHide(@Nullable Bundle state) {
        setViewStatus(STATUS_HIDE);
    }

    /**
     * 5. 生命周期
     */
    public void onIViewUnLoad() {
        setViewStatus(STATUS_UNLOAD);
    }

    /**
     * 界面已经装载了
     */
    public boolean isIViewLoad() {
        return iViewStatus == STATUS_LOAD || isIViewShow();
    }

    public boolean isIViewShow() {
        return iViewStatus == STATUS_SHOW || iViewStatus == STATUS_RESHOW;
    }

    public void show(@NonNull ViewGroup parent, @Nullable final Bundle state, @Nullable Animation animation, final @Nullable Runnable endAction) {
        final Runnable endRunnable = new Runnable() {
            @Override
            public void run() {
                if (isIViewShow()) {
                    onIViewReShow(state);
                } else {
                    onIViewShow(state);
                }
                if (endAction != null) {
                    endAction.run();
                }
            }
        };

        if (iViewStatus == STATUS_INIT || rootView == null) {
            //throw new IllegalArgumentException("请调用先createView().");
            createView(parent.getContext(), parent, state, false);
        }
        if (rootView.getParent() != null) {
            Log.d("BaseIView", "已经在布局中.");
            endRunnable.run();
            return;
        }

        if (rootView.getLayoutParams() == null) {
            rootView.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        }
        this.parent = parent;
        parent.addView(rootView);

        if (!isIViewLoad()) {
            onIViewLoad(state);
        }

        if (animation == null) {
            endRunnable.run();
        } else {
            animation.setAnimationListener(new AnimationEnd() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    endRunnable.run();
                }
            });
            startAnimation(getAnimationView(), animation);
        }
    }

    public void show() {
        show(parent, AnimUtil.translateStartAnimation());
    }

    public void show(@NonNull Activity activity, @Nullable Animation animation) {
        show((ViewGroup) activity.getWindow().findViewById(Window.ID_ANDROID_CONTENT), null, animation, null);
    }

    public void show(@NonNull ViewGroup parent, @Nullable Animation animation) {
        show(parent, null, animation, null);
    }

    public void remove(@Nullable Animation animation, @Nullable final Runnable endAction) {
        if (rootView != null) {
            final ViewParent parent = rootView.getParent();
            if (parent instanceof ViewGroup) {
                final Runnable removeRunnable = new Runnable() {
                    @Override
                    public void run() {
                        onIViewUnLoad();
                        ((ViewGroup) parent).removeView(rootView);
                        if (endAction != null) {
                            endAction.run();
                        }
                    }
                };
                onIViewHide(null);

                if (animation == null) {
                    removeRunnable.run();
                } else {
                    animation.setAnimationListener(new AnimationEnd() {
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            removeRunnable.run();
                        }
                    });
                    startAnimation(getAnimationView(), animation);
                }
            } else {
                if (endAction != null) {
                    endAction.run();
                }
            }
        }
    }

    public void remove() {
        remove(AnimUtil.translateFinishAnimation());
    }

    public void remove(Animation animation) {
        remove(animation, null);
    }

    public void post(Runnable action) {
        if (rootView != null) {
            rootView.post(action);
        }
    }

    public void postDelay(long delay, Runnable action) {
        if (rootView != null) {
            rootView.postDelayed(action, delay);
        }
    }

    protected void startAnimation(@Nullable View view, @Nullable Animation animation) {
        if (view == null || animation == null) {

        } else {
            view.startAnimation(animation);
        }
    }

    /**
     * 返回需要作用动画的View
     */
    protected View getAnimationView() {
        return rootView;
    }

    public static abstract class AnimationEnd implements Animation.AnimationListener {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }
}
