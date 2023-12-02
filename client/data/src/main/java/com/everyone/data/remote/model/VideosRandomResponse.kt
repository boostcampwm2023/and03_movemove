package com.everyone.data.remote.model

import com.everyone.data.base.BaseResponse
import com.everyone.data.mapper.Mapper
import com.everyone.data.remote.model.VideosResponse.Companion.toDomainModel
import com.everyone.domain.model.VideosRandom
import kotlinx.android.parcel.Parcelize

@Parcelize
data class VideosRandomResponse(
    val videos: List<VideosResponse>?,
    val seed: Int?
) : BaseResponse {
    companion object : Mapper<VideosRandomResponse, VideosRandom> {
        override fun VideosRandomResponse.toDomainModel(): VideosRandom {
            return VideosRandom(
                videos = videos?.map {
                    it.toDomainModel()
                },
                seed = seed
            )
        }
    }
}
