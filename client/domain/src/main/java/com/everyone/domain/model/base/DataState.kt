package com.everyone.domain.model.base

sealed class DataState<out T> {
    data class Success<T>(val data: T) : DataState<T>()
    data class Failure(val networkError: NetworkError) : DataState<Nothing>()
}
