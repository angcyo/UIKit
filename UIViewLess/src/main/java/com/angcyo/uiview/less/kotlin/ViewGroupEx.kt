package com.angcyo.uiview.less.kotlin

import android.app.Activity
import android.graphics.Rect
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnticipateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.EditText
import android.widget.TextView
import com.angcyo.uiview.less.R
import com.angcyo.uiview.less.resources.AnimUtil
import com.angcyo.uiview.less.widget.group.RSoftInputLayout

/**
 * Kotlin ViewGroup的扩展
 * Created by angcyo on 2017-07-26.
 */
/**
 * 计算child在parent中的位置坐标, 请确保child在parent中.
 * */
public fun ViewGroup.getLocationInParent(child: View, location: Rect) {
    var x = 0
    var y = 0

    var view = child
    while (view.parent != this) {
        x += view.left
        y += view.top
        view = view.parent as View
    }

    x += view.left
    y += view.top

    location.set(x, y, x + child.measuredWidth, y + child.measuredHeight)
}

/**返回当软键盘弹出时, 布局向上偏移了多少距离*/
public fun View.getLayoutOffsetTopWidthSoftInput(): Int {
    val rect = Rect()
    var offsetTop = 0

    try {
        val activity = this.context as Activity
        val softInputMode = activity.window.attributes.softInputMode
        if (softInputMode == WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN) {
            val keyboardHeight = RSoftInputLayout.getSoftKeyboardHeight(this)

            /**在ADJUST_PAN模式下, 键盘弹出时, 坐标需要进行偏移*/
            if (keyboardHeight > 0) {
                //return targetView
                val findFocus = this.findFocus()
                if (findFocus is EditText) {
                    findFocus.getWindowVisibleDisplayFrame(rect)
                    offsetTop = findFocus.bottom - rect.bottom
                }
            }
        }

    } catch (e: Exception) {
    }
    return offsetTop
}


/**获取touch坐标对应的RecyclerView, 如果没有则null*/
public fun ViewGroup.getTouchOnRecyclerView(touchRawX: Float, touchRawY: Float): androidx.recyclerview.widget.RecyclerView? {
    return findRecyclerView(touchRawX, touchRawY)
}

public fun ViewGroup.getTouchOnRecyclerView(event: MotionEvent): androidx.recyclerview.widget.RecyclerView? {
    return findRecyclerView(event.rawX, event.rawY)
}

/**
 * 根据touch坐标, 返回touch的View
 */
public fun ViewGroup.findView(
    event: MotionEvent,
    intercept: (View, Rect) -> Boolean = { _, _ -> false },
    jumpTarget: (View, Rect) -> Boolean = { _, _ -> false }
): View? {
    return findView(this, event.rawX, event.rawY, getLayoutOffsetTopWidthSoftInput(), intercept, jumpTarget)
}

public fun ViewGroup.findView(
    touchRawX: Float,
    touchRawY: Float,
    intercept: (View, Rect) -> Boolean = { _, _ -> false },
    jumpTarget: (View, Rect) -> Boolean = { _, _ -> false }
): View? {
    return findView(this, touchRawX, touchRawY, getLayoutOffsetTopWidthSoftInput(), intercept, jumpTarget)
}

public fun ViewGroup.findView(
    targetView: View /*判断需要结束的View*/,
    touchRawX: Float,
    touchRawY: Float,
    offsetTop: Int = 0,
    /*是否需要拦截View, 拦截后 立马返回. 通常用来拦截ViewGroup, 防止枚举目标ViewGroup*/
    intercept: (View, Rect) -> Boolean = { _, _ -> false },
    /*找到目标后, 是否需要跳过目标继续搜索*/
    jumpTarget: (View, Rect) -> Boolean = { _, _ -> false }
): View? {
    /**键盘的高度*/
    var touchView: View? = targetView
    val rect = Rect()

    for (i in childCount - 1 downTo 0) {
        val childAt = getChildAt(i)

        if (childAt.visibility != View.VISIBLE) {
            continue
        }

//        childAt.getWindowVisibleDisplayFrame(rect)
//        L.e("${this}:1 ->$i $rect")
        childAt.getGlobalVisibleRect(rect)
//        L.e("${this}:2 ->$i $rect")
//        L.e("call: ------------------end -> ")
        rect.offset(0, -offsetTop)

        //检查当前view, 是否在 touch坐标中
        fun check(view: View): View? {
            if (view.visibility == View.VISIBLE &&
                view.measuredHeight != 0 &&
                view.measuredWidth != 0 &&
                (view.left != view.right) &&
                (view.top != view.bottom) &&
                rect.contains(touchRawX.toInt(), touchRawY.toInt())
            ) {
                return view
            }
            return null
        }

        val checkView = check(childAt)

        //拦截处理
        if (checkView != null && intercept.invoke(childAt, rect)) {
            touchView = childAt
            break
        }

        if (childAt is ViewGroup && childAt.childCount > 0) {
            val resultView = childAt.findView(targetView, touchRawX, touchRawY, offsetTop, intercept, jumpTarget)
            if (resultView != null && resultView != targetView) {
                if (jumpTarget.invoke(resultView, rect)) {

                } else {
                    touchView = resultView
                    break
                }
            } else {
                if (checkView != null) {
                    if (jumpTarget.invoke(checkView, rect)) {

                    } else {
                        touchView = checkView
                        break
                    }
                }
            }
        } else {
            if (checkView != null) {
                if (jumpTarget.invoke(checkView, rect)) {

                } else {
                    touchView = checkView
                    break
                }
            }
        }
    }
    return touchView
}

public fun ViewGroup.findRecyclerView(
    touchRawX: Float,
    touchRawY: Float
): androidx.recyclerview.widget.RecyclerView? {
    /**键盘的高度*/
    var touchView: androidx.recyclerview.widget.RecyclerView? = null

    val findView = findView(touchRawX, touchRawY,
        { view, _ ->
            view is androidx.recyclerview.widget.RecyclerView
        }, { view, _ ->
            view !is androidx.recyclerview.widget.RecyclerView
        })

    if (findView is androidx.recyclerview.widget.RecyclerView) {
        touchView = findView
    }

    return touchView
}

/**将子View的数量, 重置到指定的数量*/
public fun ViewGroup.resetChildCount(newSize: Int, onAddView: (childIndex: Int) -> View) {
    val oldSize = childCount
    val count = newSize - oldSize
    if (count > 0) {
        //需要补充子View
        for (i in 0 until count) {
            addView(onAddView.invoke(oldSize + i))
        }
    } else if (count < 0) {
        //需要移除子View
        for (i in 0 until count.abs()) {
            removeViewAt(oldSize - 1 - i)
        }
    }
}

public fun ViewGroup.resetChild(
    size: Int,
    childLayoutId: Int,
    init: (view: View, data: String?, index: Int) -> Unit = { _, _, _ -> }
) {
    resetChild(size, null, object : OnAddViewCallback<String>() {
        override fun getLayoutId(): Int = childLayoutId

        override fun onInitView(view: View, data: String?, index: Int) {
            super.onInitView(view, data, index)
            init.invoke(view, data, index)
        }
    })
}

public fun <T> ViewGroup.resetChild(
    list: List<T>?,
    childLayoutId: Int,
    init: (view: View, data: T?, index: Int) -> Unit = { _, _, _ -> }
) {
    resetChild(list?.size ?: 0, list, object : OnAddViewCallback<T>() {
        override fun getLayoutId(): Int = childLayoutId

        override fun onInitView(view: View, data: T?, index: Int) {
            super.onInitView(view, data, index)
            init.invoke(view, data, index)
        }
    })
}

public fun <T> ViewGroup.resetChild(size: Int, datas: List<T>? = null, onAddViewCallback: OnAddViewCallback<T>) {
    addView(size, datas, onAddViewCallback)
}

public fun <T> ViewGroup.addView(size: Int, datas: List<T>? = null, onAddViewCallback: OnAddViewCallback<T>) {
    val layoutId = onAddViewCallback.getLayoutId()

    //如果布局id不一样, 说明child不一样, 需要remove
    if (layoutId > 0) {
        for (index in childCount - 1 downTo 0) {
            val tag = getChildAt(index).getTag(R.id.tag)
            if (tag is Int) {
                if (tag != layoutId) {
                    removeViewAt(index)
                }
            }
        }
    }

    this.resetChildCount(size) {
        val childView = if (layoutId > 0) {
            LayoutInflater.from(context).inflate(layoutId, this, false)
        } else onAddViewCallback.getView()!!

        onAddViewCallback.onCreateView(childView)

        childView.setTag(R.id.tag, layoutId)

        childView
    }

    for (i in 0 until size) {
        onAddViewCallback.onInitView(getChildAt(i), if (datas != null && i < datas.size) datas[i] else null, i)
    }
}


/**动态添加View, 并初始化 (做了性能优化)*/
public fun <T> ViewGroup.addView(datas: List<T>, onAddViewCallback: OnAddViewCallback<T>) {
    addView(datas.size, datas, onAddViewCallback)
}

/**枚举所有child view*/
public fun ViewGroup.childs(map: (index: Int, child: View) -> Unit) {
    for (index in 0 until childCount) {
        val childAt = getChildAt(index)
        map.invoke(index, childAt)
    }
}

public fun ViewGroup.show(@LayoutRes layoutId: Int): View {
    return show(layoutId, null, null)
}

public fun ViewGroup.show2(@LayoutRes layoutId: Int): View {
    return show(layoutId, IViewAnimationType.TRANSLATE_HORIZONTAL)
}

public fun ViewGroup.show(@LayoutRes layoutId: Int, animType: IViewAnimationType): View {
    var enterAnimation: Animation? = null
    var otherExitAnimation: Animation? = null

    when (animType) {
        IViewAnimationType.NONE -> null
        IViewAnimationType.ALPHA -> {
            enterAnimation = AnimUtil.createAlphaEnterAnim(0.8f)
            otherExitAnimation = AnimUtil.createAlphaExitAnim(0.8f)
        }
        IViewAnimationType.TRANSLATE_VERTICAL -> {
            enterAnimation = AnimUtil.translateStartAnimation()
            otherExitAnimation = AnimUtil.createAlphaExitAnim(0.8f)
        }
        IViewAnimationType.SCALE_TO_MAX,
        IViewAnimationType.SCALE_TO_MAX_AND_END,
        IViewAnimationType.SCALE_TO_MAX_OVERSHOOT,
        IViewAnimationType.SCALE_TO_MAX_AND_END_OVERSHOOT,
        IViewAnimationType.SCALE_TO_MAX_AND_TO_MAX_END_OVERSHOOT -> {
            val animation = AnimUtil.scaleMaxAlphaStartAnimation(0.7f)
            if (animType == IViewAnimationType.SCALE_TO_MAX_OVERSHOOT ||
                animType == IViewAnimationType.SCALE_TO_MAX_AND_END_OVERSHOOT ||
                animType == IViewAnimationType.SCALE_TO_MAX_AND_TO_MAX_END_OVERSHOOT
            ) {
                animation.interpolator = OvershootInterpolator()
            }
            enterAnimation = animation

            otherExitAnimation = AnimUtil.createOtherExitNoAnim()
        }
        //默认 IViewAnimationType.TRANSLATE_HORIZONTAL
        else -> {
            enterAnimation = AnimUtil.translateXStartAnimation()
            otherExitAnimation = AnimUtil.translateXOtherFinishAnimation()
        }
    }

    return show(layoutId, enterAnimation, otherExitAnimation)
}

public fun ViewGroup.show(@LayoutRes layoutId: Int, enterAnimation: Animation?, otherExitAnimation: Animation?): View {
    val viewWithTag = findViewWithTag<View>(layoutId)
    if (viewWithTag == null) {

        //之前的view
        val preView = if (childCount > 0) {
            getChildAt(childCount - 1)
        } else {
            null
        }

        LayoutInflater.from(context).inflate(layoutId, this)
        val newView = getChildAt(childCount - 1)
        newView.tag = layoutId

        newView?.let { view ->
            enterAnimation?.let {
                view.startAnimation(it)
            }
        }

        preView?.let { view ->
            otherExitAnimation?.let {
                view.startAnimation(it)
            }
        }

        return newView
    }
    return viewWithTag
}

public fun ViewGroup.hide(@LayoutRes layoutId: Int): View? {
    return hide(layoutId, null, null)
}

public fun ViewGroup.hide2(@LayoutRes layoutId: Int): View? {
    return hide(layoutId, IViewAnimationType.TRANSLATE_HORIZONTAL)
}

public fun ViewGroup.hide(@LayoutRes layoutId: Int, animType: IViewAnimationType): View? {
    var exitAnimation: Animation? = null
    var otherEnterAnimation: Animation? = null

    when (animType) {
        IViewAnimationType.NONE -> null
        IViewAnimationType.ALPHA -> {
            exitAnimation = AnimUtil.createAlphaExitAnim(0.2f)
            otherEnterAnimation = AnimUtil.createAlphaEnterAnim(0.8f)
        }
        IViewAnimationType.TRANSLATE_VERTICAL -> {
            exitAnimation = AnimUtil.translateFinishAnimation()
            otherEnterAnimation = AnimUtil.createAlphaEnterAnim(0.8f)
        }
        IViewAnimationType.SCALE_TO_MAX_AND_END,
        IViewAnimationType.SCALE_TO_MAX_AND_END_OVERSHOOT -> {
            val animation = AnimUtil.scaleMaxAlphaFinishAnimation(0.7f)
            if (animType == IViewAnimationType.SCALE_TO_MAX_AND_END_OVERSHOOT) {
                animation.interpolator = OvershootInterpolator()
            }
            exitAnimation = animation
            otherEnterAnimation = AnimUtil.createOtherEnterNoAnim()
        }
        IViewAnimationType.SCALE_TO_MAX_AND_TO_MAX_END_OVERSHOOT -> {
            val animation2 = AnimUtil.scaleMaxAlphaFinishAnimation(1.2f)
            animation2.interpolator = AnticipateInterpolator()
            exitAnimation = animation2

            otherEnterAnimation = AnimUtil.createOtherEnterNoAnim()
        }
        //IViewAnimationType.SCALE_TO_MAX,
        //IViewAnimationType.SCALE_TO_MAX_OVERSHOOT,
        //IViewAnimationType.TRANSLATE_HORIZONTAL ->
        else -> {
            exitAnimation = AnimUtil.translateXFinishAnimation()
            otherEnterAnimation = AnimUtil.translateXOtherStartAnimation()
        }
    }

    return hide(layoutId, exitAnimation, otherEnterAnimation)
}

public fun ViewGroup.hide(@LayoutRes layoutId: Int, exitAnimation: Animation?, otherEnterAnimation: Animation?): View? {
    val viewWithTag = findViewWithTag<View>(layoutId)
    if (viewWithTag == null || viewWithTag.parent == null) {
    } else {

        //之前的view
        val preView = if (childCount > 1) {
            getChildAt(childCount - 2)
        } else {
            null
        }

        val parent = viewWithTag.parent
        if (parent is ViewGroup) {

            viewWithTag.let { view ->

                exitAnimation?.let {
                    it.setAnimationListener(object : Animation.AnimationListener {
                        override fun onAnimationStart(animation: Animation?) {

                        }

                        override fun onAnimationRepeat(animation: Animation?) {

                        }

                        override fun onAnimationEnd(animation: Animation?) {
                            parent.removeView(viewWithTag)
                        }
                    })
                    view.startAnimation(it)
                }

                if (exitAnimation == null) {
                    parent.removeView(viewWithTag)
                }
            }

            preView?.let { view ->
                otherEnterAnimation?.let {
                    view.startAnimation(it)
                }
            }
        }
    }
    return viewWithTag
}

/**获取ViewGroup中, 所有child的文本信息*/
public fun ViewGroup.getChildTexts(getViewTextCallback: OnGetViewTextCallback = OnGetViewTextCallback()): List<String> {
    val list = mutableListOf<String>()
    for (i in 0 until childCount) {
        getViewTextCallback.getViewText(getChildAt(i))?.let {
            list.add(it)
        }
    }
    return list
}

open class OnGetViewTextCallback {
    open fun getViewText(view: View): String? {
        if (view is TextView) {
            return "${view.text}"
        }
        return null
    }
}

abstract class OnAddViewCallback<T> {
    open fun getLayoutId(): Int = -1

    open fun getView(): View? = null

    /**当首次创建View时, 回调*/
    open fun onCreateView(child: View) {

    }

    open fun onInitView(view: View, data: T?, index: Int) {

    }
}

public fun ViewGroup.inflate(@LayoutRes id: Int, attachToRoot: Boolean = true): View {
    return LayoutInflater.from(context).inflate(id, this, attachToRoot)
}

/**
 * 追加一个布局
 * */
public fun ViewGroup.append(layoutId: Int, init: (child: View) -> Unit) {
    val child = inflate(layoutId, false)
    init(child)
    addView(child)
}