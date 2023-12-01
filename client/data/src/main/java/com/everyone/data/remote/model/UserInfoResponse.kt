package com.everyone.data.remote.model

import com.everyone.data.base.BaseResponse
import com.everyone.data.mapper.Mapper
import com.everyone.data.remote.model.JsonWebTokenResponse.Companion.toDomainModel
import com.everyone.data.remote.model.ProfileResponse.Companion.toDomainModel
import com.everyone.domain.model.UserInfo
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserInfoResponse(
    val jsonWebTokenResponse: JsonWebTokenResponse?,
    val profileResponse: ProfileResponse?
) : BaseResponse {
    companion object : Mapper<UserInfoResponse, UserInfo> {
        override fun UserInfoResponse.toDomainModel(): UserInfo {
            return UserInfo(
                jsonWebToken = jsonWebTokenResponse?.toDomainModel(),
                profile = profileResponse?.toDomainModel()
            )
        }
    }
}
