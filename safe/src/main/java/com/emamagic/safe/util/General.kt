package com.emamagic.safe.util

import kotlinx.coroutines.sync.Mutex

internal object General {
    // getting single instance of mutex for different repositories
    val  getMutex = Mutex()


    val cache = LRUCache(10)

    var shouldRetryNetworkCall = true

    const val DISCONNECT = 0
    const val CONNECT = 1
    const val OFFLINE_MODE = 2
}