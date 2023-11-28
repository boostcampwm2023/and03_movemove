package com.everyone.data.remote.model

import com.everyone.data.base.BaseResponse
import com.everyone.data.mapper.Mapper
import com.everyone.data.remote.model.VideoRandomResponse.Companion.toDomainModel
import com.everyone.domain.model.VideosRandom
import kotlinx.android.parcel.Parcelize

@Parcelize
data class VideosRandomResponse(
    val data: List<VideoRandomResponse>?
) : BaseResponse {
    companion object : Mapper<VideosRandomResponse, VideosRandom> {
        override fun VideosRandomResponse.toDomainModel(): VideosRandom {
            return VideosRandom(
                data = data?.map {
                    it.toDomainModel()
                }
            )
        }
    }
}
