package com.emamagic.safe.error

import java.io.IOException

data class HttpException(
    val code: Int,
    val messages: String?,
    val errorBody: String?
): IOException(messages)