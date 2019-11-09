package com.angcyo.uiview.less.http.form

import com.angcyo.http.Rx
import com.angcyo.lib.L
import com.angcyo.uiview.less.kotlin.app
import com.angcyo.uiview.less.utils.RNetwork
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArraySet
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicBoolean

/**
 * App 数据获取/更新管理
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/08/24
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
object AppDataModel {
    val _dataModelList = mutableListOf<IBaseDataModel>()
    val _observers = CopyOnWriteArraySet<DataObserver>()
    val _dataModelMap = ConcurrentHashMap<Class<*>, IBaseDataModel>()

    val _isRunning = AtomicBoolean(false)

    /**添加数据模型*/
    fun addDataModel(dataModel: IBaseDataModel) {
        _dataModelMap[dataModel.javaClass] = dataModel
        if (!_dataModelList.contains(dataModel)) {
            _dataModelList.add(dataModel)
        }
    }

    /**移除*/
    fun removeDataModel(dataModel: IBaseDataModel) {
        _dataModelMap.remove(dataModel.javaClass)
        _dataModelList.remove(dataModel)
    }

    /**添加监听回调*/
    fun observer(observer: DataObserver) {
        _observers.add(observer)
    }

    fun unObserver(observer: DataObserver) {
        _observers.remove(observer)
    }

    /**强制更新数据, 不检查是否已进行*/
    fun fetch() {
        if (!RNetwork.isConnect(app())) {
            return
        }

        _isRunning.set(true)
        Rx.back {
            FetchRunnable().run()
            _isRunning.set(false)
        }
    }

    /**注销登录*/
    fun logout() {
        _dataModelList.forEach { dataModel ->
            dataModel.onLogout()
        }
    }

    /**更新数据*/
    fun update(observer: DataObserver) {
        if (_isRunning.get()) {
            Rx.onMain {
                observer.isDataFetching()
            }
        } else {
            if (!_observers.contains(observer)) {
                observer(object :
                    DataObserver() {
                    override fun onDataFetchFinish(
                        dataModelList: List<IBaseDataModel>,
                        errorModelList: List<IBaseDataModel>
                    ) {
                        unObserver(this)
                        unObserver(observer)
                    }
                })
                observer(observer)
            }
            fetch()
        }
    }

    private class FetchRunnable : Runnable {

        var sum = 0
        var progress = 0

        lateinit var countDownLatch: CountDownLatch

        override fun run() {
            val modeList = ArrayList(_dataModelList)
            val errorModeList = mutableListOf<IBaseDataModel>()

            sum = modeList.sumBy {
                it.getDataCount()
            }

            Rx.onMain {
                _observers.onEach {
                    it.onDataFetchStart(modeList, sum)
                }
            }

            countDownLatch = CountDownLatch(modeList.size)

            modeList.forEach { dataModel ->
                dataModel.fetchData({
                    progress += it

                    Rx.onMain {
                        _observers.onEach {
                            it.onDataFetchProgress(dataModel, sum, progress)
                        }
                    }

                }, { success ->

                    Rx.onMain {
                        _observers.onEach {
                            it.onDataFetchEnd(dataModel, success)
                        }
                    }


                    if (success) {

                    } else {
                        errorModeList.add(dataModel)

                    }
                    countDownLatch.countDown()
                })
            }

            countDownLatch.await()

            Rx.onMain {
                _observers.onEach {
                    it.onDataFetchFinish(modeList, errorModeList)
                }
            }
        }
    }
}

/**快速获取注册过的数据模型*/
fun <T : IBaseDataModel> dataModel(cls: Class<T>): T {
    return AppDataModel._dataModelMap[cls] as T
}

/**数据模型基类*/
interface IBaseDataModel {

    /**返回数据量大小, 用来统计数据更新进度*/
    fun getDataCount(): Int {
        return 1
    }

    /**获取数据*/
    fun fetchData(update: (progress: Int) -> Unit, callback: (success: Boolean) -> Unit)

    /**退出登录*/
    fun onLogout() {

    }
}

/**监听数据进度回调*/
abstract class DataObserver {

    open fun isDataFetching() {
        L.i("数据已在拉取中.")
    }

    /**开始拉取数据*/
    open fun onDataFetchStart(dataModelList: List<IBaseDataModel>, sum: Int) {
        L.i("开始获取数据->$sum 个")
    }

    /**拉取结束*/
    open fun onDataFetchEnd(dataModel: IBaseDataModel, success: Boolean) {
        L.i("数据获取结束:$success")
    }

    /**拉取进度*/
    open fun onDataFetchProgress(dataModel: IBaseDataModel, sum: Int, progress: Int) {
        L.i("数据获取:$progress / $sum")
    }

    /**所有模块拉取结束*/
    open fun onDataFetchFinish(
        dataModelList: List<IBaseDataModel>,
        errorModelList: List<IBaseDataModel>
    ) {
        L.e("数据获取完成:${dataModelList.size} : ${errorModelList.size}")
    }
}