package com.angcyo.uiview.less.recycler.adapter

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/05/09
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */

open class DslDateFilter(val adapter: DslAdapter) {

    /**
     * 原始数据, 未过滤时的数量
     * */
    val originDataSize: Int
        get() = adapter.allDatas.size

    /**
     * 过滤后的数据源
     * */
    val filterDataList: MutableList<DslAdapterItem>
        get() {
            val list = mutableListOf<DslAdapterItem>()

            var index = 0

            while (index < originDataSize) {
                val item = adapter.allDatas[index]

                //第一条数据, 要么是分组头, 要么是 不受分组管理的子项
                list.add(item)

                if (item.itemIsGroupHead) {
                    val childSize = groupChildSize(index)
                    index += 1

                    if (childSize > 0) {
                        if (item.itemGroupExtend) {
                            //展开
                            for (i in index..(index - 1 + childSize)) {
                                list.add(adapter.allDatas[i])
                            }
                        } else {
                            //折叠
                        }

                        //跳过...child
                        index += childSize
                    }
                } else {
                    index += 1
                }
            }

            return list
        }

    /*当前位置, 距离下一个分组头, 还有多少个数据 (startIndex, endIndex)*/
    private fun groupChildSize(startIndex: Int): Int {
        var result = 0

        for (i in (startIndex + 1) until originDataSize) {
            val item = adapter.allDatas[i]

            if (item.itemIsGroupHead) {
                result = i - startIndex - 1
                break
            } else if (i == originDataSize - 1) {
                result = i - startIndex
                break
            }
        }

        return result
    }

    /**
     * 过滤item
     * @param fold 折叠 or 展开
     * */
    fun filterItem(item: DslAdapterItem, fold: Boolean) {
        val startIndex = adapter.allDatas.indexOf(item)
        item.itemGroupExtend = !fold
        if (startIndex != -1) {
            val childSize = groupChildSize(startIndex)

            if (childSize > 0) {
                if (fold) {
                    //折叠
                    adapter.notifyItemRangeRemoved(startIndex + 1, childSize)
                } else {
                    //展开
                    adapter.notifyItemRangeInserted(startIndex + 1, childSize)
                }
                adapter.notifyItemRangeChanged(startIndex + 1, originDataSize)
            }
        }
    }
}