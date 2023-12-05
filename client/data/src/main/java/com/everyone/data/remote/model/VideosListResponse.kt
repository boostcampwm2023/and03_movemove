package com.everyone.data.remote.model

import com.everyone.data.base.BaseResponse
import com.everyone.data.mapper.Mapper
import com.everyone.data.remote.model.VideosResponse.Companion.toDomainModel
import com.everyone.domain.model.VideosList
import kotlinx.android.parcel.Parcelize

@Parcelize
data class VideosListResponse(
    val videos: List<VideosResponse>?
) : BaseResponse {
    companion object : Mapper<VideosListResponse, VideosList> {
        override fun VideosListResponse.toDomainModel(): VideosList {
            return VideosList(
                videos = videos?.map {
                    it.toDomainModel()
                }
            )
        }
    }
}
