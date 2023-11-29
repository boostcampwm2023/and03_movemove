package com.everyone.data.remote.model

import com.everyone.data.base.BaseResponse
import com.everyone.data.mapper.Mapper
import com.everyone.data.remote.model.VideosResponse.Companion.toDomainModel
import com.everyone.domain.model.VideosTrend
import kotlinx.android.parcel.Parcelize

@Parcelize
data class VideosTrendResponse(
    val videos: List<VideosResponse>?
) : BaseResponse {
    companion object : Mapper<VideosTrendResponse, VideosTrend> {
        override fun VideosTrendResponse.toDomainModel(): VideosTrend {
            return VideosTrend(
                videos = videos?.map {
                    it.toDomainModel()
                }
            )
        }
    }
}
