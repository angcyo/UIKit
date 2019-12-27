package com.angcyo.uiview.less.kotlin

import android.text.TextUtils
import com.angcyo.http.Json
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/07/25
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */

public fun json(): Json.Builder = Json.json()
public fun array(): Json.Builder = Json.array()

public fun jsonString(config: Json.Builder.() -> Unit = {}): String {
    return json().run {
        config()
        get()
    }
}

public fun arrayString(config: Json.Builder.() -> Unit = {}): String {
    return array().run {
        config()
        get()
    }
}

public fun jsonObject(config: Json.Builder.() -> Unit = {}): JsonElement {
    return json().run {
        config()
        build()
    }
}

public fun jsonArray(config: Json.Builder.() -> Unit = {}): JsonElement {
    return array().run {
        config()
        build()
    }
}


//<editor-fold desc="JsonObject 扩展">

public fun JsonObject.getInt(key: String, default: Int = -1): Int {
    val element = get(key)
    if (element is JsonPrimitive) {
        if (element.isNumber) {
            return element.asInt
        }
    }
    return default
}

public fun JsonObject.getString(key: String, default: String? = null): String? {
    val element = get(key)
    if (element is JsonPrimitive) {
        if (element.isString) {
            return element.asString
        }
    }
    return default
}

public fun JsonObject.getJson(key: String, default: JsonObject? = null): JsonObject? {
    val element = get(key)
    if (element is JsonObject) {
        return element
    }
    return default
}

public fun JsonObject.getArray(key: String, default: JsonArray? = null): JsonArray? {
    val element = get(key)
    if (element is JsonArray) {
        return element
    }
    return default
}

public fun JsonObject.getLong(key: String, default: Long = -1): Long {
    val element = get(key)
    if (element is JsonPrimitive) {
        if (element.isNumber) {
            return element.asLong
        }
    }
    return default
}

public fun JsonObject.getDouble(key: String, default: Double = -1.0): Double {
    val element = get(key)
    if (element is JsonPrimitive) {
        if (element.isNumber) {
            return element.asDouble
        }
    }
    return default
}

//</editor-fold desc="JsonObject">

//<editor-fold desc="JsonElement 扩展">

public fun JsonElement.getInt(key: String): Int {
    if (this is JsonObject) {
        return this.getInt(key)
    }
    throw IllegalAccessException("不允许使用[JsonArray]操作.")
}

public fun JsonElement.getString(key: String): String? {
    if (this is JsonObject) {
        return this.getString(key)
    }
    throw IllegalAccessException("不允许使用[JsonArray]操作.")
}

public fun JsonElement.getJson(key: String): JsonObject? {
    if (this is JsonObject) {
        return this.getJson(key)
    }
    throw IllegalAccessException("不允许使用[JsonArray]操作.")
}

public fun JsonElement.getArray(key: String): JsonArray? {
    if (this is JsonObject) {
        return this.getArray(key)
    }
    throw IllegalAccessException("不允许使用[JsonArray]操作.")
}

public fun JsonElement.getLong(key: String): Long {
    if (this is JsonObject) {
        return this.getLong(key)
    }
    throw IllegalAccessException("不允许使用[JsonArray]操作.")
}

public fun JsonElement.getDouble(key: String): Double {
    if (this is JsonObject) {
        return this.getDouble(key)
    }
    throw IllegalAccessException("不允许使用[JsonArray]操作.")
}

public fun JsonElement.array(index: Int): JsonElement {
    if (this is JsonArray) {
        return get(index)
    }
    throw IllegalAccessException("不允许使用[JsonObject]操作.")
}

//</editor-fold desc="JsonElement 扩展">


public fun String?.trim(char: Char): String? {
    return this?.trimStart(char)?.trimEnd(char)
}

public fun String?.json(): JsonObject? {
    if (TextUtils.isEmpty(this)) {
        return null
    }
    var fromJson: JsonObject? = null
    try {
        fromJson = this?.fromJson(JsonObject::class.java)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return fromJson
}

public fun String?.jsonArray(): JsonArray? {
    if (TextUtils.isEmpty(this)) {
        return null
    }
    var fromJson: JsonArray? = null
    try {
        fromJson = this?.fromJson(JsonArray::class.java)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return fromJson
}