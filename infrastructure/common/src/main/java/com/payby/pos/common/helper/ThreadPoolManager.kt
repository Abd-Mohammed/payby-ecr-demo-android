package com.payby.pos.common.helper

import java.util.concurrent.Executors

object ThreadPoolManager {

    private val cachePoolExecutor = Executors.newCachedThreadPool()
    // private val singlePoolExecutor = Executors.newSingleThreadExecutor()

    // fun executeSingleTask(runnable: () -> Unit) {
    //     singlePoolExecutor.execute(runnable)
    // }

    fun executeCacheTask(runnable: () -> Unit) {
        cachePoolExecutor.execute(runnable)
    }

}