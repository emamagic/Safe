package com.emamagic.safe.policy

import com.emamagic.safe.util.SafeWrapper
import java.util.*

data class MemoryPolicy<ResultType>(
    val shouldRefresh: (current: SafeWrapper<ResultType>?) -> Boolean = { false },
    val expires: Long = -1
)  {
    private val createAt: Date = Date()

    fun isExpired(): Boolean {
        val result = if (expires != -1L) {
            (Date().time - createAt.time) > expires
        } else false
        return result
    }
}
