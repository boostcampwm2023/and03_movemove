package com.everyone.data.remote.model

import com.everyone.data.base.BaseResponse
import com.everyone.data.mapper.Mapper
import com.everyone.domain.model.Video
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.Date

@Parcelize
data class VideoResponse(
    @SerializedName("_id") val id: String?,
    val viewCount: Int?,
    val rating: String?,
    val userRating: Int?,
    val category: String?,
    val title: String?,
    val content: String?,
    val uploadedAt: Date?,
    val manifest: String?,
    val thumbnailImageUrl: String?
) : BaseResponse {
    companion object : Mapper<VideoResponse, Video> {
        override fun VideoResponse.toDomainModel(): Video {
            return Video(
                id = id,
                viewCount = viewCount,
                rating = rating,
                userRating = userRating,
                category = category,
                title = title,
                content = content,
                uploadedAt = uploadedAt,
                manifest = manifest,
                thumbnailImageUrl = thumbnailImageUrl
            )
        }
    }
}
