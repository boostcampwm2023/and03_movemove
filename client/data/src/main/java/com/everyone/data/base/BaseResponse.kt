package com.everyone.data.base

sealed class BaseResponse<T> {
    data class Success<T>(
        val statusCode: Int?,
        val message: String?,
        val data: T
    ) : BaseResponse<T>()

    data class Failure<T>(
        val statusCode: Int?,
        val message: String?
    ) : BaseResponse<T>()
}