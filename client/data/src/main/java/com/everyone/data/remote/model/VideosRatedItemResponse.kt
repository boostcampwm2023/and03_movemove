package com.everyone.data.remote.model

import com.everyone.data.base.BaseResponse
import com.everyone.data.mapper.Mapper
import com.everyone.data.remote.model.UploaderResponse.Companion.toDomainModel
import com.everyone.data.remote.model.VideoResponse.Companion.toDomainModel
import com.everyone.domain.model.Videos
import kotlinx.android.parcel.Parcelize

@Parcelize
data class VideosRatedItemResponse(
    val video: VideoResponse?,
    val uploader: UploaderResponse?,
    val ratedAt : String?
) : BaseResponse {
    companion object : Mapper<VideosRatedItemResponse, Videos> {
        override fun VideosRatedItemResponse.toDomainModel(): Videos {
            return Videos(
                video = video?.toDomainModel(),
                uploader = uploader?.toDomainModel()
            )
        }
    }
}
