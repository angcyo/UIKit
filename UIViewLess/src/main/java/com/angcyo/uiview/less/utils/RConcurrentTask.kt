package com.angcyo.uiview.less.utils

import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.SynchronousQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.ReentrantLock

/**
 * 多任务并发执行, 结束后回调
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/08/02
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
class RConcurrentTask(
    val taskQueue: ConcurrentLinkedQueue<Runnable>,
    val concurrentCount: Int = 5 /*并发数量*/,
    val onFinish: () -> Unit
) {
    /**并发线程池*/
    private val executor: ThreadPoolExecutor
    private val runTaskQueue: ConcurrentLinkedQueue<Runnable>
    private val reentrantLock: ReentrantLock
    private val condition: Condition

    init {
        val taskSize = taskQueue.size
        val threadSize = taskSize + 1
        executor = ThreadPoolExecutor(
            threadSize, Int.MAX_VALUE,
            60L, TimeUnit.SECONDS,
            SynchronousQueue()
        )

        runTaskQueue = ConcurrentLinkedQueue()
        reentrantLock = ReentrantLock()
        condition = reentrantLock.newCondition()

        executor.execute {
            while (taskQueue.isNotEmpty()) {
                if (runTaskQueue.size < concurrentCount) {
                    val task = taskQueue.poll()
                    runTaskQueue.add(task)
                    executor.execute {
                        try {
                            task.run()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        try {
                            reentrantLock.lock()
                            condition.signalAll()

                            runTaskQueue.remove(task)

                            if (taskQueue.isEmpty() && runTaskQueue.isEmpty()) {
                                release()
                                onFinish()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        } finally {
                            reentrantLock.unlock()
                        }
                    }
                } else {
                    try {
                        reentrantLock.lock()
                        condition.await()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    } finally {
                        reentrantLock.unlock()
                    }
                }
            }
        }
    }

    private fun release() {
        taskQueue.clear()
        runTaskQueue.clear()
        if (!executor.isShutdown) {
            executor.shutdownNow()
        }
        //L.d("释放资源.")
    }

}