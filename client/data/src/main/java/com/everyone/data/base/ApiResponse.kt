package com.everyone.data.base

data class ApiResponse<T>(
    val statusCode: Int?,
    val message: String?,
    val data: T?
)
