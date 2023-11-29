package com.everyone.data.remote.model

import com.everyone.data.base.BaseResponse
import com.everyone.data.mapper.Mapper
import com.everyone.domain.model.JsonWebToken
import kotlinx.android.parcel.Parcelize

@Parcelize
data class JsonWebTokenResponse(
    val accessToken: String?,
    val refreshToken: String?
) : BaseResponse {
    companion object : Mapper<JsonWebTokenResponse, JsonWebToken> {
        override fun JsonWebTokenResponse.toDomainModel(): JsonWebToken {
            return JsonWebToken(
                accessToken = accessToken,
                refreshToken = refreshToken
            )
        }
    }
}
