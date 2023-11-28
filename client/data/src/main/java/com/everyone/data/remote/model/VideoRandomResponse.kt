package com.everyone.data.remote.model

import com.everyone.data.base.BaseResponse
import com.everyone.data.mapper.Mapper
import com.everyone.data.remote.model.UploaderResponse.Companion.toDomainModel
import com.everyone.data.remote.model.VideoResponse.Companion.toDomainModel
import com.everyone.domain.model.VideoRandom
import kotlinx.android.parcel.Parcelize

@Parcelize
data class VideoRandomResponse(
    val videoResponse: VideoResponse?,
    val uploaderResponse: UploaderResponse?
) : BaseResponse {
    companion object : Mapper<VideoRandomResponse, VideoRandom> {
        override fun VideoRandomResponse.toDomainModel(): VideoRandom {
            return VideoRandom(
                video = videoResponse?.toDomainModel(),
                uploader = uploaderResponse?.toDomainModel()
            )
        }
    }
}
