package com.everyone.data.remote.model


import com.everyone.data.base.BaseResponse
import com.everyone.data.mapper.Mapper
import com.everyone.domain.model.Profile
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProfileResponse(
    val uuid: String?,
    val nickname: String?,
    val statusMessage: String?
) : BaseResponse {
    companion object : Mapper<ProfileResponse, Profile> {
        override fun ProfileResponse.toDomainModel(): Profile {
            return Profile(
                uuid = uuid,
                nickname = nickname,
                statusMessage = statusMessage
            )
        }
    }
}
