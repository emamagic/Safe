package com.emamagic.safe.util

import com.emamagic.safe.error.ErrorEntity

sealed class SafeWrapper<T>(
    val data: T? = null,
    val error: ErrorEntity? = null,
    val code: Int? = null,
) {
    class Success<T>(data: T?, code: Int? = null) : SafeWrapper<T>(data = data, code = code)
    class Failed<T>(error: ErrorEntity, data: T? = null) : SafeWrapper<T>(data = data ,error = error)
    class LoadingFetch<T>(data: T? = null) : SafeWrapper<T>(data = data)

    override fun toString(): String {
        return when(this) {
            is Success -> "Success [data=$data]"
            is Failed -> "Error[exception=${error?.throwable?.message}]"
            is LoadingFetch -> "LoadingFetch"
        }
    }
}
val SafeWrapper<*>.succeeded
    get() = this is SafeWrapper.Success && data != null