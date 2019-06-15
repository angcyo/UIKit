package com.angcyo.uiview.less.utils

import com.angcyo.uiview.less.utils.utilcode.utils.ConstUtils

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/06/15
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */

/**
 * http://tool.chinaz.com/regex/
 * */

/**手机号码简单*/
public const val PATTERN_MOBILE_SIMPLE = ConstUtils.REGEX_MOBILE_SIMPLE
/**手机号码精准*/
public const val PATTERN_MOBILE_EXACT = ConstUtils.REGEX_MOBILE_EXACT

/**座机电话, 加区号*/
public const val PATTERN_TEL = ConstUtils.REGEX_TEL

/**邮箱*/
public const val PATTERN_EMAIL = ConstUtils.REGEX_EMAIL

/**网址, 必须带协议 http等*/
public const val PATTERN_URL = ConstUtils.REGEX_URL

public fun patternOnlyMobile() = mutableSetOf(PATTERN_MOBILE_EXACT)
public fun patternTelAndMobile() = mutableSetOf(PATTERN_MOBILE_EXACT, PATTERN_TEL)
public fun patternEmail() = mutableSetOf(PATTERN_EMAIL)
public fun patternUrl() = mutableSetOf(PATTERN_URL)

