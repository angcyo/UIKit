package com.angcyo.uiview.less.recycler;

/**
 * Created by angcyo on 2016-01-30.
 */

import android.content.Context;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.angcyo.lib.L;
import com.angcyo.uiview.less.RApplication;
import com.angcyo.uiview.less.utils.ScreenUtil;
import com.angcyo.uiview.less.widget.*;
import com.angcyo.uiview.less.widget.group.ItemInfoLayout;
import com.angcyo.uiview.less.widget.group.RFlowLayout;
import com.angcyo.uiview.less.widget.group.RTabLayout;
import com.angcyo.uiview.less.widget.pager.RViewPager;
import com.bumptech.glide.Glide;
import com.orhanobut.hawk.Hawk;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

/**
 * 通用ViewHolder
 */
public class RBaseViewHolder extends RecyclerView.ViewHolder {
    public static int DEFAULT_CLICK_DELAY_TIME = RClickListener.Companion.getDEFAULT_DELAY_CLICK_TIME();
    public static int DEFAULT_INITIAL_CAPACITY = 16;

    private SparseArray<WeakReference<View>> sparseArray;
    private int viewType = -1;

    public RBaseViewHolder(View itemView) {
        this(itemView, -1);
    }

    public RBaseViewHolder(View itemView, int viewType) {
        this(itemView, viewType, DEFAULT_INITIAL_CAPACITY);
    }

    public RBaseViewHolder(View itemView, int viewType, int initialCapacity) {
        super(itemView);
        sparseArray = new SparseArray(initialCapacity);
        this.viewType = viewType;
    }

    /**
     * 填充两个字段相同的数据对象
     */
    public static void fill(@Nullable Object from, @Nullable Object to) {
        fill(from, to, false);
    }

    /**
     * @param ignoreNull 如果是null, 是否需要忽略
     */
    public static void fill(@Nullable Object from, @Nullable Object to, boolean ignoreNull) {
        if (from == null || to == null) {
            return;
        }

        Field[] fromFields = from.getClass().getDeclaredFields();
        Field[] toFields = to.getClass().getDeclaredFields();
        for (Field f : fromFields) {
            String name = f.getName();
            for (Field t : toFields) {
                String tName = t.getName();
                if (name.equalsIgnoreCase(tName)) {
                    try {
                        f.setAccessible(true);
                        t.setAccessible(true);

                        Object fromValue = f.get(from);

                        if (ignoreNull && fromValue == null) {
                        } else {
                            Type fGenericType = f.getGenericType();
                            Type tGenericType = t.getGenericType();

                            if (fGenericType == tGenericType) {
                                t.set(to, fromValue);
                            } else {
                                L.e("操作字段名:" + tName + " 类型不匹配, From:" + fGenericType + " To:" + tGenericType);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
    }

    /**
     * 从object对象中, 获取字段name的get方法
     */
    public static Method getMethod(Object object, String name) {
        Method result = null;
        Method[] methods = object.getClass().getDeclaredMethods();
        for (Method method : methods) {
            method.setAccessible(true);
            String methodName = method.getName();
//            L.e("方法名:" + methodName);
            if (methodName.equalsIgnoreCase("realmGet$" + name)) {
                //优先从realm中获取方法名
//                L.e("方法名匹配到:" + methodName);
                result = method;
                break;
            } else if (methodName.equalsIgnoreCase("get" + name)) {
//                L.e("方法名匹配到:" + methodName);
                result = method;
                break;
            }
        }
        return result;
    }

    /**
     * 清理缓存
     */
    public void clear() {
        sparseArray.clear();
    }

    public int getViewType() {
        return viewType == -1 ? super.getItemViewType() : viewType;
    }

    public <T extends View> T v(@IdRes int resId) {
        WeakReference<View> viewWeakReference = sparseArray.get(resId);
        View view;
        if (viewWeakReference == null) {
            view = itemView.findViewById(resId);
            sparseArray.put(resId, new WeakReference<>(view));
        } else {
            view = viewWeakReference.get();
            if (view == null) {
                view = itemView.findViewById(resId);
                sparseArray.put(resId, new WeakReference<>(view));
            }
        }
        return (T) view;
    }

    public <T extends View> T tag(String tag) {
        View view = itemView.findViewWithTag(tag);
        return (T) view;
    }

    public <T extends View> T v(String idName) {
        return (T) viewByName(idName);
    }

    public <T extends View> T focus(@IdRes int resId) {
        View v = v(resId);
        if (v != null) {
            v.setFocusable(true);
            v.setFocusableInTouchMode(true);
            v.requestFocus();
            return (T) v;
        }
        return null;
    }

    public View view(@IdRes int resId) {
        return v(resId);
    }

    public boolean isVisible(@IdRes int resId) {
        return v(resId).getVisibility() == View.VISIBLE;
    }

    public View visible(@IdRes int resId) {
        return visible(v(resId));
    }

    public RBaseViewHolder visible(@IdRes int resId, boolean visible) {
        View view = v(resId);
        if (visible) {
            visible(view);
        } else {
            gone(view);
        }
        return this;
    }

    public RBaseViewHolder invisible(@IdRes int resId, boolean visible) {
        View view = v(resId);
        if (visible) {
            visible(view);
        } else {
            invisible(view);
        }
        return this;
    }

    public View visible(View view) {
        if (view != null) {
            if (view.getVisibility() != View.VISIBLE) {
                view.setVisibility(View.VISIBLE);
            }
        }
        return view;
    }

    public RBaseViewHolder enable(@IdRes int resId, boolean enable) {
        View view = v(resId);
        enable(view, enable);
        return this;
    }

    private void enable(View view, boolean enable) {
        if (view == null) {
            return;
        }
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                enable(((ViewGroup) view).getChildAt(i), enable);
            }
        } else {
            if (view.isEnabled() != enable) {
                view.setEnabled(enable);
            }
            if (view instanceof EditText) {
                view.clearFocus();
            }
        }
    }

    public View invisible(@IdRes int resId) {
        return invisible(v(resId));
    }

    public View invisible(View view) {
        if (view != null) {
            if (view.getVisibility() != View.INVISIBLE) {
                view.clearAnimation();
                view.setVisibility(View.INVISIBLE);
            }
        }
        return view;
    }

    public void gone(@IdRes int resId) {
        gone(v(resId));
    }

    public RBaseViewHolder gone(View view) {
        if (view != null) {
            if (view.getVisibility() != View.GONE) {
                view.clearAnimation();
                view.setVisibility(View.GONE);
            }
        }
        return this;
    }

    public RRecyclerView reV(@IdRes int resId) {
        return (RRecyclerView) v(resId);
    }

    public RRecyclerView rv(@IdRes int resId) {
        return reV(resId);
    }

    public ViewPager pager(@IdRes int resId) {
        return v(resId);
    }

    public RViewPager rpager(@IdRes int resId) {
        return v(resId);
    }

    public RTabLayout tab(@IdRes int resId) {
        return v(resId);
    }

//    public RLoopRecyclerView loopV(@IdRes int resId) {
//        return (RLoopRecyclerView) v(resId);
//    }

    public RRecyclerView reV(String idName) {
        return (RRecyclerView) viewByName(idName);
    }

    public ItemInfoLayout item(@IdRes int id) {
        return v(id);
    }

    //
    public RFlowLayout flow(@IdRes int id) {
        return v(id);
    }

    public Button button(@IdRes int id) {
        return v(id);
    }

    /**
     * 返回 TextView
     */
    public TextView tV(@IdRes int resId) {
        return (TextView) v(resId);
    }

    public TextView tv(@IdRes int resId) {
        return (TextView) v(resId);
    }

    public <T extends TextView> T tv(@IdRes int resId, final String defaultValue) {
        return tv(resId, defaultValue, null);
    }

    public <T extends TextView> T tv(@IdRes int resId, final String defaultValue, final String hawkKey) {
        TextView textView = tv(resId);

        if (textView != null) {
            if (hawkKey == null) {
                textView.setText(defaultValue);
            } else {
                textView.setText(Hawk.get(hawkKey, defaultValue));
            }
        }

        return (T) textView;
    }

//    public TimeTextView timeV(@IdRes int resId) {
//        return (TimeTextView) v(resId);
//    }

    public RTextView rtv(@IdRes int resId) {
        return (RTextView) v(resId);
    }

    public RExTextView rxtv(@IdRes int resId) {
        return (RExTextView) v(resId);
    }

    public RExTextView extv(@IdRes int resId) {
        return rxtv(resId);
    }

    public AutoEditText auto(@IdRes int resId) {
        return v(resId);
    }

    public AutoEditText auto(@IdRes int resId, List<String> dataList) {
        return auto(resId, dataList, false);
    }

    public AutoEditText auto(@IdRes int resId, List<String> dataList, boolean showOnFocus) {
        return auto(resId, dataList, showOnFocus, 0);
    }

    public AutoEditText auto(@IdRes int resId, List<String> dataList, boolean showOnFocus, long focusDelay) {
        AutoEditText auto = auto(resId);
        if (auto != null) {
            //auto.setAdapter(new RArrayAdapter<>(getContext(), dataList));
            auto.setDataList(dataList, showOnFocus, focusDelay);
        }
        return v(resId);
    }

    public REditText ret(@IdRes int resId) {
        return v(resId);
    }

    public TextView tV(String idName) {
        return (TextView) v(idName);
    }

    public TextView textView(@IdRes int resId) {
        return tV(resId);
    }

    /**
     * 返回 CompoundButton
     */
    public CompoundButton cV(@IdRes int resId) {
        return (CompoundButton) v(resId);
    }

    public CompoundButton cb(@IdRes int resId) {
        return cV(resId);
    }

    public CompoundButton cb(@IdRes int resId, boolean checked, @Nullable CompoundButton.OnCheckedChangeListener listener) {
        CompoundButton compoundButton = cV(resId);
        if (compoundButton != null) {
            compoundButton.setOnCheckedChangeListener(listener);
            compoundButton.setChecked(checked);
        }
        return compoundButton;
    }

    public CompoundButton cb(@IdRes int resId, final boolean defaultValue, @NonNull final String hawkKey,
                             @Nullable final CompoundButton.OnCheckedChangeListener listener) {
        CompoundButton compoundButton = cV(resId);
        if (compoundButton != null) {
            compoundButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Hawk.put(hawkKey, isChecked);

                    if (listener != null) {
                        listener.onCheckedChanged(buttonView, isChecked);
                    }
                }
            });
            compoundButton.setChecked(Hawk.get(hawkKey, defaultValue));
        }
        return compoundButton;
    }

    public CompoundButton cV(String idName) {
        return (CompoundButton) v(idName);
    }

    /**
     * 返回 EditText
     */
    public EditText eV(@IdRes int resId) {
        return (EditText) v(resId);
    }

    public EditText ev(@IdRes int resId) {
        return eV(resId);
    }

    public ExEditText exV(@IdRes int resId) {
        return (ExEditText) v(resId);
    }

    public EditText editView(@IdRes int resId) {
        return eV(resId);
    }

    /**
     * 返回 ImageView
     */
    public ImageView imgV(@IdRes int resId) {
        return (ImageView) v(resId);
    }

    public AppCompatImageView civ(@IdRes int resId) {
        return (AppCompatImageView) v(resId);
    }

    public RImageView rimgV(@IdRes int resId) {
        return (RImageView) v(resId);
    }

    public GlideImageView glideImgV(@IdRes int resId) {
        return (GlideImageView) v(resId);
    }

    public GlideImageView gIV(@IdRes int resId) {
        return gv(resId);
    }

    public GlideImageView giv(@IdRes int resId) {
        return gv(resId);
    }

    public GlideImageView gv(@IdRes int resId) {
        return (GlideImageView) v(resId);
    }

    public ImageView imageView(@IdRes int resId) {
        return imgV(resId);
    }

    /**
     * 返回 ViewGroup
     */
    public ViewGroup groupV(@IdRes int resId) {
        return group(resId);
    }

    public ViewGroup group(@IdRes int resId) {
        return group(v(resId));
    }

    public ViewGroup group(View view) {
        return (ViewGroup) view;
    }

    public ViewGroup viewGroup(@IdRes int resId) {
        return groupV(resId);
    }

    public ViewGroup vg(@IdRes int resId) {
        return groupV(resId);
    }

    public RecyclerView r(@IdRes int resId) {
        return (RecyclerView) v(resId);
    }

    public void click(@IdRes int id, final View.OnClickListener listener) {
        click(id, true, listener);
    }

    public void click(@IdRes int id, boolean isClickable, final View.OnClickListener listener) {
        click(v(id), isClickable, listener);
    }

    public void longClick(@IdRes int id, final View.OnClickListener listener) {
        longClick(v(id), listener);
    }

    public void longClick(View view, final View.OnClickListener listener) {
        if (view != null) {
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    listener.onClick(v);
                    return true;
                }
            });
        }
    }

    public void longItem(final View.OnClickListener listener) {
        longClick(itemView, listener);
    }

    public void click(View view, final View.OnClickListener listener) {
        click(view, DEFAULT_CLICK_DELAY_TIME, listener);
    }

    public void click(View view, boolean isClickable, final View.OnClickListener listener) {
        if (isClickable) {
            click(view, DEFAULT_CLICK_DELAY_TIME, listener);
        } else {
            if (view != null) {
                view.setClickable(false);
            }
        }
    }

    public void clickItem(final View.OnClickListener listener) {
        click(itemView, DEFAULT_CLICK_DELAY_TIME, listener);
    }

    public void click(@IdRes int id, int delay, final View.OnClickListener listener) {
        click(v(id), delay, listener);
    }

    public void click(View view, int delay, final View.OnClickListener listener) {
        if (view != null) {
            if (listener == null) {
                view.setOnClickListener(null);
            } else if (listener instanceof RClickListener) {
                view.setOnClickListener(listener);
            } else {
                view.setOnClickListener(new RClickListener(delay) {
                    @Override
                    public void onRClick(View view) {
                        listener.onClick(view);
                    }
                });
            }
        }
    }

    /**
     * 单击某个View
     */
    public void clickView(View view) {
        if (view != null) {
            view.performClick();
        }
    }

    public void clickView(@IdRes int id) {
        clickView(view(id));
    }

    public void text(@IdRes int id, String text, View.OnClickListener listener) {
        View view = v(id);
        if (view != null) {
            view.setVisibility(View.VISIBLE);

            click(id, listener);

            if (view instanceof TextView) {
                ((TextView) view).setText(text);
            }
        }
    }

    public View viewByName(String name) {
        View view = v(getIdByName(name, "id"));
        return view;
    }

    /**
     * 根据name, 在主题中 寻找资源id
     */
    private int getIdByName(String name, String type) {
        Context context = itemView.getContext();
        return context.getResources().getIdentifier(name, type, context.getPackageName());
    }

    public void fillView(Object bean) {
        fillView(bean, false);
    }

    public void fillView(Object bean, boolean hideForEmpty) {
        fillView(bean, hideForEmpty, false);
    }

    public void fillView(Object bean, boolean hideForEmpty, boolean withGetMethod) {
        OnFillViewCallback callback = new OnFillViewCallback();
        callback.hideForEmpty = hideForEmpty;
        callback.withGetMethod = withGetMethod;
        fillView(null, bean, callback);
    }

    public void fillView(Object bean, OnFillViewCallback callback) {
        fillView(null, bean, callback);
    }

    /**
     * 请勿在bean相当复杂的情况下, 使用此方法, 会消耗很多CPU性能.
     *
     * @param clz 为了效率, 并不会遍历父类的字段, 所以可以指定类
     */
    public void fillView(Class<?> clz, @Nullable Object bean, @Nullable OnFillViewCallback callback) {
        if (callback == null ||
                (clz == null && bean == null)
        ) {
            return;
        }
        Field[] fields;
        if (clz == null) {
            fields = bean.getClass().getDeclaredFields();
        } else {
            fields = clz.getDeclaredFields();
        }
        callback.init(clz, bean);
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                if (callback.isFilterField(field)) {
                    //过滤
                } else {
                    View view = callback.getViewByField(this, field);
                    if (view != null) {
                        CharSequence value = callback.getFieldValue(bean, field);
                        callback.onFillView(view, value);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void post(Runnable runnable) {
        itemView.post(runnable);
    }

    public void postDelay(Runnable runnable, long delayMillis) {
        itemView.postDelayed(runnable, delayMillis);
    }

    public void postDelay(long delayMillis, Runnable runnable) {
        postDelay(runnable, delayMillis);
    }

    public void removeCallbacks(Runnable runnable) {
        itemView.removeCallbacks(runnable);
    }

    public Context getContext() {
        return itemView.getContext();
    }

    /**
     * ItemView是否在屏幕中显示
     */
    public boolean isItemShowInScreen() {
        if (itemView == null) {
            return false;
        }

        if (itemView.getLeft() >= 0 && itemView.getRight() <= ScreenUtil.getScreenWidth()
                && itemView.getTop() >= 0 && itemView.getBottom() <= ScreenUtil.getScreenHeight()) {
            return true;
        }
        return false;
    }

    /**
     * 填充View回调
     */
    public static class OnFillViewCallback {

        /**
         * 如果数据为空时, 是否隐藏View
         */
        public boolean hideForEmpty = true;
        /**
         * 是否通过get方法获取对象字段的值
         */
        public boolean withGetMethod = false;

        /**
         * 强制使用小学字符的view id
         */
        public boolean viewNameLowerCase = false;

        /**
         * viewByName的前缀
         */
        public String viewPrefix = null;

        public void init(Class<?> clz, @Nullable Object bean) {

        }

        /**
         * 填充View
         */
        public void onFillView(@NonNull View view, @Nullable CharSequence value) {
            if (view instanceof TextView) {
                if (TextUtils.isEmpty(value) && hideForEmpty) {
                    view.setVisibility(View.GONE);
                } else {
                    view.setVisibility(View.VISIBLE);
                }
                ((TextView) view).setText(value);
            } else if (view instanceof GlideImageView) {
                ((GlideImageView) view).reset();
                ((GlideImageView) view).setUrl(String.valueOf(value));
            } else if (view instanceof ImageView) {
                Glide.with(RApplication.getApp())
                        .load(value)
                        //.placeholder(R.drawable.default_image)
                        //.error(R.drawable.default_image)
                        //.diskCacheStrategy(DiskCacheStrategy.ALL)
                        //.centerCrop()
                        .into(((ImageView) view));
            }
        }

        /**
         * 通过字段, 获取值
         */
        public CharSequence getFieldValue(@Nullable Object bean, @NonNull Field field) {
            CharSequence value = null;
            if (bean != null) {
                String name = field.getName();
                try {
                    if (withGetMethod) {
                        value = String.valueOf(getMethod(bean, name).invoke(bean));
                    } else {
                        value = field.get(bean).toString();
                    }
                    value = onConvertValueByKey(name, value);
                } catch (Exception e) {
                    //e.printStackTrace();
                    L.w(/*"the clz=" + clz +*/ "the bean=" + bean.getClass().getSimpleName() + " field=" + name + " is null");
                }
            }
            return value;
        }

        /**
         * 重写此方法，可以过滤/重写 key对应的value
         */
        public CharSequence onConvertValueByKey(@NonNull String key, @Nullable CharSequence value) {
            return value;
        }

        /**
         * 通过字段获取View
         */
        public View getViewByField(@NonNull RBaseViewHolder viewHolder, @NonNull Field field) {
            String name = field.getName();
            if (!TextUtils.isEmpty(viewPrefix)) {
                name = viewPrefix + name;
            }
            if (viewNameLowerCase) {
                name = name.toLowerCase();
            }
            View view = viewHolder.viewByName(name);
            if (view == null) {
                view = viewHolder.viewByName(name + "_view");
            }
            return view;
        }

        /**
         * 是否需要跳过字段
         *
         * @param field 当前判断的字段
         */
        public boolean isFilterField(@NonNull Field field) {
            return false;
        }

        public OnFillViewCallback setHideForEmpty(boolean hideForEmpty) {
            this.hideForEmpty = hideForEmpty;
            return this;
        }

        public OnFillViewCallback setWithGetMethod(boolean withGetMethod) {
            this.withGetMethod = withGetMethod;
            return this;
        }

        public OnFillViewCallback setViewPrefix(String viewPrefix) {
            this.viewPrefix = viewPrefix;
            return this;
        }

        public OnFillViewCallback setViewNameLowerCase(boolean viewNameLowerCase) {
            this.viewNameLowerCase = viewNameLowerCase;
            return this;
        }
    }
}
