package com.angcyo.uiview.less.kotlin

import com.angcyo.uiview.less.utils.RUtils
import java.text.SimpleDateFormat

/**
 * Created by angcyo on ：2018/04/12 13:36
 * 修改备注：
 * Version: 1.0.0
 */

/**返回毫秒对应的天数*/
fun Long.toDay(): Int {
    return (this / (24 * 60 * 60 * 1000)).toInt()
}

/**当前时间和现在时间对比, 还剩多少天*/
fun Long.toNowDay(): Int {
    return (this - nowTime()).toDay()
}

/**返回毫秒对应的天数*/
fun Int.toDay(): Int {
    return this.toLong().toDay()
}

/**
 * 将一段时间按照 00:00的格式输出, 如果有小时: 01:00:00
 */
fun Long.toHHmmss(showMill: Boolean = false /*显示毫秒*/): String {
    val formatTime = RUtils.formatTime(this)
    return if (showMill) {
        "$formatTime.${this % 1000L}"
    } else {
        formatTime
    }
}

fun Long.toFullDate(): String {
    return this.fullTime()
}

/**时间全格式输出*/
fun Long.fullTime(): String {
    return RUtils.yyyyMMdd("yyyy-MM-dd HH:mm:ss.SSS", this)
}

/**格式化时间输出*/
fun Long.toTime(pattern: String = "yyyy-MM-dd HH:mm"): String {
    return RUtils.yyyyMMdd(pattern, this)
}

/**将字符串换算成毫秒*/
fun String.toMillis(pattern: String = "yyyyMMdd"): Long {
    val format: SimpleDateFormat = SimpleDateFormat.getDateInstance() as SimpleDateFormat
    format.applyPattern(pattern)
    var time = 0L
    try {
        time = format.parse(this).time
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return time
}

public fun <T> T.nowTimeString(pattern: String = "yyyy-MM-dd HH:mm:ss"): String {
    return nowTime().toTime(pattern)
}

public inline fun <T> T.nowTime() = System.currentTimeMillis()

/**获取当前时间对应的 y m d h s*/
public inline fun Long.spiltTime() = RUtils.splitTime(this)

public inline fun String.parseTime(pattern: String = "yyyy-MM-dd") = RUtils.parseTime(this, pattern)

/**将毫秒, 拆成 d h m s sss数组*/
public fun Long.toTimes(): LongArray {

    //剩余多少毫秒
    val ms = this % 1000

    //多少秒
    val mill = this / 1000

    //多少分
    val min = mill / 60

    //多少小时
    val hour = min / 60

    //多少天
    val day = hour / 24

    val h = hour % 24
    val m = min % 60
    val s = mill % 60

    return longArrayOf(ms, s, m, h, day)
}

/**
 * 将毫秒转成 x天x时x分x秒x毫秒
 * @param pattern 默认为<=0时, 不返回. 如果需要强制返回, 设置1, 强制不返回设置-1
 * @param h24 24小时制
 * */
fun Long.toElapsedTime(
    pattern: IntArray = intArrayOf(-1),
    h24: Boolean = true,
    units: Array<String> = arrayOf("毫秒", "秒", "分", "时", "天")
): String {
    val times = toTimes()
    val builder = StringBuilder()

    fun toH24(value: Long): String {
        return if (!h24 || value > 10) "$value" else "0${value}"
    }

    for (i in 4 downTo 0) {
        val value = times[i]
        val unit = units[i]
        val need = pattern.getOrNull(i) ?: 0
        when (need) {
            1 -> {
                //强制要
                builder.append(toH24(value))
                builder.append(unit)
            }
            -1 -> {
                //强制不要
            }
            else -> {
                //智能
                if (value > 0) {
                    builder.append(toH24(value))
                    builder.append(unit)
                }
            }
        }
    }

    return builder.toString()
}