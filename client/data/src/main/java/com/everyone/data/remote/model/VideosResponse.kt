package com.everyone.data.remote.model

import com.everyone.data.base.BaseResponse
import com.everyone.data.mapper.Mapper
import com.everyone.data.remote.model.UploaderResponse.Companion.toDomainModel
import com.everyone.data.remote.model.VideoResponse.Companion.toDomainModel
import com.everyone.domain.model.Videos
import kotlinx.android.parcel.Parcelize

@Parcelize
data class VideosResponse(
    val video: VideoResponse?,
    val uploader: UploaderResponse?
) : BaseResponse {
    companion object : Mapper<VideosResponse, Videos> {
        override fun VideosResponse.toDomainModel(): Videos {
            return Videos(
                video = video?.toDomainModel(),
                uploader = uploader?.toDomainModel()
            )
        }
    }
}
