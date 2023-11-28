package com.everyone.data.remote.model

sealed class ApiResponse<T> {
    data class Success<T>(
        val statusCode: Int?,
        val message: String?,
        val data: T?
    ) : ApiResponse<T>()

    data class Failure<T>(
        val statusCode: Int?,
        val message: String?
    ) : ApiResponse<T>()
}