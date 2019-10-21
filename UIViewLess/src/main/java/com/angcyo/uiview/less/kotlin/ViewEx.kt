package com.angcyo.uiview.less.kotlin


import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.InputFilter
import android.text.TextUtils
import android.util.LayoutDirection
import android.util.TypedValue
import android.view.*
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.angcyo.uiview.less.draw.RDrawNoRead
import com.angcyo.uiview.less.recycler.RBaseViewHolder
import com.angcyo.uiview.less.recycler.RRecyclerView
import com.angcyo.uiview.less.resources.ResUtil
import com.angcyo.uiview.less.utils.RUtils
import com.angcyo.uiview.less.utils.Reflect
import com.angcyo.uiview.less.utils.ScreenUtil.density
import com.angcyo.uiview.less.widget.*
import com.angcyo.uiview.less.widget.ExEditText.isPhone
import com.angcyo.uiview.less.widget.group.RSoftInputLayout
import com.angcyo.uiview.less.widget.group.SwipeBackLayout.clamp
import com.angcyo.uiview.less.widget.rsen.RGestureDetector
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.textfield.TextInputLayout
import java.io.File
import java.util.*
import kotlin.math.max

/**
 * Kotlin View的扩展
 * Created by angcyo on 2017-06-03.
 */

@Suppress("UNCHECKED_CAST")
public fun <V : View> View.v(id: Int): V? {
    val view: View? = findViewById(id)
    return view as V?
}

public fun <T> T.getDrawable(resId: Int): Drawable? {
    if (resId < 0) {
        return null
    }
    return ContextCompat.getDrawable(app(), resId)
}

public fun <T> T.getColor(resId: Int): Int {
    if (resId == -1 || app() == null) {
        return Color.TRANSPARENT
    }
    return ContextCompat.getColor(app(), resId)
}

public fun <T> T.getDimen(resId: Int): Int {
    return ResUtil.getDimen(resId)
}

public val View.random: Random by lazy {
    Random(System.nanoTime())
}

public val View.scaledDensity: Float
    get() = resources.displayMetrics.scaledDensity

public val View.density: Float
    get() = resources.displayMetrics.density

public val <T> T.dp: Float by lazy {
    Resources.getSystem()?.displayMetrics?.density ?: 0f
}

public val <T> T.dpi: Int by lazy {
    Resources.getSystem()?.displayMetrics?.density?.toInt() ?: 0
}

public val View.viewDrawWith: Int
    get() = measuredWidth - paddingLeft - paddingRight

public val View.viewDrawHeight: Int
    get() = measuredHeight - paddingTop - paddingBottom

public val View.debugPaint: Paint by lazy {
    Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 1 * density()
    }
}

private val View.tempRect: Rect by lazy {
    Rect()
}

public fun View.getGlobalVisibleRect(): Rect {
    //top 永远都不会少于0  bottom 永远都不会大于屏幕高度, 可见的rect就是, 不可见的会被剃掉
    getGlobalVisibleRect(tempRect)
    return tempRect
}

/**返回居中绘制文本的y坐标*/
public fun View.getDrawCenterTextCy(paint: Paint): Float {
    val rawHeight = measuredHeight - paddingTop - paddingBottom
    return paddingTop + rawHeight / 2 + paint.textDrawCy()
}

public fun View.getDrawCenterTextCx(paint: Paint, text: String): Float {
    val rawWidth = measuredWidth - paddingLeft - paddingRight
    return paddingLeft + rawWidth / 2 - paint.textDrawCx(text)
}

public fun Paint.textDrawCx(text: String): Float {
    return measureText(text) / 2
}

/**文本绘制时候 的中点y坐标*/
public fun Paint.textDrawCy(): Float {
    return (descent() - ascent()) / 2 - descent()
}

public fun View.centerX(): Int {
    return (this.x + this.measuredWidth / 2).toInt()
}

public fun View.centerY(): Int {
    return (this.y + this.measuredHeight / 2).toInt()
}

public fun View.getDrawCenterCy(): Float {
    val rawHeight = measuredHeight - paddingTop - paddingBottom
    return (paddingTop + rawHeight / 2).toFloat()
}

public fun View.getDrawCenterCx(): Float {
    val rawWidth = measuredWidth - paddingLeft - paddingRight
    return (paddingLeft + rawWidth / 2).toFloat()
}

/**最小圆的半径*/
public fun View.getDrawCenterR(): Float {
    val rawHeight = measuredHeight - paddingTop - paddingBottom
    val rawWidth = measuredWidth - paddingLeft - paddingRight
    return (Math.min(rawWidth, rawHeight) / 2).toFloat()
}

public fun TextView.getDrawCenterTextCy(): Float {
    val rawHeight = measuredHeight - paddingTop - paddingBottom
    return paddingTop + rawHeight / 2 + (paint.descent() - paint.ascent()) / 2 - paint.descent()
}

/**文本的高度*/
public fun <T> T.textHeight(paint: Paint): Float = paint.textHeight()

public fun TextView.textHeight(): Float = paint.textHeight()

/**文本宽度*/
public fun View.textWidth(paint: Paint?, text: String?): Float = paint?.measureText(text ?: "")
    ?: 0F

public fun String.textWidth(paint: Paint?): Float = paint?.measureText(this) ?: 0F

public fun TextView.textWidth(text: String?): Float = paint.measureText(text ?: "")
public fun TextView.drawPadding(padding: Int) {
    compoundDrawablePadding = padding
}

public fun TextView.drawPadding(padding: Float) {
    drawPadding(padding.toInt())
}

/**设置文本大小 dp单位*/
public fun TextView.setTextSizeDp(sizeDp: Float) {
    setTextSize(TypedValue.COMPLEX_UNIT_PX, sizeDp * RUtils.density())
}

public fun TextView.setRightIco(id: Int) {
    RExTextView.setRightIco(this, id)
}

public fun TextView.setRightIco(drawable: Drawable?) {
    RExTextView.setRightIco(this, drawable)
}

public fun TextView.setLeftIco(id: Int) {
    RExTextView.setLeftIco(this, id)
}

public fun TextView.setLeftIco(drawable: Drawable?) {
    RExTextView.setLeftIco(this, drawable)
}

public fun TextView.setTopIco(id: Int) {
    RExTextView.setTopIco(this, id)
}

public fun TextView.setTopIco(drawable: Drawable?) {
    RExTextView.setTopIco(this, drawable)
}

public fun View.getColor(id: Int): Int = ContextCompat.getColor(context, id)

public fun View.getColorList(id: Int): ColorStateList? =
    ContextCompat.getColorStateList(context, id)

public fun View.getDimensionPixelOffset(id: Int): Int = resources.getDimensionPixelOffset(id)

/**Match_Parent*/
public fun View.exactlyMeasure(size: Int): Int =
    View.MeasureSpec.makeMeasureSpec(size, View.MeasureSpec.EXACTLY)

public fun View.exactlyMeasure(size: Float): Int = this.exactlyMeasure(size.toInt())

/**Wrap_Content*/
public fun View.atmostMeasure(size: Int): Int =
    View.MeasureSpec.makeMeasureSpec(size, View.MeasureSpec.AT_MOST)

public fun View.atmostMeasure(size: Float): Int = this.atmostMeasure(size.toInt())

/**Match_Parent*/
public fun exactly(size: Int): Int =
    View.MeasureSpec.makeMeasureSpec(size, View.MeasureSpec.EXACTLY)

public fun exactly(size: Float): Int = exactly(size.toInt())

/**Wrap_Content*/
public fun atmost(size: Int): Int = View.MeasureSpec.makeMeasureSpec(size, View.MeasureSpec.AT_MOST)

public fun atmost(size: Float): Int = atmost(size.toInt())

public fun View.setOnRClickListener(listener: View.OnClickListener?) {
    if (listener == null) {
        this.isClickable = false
        this.setOnClickListener(null)
    } else {
        if (listener is RClickListener) {
            this.setOnClickListener(listener)
        } else {
            this.setOnClickListener(object : RClickListener(300) {
                override fun onRClick(view: View?) {
                    listener.onClick(view)
                }
            })
        }
    }
}

/**计算宽高比例*/
public fun View.calcWidthHeightRatio(widthHeightRatio: String?): IntArray? {
    if (!TextUtils.isEmpty(widthHeightRatio)) {
        /*固定比例如(1.5w 表示 高度是宽度的1.5倍, 0.5h 表示宽度是高度的0.5倍)*/
        if (widthHeightRatio!!.contains("wh", true)) {
            //特殊情况, size取宽高中最大的值
            val size = measuredWidth.minValue(measuredHeight)
            return intArrayOf(size, size)
        } else if (widthHeightRatio.contains("w", true)) {
            val ratio = widthHeightRatio.replace("w", "", true).toFloatOrNull()
            ratio?.let {
                return intArrayOf(measuredWidth, (it * measuredWidth).toInt())
            }
        } else if (widthHeightRatio.contains("h", true)) {
            val ratio = widthHeightRatio.replace("h", "", true).toFloatOrNull()
            ratio?.let {
                return intArrayOf((it * measuredHeight).toInt(), measuredHeight)
            }
        }
    }
    return null
}

/**用屏幕宽高, 计算View的宽高*/
public fun View.calcLayoutWidthHeight(
    rLayoutWidth: String?,
    rLayoutHeight: String?,
    rLayoutWidthExclude: Int = 0,
    rLayoutHeightExclude: Int = 0
): IntArray {
    return calcLayoutWidthHeight(
        rLayoutWidth,
        rLayoutHeight,
        measuredWidth,
        measuredHeight,
        rLayoutWidthExclude,
        rLayoutHeightExclude
    )
}

public fun View.calcLayoutWidthHeight(
    rLayoutWidth: String?, rLayoutHeight: String?,
    parentWidth: Int, parentHeight: Int,
    rLayoutWidthExclude: Int = 0, rLayoutHeightExclude: Int = 0
): IntArray {
    val size = intArrayOf(-1, -1)
    if (TextUtils.isEmpty(rLayoutWidth) && TextUtils.isEmpty(rLayoutHeight)) {
        return size
    }
    if (!TextUtils.isEmpty(rLayoutWidth)) {
        if (rLayoutWidth!!.contains("sw", true)) {
            val ratio = rLayoutWidth.replace("sw", "", true).toFloatOrNull()
            ratio?.let {
                size[0] = (ratio * (RUtils.getScreenWidth() - rLayoutWidthExclude)).toInt()
            }
        } else if (rLayoutWidth!!.contains("pw", true)) {
            val ratio = rLayoutWidth.replace("pw", "", true).toFloatOrNull()
            ratio?.let {
                size[0] = (ratio * (parentWidth - rLayoutWidthExclude)).toInt()
            }
        }
    }
    if (!TextUtils.isEmpty(rLayoutHeight)) {
        if (rLayoutHeight!!.contains("sh", true)) {
            val ratio = rLayoutHeight.replace("sh", "", true).toFloatOrNull()
            ratio?.let {
                size[1] = (ratio * (RUtils.getScreenHeight() - rLayoutHeightExclude)).toInt()
            }
        } else if (rLayoutHeight!!.contains("ph", true)) {
            val ratio = rLayoutHeight.replace("ph", "", true).toFloatOrNull()
            ratio?.let {
                size[1] = (ratio * (parentHeight - rLayoutHeightExclude)).toInt()
            }
        }
    }
    return size
}

/**手势是否结束*/
public fun View.isTouchFinish(event: MotionEvent) = event.isFinish()

public fun View.clickIt(listener: View.OnClickListener) {
    if (listener is RClickListener) {
        setOnClickListener(listener)
    } else {
        setOnClickListener(object : RClickListener() {
            override fun onRClick(view: View?) {
                listener.onClick(view)
            }
        })
    }
}

public fun View.clickIt(onClick: (View) -> Unit) {
    setOnClickListener(object : RClickListener() {
        override fun onRClick(view: View?) {
            onClick.invoke(this@clickIt)
        }
    })
}

public fun View.longClick(listener: (View) -> Unit) {
    setOnLongClickListener {
        listener.invoke(it)
        true
    }
}

/**焦点变化改变监听*/
public fun EditText.onFocusChange(listener: (Boolean) -> Unit) {
    this.onFocusChangeListener =
        View.OnFocusChangeListener { _, hasFocus -> listener.invoke(hasFocus) }
    listener.invoke(this.isFocused)
}

/**空文本变化监听*/
public fun EditText.onEmptyText(listener: (Boolean) -> Unit) {
    this.addTextChangedListener(object : SingleTextWatcher() {
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            super.onTextChanged(s, start, before, count)
            listener.invoke(TextUtils.isEmpty(s))
        }
    })
    listener.invoke(TextUtils.isEmpty(this.text))
}

/**只要文本改变就通知*/
public fun EditText.onTextChange(
    defaultText: CharSequence? = null,
    listener: (CharSequence) -> Unit
) {
    this.addTextChangedListener(object : SingleTextWatcher() {
        var lastText: CharSequence? = defaultText

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            super.onTextChanged(s, start, before, count)
            val text = s?.toString() ?: ""
            if (TextUtils.equals(lastText, text)) {
            } else {
                listener.invoke(text)
                lastText = text
            }
        }
    })
}

/**相同文本不重复通知*/
public fun EditText.onTextChangeFilter(listener: (String) -> Unit) {
    this.addTextChangedListener(object : SingleTextWatcher() {
        var lastText: String? = null
        var text: String? = null
        var lastTime = -1L

        val notify = Runnable {
            val text = this.text ?: ""
            listener.invoke(text)
            lastText = text
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            super.onTextChanged(s, start, before, count)
            text = s?.toString() ?: ""
            if (!lastText.isNullOrEmpty() && TextUtils.equals(lastText, text)) {
            } else {
                //val time = nowTime()
                removeCallbacks(notify)
//                if (lastTime == -1L || (time - lastTime) < 300L) {
//                    //延迟300毫秒通知一次
//                }
                //必定300毫秒通知一次
                postDelayed(notify, 300L)
            }
        }
    })
}

/**输入框, 按下删除键*/
public fun EditText.onBackPress(listener: (EditText) -> Unit) {
    setOnKeyListener { v, keyCode, keyEvent ->
        return@setOnKeyListener if (keyCode == KeyEvent.KEYCODE_DEL && keyEvent.action == KeyEvent.ACTION_UP) {
            listener.invoke(v as EditText)
            true
        } else {
            false
        }
    }
}

/**发送删除键*/
public fun EditText.sendDelKey() {
    this.del()
}

public fun EditText.hideSoftInput() {
    RSoftInputLayout.hideSoftInput(this)
}

public fun RRecyclerView.onSizeChanged(listener: (w: Int, h: Int, oldw: Int, oldh: Int) -> Unit) {
    this.setOnSizeChangedListener { w, h, oldw, oldh ->
        listener.invoke(w, h, oldw, oldh)
    }
}

/**
 * 错误提示
 */
public fun View.error() {
    //Anim.band(this)

    val mAnimatorSet = AnimatorSet()

    mAnimatorSet.playTogether(
        ObjectAnimator.ofFloat(this, "scaleX", 1f, 1.25f, 0.75f, 1.15f, 1f),
        ObjectAnimator.ofFloat(this, "scaleY", 1f, 0.75f, 1.25f, 0.85f, 1f)
    )

    mAnimatorSet.interpolator = DecelerateInterpolator()
    mAnimatorSet.duration = 300
    mAnimatorSet.start()

    requestFocus()
}

public fun View.visible() {
    visibility = View.VISIBLE
}

public fun View.gone() {
    visibility = View.GONE
}

public fun View.invisible() {
    visibility = View.INVISIBLE
}

public fun TextView.isEmpty(): Boolean {
    return TextUtils.isEmpty(string())
}

public fun TextView.string(trim: Boolean = true): String {
    var rawText = if (TextUtils.isEmpty(text)) {
        ""
    } else {
        text.toString()
    }
    if (trim) {
        rawText = rawText.trim({ it <= ' ' })
    }
    return rawText
}

/**
 * 获取键盘的高度
 */
public fun View.getSoftKeyboardHeight(): Int {
    val screenHeight = getScreenHeightPixels()
    val rect = Rect()
    getWindowVisibleDisplayFrame(rect)
    val visibleBottom = rect.bottom
    return screenHeight - visibleBottom
}

/**
 * 屏幕高度(不包含虚拟导航键盘的高度)
 */
public fun View.getScreenHeightPixels(): Int {
    return resources.displayMetrics.heightPixels
}

/**
 * 判断键盘是否显示
 */
public fun View.isSoftKeyboardShow(): Boolean {
    val screenHeight = getScreenHeightPixels()
    val keyboardHeight = getSoftKeyboardHeight()
    return screenHeight != keyboardHeight && keyboardHeight > 100
}

/**
 * 返回结果表示是否为空
 */
public fun EditText.checkEmpty(checkPhone: Boolean = false): Boolean {
    if (isEmpty()) {
        error()
        requestFocus()

        if (!isSoftKeyboardShow()) {
            if (parent is FrameLayout && parent.parent is TextInputLayout) {
                postDelayed({ RSoftInputLayout.showSoftInput(this) }, 200)
            } else {
                RSoftInputLayout.showSoftInput(this)
            }
        }

        return true
    }
    if (checkPhone) {
        if (isPhone(string())) {

        } else {
            error()
            requestFocus()
            return true
        }
    }
    return false
}

public fun EditText.setInputText(text: CharSequence?) {
    this.setText(text)
    setSelection(text?.length ?: 0)
}

/**触发删除或回退键*/
public fun EditText.del() {
    this.dispatchKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
}

/**取消增益滑动效果*/
public fun View.setNoOverScroll() {
    overScrollMode = View.OVER_SCROLL_NEVER
}

/**设置阴影背景*/
public fun View.showShadowViewDrawable(shadowRadius: Int = 6) {
//    val sp = ShadowProperty()
//            .setShadowColor(0x77000000)
//            .setShadowDy((1f * density()).toInt())//y轴偏移
//            .setShadowRadius((shadowRadius * density()).toInt())//阴影半径
//            .setShadowSide(ShadowProperty.ALL)
//    val sd = ShadowViewDrawable(sp, Color.RED, 0f, 0f)
//    ViewCompat.setLayerType(this, ViewCompat.LAYER_TYPE_SOFTWARE, null)
//    ViewCompat.setBackground(this, sd)
}

/**双击控件回调*/
public fun View.onDoubleTap(listener: () -> Unit) {
    RGestureDetector.onDoubleTap(this) {
        listener.invoke()
    }
}

/**自己监听控件的单击事件, 防止系统的不回调*/
public fun View.onSingleTapConfirmed(listener: () -> Boolean) {
    val gestureDetectorCompat = GestureDetectorCompat(context,
        object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
                return listener.invoke()
            }
        })
    setOnTouchListener { _, event ->
        gestureDetectorCompat.onTouchEvent(event)
        false
    }
}

/**无限循环, 每秒60帧的速度*/
public val View.valueAnimator: ValueAnimator by lazy {
    ValueAnimator.ofInt(0, 100).apply {
        interpolator = LinearInterpolator()
        repeatMode = ValueAnimator.RESTART
        repeatCount = ValueAnimator.INFINITE
        duration = 1000
    }
}

/**显示未读小红点*/
public fun View.showNoRead(
    show: Boolean = true,
    radius: Float = 3 * density(),
    paddTop: Float = 2 * density(),
    paddRight: Float = 2 * density()
) {
    var drawNoRead: RDrawNoRead? = null
    if (this is RImageView) {
        drawNoRead = this.drawNoRead
    } else if (this is RTextView) {
        drawNoRead = this.drawNoRead
    }

    if (drawNoRead != null) {
        drawNoRead.setShowNoRead(show)
        drawNoRead.setNoReadRadius(radius)
        drawNoRead.setNoReadPaddingTop(paddTop)
        drawNoRead.setNoReadPaddingRight(paddRight)
    }
}

public fun CompoundButton.onChecked(listener: (Boolean) -> Unit) {
    this.setOnCheckedChangeListener { _, isChecked ->
        listener.invoke(isChecked)
    }
    listener.invoke(isChecked)
}

public fun TextView.addPaintFlags(flag: Int, add: Boolean = true, invalidate: Boolean = true) {
    if (add) {
        paint.flags = paint.flags or flag
    } else {
        paint.flags = paint.flags and flag.inv()
    }
    if (invalidate) {
        postInvalidate()
    }
}

public fun TextView.setTextBold(bold: Boolean) {
    addPaintFlags(Paint.FAKE_BOLD_TEXT_FLAG, bold, true)
}

public fun Paint.setPaintFlags(flag: Int, add: Boolean = true) {
    if (add) {
        this.flags = this.flags or flag
    } else {
        this.flags = this.flags and flag.inv()
    }
}

public fun View.hideFromBottom(anim: Boolean = true) {
    if (this.translationY == 0f) {
        //是显示状态
        if (anim) {
            this.animate().setDuration(300)
                .translationY((this.measuredHeight).toFloat())
                .start()
        } else {
            ViewCompat.setTranslationY(this, (this.measuredHeight).toFloat())
        }
    }
}

public fun View.showFromBottom(anim: Boolean = true) {
    if (this.translationY == (this.measuredHeight).toFloat()) {
        //是隐藏状态
        if (anim) {
            this.animate().setDuration(300)
                .translationY(0f)
                .start()
        } else {
            ViewCompat.setTranslationY(this, 0f)
        }
    }
}

public fun View.hideFromTop(anim: Boolean = true) {
    if (this.translationY == 0f) {
        //是显示状态
        if (anim) {
            this.animate().setDuration(300)
                .translationY((-this.measuredHeight).toFloat())
                .start()
        } else {
            ViewCompat.setTranslationY(this, (-this.measuredHeight).toFloat())
        }
    }
}

public fun View.showFromTop(anim: Boolean = true) {
    if (this.translationY == (-this.measuredHeight).toFloat()) {
        //是隐藏状态
        if (anim) {
            this.animate().setDuration(300)
                .translationY(0f)
                .start()
        } else {
            ViewCompat.setTranslationY(this, 0f)
        }
    }
}

/**布局中心的坐标*/
public fun View.layoutCenterX(): Int {
    return left + measuredWidth / 2
}

public fun View.layoutCenterY(): Int {
    return top + measuredHeight / 2
}

public fun View.onInitView(init: (RBaseViewHolder) -> Unit) {
    init.invoke(RBaseViewHolder(this))
}

public fun View.toBitmap(): Bitmap {
    return RUtils.saveView(this)
}

public fun EditText.addFilter(filter: InputFilter) {
    val oldFilters = filters
    val newFilters = arrayOfNulls<InputFilter>(oldFilters.size + 1)
    System.arraycopy(oldFilters, 0, newFilters, 0, oldFilters.size)
    newFilters[oldFilters.size] = filter
    filters = newFilters
}

public fun EditText.setFilter(filter: InputFilter) {
    val newFilters = arrayOfNulls<InputFilter>(1)
    newFilters[0] = filter
    filters = newFilters
}

/**
 * 竖直方向上的padding
 */
public fun View.getPaddingVertical(): Int {
    return paddingTop + paddingBottom
}

/**
 * 水平方向上的padding
 */
public fun View.getPaddingHorizontal(): Int {
    return paddingLeft + paddingRight
}

public fun View.setBgDrawable(drawable: Drawable? = null) {
    ResUtil.setBgDrawable(this, drawable)
}

public fun View.setBgDrawable(resId: Int) {
    setBgDrawable(getDrawable(resId))
}

/**
 * 设置视图的宽高
 * */
public fun View.setWidthHeight(width: Int, height: Int) {
    val params = layoutParams
    params.width = width
    params.height = height
    layoutParams = params
}

public fun View.setWidth(width: Int, keepOffset: Boolean = false) {
    val offsetTop = top
    val offsetLeft = left

    val params = layoutParams
    params.width = width
    layoutParams = params

    if (keepOffset) {
        viewTreeObserver.addOnGlobalLayoutListener(
            RestoreOffsetLayoutListener(
                this,
                offsetTop,
                offsetLeft
            )
        )
    }
}

public fun View.setHeight(height: Int, keepOffset: Boolean = false) {
    val offsetTop = top
    val offsetLeft = left
    val params = layoutParams
    params.height = height
    layoutParams = params

    if (keepOffset) {
        viewTreeObserver.addOnGlobalLayoutListener(
            RestoreOffsetLayoutListener(
                this,
                offsetTop,
                offsetLeft
            )
        )
    }
}

/**
 * 加载网络图片或者地址
 * */
public fun ImageView.load(url: String?, option: (RequestBuilder<Drawable>.() -> Unit)? = null) {
    if (TextUtils.isEmpty(url)) {
    } else {
        if (url!!.isFileExists()) {
            Glide.with(this)
                .load(File(url))
        } else {
            Glide.with(this)
                .load(url)
        }
            .apply {
                //dontAnimate()
                //autoClone()
                diskCacheStrategy(DiskCacheStrategy.ALL)
                override(Target.SIZE_ORIGINAL)

                addListener(object : RequestListener<Drawable> {
                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                })

                option?.let {
                    it()
                }
            }
            .into(this)
    }
}

/**
 * 获取View, 相对于手机屏幕的矩形
 * */
public fun View.getViewRect(result: Rect = Rect()): Rect {
    var offsetX = 0
    var offsetY = 0

    //横屏, 并且显示了虚拟导航栏的时候. 需要左边偏移
    //只计算一次
    (context as? Activity)?.let {
        it.window.decorView.getGlobalVisibleRect(result)
        if (result.width() > result.height()) {
            //横屏了
            offsetX = -RUtils.navBarHeight(it)
        }
    }

    return getViewRect(offsetX, offsetY, result)
}

/**
 * 获取View, 相对于手机屏幕的矩形, 带皮阿尼一
 * */
public fun View.getViewRect(offsetX: Int, offsetY: Int, result: Rect = Rect()): Rect {
    //可见位置的坐标, 超出屏幕的距离会被剃掉
    //getGlobalVisibleRect(r)
    val r2 = IntArray(2)
    //val r3 = IntArray(2)
    //相对于屏幕的坐标
    getLocationOnScreen(r2)
    //相对于窗口的坐标
    //getLocationInWindow(r3)

    val left = r2[0] + offsetX
    val top = r2[1] + offsetY

    result.set(left, top, left + measuredWidth, top + measuredHeight)
    return result
}

public fun View.marginLayoutParams(config: ViewGroup.MarginLayoutParams.() -> Unit) {
    (layoutParams as? ViewGroup.MarginLayoutParams)?.let {
        it.config()
        layoutParams = it
    }
}

public fun View.layoutParams(config: ViewGroup.LayoutParams.() -> Unit) {
    layoutParams.let {
        it.config()
        layoutParams = it
    }
}

public fun <T : View> View.find(id: Int): T? {
    return findViewById<T>(id)
}

/**
 * 旋转到多少度
 * */
public fun View.rotation(
    rotation: Float,
    duration: Long = 300,
    config: ViewPropertyAnimator.() -> Unit = {}
) {
    animate().apply {
        rotation(rotation)
        setDuration(duration)
        config()
        start()
    }
}

/**
 * 旋转多少度
 * */
public fun View.rotationBy(
    rotation: Float,
    duration: Long = 300,
    config: ViewPropertyAnimator.() -> Unit = {}
) {
    animate().apply {
        rotationBy(rotation)
        setDuration(duration)
        config()
        start()
    }
}

public fun View.setPadding(padding: Int) {
    setPadding(padding, padding, padding, padding)
}

public fun View.setPaddingVertical(padding: Int) {
    setPadding(left, padding, right, padding)
}

public fun View.setPaddingHorizontal(padding: Int) {
    setPadding(padding, top, padding, bottom)
}

/**
 * 判断v, 是否在 view 内
 * */
public fun View.isViewIn(v: View): Boolean {
    if (v.left - scrollX >= 0 &&
        v.right - scrollX <= measuredWidth &&
        v.top - scrollY >= 0 &&
        v.bottom - scrollY == measuredHeight
    ) {
        return true
    }
    return false
}

/**清空所有[TextWatcher]*/
public fun TextView.clearListeners() {
    //private ArrayList<TextWatcher> mListeners;
    val mListeners: ArrayList<Any>? =
        Reflect.getMember(TextView::class.java, this, "mListeners") as? ArrayList<Any>
    mListeners?.clear()
}

public fun View.drawSize(): Int = max(viewDrawWith, viewDrawHeight)
public fun View.viewSize(): Int = max(measuredWidth, measuredHeight)

public fun View.drawCenterX(): Int {
    return paddingLeft + viewDrawWith / 2
}

public fun View.drawCenterY(): Int {
    return paddingTop + viewDrawHeight / 2
}

public fun Int.gravityFlag(): ByteArray {
    //L T R B C
    //11111
    //00000
    val bytes = ByteArray(5) {
        0
    }

    val absoluteGravity = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        Gravity.getAbsoluteGravity(this, LayoutDirection.LTR)
    } else {
        this
    }

    if (absoluteGravity and Gravity.FILL == Gravity.FILL) {
        //no op
    } else {
        if (absoluteGravity and Gravity.FILL_VERTICAL == Gravity.FILL_VERTICAL) {
            //no op
        } else {
            if (absoluteGravity and Gravity.TOP == Gravity.TOP) {
                bytes[1] = 1
            }
            if (absoluteGravity and Gravity.BOTTOM == Gravity.BOTTOM) {
                bytes[3] = 1
            }
        }
        if (absoluteGravity and Gravity.FILL_HORIZONTAL == Gravity.FILL_HORIZONTAL) {
            //no op
        } else {
            if (absoluteGravity and Gravity.START == Gravity.START) {
                bytes[0] = 1
            } else if (absoluteGravity and Gravity.LEFT == Gravity.LEFT) {
                bytes[0] = 1
            }
            if (absoluteGravity and Gravity.END == Gravity.END) {
                bytes[2] = 1
            } else if (absoluteGravity and Gravity.RIGHT == Gravity.RIGHT) {
                bytes[2] = 1
            }
        }
    }
    if (absoluteGravity and Gravity.CENTER == Gravity.CENTER) {
        //都会执行
        bytes[4] = 1
    } else {
        if (absoluteGravity and Gravity.CENTER_VERTICAL == Gravity.CENTER_VERTICAL) {
            bytes[0] = 1 //竖直 左对齐
            bytes[4] = 1 //在TextView中, 这个值始终为1, 但是在自定义的Gravity的地方就不会
        }
        if (absoluteGravity and Gravity.CENTER_HORIZONTAL == Gravity.CENTER_HORIZONTAL) {
            bytes[1] = 1 //水平 顶部对齐
            bytes[4] = 1 //在TextView中, 这个值始终为1, 但是在自定义的Gravity的地方就不会
        }
    }
    return bytes
}

public fun Int.isGravityCenter(): Boolean {
    return this == Gravity.CENTER
}

public fun Int.isGravityCenterHorizontal(): Boolean {
    //L T R B C
    //0 1 2 3 4
    val flags = gravityFlag()
    val result = (flags[0] == 0.toByte() && flags[2] == 0.toByte()) &&
            (flags[1] == 1.toByte() || flags[3] == 1.toByte()) &&
            flags[4] == 1.toByte()

    return isGravityCenter() || result
}

public fun Int.isGravityCenterVertical(): Boolean {
    //L T R B C
    //0 1 2 3 4
    val flags = gravityFlag()
    val result = (flags[1] == 0.toByte() && flags[3] == 0.toByte()) &&
            (flags[0] == 1.toByte() || flags[2] == 1.toByte()) &&
            flags[4] == 1.toByte()
    return isGravityCenter() || result
}

public fun Int.isGravityTop(): Boolean {
    val flags = gravityFlag()
    return flags[1] == 1.toByte()
}

public fun Int.isGravityBottom(): Boolean {
    val flags = gravityFlag()
    return flags[3] == 1.toByte()
}

public fun Int.isGravityLeft(): Boolean {
    val flags = gravityFlag()
    return flags[0] == 1.toByte()
}

public fun Int.isGravityRight(): Boolean {
    val flags = gravityFlag()
    return flags[2] == 1.toByte()
}

/*---------------------------------------------*/

public fun TextView.gravityFlag(): ByteArray {
    return gravity.gravityFlag()
}

public fun TextView.isGravityCenter(): Boolean {
    return gravity.isGravityCenter()
}

public fun TextView.isGravityCenterHorizontal(): Boolean {
    return gravity.isGravityCenterHorizontal()
}

public fun TextView.isGravityCenterVertical(): Boolean {
    return gravity.isGravityCenterVertical()
}

public fun TextView.isGravityTop(): Boolean {
    return gravity.isGravityTop()
}

public fun TextView.isGravityBottom(): Boolean {
    return gravity.isGravityBottom()
}

public fun TextView.isGravityLeft(): Boolean {
    return gravity.isGravityLeft()
}

public fun TextView.isGravityRight(): Boolean {
    return gravity.isGravityRight()
}

public fun View.offsetTop(offset: Int) {
    ViewCompat.offsetTopAndBottom(this, offset)
}

/**限制滚动偏移的范围, 返回值表示 需要消耗的 距离*/
public fun View.offsetTop(offset: Int, minTop: Int, maxTop: Int): Int {
    val offsetTop = top + offset
    val newTop = clamp(offsetTop, minTop, maxTop)

    offsetTopTo(newTop)

    return -(offset - (offsetTop - newTop))
}

public fun View.offsetTopTo(newTop: Int) {
    offsetTop(newTop - top)
}

public fun View.offsetTopTo(newTop: Int, minTop: Int, maxTop: Int) {
    offsetTop(newTop - top, minTop, maxTop)
}

public fun View.offsetLeft(offset: Int) {
    ViewCompat.offsetLeftAndRight(this, offset)
}

/**限制滚动偏移的范围, 返回值表示 需要消耗的 距离*/
public fun View.offsetLeft(offset: Int, minLeft: Int, maxLeft: Int): Int {
    val offsetLeft = left + offset
    val newLeft = clamp(offsetLeft, minLeft, maxLeft)

    offsetTopTo(newLeft)

    return -(offset - (offsetLeft - newLeft))
}

public fun View.offsetLeftTo(newLeft: Int) {
    offsetLeft(newLeft - left)
}

/**显示软键盘, [EditText]*/
public fun View.showSoftInput() {
    RSoftInputLayout.showSoftInput(this)
}

/**快速操作[LayoutParams]*/
public fun View.marginParams(config: ViewGroup.MarginLayoutParams.() -> Unit = {}): View {
    (this.layoutParams as? ViewGroup.MarginLayoutParams)?.apply {
        config()
        this@marginParams.layoutParams = layoutParams
    }
    return this
}

public fun View.frameParams(config: FrameLayout.LayoutParams.() -> Unit = {}): View {
    (this.layoutParams as? FrameLayout.LayoutParams)?.apply {
        config()
        this@frameParams.layoutParams = layoutParams
    }
    return this
}

public fun View.coordinatorParams(config: CoordinatorLayout.LayoutParams.() -> Unit = {}): View {
    (this.layoutParams as? CoordinatorLayout.LayoutParams)?.apply {
        config()
        this@coordinatorParams.layoutParams = layoutParams
    }
    return this
}

public fun View.constraintParams(config: ConstraintLayout.LayoutParams.() -> Unit = {}): View {
    (this.layoutParams as? ConstraintLayout.LayoutParams)?.apply {
        config()
        this@constraintParams.layoutParams = layoutParams
    }
    return this
}

/**将[LayoutParams]强转成指定对象*/
public fun ViewGroup.LayoutParams.marginParams(config: ViewGroup.MarginLayoutParams.() -> Unit = {}): ViewGroup.MarginLayoutParams? {
    return (this as? ViewGroup.MarginLayoutParams)?.run {
        config()
        this
    }
}

public fun ViewGroup.LayoutParams.frameParams(config: FrameLayout.LayoutParams.() -> Unit = {}): FrameLayout.LayoutParams? {
    return (this as? FrameLayout.LayoutParams)?.run {
        config()
        this
    }
}

public fun ViewGroup.LayoutParams.coordinatorParams(config: CoordinatorLayout.LayoutParams.() -> Unit = {}): CoordinatorLayout.LayoutParams? {
    return (this as? CoordinatorLayout.LayoutParams)?.run {
        config()
        this
    }
}

public fun ViewGroup.LayoutParams.constraintParams(config: ConstraintLayout.LayoutParams.() -> Unit = {}): ConstraintLayout.LayoutParams? {
    return (this as? ConstraintLayout.LayoutParams)?.run {
        config()
        this
    }
}

public fun ViewGroup.LayoutParams.recyclerParams(config: RecyclerView.LayoutParams.() -> Unit = {}): RecyclerView.LayoutParams? {
    return (this as? RecyclerView.LayoutParams)?.run {
        config()
        this
    }
}

public fun TextView.setTextSizeWithDp(textSize: Any) {
    setTextSize(
        TypedValue.COMPLEX_UNIT_PX,
        when (textSize) {
            is Int -> textSize.toFloat()
            is Float -> textSize
            else -> getTextSize()
        }
    )
}

/**视图 变灰*/
public fun View.grayscale(enable: Boolean = true) {
    if (enable) {
        //变灰, 界面灰度处理
        val matrix = ColorMatrix()
        matrix.setSaturation(0f)//饱和度 0灰色 100过度彩色，50正常
        val filter = ColorMatrixColorFilter(matrix)
        val paint = Paint()
        paint.colorFilter = filter

        setLayerType(View.LAYER_TYPE_SOFTWARE, paint)
    } else {
        setLayerType(View.LAYER_TYPE_NONE, null)
    }
}

public fun View.padding(config: Padding.() -> Unit) {
    val padding = Padding(paddingLeft, paddingTop, paddingRight, paddingBottom)
    padding.config()
    setPadding(padding.left, padding.top, padding.right, padding.bottom)
}

data class Padding(var left: Int, var top: Int, var right: Int, var bottom: Int)

class RestoreOffsetLayoutListener(val view: View, val offsetTop: Int, val offsetLeft: Int) :
    OnGlobalLayoutListener {
    override fun onGlobalLayout() {
        view.offsetTopAndBottom(offsetTop)
        view.offsetLeftAndRight(offsetLeft)
        view.viewTreeObserver.removeOnGlobalLayoutListener(this)
    }
}

/**获取[View]在指定[parent]中的矩形坐标*/
public fun View.getLocationInParent(parentView: View? = null): Rect {
    val result: Rect

    val parent: View? = parentView ?: (parent as? View)

    if (parent == null) {
        result = getViewRect()
    } else {
        result = Rect(0, 0, 0, 0)
        if (this != parent) {
            fun doIt(view: View, parent: View, rect: Rect) {
                val viewParent = view.parent
                if (viewParent is View) {
                    rect.left += view.left
                    rect.top += view.top
                    if (viewParent != parent) {
                        doIt(viewParent, parent, rect)
                    }
                }
            }
            doIt(this, parent, result)
        }

        result.right = result.left + this.measuredWidth
        result.bottom = result.top + this.measuredHeight
    }

    return result
}

public fun View.findRecyclerView(): RecyclerView? {
    return findView {
        it is RecyclerView
    } as? RecyclerView
}

public fun View.findView(isIt: (View) -> Boolean): View? {
    return when {
        isIt(this) -> this
        this is ViewGroup -> {
            var result: View? = null
            for (i in 0 until childCount) {
                val childAt = getChildAt(i)
                result = childAt.findView(isIt)
                if (result != null) {
                    break
                }
            }
            result
        }
        else -> null
    }
}