package com.angcyo.uiview.less.http.form

import com.angcyo.uiview.less.kotlin.uuid

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/05/21
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
data class FormData(
    /**表单需要上传的地址*/
    var formUrl: String? = null,

    /**需要[post]提交的[jsonBody]*/
    var formJson: String = "{}",

    /**附件文件, 上传完之后, 会被以相同的 key, 附加到 formJson 中*/
    var formAttachJson: String = "{}",



    /**表单任务类型, 用于任务列表展示*/
    var formType: String? = null,
    /**表单任务描述, 用于任务列表展示*/
    var formDes: String? = null,


    /**标志, 自定义标志位*/
    var formFlag: Int = -1,
    /**唯一标识符, 也可以自己指定*/
    var formUUID: String = uuid(),


    /**扩展字段, 任意支配*/
    var formEx: String = ""
)