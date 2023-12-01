package com.everyone.data.remote.model

import com.everyone.data.base.BaseResponse
import com.everyone.data.mapper.Mapper
import com.everyone.domain.model.VideoUploadUrl
import kotlinx.parcelize.Parcelize

@Parcelize
data class VideoUploadUrlResponse(
    val videoId: String?,
    val videoUrl: String?,
    val thumbnailUrl: String?
) : BaseResponse {
    companion object : Mapper<VideoUploadUrlResponse, VideoUploadUrl> {
        override fun VideoUploadUrlResponse.toDomainModel(): VideoUploadUrl {
            return VideoUploadUrl(
                videoId = videoId,
                videoUrl = videoUrl,
                thumbnailUrl = thumbnailUrl
            )
        }
    }
}
