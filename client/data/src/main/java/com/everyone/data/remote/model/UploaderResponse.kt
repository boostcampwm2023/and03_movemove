package com.everyone.data.remote.model

import com.everyone.data.base.BaseResponse
import com.everyone.data.mapper.Mapper
import com.everyone.domain.model.Uploader
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UploaderResponse(
    val uuid: String?,
    val nickname: String?,
    val statusMessage: String?,
    val profileImageUrl: String?
) : BaseResponse {
    companion object : Mapper<UploaderResponse, Uploader> {
        override fun UploaderResponse.toDomainModel(): Uploader {
            return Uploader(
                uuid = uuid,
                nickname = nickname,
                statusMessage = statusMessage,
                profileImageUrl = profileImageUrl
            )
        }
    }
}
