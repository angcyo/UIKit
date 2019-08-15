package com.angcyo.uiview.less.kotlin

import android.graphics.Color
import android.view.View
import com.angcyo.lib.L
import com.angcyo.uiview.less.R
import com.angcyo.uiview.less.base.BaseFragment
import com.angcyo.uiview.less.base.BaseTitleFragment
import com.angcyo.uiview.less.base.helper.FragmentHelper
import com.angcyo.uiview.less.base.helper.TitleItemHelper
import com.angcyo.uiview.less.utils.TopToast
import com.angcyo.uiview.less.widget.ImageTextView
import com.luck.picture.lib.rxbus2.RxBus
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/05/06
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */

public fun get(fm: androidx.fragment.app.FragmentManager?): FragmentHelper.Builder {
    return FragmentHelper.build(fm)
        .defaultEnterAnim()
        .hideBeforeIndex(2)
}

public fun only(fm: androidx.fragment.app.FragmentManager?): FragmentHelper.Builder {
    return FragmentHelper.build(fm)
        .anim(R.anim.base_scale_alpha_enter, R.anim.base_no_alpha)
        .keepFragment(emptyList())
}

public fun BaseFragment.wtf(): FragmentHelper.Builder {
    return get(parentFragmentManager())
}

public fun BaseFragment.only(): FragmentHelper.Builder {
    return only(parentFragmentManager())
}

/**
 * 移除其他, 只显示 f
 * */
public fun BaseFragment.only(
    f: Class<out androidx.fragment.app.Fragment>,
    init: FragmentHelper.Builder.() -> Unit = {}
): androidx.fragment.app.Fragment? {
    val builder = only().showFragment(f).apply {
        init()
    }
    return builder.doIt()
}

/**
 * 移除其他, 只显示 f
 * */
public fun BaseFragment.only(
    f: androidx.fragment.app.Fragment,
    init: FragmentHelper.Builder.() -> Unit = {}
): androidx.fragment.app.Fragment? {
    val builder = only().showFragment(f).apply {
        init()
    }
    return builder.doIt()
}

public fun BaseFragment.show(
    f: Class<out androidx.fragment.app.Fragment>,
    init: FragmentHelper.Builder.() -> Unit = {}
): androidx.fragment.app.Fragment? {
    val builder = wtf().showFragment(f).apply {
        init()
    }
    return builder.doIt()
}

public fun BaseFragment.show(
    f: androidx.fragment.app.Fragment,
    init: FragmentHelper.Builder.() -> Unit = {}
): androidx.fragment.app.Fragment? {
    val builder = wtf().showFragment(f).apply {
        init()
    }
    return builder.doIt()
}

public fun toast_tip(tipText: CharSequence, imageResId: Int = -1) {
    TopToast.show(tipText, imageResId)
}

public fun BaseFragment.createItem(
    res: Int,
    config: TitleItemHelper.Builder.() -> Unit = {},
    click: (View) -> Unit
): ImageTextView {
    val item = TitleItemHelper.createItem(requireContext(), res) {
        click(it)
    }
    val builder = TitleItemHelper.Builder(requireContext())
    builder.targetView = item
    builder.config()
    builder.doIt()
    return item
}

public fun BaseFragment.createItem(
    text: String,
    config: TitleItemHelper.Builder.() -> Unit = {},
    click: (View) -> Unit
): ImageTextView {
    val item = TitleItemHelper.createItem(requireContext(), text) {
        click(it)
    }
    val builder = TitleItemHelper.Builder(requireContext())
    builder.targetView = item
    builder.config()
    builder.doIt()
    return item
}

public fun BaseTitleFragment.appendRightItem(
    res: Int,
    textColor: Int = Color.WHITE,
    config: TitleItemHelper.Builder.() -> Unit = {},
    click: (View) -> Unit
): ImageTextView {
    val item = createItem(res, config, click)
    item.textShowColor = textColor
    rightControl().addView(item)
    return item
}

public fun BaseTitleFragment.appendRightItem(
    text: String,
    textColor: Int = Color.WHITE,
    config: TitleItemHelper.Builder.() -> Unit = {},
    click: (View) -> Unit
): ImageTextView {
    val item = createItem(text, config, click)
    item.textShowColor = textColor
    rightControl().addView(item)
    return item
}

public fun BaseTitleFragment.appendLeftItem(
    res: Int,
    textColor: Int = Color.WHITE,
    config: TitleItemHelper.Builder.() -> Unit = {},
    click: (View) -> Unit
): ImageTextView {
    val item = createItem(res, config, click)
    item.textShowColor = textColor
    leftControl().addView(item)
    return item
}

public fun BaseTitleFragment.appendLeftItem(
    text: String,
    textColor: Int = Color.WHITE,
    config: TitleItemHelper.Builder.() -> Unit = {},
    click: (View) -> Unit
): ImageTextView {
    val item = createItem(text, config, click)
    item.textShowColor = textColor
    leftControl().addView(item)
    return item
}

/**RxBus*/
public fun <T> T.busRegister() {
    if (!RxBus.getDefault().isRegistered(this)) {
        RxBus.getDefault().register(this)
    }
}

/**RxBus*/
public fun <T> T.busUnRegister() {
    if (RxBus.getDefault().isRegistered(this)) {
        RxBus.getDefault().unregister(this)
    }
}

/**RxBus*/
public fun <T> T.busPost() {
    RxBus.getDefault().post(this)
}

/**异常捕获处理*/
public val BaseFragment.coroutineExceptionHandler by lazy {
    CoroutineExceptionHandler { coroutineContext, throwable ->
        L.e("协程异常处理:$coroutineContext ${throwable.message}")
        throwable.printStackTrace()
    }
}

/**单一请求, 单一返回处理*/
public fun <T> BaseFragment.load(
    loader: suspend CoroutineScope.() -> T,
    receiver: (suspend CoroutineScope.(T) -> Unit)? = null,
    onCoroutineExceptionHandler: ((coroutineContext: CoroutineContext, throwable: Throwable) -> Unit)? = null
): Job {
    return baseMainScope.launch(
        Dispatchers.Main +
                if (onCoroutineExceptionHandler == null)
                    coroutineExceptionHandler
                else
                    CoroutineExceptionHandler { coroutineContext, throwable ->
                        onCoroutineExceptionHandler(coroutineContext, throwable)
                    }
    )
    {
        val deferred =
            async(Dispatchers.IO + coroutineExceptionHandler) {
                loader()
            }
        receiver?.let {
            it(deferred.await())
        }
    }
}

public fun <T> BaseFragment.load(loader: suspend CoroutineScope.() -> T): Job {
    return load(loader, null, null)
}

//public fun String.toUrl(fileId: Long = -1): String {
//    if (isFileExists()) {
//        return this
//    }
//
//    var result: String = this
//    if (!toLowerCase().startsWith("http")) {
//        val baseUrl = UIApplication.uiApp.baseUrl
//
//        result = when {
//            /* xx.com/ /api/xx */
//            baseUrl.endsWith("/") && startsWith("/") -> "${baseUrl.substring(
//                0,
//                baseUrl.length - 1
//            )}$this"
//            /* xx.com api/xx */
//            !baseUrl.endsWith("/") && !startsWith("/") -> "$baseUrl/$this"
//            /* xx.com/ api/xx or xx.com /api/xx*/
//            else -> "$baseUrl$this"
//        }
//    }
//
//    if (fileId >= 0) {
//        val uri = Uri.parse(this)
//        val param = "${FormAttachManager.KEY_FILE_ID}=${fileId}"
//
//        if (uri.query?.isNullOrEmpty() != false) {
//            //url 没有 查询参数
//            result = "${result}?${param}"
//        } else {
//            val oldFileId = result.queryParameter(FormAttachManager.KEY_FILE_ID)
//            if (oldFileId?.isNullOrEmpty() != false) {
//                //没有fileId参数
//                result = "${result}&${param}"
//            } else {
//                //有fileId参数
//                result = result.replace("${FormAttachManager.KEY_FILE_ID}=${oldFileId}", param)
//            }
//        }
//    }
//
//    return result
//}