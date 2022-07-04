package com.emamagic.safe.util

interface IResponse<T> {

    fun isSuccessful(): Boolean

    fun body(): T?

    fun code(): Int

    fun errorBody(): String?

}