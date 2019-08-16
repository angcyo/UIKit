package com.angcyo.uiview.less.kotlin

import android.text.TextUtils
import com.angcyo.uiview.less.utils.RUtils
import com.angcyo.uiview.less.utils.utilcode.utils.FileUtils
import java.io.File

/**
 * Copyright (C) 2016,深圳市红鸟网络科技股份有限公司 All rights reserved.
 * 项目名称：
 * 类的描述：
 * 创建人员：Robi
 * 创建时间：2018/06/21 09:58
 * 修改人员：Robi
 * 修改时间：2018/06/21 09:58
 * 修改备注：
 * Version: 1.0.0
 */

/**创建新文件*/
public fun String.createFile() {
    val file = File(this)
    if (file.exists()) {
        file.delete()
    }
    try {
        file.createNewFile()
    } catch (e: Exception) {
    }
}

/**删除文件*/
public fun String.deleteFile() {
    val file = File(this)
    if (file.exists()) {
        file.delete()
    }
}

/**将字符串写入文件*/
public fun String.saveToFile(data: String, append: Boolean = true) {
    writeData(data, append)
}

public fun String.writeData(data: String, append: Boolean = true) {
    FileUtils.writeFileFromString(this, data, append)
}

/**将文件内容, 转移到另一个文件*/
public fun String.transferToFile(filePath: String) {
    com.yalantis.ucrop.util.FileUtils.copyFile(this, filePath)
}

/**获取文件md5值*/
public fun File.md5(): String {
    return FileUtils.getFileMD5ToString(this)
}

public fun String.file(): File {
    return File(this)
}

public fun String?.fileSize(): Long {
    if (TextUtils.isEmpty(this)) {
        return 0L
    }
    val file = this!!.file()
    return if (file.exists()) {
        file.length()
    } else {
        0L
    }
}

public fun Long.fileSizeString(): String {
    return RUtils.formatFileSize(this)
}

public fun String?.fileSizeString(): String {
    return fileSize().fileSizeString()
}