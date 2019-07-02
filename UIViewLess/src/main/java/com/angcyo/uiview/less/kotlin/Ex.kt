package com.angcyo.uiview.less.kotlin

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.*
import android.net.Uri
import androidx.core.math.MathUtils
import android.text.TextUtils
import android.util.Base64
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.webkit.MimeTypeMap
import com.angcyo.http.Http
import com.angcyo.http.Json
import com.angcyo.uiview.less.resources.ResUtil
import com.angcyo.uiview.less.skin.SkinHelper
import com.angcyo.uiview.less.utils.H5
import com.angcyo.uiview.less.utils.RUtils
import com.angcyo.uiview.less.utils.Reflect
import com.angcyo.uiview.less.utils.utilcode.utils.EncodeUtils
import com.angcyo.uiview.less.utils.utilcode.utils.FileUtils
import okhttp3.ResponseBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.Charset
import java.util.*
import java.util.regex.Pattern
import kotlin.random.Random

/**
 * Created by angcyo on ：2017/07/07 16:41
 * 修改备注：
 * Version: 1.0.0
 */

/**整型数中, 是否包含另一个整数*/
public fun Int.have(value: Int): Boolean = if (this == 0 || value == 0) false
else if (this == 0 && value == 0) true
else {
    ((this > 0 && value > 0) || (this < 0 && value < 0)) &&
            this and value == value
}

public fun Int.isIn(value1: Int, value2: Int): Boolean {
    val min = Math.min(value1, value2)
    val max = Math.max(value1, value2)
    return this in min..max
}

public fun Int.remove(value: Int): Int = this and value.inv()
public fun Int.add(value: Int): Int = this or value
public fun Int.dpi(designDpi: Float): Int = RUtils.size(this, designDpi)

public fun Float.toDp() = ResUtil.dpToPx(this)
public fun Int.toDp() = this.toFloat().toDp()

/**
 * 转换成 百分比
 * */
public fun Int.toPercent(bitNum: Int = 0, halfUp: Boolean = false): String =
    RUtils.decimal(this * 1f / 100f, bitNum, halfUp).toFloat().toPercent()

public fun Float.toPercent(): String = "${this * 100}%"

public inline fun <T> T.isLollipop() = RUtils.isLollipop()

public inline fun <T> T.nextInt(until: Int) = Random.nextInt(until)
public inline fun <T> T.nextInt(from: Int /*包含*/, to: Int /*不包含*/) = Random.nextInt(from, to)

public inline fun <T> T.toJson() = Json.to(this)
public inline fun <T> String.fromJson(type: Class<T>) = Json.from<T>(this, type)
public inline fun <T> String.fromJsonList(type: Class<T>) = Json.fromList<T>(this, type)

public inline fun <T> T.toBody() = Http.getJsonBody(toJson())

/**文本的高度*/
public fun Paint.textHeight(): Float = descent() - ascent()

/**文本的宽度*/
public fun Paint.textWidth(text: String): Float = this.measureText(text)

//public inline fun <T : View> UIIViewImpl.vh(id: Int): Lazy<T> {
//    return lazy {
//        v<T>(id)
//    }
//}

public fun Float.abs() = Math.abs(this)
public fun Int.abs() = Math.abs(this)

public fun Float.max0() = Math.max(0f, this)
public fun Int.max0() = Math.max(0, this)

/**目标值的最小值是自己,  目标值必须超过自己*/
public fun Float.minValue(value: Float /*允许的最小值*/) = Math.max(value, this)

public fun Float.minValue(value: Int) = Math.max(value.toFloat(), this)

public fun Int.minValue(value: Int) = Math.max(value, this)

/**目标值的最大值是自己,  目标值不能超过自己*/
public fun Float.maxValue(value: Float /*允许的最大值*/) = Math.min(value, this)

public fun Int.maxValue(value: Int) = Math.min(value, this)

public fun String.int() = if ((TextUtils.isEmpty(this) || "null".equals(this, true))) 0 else this.toInt()

/**矩形缩放*/
public fun Rect.scale(scaleX: Float, scaleY: Float) {
    var dw = 0
    var dh = 0
    if (scaleX != 1.0f) {
        /*宽度变化量*/
        val offsetW = (width() * scaleX + 0.5f).toInt() - width()
        dw = offsetW / 2
    }
    if (scaleY != 1.0f) {
        /*高度变化量*/
        val offsetH = (height() * scaleY + 0.5f).toInt() - height()
        dh = offsetH / 2
    }
    inset(-dw, -dh)
}

/**矩形旋转*/
public fun Rect.rotateTo(inRect: Rect, degrees: Float) {
    var dw = 0
    var dh = 0

    /*斜边长度*/
    val c = c()

    /*斜边与邻边的幅度*/
    val aR = Math.asin(this.height().toDouble() / c /*弧度*/)

    /*角度转弧度*/
    val d = Math.toRadians(degrees.toDouble()) /*弧度*/

    val nW1 = Math.abs(c / 2 * Math.cos(aR - d))
    val nW2 = Math.abs(c / 2 * Math.cos(Math.PI - aR - d))

    val nH1 = Math.abs(c / 2 * Math.sin(aR - d))
    val nH2 = Math.abs(c / 2 * Math.sin(Math.PI - aR - d))

    /*新的宽度*/
    val nW = 2 * Math.max(nW1, nW2)
    /*新的宽度*/
    val nH = 2 * Math.max(nH1, nH2)

    dw = nW.toInt() - this.width()
    dh = nH.toInt() - this.height()

    inRect.set(this)
    inRect.inset(-dw / 2, -dh / 2)
}

/**矩形斜边长度*/
public fun Rect.c(): Double {
    /*斜边长度*/
    val c =
        Math.sqrt(Math.pow(this.width().toDouble(), 2.toDouble()) + Math.pow(this.height().toDouble(), 2.toDouble()))
    return c
}

public fun Rect.scaleTo(inRect: Rect /*用来接收最后结果的矩形*/, scaleX: Float, scaleY: Float) {
    var dw = 0
    var dh = 0
    if (scaleX != 1.0f) {
        /*宽度变化量*/
        val offsetW = (width() * scaleX + 0.5f).toInt() - width()
        dw = offsetW / 2
    }
    if (scaleY != 1.0f) {
        /*高度变化量*/
        val offsetH = (height() * scaleY + 0.5f).toInt() - height()
        dh = offsetH / 2
    }
    inRect.set(left, top, right, bottom)
    inRect.inset(-dw, -dh)
}

public inline fun <T> T.isMainThread() = RUtils.isMainThread()

public fun <K, V> Map<K, V>.each(item: (key: K, value: V) -> Unit) {
    for (entry in this.entries) {
        item.invoke(entry.key, entry.value)
    }
}

/**列表中, 不为空的字符串数量*/
public fun List<String>.stringSize(checkExist: Boolean = false /*是否检查重复字符串*/): Int {
    val list = mutableListOf<String>()
    for (s in this) {
        if (TextUtils.isEmpty(s)) {
            continue
        }
        if (checkExist && list.contains(s)) {
            continue
        }
        list.add(s)
    }
    return list.size
}

public fun List<*>.toStringArray(): Array<String> {
    return RUtils.toStringArray(this)
}

/**文件是否存在*/
public fun String.isFileExists(): Boolean {
    return File(this).exists()
}

/**通过类, 调用无参构造方法, 创建一个对象 */
public fun <T> Class<T>.newObject(): T {
    return Reflect.newObject<T>(this)
}

/**大数字 缩短显示*/
public fun String.shortString(): String {
    return RUtils.getShortString(this, "", true)
}

public fun String.isVideoMimeType(): Boolean {
    return this.startsWith("video", true)
}

public fun String.isAudioMimeType(): Boolean {
    return this.startsWith("audio", true)
}

public fun String.isImageMimeType(): Boolean {
    return this.startsWith("image", true)
}

public fun String.http(): String {
    return if (this.startsWith("http")) {
        this
    } else {
        if (this.startsWith("//")) {
            "http:$this"
        } else {
            "http://$this"
        }
    }
}

public fun String.https(): String {
    return if (this.startsWith("http")) {
        this
    } else {
        if (this.startsWith("//")) {
            "https:$this"
        } else {
            "https://$this"
        }
    }
}

///**返回拼音首字母*/
//public fun String.toPinyin(): String {
//    if (TextUtils.isEmpty(this)) {
//        return ""
//    }
//    return Pinyin.toPinyin(this[0]).toUpperCase()[0].toString()
//}

/**判断字符串是否是纯数字*/
public fun String.isNumber(): Boolean {
    val pattern = Pattern.compile("^[-\\+]?[\\d]*$")
    return pattern.matcher(this).matches()
}

public fun String.equ(char: CharSequence): Boolean {
    return TextUtils.equals(this, char)
}

public fun String.toFloatNumber(): Float {
    return this.toFloatOrNull() ?: 0f
}

public fun String.urlEncode(): String {
    return URLEncoder.encode(this, "UTF-8")
}

/**
 * 获取文件扩展名
 * */
public fun String.extName(): String {
    return FileUtils.getExtensionName(this)
}

/**
 * 不带扩展名的文件名
 * */
public fun String.noExtName(): String {
    return FileUtils.getFileNameNoEx(this)
}

/**
 * 获取Int对应颜色的透明颜色
 * @param alpha [0..255] 值越小,越透明
 * */
public fun Int.tranColor(alpha: Int): Int {
    return SkinHelper.getTranColor(this, alpha)
}


public fun MotionEvent.isDown(): Boolean {
    return this.actionMasked == MotionEvent.ACTION_DOWN
}

public fun MotionEvent.isFinish(): Boolean {
    return this.actionMasked == MotionEvent.ACTION_UP || this.actionMasked == MotionEvent.ACTION_CANCEL
}

/*是否可以当做点击事件*/
public fun MotionEvent.isClickEvent(context: Context, downX: Float, downY: Float): Boolean {
    val touchSlop = ViewConfiguration.get(context).scaledTouchSlop
    return this.actionMasked == MotionEvent.ACTION_UP &&
            (Math.abs(this.x - downX) <= touchSlop && Math.abs(this.y - downY) <= touchSlop)
}

/**将图片转成字节数组*/
public fun Bitmap.toBytes(format: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG, quality: Int = 100): ByteArray? {
    var out: ByteArrayOutputStream? = null
    var bytes: ByteArray? = null
    try {
        out = ByteArrayOutputStream()
        this.compress(format, quality, out)
        out.flush()

        bytes = out.toByteArray()

        out.close()
    } finally {
        out?.close()
    }
    return bytes
}

/**
 * 将图片转成base64字符串
 * */
public fun Bitmap.toBase64(format: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG, quality: Int = 100): String {
    var result = ""
    toBytes(format, quality)?.let {
        result = Base64.encodeToString(it, Base64.NO_WRAP /*去掉/n符*/)
    }
    return result
}

/**保存图片到指定文件*/
public fun Bitmap.save(
    filePath: String,
    format: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG,
    quality: Int = 100
): Boolean {
    var result = true
    try {
        val outputFile = File(filePath)
        if (!outputFile.exists()) {
            result = outputFile.createNewFile()
        }
        val fileOutputStream = FileOutputStream(outputFile)
        compress(format, quality, fileOutputStream)
        fileOutputStream.flush()
        fileOutputStream.close()
    } catch (e: Exception) {
        e.printStackTrace()
        result = false
    }

    return result
}

public fun ByteArray.toBitmap(): Bitmap {
    return BitmapFactory.decodeByteArray(this, 0, this.size)
}

public fun Bitmap.share(context: Context, shareQQ: Boolean = false, chooser: Boolean = true) {
    RUtils.shareBitmap(context, this, shareQQ, chooser)
}

/**将base64字符串, 转换成图片*/
public fun String.toBitmap(): Bitmap {
    val bytes = Base64.decode(this, Base64.NO_WRAP)
    return bytes.toBitmap()
}

public fun String.share(context: Context, shareQQ: Boolean = false, chooser: Boolean = true) {
    RUtils.shareText(context, null, this, shareQQ, chooser)
}

public fun String.startApp(context: Context) {
    RUtils.startApp(context, this)
}

public fun String.copy() {
    RUtils.copyText(this)
}

public fun CharSequence.copy() {
    RUtils.copyText(this)
}

public fun String.toBitmap(context: Context): Bitmap {
    return RUtils.textToBitmap(context, this)
}

public fun Context.runMain() {
    packageManager.getLaunchIntentForPackage(packageName).apply {
        this?.let {
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            this@runMain.startActivity(it)
        }
    }
}

public fun Context.runActivity(cls: Class<*>) {
    startActivity(Intent(this, cls).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    })
}

/**
 * 获取屏幕方向
 *
 * @see android.content.res.Configuration#ORIENTATION_LANDSCAPE
 * @see android.content.res.Configuration#ORIENTATION_PORTRAIT
 */
public fun Context.getScreenOrientation(): Int {
    return resources.configuration.orientation
}

/**
 * 横屏
 * */
public fun Context.isLandscape(): Boolean {
    return resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
}

/**
 * 竖屏
 * */
public fun Context.isPortrait(): Boolean {
    return resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
}

/**
 * 是否是平板
 * */
public fun Context.isTablet(): Boolean {
    return (resources.configuration.screenLayout and
            Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE
}

public fun Rect.set(rectF: RectF) {
    set(rectF.left.toInt(), rectF.top.toInt(), rectF.right.toInt(), rectF.bottom.toInt())
}

public fun RectF.set(left: Int, top: Int, right: Int, bottom: Int) {
    set(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat())
}

public fun String.toColor(): Int = Color.parseColor(this)

public fun CharSequence?.patternList(regex: String?): MutableList<String> {
    val result = mutableListOf<String>()
    if (this == null) {
        return result
    }
    regex?.let {
        val matcher = regex.toPattern().matcher(this)
        while (matcher.find()) {
            result.add(matcher.group())
        }
    }
    return result
}

public fun CharSequence?.pattern(regex: String?, allowEmpty: Boolean = true): Boolean {

    if (TextUtils.isEmpty(this)) {
        if (allowEmpty) {
            return true
        }
    }

    if (this == null) {
        return false
    }
    if (regex == null) {
        if (allowEmpty) {
            return true
        }
        return !TextUtils.isEmpty(this)
    }
    val matcher = regex.toPattern().matcher(this)
    return matcher.matches()
}

public fun CharSequence?.pattern(regexList: Iterable<String>, allowEmpty: Boolean = true): Boolean {
    if (TextUtils.isEmpty(this)) {
        if (allowEmpty) {
            return true
        }
    }

    if (this == null) {
        return false
    }

    if (!regexList.iterator().hasNext()) {
        if (allowEmpty) {
            return true
        }
        return !TextUtils.isEmpty(this)
    }

    var result = false

// 有BUG的代码, if 条件永远不会为true, 虽然你已经给 result 赋值了true
//    regexList.forEach {
//        result = this.pattern(it, allowEmpty)
//        if (result) {
//            return@forEach
//        }
//    }

    regexList.forEach {
        //闭包外面声明的变量, 虽然已经修改了, 但是修改后的值, 不影响 if 条件判断
        if (this.pattern(it, allowEmpty)) {
            result = true
            return@forEach
        }
    }

    return result
}

/**url中的参数获取*/
public fun String.queryParameter(key: String): String? {
    val uri = Uri.parse(this)
    return uri.getQueryParameter(key)
}

/**获取url或者文件扩展名 对应的mimeType*/
public fun String.mimeType(): String? {
    return MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(this))
}

public fun String?.decode(): String? {
    if (TextUtils.isEmpty(this)) {
        return this
    }

    val decode = try {
        URLDecoder.decode(this, "UTF-8")
    } catch (e: Exception) {
        this
    }

    return decode
}

/**读取body中的字符串*/
public fun ResponseBody?.readString(charsetName: String = "UTF-8"): String {
    if (this == null) {
        return ""
    }
    val source = source()
    source.request(Long.MAX_VALUE)
    val buffer = source.buffer
    val charset: Charset = Charset.forName(charsetName)
    return buffer.clone().readString(charset)
}

/**
 * 返回一个颜色的透明颜色,
 *
 * @param alpha [0..255] 值越小,越透明
 */
public fun Int.alpha(alpha: Int): Int {
    return SkinHelper.getTranColor(this, MathUtils.clamp(alpha, 0, 255))
}

public fun Int.alpha(alpha: Float): Int {
    return alpha(alpha.toInt())
}

/**删除html元素*/
public fun String?.delHTMLTag(): String {
    if (TextUtils.isEmpty(this)) {
        return ""
    }
    return H5.delHTMLTag(this)
}

/**将html格式文本, 转成span*/
public fun String?.fromHtml(): CharSequence {
    if (TextUtils.isEmpty(this)) {
        return ""
    }
    return EncodeUtils.htmlDecode(this)
}

public fun uuid(): String {
    return UUID.randomUUID().toString()
}