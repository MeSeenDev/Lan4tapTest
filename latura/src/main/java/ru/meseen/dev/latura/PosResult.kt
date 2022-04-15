package ru.meseen.dev.latura

sealed class PosResult<Type> {
    data class Error(val throwable: Throwable) : PosResult<Any>()
    data class Success<Type>(val data: Type) : PosResult<Type>()
}
