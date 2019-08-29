package com.angcyo.uiview.less.base;

import android.animation.Animator;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.EditText;
import com.angcyo.http.HttpSubscriber;
import com.angcyo.http.NonetException;
import com.angcyo.uiview.less.R;
import com.angcyo.uiview.less.RApplication;
import com.angcyo.uiview.less.base.helper.FragmentHelper;
import com.angcyo.uiview.less.recycler.RBaseViewHolder;
import com.angcyo.uiview.less.utils.RUtils;
import com.angcyo.uiview.less.widget.group.RSoftInputLayout;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.CoroutineScopeKt;
import org.jetbrains.annotations.NotNull;
import rx.Subscription;
import rx.observers.SafeSubscriber;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by angcyo on 2018/12/03 23:17
 * <p>
 * 生命周期的封装, 只需要关注 {@link #onFragmentShow(Bundle)} 和 {@link #onFragmentHide()}
 */
public abstract class BaseFragment extends AbsLifeCycleFragment {

    /**
     * onCreateAnimation -> onCreateAnimator
     */

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        return super.onCreateAnimation(transit, enter, nextAnim);
    }

    @Override
    public Animator onCreateAnimator(int transit, boolean enter, int nextAnim) {
        return super.onCreateAnimator(transit, enter, nextAnim);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        enableFragmentSoftInputLayout(false);

        final Bundle arguments = getArguments();

        baseViewHolder.post(new Runnable() {
            @Override
            public void run() {
                onPostCreateView(container, arguments, savedInstanceState);
            }
        });
        return view;
    }

    /**
     * 此方法会在onCreateView之后回调
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * 模拟Activity 的 onPostCreate啊
     */
    protected void onPostCreateView(@Nullable ViewGroup container, @Nullable Bundle arguments, @Nullable Bundle savedInstanceState) {

    }

    @Override
    protected void initBaseView(@NonNull RBaseViewHolder viewHolder, @Nullable Bundle arguments, @Nullable Bundle savedInstanceState) {
        super.initBaseView(viewHolder, arguments, savedInstanceState);
        if (interceptRootTouchEvent()) {
            viewHolder.itemView.setClickable(true);
        }
    }

    /**
     * 拦截RootView的事件, 防止事件穿透到底下的Fragment
     */
    protected boolean interceptRootTouchEvent() {
        return true;
    }

    @NonNull
    public Fragment topFragment() {
        Fragment parentFragment = getParentFragment();
        if (parentFragment == null) {
            return this;
        } else {
            if (parentFragment instanceof BaseFragment) {
                return ((BaseFragment) parentFragment).topFragment();
            } else {
                return parentFragment;
            }
        }
    }

    @Nullable
    public FragmentManager parentFragmentManager() {
        return topFragment().getFragmentManager();
    }

    @Override
    public void onFragmentFirstShow(@Nullable Bundle bundle) {
        super.onFragmentFirstShow(bundle);
        if (baseViewHolder != null) {
            View focus = baseViewHolder.itemView.findFocus();

            if (focus instanceof EditText) {

            } else {
                hideSoftInput();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        onCancelSubscriptions();
        onCancelCoroutine();
    }

    /**
     * 隐藏键盘
     */
    public void hideSoftInput() {
        View fragmentRootView = getView();
        if (fragmentRootView != null) {
            View focus = fragmentRootView.findFocus();
            if (focus instanceof EditText) {
                RSoftInputLayout.hideSoftInput(focus);
            } else if (focus != null) {
                RSoftInputLayout.hideSoftInput(focus);
            } else {
                RSoftInputLayout.hideSoftInput(fragmentRootView);
            }
        }
    }

    /**
     * 显示键盘, 尽量使用 EditText
     */
    public void showSoftInput(View view) {
        if (view != null) {
            RSoftInputLayout.showSoftInput(view);
        }
    }

    public void backFragment(boolean checkBackPress) {
        backFragment(checkBackPress, true);
    }

    public void backFragment(boolean checkBackPress, boolean defaultAnim) {
        FragmentManager fragmentManager = parentFragmentManager();

        FragmentHelper.Builder builder = FragmentHelper.build(fragmentManager)
                .parentLayoutId(topFragment())
                .setCheckBackPress(checkBackPress);

        if (defaultAnim) {
            builder.defaultExitAnim();
        } else {
            builder.noAnim();
        }

        configBackBuilder(builder);

        builder.back(getActivity());
    }

    /**
     * @see #backFragment(boolean, boolean)
     * @see BaseAppCompatActivity#onFragmentBackPressed(int, Fragment)
     */
    protected void configBackBuilder(@NonNull FragmentHelper.Builder builder) {

    }

    //<editor-fold defaultstate="collapsed" desc="网络请求管理">

    protected CompositeSubscription mSubscriptions = new CompositeSubscription();

    public static void addSubscription(CompositeSubscription subscriptions, Subscription subscription,
                                       boolean checkToken, Runnable onCancel) {
        if (subscription == null) {
            return;
        }
        if (subscriptions != null) {
            subscriptions.add(subscription);
        }

        /*没有离线缓存的情况下*/
        if (RApplication.getApp().buildCacheInterceptor() == null
                && RUtils.isNoNet()) {
            //取消网络请求
            if (onCancel != null) {
                onCancel.run();
            }
            try {
                if (subscription instanceof SafeSubscriber) {
                    if (((SafeSubscriber) subscription).getActual() instanceof HttpSubscriber) {
                        ((SafeSubscriber) subscription).getActual().onError(new NonetException());
                        ((SafeSubscriber) subscription).getActual().unsubscribe();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void onCancelSubscriptions() {
        if (mSubscriptions != null) {
            mSubscriptions.clear();
        }
    }

    public void addSubscription(Subscription subscription) {
        addSubscription(subscription, false);
    }

    public void addSubscription(Subscription subscription, boolean checkToken) {
        addSubscription(getCompositeSubscription(), subscription, checkToken, new Runnable() {
            @Override
            public void run() {
                onCancelSubscriptions();
            }
        });
    }

    @NotNull
    public CompositeSubscription getCompositeSubscription() {
        if (mSubscriptions == null || mSubscriptions.isUnsubscribed()) {
            mSubscriptions = new CompositeSubscription();
        }
        return mSubscriptions;
    }

    //</editor-fold">

    //<editor-fold defaultstate="collapsed" desc="协程 相关">

    /**
     * 主线程的协程作用域
     */
    protected CoroutineScope baseMainScope;

    @NonNull
    public CoroutineScope getBaseMainScope() {
        if (baseMainScope == null || !CoroutineScopeKt.isActive(baseMainScope)) {
            baseMainScope = CoroutineScopeKt.MainScope();
        }
        return baseMainScope;
    }

    /**
     * 取消协程作用域中的所有协程
     */
    public void onCancelCoroutine() {
        if (baseMainScope != null) {
            CoroutineScopeKt.cancel(baseMainScope, null);
        }
    }

    //</editor-fold">

    //<editor-fold defaultstate="collapsed" desc="软键盘 相关处理">

    @Nullable
    protected RSoftInputLayout getFragmentSoftInputLayout() {
        RSoftInputLayout result = null;
        if (baseViewHolder != null) {
            result = baseViewHolder.v(R.id.base_soft_input_layout);
        }
        return result;
    }

    /**
     * 激活 or 进制 软键盘的处理
     */
    public void enableFragmentSoftInputLayout(boolean enable) {
        RSoftInputLayout fragmentSoftInputLayout = getFragmentSoftInputLayout();
        if (fragmentSoftInputLayout != null) {
            fragmentSoftInputLayout.setEnableSoftInput(enable);
        }
    }

    @Override
    public void onFragmentShow(@Nullable Bundle bundle) {
        super.onFragmentShow(bundle);

        enableFragmentSoftInputLayout(true);
    }

    @Override
    public void onFragmentHide() {
        super.onFragmentHide();

        enableFragmentSoftInputLayout(false);
    }

    //</editor-fold">
}
