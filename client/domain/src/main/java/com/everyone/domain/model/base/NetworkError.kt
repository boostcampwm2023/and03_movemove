package com.everyone.domain.model.base

data class NetworkError(
    val statusCode: Int? = -1,
    val message: String? = ""
)
