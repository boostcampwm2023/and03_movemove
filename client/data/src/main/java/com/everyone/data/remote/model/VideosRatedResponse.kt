package com.everyone.data.remote.model

import com.everyone.data.base.BaseResponse
import com.everyone.data.mapper.Mapper
import com.everyone.data.remote.model.RaterResponse.Companion.toDomainModel
import com.everyone.data.remote.model.UploaderResponse.Companion.toDomainModel
import com.everyone.data.remote.model.VideoResponse.Companion.toDomainModel
import com.everyone.domain.model.VideosRated
import com.everyone.domain.model.VideosRatedItem
import kotlinx.parcelize.Parcelize

@Parcelize
data class VideosRatedResponse(
    val rater: RaterResponse?,
    val videos: List<VideosRatedItemResponse>?,
) : BaseResponse {
    companion object : Mapper<VideosRatedResponse, VideosRated> {
        override fun VideosRatedResponse.toDomainModel(): VideosRated {
            return VideosRated(
                rater = rater?.toDomainModel(),
                videos = videos?.map {
                    VideosRatedItem(
                        video = it.video?.toDomainModel(),
                        uploader = it.uploader?.toDomainModel(),
                        ratedAt = it.ratedAt
                    )
                }
            )
        }
    }
}
