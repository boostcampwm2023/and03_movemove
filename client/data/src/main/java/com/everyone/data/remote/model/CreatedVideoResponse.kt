package com.everyone.data.remote.model

import com.everyone.data.base.BaseResponse
import com.everyone.data.mapper.Mapper
import com.everyone.domain.model.CreatedVideo
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class CreatedVideoResponse(
    @SerializedName("_id") val id: String?,
    val title: String?,
    val content: String?,
    val category: String?
) : BaseResponse {
    companion object : Mapper<CreatedVideoResponse, CreatedVideo> {
        override fun CreatedVideoResponse.toDomainModel(): CreatedVideo {
            return CreatedVideo(
                id = id,
                title = title,
                content = content,
                category = category
            )
        }
    }
}
