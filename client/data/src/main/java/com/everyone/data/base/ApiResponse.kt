package com.everyone.data.base

import com.everyone.domain.model.base.DataState
import com.everyone.domain.model.base.NetworkError

data class ApiResponse<T>(
    val statusCode: Int?,
    val message: String?,
    val data: T?
) {
    fun toFailure(): DataState.Failure = DataState.Failure(
        NetworkError(
            statusCode = this.statusCode,
            message = this.message
        )
    )
}
