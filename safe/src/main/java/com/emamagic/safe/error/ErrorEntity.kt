package com.emamagic.safe.error

sealed class ErrorEntity(
    var throwable: Throwable? = null
) {
    class Network(throwable: Throwable? = null) :
        ErrorEntity(throwable = throwable)

    class Api(
        throwable: Throwable? = null
    ) : ErrorEntity(throwable = throwable)

    class Database(throwable: Throwable? = null) :
        ErrorEntity(throwable = throwable)

    class Server(throwable: Throwable? = null) :
        ErrorEntity(throwable = throwable)

    class Unknown(throwable: Throwable? = null) :
        ErrorEntity(throwable = throwable)
}