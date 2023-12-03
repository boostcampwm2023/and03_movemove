package com.everyone.data.remote.model

import com.everyone.data.base.BaseResponse
import com.everyone.data.mapper.Mapper
import com.everyone.domain.model.ProfileImageUploadUrl
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProfileImageUploadUrlResponse(
    val presignedUrl: String?
) : BaseResponse {
    companion object : Mapper<ProfileImageUploadUrlResponse, ProfileImageUploadUrl> {
        override fun ProfileImageUploadUrlResponse.toDomainModel(): ProfileImageUploadUrl {
            return ProfileImageUploadUrl(presignedUrl)
        }
    }
}
