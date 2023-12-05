package com.everyone.data.remote.model

import com.everyone.data.base.BaseResponse
import com.everyone.data.mapper.Mapper
import com.everyone.data.remote.model.UploaderResponse.Companion.toDomainModel
import com.everyone.data.remote.model.VideoResponse.Companion.toDomainModel
import com.everyone.data.remote.model.VideosResponse.Companion.toDomainModel
import com.everyone.domain.model.Videos
import com.everyone.domain.model.VideosRatedItem
import kotlinx.android.parcel.Parcelize

@Parcelize
data class VideosRatedItemResponse(
    val video: VideoResponse?,
    val uploader: UploaderResponse?,
    val ratedAt : String?
) : BaseResponse {
    companion object : Mapper<VideosRatedItemResponse, VideosRatedItem> {
        override fun VideosRatedItemResponse.toDomainModel(): VideosRatedItem {
            return VideosRatedItem(
                video = video?.toDomainModel(),
                uploader = uploader?.toDomainModel(),
                ratedAt = ratedAt
            )
        }
    }
}
