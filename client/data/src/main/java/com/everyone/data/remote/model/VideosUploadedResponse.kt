package com.everyone.data.remote.model

import com.everyone.data.base.BaseResponse
import com.everyone.data.mapper.Mapper
import com.everyone.data.remote.model.UploaderResponse.Companion.toDomainModel
import com.everyone.data.remote.model.VideoResponse.Companion.toDomainModel
import com.everyone.domain.model.VideosUploaded
import kotlinx.android.parcel.Parcelize

@Parcelize
data class VideosUploadedResponse(
    val uploader: UploaderResponse?,
    val video: List<VideoResponse>?,
) : BaseResponse {
    companion object : Mapper<VideosUploadedResponse, VideosUploaded> {
        override fun VideosUploadedResponse.toDomainModel(): VideosUploaded {
            return VideosUploaded(
                uploader = uploader?.toDomainModel(),
                video = video?.map { it.toDomainModel() }
            )
        }
    }
}
