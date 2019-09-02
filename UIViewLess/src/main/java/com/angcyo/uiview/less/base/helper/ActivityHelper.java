package com.angcyo.uiview.less.base.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.transition.ChangeBounds;
import android.transition.ChangeClipBounds;
import android.transition.ChangeImageTransform;
import android.transition.ChangeTransform;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionSet;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import androidx.annotation.*;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.util.Function;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.angcyo.lib.L;
import com.angcyo.uiview.less.base.BaseAppCompatActivity;
import com.angcyo.uiview.less.kotlin.ExKt;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by angcyo on 2018/12/02 19:21
 */
public class ActivityHelper {

    public static final String KEY_EXTRA = "key_extra";

    /**
     * 设置状态栏背景颜色
     */
    public static void setStatusBarColor(Activity activity, @ColorInt int color) {
        if (activity == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        }
    }

    /**
     * 设置状态栏背景
     */
    public static void setStatusBarDrawable(final Activity activity, final Drawable drawable) {
        if (activity == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            int identifier = activity.getResources().getIdentifier("statusBarBackground", "id", "android");
            View statusBarView = window.findViewById(identifier);
            if (statusBarView != null) {
                ViewCompat.setBackground(statusBarView, drawable);
            } else {
                window.getDecorView().addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                    @Override
                    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                        window.getDecorView().removeOnLayoutChangeListener(this);
                        setStatusBarDrawable(activity, drawable);
                    }
                });
            }
        }
    }

    /**
     * 是否是白色状态栏. 如果是, 那么系统的状态栏字体会是灰色
     */
    public static void lightStatusBar(Activity activity, boolean light) {
        if (activity == null) {
            return;
        }
        //android 6
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int systemUiVisibility = activity.getWindow().getDecorView().getSystemUiVisibility();
            if (light) {
                if (ExKt.have(systemUiVisibility, View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)) {
                    return;
                }
                activity.getWindow()
                        .getDecorView()
                        .setSystemUiVisibility(
                                systemUiVisibility | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                if (!ExKt.have(systemUiVisibility, View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)) {
                    return;
                }
                activity.getWindow()
                        .getDecorView()
                        .setSystemUiVisibility(
                                systemUiVisibility & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }
        if (activity instanceof BaseAppCompatActivity) {
            ((BaseAppCompatActivity) activity).checkLightStatusBar(light);
        }
    }

    /**
     * 激活布局到状态栏中, 只要 WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS 属性, 就可以实现.
     * <p>
     * View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN 属性主要用来做检查判断.
     */
    public static void enableLayoutFullScreen(Activity activity, boolean enable) {
        if (activity == null) {
            return;
        }
        enableLayoutFullScreen(activity.getWindow(), enable);
    }

    public static void enableLayoutFullScreen(Window window, boolean enable) {
        if (window == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //window.setStatusBarColor(Color.TRANSPARENT);

            View decorView = window.getDecorView();
            int systemUiVisibility = decorView.getSystemUiVisibility();
            if (enable) {
                //https://blog.csdn.net/xiaonaihe/article/details/54929504
                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE /*沉浸式, 用户显示状态, 不会清楚原来的状态*/
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            } else {
                systemUiVisibility = ExKt.remove(systemUiVisibility, View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                decorView.setSystemUiVisibility(systemUiVisibility);
            }
        }
    }

    /**
     * @param checkSdk true 表示只在高版本的SDK上使用.
     */
    public static void fullscreen(@NonNull final Activity activity, final boolean enable, boolean checkSdk) {
        //View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
        //View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Window window = activity.getWindow();
                final View decorView = window.getDecorView();
                int uiOptions = decorView.getSystemUiVisibility();
                int enableUiOptions = 0;
                int noenableUiOptions = uiOptions;

                //14
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    enableUiOptions |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
                    enableUiOptions |= View.SYSTEM_UI_FLAG_LOW_PROFILE;

                    noenableUiOptions &= ~View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
                    noenableUiOptions &= ~View.SYSTEM_UI_FLAG_LOW_PROFILE;
                }

                //16
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    enableUiOptions |= View.SYSTEM_UI_FLAG_FULLSCREEN;
                    enableUiOptions |= View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
                    enableUiOptions |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE;

                    noenableUiOptions &= ~View.SYSTEM_UI_FLAG_FULLSCREEN;
                    noenableUiOptions &= ~View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
                    noenableUiOptions &= ~View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                }

                //18
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {


                }

                //19
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    enableUiOptions |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

                    //https://blog.csdn.net/xiaonaihe/article/details/54929504
                    //SYSTEM_UI_FLAG_IMMERSIVE

                    noenableUiOptions &= ~View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
                }


                if (enable) {
                    //window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    decorView.setSystemUiVisibility(enableUiOptions);
                } else {
                    //window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    decorView.setSystemUiVisibility(noenableUiOptions);
                }
            }
        };

        if (checkSdk) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                runnable.run();
            }
        } else {
            runnable.run();
        }
    }

    public static boolean isLayoutFullScreen(Activity activity) {
        if (activity == null) {
            return false;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            return ExKt.have(window.getDecorView().getSystemUiVisibility(), View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        } else {
            return false;
        }
    }

    /**
     * 请在 {@link Activity#setContentView(View)} 之前调用
     * 低版本系统, 可能需要在 {@link Activity#onCreate(Bundle)} 之前调用
     */
    public static void setNoTitleNoActionBar(Activity activity) {
        if (activity == null) {
            return;
        }
        activity.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        if (activity instanceof AppCompatActivity) {
            ActionBar actionBar = ((AppCompatActivity) activity).getSupportActionBar();
            if (actionBar != null) {
                actionBar.hide();
            }
        }
        android.app.ActionBar actionBar = activity.getActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    @Deprecated
    public static <T extends Activity> void startActivity(@NonNull Context context, Class<T> cls, @Nullable Bundle bundle) {
        build(context)
                .setClass(cls)
                .setBundle(bundle)
                .start();
    }

    @Deprecated
    public static void startActivity(@NonNull Context context, @NonNull Intent intent) {
        build(context)
                .setIntent(intent)
                .start();
    }

    /**
     * 恢复已存在的
     */
    public static List<Fragment> restore(@NonNull Context context,
                                         @NonNull FragmentManager fragmentManager,
                                         Class<? extends Fragment>... cls) {
        return FragmentHelper.restore(context, fragmentManager, cls);
    }

    /**
     * 恢复/添加 所有
     */
    public static List<Fragment> restoreShow(@NonNull Context context,
                                             @NonNull FragmentManager fragmentManager,
                                             @IdRes int layoutId,
                                             Class<? extends Fragment>... cls) {
        return FragmentHelper.restoreShow(context, fragmentManager, layoutId, cls);
    }

    /**
     * 重新创建所有
     */
    public static List<Fragment> recreate(@NonNull Context context,
                                          @NonNull FragmentManager fragmentManager,
                                          @IdRes int layoutId,
                                          Class<? extends Fragment>... cls) {
        return FragmentHelper.recreate(context, fragmentManager, layoutId, cls);
    }

    /**
     * 获取启动的时, 设置的参数
     */
    public static Bundle getBundle(@NonNull Intent intent) {
        return intent.getBundleExtra(KEY_EXTRA);
    }

    public static Builder build(@NonNull Context context) {
        return new Builder(context);
    }

    public static class Builder {
        Context context;
        Intent intent;
        Bundle bundle = null;
        int enterAnim = -1;
        int exitAnim = -1;
        int requestCode = -1;
        int resultCode = Activity.RESULT_CANCELED;
        Intent resultData = null;

        String bundleKey = KEY_EXTRA;

        private Bundle transitionOptions = null;

        public Builder(@NonNull Context context) {
            this.context = context;
        }

        /**
         * 用cls, 启动Activity
         */
        public Builder setClass(@NonNull Class<? extends Activity> cls) {
            intent = new Intent(context, cls);
            return this;
        }

        /**
         * 用Intent, 启动Activity
         */
        public Builder setIntent(@NonNull Intent intent) {
            this.intent = intent;
            return this;
        }

        /**
         * 用包名, 启动Activity
         */
        public Builder setPackageName(@NonNull String packageName) {
            intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
            return this;
        }

        private void configIntent() {
            if (context instanceof Activity) {

            } else {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }

            if (bundle != null) {
                intent.putExtra(bundleKey, bundle);
            }
        }

        /**
         * 设置传输的参数
         */
        public Builder setBundle(Bundle bundle) {
            this.bundle = bundle;
            return this;
        }

        public Builder setBundle(@NonNull String key, Bundle bundle) {
            bundleKey = key;
            setBundle(bundle);
            return this;
        }

        /**
         * 扩展设置
         */
        public Builder extra(@NonNull Function<Bundle, Void> function) {
            if (bundle == null) {
                this.bundle = new Bundle();
            }
            function.apply(bundle);
            return this;
        }

        public Builder enterAnim(@AnimRes int enterAnim) {
            this.enterAnim = enterAnim;
            return this;
        }

        public Builder exitAnim(@AnimRes int exitAnim) {
            this.exitAnim = exitAnim;
            return this;
        }

        public Builder defaultExitAnim() {
            this.enterAnim = FragmentHelper.Builder.DEFAULT_NO_ANIM;
            this.exitAnim = FragmentHelper.Builder.DEFAULT_EXIT_ANIM;
            return this;
        }

        public Builder defaultEnterAnim() {
            this.exitAnim = FragmentHelper.Builder.DEFAULT_NO_ANIM;
            this.enterAnim = FragmentHelper.Builder.DEFAULT_ENTER_ANIM;
            return this;
        }

        /**
         * 无动画效果
         */
        public Builder noAnim() {
            enterAnim(0);
            exitAnim(0);
            return this;
        }

        /**
         * 用来启动Activity
         */
        public Intent start() {
            if (intent == null) {
                L.e("必要的参数不合法,请检查参数:" + "\n1->intent:null ×");
            } else {

                configIntent();

                if (context instanceof Activity) {
                    if (sharedElementList != null) {
                        transitionOptions = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context,
                                sharedElementList.toArray(new Pair[sharedElementList.size()])).toBundle();
                    }
                }

                if (requestCode != -1 && context instanceof Activity) {
                    ActivityCompat.startActivityForResult((Activity) context, intent, requestCode, transitionOptions);
                } else {
                    ActivityCompat.startActivity(context, intent, transitionOptions);
                }

                if (context instanceof Activity) {
                    if (enterAnim != -1 || exitAnim != -1) {
                        ((Activity) context).overridePendingTransition(enterAnim, exitAnim);
                    }
                }
            }
            return intent;
        }

        public Intent doIt() {
            return start();
        }

        public Intent doIt(int requestCode) {
            this.requestCode = requestCode;
            return start();
        }

        /**
         * @see Activity#setResult(int, Intent)
         */
        public Builder setResult(int resultCode, Intent data) {
            resultData = data;
            this.resultCode = resultCode;
            return this;
        }

        /**
         * 可以在Fragment中, 关闭Activity , 或者 Remove  Fragment
         *
         * @deprecated 请使用 {@link FragmentHelper.Builder#back(Activity)}
         */
        @Deprecated
        public void back() {

        }

        /**
         * 关闭Activity, 不考虑Fragment
         */
        public void finish() {
            finish(true);
        }

        public void finish(boolean withBackPress) {
            if (context instanceof Activity) {
                ((Activity) context).setResult(resultCode, resultData);

                if (withBackPress) {
                    ((Activity) context).onBackPressed();
                } else {
                    ((Activity) context).finish();
                }

                if (enterAnim != -1 || exitAnim != -1) {
                    ((Activity) context).overridePendingTransition(enterAnim, exitAnim);
                }
            } else {
                L.e("context 必须是 Activity, 才能执行 finish()");
            }
        }

        private List<Pair<View, String>> sharedElementList;

        /**
         * 转场动画支持.
         * 步骤1: 获取共享元素属性值
         * 步骤2: 传递属性
         * 步骤3: 播放动画
         *
         *  示例:
         *  1.启动新的Activity:
         *   ActivityHelper.build(mAttachContext)
         *             .setClass(cls)
         *             .setBundle(BaseCallActivity.DATA, bundle)
         *             .transitionView(userName, BaseCallActivity.USER_NAME)
         *             .transitionView(userAvatar, BaseCallActivity.USER_AVATAR)
         *             .doIt()
         *
         *  2.新的Activity#onCreate
         *   ActivityHelper.transition(this)
         *             .transitionView(avatarImg, USER_AVATAR)
         *             .transitionView(userName, USER_NAME)
         *             .defaultTransition()
         *             .doIt()
         */
        public Builder transitionView(@NonNull View sharedElement, @Nullable String sharedElementName) {
            if (!TextUtils.isEmpty(sharedElementName)) {
                if (sharedElementList == null) {
                    sharedElementList = new ArrayList<>();
                }
                sharedElementList.add(new Pair<View, String>(sharedElement, sharedElementName));
            }
            return this;
        }

        public Builder transitionView(@NonNull View sharedElement) {
            return transitionView(sharedElement, ViewCompat.getTransitionName(sharedElement));
        }
    }

    public static TransitionBuilder transition(Activity activity) {
        return new TransitionBuilder(activity);
    }

    public static class TransitionBuilder {
        Activity activity;

        private TransitionBuilder(Activity activity) {
            this.activity = activity;
        }

        private List<Pair<View, String>> sharedElementList;

        /**
         * 转场动画支持.
         * 步骤1: 获取共享元素属性值
         * 步骤2: 传递属性
         * 步骤3: 播放动画
         */
        public TransitionBuilder transitionView(@NonNull View sharedElement, @Nullable String sharedElementName) {
            if (!TextUtils.isEmpty(sharedElementName)) {
                if (sharedElementList == null) {
                    sharedElementList = new ArrayList<>();
                }
                sharedElementList.add(new Pair<View, String>(sharedElement, sharedElementName));
            }
            return this;
        }

        public TransitionBuilder transitionView(@NonNull View sharedElement) {
            return transitionView(sharedElement, ViewCompat.getTransitionName(sharedElement));
        }

        private boolean isSupport() {
            return activity != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
        }

        public TransitionBuilder defaultTransition() {
            defaultWindowTransition();
            defaultShareElementTransition();
            return this;
        }

        public TransitionBuilder defaultWindowTransition() {
            if (isSupport()) {
                return windowTransition(new Fade(), new Fade());
            } else {
                return this;
            }
        }

        public TransitionBuilder windowTransition(Transition enterTransition, Transition exitTransition) {
            windowEnterTransition(enterTransition);
            windowExitTransition(exitTransition);
            return this;
        }

        public TransitionBuilder windowEnterTransition(Transition enterTransition) {
            if (isSupport()) {
                activity.getWindow().setEnterTransition(enterTransition);
            }
            return this;
        }

        public TransitionBuilder windowExitTransition(Transition exitTransition) {
            if (isSupport()) {
                activity.getWindow().setExitTransition(exitTransition);
            }
            return this;
        }

        public TransitionBuilder defaultShareElementTransition() {
            if (isSupport()) {
                TransitionSet transitionSet = new TransitionSet();
                transitionSet.addTransition(new ChangeBounds());
                transitionSet.addTransition(new ChangeTransform());
                transitionSet.addTransition(new ChangeClipBounds());
                transitionSet.addTransition(new ChangeImageTransform());
                //transitionSet.addTransition(ChangeScroll()) //图片过渡效果, 请勿设置此项
                return shareElementTransition(transitionSet, transitionSet);
            } else {
                return this;
            }
        }

        public TransitionBuilder shareElementTransition(Transition enterTransition, Transition exitTransition) {
            shareElementEnterTransition(enterTransition);
            shareElementExitTransition(exitTransition);
            return this;
        }

        public TransitionBuilder shareElementEnterTransition(Transition enterTransition) {
            if (isSupport()) {
                activity.getWindow().setSharedElementEnterTransition(enterTransition);
            }
            return this;
        }

        public TransitionBuilder shareElementExitTransition(Transition exitTransition) {
            if (isSupport()) {
                activity.getWindow().setSharedElementExitTransition(exitTransition);
            }
            return this;
        }

        public void doIt() {
            if (activity == null || sharedElementList == null) {
                L.e("必要的参数不合法,请检查参数:"
                        + "\n1->activity:" + activity + (activity == null ? " ×" : " √")
                        + "\n2->sharedElementList:" + sharedElementList + (sharedElementList == null ? " ×" : " √")
                );
            } else {
                for (Pair<View, String> pair : sharedElementList) {
                    if (pair.first != null) {
                        ViewCompat.setTransitionName(pair.first, pair.second);
                    }
                }
            }
        }
    }
}
